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

import ae.db.Attr;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import com.google.appengine.api.datastore.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import ae.db.ChildWithId;
import ae.db.ChildWithName;
import ae.db.Field;
import ae.db.RootWithId;
import ae.db.RootWithName;
import ae.db.Validation;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.WildcardTypeName;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ModelBaseClassCodeGenerator implements CodeGenerator {

        @Override
        public ImmutableList<JavaFile> generateCode(final MetaModels models, final Date date)
        {
                final ImmutableList.Builder<JavaFile> generatedJavaFiles = ImmutableList.builder();
                for (final MetaModel model : models.all()) {
                        generatedJavaFiles.add(baseModelJavaFile(model, date).build());
                }
                return generatedJavaFiles.build();
        }

        BaseModelJavaClassBuilder<?> baseModelJavaFile(final MetaModel model, final Date date)
        {
                if (model instanceof RootModel) {
                        return new RootBaseModelJavaClassBuilder((RootModel) model, date);
                } else {
                        return new ChildBaseModelJavaClassBuilder((ChildModel) model, date);
                }
        }
}

abstract class BaseModelJavaClassBuilder<M extends MetaModel> {

        private static final ClassName LOGGER_CLASS = ClassName.get(Logger.class);
        private static final ClassName LOGGER_FACTORY_CLASS = ClassName.get(LoggerFactory.class);
        final M model;
        final TypeSpec.Builder baseModelClass;
        final ClassName modelClass;
        final Date date;

        BaseModelJavaClassBuilder(final M model, final Date date)
        {
                this.model = model;
                this.date = date;
                baseModelClass = TypeSpec.classBuilder(model.baseClass).addModifiers(Modifier.ABSTRACT);
                modelClass = ClassName.get(model.packageName, model.name);
        }

        JavaFile build()
        {
                return JavaFile.builder(model.packageName, baseModel())
                        .addStaticImport(ClassName.get(ae.db.DSL.class), "*")
                        .skipJavaLangImports(true)
                        .build();
        }

        private TypeSpec baseModel()
        {
                defineGenerated();
                defineSuperClass();

                defineLogger();
                defineM();

                defineIdField();
                defineParentField();
                defineFields();

                defineMetadataInfo();

                defineConstructor();

                defineOverridenMethods();
                defineIdReaders();
                defineParentReaders();
                definePropertyAccessors();

                if (shouldDefineBuilder()) {
                        defineEntityBuilder();
                }
                if (shouldDefineWrapper()) {
                        defineEntityWrapper();
                }
                return baseModelClass.build();
        }

