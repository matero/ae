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

import ae.web.RouterServlet;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class RoutesDeclarations {

        final ImmutableList<RouteDescriptor> routes;
        final ImmutableListMultimap<HttpVerb, RouteDescriptor> routesByVerb;
        final String paths;
        final String date;
        final String packageName;
        final Class<? extends RouterServlet> superClass;
        final long serialVersionUID;
        final String routerClass;
        final String basePath;
        final String apiPath;

        RoutesDeclarations(final String basePath,
                           final String apiPath,
                           final ImmutableList<RouteDescriptor> routes,
                           final ImmutableListMultimap<HttpVerb, RouteDescriptor> routesByVerb,
                           final String paths,
                           final String date,
                           final String packageName,
                           final String routerClass,
                           final Class<? extends RouterServlet> superClass,
                           final long serialVersionUID)
        {
                this.basePath = basePath;
                this.apiPath = apiPath;
                this.routes = routes;
                this.routesByVerb = routesByVerb;
                this.paths = paths;
                this.date = date;
                this.packageName = packageName;
                this.superClass = superClass;
                this.serialVersionUID = serialVersionUID;
                this.routerClass = routerClass;
        }

        String webServletValue()
        {
                if (this.basePath.endsWith("*")) {
                        return this.basePath;
                } else {
                        if ("".equals(this.basePath)) {
                                return "/*";
                        } else {
                                return this.basePath + '*';
                        }
                }
        }

        List<TypeElement> controllers()
        {
                return routes.stream().map(route -> route.controller).distinct().collect(toList());
        }

        ClassName routerClassName()
        {
                return ClassName.get(this.packageName, this.routerClass);
        }

        static Builder builder(final Date today)
        {
                return new Builder(ImmutableList.<RouteDescriptor>builder(),
                                   ImmutableListMultimap.<HttpVerb, RouteDescriptor>builder(),
                                   new StringBuilder(),
                                   today,
                                   new java.text.SimpleDateFormat(Builder.DEFAULT_DATE_FORMAT),
                                   RouterServlet.class);
        }

        static class Builder {

                private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

                final ImmutableList.Builder<RouteDescriptor> routes;
                final ImmutableListMultimap.Builder<HttpVerb, RouteDescriptor> routesByVerb;
                final StringBuilder paths;
                final Date today;
                final DateFormat dateFormat;
                final Class<? extends RouterServlet> superClass;
                private String packageName;
                private String routerClass;
                private String basePath;
                private String apiPath;

                Builder(final ImmutableList.Builder<RouteDescriptor> routes,
                        final ImmutableListMultimap.Builder<HttpVerb, RouteDescriptor> routesByVerb,
                        final StringBuilder paths,
                        final Date today,
                        final DateFormat dateFormat,
                        final Class<? extends RouterServlet> superClass)
                {
                        this.routes = routes;
                        this.routesByVerb = routesByVerb.orderValuesBy(
                                ComparingRouteDescriptor.BY_PATTERN_AND_HEADERS_COUNT);
                        this.paths = paths;
                        this.today = today;
                        this.dateFormat = dateFormat;
                        this.superClass = superClass;
                }

                void addRoute(final RouteDescriptor route)
                {
                        if (route != null) {
                                routes.add(route);
                                routesByVerb.put(route.verb, route);
                        }
                }

                void packageName(final String value)
                {
                        this.packageName = value;
                }

                void routerClass(final String value)
                {
                        this.routerClass = value;
                }

                public void basePath(final String basePath)
                {
                        this.basePath = basePath;
                }

                public void apiPath(final String apiPath)
                {
                        this.apiPath = apiPath;
                }

                RoutesDeclarations build()
                {
                        return new RoutesDeclarations(this.basePath,
                                                      this.apiPath,
                                                      this.routes.build(),
                                                      this.routesByVerb.build(),
                                                      this.paths.toString(),
                                                      this.dateFormat.format(this.today),
                                                      this.packageName,
                                                      this.routerClass,
                                                      this.superClass,
                                                      this.today.getTime());
                }

                enum ComparingRouteDescriptor implements Comparator<RouteDescriptor> {
                        BY_PATTERN_AND_HEADERS_COUNT;

                        @Override
                        public int compare(final RouteDescriptor a, final RouteDescriptor b)
                        {
                                final int patternCmp = a.pattern.compareTo(b.pattern);
                                if (patternCmp == 0) {
                                        return b.headers.size() - a.headers.size();
                                } else {
                                        return patternCmp;
                                }
                        }
                }
        }
}
