/*
 * The MIT License
 *
 * Copyright (c) 2018 ActiveEngine.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without renumberiction, including without limitation the rights
 * to use, copy, modify, merge, publish, dinumberibute, sublicense, and/or sell
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

import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.nullNode;
import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonNodeFactories.number;
import static org.assertj.core.api.Assertions.assertThat;

import argo.jdom.JsonNode;
import org.testng.annotations.Test;

public class LongJsonSerializerTest {

    private final LongJsonSerializer serializer = LongJsonSerializer.INSTANCE;

    @Test
    public void shoud_be_able_to_serialize_null()
    {
        final JsonNode json = serializer.toJson(null);
        assertThat(json.isNullNode()).isTrue();
    }

    @Test
    public void shoud_be_able_to_serialize_non_null_numbers()
    {
        final JsonNode json = serializer.toJson(Long.valueOf(10));
        assertThat(json.isNullNode()).isFalse();
        assertThat(json.isNumberValue()).isTrue();
        assertThat(json.getText()).isEqualTo("10");
    }

    @Test
    public void shoud_be_able_to_deserialize_null_nodes()
    {
        final Long number = serializer.fromJson(nullNode());
        assertThat(number).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_non_null_node()
    {
        final Long number = serializer.fromJson(number(10));
        assertThat(number).isEqualTo(Long.valueOf(10));
    }

    @Test
    public void shoud_be_able_to_deserialize_null_node_from_object_node()
    {
        final JsonNode object = object(field(string("attr1"), number("10")),
                                       field(string("attr2"), nullNode()));
        final Long number = serializer.fromJson(object, "attr2");
        assertThat(number).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_non_null_node_from_object_node()
    {
        final JsonNode object = object(field(string("attr1"), number("10")),
                                       field(string("attr2"), number(20)));
        final Long number = serializer.fromJson(object, "attr1");
        assertThat(number).isEqualTo(Long.valueOf(10));
    }
}
