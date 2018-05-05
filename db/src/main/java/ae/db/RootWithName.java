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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class RootWithName extends RootActiveEntity implements WithName {
  /**
   * Constructs an ROOT active entity with ID defining its kind.
   *
   * @param kind Kind of the active entity.
   */
  protected RootWithName(final @NonNull String kind) {
    super(kind);
  }

  /* **************************************************************************
   * entity construction facilities
   */
  public final @NonNull Entity make(final @NonNull String name) {
    final Entity data = newEntity(name);
    init(data);
    return data;
  }

  public final @NonNull Entity newEntity(final @NonNull String name) {
    return new Entity(kind, name);
  }

  public final @NonNull Key makeKey(final @NonNull String name) {
    return KeyFactory.createKey(kind, name);
  }

  /* **************************************************************************
   * persistence methods
   */
  public @Nullable Entity findByName(final @NonNull String name) {
    return find(makeKey(name));
  }

  public @NonNull Entity getByName(final @NonNull String name) throws EntityNotFoundException {
    return get(makeKey(name));
  }

  public void deleteByName(final @NonNull String name) {
    delete(makeKey(name));
  }

  public boolean existsByName(final @NonNull String name) {
    return exists(makeKey(name));
  }

  /* **************************************************************************
   * JSON Serialization
   */
  @Override protected final @NonNull Iterable<@NonNull JsonField> jsonKeyFields(final @NonNull Key key) {
    return ImmutableList.of(modelIdentifier().makeJsonFieldFrom(key));
  }

  @Override public final @Nullable Key keyFromJson(final @NonNull JsonNode json) {
    if (json == null || json.isNullNode()) {
      return null;
    }
    final String name = modelIdentifier().interpretJson(json);
    return makeKey(name);
  }

  @Override public @Nullable Entity fromJson(final @NonNull JsonNode json) {
    if (json.isNullNode()) {
      return null;
    }
    final String name = modelIdentifier().interpretJson(json);
    final Entity data = make(name);
    updatePropertiesWithJsonContents(data, json);
    return data;
  }
}
