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

import static argo.jdom.JsonNodeFactories.array;
import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public final class Validation {

  public static Validation withSuccessMessage(final String successMessage)
  {
    if (successMessage == null) {
      throw new NullPointerException("successMessage");
    }
    return new Validation(successMessage);
  }

  public static Validation failed(final Attribute attr, final String failure)
  {
    if (failure == null) {
      throw new NullPointerException("failure");
    }
    if (failure == null) {
      throw new NullPointerException("failure");
    }
    final Validation validation = new Validation("");
    validation.registerFailure(attr, failure);
    return validation;
  }

  private final LinkedHashMap<Attribute, List<String>> errors;
  public final String successMessage;

  private Validation(final String successMessage)
  {
    this.errors = new LinkedHashMap<>();
    this.successMessage = successMessage;
  }

  public boolean succeded()
  {
    return this.errors.isEmpty();
  }

  public boolean failed()
  {
    return !this.errors.isEmpty();
  }

  public List<String> get(final Attribute attr)
  {
    if (this.errors.containsKey(attr)) {
      return this.errors.get(attr);
    } else {
      return ImmutableList.of();
    }
  }

  public JsonNode asJson()
  {
    if (succeded()) {
      return object(success());
    } else {
      return object(failure());
    }
  }

  private JsonField success()
  {
    return field("success", string(this.successMessage));
  }

  private JsonField failure()
  {
    final List<JsonNode> propertiesErrors = new java.util.ArrayList<>(this.errors.size());
    for (final Attribute attr : this.errors.keySet()) {
      final List<String> attrErrors = this.errors.get(attr);
      if (!attrErrors.isEmpty()) {
        final List<JsonStringNode> failureMsgs = new java.util.ArrayList<>(attrErrors.size());
        for (final String failureMessage : attrErrors) {
          failureMsgs.add(string(failureMessage));
        }
        propertiesErrors.add(attributeFailures(attr, failureMsgs));
      }
    }
    return field("failure", array(propertiesErrors));
  }

  protected static JsonNode attributeFailures(final Attribute attr, final List<JsonStringNode> errorMessages)
  {
    return object(ImmutableList.of(field(attr.jsonName(), array(errorMessages))));
  }

  public void reject(final Attribute attr, final String failure)
  {
    if (attr == null) {
      throw new NullPointerException("attr");
    }
    if (failure == null) {
      throw new NullPointerException("failure");
    }
    registerFailure(attr, failure);
  }

  private void registerFailure(final Attribute attr, final String failure)
  {
    this.errors.putIfAbsent(attr, new LinkedList<>());
    this.errors.get(attr).add(failure);
  }
}
