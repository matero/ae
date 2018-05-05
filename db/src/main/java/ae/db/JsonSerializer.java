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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface JsonSerializer<T> extends java.io.Serializable {
  @NonNull JsonNode toJson(@Nullable T value);

  @Nullable T fromJson(@NonNull JsonNode json, @NonNull String jsonPath);

  @Nullable T fromJson(@NonNull JsonNode json);
}

final class NotSerializableToJson<T> implements JsonSerializer<T> {
  private final Class<T> type;

  NotSerializableToJson(final Class<T> type) {
    this.type = type;
  }

  @Override public @NonNull JsonNode toJson(final @Nullable T value) {
    throw new UnsupportedOperationException("JSON serialization of " + type.getCanonicalName() + " instances is not supported.");
  }

  @Override public @Nullable T fromJson(final @NonNull JsonNode json, final @NonNull String jsonPath) {
    throw new UnsupportedOperationException("JSON serialization of " + type.getCanonicalName() + " instances is not supported.");
  }

  @Override public @Nullable T fromJson(final @NonNull JsonNode json) {
    throw new UnsupportedOperationException("JSON serialization of " + type.getCanonicalName() + " instances is not supported.");
  }
}

final class JsonArraySerializer<E> implements JsonSerializer<List<@Nullable E>> {
  private final JsonSerializer<E> elementJsonSerializer;

  JsonArraySerializer(final JsonSerializer<E> elementJsonSerializer) {
    this.elementJsonSerializer = elementJsonSerializer;
  }

  @Override public @NonNull JsonNode toJson(final @Nullable List<@Nullable E> value) {
    if (value == null) {
      return JsonNodeFactories.nullNode();
    }
    final ArrayList<JsonNode> elements = new ArrayList<>(value.size());
    for (final E element : value) {
      elements.add(elementJsonSerializer.toJson(element));
    }
    return JsonNodeFactories.array(elements);
  }

  @Override public @Nullable List<@Nullable E> fromJson(final @NonNull JsonNode json, final @NonNull String jsonPath) {
    if (!json.isNode(jsonPath)) {
      return null;
    }
    if (json.isNullNode(jsonPath)) {
      return null;
    }
    final List<JsonNode> array = json.getArrayNode(jsonPath);
    return interpret(array);
  }

  @Override @Nullable public List<@Nullable E> fromJson(final @NonNull JsonNode json) {
    if (json == null) {
      return null;
    }
    if (json.isNullNode()) {
      return null;
    }
    final List<JsonNode> array = json.getArrayNode();
    return interpret(array);
  }

  @NonNull List<@Nullable E> interpret(final @NonNull List<JsonNode> array) {
    if (array.isEmpty()) {
      return new ArrayList<>(2);
    }
    final ArrayList<@Nullable E> result = new ArrayList<>(array.size());
    for (final JsonNode element : array) {
      result.add(elementJsonSerializer.fromJson(element));
    }
    return result;
  }
}

final class JsonArrayNotSerializable<E> implements JsonSerializer<List<@Nullable E>> {
  private final @NonNull Class<E> elementType;

  JsonArrayNotSerializable(final @NonNull Class<E> elementType) {
    this.elementType = elementType;
  }

  @Override public @NonNull JsonNode toJson(final @Nullable List<@Nullable E> value) {
    throw new UnsupportedOperationException("JSON serialization of " + elementType.getCanonicalName() + " arrays is not supported.");
  }

  @Override public @Nullable List<@Nullable E> fromJson(final @NonNull JsonNode json, final @NonNull String jsonPath) {
    throw new UnsupportedOperationException("JSON serialization of " + elementType.getCanonicalName() + " arrays is not supported.");
  }

  @Override public @Nullable List<@Nullable E> fromJson(final @NonNull JsonNode json) {
    throw new UnsupportedOperationException("JSON serialization of " + elementType.getCanonicalName() + " arrays is not supported.");
  }
}
