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

public interface ListField<E> extends Field<List<@Nullable E>> {

  @Override default Class<List<@Nullable E>> type()
  {
    return (Class<List<@Nullable E>>) (Class<?>) List.class;
  }

  Class<E> elementType();

  default @PolyNull E asModelElementValue(final @PolyNull Object value)
  {
    if (value == null) {
      return null;
    } else {
      return castNonNull(elementType().cast(value));
    }
  }

  default @PolyNull Object asDatastoreElementValue(final @PolyNull E value)
  {
    return value;
  }

  default @PolyNull ArrayList<@Nullable Object> asDatastoreValues(final @Nullable E @PolyNull ... values)
  {
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

  default @PolyNull List<@Nullable Object> asDatastoreValues(final @PolyNull Iterable<@Nullable E> values)
  {
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

  default @PolyNull List<@Nullable Object> asDatastoreValues(final @PolyNull Iterator<@Nullable E> values)
  {
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

  default @PolyNull List<@Nullable Object> asDatastoreValues(final @PolyNull Collection<@Nullable E> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable Object> result;
    if (values.isEmpty()) {
      result = new ArrayList<>();
    } else {
      result = new ArrayList<>(values.size());
      for (final E value : values) {
        result.add(asDatastoreElementValue(value));
      }
      result.trimToSize();
    }
    return result;
  }

  default @PolyNull List<@Nullable E> asModelValues(final @Nullable Object @PolyNull ... values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable E> result;
    if (values.length == 0) {
      result = new ArrayList<>(1);
    } else {
      result = new ArrayList<>(values.length);
      for (final Object value : values) {
        result.add(asModelElementValue(value));
      }
    }
    return result;
  }

  default @PolyNull List<@Nullable E> asModelValues(final @PolyNull Iterable<@Nullable Object> values)
  {
    if (values == null) {
      return null;
    }
    final ArrayList<@Nullable E> result = new ArrayList<>();
    for (final Object value : values) {
      result.add(asModelElementValue(value));
    }
    result.trimToSize();
    return result;
  }

  default @PolyNull List<@Nullable E> asModelValues(final @PolyNull Iterator<@Nullable Object> values)
  {
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

  default @PolyNull List<@Nullable E> asModelValues(final @PolyNull Collection<@Nullable Object> values)
  {
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

  @Override default @Nullable List<@Nullable E> read(final PropertyContainer data)
  {
    final @Nullable Object values = data.getProperty(property());
    return asModelValues((@Nullable Collection<@Nullable Object>) values);
  }

  /**
   * unindexed list properties
   */
  abstract class Unindexed<E> extends FieldData<List<@Nullable E>> implements ListField<E> {
    public Unindexed(final String canonicalName,
                     final String property,
                     final String field,
                     final boolean required,
                     final JsonStringNode jsonName,
                     final String jsonPath,
                     final JsonSerializer<List<@Nullable E>> jsonSerializer,
                     final ImmutableList<Constraint> constraints)
    {
      super(canonicalName, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
    }

    @Override public final boolean indexed()
    {
      return false;
    }

    @Override @SuppressWarnings("nullness") public final void write(final PropertyContainer data, final List<E> value)
    {
      data.setUnindexedProperty(property(), asDatastoreValues(value));
    }
  }

  /**
   * IndexedBooleanList List properties.
   */
  abstract class Indexed<E> extends FieldData<List<@Nullable E>> implements ListField<E>, Filterable<E> {

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
                      final JsonSerializer<List<@Nullable E>> jsonSerializer,
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

    @SuppressWarnings("nullness") static FilterPredicate makeIsNotNullFilter(final String property)
    {
      return new FilterPredicate(property, FilterOperator.NOT_EQUAL, null);
    }

    @SuppressWarnings("nullness") static FilterPredicate makeIsNullFilter(final String property)
    {
      return new FilterPredicate(property, FilterOperator.EQUAL, null);
    }

    @Override public final FilterPredicate isNull()
    {
      return isNull;
    }

    @Override public final FilterPredicate isNotNull()
    {
      return isNotNull;
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

    @Override public final boolean indexed()
    {
      return true;
    }

    @Override @SuppressWarnings("nullness") public final void write(final PropertyContainer data, final List<@Nullable E> value)
    {
      data.setIndexedProperty(property(), asDatastoreValues(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate eq(final @Nullable E value)
    {
      return new FilterPredicate(field(), FilterOperator.EQUAL, asDatastoreElementValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate ne(final @Nullable E value)
    {
      return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, asDatastoreElementValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate lt(final @Nullable E value)
    {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN, asDatastoreElementValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate le(final @Nullable E value)
    {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, asDatastoreElementValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate gt(final E value)
    {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN, asDatastoreElementValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate ge(final @Nullable E value)
    {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL,
                                 asDatastoreElementValue(value));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable E @Nullable ... values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable Iterable<@Nullable E> values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable Iterator<@Nullable E> values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }

    @Override @SuppressWarnings("nullness") public final FilterPredicate in(final @Nullable Collection<@Nullable E> values)
    {
      return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
    }
  }
}
