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

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.common.collect.ImmutableList;

public final class IndexedEmail extends ScalarField.Indexed<Email> implements EmailField {

  private static final long serialVersionUID = 6163788779797349171L;

  public IndexedEmail(final String canonicalName,
                      final String property,
                      final String field,
                      final boolean required,
                      final JsonStringNode jsonName,
                      final String jsonPath,
                      final ImmutableList<Constraint> constraints)
  {
    super(canonicalName, property, field, required, jsonName, jsonPath,
          EmailJsonSerializer.INSTANCE,
          new PropertyProjection(property, Email.class), constraints);
  }

  @Override
  protected void validateNotNullValue(final Email value, final Validation validation)
  {
    if (EmailConstraint.ForEmail.INSTANCE.isInvalid(value)) {
      validation.reject(this, EmailConstraint.ForEmail.INSTANCE.messageFor(this, value));
    }
    super.validateNotNullValue(value, validation);
  }
}
