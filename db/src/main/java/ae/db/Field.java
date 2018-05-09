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
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.NullnessUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Field<T> extends Attr {
  @Override default boolean isDefinedAt(final @NonNull Entity data) { return data.hasProperty(property()); }

  default boolean isDefinedAt(final @NonNull PropertyContainer data) { return data.hasProperty(property()); }

  @NonNull Class<T> type();

  boolean indexed();

  boolean required();

  default @Nullable T of(final @NonNull PropertyContainer data) { return read(data); }

  @Nullable T read(final @NonNull PropertyContainer data);

  void write(@NonNull PropertyContainer data, @Nullable T value);

  default void write(final @NonNull PropertyContainer data, final @NonNull JsonNode json) { write(data, interpretJson(json)); }

  @NonNull JsonNode makeJsonValue(@Nullable T value);

  default @NonNull JsonField makeJsonFieldFrom(final @NonNull PropertyContainer data) { return makeJsonField(read(data)); }

  default @NonNull JsonField makeJsonField(final @Nullable T value) { return JsonNodeFactories.field(jsonName(), makeJsonValue(value)); }

  @Override default @NonNull JsonNode makeJsonValue(final @NonNull Entity data) { return makeJsonValue(read(data)); }

  default @NonNull JsonNode makeJsonValue(final @NonNull PropertyContainer data) { return makeJsonValue(read(data)); }

  @Override @Nullable T interpretJson(@NonNull JsonNode json);
}

abstract class FieldData<T> extends AttrData implements Field<T> {
  private final @NonNull String property;
  private final boolean required;
  private final @NonNull JsonSerializer<T> jsonSerializer;

  protected FieldData(final @NonNull String canonicalName,
                      final @NonNull String description,
                      final @NonNull String property,
                      final @NonNull String field,
                      final boolean required,
                      final @NonNull JsonStringNode jsonName,
                      final @NonNull String jsonPath,
                      final @NonNull JsonSerializer<T> jsonSerializer,
                      final @NonNull ImmutableList<@NonNull Constraint> constraints) {
    super(canonicalName, description, field, jsonName, jsonPath, constraints);
    this.property = property;
    this.required = required;
    this.jsonSerializer = jsonSerializer;
  }

  @Override public final @NonNull String property() { return property; }

  @Override public final boolean required() { return required; }

  @Override public final @NonNull JsonNode makeJsonValue(final @Nullable  T value) { return jsonSerializer.toJson(value); }

  @Override public final @Nullable T interpretJson(final @NonNull JsonNode json) { return jsonSerializer.fromJson(json, jsonPath()); }

  @Override public final void validate(final @NonNull Entity data, final @NonNull Validation validation) {
    final @Nullable T value = read(data);
    if (value == null) {
      if (required()) {
        validation.reject(this, RequiredConstraint.INSTANCE.messageFor(this));
      }
    } else {
      validateNotNullValue(value, validation);
    }
  }

  protected void validateNotNullValue(final @NonNull T value, final @NonNull Validation validation) {
    for (final Constraint constraint : constraints()) {
      if (constraint.isInvalid(value)) {
        validation.reject(this, constraint.messageFor(this, value));
      }
    }    
  }
}

enum RequiredConstraint implements Constraint<Object> {
  INSTANCE;

  @Override public boolean isInvalid(final Object value) {
    return value == null;
  }

  public String messageFor(final @NonNull Attr attr) { return messageFor(attr, this); }
  
  @Override public String messageFor(final @NonNull Attr attr, final @NonNull Object value) {
    final StringBuilder msg = new StringBuilder().append('\'').append(attr.description()).append("' es requerido.");
    return msg.toString();
  }

  @Override public String getName() {
    return "required";
  }
}
