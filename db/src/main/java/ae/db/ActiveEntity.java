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
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * ActiveRecord over google appengine datastore's {@link Entity}.
 * <p>
 * Usually used as base for code generated by db-processor compiler.
 */
public abstract class ActiveEntity implements Serializable {
  private static final long serialVersionUID = -6755700696013461574L;
  /**
   * Kind of modeled Entities.
   */
  protected final String kind;

  /**
   * Constructs an active entity defining its kind.
   *
   * @param kind Kind of the active entity.
   */
  protected ActiveEntity(final String kind) {
    this.kind = kind;
  }

  /**
   * @return the logger associated to the active entity, <em>never</em> {@code null}.
   */
  protected abstract Logger log();
  /* **************************************************************************
   * entity construction facilities
   */

  /**
   * Initialize an {@link Entity} after its construction.
   *
   * @param data the {@link Entity} to initialize.
   */
  protected void init(final Entity data) {
    /*nothing to do*/ }

  /* **************************************************************************
   * validation methods
   */
  public boolean isKindOf(final Entity data) {
    return isKindOf(data.getKey());
  }

  public boolean isKindOf(final Key key) {
    return kind.equals(key.getKind());
  }

  public Key keyFrom(final String webSafeKey) {
    if (webSafeKey == null) {
      return null;
    }
    final Key key = KeyFactory.stringToKey(webSafeKey);
    if (!isKindOf(key)) {
      throw new IllegalArgumentException("'" + webSafeKey + "' represents '" + key + "' which doesn't identify a '" + kind + "' Entity.");
    }
    return key;
  }

  /* **************************************************************************
   * metadata
   */
  public abstract ImmutableList<Attr> modelAttributes();

  public abstract Identifier modelIdentifier();

  public abstract ImmutableList<Field<?>> modelFields();

  /* **************************************************************************
   * json serialization methods
   */
  public abstract JsonNode toJson(final Entity data);

  public JsonNode toJson(final Entity... elements) {
    if (elements == null) {
      return JsonNodeFactories.nullNode();
    }
    if (elements.length == 0) {
      return JsonNodeFactories.array(Collections.<JsonNode>emptyList());
    }
    final List<JsonNode> nodes = new java.util.ArrayList<>(elements.length);
    for (final Entity e : elements) {
      nodes.add(toJson(e));
    }
    return JsonNodeFactories.array(nodes);
  }

  public JsonNode toJson(final Collection<Entity> elements) {
    if (elements == null) {
      return JsonNodeFactories.nullNode();
    }
    if (elements.isEmpty()) {
      return JsonNodeFactories.array(Collections.<JsonNode>emptyList());
    }
    final List<JsonNode> nodes = new java.util.ArrayList<>(elements.size());
    for (final Entity e : elements) {
      nodes.add(toJson(e));
    }
    return JsonNodeFactories.array(nodes);
  }

  public JsonNode toJson(final Iterable<Entity> elements) {
    if (elements == null) {
      return JsonNodeFactories.nullNode();
    }
    final List<JsonNode> nodes = new java.util.ArrayList<>(Iterables.size(elements));
    for (final Entity e : elements) {
      nodes.add(toJson(e));
    }
    return JsonNodeFactories.array(nodes);
  }

