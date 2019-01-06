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
import com.google.common.collect.ImmutableList;
import java.util.concurrent.ExecutionException;

public abstract class ChildWithId<P extends ActiveEntity> extends ChildActiveEntity<P> implements WithId {

  private static final long serialVersionUID = 240124574244515721L;

  protected ChildWithId()
  {
    // nothing to do
  }

  @Override
  public final Entity make()
  {
    final Entity data = newEntity();
    init(data);
    return data;
  }

  @Override
  public final Entity make(long id)
  {
    final Entity data = newEntity(id);
    init(data);
    return data;
  }

  public Entity make(final Entity parent, final long id)
  {
    final Entity data = newEntity(parent, id);
    init(data);
    return data;
  }

  public Entity make(final Key parentKey, final long id)
  {
    final Entity data = newEntity(parentKey, id);
    init(data);
    return data;
  }

  public Entity make(final Entity parent)
  {
    final Entity data = newEntity(parent);
    init(data);
    return data;
  }

  public Entity make(final Key parentKey)
  {
    final Entity data = newEntity(parentKey);
    init(data);
    return data;
  }

  @Override
  public final Key makeKey(long id)
  {
    return KeyFactory.createKey(kind(), id);
  }

  public final Key makeKey(final Key parentKey)
  {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalParentKind(parentKey, getClass());
    }
    return new Entity(kind(), parentKey).getKey();
  }

  public final Key makeKey(final Entity parent, final long id)
  {
    return makeKey(parent.getKey(), id);
  }

  public final Key makeKey(final Key parentKey, final long id)
  {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalParentKind(parentKey, getClass());
    }
    return KeyFactory.createKey(parentKey, kind(), id);
  }

  @Override
  public Entity newEntity()
  {
    return new Entity(kind());
  }

  @Override
  public Entity newEntity(final long id)
  {
    return new Entity(kind(), id);
  }

  public final Entity newEntity(final Entity parent, final long id)
  {
    if (parent == null) {
      throw new NullPointerException("parent");
    }
    return newEntity(parent.getKey(), id);
  }

  public final Entity newEntity(final Key parentKey, final long id)
  {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalParentKind(parentKey, getClass());
    }
    return new Entity(kind(), id, parentKey);
  }

  public final Entity newEntity(final Entity parent)
  {
    if (parent == null) {
      throw new NullPointerException("parent");
    }
    return newEntity(parent.getKey());
  }

  public final Entity newEntity(final Key parentKey)
  {
    if (!modelParent().isKindOf(parentKey)) {
      throw new IllegalParentKind(parentKey, getClass());
    }
    return new Entity(kind(), parentKey);
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

  public void deleteByParentAndId(final Entity parent, final long id)
  {
    final Key key = makeKey(parent, id);
    try {
      deleteEntity(key).get();
    } catch (final InterruptedException | ExecutionException e) {
      throw new PersistenceException("could not delete entity", e);
    }
  }

  public void deleteByParentKeyAndId(final Key parentKey, final long id)
  {
    final Key key = makeKey(parentKey, id);
    try {
      deleteEntity(key).get();
    } catch (final InterruptedException | ExecutionException e) {
      throw new PersistenceException("could not delete entity", e);
    }
  }

  public Entity findByParentAndId(final Entity parent, final long id)
  {
    final Key key = makeKey(parent, id);
    try {
      return getEntity(key);
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public Entity findByParentKeyAndId(final Key parentKey, final long id)
  {
    final Key key = makeKey(parentKey, id);
    try {
      return getEntity(key);
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public Entity getByParentAndId(final Entity parent, final long id) throws EntityNotFoundException
  {
    final Key key = makeKey(parent, id);
    return getEntity(key);
  }

  public Entity getByParentKeyAndId(final Key parentKey, final long id) throws EntityNotFoundException
  {
    final Key key = makeKey(parentKey, id);
    return getEntity(key);
  }

  public boolean existsByParentAndId(final Entity parent, final long id)
  {
    final Key key = makeKey(parent, id);
    return checkExists(key);
  }

  public boolean existsByParentKeyAndId(final Key parentKey, final long id)
  {
    final Key key = makeKey(parentKey, id);
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

  @Override
  public Entity fromJson(final JsonNode json)
  {
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
