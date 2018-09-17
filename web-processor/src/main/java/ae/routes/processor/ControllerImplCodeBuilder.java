/*
 * The MIT License
 *
 * Copyright 2018 jj.
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

import ae.web.ControllerWithThymeleafSupport;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Builds definitions for controllers if they are required.
 */
class ControllerImplCodeBuilder {

    private final TypeMirror controllerWithThymeleafSupportClass;
    private final Types types;

    ControllerImplCodeBuilder(final Elements elements, final Types types)
    {
        this.controllerWithThymeleafSupportClass = elements.getTypeElement(ControllerWithThymeleafSupport.class.
                getCanonicalName()).asType();
        this.types = types;
    }

    List<JavaFile> buildJavaCode(final RoutesDeclarations routes)
    {
        final List<TypeElement> controllers = routes.controllers();
        return ImmutableList.copyOf(generatedImplsFor(controllers, routes.paths, routes.date));
    }

    List<JavaFile> generatedImplsFor(final List<TypeElement> controllers, final String paths, final String date)
    {
        final ArrayList<JavaFile> generatedImpls = new ArrayList<>(controllers.size());
        for (final TypeElement controllerClass : controllers) {
            if (shouldGenerateImplFor(controllerClass)) {
                generatedImpls.add(generateImplFor(controllerClass, paths, date));
            }
        }
        generatedImpls.trimToSize();
        return generatedImpls;
    }

    boolean shouldGenerateImplFor(final TypeElement controllerClass)
    {
        final Set<Modifier> modifiers = controllerClass.getModifiers();
        return !modifiers.contains(Modifier.ABSTRACT) && !modifiers.contains(Modifier.FINAL);
    }

    JavaFile generateImplFor(final TypeElement controllerClass, final String paths, final String date)
    {
        final String controllerQualifiedName = controllerClass.getQualifiedName().toString();
        final ClassName controllerClassName = ClassName.bestGuess(controllerQualifiedName + "_Impl");
        final TypeSpec.Builder controllerImpl = TypeSpec.classBuilder(controllerClassName)
                .superclass(ClassName.bestGuess(controllerQualifiedName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", "AE/web-processor")
                        .addMember("comments", "$S", paths)
                        .addMember("date", "$S", date)
                        .build());
        controllerImpl.addField(FieldSpec.
                builder(Logger.class, "LOG", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("Logger.getLogger($S)", controllerQualifiedName).build());
        controllerImpl.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(HttpServletRequest.class, "request", Modifier.FINAL).build()).
                        addParameter(ParameterSpec.builder(HttpServletResponse.class, "response", Modifier.FINAL).
                                build())
                        .addCode("setRequest(request);\n")
                        .addCode("setResponse(response);\n")
                        .build());
        if (shouldGenerateConstructorWithThymeleafsupportFor(controllerClass)) {
            controllerImpl.addMethod(
                    MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec.builder(HttpServletRequest.class, "request", Modifier.FINAL).
                                    build())
                            .addParameter(ParameterSpec.builder(HttpServletResponse.class, "response", Modifier.FINAL).
                                    build())
                            .addParameter(ParameterSpec.builder(WebContext.class, "templateContext", Modifier.FINAL).
                                    build())
                            .addParameter(ParameterSpec.builder(TemplateEngine.class, "templateEngine", Modifier.FINAL).
                                    build())
                            .addCode("setRequest(request);\n")
                            .addCode("setResponse(response);\n")
                            .addCode("setTemplateContext(templateContext);\n")
                            .addCode("setTemplateEngine(templateEngine);\n")
                            .build());
        }
        controllerImpl.addMethod(
                MethodSpec.methodBuilder("log")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(Logger.class)
                        .addCode("return LOG;\n")
                        .build());
        return JavaFile.builder(controllerClassName.packageName(), controllerImpl.build()).skipJavaLangImports(true).
                build();
    }

    boolean shouldGenerateConstructorWithThymeleafsupportFor(final TypeElement controllerClass)
    {
        return types.isSubtype(controllerClass.asType(), controllerWithThymeleafSupportClass);
    }
}
