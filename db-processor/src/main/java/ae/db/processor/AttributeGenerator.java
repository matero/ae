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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.Modifier;

abstract class AttributeGenerator {

        static final Joiner JOINER = Joiner.on('.');

        final ClassName modelClass;

        AttributeGenerator(final ClassName modelClassName)
        {
                modelClass = modelClassName;
        }

        Modifier[] attributeModifiers(final MetaModelAttribute attr)
        {
                return attr.modifiers.toArray(new Modifier[0]);
        }

        abstract void buildAt(TypeSpec.Builder modelSpec);

        abstract String canonicalName();

        static String simpleClassName(final ClassName fieldClassName)
        {
                return JOINER.join(fieldClassName.simpleNames());
        }

        String required(final MetaModelAttribute attr)
        {
                if (attr.required) {
                        return "required";
                } else {
                        return "nullable";
                }
        }

        String getConstraints(final MetaModelAttribute attr)
        {
                if (attr.hasConstraints()) {
                        return "constraints($L)";
                } else {
                        return "noConstraints";
                }
        }

        List<Object> constraintsArgs(final MetaModelAttribute attr)
        {
                if (attr.hasConstraints()) {
                        final LinkedList<Object> args = new LinkedList<>();
                        for (final MetaConstraint constraint : attr.constraints) {
                                args.add(constraint);
                                for (final Object arg : constraint.args) {
                                        args.add(arg);
                                }
                        }
                        return args;
                } else {
                        return ImmutableList.of();
                }
        }
}
