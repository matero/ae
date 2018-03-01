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
import com.google.common.collect.ImmutableListMultimap;
import java.text.DateFormat;
import java.util.Date;

class RoutesDeclarations {
  final ImmutableList<RouteDescriptor> routes;
  final ImmutableListMultimap<HttpVerb, RouteDescriptor> routesByVerb;
  final String paths;
  final String date;
  final String packageName;
  final String superClass;
  final long serialVersionUID;

  RoutesDeclarations(final ImmutableList<RouteDescriptor> routes,
                     final ImmutableListMultimap<HttpVerb, RouteDescriptor> routesByVerb,
                     final String paths,
                     final String date,
                     final String packageName,
                     final String superClass,
                     final long serialVersionUID) {
    this.routes = routes;
    this.routesByVerb = routesByVerb;
    this.paths = paths;
    this.date = date;
    this.packageName = packageName;
    this.superClass = superClass;
    this.serialVersionUID = serialVersionUID;
  }

  boolean hasHandlerThatRequiresCredentials() {
    return routes.stream().anyMatch(route -> route.useCredentials);
  }

  static class Builder {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    final ImmutableList.Builder<RouteDescriptor> routes;
    final ImmutableListMultimap.Builder<HttpVerb, RouteDescriptor> routesByVerb;
    final StringBuilder paths;
    final Date today;
    final DateFormat dateFormat;
    final String superClass;
    private String packageName;

    Builder(final String superClass, final Date today) {
      this(ImmutableList.<RouteDescriptor>builder(),
           ImmutableListMultimap.<HttpVerb, RouteDescriptor>builder(),
           new StringBuilder(),
           today,
           new java.text.SimpleDateFormat(DEFAULT_DATE_FORMAT),
           superClass);
    }

    Builder(final ImmutableList.Builder<RouteDescriptor> routes,
            final ImmutableListMultimap.Builder<HttpVerb, RouteDescriptor> routesByVerb,
            final StringBuilder paths,
            final Date today,
            final DateFormat dateFormat,
            final String superClass) {
      this.routes = routes;
      this.routesByVerb = routesByVerb;
      this.paths = paths;
      this.today = today;
      this.dateFormat = dateFormat;
      this.superClass = superClass;
    }

    void addPath(final String path) {
      if (paths.length() > 0) {
        paths.append(", ");
      } else {
        paths.append("Build from specs at: ");
      }
      paths.append(path);
    }

    void addRoute(final RouteDescriptor route) {
      routes.add(route);
      routesByVerb.put(route.verb, route);
    }

    void packageName(final String value) {
      this.packageName = value;
    }

    RoutesDeclarations build() {
      return new RoutesDeclarations(routes.build(),
                                    routesByVerb.build(),
                                    paths.toString(),
                                    dateFormat.format(today),
                                    packageName,
                                    superClass,
                                    today.getTime());
    }
  }
}
