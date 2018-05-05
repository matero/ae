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
import com.google.appengine.api.datastore.IMHandle;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

enum IMHandleJsonSerializer implements JsonSerializer<IMHandle> {
  INSTANCE;

  static final JsonArraySerializer<IMHandle> ARRAY = new JsonArraySerializer<>(INSTANCE);

  private final JsonStringNode address  = JsonNodeFactories.string("addr");
  private final JsonStringNode protocol = JsonNodeFactories.string("prot");

  @Override public @NonNull JsonNode toJson(final @Nullable IMHandle value) {
    if (value == null) {
      return JsonNodeFactories.nullNode();
    }
    return JsonNodeFactories.object(
        ImmutableList.of(JsonNodeFactories.field(address, JsonNodeFactories.string(value.getAddress())),
                         JsonNodeFactories.field(protocol, JsonNodeFactories.string(value.getProtocol()))));
  }

  @Override public @Nullable IMHandle fromJson(final @NonNull JsonNode json, final @NonNull String jsonPath) {
    if (json.isNullNode(jsonPath)) {
      return null;
    } else {
      final String addr = json.getNullableStringValue(jsonPath, "addr");
      final String prot = json.getStringValue(jsonPath, "prot");
      return makeImHandle(addr, prot);
    }
  }

  @Override public @Nullable IMHandle fromJson(final @NonNull  JsonNode json) {
    if (json.isNullNode()) {
      return null;
    } else {
      final String addr = json.getNullableStringValue("addr");
      final String prot = json.getStringValue("prot");
      return makeImHandle(addr, prot);
    }
  }

  @Nullable IMHandle makeImHandle(final @NonNull String addr, final @NonNull String prot) {
    try {
      return new IMHandle(IMHandle.Scheme.valueOf(prot), addr);
    } catch (final IllegalArgumentException e) {
      try {
        return new IMHandle(new URL(prot), addr);
      } catch (final MalformedURLException ex) {
        throw new IllegalStateException("IMHandle protocol is invalid", ex);
      }
    }
  }
}
