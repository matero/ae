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
package ae.web.processor;

import ae.web.RouterServlet;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.text.DateFormat;
import java.util.Date;

import static com.google.common.collect.Iterables.any;

class EndPointSpec
{
  final ImmutableList<Route> routes;
  final ImmutableListMultimap<HttpVerb, Route> routesByVerb;
  final String paths;
  final String date;
  final ClassName superClass;
  final long serialVersionUID;
  final String routerClass;
  final String path;
  final boolean noLoggerDefined;

  EndPointSpec(final String path,
               final ImmutableList<Route> routes,
               final ImmutableListMultimap<HttpVerb, Route> routesByVerb,
               final String paths,
               final String date,
               final String routerClass,
               final ClassName superClass,
               final long serialVersionUID,
               final boolean noLoggerDefined)
  {
    this.path = path;
    this.routes = routes;
    this.routesByVerb = routesByVerb;
    this.paths = paths;
    this.date = date;
    this.superClass = superClass;
    this.serialVersionUID = serialVersionUID;
    this.routerClass = routerClass;
    this.noLoggerDefined = noLoggerDefined;
  }

  TypeName routerClass()
  {
    return ClassName.get(RouterServlet.class);
  }

  enum Has implements Predicate<Route>
  {
    RoleConstraints;

    @Override
    public boolean apply(final Route route)
    {
      return route.hasRoleConstrains();
    }
  }

  boolean hasRoleConstraints()
  {
    return any(routes, Has.RoleConstraints);
  }

  ClassName routerClassName()
  {
    return ClassName.bestGuess(routerClass);
  }

  static Builder builder(final ClassName endpointClass, final Date today)
  {
    return new Builder(ImmutableList.<Route>builder(),
                       ImmutableListMultimap.<HttpVerb, Route>builder(),
                       new StringBuilder(),
                       today,
                       new java.text.SimpleDateFormat(Builder.DEFAULT_DATE_FORMAT),
                       endpointClass);
  }

  static class Builder
  {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    final ImmutableList.Builder<Route> routes;
    final ImmutableListMultimap.Builder<HttpVerb, Route> routesByVerb;
    final StringBuilder paths;
    final Date today;
    final DateFormat dateFormat;
    private ClassName endpointClass;
    private String implClass;
    private String path;
    private boolean loggerDefined;

    Builder(final ImmutableList.Builder<Route> routes,
            final ImmutableListMultimap.Builder<HttpVerb, Route> routesByVerb,
            final StringBuilder paths,
            final Date today,
            final DateFormat dateFormat,
            final ClassName endpointClass)
    {
      this.routes = routes;
      this.routesByVerb = routesByVerb;
      this.paths = paths;
      this.today = today;
      this.dateFormat = dateFormat;
      this.endpointClass = endpointClass;
    }

    void addRoute(final Route route)
    {
      if (route != null) {
        routes.add(route);
        routesByVerb.put(route.verb, route);
      }
    }

    void implClass(final String value)
    {
      implClass = value;
    }

    void path(final String value)
    {
      path = value;
    }

    EndPointSpec build()
    {
      return new EndPointSpec(path,
                              routes.build(),
                              routesByVerb.build(),
                              paths.toString(),
                              dateFormat.format(today),
                              implClass,
                              endpointClass,
                              today.getTime(),
                              !loggerDefined);
    }

    void loggerDefined(final boolean value)
    {
      loggerDefined = value;
    }
  }


}
