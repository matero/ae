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
import com.google.appengine.api.datastore.PropertyProjection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Fields to access Long properties at appengine datastore's {@link com.google.appengine.api.datastore.Entity}.
 */
public interface LongField extends ScalarField<Long> {
  /** @return Long class. */
  @Override default @NonNull Class<Long> type() {
    return Long.class;
  }
  /** Metadata to access unindexed Long properties. */
  final class Unindexed extends ScalarField.Unindexed<Long> implements LongField {
    public Unindexed(final @NonNull String canonicalName,
                     final @NonNull String description,
                     final @NonNull String property,
                     final @NonNull String field,
                     final boolean required,
                     final @NonNull JsonStringNode jsonName,
                     final @NonNull String jsonPath,
                     final @Nullable Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, LongJsonSerializer.INSTANCE, constraints);
    }
  }
  /**
   * Metadata to access indexed Long properties, allows to define search criterias and projections.
   */
  final class Indexed extends ScalarField.Indexed<Long> implements LongField {
    public Indexed(final @NonNull String canonicalName,
                   final @NonNull String description,
                   final @NonNull String property,
                   final @NonNull String field,
                   final boolean required,
                   final @NonNull JsonStringNode jsonName,
                   final @NonNull String jsonPath,
                   final @Nullable Constraint... constraints) {
      super(canonicalName,
            description,
            property,
            field,
            required,
            jsonName,
            jsonPath,
            LongJsonSerializer.INSTANCE,
            new PropertyProjection(property, Long.class),
            constraints);
    }
  }
}
