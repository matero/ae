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

public interface ListField<E> extends Field<List<E>> {

        @Override
        default Class<List<E>> type()
        {
                return (Class<List<E>>) (Class<?>) List.class;
        }

        Class<E> elementType();

        default E asModelElementValue(final Object value)
        {
                return elementType().cast(value);
        }

        default Object asDatastoreElementValue(final E value)
        {
                return value;
        }

        default ArrayList<Object> asDatastoreValues(final E... values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<Object> result;
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

        default List<Object> asDatastoreValues(final Iterable<E> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<Object> result = new ArrayList<>();
                for (final E value : values) {
                        result.add(asDatastoreElementValue(value));
                }
                result.trimToSize();
                return result;
        }

        default List<Object> asDatastoreValues(final Iterator<E> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<Object> result = new ArrayList<>();
                while (values.hasNext()) {
                        result.add(asDatastoreElementValue(values.next()));
                }
                result.trimToSize();
                return result;
        }

        default List<Object> asDatastoreValues(final Collection<E> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<Object> result;
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

        default List<E> asModelValues(final Object... values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<E> result;
                if (values.length == 0) {
                        result = new ArrayList<>();
                } else {
                        result = new ArrayList<>(values.length);
                        for (final Object value : values) {
                                result.add(asModelElementValue(value));
                        }
                }
                return result;
        }

        default List<E> asModelValues(final Iterable<Object> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<E> result = new ArrayList<>();
                for (final Object value : values) {
                        result.add(asModelElementValue(value));
                }
                result.trimToSize();
                return result;
        }

        default List<E> asModelValues(final Iterator<Object> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<E> result = new ArrayList<>();
                while (values.hasNext()) {
                        result.add(asModelElementValue(values.next()));
                }
                result.trimToSize();
                return result;
        }

        default List<E> asModelValues(final Collection<Object> values)
        {
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

        @Override
        default List<E> read(final PropertyContainer data)
        {
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
                                 final JsonSerializer<List<E>> jsonSerializer,
                                 final ImmutableList<Constraint> constraints)
                {
                        super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer,
                              constraints);
                }

                @Override
                public final boolean indexed()
                {
                        return false;
                }

                @Override
                public final void write(final PropertyContainer data, final List<E> value)
                {
                        data.setUnindexedProperty(property(), asDatastoreValues(value));
                }
        }

        /**
         * IndexedBooleanList List properties.
         */
        abstract class Indexed<E> extends FieldData<List<E>> implements ListField<E>, Filterable<E> {

                private final PropertyProjection projection;
                private final SortPredicate asc;
                private final SortPredicate desc;
                private final FilterPredicate isNull;
                private final FilterPredicate isNotNull;

                protected Indexed(final String canonicalName,
                                  final String description,
                                  final String property,
                                  final String field,
                                  final boolean required,
                                  final JsonStringNode jsonName,
                                  final String jsonPath,
                                  final JsonSerializer<List<E>> jsonSerializer,
                                  final PropertyProjection projection,
                                  final ImmutableList<Constraint> constraints)
                {
                        super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer,
                              constraints);
                        this.projection = projection;
                        this.asc = new Query.SortPredicate(property, Query.SortDirection.ASCENDING);
                        this.desc = new Query.SortPredicate(property, Query.SortDirection.DESCENDING);
                        this.isNull = new FilterPredicate(property, FilterOperator.EQUAL, null);
                        this.isNotNull = new FilterPredicate(property, FilterOperator.NOT_EQUAL, null);
                }

                @Override
                public final FilterPredicate isNull()
                {
                        return isNull;
                }

                @Override
                public final FilterPredicate isNotNull()
                {
                        return isNotNull;
                }

                @Override
                public final PropertyProjection projection()
                {
                        return projection;
                }

                @Override
                public final SortPredicate asc()
                {
                        return asc;
                }

                @Override
                public final SortPredicate desc()
                {
                        return desc;
                }

                @Override
                public final boolean indexed()
                {
                        return true;
                }

                @Override
                public final void write(final PropertyContainer data, final List<E> value)
                {
                        data.setIndexedProperty(property(), asDatastoreValues(value));
                }

                @Override
                public final FilterPredicate eq(final E value)
                {
                        return new FilterPredicate(field(), FilterOperator.EQUAL, asDatastoreElementValue(value));
                }

                @Override
                public final FilterPredicate ne(final E value)
                {
                        return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, asDatastoreElementValue(value));
                }

                @Override
                public final FilterPredicate lt(final E value)
                {
                        return new FilterPredicate(field(), FilterOperator.LESS_THAN, asDatastoreElementValue(value));
                }

                @Override
                public final FilterPredicate le(final E value)
                {
                        return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, asDatastoreElementValue(
                                                   value));
                }

                @Override
                public final FilterPredicate gt(final E value)
                {
                        return new FilterPredicate(field(), FilterOperator.GREATER_THAN, asDatastoreElementValue(value));
                }

                @Override
                public final FilterPredicate ge(final E value)
                {
                        return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL,
                                                   asDatastoreElementValue(value));
                }

                @Override
                public final FilterPredicate in(final E... values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }

                @Override
                public final FilterPredicate in(final Iterable<E> values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }

                @Override
                public final FilterPredicate in(final Iterator<E> values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }

                @Override
                public final FilterPredicate in(final Collection<E> values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }
        }
}
