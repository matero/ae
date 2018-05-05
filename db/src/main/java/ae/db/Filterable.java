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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Iterator;

public interface Filterable<T> extends java.io.Serializable {
  @NonNull PropertyProjection projection();

  @NonNull SortPredicate asc();

  @NonNull SortPredicate desc();

  @NonNull FilterPredicate isNull();

  @NonNull FilterPredicate isNotNull();

  @NonNull FilterPredicate eq(@Nullable T value);

  @NonNull FilterPredicate ne(@Nullable T value);

  @NonNull FilterPredicate lt(@Nullable T value);

  @NonNull FilterPredicate le(@Nullable T value);

  @NonNull FilterPredicate gt(@Nullable T value);

  @NonNull FilterPredicate ge(@Nullable T value);

  @NonNull FilterPredicate in(@Nullable T... values);

  @NonNull FilterPredicate in(@Nullable Iterable<@Nullable T> values);

  @NonNull FilterPredicate in(@Nullable Iterator<@Nullable T> values);

  @NonNull FilterPredicate in(@Nullable Collection<@Nullable T> values);
}
