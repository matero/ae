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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.ExecutionException;

public abstract class RootWithId extends RootActiveEntity implements WithId {

        private static final long serialVersionUID = -4301518873000440300L;

        protected RootWithId()
        {
                // nothing more to do
        }

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
                return new Entity(kind());
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
                return new Entity(kind(), id);
        }

        @Override
        public Key makeKey(final long id)
        {
                return KeyFactory.createKey(kind(), id);
        }

        public void deleteById(final long id)
        {
                final Key key = makeKey(id);
                try {
                        deleteEntity(key).get();
                } catch (final InterruptedException | ExecutionException e) {
                        throw new PersistenceException("could not delete entity", e);
                }
        }

        public Entity findById(final long id)
        {
                final Key key = makeKey(id);
                try {
                        return getEntity(key);
                } catch (final EntityNotFoundException e) {
                        return null;
                }
        }

        public Entity getById(final long id) throws EntityNotFoundException
        {
                final Key key = makeKey(id);
                return getEntity(key);
        }

        public boolean existsById(final long id)
        {
                final Key key = makeKey(id);
                return checkExists(key);
        }

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
