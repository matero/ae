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
import ae.namespace;
import ae.roles;
import ae.router;
import ae.template;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@router
@WebServlet("/*")
class RolesConstraintsController extends RolesRouter {

}

@controller
@namespace(namespace.MULTITENANT)
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
        @namespace(namespace.GLOBAL)
        public void htmlIndex() throws IOException, ServletException
        {
        }

        @GET
        @namespace("M")
        public void index() throws IOException, ServletException
        {
        }

        @GET
        @template
        @roles({"r1", "r2"})
        @namespace("otro")
        public void create() throws IOException, ServletException
        {
        }

        @POST
        public void save() throws IOException, ServletException
        {
        }
}
