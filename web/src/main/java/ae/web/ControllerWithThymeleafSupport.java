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
package ae.web;

import ae.db.Attr;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public abstract class ControllerWithThymeleafSupport extends Controller {

    protected final WebContext templateContext;
    protected final TemplateEngine templateEngine;

    protected ControllerWithThymeleafSupport(final HttpServletRequest request, final HttpServletResponse response)
    {
        this(request, response, null, null);
    }

    protected ControllerWithThymeleafSupport(final HttpServletRequest request,
                                             final HttpServletResponse response,
                                             final WebContext templateContext,
                                             final TemplateEngine templateEngine)
    {
        super(request, response);
        this.templateContext = templateContext;
        this.templateEngine = templateEngine;
    }
    
    @Override
    protected void setup()
    {
        if (this.templateContext != null) {
            ctx("gae", AppengineDialect.INSTANCE);
            if (isUserLoggedIn()) {
                ctx("user_type", isUserAdmin() ? "adm" : "usr");
                ctx("current_user", currentUser());
                ctx("logout", logoutURL(request));
            } else {
                ctx("user", "none");
                ctx("login", loginURL(request));
            }
        }
    }

    protected void renderHtml(final String template) throws IOException, ServletException
    {
        prepareHtmlHeaders();
        writeHtml(processTemplate(template));
    }

    protected void prepareHtmlHeaders()
    {
        set(Header.PRAGMA, "no-cache");
        set(Header.CACHE_CONTROL, "no-cache");
        set(Header.EXPIRES, 0L);
    }

    protected String processTemplate(final String template)
    {
        return templateEngine.process(template, templateContext);
    }

    protected void ctx(final String name, final Object value)
    {
        templateContext.setVariable(name, value);
    }

    protected void ctx(final String name, final ImmutableCollection.Builder<?> collectionBuilder)
    {
        templateContext.setVariable(name, collectionBuilder.build());
    }

    protected void ctx(final String name, final ImmutableMap.Builder<?, ?> mapBuilder)
    {
        templateContext.setVariable(name, mapBuilder.build());
    }

    protected static final String template(final String template)
    {
        return template;
    }

    protected final ImmutableMap<String, Object> data(final Attr attr, final Object value)
    {
        return ImmutableMap.of(attr.property(), value);
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2)
    {
        return ImmutableMap.of(attr1.property(), value1,
                               attr2.property(), value2);
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3)
    {
        return ImmutableMap.of(attr1.property(), value1,
                               attr2.property(), value2,
                               attr3.property(), value3);
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4)
    {
        return ImmutableMap.of(attr1.property(), value1,
                               attr2.property(), value2,
                               attr3.property(), value3,
                               attr4.property(), value4);
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4,
                                                      final Attr attr5, final Object value5)
    {
        return ImmutableMap.of(attr1.property(), value1,
                               attr2.property(), value2,
                               attr3.property(), value3,
                               attr4.property(), value4,
                               attr5.property(), value5);
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4,
                                                      final Attr attr5, final Object value5,
                                                      final Attr attr6, final Object value6)
    {
        return ImmutableMap.<String, Object>builder()
                .put(attr1.property(), value1)
                .put(attr2.property(), value2)
                .put(attr3.property(), value3)
                .put(attr4.property(), value4)
                .put(attr5.property(), value5)
                .put(attr6.property(), value6)
                .build();
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4,
                                                      final Attr attr5, final Object value5,
                                                      final Attr attr6, final Object value6,
                                                      final Attr attr7, final Object value7)
    {
        return ImmutableMap.<String, Object>builder()
                .put(attr1.property(), value1)
                .put(attr2.property(), value2)
                .put(attr3.property(), value3)
                .put(attr4.property(), value4)
                .put(attr5.property(), value5)
                .put(attr6.property(), value6)
                .put(attr7.property(), value7)
                .build();
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4,
                                                      final Attr attr5, final Object value5,
                                                      final Attr attr6, final Object value6,
                                                      final Attr attr7, final Object value7,
                                                      final Attr attr8, final Object value8)
    {
        return ImmutableMap.<String, Object>builder()
                .put(attr1.property(), value1)
                .put(attr2.property(), value2)
                .put(attr3.property(), value3)
                .put(attr4.property(), value4)
                .put(attr5.property(), value5)
                .put(attr6.property(), value6)
                .put(attr7.property(), value7)
                .put(attr8.property(), value8)
                .build();
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4,
                                                      final Attr attr5, final Object value5,
                                                      final Attr attr6, final Object value6,
                                                      final Attr attr7, final Object value7,
                                                      final Attr attr8, final Object value8,
                                                      final Attr attr9, final Object value9)
    {
        return ImmutableMap.<String, Object>builder()
                .put(attr1.property(), value1)
                .put(attr2.property(), value2)
                .put(attr3.property(), value3)
                .put(attr4.property(), value4)
                .put(attr5.property(), value5)
                .put(attr6.property(), value6)
                .put(attr7.property(), value7)
                .put(attr8.property(), value8)
                .put(attr9.property(), value9)
                .build();
    }

    protected final ImmutableMap<String, Object> data(final Attr attr1, final Object value1,
                                                      final Attr attr2, final Object value2,
                                                      final Attr attr3, final Object value3,
                                                      final Attr attr4, final Object value4,
                                                      final Attr attr5, final Object value5,
                                                      final Attr attr6, final Object value6,
                                                      final Attr attr7, final Object value7,
                                                      final Attr attr8, final Object value8,
                                                      final Attr attr9, final Object value9,
                                                      final Attr attr10, final Object value10)
    {
        return ImmutableMap.<String, Object>builder()
                .put(attr1.property(), value1)
                .put(attr2.property(), value2)
                .put(attr3.property(), value3)
                .put(attr4.property(), value4)
                .put(attr5.property(), value5)
                .put(attr6.property(), value6)
                .put(attr7.property(), value7)
                .put(attr8.property(), value8)
                .put(attr9.property(), value9)
                .put(attr10.property(), value10)
                .build();
    }
}
