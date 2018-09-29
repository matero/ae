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
import ae.roles;
import ae.router;
import ae.template;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@router
@WebServlet("/*")
class RolesConstraintsController extends RolesRouter {

        @Override
        protected boolean loggedUserHas(final String rol)
        {
                return false;
        }

        @Override
        protected boolean loggedUserHasOneOf(final String r1, final String r2)
        {
                return false;
        }

        @Override
        protected boolean loggedUserHasOneOf(final String r1, final String r2, final String r3)
        {
                return false;
        }

        @Override
        protected boolean loggedUserHasOneOf(final String r1, final String r2, final String r3, final String r4)
        {
                return false;
        }

        @Override
        protected boolean loggedUserHasOneOf(final String r1,
                                             final String r2,
                                             final String r3,
                                             final String r4,
                                             final String r5)
        {
                return false;
        }
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

        @GET
        @template
        @roles("r1")
        public void htmlIndex() throws IOException, ServletException
        {
        }

        @GET
        public void index() throws IOException, ServletException
        {
        }

        @GET
        @template
        @roles({"r1", "r2"})
        public void create() throws IOException, ServletException
        {
        }

        @POST
        public void save() throws IOException, ServletException
        {
        }
}
