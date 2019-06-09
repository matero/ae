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

import ae.annotation.processor.AnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("ae.endpoint")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions("environment")
public class EndPointsCompiler extends AnnotationProcessor
{
  private final RoutersCodeBuilder routerBuilder;

  public EndPointsCompiler()
  {
    this(new Date(), new RoutersCodeBuilder());
  }

  EndPointsCompiler(final Date today, final RoutersCodeBuilder routerBuilder)
  {
    super(today);
    this.routerBuilder = routerBuilder;
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment)
  {
    final boolean isDevelopmentEnvironment = "development".equals(option("environment"));
    for (final TypeElement endpointClass : getEndpoints(roundEnvironment)) {
      final EndPointSpec.Builder declarations = EndPointSpec.builder(ClassName.get(endpointClass), this.today);
      final RoutesReader interpreter = new RoutesReader(messager, declarations);

      if (interpreter.buildRoutesFor(endpointClass)) {
        final String impl = endpointClass.getQualifiedName().toString() + "__aeImpl";
        declarations.implClass(impl);
        declarations.loggerDefined(hasLogger(endpointClass));
        final EndPointSpec routes = declarations.build();
        generateJavaCode(routes, isDevelopmentEnvironment);
      }
    }
    return true;
  }

  private boolean hasLogger(final TypeElement aClass)
  {
    for (final ExecutableElement method : ElementFilter.methodsIn(aClass.getEnclosedElements())) {
      final String methodName = method.getSimpleName().toString();
      final Set<Modifier> modifiers = method.getModifiers();
      if ("logger".equals(methodName) && modifiers.contains(Modifier.PROTECTED)) {
        // if its org.slf4j.Logger or a subtype of it, its ok; if its incompatible -> java compiler will reject it
        return true;
      }
    }
    return false;
  }

  private ImmutableList<TypeElement> getEndpoints(final RoundEnvironment roundEnvironment)
  {
    final Set<? extends Element> endpoints = roundEnvironment.getElementsAnnotatedWith(ae.endpoint.class);
    if (endpoints.isEmpty()) {
      return ImmutableList.of();
    } else {
      ImmutableList.Builder<TypeElement> result = ImmutableList.builder();
      for (final Element e : endpoints) {
        final TypeElement endpoint = (TypeElement) e;
        if (endpoint.getKind() != ElementKind.CLASS) {
          error(e, "only classes can be marked as @ae.endpoint");
          result = null;
        } else {
          if (result != null) {
            result.add(endpoint);
          }
        }
      }
      if (result == null) {
        return ImmutableList.of();
      }
      return result.build();
    }
  }

  private void generateJavaCode(final EndPointSpec routes, boolean isDevelopmentEnvironment)
  {
    final JavaFile routerCode = this.routerBuilder.buildJavaCode(routes, isDevelopmentEnvironment);
    try {
      routerCode.writeTo(this.filer);
    } catch (final IOException e) {
      error("could not write WebApp code, reason: " + e.getMessage());
    }
  }
}
