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
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.collect.ImmutableList;

public abstract class ChildWithId<P extends ActiveEntity> extends ChildActiveEntity<P> implements WithId {

        private static final long serialVersionUID = 240124574244515721L;

        protected ChildWithId(final String kind)
        {
                super(kind);
        }

        /* **************************************************************************
   * entity construction facilities
         */
        @Override
        public final Entity make()
        {
                final Entity data = newEntity();
                init(data);
                return data;
        }

        @Override
        public final Entity make(long id)
        {
                final Entity data = newEntity(id);
                init(data);
                return data;
        }

        public Entity make(final Entity parent, final long id)
        {
                final Entity data = newEntity(parent, id);
                init(data);
                return data;
        }

        public Entity make(final Key parentKey, final long id)
        {
                final Entity data = newEntity(parentKey, id);
                init(data);
                return data;
        }

        public Entity make(final Entity parent)
        {
                final Entity data = newEntity(parent);
                init(data);
                return data;
        }

        public Entity make(final Key parentKey)
        {
                final Entity data = newEntity(parentKey);
                init(data);
                return data;
        }

        @Override
        public final Key makeKey(long id)
        {
                return KeyFactory.createKey(kind, id);
        }

        public final Key makeKey(final Key parentKey)
        {
                if (!modelParent().isKindOf(parentKey)) {
                        throw new IllegalArgumentException(
                                "[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
                }
                return new Entity(kind, parentKey).getKey();
        }

        public final Key makeKey(final Entity parent, final long id)
        {
                return makeKey(parent.getKey(), id);
        }

        public final Key makeKey(final Key parentKey, final long id)
        {
                if (!modelParent().isKindOf(parentKey)) {
                        throw new IllegalArgumentException(
                                "[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
                }
                return KeyFactory.createKey(parentKey, kind, id);
        }

        @Override
        public Entity newEntity()
        {
                return new Entity(kind);
        }

        @Override
        public Entity newEntity(final long id)
        {
                return new Entity(kind, id);
        }

        public final Entity newEntity(final Entity parent, final long id)
        {
                return newEntity(parent.getKey(), id);
        }

        public final Entity newEntity(final Key parentKey, final long id)
        {
                if (!modelParent().isKindOf(parentKey)) {
                        throw new IllegalArgumentException(
                                "[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
                }
                return new Entity(kind, id, parentKey);
        }

        public final Entity newEntity(final Entity parent)
        {
                return newEntity(parent.getKey());
        }

        public final Entity newEntity(final Key parentKey)
        {
                if (!modelParent().isKindOf(parentKey)) {
                        throw new IllegalArgumentException(
                                "[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
                }
                return new Entity(kind, parentKey);
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

        public void deleteByParentAndId(final Entity parent, final long id)
        {
                DatastoreServiceFactory.getDatastoreService().delete(makeKey(parent, id));
        }

        public void deleteByParentKeyAndId(final Key parentKey, final long id)
        {
                DatastoreServiceFactory.getDatastoreService().delete(makeKey(parentKey, id));
        }

        public Entity findByParentAndId(final Entity parent, final long id)
        {
                try {
                        return DatastoreServiceFactory.getDatastoreService().get(makeKey(parent, id));
                } catch (final EntityNotFoundException e) {
                        return null;
                }
        }

        public Entity findByParentKeyAndId(final Key parentKey, final long id)
        {
                try {
                        return DatastoreServiceFactory.getDatastoreService().get(makeKey(parentKey, id));
                } catch (final EntityNotFoundException e) {
                        return null;
                }
        }

        public Entity getByParentAndId(final Entity parent, final long id) throws EntityNotFoundException
        {
                return DatastoreServiceFactory.getDatastoreService().get(makeKey(parent, id));
        }

        public Entity getByParentKeyAndId(final Key parentKey, final long id) throws EntityNotFoundException
        {
                return DatastoreServiceFactory.getDatastoreService().get(makeKey(parentKey, id));
        }

        public boolean existsByParentAndId(final Entity parent, final long id)
        {
                final Query exists = makeQuery()
                        .setKeysOnly()
                        .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, makeKey(
                                                       parent, id)));
                final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
                return data != null;
        }

        public boolean existsByParentKeyAndId(final Key parentKey, final long id)
        {
                final Query exists = makeQuery()
                        .setKeysOnly()
                        .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL,
                                                       makeKey(parentKey, id)));
                final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
                return data != null;
        }

        /* **************************************************************************
   * JSON Serialization
         */
        @Override
        protected final Iterable<JsonField> jsonKeyFields(final Key key)
        {
                return ImmutableList.of(modelIdentifier().makeJsonFieldFrom(key), modelParent().makeJsonFieldFrom(key));
        }

        @Override
        public final Key keyFromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                }
                final Long id = modelIdentifier().interpretJson(json);
                final Key parentKey = modelParent().interpretJson(json);
                if (id == null) {
                        if (parentKey == null) {
                                return makeKey(0);
                        } else {
                                return makeKey(parentKey);
                        }
                } else {
                        if (parentKey == null) {
                                return makeKey(id);
                        } else {
                                return makeKey(parentKey, id);
                        }
                }
        }

        @Override
        public Entity fromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                }
                final Long id = modelIdentifier().interpretJson(json);
                final Key parentKey = modelParent().interpretJson(json);
                final Entity data;
                if (id == null) {
                        if (parentKey == null) {
                                data = make();
                        } else {
                                data = make(parentKey);
                        }
                } else {
                        if (parentKey == null) {
                                data = make(id);
                        } else {
                                data = make(parentKey, id);
                        }
                }
                updatePropertiesWithJsonContents(data, json);
                return data;
        }
}
