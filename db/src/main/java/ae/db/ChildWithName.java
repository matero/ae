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
import com.google.common.collect.ImmutableList;

public abstract class ChildWithName<P extends ActiveEntity> extends ChildActiveEntity<P> implements WithName {
  protected ChildWithName(final String kind) {
    super(kind);
  }

  /* **************************************************************************
   * entity construction facilities
   */
  public Entity make(final Entity parent, final String name) {
    final Entity data = newEntity(parent, name);
    init(data);
    return data;
  }

  public Entity make(final Key parentKey, final String name) {
    final Entity data = newEntity(parentKey, name);
    init(data);
    return data;
  }

  public Key makeKey(final Entity parent, final String name) {
    return ChildWithName.this.makeKey(parent.getKey(), name);
  }

  public Key makeKey(final Key parentKey, final String name) {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalArgumentException("[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
    }
    return KeyFactory.createKey(parentKey, kind, name);
  }

  public Entity newEntity(final Entity parent, final String name) {
    return newEntity(parent.getKey(), name);
  }

  public Entity newEntity(final Key parentKey, final String name) {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalArgumentException("[parentKey=" + parentKey + "] is not a possible parent for " + this + '.');
    }
    return new Entity(kind, name, parentKey);
  }

  /* **************************************************************************
   * persistence methods
   */
  public void deleteByParentAndName(final Entity parent, final String name) {
    DatastoreServiceFactory.getDatastoreService().delete(makeKey(parent, name));
  }

  public void deleteByParentKeyAndName(final Key parentKey, final String name) {
    DatastoreServiceFactory.getDatastoreService().delete(makeKey(parentKey, name));
  }

  public Entity findByParentAndName(final Entity parent, final String name) {
    try {
      return DatastoreServiceFactory.getDatastoreService().get(makeKey(parent, name));
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public Entity findByParentKeyAndName(final Key parentKey, final String name) {
    try {
      return DatastoreServiceFactory.getDatastoreService().get(makeKey(parentKey, name));
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public Entity getByParentAndName(final Entity parent, final String name) throws EntityNotFoundException {
    return DatastoreServiceFactory.getDatastoreService().get(makeKey(parent, name));
  }

  public Entity getByParentKeyAndName(final Key parentKey, final String name) throws EntityNotFoundException {
    return DatastoreServiceFactory.getDatastoreService().get(makeKey(parentKey, name));
  }

  public boolean existsByParentAndName(final Entity parent, final String name) {
    final Query exists = makeQuery()
            .setKeysOnly()
            .setFilter(new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, makeKey(parent, name)));
    final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
    return data != null;
  }

  public boolean existsByParentKeyAndName(final Key parentKey, final String name) {
    final Query exists = makeQuery()
            .setKeysOnly()
            .setFilter(new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, makeKey(parentKey, name)));
    final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
    return data != null;
  }

  /* **************************************************************************
   * JSON Serialization
   */
  @Override protected final Iterable<JsonField> jsonKeyFields(final Key key) {
    return ImmutableList.of(modelName().makeJsonFieldFrom(key), modelParent().makeJsonFieldFrom(key));
  }

  @Override public final Key keyFromJson(final JsonNode json) {
    if (json.isNullNode()) {
      return null;
    }
    final String name = modelName().interpretJson(json);
    final Key parentKey = modelParent().interpretJson(json);
    return makeKey(parentKey, name);
  }

  @Override public Entity fromJson(final JsonNode json) {
    if (json.isNullNode()) {
      return null;
    }
    final String name = modelName().interpretJson(json);
    final Key parentKey = modelParent().interpretJson(json);
    final Entity data = make(parentKey, name);
    updatePropertiesWithJsonContents(data, json);
    return data;
  }
}
