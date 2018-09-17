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
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.nullNode;
import static argo.jdom.JsonNodeFactories.string;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import argo.jdom.JsonNode;
import java.util.List;
import org.testng.annotations.Test;

public class StringArrayJsonSerializerTest {

    private final JsonArraySerializer<String> serializer = StringJsonSerializer.ARRAY;

    @Test
    public void shoud_be_able_to_serialize_null()
    {
        final JsonNode json = serializer.toJson(null);
        assertThat(json.isNullNode()).isTrue();
    }

    @Test
    public void shoud_be_able_to_serialize_non_null_strings()
    {
        //given:
        final JsonNode json = serializer.toJson(asList("no null", "another", null));
        //expect:
        assertThat(json.isNullNode()).isFalse();
        assertThat(json.isArrayNode()).isTrue();
        assertThat(json.getNullableStringValue(0)).isEqualTo("no null");
        assertThat(json.getNullableStringValue(1)).isEqualTo("another");
        assertThat(json.getNullableStringValue(2)).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_null_nodes()
    {
        final List<String> lst = serializer.fromJson(nullNode());
        assertThat(lst).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_non_null_node()
    {
        final List<String> lst = serializer.fromJson(array(string("no null"), nullNode(), string("another")));
        assertThat(lst).containsExactly("no null", null, "another");
    }

    @Test
    public void shoud_be_able_to_deserialize_null_node_from_object_node()
    {
        final JsonNode object = object(field(string("attr1"), string("no null")),
                                       field(string("attr2"), nullNode()));
        final List<String> lst = serializer.fromJson(object, "attr2");
        assertThat(lst).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_non_null_node_from_object_node()
    {
        final JsonNode object = object(field(string("attr1"), array(nullNode(), string("no null"), string("another"))),
                                       field(string("attr2"), string("no null")));
        final List<String> lst = serializer.fromJson(object, "attr1");
        assertThat(lst).containsExactly(null, "no null", "another");
    }
}
