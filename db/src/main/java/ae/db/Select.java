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

import com.google.appengine.api.datastore.BaseDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Cursor;

import java.util.Iterator;
import java.util.List;

public abstract class Select implements java.io.Serializable {

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
                return asList(DatastoreServiceFactory.getDatastoreService());
        }

        protected List<Entity> asList(final BaseDatastoreService datastore)
        {
                return prepare(datastore).asList(fetchOptions);
        }

        public QueryResultList<Entity> asQueryResultList()
        {
                return asQueryResultList(DatastoreServiceFactory.getDatastoreService());
        }

        protected QueryResultList<Entity> asQueryResultList(final BaseDatastoreService datastore)
        {
                return prepare(datastore).asQueryResultList(fetchOptions);
        }

        public Iterable<Entity> asIterable()
        {
                return asIterable(DatastoreServiceFactory.getDatastoreService());
        }

        protected Iterable<Entity> asIterable(final BaseDatastoreService datastore)
        {
                return prepare(datastore).asIterable(fetchOptions);
        }

        public QueryResultIterable<Entity> asQueryResultIterable()
        {
                return asQueryResultIterable(DatastoreServiceFactory.getDatastoreService());
        }

        protected QueryResultIterable<Entity> asQueryResultIterable(final BaseDatastoreService datastore)
        {
                return prepare(datastore).asQueryResultIterable(fetchOptions);
        }

        public Iterator<Entity> asIterator()
        {
                return asIterator(DatastoreServiceFactory.getDatastoreService());
        }

        protected Iterator<Entity> asIterator(final BaseDatastoreService datastore)
        {
                return prepare(datastore).asIterator(fetchOptions);
        }

        public QueryResultIterator<Entity> asQueryResultIterator()
        {
                return asQueryResultIterator(DatastoreServiceFactory.getDatastoreService());
        }

        protected QueryResultIterator<Entity> asQueryResultIterator(final BaseDatastoreService datastore)
        {
                return prepare(datastore).asQueryResultIterator(fetchOptions);
        }

        public Entity asSingle() throws PreparedQuery.TooManyResultsException
        {
                return asSingle(DatastoreServiceFactory.getDatastoreService());
        }

        protected Entity asSingle(final BaseDatastoreService datastore) throws PreparedQuery.TooManyResultsException
        {
                return prepare(datastore).asSingleEntity();
        }

        public int count()
        {
                return count(DatastoreServiceFactory.getDatastoreService());
        }

        protected int count(final BaseDatastoreService datastore)
        {
                return prepare(datastore).countEntities(fetchOptions);
        }

        public Select limit(final int limit)
        {
                fetchOptions.limit(limit);
                return this;
        }

        public Select offset(final int offset)
        {
                fetchOptions.offset(offset);
                return this;
        }

        public Select chunkSize(final int chunkSize)
        {
                fetchOptions.chunkSize(chunkSize);
                return this;
        }

        public Select prefetchSize(final int prefetchSize)
        {
                fetchOptions.prefetchSize(prefetchSize);
                return this;
        }

        public Select startCursor(final Cursor startCursor)
        {
                fetchOptions.startCursor(startCursor);
                return this;
        }

        public Select endCursor(final Cursor endCursor)
        {
                fetchOptions.endCursor(endCursor);
                return this;
        }

        protected PreparedQuery prepare(final BaseDatastoreService datastore)
        {
                return datastore.prepare(query);
        }
}
