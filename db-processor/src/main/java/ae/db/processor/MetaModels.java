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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;

final class MetaModels implements Iterable<MetaModel> {

        final ImmutableMap<String, MetaModel> byKind;
        final ImmutableListMultimap<String, MetaModel> byPackage;
        final ImmutableMap<String, MetaModel> byCannonicalName;

        MetaModels(final ImmutableMap<String, MetaModel> byKind,
                   final ImmutableListMultimap<String, MetaModel> byPackage,
                   final ImmutableMap<String, MetaModel> byCannonicalClassName)
        {
                this.byKind = byKind;
                this.byPackage = byPackage;
                this.byCannonicalName = byCannonicalClassName;
        }

        static Builder builder()
        {
                return new Builder(new java.util.LinkedHashMap<>());
        }

        @Override
        public UnmodifiableIterator<MetaModel> iterator()
        {
                return byKind.values().iterator();
        }

        ImmutableSet<String> getPackages()
        {
                return byPackage.keySet();
        }

        ImmutableList<MetaModel> atPackage(final String packageName)
        {
                return byPackage.get(packageName);
        }

        ImmutableCollection<MetaModel> all()
        {
                return byKind.values();
        }

        int count()
        {
                return byKind.size();
        }

        static class Builder {

                private final java.util.Map<String, MetaModel> modelsByKind;

                Builder(final java.util.Map<String, MetaModel> modelsByKind)
                {
                        this.modelsByKind = modelsByKind;
                }

                Builder add(final MetaModel model)
                {
                        modelsByKind.put(model.kind, model);
                        return this;
                }

                boolean containsModelWithKind(final String modelKind)
                {
                        return modelsByKind.containsKey(modelKind);
                }

                MetaModels build()
                {
                        final ImmutableMap<String, MetaModel> byKind = ImmutableMap.copyOf(modelsByKind);
                        final ImmutableListMultimap.Builder<String, MetaModel> byPackage = ImmutableListMultimap.
                                builder();
                        final ImmutableMap.Builder<String, MetaModel> byCanonicalName = ImmutableMap.builder();

                        for (final MetaModel model : byKind.values()) {
                                byPackage.put(model.packageName, model);
                                byCanonicalName.put(model.canonicalName, model);
                        }

                        return new MetaModels(byKind, byPackage.build(), byCanonicalName.build());
                }

                Builder addAll(final MetaModel... models)
                {
                        for (final MetaModel m : models) {
                                this.modelsByKind.put(m.kind, m);
                        }
                        return this;
                }

                Builder addAll(final Iterable<MetaModel> models)
                {
                        for (final MetaModel m : models) {
                                this.modelsByKind.put(m.kind, m);
                        }
                        return this;
                }
        }
}
