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
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Attr extends java.io.Serializable {
  @NonNull String canonicalName();

  @NonNull String description();

  @NonNull String field();

  @NonNull String property();

  @NonNull JsonStringNode jsonName();

  @NonNull String jsonPath();

  boolean isDefinedAt(@NonNull Entity data);

  default boolean isDefinedAt(final @NonNull JsonNode json) { return json.getFields().containsKey(jsonName()); }

  @NonNull JsonNode makeJsonValue(@NonNull Entity data);

  default @NonNull JsonField makeJsonField(final @NonNull Entity data) { return JsonNodeFactories.field(jsonName(), makeJsonValue(data)); }

  @Nullable Object interpretJson(@NonNull JsonNode json);

  @NonNull ImmutableList<@NonNull Constraint> constraints();

  void validate(@NonNull Entity data, @NonNull Validation validation);
}

abstract class AttrData implements Attr {
  private final @NonNull String canonicalName;

  private final @NonNull String description;

  private final @NonNull String field;

  private final @NonNull JsonStringNode jsonName;

  private final @NonNull String jsonPath;

  private final @NonNull ImmutableList<@NonNull Constraint> constraints;

  protected AttrData(final @NonNull String canonicalName,
                     final @NonNull String description,
                     final @NonNull String field,
                     final @NonNull JsonStringNode jsonName,
                     final @NonNull String jsonPath,
                     final @NonNull ImmutableList<@NonNull Constraint> constraints) {
    this.canonicalName = canonicalName;
    this.description = description;
    this.field = field;
    this.jsonName = jsonName;
    this.jsonPath = jsonPath;
    this.constraints = constraints;
  }

  @Override public final @NonNull String canonicalName() { return canonicalName; }

  @Override public final @NonNull String description() { return description; }

  @Override public final @NonNull String field() { return field; }

  @Override public final @NonNull JsonStringNode jsonName() { return jsonName; }

  @Override public final @NonNull String jsonPath() { return jsonPath; }
  
  @Override public final @NonNull ImmutableList<@NonNull Constraint> constraints() { return constraints; }
}
