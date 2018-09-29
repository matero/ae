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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

enum HttpVerb {
        GET("doGet", "unhandledGet") {
                @Override
                void buildRoute(final TypeElement controllerClass,
                                final ExecutableElement method,
                                final RoutesReader interpreter)
                {
                        final ae.GET metadata = method.getAnnotation(ae.GET.class);
                        if (metadata != null) {
                                interpreter.buildRoute(this,
                                                       metadata.path(),
                                                       metadata.template(),
                                                       metadata.oauth2(),
                                                       metadata.roles(),
                                                       method,
                                                       controllerClass);
                        }
                }

        },
        POST("doPost", "unhandledPost") {
                @Override
                void buildRoute(final TypeElement controllerClass,
                                final ExecutableElement method,
                                final RoutesReader interpreter)
                {
                        final ae.POST metadata = method.getAnnotation(ae.POST.class);
                        if (metadata != null) {
                                interpreter.buildRoute(this,
                                                       metadata.path(),
                                                       metadata.template(),
                                                       metadata.oauth2(),
                                                       metadata.roles(),
                                                       method,
                                                       controllerClass);
                        }
                }

        },
        PUT("doPut", "unhandledPut") {
                @Override
                void buildRoute(final TypeElement controllerClass,
                                final ExecutableElement method,
                                final RoutesReader interpreter)
                {
                        final ae.PUT metadata = method.getAnnotation(ae.PUT.class);
                        if (metadata != null) {
                                interpreter.buildRoute(this,
                                                       metadata.path(),
                                                       metadata.template(),
                                                       metadata.oauth2(),
                                                       metadata.roles(),
                                                       method,
                                                       controllerClass);
                        }
                }

        },
        DELETE("doDelete", "unhandledDelete") {
                @Override
                void buildRoute(final TypeElement controllerClass,
                                final ExecutableElement method,
                                final RoutesReader interpreter)
                {
                        final ae.DELETE metadata = method.getAnnotation(ae.DELETE.class);
                        if (metadata != null) {
                                interpreter.buildRoute(this,
                                                       metadata.path(),
                                                       metadata.template(),
                                                       metadata.oauth2(),
                                                       metadata.roles(),
                                                       method,
                                                       controllerClass);
                        }
                }

        };

        final String handler;
        final String unhandled;

        HttpVerb(final String handler, final String unhandled)
        {
                this.handler = handler;
                this.unhandled = unhandled;
        }

        abstract void buildRoute(TypeElement controllerClass, ExecutableElement method, RoutesReader interpreter);
}
