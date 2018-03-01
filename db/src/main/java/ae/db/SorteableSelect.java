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

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import java.util.Iterator;

public abstract class SorteableSelect extends Select {
  private static final long serialVersionUID = 8141301362335037541L;

  SorteableSelect(final Query query,
                  final FetchOptions fetchOptions) {
    super(query, fetchOptions);
  }

  public final Select sortedBy(final Query.SortPredicate firstSort,
                               final Query.SortPredicate... extraSorts) {
    query.addSort(firstSort.getPropertyName(), firstSort.getDirection());
    if (extraSorts.length > 0) {
      for (final Query.SortPredicate sort : extraSorts) {
        query.addSort(sort.getPropertyName(), sort.getDirection());
      }
    }
    return this;
  }

  public final Select sortedBy(final Query.SortPredicate sort) {
    query.addSort(sort.getPropertyName(), sort.getDirection());
    return this;
  }

  public final Select sortedBy(final Query.SortPredicate firstSort,
                               final Query.SortPredicate secondSort) {
    query.addSort(firstSort.getPropertyName(), firstSort.getDirection());
    query.addSort(secondSort.getPropertyName(), secondSort.getDirection());
    return this;
  }

  public final Select sortedBy(final Query.SortPredicate firstSort,
                               final Query.SortPredicate secondSort,
                               final Query.SortPredicate thirdSort) {
    query.addSort(firstSort.getPropertyName(), firstSort.getDirection());
    query.addSort(secondSort.getPropertyName(), secondSort.getDirection());
    query.addSort(thirdSort.getPropertyName(), thirdSort.getDirection());
    return this;
  }

  public final Select sortedBy(final Query.SortPredicate firstSort,
                               final Query.SortPredicate secondSort,
                               final Query.SortPredicate thirdSort,
                               final Query.SortPredicate... otherSorts) {
    query.addSort(firstSort.getPropertyName(), firstSort.getDirection());
    query.addSort(secondSort.getPropertyName(), secondSort.getDirection());
    query.addSort(thirdSort.getPropertyName(), thirdSort.getDirection());
    for (final Query.SortPredicate sort : otherSorts) {
      query.addSort(sort.getPropertyName(), sort.getDirection());
    }
    return this;
  }

  public final Select sortedBy(final Iterable<Query.SortPredicate> sorts) {
    final Iterator<Query.SortPredicate> i = sorts.iterator();
    while (i.hasNext()) {
      final Query.SortPredicate sort = i.next();
      query.addSort(sort.getPropertyName(), sort.getDirection());
    }
    return this;
  }
}
