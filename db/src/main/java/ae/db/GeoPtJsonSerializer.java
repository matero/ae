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
import java.util.Map;

enum GeoPtJsonSerializer implements JsonSerializer<GeoPt> {
  INSTANCE;

  static final JsonArraySerializer<GeoPt> ARRAY = new JsonArraySerializer<>(INSTANCE);

  private final JsonStringNode latitude = JsonNodeFactories.string("lat");
  private final JsonStringNode longitude = JsonNodeFactories.string("lon");

  @Override public JsonNode toJson(final GeoPt value) {
    if (value == null) {
      return JsonNodeFactories.nullNode();
    }
    return JsonNodeFactories.object(
            ImmutableList.of(
                    JsonNodeFactories.field(latitude, JsonNodeFactories.number(Float.toString(value.getLatitude()))),
                    JsonNodeFactories.field(longitude, JsonNodeFactories.number(Float.toString(value.getLongitude())))
            )
    );
  }

  @Override public GeoPt fromJson(final JsonNode json, final String jsonPath) {
    if (json.isNullNode(jsonPath)) {
      return null;
    } else {
      final Map<JsonStringNode, JsonNode> value = json.getObjectNode(jsonPath);
      return new GeoPt(Float.parseFloat(value.get(latitude).getNumberValue()), Float.parseFloat(value.get(longitude).getNumberValue()));
    }
  }

  @Override public GeoPt fromJson(final JsonNode json) {
    if (json.isNullNode()) {
      return null;
    } else {
      final Map<JsonStringNode, JsonNode> value = json.getObjectNode();
      return new GeoPt(Float.parseFloat(value.get(latitude).getNumberValue()), Float.parseFloat(value.get(longitude).getNumberValue()));
    }
  }
}
