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
import com.google.appengine.api.datastore.Query.*;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class RootWithId extends RootActiveEntity implements WithId {
  /**
   * Constructs an ROOT active entity with ID defining its kind.
   *
   * @param kind Kind of the active entity.
   */
  protected RootWithId(final @NonNull String kind) {
    super(kind);
  }

  /* **************************************************************************
   * entity construction facilities
   */
  @Override public @NonNull Entity make() {
    final Entity data = newEntity();
    init(data);
    return data;
  }

  @Override public final @NonNull Entity newEntity() {
    return new Entity(this.kind);
  }

  @Override public @NonNull Entity make(final long id) {
    final Entity data = newEntity(id);
    init(data);
    return data;
  }

  @Override public final @NonNull Entity newEntity(final long id) {
    return new Entity(this.kind, id);
  }

  @Override public @NonNull Key makeKey(final long id) {
    return KeyFactory.createKey(this.kind, id);
  }

  /* **************************************************************************
   * persistence methods
   */
  public void deleteById(final long id) {
    DatastoreServiceFactory.getDatastoreService().delete(makeKey(id));
  }

  public @Nullable Entity findById(final long id) {
    try {
      return DatastoreServiceFactory.getDatastoreService().get(makeKey(id));
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public @NonNull Entity getById(final long id) throws EntityNotFoundException {
    return DatastoreServiceFactory.getDatastoreService().get(makeKey(id));
  }

  public boolean existsById(final long id) {
    final Query exists = makeQuery()
            .setKeysOnly()
            .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, makeKey(id)));
    final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
    return data != null;
  }

  /* **************************************************************************
   * JSON Serialization
   */
  @Override protected final @NonNull Iterable<JsonField> jsonKeyFields(final @NonNull Key key) {
    return ImmutableList.of(modelIdentifier().makeJsonFieldFrom(key));
  }

  @Override public final @Nullable Key keyFromJson(final @NonNull JsonNode json) {
    if (json == null || json.isNullNode()) {
      return null;
    }
    final @Nullable Long id = modelIdentifier().interpretJson(json);
    if (id == null) {
      return newEntity().getKey();
    } else {
      return makeKey(id);
    }
  }

  @Override public @Nullable Entity fromJson(final @NonNull JsonNode json) {
    if (json.isNullNode()) {
      return null;
    }
    final @Nullable Long id = modelIdentifier().interpretJson(json);
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
