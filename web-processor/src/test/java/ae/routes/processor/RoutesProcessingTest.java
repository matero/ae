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
package ae.routes.processor;

import com.google.testing.compile.Compilation;
import static com.google.testing.compile.CompilationSubject.assertThat;
import com.google.testing.compile.Compiler;
import static com.google.testing.compile.Compiler.javac;
import com.google.testing.compile.JavaFileObjects;
import java.time.ZoneId;
import java.util.*;

import org.junit.Test;

public class RoutesProcessingTest {

        static final Date GENERATION_DATE;

        static {
                final GregorianCalendar calendar = new GregorianCalendar(2017, Calendar.FEBRUARY, 23, 12, 0);
                calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC-3")));
                GENERATION_DATE = calendar.getTime();
        }

        final Compiler compiler = javac().withProcessors(
                new ControllerAndRoutesCompiler(GENERATION_DATE, new RoutersCodeBuilder()));

        @Test
        public void should_compile_multiple_verb_routes()
        {
                final Compilation compilation = compiler.compile(
                        JavaFileObjects.forSourceLines(
                                "AppRouter",
                                "package processor.test;",
                                "import ae.GET;",
                                "import ae.POST;",
                                "import java.io.IOException;",
                                "import javax.servlet.ServletException;",
                                "import javax.servlet.http.HttpServletRequest;",
                                "import javax.servlet.http.HttpServletResponse;",
                                "import org.thymeleaf.TemplateEngine;",
                                "import org.thymeleaf.context.WebContext;",
                                "import ae.web.OAuth2Flow;",
                                "import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;",
                                "import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;",
                                "import com.google.appengine.api.datastore.Cursor;",
                                "import ae.DELETE;",
                                "import ae.GET;",
                                "import ae.POST;",
                                "import ae.PUT;",
                                "import ae.controller;",
                                "import ae.web.ControllerWithThymeleafSupport;",
                                "import ae.web.OAuth2Flow;",
                                "import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;",
                                "import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;",
                                "import com.google.appengine.api.datastore.Cursor;",
                                "import javax.servlet.ServletException;",
                                "import java.io.IOException;",
                                "import javax.servlet.http.HttpServletRequest;",
                                "import javax.servlet.http.HttpServletResponse;",
                                "import org.thymeleaf.TemplateEngine;",
                                "import org.thymeleaf.context.WebContext;",
                                "import org.slf4j.Logger;",
                                "import org.slf4j.LoggerFactory;",
                                
                                "@ae.router(apiPath=\"api/v1\")",
                                "interface AppRouter {}",
                                
                                "@ae.controller(path = \"client\")",
                                "class ClientController extends ae.web.ControllerWithThymeleafSupport {",
                                "        public ClientController(HttpServletRequest request, HttpServletResponse response)",
                                "        { super(request, response); }",
                                "        public ClientController(HttpServletRequest request, HttpServletResponse response, WebContext templateContext, TemplateEngine templateEngine)",
                                "        { super(request, response, templateContext, templateEngine); }",
                                "        @Override protected Logger logger() { return LoggerFactory.getLogger(getClass()); }",
                                "        @GET(template = true) public void htmlIndex() throws IOException, ServletException {}",
                                "        @GET public void index() throws IOException, ServletException {}",
                                "        @GET(path = \"create\", template = true) public void create() throws IOException, ServletException {}",
                                "        @POST public void save() throws IOException, ServletException {}",
                                "}",
                                
                                "@ae.controller(path = \"gym\")",
                                "class GymController extends ae.web.ControllerWithThymeleafSupport {",
                                "        public GymController(HttpServletRequest request, HttpServletResponse response)",
                                "        {",
                                "                super(request, response);",
                                "        }",
                                "        public GymController(HttpServletRequest request, HttpServletResponse response, WebContext templateContext, TemplateEngine templateEngine)",
                                "        {",
                                "                super(request, response, templateContext, templateEngine);",
                                "        }",
                                "        @Override protected Logger logger() { return LoggerFactory.getLogger(getClass()); }",
                                "        @GET public void index() {}",
                                "        @GET(template = true) public void htmlIndex() {}",
                                "        @GET(path = \"create\") public void create() {}",
                                "        @POST public void save() {}",
                                "        @GET(path = \"{id}\") public void show(final long id) {}",
                                "        @GET(path = \"{id}/edit\", template = true) public void edit(final long id) {}",
                                "        @PUT(path = \"{id}\") public void update(final long id) {}",
                                "        @DELETE(path = \"{id}\") public void delete(final long id) {}",
                                "}",
                                
                                "@ae.controller(path = \"book\")",
                                "class BookController extends ControllerWithThymeleafSupport implements OAuth2Flow {",
                                "        public BookController(final HttpServletRequest request, final HttpServletResponse response)",
                                "        {",
                                "                super(request, response);",
                                "        }",
                                "        public BookController(final HttpServletRequest request,",
                                "                          final HttpServletResponse response,",
                                "                          final WebContext templateContext,",
                                "                          final TemplateEngine templateEngine)",
                                "        {",
                                "                super(request, response, templateContext, templateEngine);",
                                "        }",
                                "        @Override protected Logger logger() { return LoggerFactory.getLogger(getClass()); }",
                                "        @Override public AuthorizationCodeFlow flow() { return null; }",
                                "        @Override public AuthorizationCodeRequestUrl authorizationUrl() { return null; }",
                                "        @Override public String redirectUri() throws ServletException, IOException { return null; }",
                                "        @Override public void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException { }",
                                "        @GET public void index() {}",
                                "        @GET(template = true) public void htmlIndex() {}",
                                "        @GET(path = \"create\") public void create() {}",
                                "        @POST public void save() {}",
                                "        @GET(path = \"{id}\", template = true) public void show(final long id) {}",
                                "        @GET(path = \"{id}/edit\", template = true) public void edit(final long id) {}",
                                "        @PUT(path = \"{id}\") public void update(final long id) {}",
                                "        @DELETE(path = \"{id}\") public void delete(final long id) {}",
                                "        @GET(path = \"foo/{id}/{arg}\", oauth2 = true) public void foo(final long id, final String arg) {}",
                                "        @GET(path=\"bar/{id}/{cursor}/{arg}\") public void bar(final long id, final Cursor c, final String arg){}",
                                "}"
                        )
                );
                assertThat(compilation).succeeded();
                assertThat(compilation)
                        .generatedSourceFile("processor.test.AppRouter_impl")
                        .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/routes/AppRouter_impl.java"));
        }
}
