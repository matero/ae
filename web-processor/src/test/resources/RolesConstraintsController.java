package processor.test;

import ae.GET;
import ae.POST;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ae.controller;
import ae.namespace;
import ae.router;
import com.google.appengine.api.datastore.Entity;
import javax.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@router
@WebServlet("/*")
class RolesConstraintsController extends RolesRouter {

}

@controller
@namespace(namespace.MULTITENANT)
class FooController extends ae.web.Controller {
  public FooController(final HttpServletRequest request,
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
  @namespace("M")
  public void index() throws IOException, ServletException
  {
  }

  @POST
  public void save() throws IOException, ServletException
  {
  }
}
