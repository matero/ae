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

import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortPredicate;

import java.util.Collection;
import java.util.Iterator;

public interface Filterable<T> extends java.io.Serializable {

        PropertyProjection projection();

        SortPredicate asc();

        SortPredicate desc();

        FilterPredicate isNull();

        FilterPredicate isNotNull();

        FilterPredicate eq(T value);

        FilterPredicate ne(T value);

        FilterPredicate lt(T value);

        FilterPredicate le(T value);

        FilterPredicate gt(T value);

        FilterPredicate ge(T value);

        FilterPredicate in(T... values);

        FilterPredicate in(Iterable<T> values);

        FilterPredicate in(Iterator<T> values);

        FilterPredicate in(Collection<T> values);
}
