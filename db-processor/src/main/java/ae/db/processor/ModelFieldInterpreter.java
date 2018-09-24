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

import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import ae.Record;
import ae.model;

class ModelFieldInterpreter extends ModelAttributeInterpreter {

        private static final ImmutableSet<TypeName> MAPPED_TYPES = ImmutableSet.of(
                FieldType.BIG_DECIMAL,
                FieldType.BLOB,
                FieldType.BLOB_KEY,
                FieldType.BOOLEAN,
                FieldType.CATEGORY,
                FieldType.DATE,
                FieldType.DOUBLE,
                FieldType.EMAIL,
                FieldType.EMBEDDED_ENTITY,
                FieldType.GEO_PT,
                FieldType.IM_HANDLE,
                FieldType.KEY,
                FieldType.LINK,
                FieldType.LIST, //FieldType.bag
                FieldType.LONG,
                FieldType.PHONE_NUMBER,
                FieldType.POSTAL_ADDRESS,
                FieldType.RATING,
                FieldType.SHORT_BLOB,
                FieldType.STRING,
                FieldType.TEXT,
                FieldType.USER,
                FieldType.BIG_DECIMAL_LIST,
                FieldType.BLOB_LIST,
                FieldType.BLOB_KEY_LIST,
                FieldType.BOOLEAN_LIST,
                FieldType.CATEGORY_LIST,
                FieldType.DATE_LIST,
                FieldType.DOUBLE_LIST,
                FieldType.EMAIL_LIST,
                FieldType.EMBEDDED_ENTITY_LIST,
                FieldType.GEO_PT_LIST,
                FieldType.IM_HANDLE_LIST,
                FieldType.KEY_LIST,
                FieldType.LINK_LIST,
                FieldType.LONG_LIST,
                FieldType.PHONE_NUMBER_LIST,
                FieldType.POSTAL_ADDRESS_LIST,
                FieldType.RATING_LIST,
                FieldType.SHORT_BLOB_LIST,
                FieldType.STRING_LIST,
                FieldType.TEXT_LIST,
                FieldType.USER_LIST);
        private final ImmutableSet<TypeName> supportedTypes;

        ModelFieldInterpreter(final ProcessingEnvironment environment)
        {
                this(environment, MAPPED_TYPES);
        }

        ModelFieldInterpreter(final ProcessingEnvironment environment,
                              final ImmutableSet<TypeName> interpreterMappedTypes)
        {
                super(environment);
                supportedTypes = interpreterMappedTypes;
        }

        MetaField read(final VariableElement variable)
        {
                final TypeName type = fieldTypeName(variable);

                checkModifiersOf(variable);
                checkAnnotationsOf(variable, type);

                return new MetaField(type,
                                     nameOf(variable),
                                     descriptionOf(variable),
                                     propertyOf(variable),
                                     isIndexed(variable),
                                     isRequired(variable),
                                     shouldIgnoreAtJsonSerialization(variable),
                                     jsonFormat(variable),
                                     modifiersOf(variable),
                                     constraintsOf(variable));
        }

        TypeName fieldTypeName(final VariableElement variable)
        {
                final TypeMirror varType = variable.asType();

                if (varType.getKind().isPrimitive()) {
                        throw new IllegalFieldType(variable, "Primitive types aren't supported.");
                }

                final TypeName type = typeNameOf(variable);
                if (!supportedTypes.contains(type)) {
                        throw new IllegalFieldType(variable,
                                                   type.toString(),
                                                   supportedTypes.stream().map(t -> t.toString()).collect(toSet()));
                }
                return type;
        }

        void checkModifiersOf(final VariableElement variable) throws ModelException
        {
                final Set<Modifier> modifiers = variable.getModifiers();
                if (modifiers.contains(Modifier.STATIC)) {
                        throw new ModelException(variable, "only member fields can be annotated as properties");
                }
                if (modifiers.contains(Modifier.TRANSIENT)) {
                        throw new ModelException(variable, "only member fields can be annotated as properties");
                }
                if (modifiers.contains(Modifier.VOLATILE)) {
                        throw new ModelException(variable, "only member fields can be annotated as properties");
                }
        }

        void checkAnnotationsOf(final VariableElement variable, final TypeName typeCanonicalName)
        {
                if (variable.getAnnotation(Record.id.class) != null) {
                        throw new ModelException(variable, "@Id fields can not be interpreted as @Property");
                }
                if (variable.getAnnotation(Record.parent.class) != null) {
                        throw new ModelException(variable, "@Parent fields can not be interpreted as @Property");
                }
                if (variable.getAnnotation(Record.indexed.class) != null) {
                        if (Objects.equals(Text.class.getCanonicalName(), typeCanonicalName)) {
                                throw new ModelException(variable, "Text fields can't be @IndexedBooleanList.");
                        }
                        if (FieldType.BLOB.equals(typeCanonicalName)) {
                                throw new ModelException(variable, "Blob fields can't be @IndexedBooleanList.");
                        }
                        if (FieldType.EMBEDDED_ENTITY.equals(typeCanonicalName)) {
                                throw new ModelException(variable, "EmbeddedEntity fields can't be @IndexedBooleanList.");
                        }
                }
        }

        String propertyOf(final VariableElement variable)
        {
                final Record.property property = variable.getAnnotation(Record.property.class);
                if (property == null || "".equals(property.value())) {
                        return variable.getSimpleName().toString();
                }
                final String propertyName = property.value();
                if ("".equals(propertyName)) {
                        return variable.getSimpleName().toString();
                } else {
                        return propertyName;
                }
        }

        boolean isReference(final Element clazz)
        {
                final model modelAnnotation = clazz.getAnnotation(model.class);
                return (modelAnnotation != null);
        }

        boolean isEnum(final Element clazz)
        {
                return clazz.getKind() == ElementKind.ENUM;
        }

        boolean isIndexed(final VariableElement variable)
        {
                return variable.getAnnotation(Record.indexed.class) != null;
        }
}
