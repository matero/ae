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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.thymeleaf.TemplateEngine;

public final class ThymeleafTemplateEngine {

    public static final String TEMPLATE_ENGINE = "$thyme";

    private ThymeleafTemplateEngine()
    {
        throw new UnsupportedOperationException();
    }

    public static TemplateEngine get(final ServletContextEvent event)
    {
        return get(event.getServletContext());
    }

    public static TemplateEngine get(final ServletContext ctx)
    {
        return (TemplateEngine) ctx.getAttribute(TEMPLATE_ENGINE);
    }

    public static void set(final ServletContextEvent event, final TemplateEngine engine)
    {
        set(event.getServletContext(), engine);
    }

    public static void set(final ServletContext ctx, final TemplateEngine engine)
    {
        ctx.setAttribute(TEMPLATE_ENGINE, engine);
    }
}
