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

import static ae.db.DSL.canonicalName;
import static ae.db.DSL.description;
import static ae.db.DSL.propertyName;
import static ae.db.DSL.fieldName;
import static ae.db.DSL.required;
import static ae.db.DSL.jsonName;
import static ae.db.DSL.jsonPath;
import static ae.db.DSL.noConstraints;
import static ae.db.DSL.nullable;

import argo.jdom.JsonNode;

import static argo.jdom.JsonNodeFactories.array;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;
import static org.assertj.core.api.Assertions.assertThat;

import static argo.jdom.JsonNodeFactories.field;

import org.testng.annotations.Test;

public class ValidationTest {

    @Test
    public void can_be_created_with_nonnull_success_message()
    {
        // given
        final String successMessage = "some message";
        // when
        Validation v = Validation.withSuccessMessage(successMessage);
        // then
        assertThat(v.success()).isTrue();
        assertThat(v.failure()).isFalse();
        assertThat(v.successMessage).isEqualTo("some message");
    }

    @Test
    public void when_no_attr_is_rejected_then_the_validation_is_on_success()
    {
        // given, a validation without rejections
        final Validation validation = Validation.withSuccessMessage("Message");

        // expect
        assertThat(validation.success()).isTrue();
        assertThat(validation.failure()).isFalse();
    }

    @Test
    public void failed_validations_are_considered_on_failure_from_creation()
    {
        // given, an attr
        final StringField attr = new UnindexedString(canonicalName("attr"),
                                                     description("test attribute"),
                                                     propertyName("attr"),
                                                     fieldName("attr"),
                                                     required,
                                                     jsonName("attr"),
                                                     jsonPath("attr"),
                                                     noConstraints);

        // and given, a failed validation on attr
        final Validation validation = Validation.failed(attr, "Message");

        // expect, the validation is afailure
        assertThat(validation.success()).isFalse();
        assertThat(validation.failure()).isTrue();
    }

    @Test
    public void when_at_least_one_attr_is_rejected_then_the_validation_is_on_failure()
    {
        // given, a validation
        final Validation validation = Validation.withSuccessMessage("Message");
        // and given, an attr
        final StringField attr = new UnindexedString(canonicalName("attr"),
                                                     description("test attribute"),
                                                     propertyName("attr"),
                                                     fieldName("attr"),
                                                     nullable,
                                                     jsonName("attr"),
                                                     jsonPath("attr"),
                                                     noConstraints);

        // when the attr is rejected
        validation.reject(attr, "message");

        // then, the validation is afailure
        assertThat(validation.success()).isFalse();
        assertThat(validation.failure()).isTrue();
    }

    @Test
    public void when_a_success_is_serialized_asJson_then_a_success_field_with_the_successMessage_as_value_is_generated()
    {
        // given, a validation without rejections
        final Validation validation = Validation.withSuccessMessage("The Success Message");

        // expect
        assertThat(validation.asJson()).isEqualTo(object(field("success", string("The Success Message"))));
    }

    @Test
    public void when_a_failure_is_serialized_asJson_then_a_failure_field_with_all_the_messages_by_rejected_attr_is_generated()
    {
        // given, a validation
        final Validation validation = Validation.withSuccessMessage("The Success Message");
        // and given, an 3 attrs
        final StringField a = new UnindexedString(canonicalName("a"),
                                                  description("test attribute a"),
                                                  propertyName("a"),
                                                  fieldName("a"),
                                                  nullable,
                                                  jsonName("a"),
                                                  jsonPath("a"),
                                                  noConstraints);
        final StringField b = new UnindexedString(canonicalName("b"),
                                                  description("test attribute b"),
                                                  propertyName("b"),
                                                  fieldName("b"),
                                                  nullable,
                                                  jsonName("b"),
                                                  jsonPath("b"),
                                                  noConstraints);

        // when, a is rejected 1 time
        validation.reject(a, "'a' message error");
        // and c is rejected 3 times
        validation.reject(b, "'b' message error #1");
        validation.reject(b, "'b' message error #2");
        validation.reject(b, "'b' message error #3");

        // and validation is serialized to json
        final JsonNode json = validation.asJson();

        // then
        assertThat(json).isEqualTo(object(field("failure", array(object(field("a", array(string("'a' message error")))),
                                                                 object(field("b", array(string("'b' message error #1"),
                                                                                         string("'b' message error #2"),
                                                                                         string("'b' message error #3"))))))));
    }
}
