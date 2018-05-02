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

import ae.OAuth2;
import ae.Template;
import ae.web.ControllerWithThymeleafSupport;
import com.google.common.collect.ImmutableList;
import com.opencsv.CSVReader;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * On routes.csv, you can declare routes as:
 * <p>
 * <code>
 * <pre>
 *  HTTP Method | URI               | Controller      | Action
 * -------------+-------------------+-----------------+--------
 *      GET     | /books            | book.Controller | index
 *      GET     | /books/create     | book.Controller | create
 *     POST     | /books            | book.Controller | save
 *      GET     | /books/${id}      | book.Controller | show
 *      GET     | /books/${id}/edit | book.Controller | edit
 *      PUT     | /books/${id}      | book.Controller | update
 *    DELETE    | /books/${id}      | book.Controller | delete
 * </pre>
 * </code>
 * <p>
 * As rest resource are pretty common, you can simplify this even more, to something as:
 * <p>
 * <code>
 * <pre>
 *  HTTP Method | URI               | Controller      | Action
 * -------------+-------------------+-----------------+--------
 *    RESOURCE  | /books            | book.Controller |
 * </pre>
 * </code>
 */
class RoutesReader {
  // Matches: {id} AND {id: .*?}
  // group(1) extracts the name of the group (in that case "id").
  // group(3) extracts the regex if defined
  private static final Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

  // This regex matches everything in between path slashes.
  private static final String VARIABLE_ROUTES_DEFAULT_REGEX = "(?<%s>[^/]+)";

  private static final int  SKIP_HEADER    = 1;
  private static final char QUOTE_CHAR     = '\"';
  private static final char SEPARATOR_CHAR = '|';

  private static final int ACTION     = 3;
  private static final int CONTROLLER = 2;
  private static final int PATH       = 1;
  private static final int HTTP_VERB  = 0;

  private final Elements elements;
  private final Types    types;
  private final Messager messager;

  private final TypeMirror controllerWithThymeleafSupportClass;

  private String lastControllerQualifiedName;

  RoutesReader(final Elements elements, final Types types, final Messager messager) {
    this.elements = elements;
    this.types = types;
    this.messager = messager;

    this.controllerWithThymeleafSupportClass = elements.getTypeElement(ControllerWithThymeleafSupport.class.getCanonicalName()).asType();
    this.lastControllerQualifiedName = "";
  }

  /**
   * @param routesDeclarationsBuilder
   * @return null if no processing done (reports errors on such case), the route add
   */
  boolean readRoutes(final String[] paths, final RoutesDeclarations.Builder routesDeclarationsBuilder) {
    ImmutableList<String[]> csvLines = ImmutableList.of();
    boolean                 success  = true;
    for (final String path : paths) {
      final File file = new File(path);
      try {
        csvLines = readCsvLinesFrom(file);
        printNote("using routes from'" + file.getAbsolutePath() + "'.");
        routesDeclarationsBuilder.addPath(path);
      } catch (final IOException e) {
        printWarning("routes file '" + file.getAbsolutePath() + "' can't be processed, cause: " + e.getMessage());
        success = false;
      }
    }
    if (success) {
      for (final String[] line : csvLines) {
        final Iterable<RouteDescriptor> routes = makeRoutesFrom(line);
        if (routes != null) {
          routesDeclarationsBuilder.addRoutes(routes);
        } else {
          success = false;
        }
      }
    } else {
      printError("no routes file could be processed.");
    }
    return success;
  }

  ImmutableList<String[]> readCsvLinesFrom(final File path) throws IOException {
    final ImmutableList.Builder<String[]> lines = ImmutableList.builder();
    String[]                              line;
    try (final FileReader reader = new FileReader(path);
         final CSVReader csv = new CSVReader(reader, SEPARATOR_CHAR, QUOTE_CHAR, SKIP_HEADER)) {
      while ((line = csv.readNext()) != null) {
        if (line[0].startsWith("-")) {
          continue;
        }
        for (int i = 0; i < line.length; ++i) {
          if (line[i] == null) {
            line[i] = "";
          } else {
            line[i] = line[i].trim();
          }
        }
        lines.add(line);
      }
    }
    return lines.build();
  }

