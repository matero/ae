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

import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.nullNode;
import static argo.jdom.JsonNodeFactories.string;

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;

import java.util.Map;

enum UserJsonSerializer implements JsonSerializer<User> {
        INSTANCE;

        static final JsonArraySerializer<User> ARRAY = new JsonArraySerializer<>(INSTANCE);

        private final JsonStringNode userId = string("userId");
        private final JsonStringNode email = string("email");
        private final JsonStringNode federatedIdentity = string("fedId");
        private final JsonStringNode authDomain = string("authDomain");

        @Override
        public JsonNode toJson(final User value)
        {
                if (value == null) {
                        return nullNode();
                }
                return object(ImmutableList.of(field(userId, string(value.getUserId())),
                                               field(email, string(value.getEmail())),
                                               field(federatedIdentity, string(value.getFederatedIdentity())),
                                               field(authDomain, string(value.getAuthDomain()))));
        }

        @Override
        public User fromJson(final JsonNode json, final String jsonPath)
        {
                if (json.isNullNode(jsonPath)) {
                        return null;
                } else {
                        final Map<JsonStringNode, JsonNode> user = json.getObjectNode(jsonPath);
                        return jsonToUser(user);
                }
        }

        @Override
        public User fromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                } else {
                        return jsonToUser(json.getObjectNode());
                }
        }

        User jsonToUser(final Map<JsonStringNode, JsonNode> user)
        {
                if (user == null) {
                        return null;
                }
                final String _email = readNonNull(user, email);
                final String _authDomain = readNonNull(user, authDomain);
                final String _userId = readNullable(user, userId);
                if (_userId == null) {
                        return new User(_email, _authDomain);
                } else {
                        final String _federatedIdentity = readNullable(user, federatedIdentity);
                        if (_federatedIdentity == null) {
                                return new User(_email, _authDomain, _userId);
                        } else {
                                return new User(_email, _authDomain, _userId, _federatedIdentity);
                        }
                }
        }

        String readNonNull(final Map<JsonStringNode, JsonNode> user, final JsonStringNode field)
        {
                final JsonNode node = user.get(field);
                if (node == null) {
                        throw new NullPointerException(field.toString());
                } else if (node.isNullNode()) {
                        throw new NullPointerException(field.toString());
                } else {
                        return node.getStringValue();
                }
        }

        String readNullable(final Map<JsonStringNode, JsonNode> user, final JsonStringNode field)
        {
                final JsonNode node = user.get(field);
                if (node == null) {
                        return null;
                } else if (node.isNullNode()) {
                        return null;
                } else {
                        return node.getStringValue();
                }
        }
}
