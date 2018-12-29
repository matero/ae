package processor.test;

import ae.DELETE;
import ae.GET;
import ae.POST;
import ae.PUT;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ae.web.OAuth2Flow;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import ae.controller;
import ae.namespace;
import ae.oauth2;
import ae.router;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@router
@WebServlet("/*")
class AppRouter extends SigexRouter {
        // nothing to define
}

@controller("")
@namespace(namespace.GLOBAL)
class ClientController extends ae.web.Controller {
        public ClientController(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final Entity userData)
        {
                super(request, response, userData);
        }

        @Override
        protected Logger logger()
        {
                return LoggerFactory.getLogger(getClass());
        }

        @GET
        @namespace("test")
        public void index() throws IOException, ServletException
        {
        }

        @POST
        @namespace(namespace.MULTITENANT)
        public void save() throws IOException, ServletException
        {
        }
}

@controller
@namespace(namespace.MULTITENANT)
final class Gym extends ae.web.Controller {
        public Gym(final HttpServletRequest request, final HttpServletResponse response, final Entity userData)
        {
                super(request, response, userData);
        }

        @Override
        protected Logger logger()
        {
                return LoggerFactory.getLogger(getClass());
        }

        @GET
        @namespace(namespace.GLOBAL)
        public void index()
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

        @GET("{id}")
        public void show(final long id)
        {
        }

        @PUT("{id}")
        public void update(final long id)
        {
        }

        @DELETE("{id}")
        public void delete(final long id)
        {
        }
}

@controller
class BookController extends ae.web.Controller implements OAuth2Flow {
        public BookController(final HttpServletRequest request,
                              final HttpServletResponse response,
                              final Entity userData)
        {
                super(request, response, userData);
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

        @GET
        public void create()
        {
        }

        @POST
        public void save()
        {
        }

        @PUT("{id}")
        public void update(final long id)
        {
        }

        @DELETE("{id}")
        public void delete(final long id)
        {
        }

        @GET("foo/{id}/{arg}")
        @oauth2
        public void foo(final long id, final String arg)
        {
        }

        @GET("bar/{id}/{cursor}/{arg}")
        public void bar(final long id, final Cursor c, final String arg)
        {
        }
}
