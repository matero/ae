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

import static org.checkerframework.checker.nullness.NullnessUtil.*;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

public interface ListField<E> extends Field<List<@Nullable E>> {
  default int sizeAt(final @NonNull PropertyContainer data) {
    return read(data).size();
  }

  default boolean isEmptyAt(final @NonNull PropertyContainer data) {
    return read(data).isEmpty();
  }

  default boolean containsAt(final @NonNull PropertyContainer data, final @Nullable E element) {
    return read(data).contains(element);
  }

  default @Nullable ListIterator<@Nullable E> iteratorAt(final @NonNull PropertyContainer data) {
    final @Nullable List<E> list = read(data);
    return list == null ? null : list.listIterator();
  }

  default @Nullable E[] toArrayAt(final @NonNull PropertyContainer data) {
    final @Nullable List<E> list = read(data);
    return list == null ? null : (E[]) read(data).toArray();
  }

  default @Nullable E[] toArrayAt(final @NonNull PropertyContainer data, final @NonNull E[] array) {
    final @Nullable List<E> list = read(data);
    return list == null ? null : list.toArray(array);
  }

  default boolean addAt(final @NonNull PropertyContainer data, final @Nullable E e) {
    return read(data).add(e);
  }

  default boolean removeAt(final @NonNull PropertyContainer data, final @Nullable E e) {
    return read(data).remove(e);
  }

  default boolean containsAllAt(final @NonNull PropertyContainer data, final @NonNull Collection<?> c) {
    return read(data).containsAll(c);
  }

  default boolean addAllAt(final @NonNull PropertyContainer data, final @NonNull Collection<? extends E> c) {
    return read(data).addAll(c);
  }

  default boolean addAllAt(final @NonNull PropertyContainer data, final int index, final @NonNull Collection<? extends E> c) {
    return read(data).addAll(index, c);
  }

  default boolean removeAllAt(final @NonNull PropertyContainer data, final @NonNull Collection<?> c) {
    return read(data).removeAll(c);
  }

  default boolean retainAllAt(final @NonNull PropertyContainer data, final @NonNull Collection<?> c) {
    return read(data).removeAll(c);
  }

  default void replaceAllAt(final @NonNull PropertyContainer data, final @NonNull UnaryOperator<E> operator) {
    read(data).replaceAll(operator);
  }

  default void sortAt(final @NonNull PropertyContainer data, final @NonNull Comparator<? super E> c) {
    read(data).sort(c);
  }

  default void clearAt(final @NonNull PropertyContainer data) {
    read(data).clear();
  }

  // Positional Access Operations
  default E getAt(final @NonNull PropertyContainer data, final int index) {
    return read(data).get(index);
  }

  default E setAt(final @NonNull PropertyContainer data, final int index, final E element) {
    return read(data).set(index, element);
  }

  default void addAt(final @NonNull PropertyContainer data, final int index, final E element) {
    read(data).add(index, element);
  }

  default E removeAt(final @NonNull PropertyContainer data, final int index) {
    return read(data).remove(index);
  }

  default int indexOfAt(final @NonNull PropertyContainer data, final @Nullable Object o) {
    return read(data).indexOf(o);
  }

  default int lastIndexOfAt(final @NonNull PropertyContainer data, final @Nullable Object o) {
    return read(data).lastIndexOf(o);
  }

  default ListIterator<E> listIteratorAt(final @NonNull PropertyContainer data) {
    return read(data).listIterator();
  }

  default ListIterator<E> listIteratorAt(final @NonNull PropertyContainer data, final int index) {
    return read(data).listIterator(index);
  }

  default List<E> subListAt(final @NonNull PropertyContainer data, final int fromIndex, final int toIndex) {
    return read(data).subList(fromIndex, toIndex);
  }

  default Spliterator<E> spliteratorAt(final @NonNull PropertyContainer data) {
    return read(data).spliterator();
  }

  @Override default @NonNull Class<List<E>> type() {
    return (Class<List<E>>) (Class<?>) List.class;
  }

  @NonNull Class<E> elementType();

  default @Nullable E asModelElementValue(final @Nullable Object value) {
    return elementType().cast(value);
  }

