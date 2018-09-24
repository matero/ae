/*
 * The MIT License
 *
 * Copyright (c) 2018 ActiveEngine.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ae.routes.processor;

import ae.web.ControllerWithThymeleafSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.lang.model.util.ElementFilter.methodsIn;
import ae.controller;

class RoutesReader {
        private static final String API_CONSTRUCTOR_ARGS = "request, response";
        private static final String THYMELEAF_CONSTRUCTOR_ARGS
                = "request, response, webContext(request, response), templateEngine()";
        private static final String CONTROLLER_MUST_SUPPORT_THYMELEAF
                = "Controller must extend "
                + ControllerWithThymeleafSupport.class
                + " to be able to use @GET(template=true).";

        private final Elements elements;
        private final Types types;
        private final Messager messager;

        private final TypeMirror controllerWithThymeleafSupportClass;

        final String routerBasePath;
        final String routerApiPath;

        private final RoutesDeclarations.Builder routes;

        private String controllerBasePath;
        private String controllerApiPath;

        RoutesReader(final String routerBasePath,
                     final String routerApiPath,
                     final Elements elements,
                     final Types types,
                     final Messager messager,
                     final RoutesDeclarations.Builder routes)
        {
                this.routerBasePath = routerBasePath;
                this.routerApiPath = makePath(this.routerBasePath, routerApiPath);
                this.elements = elements;
                this.types = types;
                this.messager = messager;
                this.controllerWithThymeleafSupportClass = getControllerWithThymeleafSupportClass();
                this.routes = routes;
        }

        private TypeMirror getControllerWithThymeleafSupportClass()
        {
                return elements.getTypeElement(ControllerWithThymeleafSupport.class.getCanonicalName()).asType();
        }

        /**
         * @param routesDeclarationsBuilder
         * @return null if no processing done (reports errors on such case), the route add
         */
        boolean readRoutes(final TypeElement controllerClass)
        {
                boolean success = true;
                final controller ctrler = controllerClass.getAnnotation(controller.class);

                if (ctrler.path() == null) {
                        printError("@controller.path can't be null", controllerClass);
                        success = false;
                        controllerBasePath = "";
                        controllerApiPath = "";
                } else {
                        controllerBasePath = makePath(routerBasePath, ctrler.path());
                        controllerApiPath = makePath(routerApiPath, ctrler.path());
                }
                for (final ExecutableElement method : methodsIn(controllerClass.getEnclosedElements())) {
                        for (final HttpVerb httpVerb : HttpVerb.values()) {
                                success &= httpVerb.buildRoute(controllerClass, method, this);
                        }
                }
                return success;
        }

        boolean buildRoute(final HttpVerb httpVerb,
                           final String path,
                           final boolean template,
                           final boolean useCredentials,
                           final ExecutableElement method,
                           final TypeElement controllerClass
        )
        {
                if (path == null) {
                        printError("@" + httpVerb.name() + ".path can't be null", method);
                        return false;
                }

                final String actionPath;
                final String ctorArgs;

                if (template) {
                        if (supportsThymeleaf(controllerClass)) {
                                actionPath = makePath(controllerBasePath, path);
                                ctorArgs = THYMELEAF_CONSTRUCTOR_ARGS;
                        } else {
                                printError(CONTROLLER_MUST_SUPPORT_THYMELEAF, method);
                                return false;
                        }
                } else {
                        actionPath = makePath(controllerApiPath, path);
                        ctorArgs = API_CONSTRUCTOR_ARGS;
                }

                routes.addRoute(makeRoute(httpVerb, actionPath, controllerClass, ctorArgs, useCredentials, method));
                return true;
        }

        private RouteDescriptor makeRoute(final HttpVerb verb,
                                          final String uri,
                                          final TypeElement controller,
                                          final String ctorArguments,
                                          boolean useCredentials,
                                          final ExecutableElement action)
        {
                final PathSpec path = PathSpec.from(uri);
                final ImmutableList<Parameter> parameters = parametersAt(action);
                final int paramsCount = parameters.size();

                if (path.isStatic()) {
                        if (paramsCount > 0) {
                                printError(actionRequireParameters(paramsCount), action);
                                return null;
                        }
                        return new RouteDescriptor(verb,
                                                   path.pattern,
                                                   useCredentials,
                                                   controller,
                                                   action.getSimpleName().toString(),
                                                   ctorArguments,
                                                   path.headers);
                } else {
                        final int pathParams = path.parameterNames.size();
                        if (pathParams != paramsCount) {
                                printError(actionParametersUmatched(paramsCount, pathParams), action);
                                return null;
                        }
                        return new RouteDescriptor(verb,
                                                   path.pattern,
                                                   path.regex,
                                                   useCredentials,
                                                   controller,
                                                   action.getSimpleName().toString(),
                                                   parameters,
                                                   ctorArguments, path.headers);
                }
        }

        private static String actionParametersUmatched(final int expectedParams, final int pathParams)
        {
                return "Route parameters count (" + pathParams + ") differs from action parameters count (" + expectedParams + '.';
        }

        private static String actionRequireParameters(final int paramsCount)
        {
                return "Route doesn't have parameters, but action method requires " + paramsCount + " parameters.";
        }

        private ImmutableList<Parameter> parametersAt(final ExecutableElement actionMethod)
        {
                final ImmutableList.Builder<Parameter> parameters = ImmutableList.builder();
                for (final VariableElement parameter : actionMethod.getParameters()) {
                        final TypeMirror type = parameter.asType();
                        final String name = parameter.getSimpleName().toString();
                        parameters.add(new Parameter(name, type, interpreterOf(type)));
                }
                return parameters.build();
        }

        private String interpreterOf(final TypeMirror type)
        {
                switch (type.getKind()) {
                        case BOOLEAN:
                                return "asPrimitiveBoolean";
                        case BYTE:
                                return "asPrimitiveByte";
                        case SHORT:
                                return "asPrimitiveShort";
                        case INT:
                                return "asPrimitiveInt";
                        case LONG:
                                return "asPrimitiveLong";
                        case CHAR:
                                return "asPrimitiveChar";
                        case FLOAT:
                                return "asPrimitiveFloat";
                        case DOUBLE:
                                return "asPrimitiveDouble";
                        case DECLARED: {
                                final String name = types.asElement(type).getSimpleName().toString();
                                return "as" + name;
                        }
                        default:
                                throw new IllegalArgumentException("type not supported as parameter");
                }
        }

        private boolean supportsThymeleaf(final TypeElement controller)
        {
                return types.isSubtype(controller.asType(), controllerWithThymeleafSupportClass);
        }

        private void printError(final String message, final Element e)
        {
                messager.printMessage(Diagnostic.Kind.ERROR, message, e);
        }

        private String makePath(final String parentPath, final String childPath)
        {
                if (childPath.startsWith("/")) {
                        return childPath;
                } else if (parentPath.endsWith("/")) {
                        return parentPath + childPath;
                } else {
                        return parentPath + '/' + childPath;
                }
        }
}

