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

import ae.web.ParameterizedRoute;
import ae.web.Route;
import com.google.common.collect.ImmutableList;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

final class Parameter {
  final String     name;
  final TypeMirror type;
  final String     interpreterMethod;

  Parameter(String name, TypeMirror type, String interpreterMethod) {
    this.name = name;
    this.type = type;
    this.interpreterMethod = interpreterMethod;
  }
}

final class RouteDescriptor {
  final Class<?>                 type;
  final HttpVerb                 verb;
  final String                   pattern;
  final String                   regex;
  final TypeElement              controller;
  final String                   action;
  final ImmutableList<Parameter> parameters;
  final String                   ctorArgs;
  final boolean                  useCredentials;

  RouteDescriptor(final HttpVerb verb,
                  final String pattern,
                  final boolean useCredentials,
                  final TypeElement controller,
                  final String action,
                  final String ctorArgs) {
    this(verb, pattern, pattern, useCredentials, controller, action, ImmutableList.of(), ctorArgs);
  }

  RouteDescriptor(final HttpVerb verb,
                  final String pattern,
                  final String regex,
                  final boolean useCredentials,
                  final TypeElement controller,
                  final String action,
                  final ImmutableList<Parameter> parameters,
                  final String ctorArgs) {
    this.type = parameters.isEmpty() ? Route.class : ParameterizedRoute.class;
    this.verb = verb;
    this.pattern = pattern;
    this.regex = regex;
    this.useCredentials = useCredentials;
    this.controller = controller;
    this.parameters = parameters;
    this.ctorArgs = ctorArgs;
    this.action = action;
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
    return "Route{type=" + type + "verb=" + verb + ", pattern=" + pattern + ", controller=" + controller + '}';
  }

  String routeField() {
    return this.verb.name() + '_' + controllerQualifiedName().replace('.', '_') + '_' + action;
  }

  String controllerQualifiedName() { return controller.getQualifiedName().toString(); }

  boolean isDynamic() {
    return type == ParameterizedRoute.class;
  }

  String arguments() {
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

  int parametersCount() { return parameters.size(); }
  String parameterName(final int i) { return parameters.get(i).name; }
  TypeMirror parameterType(final int i) { return parameters.get(i).type; }
  String parameterInterpreterMethod(final int i) { return parameters.get(i).interpreterMethod; }
}
