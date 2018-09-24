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

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.collect.ImmutableList;

public abstract class RootWithId extends RootActiveEntity implements WithId {

        private static final long serialVersionUID = -4301518873000440300L;

        /**
         * Constructs an ROOT active entity with ID defining its kind.
         *
         * @param kind Kind of the active entity.
         */
        protected RootWithId(final String kind)
        {
                super(kind);
        }

        /* **************************************************************************
   * entity construction facilities
         */
        @Override
        public Entity make()
        {
                final Entity data = newEntity();
                init(data);
                return data;
        }

        @Override
        public final Entity newEntity()
        {
                return new Entity(this.kind);
        }

        @Override
        public Entity make(final long id)
        {
                final Entity data = newEntity(id);
                init(data);
                return data;
        }

        @Override
        public final Entity newEntity(final long id)
        {
                return new Entity(this.kind, id);
        }

        @Override
        public Key makeKey(final long id)
        {
                return KeyFactory.createKey(this.kind, id);
        }

        /* **************************************************************************
   * persistence methods
         */
        public void deleteById(final long id)
        {
                DatastoreServiceFactory.getDatastoreService().delete(makeKey(id));
        }

        public Entity findById(final long id)
        {
                try {
                        return DatastoreServiceFactory.getDatastoreService().get(makeKey(id));
                } catch (final EntityNotFoundException e) {
                        return null;
                }
        }

        public Entity getById(final long id) throws EntityNotFoundException
        {
                return DatastoreServiceFactory.getDatastoreService().get(makeKey(id));
        }

        public boolean existsById(final long id)
        {
                final Query exists = makeQuery()
                        .setKeysOnly()
                        .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, makeKey(id)));
                final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
                return data != null;
        }

        /* **************************************************************************
   * JSON Serialization
         */
        @Override
        protected final Iterable<JsonField> jsonKeyFields(final Key key)
        {
                return ImmutableList.of(modelIdentifier().makeJsonFieldFrom(key));
        }

        @Override
        public final Key keyFromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                }
                final Long id = modelIdentifier().interpretJson(json);
                if (id == null) {
                        return newEntity().getKey();
                } else {
                        return makeKey(id);
                }
        }

        @Override
        public Entity fromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                }
                final Long id = modelIdentifier().interpretJson(json);
                final Entity data;
                if (id == null) {
                        data = make();
                } else {
                        data = make(id);
                }
                updatePropertiesWithJsonContents(data, json);
                return data;
        }
}
