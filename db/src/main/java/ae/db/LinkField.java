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

import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PropertyContainer;

public interface LinkField extends ScalarField<Link> {

        @Override
        default Class<Link> type()
        {
                return Link.class;
        }

        default void set(final PropertyContainer data, final CharSequence rawValue)
        {
                write(data, rawValue);
        }

        default void write(final PropertyContainer data, final CharSequence rawValue)
        {
                if (rawValue == null) {
                        write(data, (Link) null);
                } else {
                        write(data, new Link(rawValue.toString()));
                }
        }

        default void set(final PropertyContainer data, final String rawValue)
        {
                write(data, rawValue);
        }

        default void write(final PropertyContainer data, final String rawValue)
        {
                if (rawValue == null) {
                        write(data, (Link) null);
                } else {
                        write(data, new Link(rawValue));
                }
        }
}
