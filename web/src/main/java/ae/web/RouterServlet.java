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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public abstract class RouterServlet extends HttpServlet {

        private static final long serialVersionUID = -8511948712493790911L;

        @SuppressWarnings("initialization.fields.uninitialized")
        private TemplateEngine templateEngine;

        protected RouterServlet()
        {
                // nothing more to do
        }

        protected void unhandledDelete(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doDelete(request, response);
        }

        protected void unhandledPut(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doPut(request, response);
        }

        protected void unhandledPost(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doPost(request, response);
        }

        protected void unhandledGet(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doGet(request, response);
        }

        protected void unhandledHead(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doHead(request, response);
        }

        protected void unhandledOptions(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doOptions(request, response);
        }

        protected void unhandledTrace(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException
        {
                super.doTrace(request, response);
        }

        protected <C extends Controller> void handle(final C controller, final HttpRequestHandler<C> handler)
                throws ServletException, IOException
        {
                try {
                        controller.setup();
                        handler.accept(controller);
                } finally {
                        controller.teardown();
                }
        }

        @Override
        public void init() throws ServletException
        {
                super.init();
                templateEngine = ThymeleafTemplateEngine.get(getServletContext());
        }

        protected TemplateEngine templateEngine()
        {
                return templateEngine;
        }

        protected WebContext webContext(final HttpServletRequest request, final HttpServletResponse response)
        {
                return new WebContext(request, response, getServletContext());
        }
}