  public JsonNode toJson(final Iterator<Entity> elements) {
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
      return JsonNodeFactories.array(Collections.<JsonNode>emptyList());
    }
  }

  public abstract Entity fromJson(JsonNode data);

  public abstract Key keyFromJson(JsonNode json);

  protected abstract Iterable<JsonField> jsonKeyFields(Key key);

  public abstract void updatePropertiesWithJsonContents(Entity data, JsonNode json);

  /* **************************************************************************
   * persistence methods
   */
  public final Key save(final Entity data) {
    if (!isKindOf(data)) {
      throw new IllegalArgumentException("Entity with key '" + data.getKey() + "' does not represent " + getClass().getCanonicalName());
    }
    return DatastoreServiceFactory.getDatastoreService().put(data);
  }
  public final Future<Key> saveAsync(final Entity data) {
    if (!isKindOf(data)) {
      throw new IllegalArgumentException("Entity with key '" + data.getKey() + "' does not represent " + getClass().getCanonicalName());
    }
    return DatastoreServiceFactory.getAsyncDatastoreService().put(data);
  }

  /**
   * Deletes the entity identified by the key
   *
   * @param key Key of the entity to delete.
   */
  public void delete(final Key key) {
    if (!isKindOf(key)) {
      throw new IllegalArgumentException("Key '" + key + "' does not represent " + getClass().getCanonicalName());
    }
    DatastoreServiceFactory.getDatastoreService().delete(key);
  }
  public void deleteAsync(final Key key) {
    if (!isKindOf(key)) {
      throw new IllegalArgumentException("Key '" + key + "' does not represent " + getClass().getCanonicalName());
    }
    DatastoreServiceFactory.getAsyncDatastoreService().delete(key);
  }

  public Entity find(final Key key) {
    if (!isKindOf(key)) {
      throw new IllegalArgumentException("Key '" + key + "' does not represent " + getClass().getCanonicalName());
    }
    try {
      return DatastoreServiceFactory.getDatastoreService().get(key);
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public Entity get(final Key key) throws EntityNotFoundException {
    if (!isKindOf(key)) {
      throw new IllegalArgumentException("Key '" + key + "' does not represent " + getClass().getCanonicalName());
    }
    return DatastoreServiceFactory.getDatastoreService().get(key);
  }

  public boolean exists(final Key key) {
    if (!isKindOf(key)) {
      throw new IllegalArgumentException("Key '" + key + "' does not represent " + getClass().getCanonicalName());
    }
    final Query exists = makeQuery()
            .setKeysOnly()
            .setFilter(new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, key));
    final Entity data = DatastoreServiceFactory.getDatastoreService().prepare(exists).asSingleEntity();
    return data != null;
  }

  public Query makeQuery() {
    return new Query(kind);
  }

  public Validation validate(final Entity data, final String successMessage) {
    final Validation validation = Validation.withSuccessMessage(successMessage);
    doValidate(data, validation);
    return validation;
  }

  public Validation validate(final Entity data, final Validation validation) {
    doValidate(data, validation);
    return validation;
  }

  protected abstract void doValidate(Entity data, Validation validation);

  public static abstract class Identifier extends AttrData {
    private static final long serialVersionUID = -1934606419426144533L;

    protected Identifier(final String canonicalName,
                         final String description,
                         final String field,
                         final JsonStringNode jsonName,
                         final String jsonPath,
                         final ImmutableList<Constraint> constraints) {
      super(canonicalName, description, field, jsonName, jsonPath, constraints);
    }

    @Override public final String property() {
      return field();
    }

    @Override public final boolean isDefinedAt(final Entity data) {
      return isDefinedAt(data.getKey());
    }

    public abstract boolean isDefinedAt(Key key);

    public final JsonField makeJsonFieldFrom(final Entity data) {
      return makeJsonFieldFrom(data.getKey());
    }

    public final JsonField makeJsonFieldFrom(final Key key) {
      return JsonNodeFactories.field(jsonName(), makeJsonValue(key));
    }

    @Override public final JsonNode makeJsonValue(final Entity data) {
      return makeJsonValue(data.getKey());
    }

    public abstract JsonNode makeJsonValue(Key key);
  }

  /* **************************************************************************
   * query building facilities
   */
  final Query projection(final Filterable<?>... projectedProperties) {
    final Query q = makeQuery();
    for (final Filterable<?> filterable : projectedProperties) {
      q.addProjection(filterable.projection());
    }
    return q;
  }

  final Query projection(final Iterable<Filterable<?>> projectedProperties) {
    final Query q = makeQuery();
    for (final Filterable<?> filterable : projectedProperties) {
      q.addProjection(filterable.projection());
    }
    return q;
  }

  protected static final Filter not(final IndexedBoolean flag) {
    return flag.isFalse();
  }

  protected static final Filter and(final IndexedBoolean flag, final Filter rhs) {
    return Query.CompositeFilterOperator.AND.of(flag.isTrue(), rhs);
  }

  protected static final Filter or(final IndexedBoolean flag, final Filter rhs) {
    return Query.CompositeFilterOperator.OR.of(flag.isTrue(), rhs);
  }

  protected static final Filter and(final Filter lhs, final IndexedBoolean flag) {
    return Query.CompositeFilterOperator.AND.of(flag.isTrue(), lhs);
  }

  protected static final Filter or(final Filter lhs, final IndexedBoolean flag) {
    return Query.CompositeFilterOperator.OR.of(flag.isTrue(), lhs);
  }

  public static class SelectEntities extends SorteableSelect {
    private static final long serialVersionUID = 5591903627552341816L;

    SelectEntities(final Query query, final FetchOptions fetchOptions) {
      super(query, fetchOptions);
    }

    public final SorteableSelect where(final Filter filter) {
      query.setFilter(filter);
      return this;
    }

    public final SorteableSelect where(final IndexedBoolean flag) {
      query.setFilter(flag.isTrue());
      return this;
    }
  }
}