        void defineGenerated()
        {
                baseModelClass.addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", "ae-db")
                        .addMember("date", "$S", new SimpleDateFormat("yyyy-MM-dd").format(date))
                        .build());
        }

        Builder defineSuperClass()
        {
                return baseModelClass.superclass(superClass());
        }

        TypeName superClass()
        {
                return model.useId() ? superClassWithId() : superClassWithName();
        }

        abstract TypeName superClassWithId();

        abstract TypeName superClassWithName();

        void defineLogger()
        {
                baseModelClass.addField(
                        FieldSpec.builder(LOGGER_CLASS, "LOGGER", Modifiers.PROTECTED_STATIC_FINAL)
                                .initializer("$T.getLogger($S)", LOGGER_FACTORY_CLASS, model.canonicalName)
                                .build()
                );
        }

        void defineM()
        {
                final FieldSpec.Builder m = FieldSpec.builder(this.modelClass, "m", Modifiers.STATIC_FINAL);
                if (model.modifiers.contains(Modifier.PUBLIC)) {
                        m.addModifiers(Modifier.PUBLIC);
                }
                baseModelClass.addField(m.initializer("new $T()", this.modelClass).build());
        }

        void defineIdField()
        {
                IdGenerator.of(model).buildAt(baseModelClass);
        }

        abstract void defineParentField();

        void defineFields()
        {
                for (final MetaField metaField : model.fields) {
                        getFieldGenerator(metaField).buildAt(baseModelClass);
                }
        }

        FieldGenerator getFieldGenerator(final MetaField field)
        {
                return new FieldGenerator(field, modelClass);
        }

        static final ParameterizedTypeName META_FIELDS_TYPE = ParameterizedTypeName.get(
                ClassName.get(ImmutableList.class),
                ParameterizedTypeName.get(ClassName.get(Field.class), WildcardTypeName.subtypeOf(Object.class)));
        static final ParameterizedTypeName META_ATTRS_TYPE = ParameterizedTypeName.get(
                ClassName.get(ImmutableList.class), ClassName.get(Attr.class));

        void defineMetadataInfo()
        {
                baseModelClass.addField(FieldSpec.builder(META_ATTRS_TYPE, "_attrs", Modifiers.PRIVATE_FINAL)
                        .initializer(initImmutableList(model.attributesNames()))
                        .build()
                );
                baseModelClass.addField(FieldSpec.builder(META_FIELDS_TYPE, "_fields", Modifiers.PRIVATE_FINAL)
                        .initializer(initImmutableList(model.fieldsNames()))
                        .build()
                );
        }

        void defineConstructor()
        {
                MethodSpec.Builder ctor = MethodSpec.constructorBuilder().addStatement("super($S)", model.kind);
                baseModelClass.addMethod(ctor.build());
        }

        void defineOverridenMethods()
        {
                baseModelClass.addMethod(logger());
                baseModelClass.addMethod(modelIdentifier());
                baseModelClass.addMethod(modelFields());
                baseModelClass.addMethod(modelAttributes());
                baseModelClass.addMethod(toJson());
                baseModelClass.addMethod(updatePropertiesWithJsonContents());
                baseModelClass.addMethod(doValidate());
        }

        MethodSpec logger()
        {
                return methodBuilder("logger")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifiers.PROTECTED_FINAL)
                        .returns(Logger.class)
                        .addStatement("return LOGGER")
                        .build();
        }

        MethodSpec modelIdentifier()
        {
                return methodBuilder(modelIdReader())
                        .addAnnotation(Override.class)
                        .addModifiers(Modifiers.PUBLIC_FINAL)
                        .returns(IdGenerator.getIdClassName(model))
                        .addStatement("return $L", model.id.name)
                        .build();
        }

        MethodSpec modelFields()
        {
                return methodBuilder("modelFields")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifiers.PUBLIC_FINAL)
                        .returns(META_FIELDS_TYPE)
                        .addStatement("return _fields")
                        .build();
        }

        MethodSpec modelAttributes()
        {
                return methodBuilder("modelAttributes")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifiers.PUBLIC_FINAL)
                        .returns(META_ATTRS_TYPE)
                        .addStatement("return _attrs")
                        .build();
        }

        MethodSpec toJson()
        {
                final StringBuilder fields = new StringBuilder();
                fields.append(model.id.name).append(".makeJsonFieldFrom(data)");
                for (final MetaField field : model.fields) {
                        if (!field.jsonIgnore) {
                                fields.append(',').append(field.name).append(".makeJsonFieldFrom(data)");
                        }
                }

                return methodBuilder("toJson")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(JsonNode.class))
                        .addParameter(TypeName.get(Entity.class), "data", Modifier.FINAL)
                        .beginControlFlow("if (null == data)")
                        .addStatement("return $T.nullNode()", ClassName.get(JsonNodeFactories.class))
                        .endControlFlow()
                        .addStatement("return $T.object($T.of($L))",
                                      ClassName.get(JsonNodeFactories.class),
                                      ClassName.get(ImmutableList.class),
                                      fields.toString())
                        .build();
        }

        MethodSpec updatePropertiesWithJsonContents()
        {
                MethodSpec.Builder method = methodBuilder("updatePropertiesWithJsonContents")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addParameter(Entity.class, "data", Modifier.FINAL)
                        .addParameter(JsonNode.class, "json", Modifier.FINAL);
                for (final MetaField field : model.fields) {
                        if (!field.jsonIgnore) {
                                method.addStatement("$L.write(data, json)", field.name);
                        }
                }
                return method.build();
        }

        MethodSpec doValidate()
        {
                MethodSpec.Builder method = methodBuilder("doValidate")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                        .addParameter(Entity.class, "data", Modifier.FINAL)
                        .addParameter(Validation.class, "validation", Modifier.FINAL);

                if (model.id.shouldValidate()) {
                        method.addStatement("$L.validate(data, validation)", model.id.name);
                }
                try {
                        final MetaParent parent = model.parent();
                        if (parent.shouldValidate()) {
                                method.addStatement("$L.validate(data, validation)", parent.name);
                        }
                } catch (final UnsupportedOperationException e) {
                        // ok, its a root model, no parent :D
                }
                for (final MetaField field : model.fields) {
                        if (field.shouldValidate()) {
                                method.addStatement("$L.validate(data, validation)", field.name);
                        }
                }
                return method.build();
        }

        String modelIdReader()
        {
                return "modelIdentifier";
        }

        void defineIdReaders()
        {
                baseModelClass.addMethod(entityIdReader());
                baseModelClass.addMethod(keyIdReader());
        }

        MethodSpec entityIdReader()
        {
                final Class<?> idType = model.useId() ? long.class : String.class;

                return MethodSpec.methodBuilder(model.id.name)
                        .addModifiers(model.id.modifiers)
                        .returns(idType)
                        .addParameter(ENTITY_TYPE, "data", Modifier.FINAL)
                        .addStatement("return $L.read(data)", model.id.name)
                        .build();
        }

        static final ClassName ENTITY_TYPE = ClassName.get(Entity.class);

        MethodSpec keyIdReader()
        {
                final Class<?> idType = model.useId() ? long.class : String.class;

                return methodBuilder(model.id.name)
                        .addModifiers(model.id.modifiers)
                        .returns(idType)
                        .addParameter(ClassName.get(Key.class), "key", Modifier.FINAL)
                        .addStatement("return $L.read(key)", model.id.name)
                        .build();
        }

        abstract void defineParentReaders();

        void definePropertyAccessors()
        {
                if (shouldDefineAccessors()) {
                        for (final MetaField field : model.fields) {
                                baseModelClass.addMethod(methodBuilder(field.name)
                                        .addModifiers(field.modifiers)
                                        .returns(field.type)
                                        .addParameter(ENTITY_TYPE, "data", Modifier.FINAL)
                                        .addStatement("return $L.read(data)", field.name)
                                        .build()
                                );

                                baseModelClass.addMethod(methodBuilder(field.name)
                                        .addModifiers(field.modifiers)
                                        .returns(TypeName.VOID)
                                        .addParameter(ENTITY_TYPE, "data", Modifier.FINAL)
                                        .addParameter(field.type, "newValue", Modifier.FINAL)
                                        .addStatement("$L.write(data, newValue)", field.name)
                                        .build()
                                );
                        }
                }
        }

        boolean shouldDefineAccessors()
        {
                return model.hasFields();
        }

        void defineEntityBuilder()
        {
                final ClassName builderClassName = modelBuilderClassName();
                baseModelClass.addType(modelBuilderClass(builderClassName));
                baseModelClass.addMethods(modelBuilderInstantiators(builderClassName));
        }

        boolean shouldDefineBuilder()
        {
                return model.hasFields();
        }

        ClassName modelBuilderClassName()
        {
                return ClassName.get(model.packageName, model.name, "Builder");
        }

        TypeSpec modelBuilderClass(final ClassName builderClassName)
        {
                final Modifier[] modifiers = model.isPublic() ? Modifiers.PUBLIC_FINAL : Modifiers.FINAL;
                final TypeSpec.Builder builderClass = TypeSpec.classBuilder("Builder")
                        .addModifiers(modifiers)
                        .addField(Entity.class, "entity", Modifier.FINAL)
                        .addMethod(
                                constructorBuilder()
                                        .addParameter(Entity.class, "entity", Modifier.FINAL)
                                        .addStatement("this.entity = entity")
                                        .build()
                        )
                        .addMethod(
                                methodBuilder("build")
                                        .returns(Entity.class)
                                        .addStatement("return entity")
                                        .build()
                        );
                for (final MetaField field : model.fields) {
                        builderClass.addMethod(
                                methodBuilder(field.name)
                                        .addModifiers(field.modifiers)
                                        .returns(builderClassName)
                                        .addParameter(field.type, "value", Modifier.FINAL)
                                        .addStatement("$L.write(this.entity, value)", field.name)
                                        .addStatement("return this")
                                        .build()
                        );
                }
                return builderClass.build();
        }

        abstract Iterable<MethodSpec> modelBuilderInstantiators(ClassName builderClassName);

        void defineEntityWrapper()
        {
                final ClassName builderClassName = modelWrapperClassName();
                baseModelClass.addType(modelWrapperClass(builderClassName));
                baseModelClass.addMethods(modelWrapperInstantiators(builderClassName));
        }

        boolean shouldDefineWrapper()
        {
                return model.hasFields();
        }

        ClassName modelWrapperClassName()
        {
                return ClassName.get(model.packageName, model.name, "Wrapper");
        }

        TypeSpec modelWrapperClass(final ClassName wrapperClassName)
        {
                final Modifier[] modifiers = model.isPublic() ? Modifiers.PUBLIC_FINAL : Modifiers.FINAL;
                final TypeSpec.Builder builderClass = TypeSpec.classBuilder("Wrapper")
                        .addModifiers(modifiers).addField(Entity.class, "entity", Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(
                                constructorBuilder()
                                        .addParameter(Entity.class, "entity", Modifier.FINAL)
                                        .addStatement("this.entity = entity")
                                        .build()
                        );
                for (final MetaField field : model.fields) {
                        builderClass.addMethod(methodBuilder(field.name)
                                .addModifiers(field.modifiers)
                                .returns(field.type)
                                .addStatement("return $L.read(this.entity)", field.name)
                                .build()
                        );
                        builderClass.addMethod(
                                methodBuilder(field.name)
                                        .addModifiers(field.modifiers)
                                        .returns(wrapperClassName)
                                        .addParameter(field.type, "value", Modifier.FINAL)
                                        .addStatement("$L.write(this.entity, value)", field.name)
                                        .addStatement("return this")
                                        .build()
                        );
                }
                return builderClass.build();
        }

        Iterable<MethodSpec> modelWrapperInstantiators(final ClassName wrapperClassName)
        {
                return ImmutableList.of(
                        methodBuilder("wrap")
                                .addModifiers(model.modifiers)
                                .returns(wrapperClassName)
                                .addParameter(Entity.class, "data", Modifier.FINAL)
                                .addStatement("return new Wrapper(data)")
                                .build()
                );
        }

        private CodeBlock initImmutableList(final List<String> values)
        {
                if (values.isEmpty()) {
                        return CodeBlock.of("$T.of()", ImmutableList.class);
                } else {
                        return CodeBlock.of("$T.of($L)", ImmutableList.class, String.join(", ", values));
                }
        }
}

