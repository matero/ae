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
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.common.collect.ImmutableList;

public interface Field<T> extends Attr {

        @Override
        default boolean isDefinedAt(final Entity data)
        {
                return data.hasProperty(property());
        }

        default boolean isDefinedAt(final PropertyContainer data)
        {
                return data.hasProperty(property());
        }

        Class<T> type();

        boolean indexed();

        boolean required();

        default T of(final PropertyContainer data)
        {
                return read(data);
        }

        default T get(final PropertyContainer data)
        {
                return read(data);
        }

        default String str(final PropertyContainer data)
        {
                final Object value = read(data);
                if (value == null) {
                        return null;
                } else {
                        return value.toString();
                }
        }

        T read(final PropertyContainer data);

        default void set(final PropertyContainer data, final T value)
        {
                write(data, value);
        }

        void write(PropertyContainer data, T value);

        default void write(final PropertyContainer data, final JsonNode json)
        {
                write(data, interpretJson(json));
        }

        JsonNode makeJsonValue(T value);

        default JsonField makeJsonFieldFrom(final PropertyContainer data)
        {
                return makeJsonField(read(data));
        }

        default JsonField makeJsonField(final T value)
        {
                return JsonNodeFactories.field(jsonName(), makeJsonValue(value));
        }

        @Override
        default JsonNode makeJsonValue(final Entity data)
        {
                return makeJsonValue(read(data));
        }

        default JsonNode makeJsonValue(final PropertyContainer data)
        {
                return makeJsonValue(read(data));
        }

        @Override
        T interpretJson(JsonNode json);
}

abstract class FieldData<T> extends AttrData implements Field<T> {

        private static final long serialVersionUID = -5509719667123101352L;

        private final String property;
        private final boolean required;
        private final JsonSerializer<T> jsonSerializer;

        protected FieldData(final String canonicalName,
                            final String description,
                            final String property,
                            final String field,
                            final boolean required,
                            final JsonStringNode jsonName,
                            final String jsonPath,
                            final JsonSerializer<T> jsonSerializer,
                            final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, field, jsonName, jsonPath, constraints);
                this.property = property;
                this.required = required;
                this.jsonSerializer = jsonSerializer;
        }

        @Override
        public final String property()
        {
                return property;
        }

        @Override
        public final boolean required()
        {
                return required;
        }

        @Override
        public final JsonNode makeJsonValue(final T value)
        {
                return jsonSerializer.toJson(value);
        }

        @Override
        public final T interpretJson(final JsonNode json)
        {
                return jsonSerializer.fromJson(json, jsonPath());
        }

        @Override
        public final void validate(final Entity data, final Validation validation)
        {
                final T value = read(data);
                if (value == null) {
                        if (required()) {
                                validation.reject(this, RequiredConstraint.INSTANCE.messageFor(this));
                        }
                } else {
                        validateNotNullValue(value, validation);
                }
        }

        protected void validateNotNullValue(final T value, final Validation validation)
        {
                for (final Constraint constraint : constraints()) {
                        if (constraint.isInvalid(value)) {
                                validation.reject(this, constraint.messageFor(this, value));
                        }
                }
        }
}

enum RequiredConstraint implements Constraint<Object> {
        INSTANCE;

        @Override
        public boolean isInvalid(final Object value)
        {
                throw new UnsupportedOperationException();
        }

        public String messageFor(final Attr attr)
        {
                return messageFor(attr, this);
        }

        @Override
        public String messageFor(final Attr attr, final Object value)
        {
                final StringBuilder msg = new StringBuilder().append('\'').append(attr.description()).append(
                        "' es requerido.");
                return msg.toString();
        }

        @Override
        public String getName()
        {
                return "required";
        }
}
