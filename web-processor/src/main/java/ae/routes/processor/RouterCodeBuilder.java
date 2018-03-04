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
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ae.web.route.RouterServlet;

class RouterCodeBuilder {
  private static final ClassName HTTP_SERVLET = ClassName.get(RouterServlet.class);

  RouterCodeBuilder() {
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
        router.addField(routeField(router, route, httpVerb));
      }

      router.addMethod(overrideVerbHandler(httpVerb, router, routes));
    }

    return JavaFile.builder(classname.packageName(), router.build()).build();
  }

  static FieldSpec routeField(final TypeSpec.Builder routerServlet, final RouteDescriptor route, final HttpVerb httpVerb) {
    final FieldSpec.Builder property = FieldSpec.builder(TypeName.get(route.type),
                                                         route.routeField(),
                                                         Modifier.PRIVATE, Modifier.FINAL);
    if (route.isDynamic()) {
      property.initializer("new $T($S, $T.compile($S), $T.of($L))",
                           route.type,
                           route.pattern,
                           Pattern.class,
                           route.regex,
                           ImmutableList.class,
                           route.parametersNamesLiteral());
    } else {
      property.initializer("new $T($S)", route.type, route.pattern);
    }
    return property.build();
  }

  MethodSpec overrideVerbHandler(final HttpVerb httpVerb, final TypeSpec.Builder router, final ImmutableList<RouteDescriptor> routes) {
    final MethodSpec.Builder httpVerbHandler = MethodSpec
            .methodBuilder(httpVerb.handler)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(HttpServletRequest.class, "request", Modifier.FINAL)
            .addParameter(HttpServletResponse.class, "response", Modifier.FINAL)
            .addException(ServletException.class)
            .addException(IOException.class);
    if (hasDynamicRoutes(routes)) {
      httpVerbHandler.addStatement("final $T routeParameters = $T.builder()",
                                   ParameterizedTypeName.get(ImmutableMap.Builder.class, String.class, String.class),
                                   ClassName.get(ImmutableMap.class));
    }

    for (final RouteDescriptor r : routes) {
      final MethodSpec.Builder control;
      if (r.isDynamic()) {
        control = httpVerbHandler.beginControlFlow("if ($L.matches(request, routeParameters))", r.routeField());
        if (r.useCredentials) {
          control.addStatement("OAuth2FlowDirector.of(new $T(" + r.ctorArgs + ")).authorize()", ClassName.bestGuess(r.handlerCannonicalName));
        } else {
          control.addStatement("new $T(" + r.ctorArgs + ").handle()", ClassName.bestGuess(r.handlerCannonicalName));
        }
      } else {
        control = httpVerbHandler.beginControlFlow("if ($L.matches(request))", r.routeField());
        if (r.useCredentials) {
          control.addStatement("OAuth2FlowDirector.of(new $T(" + r.ctorArgs + ")).authorize()", ClassName.bestGuess(r.handlerCannonicalName));
        } else {
          control.addStatement("new $T(" + r.ctorArgs + ").handle()", ClassName.bestGuess(r.handlerCannonicalName));
        }
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

  TypeName handlerType(final RouteDescriptor route) {
    return ClassName.bestGuess(route.handlerCannonicalName);
  }
}
