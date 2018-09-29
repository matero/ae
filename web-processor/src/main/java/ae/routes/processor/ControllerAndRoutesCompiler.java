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

import ae.annotation.processor.AnnotationProcessor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import ae.controller;
import ae.router;
import java.util.ArrayList;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"ae.router", "ae.controller"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ControllerAndRoutesCompiler extends AnnotationProcessor {

        private final RoutersCodeBuilder routerBuilder;
        private boolean shouldGenerateCode;

        public ControllerAndRoutesCompiler()
        {
                this(new Date(), new RoutersCodeBuilder());
        }

        ControllerAndRoutesCompiler(final Date today, final RoutersCodeBuilder routerBuilder)
        {
                super(today);
                this.routerBuilder = routerBuilder;
        }

        @Override
        public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment)
        {
                this.shouldGenerateCode = true;
                final TypeElement routerClass = routerAt(roundEnvironment);
                if (routerClass != null) {
                        readRoutes(routerClass, roundEnvironment);
                }
                return true;
        }

        private void readRoutes(final TypeElement routerClass, final RoundEnvironment roundEnvironment)
        {
                final ae.router router = routerClass.getAnnotation(ae.router.class);
                final String appPath;
                final String apiPath;
                final String admPath;
                if (router.application()== null) {
                        appPath = "/"; // to allow reading the controllers and try to find more errors
                        error(routerClass, "@router.application can't be null");
                } else {
                        appPath = router.application();
                }
                if (router.api() == null) {
                        apiPath = ""; // to allow reading the controllers and try to find more errors
                        error(routerClass, "@router.api can't be null");
                } else {
                        apiPath = router.api();
                }
                if (router.administration()== null) {
                        admPath = ""; // to allow reading the controllers and try to find more errors
                        error(routerClass, "@router.administration can't be null");
                } else {
                        admPath = router.administration();
                }

                final RoutesDeclarations.Builder declarations = RoutesDeclarations.builder(this.today);

                final RoutesReader interpreter = new RoutesReader(appPath,
                                                                  apiPath,
                                                                  admPath,
                                                                  this.elements,
                                                                  this.types,
                                                                  this.messager,
                                                                  declarations);
                for (final TypeElement controllerClass : controllersAt(roundEnvironment)) {
                        this.shouldGenerateCode &= interpreter.readRoutes(controllerClass);
                }
                if (this.shouldGenerateCode) {
                        final PackageElement routerPackage = elements.getPackageOf(routerClass);
                        declarations.packageName(routerPackage.getQualifiedName().toString());
                        final TypeMirror baseClass = routerClass.getSuperclass();
                        final String routerClassImpl = baseClass.toString();
                        declarations.routerClass(routerClassImpl);
                        final RoutesDeclarations routes = declarations.build();
                        generateJavaCode(routes);
                }
        }

        private TypeElement routerAt(final RoundEnvironment roundEnvironment)
        {
                final Set<? extends Element> routers = roundEnvironment.getElementsAnnotatedWith(router.class);
                switch (routers.size()) {
                        case 0:
                                return null;
                        case 1: {
                                final TypeElement router = (TypeElement) routers.iterator().next();
                                if (router.getKind() != ElementKind.CLASS) {
                                        error(router, "only classes can be marked as @ae.router");
                                        return null;
                                }
                                return router;
                        }
                        default:
                                for (final Element r : routers) {
                                        error(r, "only one @router can be defined");
                                }
                                return null;
                }
        }

        private List<TypeElement> controllersAt(final RoundEnvironment roundEnvironment)
        {
                final Set<? extends Element> controllers = roundEnvironment.getElementsAnnotatedWith(controller.class);
                final ArrayList<TypeElement> result = new ArrayList<>(controllers.size());

                for (final Element controller : controllers) {
                        if (controller.getKind() == ElementKind.CLASS) {
                                result.add((TypeElement) controller);
                        } else {
                                error(controller, "only classes can be defined as @ae.controller");
                        }
                }
                result.trimToSize();

                return result;
        }

        private void generateJavaCode(final RoutesDeclarations routes)
        {
                final JavaFile routerCode = this.routerBuilder.buildJavaCode(routes);
                try {
                        routerCode.writeTo(this.filer);
                } catch (final IOException e) {
                        error("could not write router code, reason: " + e.getMessage());
                }
        }

        @Override
        protected void error(final String message)
        {
                this.shouldGenerateCode = false;
                super.error(message);
        }

        @Override
        protected void error(final Throwable failure)
        {
                this.shouldGenerateCode = false;
                super.error(failure);
        }

        @Override
        protected void error(final Element element, final String errorMessage)
        {
                this.shouldGenerateCode = false;
                super.error(element, errorMessage);
        }

        @Override
        protected void error(final Element element, final Throwable failure)
        {
                this.shouldGenerateCode = false;
                super.error(element, failure);
        }
}
