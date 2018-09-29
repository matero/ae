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

import ae.web.Interpret;
import ae.web.ParameterizedRoute;
import ae.web.Route;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

final class Parameter {

        final String name;
        final TypeMirror type;
        final String interpreterMethod;

        Parameter(String name, TypeMirror type, String interpreterMethod)
        {
                this.name = name;
                this.type = type;
                this.interpreterMethod = interpreterMethod;
        }
}

final class RouteDescriptor {

        private static final ImmutableMap<String, String> DEFAULT_HEADER = ImmutableMap.of("Content-Type",
                                                                                           "application/json");

        final Class<?> type;
        final HttpVerb verb;
        final String pattern;
        final String regex;
        final TypeElement controller;
        final String action;
        final ImmutableList<Parameter> parameters;
        final String ctorArgs;
        final boolean useCredentials;
        final ImmutableMap<String, String> headers;
        final String[] roles;
        final boolean admin;

        RouteDescriptor(final HttpVerb verb,
                        final String pattern,
                        final boolean useCredentials,
                        final String[] roles,
                        final boolean admin,
                        final TypeElement controller,
                        final String action,
                        final String ctorArgs)
        {
                this(verb, pattern, pattern, useCredentials, roles, admin, controller, action, ImmutableList.of(),
                     ctorArgs, DEFAULT_HEADER);
        }

        RouteDescriptor(final HttpVerb verb,
                        final String pattern,
                        final boolean useCredentials,
                        final String[] roles,
                        final boolean admin,
                        final TypeElement controller,
                        final String action,
                        final String ctorArgs,
                        final ImmutableMap<String, String> headers)
        {
                this(verb, pattern, pattern, useCredentials, roles, admin, controller, action, ImmutableList.of(), ctorArgs,
                     headers);
        }

        RouteDescriptor(final HttpVerb verb,
                        final String pattern,
                        final String regex,
                        final boolean useCredentials,
                        final String[] roles,
                        final boolean admin,
                        final TypeElement controller,
                        final String action,
                        final ImmutableList<Parameter> parameters,
                        final String ctorArgs)
        {
                this(verb, pattern, pattern, useCredentials, roles, admin, controller, action, parameters, ctorArgs,
                     DEFAULT_HEADER);
        }

        RouteDescriptor(final HttpVerb verb,
                        final String pattern,
                        final String regex,
                        final boolean useCredentials,
                        final String[] roles,
                        final boolean admin,
                        final TypeElement controller,
                        final String action,
                        final ImmutableList<Parameter> parameters,
                        final String ctorArgs,
                        final ImmutableMap<String, String> headers)
        {
                this.type = parameters.isEmpty() ? Route.class : ParameterizedRoute.class;
                this.verb = verb;
                this.pattern = pattern;
                this.regex = regex;
                this.useCredentials = useCredentials;
                if (roles == null) {
                        this.roles = new String[0];
                } else {
                        this.roles = roles.clone();
                }
                this.admin = admin;
                this.controller = controller;
                this.parameters = parameters;
                this.ctorArgs = ctorArgs;
                this.action = action;
                this.headers = headers;
        }

        @Override
        public int hashCode()
        {
                int hash = 3;
                hash = 17 * hash + this.verb.hashCode();
                hash = 17 * hash + this.pattern.hashCode();
                return hash;
        }

        @Override
        public boolean equals(final Object that)
        {
                if (this == that) {
                        return true;
                }
                if (that instanceof RouteDescriptor) {
                        final RouteDescriptor other = (RouteDescriptor) that;
                        return this.pattern.equals(other.pattern) && this.verb == other.verb;
                }
                return false;
        }

        @Override
        public String toString()
        {
                return "Route{type=" + type + "verb=" + verb + ", pattern=" + pattern + ", controller=" + controller + ", headers=" + headers + '}';
        }

        boolean hasRoleConstrains()
        {
                return this.roles.length > 0;
        }