final class RootBaseModelJavaClassBuilder extends BaseModelJavaClassBuilder<RootModel> {

        private static final ClassName MODEL_WITH_NAME = ClassName.get(RootWithName.class);
        private static final ClassName MODEL_WITH_ID = ClassName.get(RootWithId.class);

        RootBaseModelJavaClassBuilder(final RootModel model, final Date date)
        {
                super(model, date);
        }

        @Override
        TypeName superClassWithId()
        {
                return MODEL_WITH_ID;
        }

        @Override
        TypeName superClassWithName()
        {
                return MODEL_WITH_NAME;
        }

        @Override
        void defineParentField()
        {
                //nothing to do, its a Root entity -> it has no parent
        }

        @Override
        void defineParentReaders()
        {
                //nothing to do, its a Root entity -> it has no parent
        }

        @Override
        Iterable<MethodSpec> modelBuilderInstantiators(final ClassName builderClassName)
        {
                if (model.useId()) {
                        return ImmutableList.of(
                                methodBuilder("with")
                                        .addModifiers(model.id.modifiers)
                                        .returns(builderClassName)
                                        .addStatement("return new Builder(make())", model.id.name)
                                        .build(),
                                methodBuilder("with")
                                        .addModifiers(model.id.modifiers)
                                        .returns(builderClassName)
                                        .addParameter(long.class, model.id.name, Modifier.FINAL)
                                        .addStatement("return new Builder(make($L))", model.id.name)
                                        .build()
                        );
                } else {
                        return ImmutableList.of(
                                methodBuilder("with")
                                        .addModifiers(model.id.modifiers)
                                        .returns(builderClassName)
                                        .addParameter(String.class, model.id.name, Modifier.FINAL)
                                        .addStatement("return new Builder(make($L))", model.id.name)
                                        .build()
                        );
                }
        }
}

