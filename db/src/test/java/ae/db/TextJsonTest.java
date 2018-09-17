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

import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonNodeFactories.nullNode;
import static org.assertj.core.api.Assertions.assertThat;

import argo.jdom.JsonNode;
import com.google.appengine.api.datastore.Text;

import org.testng.annotations.Test;

public class TextJsonTest {

    private final TextJsonSerializer serializer = TextJsonSerializer.INSTANCE;

    @Test
    public void shoud_be_able_to_serialize_null()
    {
        final JsonNode json = serializer.toJson(null);
        assertThat(json.isNullNode()).isTrue();
    }

    @Test
    public void shoud_be_able_to_serialize_non_null_strings()
    {
        final JsonNode json = serializer.toJson(new Text("no null"));
        assertThat(json.isNullNode()).isFalse();
        assertThat(json.isStringValue()).isTrue();
        assertThat(json.getText()).isEqualTo("no null");
    }

    @Test
    public void shoud_be_able_to_deserialize_null_nodes()
    {
        final Text txt = serializer.fromJson(nullNode());
        assertThat(txt).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_non_null_node()
    {
        final Text txt = serializer.fromJson(string("no null"));
        assertThat(txt).isEqualTo(new Text("no null"));
    }

    @Test
    public void shoud_be_able_to_deserialize_null_node_from_object_node()
    {
        final JsonNode object = object(field(string("attr1"), string("no null")),
                                       field(string("attr2"), nullNode()));
        final Text txt = serializer.fromJson(object, "attr2");
        assertThat(txt).isNull();
    }

    @Test
    public void shoud_be_able_to_deserialize_non_null_node_from_object_node()
    {
        final JsonNode object = object(field(string("attr1"), string("no null")),
                                       field(string("attr2"), string("another")));
        final Text txt = serializer.fromJson(object, "attr1");
        assertThat(txt).isEqualTo(new Text("no null"));
    }
}
