package processor.test;

import ae.DELETE;
import ae.GET;
import ae.POST;
import ae.PUT;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ae.web.OAuth2Flow;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import ae.controller;
import ae.router;
import ae.web.ControllerWithThymeleafSupport;
import com.google.appengine.api.datastore.Cursor;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@router(basePath = "", apiPath = "api/v1")
@WebServlet("/*")
class AppRouter extends SigexRouter {
        // nothing to define
}

@controller(path = "")
class ClientController extends ae.web.ControllerWithThymeleafSupport {
        public ClientController(final HttpServletRequest request, final HttpServletResponse response)
        {
                super(request, response);
        }

        public ClientController(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final WebContext templateContext,
                                final TemplateEngine templateEngine)
        {
                super(request, response, templateContext, templateEngine);
        }

        @Override
        protected Logger logger()
        {
                return LoggerFactory.getLogger(getClass());
        }

        @GET(template = true)
        public void htmlIndex() throws IOException, ServletException
        {
        }

        @GET
        public void index() throws IOException, ServletException
        {
        }

        @GET(path = "create", template = true)
        public void create() throws IOException, ServletException
        {
        }

        @POST
        public void save() throws IOException, ServletException
        {
        }
}

@ae.controller
final class Gym extends ControllerWithThymeleafSupport {
        public Gym(final HttpServletRequest request, final HttpServletResponse response)
        {
                super(request, response);
        }

        public Gym(final HttpServletRequest request,
                   final HttpServletResponse response,
                   final WebContext templateContext,
                   final TemplateEngine templateEngine)
        {
                super(request, response, templateContext, templateEngine);
        }

        @Override
        protected Logger logger()
        {
                return LoggerFactory.getLogger(getClass());
        }

        @GET
        public void index()
        {
        }

        @GET(template = true)
        public void htmlIndex()
        {
        }

        @GET
        public void create()
        {
        }

        @POST
        public void save()
        {
        }

        @GET(path = "{id}")
        public void show(final long id)
        {
        }

        @GET(path = "{id}/edit", template = true)
        public void edit(final long id)
        {
        }

        @PUT(path = "{id}")
        public void update(final long id)
        {
        }

        @DELETE(path = "{id}")
        public void delete(final long id)
        {
        }
}

@controller
class BookController extends ControllerWithThymeleafSupport implements OAuth2Flow {
        public BookController(final HttpServletRequest request, final HttpServletResponse response)
        {
                super(request, response);
        }

        public BookController(final HttpServletRequest request,
                              final HttpServletResponse response,
                              final WebContext templateContext,
                              final TemplateEngine templateEngine)
        {
                super(request, response, templateContext, templateEngine);
        }

        @Override
        protected Logger logger()
        {
                return LoggerFactory.getLogger(getClass());
        }

        @Override
        public AuthorizationCodeFlow flow()
        {
                return null;
        }

        @Override
        public AuthorizationCodeRequestUrl authorizationUrl()
        {
                return null;
        }

        @Override
        public String redirectUri() throws ServletException, IOException
        {
                return null;
        }

        @Override
        public void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException
        {
        }

        @GET
        public void index()
        {
        }

        @GET(template = true)
        public void htmlIndex()
        {
        }

        @GET
        public void create()
        {
        }

        @POST
        public void save()
        {
        }

        @GET(path = "{id}", template = true)
        public void show(final long id)
        {
        }

        @GET(path = "{id}/edit", template = true)
        public void edit(final long id)
        {
        }

        @PUT(path = "{id}")
        public void update(final long id)
        {
        }

        @DELETE(path = "{id}")
        public void delete(final long id)
        {
        }

        @GET(path = "foo/{id}/{arg}", oauth2 = true)
        public void foo(final long id, final String arg)
        {
        }

        @GET(path = "bar/{id}/{cursor}/{arg}")
        public void bar(final long id, final Cursor c, final String arg)
        {
        }
}
