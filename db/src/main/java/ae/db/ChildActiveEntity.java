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
package ae.db;

import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableList;

public abstract class ChildActiveEntity<P extends ActiveEntity> extends ActiveEntity {

        private static final long serialVersionUID = -1172162463919296861L;

        protected ChildActiveEntity(final String kind)
        {
                super(kind);
        }

        /* **************************************************************************
   * metadata facilities
         */
        public abstract Parent<P> modelParent();

        @Override
        public final boolean isKindOf(final Key key)
        {
                return super.isKindOf(key) && modelParent().isKindOf(key.getParent());
        }

        public static final class Parent<P extends ActiveEntity> extends ActiveEntity.Identifier {

                private static final long serialVersionUID = -8377743561105112889L;

                private final P parent;
                private final boolean required;

                public Parent(final P parent,
                              final String canonicalName,
                              final String description,
                              final String field,
                              final boolean required,
                              final JsonStringNode jsonName,
                              final String jsonPath,
                              final ImmutableList<Constraint> constraints)
                {
                        super(canonicalName, description, field, jsonName, jsonPath, constraints);
                        this.parent = parent;
                        this.required = required;
                }

                public P parent()
                {
                        return parent;
                }

                public boolean isKindOf(final Key key)
                {
                        return parent().isKindOf(key);
                }

                @Override
                public boolean isDefinedAt(final Key key)
                {
                        return key.getParent() != null && parent().isKindOf(key.getParent());
                }

                public Key of(final Entity data)
                {
                        return read(data);
                }

                public Key read(final Entity data)
                {
                        return data.getParent();
                }

                public Key of(final Key key)
                {
                        return read(key);
                }

                public Key read(final Key key)
                {
                        return key.getParent();
                }

                @Override
                public JsonNode makeJsonValue(final Key key)
                {
                        return JsonNodeFactories.object(parent().jsonKeyFields(key));
                }

                @Override
                public Key interpretJson(final JsonNode json)
                {
                        return parent().keyFromJson(json.getNode(jsonPath()));
                }

                @Override
                public void validate(final Entity data, final Validation validation)
                {
                        final Key value = read(data);
                        if (value == null) {
                                if (required) {
                                        validation.reject(this, RequiredConstraint.INSTANCE.messageFor(this));
                                }
                        } else {
                                for (final Constraint constraint : constraints()) {
                                        if (constraint.isInvalid(value)) {
                                                validation.reject(this, constraint.messageFor(this, value));
                                        }
                                }
                        }
                }
        }

        /* **************************************************************************
   * query building facilities
         */
        public final Query makeQuery(final Entity parent)
        {
                if (parent == null) {
                        return new Query(kind);
                } else {
                        return new Query(kind, parent.getKey());
                }
        }

        public final Query makeQuery(final Key parentKey)
        {
                if (parentKey == null) {
                        return new Query(kind);
                } else {
                        return new Query(kind, parentKey);
                }
        }

        public final SelectChildEntities selectAll()
        {
                return new SelectChildEntities(makeQuery(), FetchOptions.Builder.withDefaults());
        }

        public final SelectChildEntities selectAll(final FetchOptions fetchOptions)
        {
                return new SelectChildEntities(makeQuery(), fetchOptions);
        }

        public final SelectChildEntities selectKeys()
        {
                return new SelectChildEntities(makeQuery().setKeysOnly(), FetchOptions.Builder.withDefaults());
        }

        public final SelectChildEntities select(final Filterable<?>... projectedProperties)
        {
                return new SelectChildEntities(projection(projectedProperties), FetchOptions.Builder.withDefaults());
        }

        public final SelectChildEntities select(final FetchOptions fetchOptions,
                                                final Filterable<?>... projectedProperties)
        {
                return new SelectChildEntities(projection(projectedProperties), fetchOptions);
        }

        public final SelectChildEntities select(final Iterable<Filterable<?>> projectedProperties)
        {
                return new SelectChildEntities(projection(projectedProperties), FetchOptions.Builder.withDefaults());
        }

        public final SelectChildEntities select(final FetchOptions fetchOptions,
                                                final Iterable<Filterable<?>> projectedProperties)
        {
                return new SelectChildEntities(projection(projectedProperties), fetchOptions);
        }

        public static final class SelectChildEntities extends RootActiveEntity.SelectEntities {

                private static final long serialVersionUID = 5591903627552341816L;

                SelectChildEntities(final Query query, final FetchOptions fetchOptions)
                {
                        super(query, fetchOptions);
                }

                public final SelectEntities withoutAncestor()
                {
                        return this;
                }

                public final SelectEntities withAncestor(final Entity ancestor)
                {
                        return withAncestor(ancestor.getKey());
                }

                public final SelectEntities withAncestor(final Key ancestorKey)
                {
                        query.setAncestor(ancestorKey);
                        return this;
                }
        }
}
