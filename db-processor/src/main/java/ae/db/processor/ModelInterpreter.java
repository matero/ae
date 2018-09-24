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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ae.Record;
import ae.model;

class ModelInterpreter {

        private final Types types;
        private final ModelIdInterpreter idInterpreter;
        private final ModelParentInterpreter parentInterpreter;
        private final ModelFieldInterpreter fieldInterpreter;
        private final TypeMirror recordClass;
        private final TypeMirror datastoreServiceClass;

        ModelInterpreter(final ProcessingEnvironment environment)
        {
                this(environment.getTypeUtils(),
                     environment.getElementUtils(),
                     new ModelIdInterpreter(environment),
                     new ModelParentInterpreter(environment),
                     new ModelFieldInterpreter(environment));
        }

        ModelInterpreter(final Types types,
                         final Elements elements,
                         final ModelIdInterpreter idInterpreter,
                         final ModelParentInterpreter parentInterpreter,
                         final ModelFieldInterpreter fieldInterpreter)
        {
                this.types = types;
                this.idInterpreter = idInterpreter;
                this.parentInterpreter = parentInterpreter;
                this.fieldInterpreter = fieldInterpreter;

                this.recordClass = elements.getTypeElement(Record.class.getCanonicalName()).asType();
                this.datastoreServiceClass = elements.getTypeElement(DatastoreService.class.getCanonicalName()).asType();
        }

        MetaModel read(final Element modelElement)
        {
                final TypeElement modelClass = modelClass(modelElement);
                final PackageElement modelPackage = modelPackage(modelClass);

                final TypeElement record = recordAt(modelClass);
                final List<VariableElement> attrs = getAttributes(record);

                return MetaModel.withSpec(modelPackage.getQualifiedName().toString(),
                                          modelClass.getSimpleName().toString(),
                                          modelClass.getQualifiedName().toString(),
                                          modelKind(modelClass),
                                          baseClass(modelClass),
                                          getModelId(record, attrs),
                                          getModelParent(attrs),
                                          getModelFields(attrs),
                                          modelClass.getModifiers(),
                                          modelConfiguresDatastore(modelClass));
        }

        TypeElement modelClass(final Element modelElement) throws IllegalArgumentException
        {
                if (modelElement.getKind() != ElementKind.CLASS) {
                        throw new ModelException(modelElement, "only classes can be annotated as @ae.Model");
                }
                return (TypeElement) modelElement;
        }

        PackageElement modelPackage(final TypeElement annotatedClass) throws IllegalArgumentException
        {
                final Element enclosingElement = annotatedClass.getEnclosingElement();
                if (enclosingElement.getKind() != ElementKind.PACKAGE) {
                        throw new IllegalArgumentException("only ROOT classes can be annotated as @ae.Model");
                }
                return (PackageElement) enclosingElement;
        }

        model modelAnnotation(final TypeElement modelClass)
        {
                final model modelAnnotation = modelClass.getAnnotation(model.class);
                if (modelAnnotation == null) {
                        throw new ModelException(modelClass, "isn't annotated as @db.Model");
                }
                return modelAnnotation;
        }

        String modelKind(final TypeElement modelClass)
        {
                final model model = modelAnnotation(modelClass);
                final String declaredKind = model.kind();
                if ("".equals(declaredKind)) {
                        return modelClass.getSimpleName().toString();
                } else {
                        return declaredKind;
                }
        }

        String baseClass(final TypeElement modelClass)
        {
                final TypeMirror superclass = modelClass.getSuperclass();
                final TypeElement baseClass = (TypeElement) types.asElement(superclass);

                if (Object.class.getCanonicalName().equals(baseClass.getQualifiedName().toString())) {
                        throw new ModelException(modelClass, "No base class defined!");
                }

                return baseClass.getSimpleName().toString();
        }

        List<VariableElement> getAttributes(final TypeElement record)
        {
                return ElementFilter.fieldsIn(record.getEnclosedElements());
        }

        TypeElement recordAt(final TypeElement modelClass)
        {
                TypeElement record = null;
                for (final TypeElement type : innerClassesOf(modelClass)) {
                        if (definesRecord(type)) {
                                if (record != null) {
                                        throw new ModelRecordAlreadyDefined(type);
                                }
                                record = type;
                        }
                }
                if (record == null) {
                        throw new ModelRecordNotDefined(modelClass);
                }
                return record;
        }

        MetaModelId getModelId(final TypeElement record, final List<VariableElement> modelAttributes)
        {
                MetaModelId id = null;

                for (final Iterator<VariableElement> iAttr = modelAttributes.iterator(); iAttr.hasNext();) {
                        final VariableElement attr = iAttr.next();
                        if (idInterpreter.isModelIdDefinedAt(attr)) {
                                if (id != null) {
                                        throw new ModelIdAlreadyDefined(attr);
                                }
                                id = idInterpreter.read(attr);
                                iAttr.remove();
                        }
                }
                if (id == null) {
                        throw new ModelIdNotDefined(record);
                }
                return id;
        }

        MetaParent getModelParent(final List<VariableElement> modelAttributes)
        {
                MetaParent parent = null;
                for (final Iterator<VariableElement> iAttr = modelAttributes.iterator(); iAttr.hasNext();) {
                        final VariableElement attr = iAttr.next();
                        if (parentInterpreter.isModelParentDefinedAt(attr)) {
                                if (parent != null) {
                                        throw new ModelParentAlreadyDefined(attr);
                                }
                                parent = parentInterpreter.read(attr);
                                iAttr.remove();
                        }
                }
                return parent;
        }

        MetaModel.Fields getModelFields(final List<VariableElement> modelAttributes)
        {
                final ImmutableList.Builder<MetaField> fieldsDeclarations = ImmutableList.builder();
                for (final VariableElement attr : modelAttributes) {
                        fieldsDeclarations.add(this.fieldInterpreter.read(attr));
                }
                return MetaModel.fields(fieldsDeclarations.build());
        }

        boolean definesRecord(final TypeElement type)
        {
                if (type.getSuperclass() == null) {
                        return false;
                }
                return types.isAssignable(type.asType(), recordClass);
        }

        Iterable<TypeElement> innerClassesOf(final TypeElement modelClass)
        {
                return ElementFilter.typesIn(modelClass.getEnclosedElements());
        }

        boolean modelConfiguresDatastore(TypeElement model)
        {
                for (final ExecutableElement constructor : ElementFilter.constructorsIn(model.getEnclosedElements())) {
                        if (constructor.getAnnotation(Inject.class) != null) {
                                for (final VariableElement parameter : constructor.getParameters()) {
                                        if (types.isAssignable(parameter.asType(), datastoreServiceClass)) {
                                                return true;
                                        }
                                }
                        }
                }
                return false;
        }
}
