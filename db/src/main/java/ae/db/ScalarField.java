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

/**
 * scalar properties.
 */
public interface ScalarField<T> extends Field<T> {

        default T asModelValue(final Object value)
        {
                return type().cast(value);
        }

        default Object asDatastoreValue(final T value)
        {
                return value;
        }

        default ArrayList<Object> asDatastoreValues(final T... values)
        {
                if (values == null) {
                        return null;
                }
                if (values.length == 0) {
                        return new ArrayList<>();
                } else {
                        final ArrayList<Object> result = new ArrayList<>(values.length);
                        for (final T value : values) {
                                result.add(asDatastoreValue(value));
                        }
                        return result;
                }
        }

        default List<Object> asDatastoreValues(final Iterable<T> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<Object> result = new ArrayList<>();
                for (final T value : values) {
                        result.add(asDatastoreValue(value));
                }
                result.trimToSize();
                return result;
        }

        default ArrayList<Object> asDatastoreValues(final Iterator<T> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<Object> result = new ArrayList<>();
                while (values.hasNext()) {
                        result.add(asDatastoreValue(values.next()));
                }
                result.trimToSize();
                return result;
        }

        default ArrayList<Object> asDatastoreValues(final Collection<T> values)
        {
                if (values == null) {
                        return null;
                }
                if (values.isEmpty()) {
                        return new ArrayList<>();
                } else {
                        final ArrayList<Object> result = new ArrayList<>(values.size());
                        for (final T value : values) {
                                result.add(asDatastoreValue(value));
                        }
                        return result;
                }
        }

        default List<T> asModelValues(final Object... values)
        {
                if (values == null) {
                        return null;
                }
                if (values.length == 0) {
                        return new ArrayList<>();
                } else {
                        final ArrayList<T> result = new ArrayList<>(values.length);
                        for (final Object value : values) {
                                result.add(asModelValue(value));
                        }
                        return result;
                }
        }

        default ArrayList<T> asModelValues(final Iterable<Object> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<T> result = new ArrayList<>();
                for (final Object value : values) {
                        result.add(asModelValue(value));
                }
                result.trimToSize();
                return result;
        }

        default ArrayList<T> asModelValues(final Iterator<Object> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<T> result = new ArrayList<>();
                while (values.hasNext()) {
                        result.add(asModelValue(values.next()));
                }
                result.trimToSize();
                return result;
        }

        default ArrayList<T> asModelValues(final Collection<Object> values)
        {
                if (values == null) {
                        return null;
                }
                final ArrayList<T> result = new ArrayList<>();
                if (!values.isEmpty()) {
                        for (final Object value : values) {
                                result.add(asModelValue(value));
                        }
                        result.trimToSize();
                }
                return result;
        }

        @Override
        default T read(final PropertyContainer data)
        {
                return asModelValue(data.getProperty(property()));
        }

        /**
         * unindexed scalar properties
         */
        abstract class Unindexed<T> extends FieldData<T> implements ScalarField<T> {

                protected Unindexed(final String canonicalName,
                                    final String description,
                                    final String property,
                                    final String field,
                                    final boolean required,
                                    final JsonStringNode jsonName,
                                    final String jsonPath,
                                    final JsonSerializer<T> jsonSerializer,
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
                public final void write(final PropertyContainer data, final T value)
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
                                  final String description,
                                  final String property,
                                  final String field,
                                  final boolean required,
                                  final JsonStringNode jsonName,
                                  final String jsonPath,
                                  final JsonSerializer<T> jsonSerializer,
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
                public final boolean indexed()
                {
                        return true;
                }

                @Override
                public final void write(final PropertyContainer data, final T value)
                {
                        data.setIndexedProperty(property(), asDatastoreValue(value));
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
                public final FilterPredicate eq(final T value)
                {
                        return new FilterPredicate(field(), FilterOperator.EQUAL, asDatastoreValue(value));
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
                public final FilterPredicate ne(final T value)
                {
                        return new FilterPredicate(field(), FilterOperator.NOT_EQUAL, asDatastoreValue(value));
                }

                @Override
                public final FilterPredicate lt(final T value)
                {
                        return new FilterPredicate(field(), FilterOperator.LESS_THAN, asDatastoreValue(value));
                }

                @Override
                public final FilterPredicate le(final T value)
                {
                        return new FilterPredicate(field(), FilterOperator.LESS_THAN_OR_EQUAL, asDatastoreValue(value));
                }

                @Override
                public final FilterPredicate gt(final T value)
                {
                        return new FilterPredicate(field(), FilterOperator.GREATER_THAN, asDatastoreValue(value));
                }

                @Override
                public final FilterPredicate ge(final T value)
                {
                        return new FilterPredicate(field(), FilterOperator.GREATER_THAN_OR_EQUAL,
                                                   asDatastoreValue(value));
                }

                @Override
                public final FilterPredicate in(final T... values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }

                @Override
                public final FilterPredicate in(final Iterable<T> values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }

                @Override
                public final FilterPredicate in(final Iterator<T> values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }

                @Override
                public final FilterPredicate in(final Collection<T> values)
                {
                        return new FilterPredicate(field(), FilterOperator.IN, asDatastoreValues(values));
                }
        }
}