final class PathSpec {

        private static final String SEPARATOR = " ";
        private static final String HEADER_SEPARATOR = ":";
        private static final int PATTERN = 0;
        private static final int NAME = 0;
        private static final int VALUE = 1;
        // Matches: {id} AND {id: .*?}
        // group(1) extracts the name of the group (in that case "id").
        // group(3) extracts the regex if defined
        private static final Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

        // This regex matches everything in between path slashes.
        private static final String VARIABLE_ROUTES_DEFAULT_REGEX = "(?<%s>[^/]+)";

        final String pattern;
        final String regex;
        final ImmutableList<String> parameterNames;
        final ImmutableMap<String, String> headers;

        PathSpec(final String path, final String regex, final ImmutableList<String> parameterNames,
                 final ImmutableMap<String, String> headers)
        {
                this.pattern = path;
                this.regex = regex;
                this.parameterNames = parameterNames;
                this.headers = headers;
        }

        @Override
        public int hashCode()
        {
                int hash = 3;
                hash = 53 * hash + this.pattern.hashCode();
                hash = 53 * hash + this.headers.hashCode();
                return hash;
        }

        @Override
        public boolean equals(final Object obj)
        {
                if (this == obj) {
                        return true;
                }
                if (obj instanceof PathSpec) {
                        final PathSpec other = (PathSpec) obj;
                        return this.pattern.equals(other.pattern) && this.headers.equals(other.headers);
                }
                return false;
        }

        boolean isStatic()
        {
                return regex == null;
        }

