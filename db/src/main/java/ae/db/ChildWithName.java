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
import java.util.concurrent.ExecutionException;

public abstract class ChildWithName<P extends ActiveEntity> extends ChildActiveEntity<P> implements WithName {

        private static final long serialVersionUID = -7165772094949873742L;

        public Entity make(final String name)
        {
                if (name == null) {
                        throw new NullPointerException("name");
                }
                final Entity data = newEntity(name);
                init(data);
                return data;
        }

        public Entity make(final Key key)
        {
                final Entity data = newEntity(key);
                init(data);
                return data;
        }

        public Entity make(final Entity parent, final String name)
        {
                if (parent == null) {
                        throw new NullPointerException("parent");
                }
                if (name == null) {
                        throw new NullPointerException("name");
                }
                final Entity data = newEntity(parent, name);
                init(data);
                return data;
        }

        public Entity make(final Key parentKey, final String name)
        {
                if (name == null) {
                        throw new NullPointerException("name");
                }
                final Entity data = newEntity(parentKey, name);
                init(data);
                return data;
        }

        public Key makeKey(final String name)
        {
                if (name == null) {
                        throw new NullPointerException("name");
                }
                return KeyFactory.createKey(kind(), name);
        }

        public Key makeKey(final Entity parent, final String name)
        {
                if (parent == null) {
                        throw new NullPointerException("parent");
                }
                if (name == null) {
                        throw new NullPointerException("name");
                }
                return KeyFactory.createKey(parent.getKey(), kind(), name);
        }

        public Key makeKey(final Key parentKey, final String name)
        {
                if (name == null) {
                        throw new NullPointerException("name");
                }
                if (!modelParent().isKindOf(parentKey)) {
                        throw new IllegalParentKind(parentKey, getClass());
                }
                return KeyFactory.createKey(parentKey, kind(), name);
        }

        public Entity newEntity(final String name)
        {
                if (name == null) {
                        throw new NullPointerException("name");
                }
                return new Entity(kind(), name);
        }

        public Entity newEntity(final Key key)
        {
                if (!isKindOf(key)) {
                        throw new IllegalEntityKind(key, getClass());
                }
                return new Entity(key);
        }

        public Entity newEntity(final Entity parent, final String name)
        {
                if (parent == null) {
                        throw new NullPointerException("parent");
                }
                if (name == null) {
                        throw new NullPointerException("name");
                }
                return new Entity(kind(), name, parent.getKey());
        }

        public Entity newEntity(final Key parentKey, final String name)
        {
                if (name == null) {
                        throw new NullPointerException("name");
                }
                if (!modelParent().isKindOf(parentKey)) {
                        throw new IllegalParentKind(parentKey, getClass());
                }
                return new Entity(kind(), name, parentKey);
        }

        public void deleteByParentAndName(final Entity parent, final String name)
        {
                final Key key = makeKey(parent, name);
                try {
                        deleteEntity(key).get();
                } catch (final InterruptedException | ExecutionException e) {
                        throw new PersistenceException("could not delete entity", e);
                }
        }

        public void deleteByParentKeyAndName(final Key parentKey, final String name)
        {
                final Key key = makeKey(parentKey, name);
                try {
                        deleteEntity(key).get();
                } catch (final InterruptedException | ExecutionException e) {
                        throw new PersistenceException("could not delete entity", e);
                }
        }

        public Entity findByParentAndName(final Entity parent, final String name)
        {
                final Key key = makeKey(parent, name);
                try {
                        return getEntity(key);
                } catch (final EntityNotFoundException e) {
                        return null;
                }
        }

        public Entity findByParentKeyAndName(final Key parentKey, final String name)
        {
                final Key key = makeKey(parentKey, name);
                try {
                        return getEntity(key);
                } catch (final EntityNotFoundException e) {
                        return null;
                }
        }

        public Entity getByParentAndName(final Entity parent, final String name) throws EntityNotFoundException
        {
                final Key key = makeKey(parent, name);
                return getEntity(key);
        }

        public Entity getByParentKeyAndName(final Key parentKey, final String name) throws EntityNotFoundException
        {
                final Key key = makeKey(parentKey, name);
                return getEntity(key);
        }

        public boolean existsByParentAndName(final Entity parent, final String name)
        {
                final Key key = makeKey(parent, name);
                return checkExists(key);
        }

        public boolean existsByParentKeyAndName(final Key parentKey, final String name)
        {
                final Key key = makeKey(parentKey, name);
                return checkExists(key);
        }

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
                final String name = modelIdentifier().interpretJson(json);
                final Key parentKey = modelParent().interpretJson(json);
                if (name == null) {
                        throw new NullPointerException(modelIdentifier().field());
                }
                if (parentKey == null) {
                        return makeKey(name);
                } else {
                        return makeKey(parentKey, name);
                }
        }

        @Override
        public Entity fromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                }
                final String name = modelIdentifier().interpretJson(json);
                final Key parentKey = modelParent().interpretJson(json);
                if (name == null) {
                        throw new NullPointerException(modelIdentifier().field());
                }
                final Entity data;
                if (parentKey == null) {
                        data = make(name);
                } else {
                        data = make(parentKey, name);
                }
                updatePropertiesWithJsonContents(data, json);
                return data;
        }
}
