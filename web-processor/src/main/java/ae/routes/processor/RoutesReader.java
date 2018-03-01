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

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import ae.web.ParameterizedHandler;
import ae.web.ParameterizedTemplateHandler;
import ae.web.RequestHandler;
import ae.web.StaticHandler;
import ae.web.StaticTemplateHandler;
import com.opencsv.CSVReader;

class RoutesReader {
  // Matches: {id} AND {id: .*?}
  // group(1) extracts the name of the group (in that case "id").
  // group(3) extracts the regex if defined
  private static final Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

  // This regex matches everything in between path slashes.
  private static final String VARIABLE_ROUTES_DEFAULT_REGEX = "(?<%s>[^/]+)";

  private static final int SKIP_HEADER = 1;
  private static final char QUOTE_CHAR = '\"';
  private static final char SEPARATOR_CHAR = ',';

  private static final int ROUTE_HANDLER = 2;
  private static final int ROUTE_PATH = 1;
  private static final int ROUTE_HTTP_VERB = 0;

  private final Elements elements;
  private final Types types;
  private final Messager messager;

  private final TypeMirror parameterizedHandlerClass;
  private final TypeMirror staticHandlerClass;
  private final TypeMirror parameterizedTemplateHandlerClass;
  private final TypeMirror staticTemplateHandlerClass;
  private final TypeMirror withOauth2Flow;

  RoutesReader(final Elements elements, final Types types, final Messager messager) {
    this.elements = elements;
    this.types = types;
    this.messager = messager;

    staticHandlerClass = elements.getTypeElement(StaticHandler.class.getCanonicalName()).asType();
    staticTemplateHandlerClass = elements.getTypeElement(StaticTemplateHandler.class.getCanonicalName()).asType();
    parameterizedHandlerClass = elements.getTypeElement(ParameterizedHandler.class.getCanonicalName()).asType();
    parameterizedTemplateHandlerClass = elements.getTypeElement(ParameterizedTemplateHandler.class.getCanonicalName()).asType();
    withOauth2Flow = elements.getTypeElement(RequestHandler.WithOAuth2Flow.class.getCanonicalName()).asType();
  }

  /**
   * @param routesPaths
   * @param routesDeclarationsBuilder
   * @return null if no processing done (reports errors on such case), the route add
   */
  boolean readRoutes(final String[] paths, final RoutesDeclarations.Builder routesDeclarationsBuilder) {
    ImmutableList<String[]> csvLines = ImmutableList.of();
    boolean success = true;
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
        final RouteDescriptor route = makeRouteFrom(line);
        if (route != null) {
          routesDeclarationsBuilder.addRoute(route);
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
    String[] line;
    try (final FileReader reader = new FileReader(path);
            final CSVReader csv = new CSVReader(reader, SEPARATOR_CHAR, QUOTE_CHAR, SKIP_HEADER)) {
      while ((line = csv.readNext()) != null) {
        for (int i = 0; i < line.length; ++i) {
          line[i] = line[i].trim();
        }
        lines.add(line);
      }
    }
    return lines.build();
  }

  RouteDescriptor makeRouteFrom(final String[] declaration) {
    final HttpVerb verb = getVerb(declaration[ROUTE_HTTP_VERB]);
    final String handler = getHandler(declaration[ROUTE_HANDLER]);
    if (verb == null || handler == null) {
      return null;
    }

    final String regex = findRegex(declaration[ROUTE_PATH]);
    if (regex == null) {
      if (!isStaticRouteHandler(handler)) {
        printError(handler + "' does not extend " + StaticHandler.class.getName() + " or " + StaticTemplateHandler.class);
      }

      return RouteDescriptor.makeStatic(verb, declaration[ROUTE_PATH], useCredentials(handler), handler, getHandlerCtorArgs(handler));
    } else {
      if (!RoutesReader.this.isParameterizedRouteHandler(handler)) {
        printError(handler + "' does not extend " + ParameterizedHandler.class.getName() + " or " + ParameterizedTemplateHandler.class);
      }
      final ImmutableList<String> parameterNames = findParameterNames(declaration[ROUTE_PATH]);
      return RouteDescriptor.makeParameterized(verb, declaration[ROUTE_PATH], regex, useCredentials(handler), handler, getParameterizedHandlerCtorArgs(handler), parameterNames);
    }
  }

  boolean isStaticRouteHandler(final String handlerCannonicalName) {
    return isStaticRouteHandler(elements.getTypeElement(handlerCannonicalName).asType());
  }

  boolean isStaticRouteHandler(final TypeMirror aClass) {
    return types.isAssignable(aClass, staticHandlerClass) || types.isAssignable(aClass, staticTemplateHandlerClass);
  }

  boolean useCredentials(final String handlerCannonicalName) {
    final TypeElement handlerClass = elements.getTypeElement(handlerCannonicalName);
    if (handlerClass == null) {
      printError("Handler '" + handlerCannonicalName + "' does not exist.");
      return false;
    }
    final boolean requiresCredentials = requiresCredentials(handlerClass.asType());
    printNote(handlerCannonicalName + " requiresCredentials? " + requiresCredentials);
    return requiresCredentials;
  }

  boolean requiresCredentials(final TypeMirror handlerClass) {
    return types.isSubtype(handlerClass, withOauth2Flow);
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
      default:
        printError("HTTP Verb '" + value + "' isn't supported, supported verbs are: GET, POST, PUT, DELETE.");
        return null;
    }
  }

