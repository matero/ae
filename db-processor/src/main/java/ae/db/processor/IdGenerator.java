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
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedList;

final class IdGenerator extends AttributeGenerator {

        private static final ClassName ID_CLASSNAME = ClassName.get("", "Id");
        private static final ClassName REQUIRED_ID_CLASSNAME = ClassName.get("", "RequiredId");
        private static final ClassName NAME_CLASSNAME = ClassName.get("", "Name");

        private final ClassName idClass;
        private final MetaModelId id;

        private IdGenerator(final MetaModelId modelId, final ClassName modelIdClass, final ClassName modelClass)
        {
                super(modelClass);
                id = modelId;
                idClass = modelIdClass;
        }

        @Override
        void buildAt(final TypeSpec.Builder modelSpec)
        {
                final FieldSpec.Builder fieldBuilder = FieldSpec.builder(idClass, id.name, attributeModifiers(id));
                if (id.isId()) {
                        fieldBuilder.initializer(idInitializerFormat(), idInitializerArgs());
                } else {
                        fieldBuilder.initializer(nameInitializerFormat(), nameInitializerArgs());
                }

                modelSpec.addField(fieldBuilder.build());
        }

        protected String idInitializerFormat()
        {
                final String constraints = getConstraints(id);
                return "new $T(canonicalName($S), description($S), fieldName($S), jsonName($S), jsonPath($S), " + constraints + ')';
        }

        private Object[] idInitializerArgs()
        {
                final LinkedList<Object> args = new LinkedList<>();
                args.add(idClass);
                args.add(canonicalName());
                args.add(id.description);
                args.add(id.name);
                args.add(id.name);
                args.add(id.name);
                args.addAll(constraintsArgs(id));
                return args.toArray();
        }

        protected String nameInitializerFormat()
        {
                final String constraints = getConstraints(id);
                return "new $T(canonicalName($S), description($S), fieldName($S), jsonName($S), jsonPath($S), " + constraints + ')';
        }

        private Object[] nameInitializerArgs()
        {
                final LinkedList<Object> args = new LinkedList<>();
                args.add(idClass);
                args.add(canonicalName());
                args.add(id.description);
                args.add(id.name);
                args.add(id.name);
                args.add(id.name);
                args.addAll(constraintsArgs(id));
                return args.toArray();
        }

        static IdGenerator of(final MetaModel model)
        {
                return new IdGenerator(model.id, getIdClassName(model), ClassName.bestGuess(model.canonicalName));
        }

        static ClassName getIdClassName(final MetaModel model)
        {
                if (model.useId()) {
                        if (model.id.required) {
                                return REQUIRED_ID_CLASSNAME;
                        } else {
                                return ID_CLASSNAME;
                        }
                } else {
                        return NAME_CLASSNAME;
                }
        }

        @Override
        String canonicalName()
        {
                return id.canonicalNameAt(modelClass.toString());
        }
}
