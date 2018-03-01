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

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import ae.Router;
import ae.annotation.processor.AnnotationProcessor;

@AutoService(Processor.class)
@SupportedAnnotationTypes("ae.Router")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RoutesCompiler extends AnnotationProcessor {
  private RoutesReader routesReader;

  public RoutesCompiler() {
    this(new Date());
  }

  RoutesCompiler(final Date today) {
    super(today);
  }

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    routesReader = new RoutesReader(processingEnv.getElementUtils(), processingEnv.getTypeUtils(), processingEnv.getMessager());
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations,
                         final RoundEnvironment roundEnv) {
    final RouterCodeBuilder routerBuilder = new RouterCodeBuilder();

    for (final Element element : roundEnv.getElementsAnnotatedWith(Router.class)) {
      final TypeElement routerClass = (TypeElement) element;
      if (routerClass.getKind() != ElementKind.CLASS) {
        error(element, "Only classes can be annotated as @" + Router.class.getCanonicalName());
        continue;
      }
      final Router router = element.getAnnotation(Router.class);
      final List<? extends TypeMirror> supertypes = types.directSupertypes(element.asType());

      if (supertypes.isEmpty()) {
        error(element, "you must define a superclass");
        continue;
      }
      if (supertypes.size() != 1) {
        error(element, "you can only define ONE superclass, no interfaces");
        continue;
      }
      final String superClass = types.asElement(supertypes.get(0)).getSimpleName().toString();
      final RoutesDeclarations.Builder builder = new RoutesDeclarations.Builder(superClass, today);
      builder.packageName(elements.getPackageOf(routerClass).toString());
      if (routesReader.readRoutes(router.routes(), builder)) {
        final RoutesDeclarations routes = builder.build();
        final JavaFile routerCode = routerBuilder.buildJavaCode(routes);
        try {
          routerCode.writeTo(filer);
        } catch (final IOException e) {
          error(element, "could not write router code, reason: " + e.getMessage());
        }
      }
    }
    return true;
  }
}
