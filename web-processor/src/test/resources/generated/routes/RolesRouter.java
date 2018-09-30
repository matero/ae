package processor.test;

import ae.web.Route;
import ae.web.RouterServlet;
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

  private final Route GET_processor_test_FooController_htmlIndex = new Route("/app/foo");

  private final Route GET_processor_test_FooController_create = new Route("/app/foo/create");

  private final Route POST_processor_test_FooController_save = new Route("/app/api/v1/foo");

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (GET_processor_test_FooController_index.matches(request)) {
      useNamespace("M");
      handle(new FooController(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_processor_test_FooController_htmlIndex.matches(request)) {
      if (!loggedUserRoleIs("r1")) {
        notAuthorized(response);
        return;
      }
      handle(new FooController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
      return;
    }
    if (GET_processor_test_FooController_create.matches(request)) {
      if (!loggedUserRoleIsIn("r1", "r2")) {
        notAuthorized(response);
        return;
      }
      useNamespace("otro");
      handle(new FooController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.create());
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (POST_processor_test_FooController_save.matches(request)) {
      useLoggedUserNamespace();
      handle(new FooController(request, response), (controller) -> controller.save());
      return;
    }
    unhandledPost(request, response);
  }
}