  default @Nullable Object asDatastoreElementValue(final @Nullable E value) {
    return value;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable E... values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result;
    if (values.length == 0) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.length);
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
    final List<@Nullable Object> result   = new java.util.LinkedList<>();
    final Iterator<@Nullable E>  iterator = values.iterator();
    if (iterator.hasNext()) {
      while (iterator.hasNext()) {
        result.add(asDatastoreElementValue(iterator.next()));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Iterator<@Nullable E> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result = new java.util.LinkedList<>();
    while (values.hasNext()) {
      result.add(asDatastoreElementValue(values.next()));
    }
    return result;
  }

  default @Nullable List<@Nullable Object> asDatastoreValues(final @Nullable Collection<@Nullable E> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable Object> result;
    if (values.isEmpty()) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.size());
      for (final E value : values) {
        result.add(asDatastoreElementValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Object... values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable E> result;
    if (values.length == 0) {
      result = new java.util.LinkedList<>();
    } else {
      result = new java.util.ArrayList<>(values.length);
      for (final Object value : values) {
        result.add(asModelElementValue(value));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Iterable<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable E>          result = new java.util.LinkedList<>();
    final Iterator<@Nullable Object> iter   = values.iterator();
    if (iter.hasNext()) {
      while (iter.hasNext()) {
        result.add(asModelElementValue(iter.next()));
      }
    }
    return result;
  }

  default @Nullable List<@Nullable E> asModelValues(final @Nullable Iterator<@Nullable Object> values) {
    if (values == null) {
      return null;
    }
    final List<@Nullable E> result = new java.util.LinkedList<>();
    while (values.hasNext()) {
      result.add(asModelElementValue(values.next()));
    }
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

  @Override default @Nullable List<@Nullable E> read(final @NonNull PropertyContainer data) {
    final @Nullable Object values = data.getProperty(property());
    return asModelValues((Collection<Object>) values);
  }

  /**
   * unindexed list properties
   */
  abstract class Unindexed<E> extends FieldData<List<E>> implements ListField<E> {
    public Unindexed(final @NonNull String canonicalName,
                     final @NonNull String description,
                     final @NonNull String property,
                     final @NonNull String field,
                     final boolean required,
                     final @NonNull JsonStringNode jsonName,
                     final @NonNull String jsonPath,
                     final @NonNull JsonSerializer<List<@Nullable E>> jsonSerializer,
                     final @Nullable Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
    }

    @Override public final boolean indexed() {
      return false;
    }

    @Override public final void write(final @NonNull PropertyContainer data, final @Nullable List<@Nullable E> value) {
      data.setUnindexedProperty(property(), castNonNull(asDatastoreValues(value)));
    }
  }

  /**
   * IndexedBooleanList List properties.
   */
  abstract class Indexed<E> extends FieldData<List<E>> implements ListField<E>, Filterable<E> {
    private final @NonNull PropertyProjection projection;
    private final @NonNull SortPredicate      asc;
    private final @NonNull SortPredicate      desc;
    private final @NonNull FilterPredicate    isNull;
    private final @NonNull FilterPredicate    isNotNull;

    protected Indexed(final @NonNull String canonicalName,
                      final @NonNull String description,
                      final @NonNull String property,
                      final @NonNull String field,
                      final boolean required,
                      final @NonNull JsonStringNode jsonName,
                      final @NonNull String jsonPath,
                      final @NonNull JsonSerializer<List<@Nullable E>> jsonSerializer,
                      final @NonNull PropertyProjection projection,
                      final @Nullable Constraint... constraints) {
      // /home/jj/Code/ae/db/src/main/java/ae/db/ListField.java:[328,88] [argument.type.incompatible] incompatible types in argument.
      //   found   : @Initialized @NonNull JsonSerializer<@Initialized @NonNull List<E[ extends @Initialized @Nullable Object super @Initialized
      // @Nullable Void]>>
      //   required: @Initialized @NonNull JsonSerializer<@Initialized @NonNull List<E[ extends @Initialized @Nullable Object super @Initialized
      // @NonNull Void]>>
      super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
      this.projection = projection;
      this.asc = new Query.SortPredicate(property, Query.SortDirection.ASCENDING);
      this.desc = new Query.SortPredicate(property, Query.SortDirection.DESCENDING);
      this.isNull = new FilterPredicate(property, FilterOperator.EQUAL, castNonNull(null));
      this.isNotNull = new FilterPredicate(property, FilterOperator.NOT_EQUAL, castNonNull(null));
    }

    @Override public final @NonNull FilterPredicate isNull() {
      return isNull;
    }

    @Override public final @NonNull FilterPredicate isNotNull() {
      return isNotNull;
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

    @Override public final boolean indexed() {
      return true;
    }

    @Override public final void write(final @NonNull PropertyContainer data, final @Nullable List<@Nullable E> value) {
      data.setIndexedProperty(property(), castNonNull(asDatastoreValues(value)));
    }

    @Override public final @NonNull FilterPredicate eq(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final @NonNull FilterPredicate ne(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final @NonNull FilterPredicate lt(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final @NonNull FilterPredicate le(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final @NonNull FilterPredicate gt(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final @NonNull FilterPredicate ge(final @Nullable E value) {
      return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL, castNonNull(asDatastoreElementValue(value)));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable E... values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable Iterable<@Nullable E> values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable Iterator<@Nullable E> values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }

    @Override public final @NonNull FilterPredicate in(final @Nullable Collection<@Nullable E> values) {
      return new FilterPredicate(field(), FilterOperator.IN, castNonNull(asDatastoreValues(values)));
    }
  }
}
