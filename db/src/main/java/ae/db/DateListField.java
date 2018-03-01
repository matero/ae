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

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import java.util.Date;
import java.util.List;

public interface DateListField extends ListField<Date> {
  @Override default Class<Date> elementType() {
    return Date.class;
  }

  @Override default JsonNode makeJsonValue(final List<Date> value) {
    if (value == null) {
      throw new NullPointerException("json");
    }
    return DateJsonSerializer.ARRAY.toJson(value);
  }

  @Override default List<Date> interpretJson(final JsonNode json) {
    if (json == null) {
      throw new NullPointerException("json");
    }
    return DateJsonSerializer.ARRAY.fromJson(json, jsonPath());
  }

  final class Unindexed extends ListField.Unindexed<Date> implements DateListField {
    public Unindexed(final String canonicalName,
                     final String description,
                     final String property,
                     final String field,
                     final boolean required,
                     final JsonStringNode jsonName,
                     final String jsonPath,
                     final Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, constraints);
    }
  }

  final class Indexed extends ListField.Indexed<Date> implements DateListField {
    public Indexed(final String canonicalName,
                   final String description,
                   final String property,
                   final String field,
                   final boolean required,
                   final JsonStringNode jsonName,
                   final String jsonPath,
                   final Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, new PropertyProjection(property, Date.class), constraints);
    }
  }
}
