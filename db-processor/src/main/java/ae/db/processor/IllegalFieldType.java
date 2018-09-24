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

import com.google.common.collect.Iterables;
import javax.lang.model.element.Element;

final class IllegalFieldType extends ModelException {

        private static final long serialVersionUID = 2016_04_02L;

        IllegalFieldType(final Element element, final String type, final java.util.Set<String> supportedTypes)
        {
                this(element, make_message(type, supportedTypes));
        }

        private static String make_message(final String type, final Iterable<String> supportedTypes)
        {
                final Iterable<String> types = Iterables.transform(supportedTypes, (input) -> {
                                                                   if (input == null || input.trim().isEmpty()) {
                                                                           return "";
                                                                   }
                                                                   return "\n\t" + input;
                                                           });
                return "Type [" + type + "] isn't mappeable. Currently supported types are enums, references, and:" + Iterables.
                        toString(types);
        }

        IllegalFieldType(final Element element, final String message)
        {
                super(element, message);
        }
}