  String getHandler(final String handlerCannonicalName) {
    final TypeElement handlerClass = elements.getTypeElement(handlerCannonicalName);
    if (handlerClass == null) {
      printError("Handler '" + handlerCannonicalName + "' does not exist.");
      return null;
    }
    return handlerCannonicalName;
  }

  String getHandlerCtorArgs(final String handlerCannonicalName) {
    final TypeElement handlerClass = elements.getTypeElement(handlerCannonicalName);
    if (handlerClass == null) {
      printError("Handler '" + handlerCannonicalName + "' does not exist.");
      return null;
    }
    final TypeMirror handlerType = handlerClass.asType();
    if (isTemplateHandler(handlerType)) {
      return "request, response, webContext(request, response), templateEngine()";
    } else {
      return "request, response";
    }
  }

  String getParameterizedHandlerCtorArgs(final String handlerCannonicalName) {
    final TypeElement handlerClass = elements.getTypeElement(handlerCannonicalName);
    if (handlerClass == null) {
      printError("Handler '" + handlerCannonicalName + "' does not exist.");
      return null;
    }
    final TypeMirror handlerType = handlerClass.asType();
    if (isTemplateHandler(handlerType)) {
      return "request, response, webContext(request, response), templateEngine(), routeParameters.build()";
    } else {
      return "request, response, routeParameters.build()";
    }
  }

  boolean isTemplateHandler(final TypeMirror handlerClass) {
    return types.isSubtype(handlerClass, staticTemplateHandlerClass) || types.isSubtype(handlerClass, parameterizedTemplateHandlerClass);
  }

  /**
   * Transforms an url pattern like "/{name}/id/*" into a regex like "/([^/]*)/id/*."
   * <p/>
   * Also handles regular expressions if defined inside routes: For instance "/users/{username: [a-zA-Z][a-zA-Z_0-9]}" becomes "/users/([a-zA-Z][a-zA-Z_0-9])"
   *
   * @return The converted regex with default matching regex - or the regex specified by the user.
   */
  String findRegex(final String urlPattern) {
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

  boolean isParameterizedRouteHandler(final String handlerCannonicalName) {
    return isParameterizedRouteHandler(elements.getTypeElement(handlerCannonicalName).asType());
  }

  boolean isParameterizedRouteHandler(final TypeMirror aClass) {
    return types.isAssignable(aClass, parameterizedHandlerClass) || types.isAssignable(aClass, parameterizedTemplateHandlerClass);
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
