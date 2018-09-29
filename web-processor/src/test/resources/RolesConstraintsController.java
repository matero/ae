package processor.test;

import ae.GET;
import ae.POST;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ae.controller;
import ae.router;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@router(basePath = "", apiPath = "api/v1")
@WebServlet("/*")
class RolesConstraintsController extends RolesRouter {
        @Override public Collection<String> loggedUserRoles() { return Collections.emptyList(); }
}

@controller
class FooController extends ae.web.ControllerWithThymeleafSupport {
        public FooController(final HttpServletRequest request, final HttpServletResponse response)
        {
                super(request, response);
        }

        public FooController(final HttpServletRequest request,
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

        @GET(template = true, roles = "r1")
        public void htmlIndex() throws IOException, ServletException
        {
        }

        @GET
        public void index() throws IOException, ServletException
        {
        }

        @GET(roles = {"r1", "r2"}, template = true)
        public void create() throws IOException, ServletException
        {
        }

        @POST
        public void save() throws IOException, ServletException
        {
        }
}