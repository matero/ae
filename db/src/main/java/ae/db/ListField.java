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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.UnaryOperator;

public interface ListField<E> extends Field<List<E>> {
  default int sizeAt(final PropertyContainer data) {
    return read(data).size();
  }

  default boolean isEmptyAt(final PropertyContainer data) {
    return read(data).isEmpty();
  }

  default boolean containsAt(final PropertyContainer data, final E element) {
    return read(data).contains(element);
  }

  default Iterator<E> iteratorAt(final PropertyContainer data, final E element) {
    return read(data).iterator();
  }

  default E[] toArrayAt(final PropertyContainer data) {
    return (E[]) read(data).toArray();
  }

  default E[] toArrayAt(final PropertyContainer data, final E[] array) {
    return read(data).toArray(array);
  }

  default boolean addAt(final PropertyContainer data, final E e) {
    return read(data).add(e);
  }

  default boolean removeAt(final PropertyContainer data, final E e) {
    return read(data).remove(e);
  }

  default boolean containsAllAt(final PropertyContainer data, final Collection<?> c) {
    return read(data).containsAll(c);
  }

  default boolean addAllAt(final PropertyContainer data, final Collection<? extends E> c) {
    return read(data).addAll(c);
  }

  default boolean addAllAt(final PropertyContainer data, final int index, final Collection<? extends E> c) {
    return read(data).addAll(index, c);
  }

  default boolean removeAllAt(final PropertyContainer data, final Collection<?> c) {
    return read(data).removeAll(c);
  }

  default boolean retainAllAt(final PropertyContainer data, final Collection<?> c) {
    return read(data).removeAll(c);
  }

  default void replaceAllAt(final PropertyContainer data, final UnaryOperator<E> operator) {
    read(data).replaceAll(operator);
  }

  default void sortAt(final PropertyContainer data, final Comparator<? super E> c) {
    read(data).sort(c);
  }

  default void clearAt(final PropertyContainer data) {
    read(data).clear();
  }

  // Positional Access Operations
  default E getAt(final PropertyContainer data, final int index) {
    return read(data).get(index);
  }

  default E setAt(final PropertyContainer data, final int index, final E element) {
    return read(data).set(index, element);
  }

  default void addAt(final PropertyContainer data, final int index, final E element) {
    read(data).add(index, element);
  }

  default E removeAt(final PropertyContainer data, final int index) {
    return read(data).remove(index);
  }

  default int indexOfAt(final PropertyContainer data, final Object o) {
    return read(data).indexOf(o);
  }

  default int lastIndexOfAt(final PropertyContainer data, final Object o) {
    return read(data).lastIndexOf(o);
  }

  default ListIterator<E> listIteratorAt(final PropertyContainer data) {
    return read(data).listIterator();
  }

  default ListIterator<E> listIteratorAt(final PropertyContainer data, final int index) {
    return read(data).listIterator(index);
  }

  default List<E> subListAt(final PropertyContainer data, final int fromIndex, final int toIndex) {
    return read(data).subList(fromIndex, toIndex);
  }

  default Spliterator<E> spliteratorAt(final PropertyContainer data) {
    return read(data).spliterator();
  }

  @Override default Class<List<E>> type() {
    return (Class<List<E>>) (Class<?>) List.class;
  }

  Class<E> elementType();

  default E asModelElementValue(final Object value) {
    return elementType().cast(value);
  }

  default Object asDatastoreElementValue(final E value) {
    return value;
  }

