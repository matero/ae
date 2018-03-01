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

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.PropertyProjection;

public interface EmailField extends ScalarField<Email> {
  @Override default Class<Email> type() {
    return Email.class;
  }

  @Override default JsonNode makeJsonValue(final Email value) {
    return EmailJsonSerializer.INSTANCE.toJson(value);
  }

  @Override default Email interpretJson(final JsonNode json) {
    if (json == null) {
      throw new NullPointerException("json");
    }
    return EmailJsonSerializer.INSTANCE.fromJson(json, jsonPath());
  }

  final class Unindexed extends ScalarField.Unindexed<Email> implements EmailField {
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

    @Override protected void validateNotNullValue(final Email value, final Validation validation) {
      if (EmailConstraint.ForEmail.INSTANCE.isInvalid(value)) {
        validation.reject(this, EmailConstraint.ForEmail.INSTANCE.messageFor(this, value));
      }
      super.validateNotNullValue(value, validation);
    }
  }

  final class Indexed extends ScalarField.Indexed<Email> implements EmailField {
    public Indexed(final String canonicalName,
                   final String description,
                   final String property,
                   final String field,
                   final boolean required,
                   final JsonStringNode jsonName,
                   final String jsonPath,
                   final Constraint... constraints) {
      super(canonicalName, description, property, field, required, jsonName, jsonPath, new PropertyProjection(property, Email.class), constraints);
    }

    @Override protected void validateNotNullValue(final Email value, final Validation validation) {
      if (EmailConstraint.ForEmail.INSTANCE.isInvalid(value)) {
        validation.reject(this, EmailConstraint.ForEmail.INSTANCE.messageFor(this, value));
      }
      super.validateNotNullValue(value, validation);
    }
  }
}
