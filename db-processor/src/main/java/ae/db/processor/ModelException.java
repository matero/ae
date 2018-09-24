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
package ae.db.processor;

import javax.lang.model.element.Element;

class ModelException extends RuntimeException {

        final Element element;

        ModelException(final String message)
        {
                this(null, message);
        }

        ModelException(final Element element, final String message)
        {
                super(message);
                this.element = element;
        }

        ModelException(final String message, final Throwable cause)
        {
                this(null, message, cause);
        }

        ModelException(final Element element, final String message, final Throwable cause)
        {
                super(message, cause);
                this.element = element;
        }

        ModelException(final Throwable cause)
        {
                this((Element) null, cause);
        }

        ModelException(final Element element, final Throwable cause)
        {
                super(cause);
                this.element = element;
        }

        ModelException(final Element element)
        {
                this.element = element;
        }
}
