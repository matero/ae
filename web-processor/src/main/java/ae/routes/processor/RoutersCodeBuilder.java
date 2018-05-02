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
import ae.web.OAuth2Flow;
import ae.web.RouterServlet;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

class RoutersCodeBuilder {
  private static final ClassName HTTP_SERVLET = ClassName.get(RouterServlet.class);

  RoutersCodeBuilder() {
  }

  JavaFile buildJavaCode(final RoutesDeclarations declarations) {
    final ClassName classname = ClassName.get(declarations.packageName, declarations.superClass);
    final TypeSpec.Builder router = TypeSpec.classBuilder(classname)
                                            .superclass(HTTP_SERVLET)
                                            .addModifiers(Modifier.ABSTRACT)
                                            .addAnnotation(AnnotationSpec.builder(Generated.class)
                                                                         .addMember("value", "$S", "AE/web-processor")
                                                                         .addMember("comments", "$S", declarations.paths)
                                                                         .addMember("date", "$S", declarations.date)
                                                                         .build())
                                            .addField(FieldSpec.builder(long.class,
                                                                        "serialVersionUID",
                                                                        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                                               .initializer("$LL", declarations.serialVersionUID).build());

    for (final HttpVerb httpVerb : HttpVerb.values()) {
      final ImmutableList<RouteDescriptor> routes = declarations.routesByVerb.get(httpVerb);

      if (routes.isEmpty()) {
        continue;
      }

      for (final RouteDescriptor route : routes) {
        router.addField(makeFieldFor(route));
      }

      router.addMethod(overrideVerbHandler(httpVerb, routes));
    }

    return JavaFile.builder(classname.packageName(), router.build()).build();
  }

  static FieldSpec makeFieldFor(final RouteDescriptor route) {
    final FieldSpec.Builder property = FieldSpec.builder(TypeName.get(route.type),
                                                         route.routeField(),
                                                         Modifier.PRIVATE, Modifier.FINAL);
    if (route.isDynamic()) {
      property.initializer("new $T($S, $T.compile($S))", route.type, route.pattern, Pattern.class, route.regex);
    } else {
      property.initializer("new $T($S)", route.type, route.pattern);
    }
    return property.build();
  }

  MethodSpec overrideVerbHandler(final HttpVerb httpVerb, final ImmutableList<RouteDescriptor> routes) {
    final MethodSpec.Builder httpVerbHandler = MethodSpec
                                                   .methodBuilder(httpVerb.handler)
                                                   .addAnnotation(Override.class)
                                                   .addModifiers(Modifier.PUBLIC)
                                                   .addParameter(HttpServletRequest.class, "request", Modifier.FINAL)
                                                   .addParameter(HttpServletResponse.class, "response", Modifier.FINAL)
                                                   .addException(ServletException.class)
                                                   .addException(IOException.class);
    if (hasDynamicRoutes(routes)) {
      httpVerbHandler.addStatement("final String[] routeParameters = new String[$L]", maxParametersCountAt(routes));
    }

    for (final RouteDescriptor route : routes) {
      final MethodSpec.Builder control;
      if (route.isDynamic()) {
        final ClassName interpreterClass = ClassName.get(Interpret.class);
        control = httpVerbHandler.beginControlFlow("if ($L.matches(request, routeParameters))", route.routeField());
        for (int i = 0; i < route.parametersCount(); i++) {
          control.addStatement("final $T $L = $T.$L(routeParameters[$L])",
                               route.parameterType(i),
                               route.parameterName(i),
                               interpreterClass,
                               route.parameterInterpreterMethod(i),
                               i);
        }
      } else {
        control = httpVerbHandler.beginControlFlow("if ($L.matches(request))", route.routeField());
      }

      final ClassName controllerClass = ClassName.get(route.controller);
      if (route.useCredentials) {
        control.addStatement("handle(new $T($L), (controller) -> $T.Director.of(controller).authorize((c) -> c.$L($L)))",
                             controllerClass,
                             route.ctorArgs,
                             ClassName.get(OAuth2Flow.class),
                             route.action,
                             route.arguments());
      } else {
        control.addStatement("handle(new $T($L), (controller) -> controller.$L($L))", controllerClass, route.ctorArgs, route.action, route.arguments());
      }

      control.addStatement("return");
      httpVerbHandler.endControlFlow();
    }
    httpVerbHandler.addStatement("$L(request, response)", httpVerb.unhandled);
    return httpVerbHandler.build();
  }

  boolean hasDynamicRoutes(final Iterable<RouteDescriptor> routes) {
    for (final RouteDescriptor r : routes) {
      if (r.isDynamic()) {
        return true;
      }
    }
    return false;
  }

  private Object maxParametersCountAt(final Iterable<RouteDescriptor> routes) {
    int maxParametersCount = 0;
    for (final RouteDescriptor r : routes) {
      if (r.isDynamic()) {
        if (r.parametersCount() > maxParametersCount) {
          maxParametersCount = r.parametersCount();
        }
      }
    }
    return maxParametersCount;
  }
}