class ChildBaseModelJavaClassBuilder
        extends BaseModelJavaClassBuilder<ChildModel> {

        private static final ClassName CHILD_MODEL_WITH_NAME = ClassName.get(ChildWithName.class);
        private static final ClassName CHILD_MODEL_WITH_ID = ClassName.get(ChildWithId.class);

        ChildBaseModelJavaClassBuilder(final ChildModel model,
                                       final Date date)
        {
                super(model, date);
        }

        @Override
        TypeName superClassWithId()
        {
                return CHILD_MODEL_WITH_ID;
        }

        @Override
        TypeName superClassWithName()
        {
                return CHILD_MODEL_WITH_NAME;
        }

        @Override
        void defineParentField()
        {
                ParentGenerator.of(model).buildAt(baseModelClass);
        }

        @Override
        void defineOverridenMethods()
        {
                super.defineOverridenMethods();
                baseModelClass.addMethod(modelParent());
        }

        MethodSpec modelParent()
        {
                return methodBuilder("modelParent")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .returns(ParentGenerator.PARENT)
                        .addStatement("return $L", model.parent.name)
                        .build();
        }

        @Override
        void defineParentReaders()
        {
                baseModelClass.addMethod(entityParentReader());
                baseModelClass.addMethod(keyParentReader());
        }

        MethodSpec entityParentReader()
        {
                return methodBuilder(model.parent.name)
                        .addModifiers(model.parent.modifiers)
                        .returns(Key.class)
                        .addParameter(ENTITY_TYPE, "data", Modifier.FINAL)
                        .addStatement("return $L.read(data)", model.parent.name)
                        .build();
        }

        MethodSpec keyParentReader()
        {
                return methodBuilder(model.parent.name)
                        .addModifiers(model.parent.modifiers)
                        .returns(Key.class)
                        .addParameter(ClassName.get(Key.class), "key", Modifier.FINAL)
                        .addStatement("return $L.read(key)", model.parent.name)
                        .build();
        }

        @Override
        Iterable<MethodSpec> modelBuilderInstantiators(final ClassName builderClassName)
        {
                if (model.useId()) {
                        final ImmutableSet<Modifier> modifiers = model.id.modifiers;
                        return ImmutableList.of(methodBuilder("with")
                                .addModifiers(modifiers)
                                .returns(builderClassName)
                                .addParameter(Entity.class, "parent", Modifier.FINAL)
                                .addStatement("return new Builder(make(parent))")
                                .build(),
                                                methodBuilder("with")
                                                        .addModifiers(model.id.modifiers)
                                                        .returns(builderClassName)
                                                        .addParameter(Key.class, "parentKey", Modifier.FINAL)
                                                        .addStatement("return new Builder(make(parentKey))")
                                                        .build(),
                                                methodBuilder("with")
                                                        .addModifiers(model.id.modifiers)
                                                        .returns(builderClassName)
                                                        .addParameter(Entity.class, "parent", Modifier.FINAL)
                                                        .addParameter(long.class, model.id.name, Modifier.FINAL)
                                                        .addStatement("return new Builder(make(parent, $L))",
                                                                      model.id.name)
                                                        .build(),
                                                methodBuilder("with")
                                                        .addModifiers(model.id.modifiers)
                                                        .returns(builderClassName)
                                                        .addParameter(Key.class, "parentKey", Modifier.FINAL)
                                                        .addParameter(long.class, model.id.name, Modifier.FINAL)
                                                        .addStatement("return new Builder(make(parentKey, $L))",
                                                                      model.id.name)
                                                        .build()
                        );
                } else {
                        return ImmutableList.of(
                                methodBuilder("with")
                                        .addModifiers(model.id.modifiers)
                                        .returns(builderClassName)
                                        .addParameter(Entity.class, "parent", Modifier.FINAL)
                                        .addParameter(String.class, model.id.name, Modifier.FINAL)
                                        .addStatement("return new Builder(make(parent, $L))", model.id.name)
                                        .build(),
                                methodBuilder("with")
                                        .addModifiers(model.id.modifiers)
                                        .returns(builderClassName)
                                        .addParameter(Key.class, "parentKey", Modifier.FINAL)
                                        .addParameter(String.class, model.id.name, Modifier.FINAL)
                                        .addStatement("return new Builder(make(parentKey, $L))", model.id.name)
                                        .build()
                        );
                }
        }
}
