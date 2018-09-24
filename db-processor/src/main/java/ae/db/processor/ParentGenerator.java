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
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedList;

final class ParentGenerator extends AttributeGenerator {

        static final ClassName PARENT = ClassName.get("", "Parent");

        private final MetaParent parent;

        private ParentGenerator(final MetaParent modelParent, final ClassName modelClass)
        {
                super(modelClass);
                parent = modelParent;
        }

        @Override
        void buildAt(final TypeSpec.Builder modelSpec)
        {
                modelSpec.addField(FieldSpec.builder(ParameterizedTypeName.get(PARENT, parent.type), parent.name,
                                                     attributeModifiers(parent))
                        .initializer(parentInitializerFormat(), parentInitializerArgs())
                        .build()
                );
        }

        protected String parentInitializerFormat()
        {
                return "new $T($L.m, canonicalName($S), description($S), fieldName($S), $L, jsonName($S), jsonPath($S), "
                        + getConstraints(parent) + ')';
        }

        private Object[] parentInitializerArgs()
        {
                final LinkedList<Object> args = new LinkedList<>();
                args.add(PARENT);
                {
                        final ClassName parentClass = (ClassName) parent.type;
                        args.add(parentClass.simpleName());
                }
                args.add(canonicalName());
                args.add(parent.description);
                args.add(parent.name);
                args.add(required(parent));
                args.add(parent.name);
                args.add(parent.name);
                args.addAll(constraintsArgs(parent));
                return args.toArray();
        }

        static ParentGenerator of(final ChildModel model)
        {
                return new ParentGenerator(model.parent, ClassName.bestGuess(model.canonicalName));
        }

        @Override
        String canonicalName()
        {
                return parent.canonicalNameAt(modelClass.toString());
        }
}
