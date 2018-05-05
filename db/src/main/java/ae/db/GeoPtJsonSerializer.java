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
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

enum GeoPtJsonSerializer implements JsonSerializer<GeoPt> {
  INSTANCE;

  static final JsonArraySerializer<GeoPt> ARRAY = new JsonArraySerializer<>(INSTANCE);

  private final JsonStringNode latitude = JsonNodeFactories.string("lat");
  private final JsonStringNode longitude = JsonNodeFactories.string("lon");

  @Override public @NonNull JsonNode toJson(final @Nullable GeoPt value) {
    if (value == null) {
      return JsonNodeFactories.nullNode();
    }
    return JsonNodeFactories.object(
            ImmutableList.of(JsonNodeFactories.field(latitude, JsonNodeFactories.number(Float.toString(value.getLatitude()))),
                             JsonNodeFactories.field(longitude, JsonNodeFactories.number(Float.toString(value.getLongitude())))));
  }

  @Override public @Nullable GeoPt fromJson(final @NonNull JsonNode json, final @NonNull String jsonPath) {
    if (json.isNullNode(jsonPath)) {
      return null;
    } else {
      final Map<JsonStringNode, JsonNode> value = json.getObjectNode(jsonPath);
      return makeGeoPt(value);
    }
  }

  @Override public @Nullable GeoPt fromJson(final @NonNull JsonNode json) {
    if (json.isNullNode()) {
      return null;
    } else {
      final Map<JsonStringNode, JsonNode> value = json.getObjectNode();
      return makeGeoPt(value);
    }
  }

  @NonNull GeoPt makeGeoPt(final @NonNull Map<JsonStringNode, JsonNode> value) {
    return new GeoPt(number(value, latitude, "lat"), number(value, longitude, "lon"));
  }

  float number(final @NonNull Map<JsonStringNode, JsonNode> value, final @NonNull JsonStringNode node, final @NonNull String field) {
    final JsonNode json = value.get(node);
    if (json == null || json.isNullNode()) {
      throw new NullPointerException(field);
    }
    final String numberValue = json.getNumberValue();
    if (numberValue == null) {
      throw new NullPointerException(field);
    }
    return Float.parseFloat(numberValue);
  }
}
