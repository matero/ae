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
import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.nullNode;

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.IMHandle;
import com.google.common.collect.ImmutableList;

import java.net.MalformedURLException;
import java.net.URL;

enum IMHandleJsonSerializer implements JsonSerializer<IMHandle> {
        INSTANCE;

        static final JsonArraySerializer<IMHandle> ARRAY = new JsonArraySerializer<>(INSTANCE);

        private final JsonStringNode address = string("addr");
        private final JsonStringNode protocol = string("prot");

        @Override
        public JsonNode toJson(final IMHandle value)
        {
                if (value == null) {
                        return nullNode();
                }
                return object(ImmutableList.of(field(address, string(value.getAddress())),
                                               field(protocol, string(value.getProtocol()))));
        }

        @Override
        public IMHandle fromJson(final JsonNode json, final String jsonPath)
        {
                if (json.isNullNode(jsonPath)) {
                        return null;
                } else {
                        final String addr = json.getNullableStringValue(jsonPath, "addr");
                        final String prot = json.getStringValue(jsonPath, "prot");
                        return makeImHandle(addr, prot);
                }
        }

        @Override
        public IMHandle fromJson(final JsonNode json)
        {
                if (json.isNullNode()) {
                        return null;
                } else {
                        final String addr = json.getNullableStringValue("addr");
                        final String prot = json.getStringValue("prot");
                        return makeImHandle(addr, prot);
                }
        }

        IMHandle makeImHandle(final String addr, final String prot)
        {
                try {
                        return new IMHandle(IMHandle.Scheme.valueOf(prot), addr);
                } catch (final IllegalArgumentException e) {
                        try {
                                return new IMHandle(new URL(prot), addr);
                        } catch (final MalformedURLException ex) {
                                throw new IllegalStateException("IMHandle protocol is invalid", ex);
                        }
                }
        }
}
