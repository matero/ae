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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public abstract class TemplateHandler extends Handler {
  private final WebContext templateContext;
  private final TemplateEngine templateEngine;

  protected TemplateHandler(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final WebContext templateContext,
                            final TemplateEngine templateEngine) {
    super(request, response);
    this.templateContext = templateContext;
    this.templateEngine = templateEngine;
  }

  @Override protected void setup() throws ServletException, IOException {
    super.setup();
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

  protected void renderHtml(final String template) throws IOException, ServletException {
    prepareHtmlHeaders();
    writeHtml(processTemplate(template));
  }

  protected void prepareHtmlHeaders() {
    set(Header.PRAGMA, "no-cache");
    set(Header.CACHE_CONTROL, "no-cache");
    set(Header.EXPIRES, 0L);
  }

  protected String processTemplate(final String template) {
    return templateEngine.process(template, templateContext);
  }

  protected void ctx(final String name, final Object value) {
    templateContext.setVariable(name, value);
  }

  protected void ctx(final String name, final ImmutableCollection.Builder<?> collectionBuilder) {
    templateContext.setVariable(name, collectionBuilder.build());
  }

  protected void ctx(final String name, final ImmutableMap.Builder<?, ?> mapBuilder) {
    templateContext.setVariable(name, mapBuilder.build());
  }

  protected static final String template(final String template) {
    return template;
  }
}