        String rolesExpr()
        {
                switch (this.roles.length) {
                        case 1:
                                return "loggedUserHas($S)";
                        case 2:
                                return "loggedUserHasOneOf($S, $S)";
                        case 3:
                                return "loggedUserHasOneOf($S, $S, $S)";
                        case 4:
                                return "loggedUserHasOneOf($S, $S, $S, $S)";
                        case 5:
                                return "loggedUserHasOneOf($S, $S, $S, $S, $S)";
                        default:
                                throw new IllegalStateException("roles count not supported!");
                }
        }

        String routeField()
        {
                return this.verb.name() + '_' + controllerQualifiedName().replace('.', '_') + '_' + action;
        }

        String controllerQualifiedName()
        {
                return controller.getQualifiedName().toString();
        }

        final ClassName controllerClass()
        {
                return ClassName.bestGuess(controllerQualifiedName());
        }

        boolean isDynamic()
        {
                return type == ParameterizedRoute.class;
        }

        String arguments()
        {
                if (parametersCount() == 0) {
                        return "";
                } else if (parametersCount() == 1) {
                        return parameterName(0);
                } else {
                        final StringBuilder args = new StringBuilder();
                        args.append(parameterName(0));
                        for (int i = 1; i < parametersCount(); i++) {
                                args.append(',').append(parameterName(i));
                        }
                        return args.toString();
                }
        }

        int parametersCount()
        {
                return parameters.size();
        }

        String parameterName(final int i)
        {
                return parameters.get(i).name;
        }

        TypeMirror parameterType(final int i)
        {
                return parameters.get(i).type;
        }

        String parameterInterpreterMethod(final int i)
        {
                return parameters.get(i).interpreterMethod;
        }

        String headersFilterExpr()
        {
                headers.entrySet();
                final StringBuilder expr = new StringBuilder().append("if ($S.equals(request.getHeader($S))");
                for (int i = 1, headersCount = headers.keySet().size(); i < headersCount; i++) {
                        expr.append(" && $S.equals(request.getHeader($S))");
                }
                expr.append(')');
                return expr.toString();
        }

        Object[] headersFilterArgs()
        {
                final Object[] args = new Object[headers.size() * 2];
                int i = 0;
                for (final String headerName : headers.keySet()) {
                        args[i++] = headers.get(headerName);
                        args[i++] = headerName;
                }
                return args;
        }

        boolean hasHeaderSelection()
        {
                return !headers.isEmpty();
        }

        MethodSpec.Builder makeMatcher(final MethodSpec.Builder httpVerbHandler)
        {
                if (isDynamic()) {
                        return makeDynamicMatcher(httpVerbHandler);
                } else {
                        return makeStaticMatcher(httpVerbHandler);
                }
        }

        MethodSpec.Builder makeDynamicMatcher(final MethodSpec.Builder httpVerbHandler)
        {
                final ClassName interpreterClass = ClassName.get(Interpret.class);
                final MethodSpec.Builder matcher = httpVerbHandler.beginControlFlow(dynamicMatcher(), matcherParams());

                for (int i = 0; i < parametersCount(); i++) {
                        matcher.addStatement("final $T $L = $T.$L(routeParameters[$L])",
                                             parameterType(i),
                                             parameterName(i),
                                             interpreterClass,
                                             parameterInterpreterMethod(i),
                                             i);
                }

                return matcher;
        }

        String dynamicMatcher()
        {
                if (hasRoleConstrains()) {
                        return "if (" + rolesExpr() + " && $L.matches(request, routeParameters))";
                } else {
                        return "if ($L.matches(request, routeParameters))";
                }
        }

        MethodSpec.Builder makeStaticMatcher(final MethodSpec.Builder httpVerbHandler)
        {
                return httpVerbHandler.beginControlFlow(staticMatcher(), matcherParams());
        }

        String staticMatcher()
        {
                if (hasRoleConstrains()) {
                        return "if (" + rolesExpr() + " && $L.matches(request))";
                } else {
                        return "if ($L.matches(request))";
                }
        }

        Object[] matcherParams()
        {
                final Object[] params = java.util.Arrays.copyOf(this.roles, this.roles.length + 1);
                params[this.roles.length] = routeField();
                return params;
        }
}
