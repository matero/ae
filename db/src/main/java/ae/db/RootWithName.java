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

public abstract class RootWithName extends RootActiveEntity implements WithName {

  private static final long serialVersionUID = -5374806570138213003L;

  protected RootWithName()
  {
    // nothing more to do
  }

  public final Entity make(final String name)
  {
    final Entity data = newEntity(name);
    init(data);
    return data;
  }

  public final Entity newEntity(final String name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    return new Entity(kind(), name);
  }

  public final Key makeKey(final String name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    return KeyFactory.createKey(kind(), name);
  }

  public Entity findByName(final String name)
  {
    final Key key = makeKey(name);
    try {
      return getEntity(key);
    } catch (final EntityNotFoundException e) {
      return null;
    }
  }

  public Entity getByName(final String name) throws EntityNotFoundException
  {
    final Key key = makeKey(name);
    return getEntity(key);
  }

  public void deleteByName(final String name)
  {
    final Key key = makeKey(name);
    try {
      deleteEntity(key).get();
    } catch (final InterruptedException | ExecutionException e) {
      throw new PersistenceException("could not delete entity", e);
    }
  }

  public boolean existsByName(final String name)
  {
    final Key key = makeKey(name);
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
    if (json == null || json.isNullNode()) {
      return null;
    }
    final String name = modelIdentifier().interpretJson(json);
    if (name == null) {
      return null;
    } else {
      return makeKey(name);
    }
  }

  @Override
  public Entity fromJson(final JsonNode json)
  {
    if (json.isNullNode()) {
      return null;
    }
    final String name = modelIdentifier().interpretJson(json);
    if (name == null) {
      throw new IllegalArgumentException("no " + modelIdentifier().field() + " defined.");
    }
    final Entity data = make(name);
    updatePropertiesWithJsonContents(data, json);
    return data;
  }
}
