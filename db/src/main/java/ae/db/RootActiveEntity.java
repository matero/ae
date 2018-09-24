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

public abstract class RootActiveEntity extends ActiveEntity {

        private static final long serialVersionUID = -7910506513755179689L;

        /**
         * Constructs a ROOT active entity defining its kind.
         *
         * @param kind Kind of the active entity.
         */
        protected RootActiveEntity(final String kind)
        {
                super(kind);
        }

        /* **************************************************************************
   * query building facilities
         */
        public final SelectEntities selectAll()
        {
                return selectAll(FetchOptions.Builder.withDefaults());
        }

        public final SelectEntities selectAll(final FetchOptions fetchOptions)
        {
                return new SelectEntities(makeQuery(), fetchOptions);
        }

        public final SelectEntities selectKeys()
        {
                return selectKeys(FetchOptions.Builder.withDefaults());
        }

        public final SelectEntities selectKeys(final FetchOptions fetchOptions)
        {
                return new SelectEntities(makeQuery().setKeysOnly(), fetchOptions);
        }

        public final SelectEntities select(final Filterable<?>... projectedProperties)
        {
                return select(FetchOptions.Builder.withDefaults(), projectedProperties);
        }

        public final SelectEntities select(final FetchOptions fetchOptions, final Filterable<?>... projectedProperties)
        {
                return new SelectEntities(projection(projectedProperties), fetchOptions);
        }

        public final SelectEntities select(final Iterable<Filterable<?>> projectedProperties)
        {
                return select(FetchOptions.Builder.withDefaults(), projectedProperties);
        }

        public final SelectEntities select(final FetchOptions fetchOptions,
                                           final Iterable<Filterable<?>> projectedProperties)
        {
                return new SelectEntities(projection(projectedProperties), fetchOptions);
        }
}