        static PathSpec from(final String value)
        {
                final String[] parts = value.split(PathSpec.SEPARATOR);

                final String pattern = parts[PATTERN];
                final String regex = findRegex(pattern);
                final ImmutableList<String> parameterNames = findParameterNames(pattern);
                if (parts.length == 1) {
                        return new PathSpec(pattern, regex, parameterNames, ImmutableMap.of());
                }
                final ImmutableMap.Builder<String, String> headers = ImmutableMap.builder();
                for (int i = 1; i < parts.length; i++) {
                        if (parts[i] != null && !parts[i].trim().isEmpty()) {
                                final String[] header = parts[i].split(PathSpec.HEADER_SEPARATOR);
                                headers.put(header[NAME], header[VALUE]);
                        }
                }
                return new PathSpec(pattern, regex, parameterNames, headers.build());
        }

        /**
         * Transforms an url pattern like "/{name}/id/*" into a regex like "/([^/]*)/id/*."
         * <p/>
         * Also handles regular expressions if defined inside routes: For instance "/users/{username:
         * [a-zA-Z][a-zA-Z_0-9]}" becomes "/users/ ([a-zA-Z][a-zA-Z_0-9])"
         *
         * @return The converted regex with default matching regex - or the regex specified by the user.
         */
        static String findRegex(final String urlPattern)
        {
                final StringBuffer buffer = new StringBuffer();
                final Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(urlPattern);
                int pathParameterIndex = 0;
                while (matcher.find()) {
                        // By convention group 3 is the regex if provided by the user.
                        // If it is not provided by the user the group 3 is null.
                        final String namedVariablePartOfRoute = matcher.group(3);
                        final String namedVariablePartOfORouteReplacedWithRegex;

                        if (namedVariablePartOfRoute != null) {
                                // we convert that into a regex matcher group itself
                                String variableRegex = replacePosixClasses(namedVariablePartOfRoute);
                                namedVariablePartOfORouteReplacedWithRegex = String.format("(?<%s>%s)",
                                                                                           getPathParameterRegexGroupName(
                                                                                                   pathParameterIndex),
                                                                                           Matcher.quoteReplacement(
                                                                                                   variableRegex));
                        } else {
                                // we convert that into the default namedVariablePartOfRoute regex group
                                namedVariablePartOfORouteReplacedWithRegex = String.
                                        format(VARIABLE_ROUTES_DEFAULT_REGEX,
                                               getPathParameterRegexGroupName(
                                                       pathParameterIndex));
                        }
                        // we replace the current namedVariablePartOfRoute group
                        matcher.appendReplacement(buffer, namedVariablePartOfORouteReplacedWithRegex);
                        pathParameterIndex++;
                }

                if (pathParameterIndex == 0) {
                        // when no "dynamic" part found, no regex is found ;)
                        return null;
                } else {
                        // .. and we append the tail to complete the stringBuffer
                        matcher.appendTail(buffer);

                        return buffer.toString();
                }
        }

        /**
         * Replace any specified POSIX character classes with the Java equivalent.
         *
         * @param input
         * @return a Java regex
         */
        final static String replacePosixClasses(final String input)
        {
                return input
                        .replace(":alnum:", "\\p{Alnum}")
                        .replace(":alpha:", "\\p{L}")
                        .replace(":ascii:", "\\p{ASCII}")
                        .replace(":digit:", "\\p{Digit}")
                        .replace(":xdigit:", "\\p{XDigit}");
        }

        final static String getPathParameterRegexGroupName(final int pathParameterIndex)
        {
                return "p" + pathParameterIndex;
        }

        /**
         * Extracts the name of the parameters from a route
         * <p/>
         * /{my_id}/{my_name}
         * <p/>
         * would return a List with "my_id" and "my_name"
         *
         * @param uriPattern
         * @return a list with the names of all parameters in the url pattern
         */
        final static ImmutableList<String> findParameterNames(final String uriPattern)
        {
                final ImmutableList.Builder<String> parameters = ImmutableList.builder();

                final Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(uriPattern);
                while (matcher.find()) {
                        // group(1) is the name of the group. Must be always there...
                        // "/assets/{file}" and "/assets/{file:[a-zA-Z][a-zA-Z_0-9]}"
                        // will return file.
                        parameters.add(matcher.group(1));
                }

                return parameters.build();
        }
}