  ImmutableList<RouteDescriptor> makeRoutesFrom(final String[] declaration) {
    final HttpVerb verb = getVerb(declaration[HTTP_VERB]);
    if (verb == null) {
      printError("HTTP Verb not defined.");
      return null;
    }
    String controllerQualifiedName;
    if (declaration[CONTROLLER].isEmpty()) {
      if (lastControllerQualifiedName.isEmpty()) {
        printError("Controller not defined.");
        return null;
      } else {
        controllerQualifiedName = lastControllerQualifiedName;
      }
    } else {
      lastControllerQualifiedName = controllerQualifiedName = declaration[CONTROLLER];
    }
    final TypeElement controller = readControllerAt(controllerQualifiedName);
    if (controller == null) {
      printError("Controller '" + controller + "' does not exist.");
      return null;
    }

    final String path = declaration[PATH];
    if (path == null) {
      return null;
    }
    if (verb == HttpVerb.RESOURCE) {
      return restResource(path, controller);
    } else {
      return ImmutableList.of(makeRouteFrom(verb, path, controller, declaration[ACTION]));
    }
  }

  private ImmutableList<RouteDescriptor> restResource(final String path, final TypeElement controller) {
    return ImmutableList.of(
        makeRouteFrom(HttpVerb.GET, path, controller, "index"),
        makeRouteFrom(HttpVerb.GET, path + "/create", controller, "create"),
        makeRouteFrom(HttpVerb.POST, path, controller, "save"),
        makeRouteFrom(HttpVerb.GET, path + "/${id}", controller, "show"),
        makeRouteFrom(HttpVerb.GET, path + "/${id}/edit", controller, "edit"),
        makeRouteFrom(HttpVerb.PUT, path + "/${id}", controller, "update"),
        makeRouteFrom(HttpVerb.DELETE, path + "/${id}", controller, "delete")
    );
  }

  private RouteDescriptor makeRouteFrom(final HttpVerb verb, final String path, final TypeElement controller, final String action) {
    if (action == null) {
      printError("Action not defined.");
      return null;
    }

    final ExecutableElement actionMethod = findMethod(controller, action);
    if (actionMethod == null) {
      printError("Controller '" + controller.getQualifiedName().toString() + "' doesn't have a method named '" + action + "'.");
      return null;
    }
    final String  regex         = findRegex(path);
    final String  ctorArguments = ctorArgumentsFor(actionMethod);
    final boolean credentials   = useCredentials(actionMethod);

    final ImmutableList<Parameter> parameters = parametersAt(actionMethod);

    if (regex == null) {
      if (parameters.size() > 0) {
        printError("Route doesn't have parameters, but action method requires " + parameters.size() + " parameters.");
        return null;
      }
      return new RouteDescriptor(verb, path, credentials, controller, action, ctorArguments);
    } else {
      final ImmutableList<String> parameterNames = findParameterNames(path);
      if (parameterNames.size() != parameters.size()) {
        printError("Route parameters count (" + parameterNames.size() + ") differs from action parameters count (" + parameters.size() + '.');
        return null;
      }
      return new RouteDescriptor(verb, path, regex, credentials, controller, action, parameters, ctorArguments);
    }
  }

  ImmutableList<Parameter> parametersAt(final ExecutableElement actionMethod) {
    final ImmutableList.Builder<Parameter> parameters = ImmutableList.builder();
    for (final VariableElement parameter : actionMethod.getParameters()) {
      final TypeMirror type = parameter.asType();
      final String     name = parameter.getSimpleName().toString();
      parameters.add(new Parameter(name, type, interpreterOf(type)));
    }
    return parameters.build();
  }

