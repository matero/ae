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
import static argo.jdom.JsonNodeFactories.array;
import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;
import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Validation {
  public static @NonNull Validation withSuccessMessage(final @NonNull String successMessage) { return new Validation(successMessage); }

  public static @NonNull Validation failed(final @NonNull Attr attr, final @NonNull String message) {
    final Validation validation = new Validation(null);
    validation.reject(attr, message);
    return validation;
  }

  private final @NonNull  LinkedHashMap<Attr, List<String>> errors;
  private final @Nullable String                            successMessage;

  private Validation(final @Nullable String successMessage) {
    this.errors = new LinkedHashMap<>();
    this.successMessage = successMessage;
  }

  public boolean success() {
    return errors.isEmpty();
  }

  public boolean failure() {
    return !errors.isEmpty();
  }

  public @NonNull List<@NonNull String> get(final @NonNull Attr attr) {
    if (errors.containsKey(attr)) {
      return errors.get(attr);
    } else {
      return ImmutableList.of();
    }
  }

  public @NonNull JsonNode asJson() {
    if (success()) {
      return object(successField());
    } else {
      return object(failureField());
    }
  }

  private @NonNull JsonField successField() {
    return field("success", string(successMessage));
  }

  private @NonNull JsonField failureField() {
    final List<JsonNode> propertiesErrors = new java.util.ArrayList<>(errors.size());
    for (final Attr attr : errors.keySet()) {
      final List<String> messages = errors.get(attr);
      if (!messages.isEmpty()) {
        final List<JsonStringNode> errorMessages = new java.util.ArrayList<>(messages.size());
        for (final String msg : messages) {
          errorMessages.add(string(msg));
        }
        propertiesErrors.add(object(ImmutableList.of(field(attr.jsonName(), array(errorMessages)))));
      }
    }
    return field("failure", array(propertiesErrors));
  }

  public void reject(final @NonNull Attr attr, final @NonNull String message) {
    errors.putIfAbsent(attr, new LinkedList<>());
    errors.get(attr).add(message);
  }
}
