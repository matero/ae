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

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Modifier;

abstract class MetaModelAttribute extends MetaData {

        final TypeName type;
        final String description;
        final boolean required;
        final ImmutableSet<MetaConstraint> constraints;

        MetaModelAttribute(final TypeName type,
                           final String name,
                           final String description,
                           final boolean required,
                           final Iterable<Modifier> modifiers,
                           final Iterable<MetaConstraint> constraints)
        {
                super(name, modifiers);
                this.type = type;
                this.description = description;
                this.required = required;
                this.constraints = ImmutableSet.copyOf(constraints);
        }

        String canonicalNameAt(final MetaModel model)
        {
                return canonicalNameAt(model.canonicalName);
        }

        String canonicalNameAt(final String modelCanonicalName)
        {
                return modelCanonicalName + '.' + name;
        }

        boolean hasConstraints()
        {
                return !constraints.isEmpty();
        }

        boolean shouldValidate()
        {
                return required || !constraints.isEmpty();
        }
}

abstract class MetaModelId extends MetaModelAttribute {

        MetaModelId(final TypeName type,
                    final String name,
                    final String description,
                    final boolean required,
                    final Iterable<Modifier> modifiers,
                    final Iterable<MetaConstraint> constraints)
        {
                super(type, name, description, required, modifiers, constraints);
        }

        boolean isId()
        {
                return false;
        }

        boolean isName()
        {
                return false;
        }
}

final class MetaName extends MetaModelId {

        private static final TypeName TYPE = ClassName.get(String.class);

        MetaName(final String name,
                 final String description,
                 final Iterable<Modifier> modifiers,
                 final Iterable<MetaConstraint> constraints)
        {
                super(TYPE, name, description, true /*name is always required*/, modifiers,
                      constraints);
        }

        @Override
        boolean isName()
        {
                return true;
        }

        @Override
        boolean shouldValidate()
        {
                return true;
        }
}

final class MetaId extends MetaModelId {

        private static final TypeName TYPE = ClassName.LONG;

        MetaId(final String name,
               final String description,
               final boolean required,
               final Iterable<Modifier> modifiers,
               final Iterable<MetaConstraint> constraints)
        {
                super(TYPE, name, description, required, modifiers, constraints);
        }

        @Override
        boolean isId()
        {
                return true;
        }
}

final class MetaParent extends MetaModelAttribute {

        MetaParent(final TypeName type,
                   final String name,
                   final String description,
                   final boolean required,
                   final Iterable<Modifier> modifiers,
                   final Iterable<MetaConstraint> constraints)
        {
                super(type, name, description, required, modifiers, constraints);
        }
}

final class MetaField extends MetaModelAttribute {

        final String property;
        final boolean indexed;
        final boolean jsonIgnore;
        final String jsonFormat;

        private static final ImmutableSet<TypeName> FIELDS_WITH_DEFAULT_VALIDATIONS = ImmutableSet.of(
                TypeName.get(Category.class),
                TypeName.get(Email.class),
                TypeName.get(PhoneNumber.class),
                TypeName.get(PostalAddress.class)
        );

        MetaField(final TypeName type,
                  final String name,
                  final String description,
                  final String property,
                  final boolean indexed,
                  final boolean required,
                  final boolean jsonIgnore,
                  final String jsonFormat,
                  final ImmutableSet<Modifier> modifiers,
                  final Iterable<MetaConstraint> constraints)
        {
                super(type, name, description, required, modifiers, constraints);
                this.property = property;
                this.indexed = indexed;
                this.jsonIgnore = jsonIgnore;
                this.jsonFormat = jsonFormat;
        }

        @Override
        boolean shouldValidate()
        {
                return FIELDS_WITH_DEFAULT_VALIDATIONS.contains(type) || super.shouldValidate();
        }
}
