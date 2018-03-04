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

public interface WithName extends java.io.Serializable {
  Name modelIdentifier();

  final class Name extends ActiveEntity.Identifier {
    public Name(final String canonicalName,
                final String description,
                final String field,
                final JsonStringNode jsonName,
                final String jsonPath,
                final Constraint... constraints) {
      super(canonicalName, description, field, jsonName, jsonPath, constraints);
    }

    public String of(final Entity data) {
      return read(data);
    }

    public String read(final Entity data) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      return read(data.getKey());
    }

    public String of(final Key key) {
      return read(key);
    }

    public String read(final Key key) {
      if (key == null) {
        throw new NullPointerException("key");
      }
      return key.getName();
    }

    @Override public boolean isDefinedAt(final Key key) {
      if (key == null) {
        throw new NullPointerException("key");
      }
      return key.getName() != null;
    }

    @Override public String interpretJson(final JsonNode json) {
      if (json == null) {
        throw new NullPointerException("json");
      }
      return json.getNullableStringValue(jsonPath());
    }

    @Override public JsonNode makeJsonValue(final Key key) {
      if (key == null) {
        throw new NullPointerException("key");
      }
      return JsonNodeFactories.string(key.getName());
    }

    @Override public void validate(final Entity data, final Validation validation) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      if (validation == null) {
        throw new NullPointerException("validation");
      }
      final String value = read(data);
      if (RequiredConstraint.INSTANCE.isInvalid(value)) {
        validation.reject(this, RequiredConstraint.INSTANCE.messageFor(this, value));
      } else {
        if (NotBlankConstraint.ForString.INSTANCE.isInvalid(value)) {
          validation.reject(this, NotBlankConstraint.ForString.INSTANCE.messageFor(this, value));
        }
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
