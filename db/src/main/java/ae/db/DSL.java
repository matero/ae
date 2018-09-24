/*
 * The MIT License
 *
 * Copyright 2018 jj.
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

import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class DSL {

        private DSL()
        {
                throw new UnsupportedOperationException();
        }

        /* methods to improve redeability on generated code */
        public static String canonicalName(final String value)
        {
                return value;
        }

        public static String description(final String value)
        {
                return value;
        }

        public static String propertyName(final String value)
        {
                return value;
        }

        public static String fieldName(final String value)
        {
                return value;
        }
        public static final boolean required = true;
        public static final boolean nullable = false;

        public static JsonStringNode jsonName(final String value)
        {
                return JsonNodeFactories.string(value);
        }

        public static String jsonPath(final String value)
        {
                return value;
        }
        public static final ImmutableList<Constraint> noConstraints = ImmutableList.of();

        public static final ImmutableList<Constraint> constraints(final Constraint constraint)
        {
                return ImmutableList.of(constraint);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2)
        {
                return ImmutableList.of(c1, c2);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3)
        {
                return ImmutableList.of(c1, c2, c3);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4)
        {
                return ImmutableList.of(c1, c2, c3, c4);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6,
                                                                  final Constraint c7)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6, c7);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6,
                                                                  final Constraint c7, final Constraint c8)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6, c7, c8);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6,
                                                                  final Constraint c7, final Constraint c8,
                                                                  final Constraint c9)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6, c7, c8, c9);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6,
                                                                  final Constraint c7, final Constraint c8,
                                                                  final Constraint c9, final Constraint c10)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
        }

        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6,
                                                                  final Constraint c7, final Constraint c8,
                                                                  final Constraint c9, final Constraint c10,
                                                                  final Constraint c11)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11);
        }

        @SafeVarargs
        public static final ImmutableList<Constraint> constraints(final Constraint c1, final Constraint c2,
                                                                  final Constraint c3, final Constraint c4,
                                                                  final Constraint c5, final Constraint c6,
                                                                  final Constraint c7, final Constraint c8,
                                                                  final Constraint c9, final Constraint c10,
                                                                  final Constraint c11, final Constraint c12,
                                                                  final Constraint... otherConstraints)
        {
                return ImmutableList.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, otherConstraints);
        }
}
