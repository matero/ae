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

import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public interface WithId extends java.io.Serializable {
  Id modelIdentifier();

  Entity make();

  Entity make(long id);

  Key makeKey(long id);

  Entity newEntity();

  Entity newEntity(long id);

  final class Id extends BasicId {
    public Id(final String canonicalName,
              final String description,
              final String field,
              final JsonStringNode jsonName,
              final String jsonPath,
              final Constraint... constraints) {
      super(canonicalName, description, field, jsonName, jsonPath, constraints);
    }

    @Override
    void doValidate(final Long value, final Validation validation) {
      if (constraints != null && constraints.length > 0) {
        for (final Constraint constraint : constraints) {
          if (constraint.isInvalid(value)) {
            validation.reject(this, constraint.messageFor(this, value));
          }
        }
      }
    }
  }

  final class RequiredId extends BasicId {
    public RequiredId(final String canonicalName,
                      final String description,
                      final String field,
                      final JsonStringNode jsonName,
                      final String jsonPath,
                      final Constraint... constraints) {
      super(canonicalName, description, field, jsonName, jsonPath, constraints);
    }

    @Override
    void doValidate(final Long value, final Validation validation) {
      if (value == 0L) {
        validation.reject(this, RequiredConstraint.INSTANCE.messageFor(this, value));
      } else {
        if (constraints != null && constraints.length > 0) {
          for (final Constraint constraint : constraints) {
            if (constraint.isInvalid(value)) {
              validation.reject(this, constraint.messageFor(this, value));
            }
          }
        }
      }
    }
  }
}

abstract class BasicId extends ActiveEntity.Identifier {
  BasicId(final String canonicalName,
          final String description,
          final String field,
          final JsonStringNode jsonName,
          final String jsonPath,
          final Constraint... constraints) {
    super(canonicalName, description, field, jsonName, jsonPath, constraints);
  }

  public long of(final Entity data) {
    return read(data);
  }

  public long read(final Entity data) {
    if (data == null) {
      throw new NullPointerException("data");
    }
    return read(data.getKey());
  }

  public long of(final Key key) {
    return read(key);
  }

  public long read(final Key key) {
    if (key == null) {
      throw new NullPointerException("key");
    }
    return key.getId();
  }

  @Override public boolean isDefinedAt(final Key key) {
    if (key == null) {
      throw new NullPointerException("key");
    }
    return key.getId() != 0;
  }

  @Override public Long interpretJson(final JsonNode json) {
    if (json == null) {
      throw new NullPointerException("json");
    }
    if (json.isNullNode(jsonPath())) {
      return null;
    }
    final String id = json.getNumberValue(jsonPath());
    if (id == null) {
      return null;
    } else {
      return Long.parseLong(id);
    }
  }

  @Override public JsonNode makeJsonValue(final Key key) {
    if (key == null) {
      throw new NullPointerException("key");
    }
    return JsonNodeFactories.number(key.getId());
  }

  @Override public void validate(final Entity data, final Validation validation) {
    if (data == null) {
      throw new NullPointerException("data");
    }
    if (validation == null) {
      throw new NullPointerException("validation");
    }
    final Long value = read(data);
    doValidate(value, validation);
  }

  abstract void doValidate(final Long value, final Validation validation);
}
