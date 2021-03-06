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

import com.google.appengine.api.datastore.Text;

public final class NotBlankConstraint {

  private NotBlankConstraint()
  {
    throw new UnsupportedOperationException();
  }

  private static final String NAME = "notBlank";

  private static String makeMessageFor(final Attribute accessor)
  {
    return accessor.field() + " no debe ser vacio o solo blancos";
  }

  public enum ForString implements Constraint<String> {
    INSTANCE;

    @Override
    public boolean isInvalid(final String value)
    {
      return value.trim().isEmpty();
    }

    @Override
    public String messageFor(final Attribute attr, final String value)
    {
      return makeMessageFor(attr);
    }

    @Override
    public String getName()
    {
      return NAME;
    }
  }

  public enum ForText implements Constraint<Text> {
    INSTANCE;

    @Override
    public boolean isInvalid(final Text value)
    {
      return value.getValue().trim().isEmpty();
    }

    @Override
    public String messageFor(final Attribute attr, final Text value)
    {
      return makeMessageFor(attr);
    }

    @Override
    public String getName()
    {
      return NAME;
    }
  }
}
