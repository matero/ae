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
import argo.jdom.JsonStringNode;
import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;
import java.util.Map;

enum UserJsonSerializer implements JsonSerializer<User> {
  INSTANCE;

  static final JsonArraySerializer<User> ARRAY = new JsonArraySerializer<>(INSTANCE);

  private final JsonStringNode userId = JsonNodeFactories.string("userId");
  private final JsonStringNode email = JsonNodeFactories.string("email");
  private final JsonStringNode federatedIdentity = JsonNodeFactories.string("fedId");
  private final JsonStringNode authDomain = JsonNodeFactories.string("authDomain");

  @Override public JsonNode toJson(final User value) {
    if (value == null) {
      return JsonNodeFactories.nullNode();
    }
    return JsonNodeFactories.object(
            ImmutableList.of(
                    JsonNodeFactories.field(userId, JsonNodeFactories.string(value.getUserId())),
                    JsonNodeFactories.field(email, JsonNodeFactories.string(value.getEmail())),
                    JsonNodeFactories.field(federatedIdentity, JsonNodeFactories.string(value.getFederatedIdentity())),
                    JsonNodeFactories.field(authDomain, JsonNodeFactories.string(value.getAuthDomain()))
            )
    );
  }

  @Override
  public User fromJson(final JsonNode json, final String jsonPath) {
    if (json.isNullNode(jsonPath)) {
      return null;
    } else {
      final Map<JsonStringNode, JsonNode> user = json.getObjectNode(jsonPath);
      return new User(user.get(email).getStringValue(),
                      user.get(authDomain).getStringValue(),
                      user.get(userId).getStringValue(),
                      user.get(federatedIdentity).getStringValue());
    }
  }

  @Override
  public User fromJson(final JsonNode json) {
    if (json.isNullNode()) {
      return null;
    } else {
      final Map<JsonStringNode, JsonNode> user = json.getObjectNode();
      return new User(user.get(email).getStringValue(),
                      user.get(authDomain).getStringValue(),
                      user.get(userId).getStringValue(),
                      user.get(federatedIdentity).getStringValue());
    }
  }
}
