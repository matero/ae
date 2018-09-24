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
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Modifier;

abstract class MetaModel extends MetaData {

        final String packageName;

        final String canonicalName;

        final String kind;

        final String baseClass;

        final MetaModelId id;

        final Fields fields;

        final boolean configuresDatastore;

        MetaModel(final String packageName,
                  final String className,
                  final String canonicalName,
                  final String kind,
                  final String baseClass,
                  final MetaModelId id,
                  final Fields modelFields,
                  final ImmutableSet<Modifier> modifiers,
                  final boolean configuresDatastore)
        {
                super(className, modifiers);
                this.packageName = packageName;
                this.canonicalName = canonicalName;
                this.kind = kind;
                this.baseClass = baseClass;
                this.id = id;
                this.fields = modelFields;
                this.configuresDatastore = configuresDatastore;
        }

        final boolean isPublic()
        {
                return visibility == Modifier.PUBLIC;
        }

        final boolean isAbstract()
        {
                return modifiers.stream().anyMatch((modifier) -> (Modifier.ABSTRACT == modifier));
        }

        abstract boolean hasParent();

        abstract MetaParent parent();

        final boolean hasFields()
        {
                return !fields.isEmpty();
        }

        final boolean useId()
        {
                return id.isId();
        }

        final boolean useName()
        {
                return id.isName();
        }

        List<String> fieldsNames()
        {
                final ArrayList<String> result = new ArrayList<>(fields.size());
                for (final MetaField field : fields) {
                        result.add(field.name);
                }
                return result;
        }

        abstract List<String> attributesNames();

        static final Fields NO_FIELDS = new Fields();

        final static Fields fields(final MetaField... fields)
        {
                synchronized (fields) {
                        if (fields.length == 0) {
                                return NO_FIELDS;
                        } else {
                                return new Fields(ImmutableList.copyOf(fields));
                        }
                }
        }

        final static Fields fields(final Collection<MetaField> fields)
        {
                synchronized (fields) {
                        if (fields.isEmpty()) {
                                return NO_FIELDS;
                        } else {
                                return new Fields(ImmutableList.copyOf(fields));
                        }

                }
        }

        static final class Fields implements Iterable<MetaField> {

                private final ImmutableList<MetaField> data;

                private Fields(final ImmutableList<MetaField> fields)
                {
                        data = fields;
                }

                private Fields()
                {
                        data = ImmutableList.of();
                }

                @Override
                public Iterator<MetaField> iterator()
                {
                        return data.iterator();
                }

                boolean isEmpty()
                {
                        return data.isEmpty();
                }

                int size()
                {
                        return data.size();
                }
        }

        static MetaModel withSpec(final String packageName,
                                  final String className,
                                  final String classQualifiedName,
                                  final String kind,
                                  final String baseClass,
                                  final MetaModelId id,
                                  final MetaParent parent,
                                  final Fields fields,
                                  final Iterable<Modifier> modifiers,
                                  final boolean configuresDatastore)
        {
                if (parent == null) {
                        return new RootModel(packageName,
                                             className,
                                             classQualifiedName,
                                             kind,
                                             baseClass,
                                             id,
                                             fields,
                                             ImmutableSet.copyOf(modifiers),
                                             configuresDatastore);
                } else {
                        return new ChildModel(packageName,
                                              className,
                                              classQualifiedName,
                                              kind,
                                              baseClass,
                                              id,
                                              parent,
                                              fields,
                                              ImmutableSet.copyOf(modifiers),
                                              configuresDatastore);
                }
        }
}

final class RootModel extends MetaModel {

        RootModel(final String packageName,
                  final String className,
                  final String canonicalName,
                  final String kind,
                  final String baseClass,
                  final MetaModelId id,
                  final Fields fields,
                  final ImmutableSet<Modifier> modifiers,
                  final boolean configuresDatastore)
        {
                super(packageName, className, canonicalName, kind, baseClass, id, fields, modifiers, configuresDatastore);
        }

        @Override
        boolean hasParent()
        {
                return false;
        }

        @Override
        MetaParent parent()
        {
                throw new UnsupportedOperationException();
        }

        @Override
        List<String> attributesNames()
        {
                final ArrayList<String> result = new ArrayList<>(fields.size() + 1);
                result.add(this.id.name);
                for (final MetaField field : fields) {
                        result.add(field.name);
                }
                return result;
        }
}

final class ChildModel extends MetaModel {

        final MetaParent parent;

        ChildModel(final String packageName,
                   final String className,
                   final String canonicalName,
                   final String kind,
                   final String baseClass,
                   final MetaModelId id,
                   final MetaParent parent,
                   final Fields fields,
                   final ImmutableSet<Modifier> modifiers,
                   final boolean configuresDatastore)
        {
                super(packageName, className, canonicalName, kind, baseClass, id, fields, modifiers, configuresDatastore);
                this.parent = parent;
        }

        @Override
        boolean hasParent()
        {
                return true;
        }

        @Override
        MetaParent parent()
        {
                return parent;
        }

        @Override
        List<String> attributesNames()
        {
                final ArrayList<String> result = new ArrayList<>(fields.size() + 2);
                result.add(this.id.name);
                result.add(this.parent.name);
                for (final MetaField field : fields) {
                        result.add(field.name);
                }
                return result;
        }
}
