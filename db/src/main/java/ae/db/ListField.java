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

import static org.checkerframework.checker.nullness.NullnessUtil.castNonNull;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortPredicate;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ListField<E> extends Field<List<@Nullable E>> {
  @Override default Class<List<@Nullable E>> type() { return (Class<List<@Nullable E>>) (Class<?>) List.class; }

  Class<E> elementType();

  default @Nullable E asModelElementValue(final @Nullable Object value) { return elementType().cast(value); }

  default @Nullable Object asDatastoreElementValue(final @Nullable E value) { return value; }

  default @Nullable ArrayList<@Nullable Object> asDatastoreValues(final @Nullable E... values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result;
    if (values.length == 0) {
      result = new ArrayList<>();
    } else {
      result = new ArrayList<>(values.length);
      for (final E value : values) {
        result.add(asDatastoreElementValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Iterable<@Nullable E> values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result = new ArrayList<>();
    for (final E value : values) {
      result.add(asDatastoreElementValue(value));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Iterator<@Nullable E> values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result = new ArrayList<>();
    while (values.hasNext()) {
      result.add(asDatastoreElementValue(values.next()));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Collection<@Nullable E> values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result;
    if (values.isEmpty()) {
      result = new ArrayList<>();
    } else {
      result = new ArrayList<>(values.size());
      for (final @Nullable E value : values) {
        result.add(asDatastoreElementValue(value));
      }
      result.trimToSize();
    }
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Object... values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable E> result;
    if (values.length == 0) {
      result = new ArrayList<>();
    } else {
      result = new ArrayList<>(values.length);
      for (final @Nullable Object value : values) {
        result.add(asModelElementValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Iterable<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable E> result = new ArrayList<>();
    for (final @Nullable Object value : values) {
      result.add(asModelElementValue(value));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Iterator<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable E> result = new ArrayList<>();
    while (values.hasNext()) {
      result.add(asModelElementValue(values.next()));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Collection<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable E> result;
    if (values.isEmpty()) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.size());
      for (final Object value : values) {
        result.add(asModelElementValue(value));
      }
    }
    return result;
  }

  @Override default @Nullable List<@Nullable E> read(final PropertyContainer data) {
    final @Nullable Object values = data.getProperty(property());
    return asModelValues((@Nullable Collection<@Nullable Object>) values);
  }

  /**
   * unindexed list properties
   */
  abstract class Unindexed<E> extends FieldData<List<@Nullable E>> implements ListField<E> {
    public Unindexed(final String canonicalName,
                     final String description,
                     final String property,
                     final String field,
                     final boolean required,
                     final JsonStringNode jsonName,
                     final String jsonPath,
                     final JsonSerializer<List<@Nullable E>> jsonSerializer,
                     final ImmutableList<Constraint> constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
    }

    @Override public final boolean indexed() {
      return false;
    }

    @Override public final void write(final PropertyContainer data, final @Nullable List<@Nullable E> value) {
      data.setUnindexedProperty(property(), castNonNull(asDatastoreValues(value)));
    }
  }

  /**
   * IndexedBooleanList List properties.
   */
  abstract class Indexed<E> extends FieldData<List<@Nullable E>> implements ListField<E>, Filterable<E> {
    private final PropertyProjection projection;
    private final SortPredicate      asc;
    private final SortPredicate      desc;
    private final FilterPredicate    isNull;
    private final FilterPredicate    isNotNull;

    protected Indexed(final String canonicalName,
                      final String description,
                      final String property,
                      final String field,
                      final boolean required,
                      final JsonStringNode jsonName,
                      final String jsonPath,
                      final JsonSerializer<List<@Nullable E>> jsonSerializer,
                      final PropertyProjection projection,
                      final ImmutableList<Constraint> constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
      this.projection = projection;
      this.asc = new Query.SortPredicate(property, Query.SortDirection.ASCENDING);
      this.desc = new Query.SortPredicate(property, Query.SortDirection.DESCENDING);
      this.isNull = new FilterPredicate(property, FilterOperator.EQUAL, castNonNull(null));
      this.isNotNull = new FilterPredicate(property, FilterOperator.NOT_EQUAL, castNonNull(null));
    }

    @Override public final FilterPredicate isNull() { return isNull; }

    @Override public final FilterPredicate isNotNull() { return isNotNull; }

    @Override public final PropertyProjection projection() { return projection; }

    @Override public final SortPredicate asc() { return asc; }

    @Override public final SortPredicate desc() { return desc; }

    @Override public final boolean indexed() { return true; }

    @Override public final void write(final PropertyContainer data, final @Nullable List<@Nullable E> value) {
      data.setIndexedProperty(property(), castNonNull(asDatastoreValues(value)));
    }

    @Override public final FilterPredicate eq(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final FilterPredicate ne(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final FilterPredicate lt(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final FilterPredicate le(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final FilterPredicate gt(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final FilterPredicate ge(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final FilterPredicate in(final @Nullable E... values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }

    @Override public final FilterPredicate in(final @Nullable Iterable<@Nullable E> values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }

    @Override public final FilterPredicate in(final @Nullable Iterator<@Nullable E> values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }

    @Override public final FilterPredicate in(final @Nullable Collection<@Nullable E> values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }
  }
}
