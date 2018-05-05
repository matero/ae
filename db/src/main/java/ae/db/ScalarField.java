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

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * scalar properties.
 */
public interface ScalarField<T> extends Field<T> {

  default @Nullable T asModelValue(final @Nullable Object value) {
    return type().cast(value);
  }

  default @Nullable Object asDatastoreValue(final @Nullable T value) {
    return value;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable T... values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result;
    if (values.length == 0) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.length);
      for (final T value : values) {
        result.add(asDatastoreValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Iterable<@Nullable T> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result = new java.util.LinkedList<>();
    final Iterator<@Nullable T> iterator = values.iterator();
    if (iterator.hasNext()) {
      while (iterator.hasNext()) {
        result.add(asDatastoreValue(iterator.next()));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Iterator<@Nullable T> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result = new java.util.LinkedList<>();
    while (values.hasNext()) {
      result.add(asDatastoreValue(values.next()));
    }
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Collection<@Nullable T> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result;
    if (values.isEmpty()) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.size());
      for (final T value : values) {
        result.add(asDatastoreValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable T> asModelValues(final @Nullable Object... values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable T> result;
    if (values.length == 0) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.length);
      for (final Object value : values) {
        result.add(asModelValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable T> asModelValues(final @Nullable Iterable<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable T>                    result   = new java.util.LinkedList<>();
    final java.util.Iterator<@Nullable Object> iterator = values.iterator();
    if (iterator.hasNext()) {
      while (iterator.hasNext()) {
        result.add(asModelValue(iterator.next()));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable T> asModelValues(final @Nullable Iterator<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable T> result = new java.util.LinkedList<>();
    while (values.hasNext()) {
      result.add(asModelValue(values.next()));
    }
    return result;
  }

  default @Nullable List<@Nullable T> asModelValues(final @Nullable Collection<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable T> result;
    if (values.isEmpty()) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.size());
      for (final Object value : values) {
        result.add(asModelValue(value));
      }
    }
    return result;
  }

  @Override default @Nullable T read(final @NonNull PropertyContainer data) {
    if (data == null) {
      throw new NullPointerException("data");
    }
    return asModelValue(data.getProperty(property()));
  }

  /**
   * unindexed scalar properties
   */
  abstract class Unindexed<T> extends FieldData<T> implements ScalarField<T> {
    protected Unindexed(final @NonNull String canonicalName,
                        final @NonNull String description,
                        final @NonNull String property,
                        final @NonNull String field,
                        final boolean required,
                        final @NonNull JsonStringNode jsonName,
                        final @NonNull String jsonPath,
                        final @NonNull JsonSerializer<T> jsonSerializer,
                        final @Nullable Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
    }

    @Override public final boolean indexed() {
      return false;
    }

    @Override public final void write(final @NonNull PropertyContainer data, final @Nullable T value) {
      data.setUnindexedProperty(property(), asDatastoreValue(value));
    }
  }

  /**
   * IndexedBooleanList scalar properties.
   */
  abstract class Indexed<T> extends FieldData<T> implements ScalarField<T>, Filterable<T> {
    private final @NonNull PropertyProjection projection;
    private final @NonNull SortPredicate asc;
    private final @NonNull SortPredicate desc;
    private final @NonNull FilterPredicate isNull;
    private final @NonNull FilterPredicate isNotNull;

    protected Indexed(final @NonNull String canonicalName,
                      final @NonNull String description,
                      final @NonNull String property,
                      final @NonNull String field,
                      final boolean required,
                      final @NonNull JsonStringNode jsonName,
                      final @NonNull String jsonPath,
                      final @NonNull JsonSerializer<T> jsonSerializer,
                      final @NonNull PropertyProjection projection,
                      final @Nullable Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
      this.projection = projection;
      this.asc = new Query.SortPredicate(property, Query.SortDirection.ASCENDING);
      this.desc = new Query.SortPredicate(property, Query.SortDirection.DESCENDING);
      this.isNull = new FilterPredicate(property, FilterOperator.EQUAL, null);
      this.isNotNull = new FilterPredicate(property, FilterOperator.NOT_EQUAL, null);
    }

    @Override public final boolean indexed() {
      return true;
    }

    @Override public final void write(final @NonNull PropertyContainer data, final @Nullable T value) {
      data.setIndexedProperty(property(), asDatastoreValue(value));
    }

    @Override public final @NonNull FilterPredicate isNull() {
      return isNull;
    }

    @Override public final @NonNull FilterPredicate isNotNull() {
      return isNotNull;
    }

    @Override public final @NonNull FilterPredicate eq(final @Nullable T value) {
      return new FilterPredicate(field(), FilterOperator.EQUAL, asDatastoreValue(value));
    }

    @Override public final @NonNull PropertyProjection projection() {
      return projection;
    }

    @Override public final @NonNull SortPredicate asc() {
      return asc;
    }

    @Override public final @NonNull SortPredicate desc() {
      return desc;
    }

    @Override public final @NonNull FilterPredicate ne(final @Nullable T value) {
      return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, asDatastoreValue(value));
    }

    @Override public final @NonNull FilterPredicate lt(final @Nullable T value) {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN, asDatastoreValue(value));
    }

    @Override public final @NonNull FilterPredicate le(final @Nullable T value) {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, asDatastoreValue(value));
    }

    @Override public final @NonNull FilterPredicate gt(final @Nullable T value) {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN, asDatastoreValue(value));
    }

    @Override public final @NonNull FilterPredicate ge(final @Nullable T value) {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL, asDatastoreValue(value));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable T... values) {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable Iterable<@Nullable T> values) {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable Iterator<@Nullable T> values) {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable Collection<@Nullable T> values) {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }
  }
}
