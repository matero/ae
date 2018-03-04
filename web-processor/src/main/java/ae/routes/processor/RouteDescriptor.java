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
import ae.web.route.ParameterizedRoute;
import ae.web.route.Route;

class RouteDescriptor {
  final Class<?> type;
  final HttpVerb verb;
  final String pattern;
  final String regex;
  final String handlerCannonicalName;
  final ImmutableList<String> parametersNames;
  final String ctorArgs;
  final boolean useCredentials;

  static final RouteDescriptor makeStatic(final HttpVerb verb,
                                          final String pattern,
                                          final boolean useCredentials,
                                          final String handlerCannonicalName,
                                          final String ctorArgs) {
    return new RouteDescriptor(verb, pattern, pattern, useCredentials, handlerCannonicalName, ctorArgs, ImmutableList.<String>of());
  }

  static final RouteDescriptor makeParameterized(final HttpVerb verb,
                                                 final String pattern,
                                                 final String regex,
                                                 final boolean useCredentials,
                                                 final String handlerCannonicalName,
                                                 final String ctorArgs,
                                                 final ImmutableList<String> parametersNames) {
    return new RouteDescriptor(verb, pattern, regex, useCredentials, handlerCannonicalName, ctorArgs, parametersNames);
  }

  RouteDescriptor(final HttpVerb verb,
                  final String pattern,
                  final String regex,
                  final boolean useCredentials,
                  final String handlerCannonicalName,
                  final String ctorArgs,
                  final ImmutableList<String> parametersNames) {
    this.type = parametersNames.isEmpty() ? Route.class : ParameterizedRoute.class;
    this.verb = verb;
    this.pattern = pattern;
    this.regex = regex;
    this.useCredentials = useCredentials;
    this.handlerCannonicalName = handlerCannonicalName;
    this.parametersNames = parametersNames;
    this.ctorArgs = ctorArgs;
  }

  @Override public int hashCode() {
    int hash = 3;
    hash = 17 * hash + this.verb.hashCode();
    hash = 17 * hash + this.pattern.hashCode();
    return hash;
  }

  @Override public boolean equals(final Object that) {
    if (this == that) {
      return true;
    }
    if (that instanceof RouteDescriptor) {
      final RouteDescriptor other = (RouteDescriptor) that;
      return this.pattern.equals(other.pattern) && this.verb == other.verb;
    }
    return false;
  }

  @Override public String toString() {
    return "Route{type=" + type + "verb=" + verb + ", pattern=" + pattern + ", handlerCannonicalName=" + handlerCannonicalName + '}';
  }

  String handlerField() {
    return this.verb.name() + '_' + this.handlerCannonicalName.replace('.', '_') + "_handler";
  }

  String routeField() {
    return this.verb.name() + '_' + this.handlerCannonicalName.replace('.', '_') + "_route";
  }

  boolean isDynamic() {
    return type == ParameterizedRoute.class;
  }

  String parametersNamesLiteral() {
    final StringBuilder params = new StringBuilder();
    params.append('"').append(parametersNames.get(0)).append('"');
    for (int i = 1; i < parametersNames.size(); ++i) {
      params.append(", \"").append(parametersNames.get(0)).append('"');
    }
    return params.toString();
  }
}