  String interpreterOf(final TypeMirror type) {
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

  private ExecutableElement findMethod(final TypeElement controller, final String action) {
    for (final Element e : controller.getEnclosedElements()) {
      if (e instanceof ExecutableElement) {
        final ExecutableElement method = (ExecutableElement) e;
        if (method.getSimpleName().toString().equals(action)) {
          return method;
        }
      }
    }
    return null;
  }

  boolean useCredentials(final ExecutableElement actionMethod) {
    final OAuth2 oauth2 = actionMethod.getAnnotation(OAuth2.class);
    return oauth2 != null;
  }

  private HttpVerb getVerb(final String value) {
    switch (value) {
      case "GET":
        return HttpVerb.GET;
      case "POST":
        return HttpVerb.POST;
      case "PUT":
        return HttpVerb.PUT;
      case "DELETE":
        return HttpVerb.DELETE;
      case "RESOURCE":
        return HttpVerb.RESOURCE;
      default:
        printError("HTTP Verb '" + value + "' isn't supported, supported verbs are: GET, POST, PUT, DELETE, RESOURCE.");
        return null;
    }
  }

  TypeElement readControllerAt(final String controllerQualifiedName) {
    final TypeElement controller = elements.getTypeElement(controllerQualifiedName);
    if (controller == null) {
      printError("Handler '" + controllerQualifiedName + "' does not exist.");
      return null;
    }
    return controller;
  }

  String ctorArgumentsFor(final ExecutableElement actionMethod) {
    final ae.Template template = actionMethod.getAnnotation(ae.Template.class);
    if (template != null) {
      return "request, response, webContext(request, response), templateEngine()";
    } else {
      return "request, response";
    }
  }

  private boolean supportsThymeleaf(final TypeElement controller) {
    return types.isSubtype(controller.asType(), controllerWithThymeleafSupportClass);
  }

  /**
   * Transforms an url pattern like "/{name}/id/*" into a regex like "/([^/]*)/id/*."
   * <p/>
   * Also handles regular expressions if defined inside routes: For instance "/users/{username: [a-zA-Z][a-zA-Z_0-9]}" becomes "/users/
   * ([a-zA-Z][a-zA-Z_0-9])"
   *
   * @return The converted regex with default matching regex - or the regex specified by the user.
   */
  String findRegex(final String urlPattern) {
    final StringBuffer buffer             = new StringBuffer();
    final Matcher      matcher            = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(urlPattern);
    int                pathParameterIndex = 0;
    while (matcher.find()) {
      // By convention group 3 is the regex if provided by the user.
      // If it is not provided by the user the group 3 is null.
      final String namedVariablePartOfRoute = matcher.group(3);
      final String namedVariablePartOfORouteReplacedWithRegex;

      if (namedVariablePartOfRoute != null) {
        // we convert that into a regex matcher group itself
        String variableRegex = replacePosixClasses(namedVariablePartOfRoute);
        namedVariablePartOfORouteReplacedWithRegex = String.format("(?<%s>%s)",
                                                                   getPathParameterRegexGroupName(pathParameterIndex),
                                                                   Matcher.quoteReplacement(variableRegex));
      } else {
        // we convert that into the default namedVariablePartOfRoute regex group
        namedVariablePartOfORouteReplacedWithRegex = String.format(VARIABLE_ROUTES_DEFAULT_REGEX,
                                                                   getPathParameterRegexGroupName(pathParameterIndex));
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
  final String replacePosixClasses(final String input) {
    return input
               .replace(":alnum:", "\\p{Alnum}")
               .replace(":alpha:", "\\p{L}")
               .replace(":ascii:", "\\p{ASCII}")
               .replace(":digit:", "\\p{Digit}")
               .replace(":xdigit:", "\\p{XDigit}");
  }

  final String getPathParameterRegexGroupName(final int pathParameterIndex) {
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
  final ImmutableList<String> findParameterNames(final String uriPattern) {
    final ImmutableList.Builder<String> parameters = ImmutableList.builder();

    final Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(uriPattern);
    while (matcher.find()) {
      // group(1) is the name of the group. Must be always there...
      // "/assets/{file}" and "/assets/{file: [a-zA-Z][a-zA-Z_0-9]}"
      // will return file.
      parameters.add(matcher.group(1));
    }

    return parameters.build();
  }

  void printError(final String message) {
    messager.printMessage(Diagnostic.Kind.ERROR, message);
  }

  void printWarning(final String message) {
    messager.printMessage(Diagnostic.Kind.WARNING, message);
  }

  void printNote(final String message) {
    messager.printMessage(Diagnostic.Kind.NOTE, message);
  }
}
