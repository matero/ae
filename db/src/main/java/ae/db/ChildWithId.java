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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ChildWithId<P extends ActiveEntity> extends ChildActiveEntity<P> implements WithId {
  protected ChildWithId(final @NonNull String kind) { super(kind); }

  /* **************************************************************************
   * entity construction facilities
   */
  @Override public final @NonNull Entity make() {
    final Entity data = newEntity();
    init(data);
    return data;
  }

  @Override public final @NonNull Entity make(long id) {
    final Entity data = newEntity(id);
    init(data);
    return data;
  }

  public @NonNull Entity make(final @NonNull Entity parent, final long id) {
    final Entity data = newEntity(parent, id);
    init(data);
    return data;
  }

  public @NonNull Entity make(final @NonNull Key parentKey, final long id) {
    final Entity data = newEntity(parentKey, id);
    init(data);
    return data;
  }

  public @NonNull Entity make(final @NonNull Entity parent) {
    final Entity data = newEntity(parent);
    init(data);
    return data;
  }

  public @NonNull Entity make(final @NonNull Key parentKey) {
    final Entity data = newEntity(parentKey);
    init(data);
    return data;
  }

  @Override public final @NonNull Key makeKey(long id) {
    return KeyFactory.createKey(kind, id);
  }

  public final @NonNull Key makeKey(final @NonNull Key parentKey) {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalArgumentException("[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
    }
    return new Entity(kind, parentKey).getKey();
  }

  public final @NonNull Key makeKey(final @NonNull Entity parent, final long id) {
    return makeKey(parent.getKey(), id);
  }

  public final @NonNull Key makeKey(final @NonNull Key parentKey, final long id) {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalArgumentException("[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
    }
    return KeyFactory.createKey(parentKey, kind, id);
  }

  @Override public @NonNull Entity newEntity() { return new Entity(kind); }

  @Override public @NonNull Entity newEntity(final long id) { return new Entity(kind, id); }

  public final @NonNull Entity newEntity(final @NonNull Entity parent, final long id) { return newEntity(parent.getKey(), id); }

  public final @NonNull Entity newEntity(final @NonNull Key parentKey, final long id) {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalArgumentException("[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
    }
    return new Entity(kind, id, parentKey);
  }

  public final @NonNull Entity newEntity(final @NonNull Entity parent) { return newEntity(parent.getKey()); }

  public final @NonNull Entity newEntity(final @NonNull Key parentKey) {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalArgumentException("[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
    }
    return new Entity(kind, parentKey);
  }

  /* **************************************************************************
   * persistence methods
   */
  public void deleteById(final long id) { DatastoreServiceFactory.getDatastoreService().delete(makeKey(id)); }

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

  public void deleteByParentAndId(final @NonNull Entity parent, final long id) {
    DatastoreServiceFactory.getDatastoreService().delete(makeKey(parent, id));
  }

  public void deleteByParentKeyAndId(final @NonNull Key parentKey, final long id) {
    DatastoreServiceFactory.getDatastoreService().delete(makeKey(parentKey, id));
  }

  public @Nullable Entity findByParentAndId(final @NonNull Entity parent, final long id) {
    try {
      return DatastoreServiceFactory.getDatastoreService().get(makeKey(parent, id));
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public @Nullable Entity findByParentKeyAndId(final @NonNull Key parentKey, final long id) {
    try {
      return DatastoreServiceFactory.getDatastoreService().get(makeKey(parentKey, id));
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public @NonNull Entity getByParentAndId(final @NonNull Entity parent, final long id) throws EntityNotFoundException {
    return DatastoreServiceFactory.getDatastoreService().get(makeKey(parent, id));
  }

  public @NonNull Entity getByParentKeyAndId(final @NonNull Key parentKey, final long id) throws EntityNotFoundException {
    return DatastoreServiceFactory.getDatastoreService().get(makeKey(parentKey, id));
  }

  public boolean existsByParentAndId(final @NonNull Entity parent, final long id) {
    final Query exists = makeQuery()
                             .setKeysOnly()
                             .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, makeKey(parent, id)));
    final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
    return data != null;
  }

  public boolean existsByParentKeyAndId(final @NonNull Key parentKey, final long id) {
    final Query exists = makeQuery()
                             .setKeysOnly()
                             .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, makeKey(parentKey, id)));
    final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
    return data != null;
  }

  /* **************************************************************************
   * JSON Serialization
   */
  @Override protected final @NonNull Iterable<@NonNull JsonField> jsonKeyFields(final @NonNull Key key) {
    return ImmutableList.of(modelIdentifier().makeJsonFieldFrom(key), modelParent().makeJsonFieldFrom(key));
  }

  @Override public final @Nullable Key keyFromJson(final @NonNull JsonNode json) {
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

  @Override public @Nullable Entity fromJson(final @NonNull JsonNode json) {
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
