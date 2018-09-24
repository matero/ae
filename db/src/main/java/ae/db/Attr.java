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

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.ImmutableList;

public interface Attr extends java.io.Serializable {

        String canonicalName();

        String description();

        String field();

        String property();

        JsonStringNode jsonName();

        String jsonPath();

        boolean isDefinedAt(Entity data);

        default boolean isDefinedAt(final JsonNode json)
        {
                return json.getFields().containsKey(jsonName());
        }

        JsonNode makeJsonValue(Entity data);

        default JsonField makeJsonField(final Entity data)
        {
                return JsonNodeFactories.field(jsonName(), makeJsonValue(data));
        }

        Object interpretJson(JsonNode json);

        ImmutableList<Constraint> constraints();

        void validate(Entity data, Validation validation);
}

abstract class AttrData implements Attr {

        private static final long serialVersionUID = -4466778777529645459L;

        private final String canonicalName;
        private final String description;
        private final String field;
        private final JsonStringNode jsonName;
        private final String jsonPath;
        private final ImmutableList<Constraint> constraints;

        protected AttrData(final String canonicalName,
                           final String description,
                           final String field,
                           final JsonStringNode jsonName,
                           final String jsonPath,
                           final ImmutableList<Constraint> constraints)
        {
                this.canonicalName = canonicalName;
                this.description = description;
                this.field = field;
                this.jsonName = jsonName;
                this.jsonPath = jsonPath;
                this.constraints = constraints;
        }

        @Override
        public final String canonicalName()
        {
                return canonicalName;
        }

        @Override
        public final String description()
        {
                return description;
        }

        @Override
        public final String field()
        {
                return field;
        }

        @Override
        public final JsonStringNode jsonName()
        {
                return jsonName;
        }

        @Override
        public final String jsonPath()
        {
                return jsonPath;
        }

        @Override
        public final ImmutableList<Constraint> constraints()
        {
                return constraints;
        }
}
