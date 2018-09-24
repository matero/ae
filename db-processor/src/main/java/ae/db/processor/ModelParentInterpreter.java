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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import ae.Record;
import ae.model;

class ModelParentInterpreter extends ModelAttributeInterpreter {

        ModelParentInterpreter(final ProcessingEnvironment environment)
        {
                super(environment);
        }

        boolean isModelParentDefinedAt(final VariableElement variable)
        {
                return variable.getAnnotation(Record.parent.class) != null;
        }

        MetaParent read(final VariableElement variable)
        {
                final TypeMirror parentType = variable.asType();
                if (parentType.getKind() != TypeKind.DECLARED) {
                        throw new ModelException(variable, "only classes can be used as @Parent types");
                }
                if (types.asElement(parentType).getAnnotation(model.class) == null) {
                        throw new ModelException(variable,
                                                 "only classes annotated with '@ae.Model' can be used as @Parent types");
                }

                checkModifiersOf(variable, Record.parent.class);
                checkAnnotationsOf(variable);
                return new MetaParent(typeNameOf(variable),
                                      nameOf(variable),
                                      descriptionOf(variable),
                                      isRequired(variable),
                                      modifiersOf(variable),
                                      constraintsOf(variable));
        }

        void checkAnnotationsOf(final VariableElement variable) throws ModelException
        {
                if (variable.getAnnotation(Record.property.class) != null) {
                        throw new ModelException(variable,
                                                 "fields can be annotated with @Parent or with @Property, but not both.");
                }
                if (variable.getAnnotation(Record.indexed.class) != null) {
                        throw new ModelException(variable,
                                                 "fields can be annotated with @Parent or with @IndexedBooleanList, but not both.");
                }
                if (variable.getAnnotation(Record.id.class) != null) {
                        throw new ModelException(variable,
                                                 "fields can be annotated with @Id or with @Parent, but not both.");
                }
        }
}