  default List<Object> asDatastoreValues(final E... values) {
    if (values == null) {
      return null;
    }
    final List<Object> result;
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

  default List<Object> asDatastoreValues(final Iterable<E> values) {
    if (values == null) {
      return null;
    }
    final List<Object> result = new java.util.LinkedList<>();
    final java.util.Iterator<E> iter = values.iterator();
    if (iter.hasNext()) {
      while (iter.hasNext()) {
        result.add(asDatastoreElementValue(iter.next()));
      }
    }
    return result;
  }

  default List<Object> asDatastoreValues(final java.util.Iterator<E> values) {
    if (values == null) {
      return null;
    }
    final List<Object> result = new java.util.LinkedList<>();
    while (values.hasNext()) {
      result.add(asDatastoreElementValue(values.next()));
    }
    return result;
  }

  default List<Object> asDatastoreValues(final Collection<E> values) {
    if (values == null) {
      return null;
    }
    final List<Object> result;
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

  default List<E> asModelValues(final Object... values) {
    if (values == null) {
      return null;
    }
    final List<E> result;
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

  default List<E> asModelValues(final Iterable<Object> values) {
    if (values == null) {
      return null;
    }
    final List<E> result = new java.util.LinkedList<>();
    final java.util.Iterator<Object> iter = values.iterator();
    if (iter.hasNext()) {
      while (iter.hasNext()) {
        result.add(asModelElementValue(iter.next()));
      }
    }
    return result;
  }

  default List<E> asModelValues(final java.util.Iterator<Object> values) {
    if (values == null) {
      return null;
    }
    final List<E> result = new java.util.LinkedList<>();
    while (values.hasNext()) {
      result.add(asModelElementValue(values.next()));
    }
    return result;
  }

  default List<E> asModelValues(final Collection<Object> values) {
    if (values == null) {
      return null;
    }
    final List<E> result;
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

  @Override default List<E> read(final PropertyContainer data) {
    if (data == null) {
      throw new NullPointerException("data");
    }
    final Object values = data.getProperty(property());
    return asModelValues((Collection<Object>) values);
  }

  /**
   * unindexed list properties
   */
  abstract class Unindexed<E> extends FieldData<List<E>> implements ListField<E> {
    public Unindexed(final String canonicalName,
                     final String description,
                     final String property,
                     final String field,
                     final boolean required,
                     final JsonStringNode jsonName,
                     final String jsonPath,
                     final Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, constraints);
    }

    @Override public final boolean indexed() {
      return false;
    }

    @Override public final void write(final PropertyContainer data, final List<E> value) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      data.setUnindexedProperty(property(), asDatastoreValues(value));
    }
  }

  /**
   * Indexed List properties.
   */
  abstract class Indexed<E> extends FieldData<List<E>> implements ListField<E>, Filterable<E> {
    private final PropertyProjection projection;
    private final Query.SortPredicate asc;
    private final Query.SortPredicate desc;
    private final Query.FilterPredicate isNull;
    private final Query.FilterPredicate isNotNull;

    protected Indexed(final String canonicalName,
                      final String description,
                      final String property,
                      final String field,
                      final boolean required,
                      final JsonStringNode jsonName,
                      final String jsonPath,
                      final PropertyProjection projection,
                      final Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, constraints);
      this.projection = projection;
      this.asc = new Query.SortPredicate(property, Query.SortDirection.ASCENDING);
      this.desc = new Query.SortPredicate(property, Query.SortDirection.DESCENDING);
      this.isNull = new Query.FilterPredicate(property, Query.FilterOperator.EQUAL, null);
      this.isNotNull = new Query.FilterPredicate(property, Query.FilterOperator.NOT_EQUAL, null);
    }

    @Override public final Query.FilterPredicate isNull() {
      return isNull;
    }

    @Override public final Query.FilterPredicate isNotNull() {
      return isNotNull;
    }

    @Override public final PropertyProjection projection() {
      return projection;
    }

    @Override public final Query.SortPredicate asc() {
      return asc;
    }

    @Override public Query.SortPredicate desc() {
      return desc;
    }

    @Override public final boolean indexed() {
      return true;
    }

    @Override public final void write(final PropertyContainer data, final List<E> value) {
      if (data == null) {
        throw new NullPointerException("data");
      }
      data.setIndexedProperty(property(), asDatastoreValues(value));
    }

    @Override public final Query.FilterPredicate eq(final E value) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.EQUAL, asDatastoreElementValue(value));
    }

    @Override public final Query.FilterPredicate ne(final E value) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.NOT_EQUAL, asDatastoreElementValue(value));
    }

    @Override public final Query.FilterPredicate lt(final E value) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.LESS_THAN, asDatastoreElementValue(value));
    }

    @Override public final Query.FilterPredicate le(final E value) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.LESS_THAN_OR_EQUAL, asDatastoreElementValue(value));
    }

    @Override public final Query.FilterPredicate gt(final E value) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.GREATER_THAN, asDatastoreElementValue(value));
    }

    @Override public final Query.FilterPredicate ge(final E value) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.GREATER_THAN_OR_EQUAL, asDatastoreElementValue(value));
    }

    @Override public final Query.FilterPredicate in(final E... values) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.IN, asDatastoreValues(values));
    }

    @Override public final Query.FilterPredicate in(final Iterable<E> values) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.IN, asDatastoreValues(values));
    }

    @Override public final Query.FilterPredicate in(final java.util.Iterator<E> values) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.IN, asDatastoreValues(values));
    }

    @Override public final Query.FilterPredicate in(final Collection<E> values) {
      return new Query.FilterPredicate(field(), Query.FilterOperator.IN, asDatastoreValues(values));
    }
  }
}
