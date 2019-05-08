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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortPredicate;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import static org.checkerframework.checker.nullness.NullnessUtil.castNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * scalar properties.
 */
public interface ScalarField<T> extends Field<T> {

  default @PolyNull T asModelValue(final @PolyNull Object value)
  {
    if (value == null) {
      return null;
    } else {
      return  castNonNull(type().cast(value));
    }
  }

  default @PolyNull Object asDatastoreValue(final @PolyNull T value)
  {
    return value;
  }

  default @Nullable ArrayList<@Nullable Object> asDatastoreValues(final @Nullable T @Nullable ... values)
  {
    if (values == null) {
      return null;
    }
    if (values.length == 0) {
      return new ArrayList<>(1);
    } else {
      final ArrayList<@Nullable Object> result = new ArrayList<>(values.length);
      for (final T value : values) {
        result.add(asDatastoreValue(value));
      }
      return result;
    }
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Iterable<@Nullable T> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result = new ArrayList<>();
    for (final T value : values) {
      result.add(asDatastoreValue(value));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable ArrayList<@Nullable Object> asDatastoreValues(final @Nullable Iterator<@Nullable T> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result = new ArrayList<>();
    while (values.hasNext()) {
      result.add(asDatastoreValue(values.next()));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable ArrayList<@Nullable Object> asDatastoreValues(final @Nullable Collection<@Nullable T> values)
  {
    if (values == null) {
      return null;
    }
    if (values.isEmpty()) {
      return new ArrayList<>(1);
    } else {
      final ArrayList<@Nullable Object> result = new ArrayList<>(values.size());
      for (final T value : values) {
        result.add(asDatastoreValue(value));
      }
      return result;
    }
  }

  default @Nullable List<@Nullable T> asModelValues(final @Nullable Object@Nullable ... values)
  {
    if (values == null) {
      return null;
    }
    if (values.length == 0) {
      return new ArrayList<>();
    } else {
      final ArrayList<@Nullable T> result = new ArrayList<>(values.length);
      for (final Object value : values) {
        result.add(asModelValue(value));
      }
      return result;
    }
  }

  default @Nullable ArrayList<@Nullable T> asModelValues(final @Nullable Iterable<@Nullable Object> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable T> result = new ArrayList<>();
    for (final Object value : values) {
      result.add(asModelValue(value));
    }
    result.trimToSize();
    return result;
  }

  default @Nullable ArrayList<@Nullable T> asModelValues(final @Nullable Iterator<@Nullable Object> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable T> result = new ArrayList<>();
    while (values.hasNext()) {
      result.add(asModelValue(values.next()));
    }
    result.trimToSize();
    return result;
  }

  default @PolyNull ArrayList<@Nullable T> asModelValues(final @PolyNull Collection<@Nullable Object> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable T> result = new ArrayList<>();
    if (!values.isEmpty()) {
      for (final Object value : values) {
        result.add(asModelValue(value));
      }
      result.trimToSize();
    }
    return result;
  }

  @Override default @Nullable T read(final PropertyContainer data)
  {
    final @Nullable Object value = data.getProperty(property());
    return asModelValue(value);
  }

  /**
   * unindexed scalar properties
   */
  abstract class Unindexed<T> extends FieldData<T> implements ScalarField<T> {

    protected Unindexed(final String canonicalName,
                        final String property,
                        final String field,
                        final boolean required,
                        final JsonStringNode jsonName,
                        final String jsonPath,
                        final JsonSerializer<T> jsonSerializer,
                        final ImmutableList<Constraint> constraints)
    {
      super(canonicalName, property, field, required, jsonName, jsonPath, jsonSerializer,
            constraints);
    }

    @Override
    public final boolean indexed()
    {
      return false;
    }

    @Override @SuppressWarnings("nullness")
    public final void write(final PropertyContainer data, final @Nullable T value)
    {
      data.setUnindexedProperty(property(), asDatastoreValue(value));
    }
  }

  /**
   * IndexedBooleanList scalar properties.
   */
  abstract class Indexed<T> extends FieldData<T> implements ScalarField<T>, Filterable<T> {

    private final PropertyProjection projection;
    private final SortPredicate asc;
    private final SortPredicate desc;
    private final FilterPredicate isNull;
    private final FilterPredicate isNotNull;

    protected Indexed(final String canonicalName,
                      final String property,
                      final String field,
                      final boolean required,
                      final JsonStringNode jsonName,
                      final String jsonPath,
                      final JsonSerializer<T> jsonSerializer,
                      final PropertyProjection projection,
                      final ImmutableList<Constraint> constraints)
    {
      super(canonicalName, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
      this.projection = projection;
      this.asc = new Query.SortPredicate(property, Query.SortDirection.ASCENDING);
      this.desc = new Query.SortPredicate(property, Query.SortDirection.DESCENDING);
      this.isNull = makeIsNullFilter(property);
      this.isNotNull = makeIsNotNullFilter(property);
    }

    @SuppressWarnings("nullness") static FilterPredicate makeIsNullFilter(final String property)
    {
      return new FilterPredicate(property, FilterOperator.EQUAL, null);
    }

    @SuppressWarnings("nullness") static FilterPredicate makeIsNotNullFilter(final String property)
    {
      return new FilterPredicate(property, FilterOperator.NOT_EQUAL, null);
    }

    @Override public final boolean indexed()
    {
      return true;
    }

    @Override @SuppressWarnings("nullness") public final void write(final PropertyContainer data, final T value)
    {
      data.setIndexedProperty(property(), asDatastoreValue(value));
    }

    @Override public final PropertyProjection projection()
    {
      return projection;
    }

    @Override public final SortPredicate asc()
    {
      return asc;
    }

    @Override public final SortPredicate desc()
    {
      return desc;
    }

    @Override public final FilterPredicate isNull()
    {
      return isNull;
    }

    @Override public final FilterPredicate isNotNull()
    {
      return isNotNull;
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate eq(final @Nullable T value)
    {
      return new FilterPredicate(field(), FilterOperator.EQUAL, asDatastoreValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate ne(final @Nullable T value)
    {
      return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, asDatastoreValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate lt(final @Nullable T value)
    {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN, asDatastoreValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate le(final @Nullable T value)
    {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, asDatastoreValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate gt(final @Nullable T value)
    {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN, asDatastoreValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate ge(final @Nullable T value)
    {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL,
                                 asDatastoreValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable T @Nullable ... values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable Iterable<@Nullable T> values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable Iterator<@Nullable T> values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable Collection<@Nullable T> values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }
  }
}
