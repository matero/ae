package processor.test;

import ae.web.Route;
import ae.web.RouterServlet;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.annotation.Generated;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Generated(
    value = "AE/web-processor",
    comments = "",
    date = "2017-02-23"
)
abstract class RolesRouter extends RouterServlet {
  private static final long serialVersionUID = 1487851200000L;

  private final Route GET_processor_test_FooController_index = new Route("/app/api/v1/foo");

  private final Route POST_processor_test_FooController_save = new Route("/app/api/v1/foo");

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException
  {
    final Entity userData = getLoggedUser();
    if (GET_processor_test_FooController_index.matches(request)) {
      useNamespace("M");
      handle(new FooController(request, response, userData), (controller) -> controller.index());
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException
  {
    final Entity userData = getLoggedUser();
    if (POST_processor_test_FooController_save.matches(request)) {
      useLoggedUserNamespace(userData);
      handle(new FooController(request, response, userData), (controller) -> controller.save());
      return;
    }
    unhandledPost(request, response);
  }
}
