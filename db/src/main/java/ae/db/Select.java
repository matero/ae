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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.List;

public abstract class Select implements java.io.Serializable {
  final @NonNull Query query;
  private final @NonNull FetchOptions fetchOptions;

  protected Select(final @NonNull Query query, final @NonNull FetchOptions fetchOptions) {
    this.query = query;
    this.fetchOptions = fetchOptions;
  }

  public @NonNull List<@NonNull Entity> asList() { return asList(DatastoreServiceFactory.getDatastoreService()); }

  protected @NonNull List<@NonNull Entity> asList(final @NonNull BaseDatastoreService datastore) { return prepare(datastore).asList(fetchOptions); }

  public @NonNull QueryResultList<@NonNull Entity> asQueryResultList() { return asQueryResultList(DatastoreServiceFactory.getDatastoreService()); }

  protected @NonNull QueryResultList<@NonNull Entity> asQueryResultList(final @NonNull BaseDatastoreService datastore) {
    return prepare(datastore).asQueryResultList(fetchOptions);
  }

  public @NonNull Iterable<@NonNull Entity> asIterable() { return asIterable(DatastoreServiceFactory.getDatastoreService()); }

  protected Iterable<Entity> asIterable(final BaseDatastoreService datastore) { return prepare(datastore).asIterable(fetchOptions); }

  public @NonNull QueryResultIterable<@NonNull Entity> asQueryResultIterable() {
    return asQueryResultIterable(DatastoreServiceFactory.getDatastoreService());
  }

  protected @NonNull QueryResultIterable<@NonNull Entity> asQueryResultIterable(final @NonNull BaseDatastoreService datastore) {
    return prepare(datastore).asQueryResultIterable(fetchOptions);
  }

  public @NonNull Iterator<@NonNull Entity> asIterator() { return asIterator(DatastoreServiceFactory.getDatastoreService()); }

  protected @NonNull Iterator<@NonNull Entity> asIterator(final @NonNull BaseDatastoreService datastore) {
    return prepare(datastore).asIterator(fetchOptions);
  }

  public @NonNull QueryResultIterator<@NonNull Entity> asQueryResultIterator() {
    return asQueryResultIterator(DatastoreServiceFactory.getDatastoreService());
  }

  protected @NonNull QueryResultIterator<@NonNull Entity> asQueryResultIterator(final @NonNull BaseDatastoreService datastore) {
    return prepare(datastore).asQueryResultIterator(fetchOptions);
  }

  public @NonNull Entity asSingle() throws PreparedQuery.TooManyResultsException {
    return asSingle(DatastoreServiceFactory.getDatastoreService());
  }

  protected @NonNull Entity asSingle(final @NonNull BaseDatastoreService datastore) throws PreparedQuery.TooManyResultsException {
    return prepare(datastore).asSingleEntity();
  }

  public int count() {
    return count(DatastoreServiceFactory.getDatastoreService());
  }

  protected int count(final BaseDatastoreService datastore) {
    return prepare(datastore).countEntities(fetchOptions);
  }

  public @NonNull Select limit(final int limit) {
    fetchOptions.limit(limit);
    return this;
  }

  public @NonNull Select offset(final int offset) {
    fetchOptions.offset(offset);
    return this;
  }

  public @NonNull Select chunkSize(final int chunkSize) {
    fetchOptions.chunkSize(chunkSize);
    return this;
  }

  public @NonNull Select prefetchSize(final int prefetchSize) {
    fetchOptions.prefetchSize(prefetchSize);
    return this;
  }

  public @NonNull Select startCursor(final Cursor startCursor) {
    fetchOptions.startCursor(startCursor);
    return this;
  }

  public @NonNull Select endCursor(final Cursor endCursor) {
    fetchOptions.endCursor(endCursor);
    return this;
  }

  protected @NonNull PreparedQuery prepare(final @NonNull BaseDatastoreService datastore) {
    return datastore.prepare(query);
  }
}
