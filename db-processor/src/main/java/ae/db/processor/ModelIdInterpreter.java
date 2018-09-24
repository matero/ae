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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import ae.Record;

class ModelIdInterpreter extends ModelAttributeInterpreter {

        ModelIdInterpreter(final ProcessingEnvironment environment)
        {
                super(environment);
        }

        boolean isModelIdDefinedAt(final VariableElement variable)
        {
                return variable.getAnnotation(Record.id.class) != null;
        }

        MetaModelId read(final VariableElement variable)
        {
                final TypeName typeCanonicalName = typeNameOf(variable);
                checkModifiersOf(variable, Record.id.class);
                checkAnnotationsOf(variable);
                if (ClassName.LONG.equals(typeCanonicalName)) {
                        return new MetaId(nameOf(variable), descriptionOf(variable), isRequired(variable), modifiersOf(
                                          variable),
                                          constraintsOf(variable));
                } else {
                        return new MetaName(nameOf(variable), descriptionOf(variable), modifiersOf(variable),
                                            constraintsOf(variable));
                }
        }

        void checkAnnotationsOf(final VariableElement variable) throws ModelException
        {
                if (variable.getAnnotation(Record.property.class) != null) {
                        throw new ModelException(variable,
                                                 "fields can be annotated with @Id or with @Property, but not both.");
                }
                if (variable.getAnnotation(Record.indexed.class) != null) {
                        throw new ModelException(variable,
                                                 "fields can be annotated with @Id or with @IndexedBooleanList, but not both.");
                }
                if (variable.getAnnotation(Record.parent.class) != null) {
                        throw new ModelException(variable,
                                                 "fields can be annotated with @Id or with @Parent, but not both.");
                }
        }

        private static final ClassName STRING = ClassName.get(String.class);

        @Override
        TypeName typeNameOf(final VariableElement variable) throws ModelException
        {
                final TypeName type = super.typeNameOf(variable);
                if (!ClassName.LONG.equals(type) && !STRING.equals(type)) {
                        throw new ModelException(variable, "only 'long' and 'String' can be used as @Id types");
                }
                return type;
        }
}
