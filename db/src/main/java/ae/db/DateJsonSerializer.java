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
import argo.jdom.JsonNodeFactories;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateJsonSerializer implements JsonSerializer<Date> {

        private static final long serialVersionUID = -4321000415258159924L;

        private final String datePattern;

        public DateJsonSerializer(final String datePattern)
        {
                this.datePattern = datePattern;
        }

        @Override
        public JsonNode toJson(final Date value)
        {
                if (value == null) {
                        return JsonNodeFactories.nullNode();
                }
                return JsonNodeFactories.string(new SimpleDateFormat(datePattern).format(value));
        }

        @Override
        public Date fromJson(final JsonNode json, final String jsonPath)
        {
                if (json.isNullNode(jsonPath)) {
                        return null;
                } else {
                        final String value = json.getStringValue(jsonPath);
                        final SimpleDateFormat parser = new SimpleDateFormat(datePattern);
                        try {
                                return parser.parse(value);
                        } catch (final ParseException e) {
                                throw new IllegalArgumentException("Could not interpret '" + value + "' as date.", e);
                        }
                }
        }

        @Override
        public Date fromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                } else {
                        final String value = json.getStringValue();
                        final SimpleDateFormat parser = new SimpleDateFormat(datePattern);
                        try {
                                return parser.parse(value);
                        } catch (final ParseException e) {
                                throw new IllegalArgumentException("Could not interpret '" + value + "' as date.", e);
                        }
                }
        }
}
