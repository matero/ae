/*
 * The MIT License
 *
 * Copyright 2018 juanjo.
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
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

public interface EntityModel extends java.io.Serializable {

        String kind();

        default boolean isKindOf(final Entity data)
        {
                if (data == null) {
                        return false;
                }
                return isKindOf(data.getKey());
        }

        default boolean isKindOf(final Key key)
        {
                if (key == null) {
                        return false;
                }
                return kind().equals(key.getKind());
        }

        default Key keyFrom(final String webSafeKey) throws IllegalEntityKind
        {
                if (webSafeKey == null) {
                        return null;
                }
                final Key key = KeyFactory.stringToKey(webSafeKey);
                if (isKindOf(key)) {
                        return key;
                }
                throw new IllegalEntityKind(webSafeKey, getClass());
        }

        ImmutableList<Attr> modelAttributes();

        Identifier modelIdentifier();

        ImmutableList<Field<?>> modelFields();

        JsonNode toJson(final Entity data);

        default JsonNode toJson(final Entity... elements)
        {
                if (elements == null) {
                        return JsonNodeFactories.nullNode();
                }
                if (elements.length == 0) {
                        return JsonNodeFactories.array(ImmutableList.<JsonNode>of());
                }
                final Collection<JsonNode> nodes = new java.util.ArrayList<>(elements.length);
                for (final Entity e : elements) {
                        nodes.add(toJson(e));
                }
                return JsonNodeFactories.array(nodes);
        }

        default JsonNode toJson(final Collection<Entity> elements)
        {
                if (elements == null) {
                        return JsonNodeFactories.nullNode();
                }
                if (elements.isEmpty()) {
                        return JsonNodeFactories.array(ImmutableList.<JsonNode>of());
                }
                final Collection<JsonNode> nodes = new java.util.ArrayList<>(elements.size());
                for (final Entity e : elements) {
                        nodes.add(toJson(e));
                }
                return JsonNodeFactories.array(nodes);
        }

        default JsonNode toJson(final Iterable<Entity> elements)
        {
                if (elements == null) {
                        return JsonNodeFactories.nullNode();
                }
                final Collection<JsonNode> nodes = new java.util.ArrayList<>(Iterables.size(elements));
                for (final Entity e : elements) {
                        nodes.add(toJson(e));
                }
                return JsonNodeFactories.array(nodes);
        }

        default JsonNode toJson(final Iterator<Entity> elements)
        {
                if (elements == null) {
                        return JsonNodeFactories.nullNode();
                }
                if (elements.hasNext()) {
                        final java.util.LinkedList<JsonNode> nodes = new java.util.LinkedList<>();
                        do {
                                nodes.add(toJson(elements.next()));
                        } while (elements.hasNext());
                        return JsonNodeFactories.array(nodes);
                } else {
                        return JsonNodeFactories.array(ImmutableList.<JsonNode>of());
                }
        }

        Entity fromJson(JsonNode data);

        Key keyFromJson(JsonNode json);

        void updatePropertiesWithJsonContents(Entity data, JsonNode json);

        Key save(Entity data) throws IllegalEntityKind, NullPointerException;

        /**
         * Deletes the entity identified by the key
         *
         * @param key Key of the entity to delete.
         */
        void delete(Key key) throws IllegalEntityKind, NullPointerException;

        Entity find(Key key) throws IllegalEntityKind, NullPointerException;

        Entity get(Key key) throws IllegalEntityKind, NullPointerException, EntityNotFoundException;

        boolean exists(Entity data) throws IllegalEntityKind, NullPointerException;

        boolean exists(Key key) throws IllegalEntityKind, NullPointerException;

        default Query makeQuery()
        {
                return new Query(kind());
        }

        Validation validate(Entity data, String successMessage);

        Validation validate(Entity data, Validation validation);

        static abstract class Identifier extends AttrData {

                private static final long serialVersionUID = -1934606419426144533L;

                protected Identifier(final String canonicalName,
                                     final String description,
                                     final String field,
                                     final JsonStringNode jsonName,
                                     final String jsonPath,
                                     final ImmutableList<Constraint> constraints)
                {
                        super(canonicalName, description, field, jsonName, jsonPath, constraints);
                }

                @Override
                public final String property()
                {
                        return field();
                }

                @Override
                public final boolean isDefinedAt(final Entity data)
                {
                        return isDefinedAt(data.getKey());
                }

                public abstract boolean isDefinedAt(Key key);

                public final JsonField makeJsonFieldFrom(final Entity data)
                {
                        return makeJsonFieldFrom(data.getKey());
                }

                public final JsonField makeJsonFieldFrom(final Key key)
                {
                        return JsonNodeFactories.field(jsonName(), makeJsonValue(key));
                }

                @Override
                public final JsonNode makeJsonValue(final Entity data)
                {
                        return makeJsonValue(data.getKey());
                }

                public abstract JsonNode makeJsonValue(Key key);
        }

        public static abstract class Select implements java.io.Serializable {

                private static final long serialVersionUID = 3845407868349511540L;

                final Query query;
                private final FetchOptions fetchOptions;

                protected Select(final Query query, final FetchOptions fetchOptions)
                {
                        this.query = query;
                        this.fetchOptions = fetchOptions;
                }

                public List<Entity> asList()
                {
                        return prepare().asList(this.fetchOptions);
                }

                public QueryResultList<Entity> asQueryResultList()
                {
                        return prepare().asQueryResultList(this.fetchOptions);
                }

                public Iterable<Entity> asIterable()
                {
                        return prepare().asIterable(this.fetchOptions);
                }

                public QueryResultIterable<Entity> asQueryResultIterable()
                {
                        return prepare().asQueryResultIterable(this.fetchOptions);
                }

                public Iterator<Entity> asIterator()
                {
                        return prepare().asIterator(this.fetchOptions);
                }

                public QueryResultIterator<Entity> asQueryResultIterator()
                {
                        return prepare().asQueryResultIterator(this.fetchOptions);
                }

                public Entity asSingle() throws PreparedQuery.TooManyResultsException
                {
                        return prepare().asSingleEntity();
                }

                public int count()
                {
                        return prepare().countEntities(this.fetchOptions);
                }

                public Select limit(final int limit)
                {
                        this.fetchOptions.limit(limit);
                        return this;
                }

                public Select offset(final int offset)
                {
                        this.fetchOptions.offset(offset);
                        return this;
                }

                public Select chunkSize(final int chunkSize)
                {
                        this.fetchOptions.chunkSize(chunkSize);
                        return this;
                }

                public Select prefetchSize(final int prefetchSize)
                {
                        this.fetchOptions.prefetchSize(prefetchSize);
                        return this;
                }

                public Select startCursor(final Cursor startCursor)
                {
                        this.fetchOptions.startCursor(startCursor);
                        return this;
                }

                public Select endCursor(final Cursor endCursor)
                {
                        this.fetchOptions.endCursor(endCursor);
                        return this;
                }

                protected PreparedQuery prepare()
                {
                        return datastore().prepare(this.query);
                }

                protected DatastoreService datastore()
                {
                        return DatastoreServiceFactory.getDatastoreService();
                }
        }

        public static abstract class SorteableSelect extends Select {

                private static final long serialVersionUID = 8141301362335037541L;

                SorteableSelect(final Query query, final FetchOptions fetchOptions)
                {
                        super(query, fetchOptions);
                }

                public final Select sortedBy(final Query.SortPredicate sort)
                {
                        this.query.addSort(sort.getPropertyName(), sort.getDirection());
                        return this;
                }

                public final Select sortedBy(final Query.SortPredicate s1, final Query.SortPredicate s2)
                {
                        this.query.addSort(s1.getPropertyName(), s1.getDirection());
                        this.query.addSort(s2.getPropertyName(), s2.getDirection());
                        return this;
                }

                public final Select sortedBy(final Query.SortPredicate s1,
                                             final Query.SortPredicate s2,
                                             final Query.SortPredicate s3)
                {
                        this.query.addSort(s1.getPropertyName(), s1.getDirection());
                        this.query.addSort(s2.getPropertyName(), s2.getDirection());
                        this.query.addSort(s3.getPropertyName(), s3.getDirection());
                        return this;
                }

                public final Select sortedBy(final Query.SortPredicate s1,
                                             final Query.SortPredicate s2,
                                             final Query.SortPredicate s3,
                                             final Query.SortPredicate... otherSorts)
                {
                        this.query.addSort(s1.getPropertyName(), s1.getDirection());
                        this.query.addSort(s2.getPropertyName(), s2.getDirection());
                        this.query.addSort(s3.getPropertyName(), s3.getDirection());
                        for (final Query.SortPredicate sort : otherSorts) {
                                this.query.addSort(sort.getPropertyName(), sort.getDirection());
                        }
                        return this;
                }

                public final Select sortedBy(final Iterable<Query.SortPredicate> sorts)
                {
                        final Iterator<Query.SortPredicate> i = sorts.iterator();
                        while (i.hasNext()) {
                                final Query.SortPredicate sort = i.next();
                                this.query.addSort(sort.getPropertyName(), sort.getDirection());
                        }
                        return this;
                }
        }

        public static class SelectEntities extends SorteableSelect {

                private static final long serialVersionUID = 5591903627552341816L;

                SelectEntities(final Query query, final FetchOptions fetchOptions)
                {
                        super(query, fetchOptions);
                }

                public final SorteableSelect where(final Query.Filter filter)
                {
                        this.query.setFilter(filter);
                        return this;
                }

                public final SorteableSelect where(final IndexedBoolean flag)
                {
                        this.query.setFilter(flag.isTrue());
                        return this;
                }
        }

        static final class IllegalEntityKind extends IllegalArgumentException {
                private static final long serialVersionUID = 6770457534939837365L;

                IllegalEntityKind(final String webSafeKey, final Class<? extends EntityModel> model)
                {
                        super("Web safe key repr. '" + webSafeKey + "' does not represent " + model);
                }

                IllegalEntityKind(final Key key, final Class<? extends EntityModel> model)
                {
                        super("Key '" + key + "' does not represent " + model);
                }

                IllegalEntityKind(final Entity data, final Class<? extends EntityModel> model)
                {
                        super("Entity with key '" + data.getKey() + "' does not represent " + model);
                }
        }
}
