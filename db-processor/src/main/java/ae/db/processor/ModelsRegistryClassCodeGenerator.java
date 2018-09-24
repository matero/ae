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
package ae.db.processor;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;

import com.squareup.javapoet.TypeSpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

class ModelsRegistryClassCodeGenerator implements CodeGenerator {

        @Override
        public ImmutableList<JavaFile> generateCode(final MetaModels models, final Date date)
        {
                final ImmutableList.Builder<JavaFile> generatedJavaFiles = ImmutableList.builder();
                for (final String packageName : models.byPackage.keySet()) {
                        generatedJavaFiles.add(modelsRegistryJavaFile(models, packageName, date));
                }
                return generatedJavaFiles.build();
        }

        JavaFile modelsRegistryJavaFile(final MetaModels models, String packageName, final Date date)
        {
                return new ModelsRegistryJavaClassBuilder(models, packageName, date).build();
        }
}

final class ModelsRegistryJavaClassBuilder {

        private static final ClassName LOGGER_CLASS = ClassName.get(Logger.class);

        final MetaModels models;
        final ClassName registryClass;
        final TypeSpec.Builder classBuilder;
        final Date date;

        ModelsRegistryJavaClassBuilder(final MetaModels models, final String packageName, final Date date)
        {
                this.models = models;
                this.date = date;
                registryClass = ClassName.get(packageName, "m");
                classBuilder = TypeSpec.classBuilder(registryClass).addModifiers(Modifier.FINAL);
                final ImmutableList<MetaModel> pkgModels = models.byPackage.get(packageName);
                for (final MetaModel model : pkgModels) {
                        if (model.modifiers.contains(Modifier.PUBLIC)) {
                                classBuilder.addModifiers(Modifier.PUBLIC);
                                break;
                        }
                }
        }

        JavaFile build()
        {
                return JavaFile.builder(registryClass.packageName(), modelsRegistryClass())
                        .skipJavaLangImports(true)
                        .build();
        }

        private TypeSpec modelsRegistryClass()
        {
                defineGenerated();
                defineConstructor();
                defineFields();
                return classBuilder.build();
        }

        void defineGenerated()
        {
                classBuilder.addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", "ae-db")
                        .addMember("date", "$S", new SimpleDateFormat("yyyy-MM-dd").format(date))
                        .build());
        }

        void defineConstructor()
        {
                MethodSpec.Builder ctor = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("new $T(\"$T can't be instantiated.\")", UnsupportedOperationException.class,
                                      registryClass);
                classBuilder.addMethod(ctor.build());
        }

        void defineFields()
        {
                for (final MetaModel model : models.byPackage.get(registryClass.packageName())) {
                        final ClassName modelClass = ClassName.get(model.packageName, model.name);
                        classBuilder.addField(
                                FieldSpec.builder(modelClass, model.name, modifiersOf(model))
                                        .addModifiers(Modifier.STATIC, Modifier.FINAL)
                                        .initializer("new $T()", modelClass)
                                        .build());
                }
        }

        private static Modifier[] modifiersOf(final MetaModel model)
        {
                return model.modifiers.toArray(new Modifier[0]);
        }
}
