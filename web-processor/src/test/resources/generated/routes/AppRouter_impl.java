package processor.test;

import ae.web.Interpret;
import ae.web.OAuth2Flow;
import ae.web.ParameterizedRoute;
import ae.web.Route;
import ae.web.RouterServlet;
import com.google.appengine.api.datastore.Cursor;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Generated;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Generated(
    value = "AE/web-processor",
    comments = "",
    date = "2017-02-23"
)
@WebServlet("/*")
public final class AppRouter_impl extends RouterServlet {
  private static final long serialVersionUID = 1487851200000L;

  private final Route GET_processor_test_BookController_index = new Route("/api/v1/book/");

  private final ParameterizedRoute GET_processor_test_BookController_bar = new ParameterizedRoute("/api/v1/book/bar/{id}/{cursor}/{arg}", Pattern.compile("/api/v1/book/bar/(?<p0>[^/]+)/(?<p1>[^/]+)/(?<p2>[^/]+)"));

  private final Route GET_processor_test_BookController_create = new Route("/api/v1/book/create");

  private final ParameterizedRoute GET_processor_test_BookController_foo = new ParameterizedRoute("/api/v1/book/foo/{id}/{arg}", Pattern.compile("/api/v1/book/foo/(?<p0>[^/]+)/(?<p1>[^/]+)"));

  private final Route GET_processor_test_ClientController_index = new Route("/api/v1/client/");

  private final Route GET_processor_test_GymController_index = new Route("/api/v1/gym/");

  private final Route GET_processor_test_GymController_create = new Route("/api/v1/gym/create");

  private final ParameterizedRoute GET_processor_test_GymController_show = new ParameterizedRoute("/api/v1/gym/{id}", Pattern.compile("/api/v1/gym/(?<p0>[^/]+)"));

  private final Route GET_processor_test_BookController_htmlIndex = new Route("/book/");

  private final ParameterizedRoute GET_processor_test_BookController_show = new ParameterizedRoute("/book/{id}", Pattern.compile("/book/(?<p0>[^/]+)"));

  private final ParameterizedRoute GET_processor_test_BookController_edit = new ParameterizedRoute("/book/{id}/edit", Pattern.compile("/book/(?<p0>[^/]+)/edit"));

  private final Route GET_processor_test_ClientController_htmlIndex = new Route("/client/");

  private final Route GET_processor_test_ClientController_create = new Route("/client/create");

  private final Route GET_processor_test_GymController_htmlIndex = new Route("/gym/");

  private final ParameterizedRoute GET_processor_test_GymController_edit = new ParameterizedRoute("/gym/{id}/edit", Pattern.compile("/gym/(?<p0>[^/]+)/edit"));

  private final Route POST_processor_test_BookController_save = new Route("/api/v1/book/");

  private final Route POST_processor_test_ClientController_save = new Route("/api/v1/client/");

  private final Route POST_processor_test_GymController_save = new Route("/api/v1/gym/");

  private final ParameterizedRoute PUT_processor_test_BookController_update = new ParameterizedRoute("/api/v1/book/{id}", Pattern.compile("/api/v1/book/(?<p0>[^/]+)"));

  private final ParameterizedRoute PUT_processor_test_GymController_update = new ParameterizedRoute("/api/v1/gym/{id}", Pattern.compile("/api/v1/gym/(?<p0>[^/]+)"));

  private final ParameterizedRoute DELETE_processor_test_BookController_delete = new ParameterizedRoute("/api/v1/book/{id}", Pattern.compile("/api/v1/book/(?<p0>[^/]+)"));

  private final ParameterizedRoute DELETE_processor_test_GymController_delete = new ParameterizedRoute("/api/v1/gym/{id}", Pattern.compile("/api/v1/gym/(?<p0>[^/]+)"));

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final String[] routeParameters = new String[]{null, null, null, null};
    if (GET_processor_test_BookController_index.matches(request)) {
      handle(new BookController(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_processor_test_BookController_bar.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      final Cursor c = Interpret.asCursor(routeParameters[1]);
      final String arg = Interpret.asString(routeParameters[2]);
      handle(new BookController(request, response), (controller) -> controller.bar(id,c,arg));
      return;
    }
    if (GET_processor_test_BookController_create.matches(request)) {
      handle(new BookController(request, response), (controller) -> controller.create());
      return;
    }
    if (GET_processor_test_BookController_foo.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      final String arg = Interpret.asString(routeParameters[1]);
      handle(new BookController(request, response), (controller) -> OAuth2Flow.Director.of(controller).authorize((c) -> c.foo(id,arg)));
      return;
    }
    if (GET_processor_test_ClientController_index.matches(request)) {
      handle(new ClientController(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_processor_test_GymController_index.matches(request)) {
      handle(new GymController(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_processor_test_GymController_create.matches(request)) {
      handle(new GymController(request, response), (controller) -> controller.create());
      return;
    }
    if (GET_processor_test_GymController_show.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new GymController(request, response), (controller) -> controller.show(id));
      return;
    }
    if (GET_processor_test_BookController_htmlIndex.matches(request)) {
      handle(new BookController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
      return;
    }
    if (GET_processor_test_BookController_show.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new BookController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.show(id));
      return;
    }
    if (GET_processor_test_BookController_edit.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new BookController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.edit(id));
      return;
    }
    if (GET_processor_test_ClientController_htmlIndex.matches(request)) {
      handle(new ClientController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
      return;
    }
    if (GET_processor_test_ClientController_create.matches(request)) {
      handle(new ClientController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.create());
      return;
    }
    if (GET_processor_test_GymController_htmlIndex.matches(request)) {
      handle(new GymController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
      return;
    }
    if (GET_processor_test_GymController_edit.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new GymController(request, response, webContext(request, response), templateEngine()), (controller) -> controller.edit(id));
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (POST_processor_test_BookController_save.matches(request)) {
      handle(new BookController(request, response), (controller) -> controller.save());
      return;
    }
    if (POST_processor_test_ClientController_save.matches(request)) {
      handle(new ClientController(request, response), (controller) -> controller.save());
      return;
    }
    if (POST_processor_test_GymController_save.matches(request)) {
      handle(new GymController(request, response), (controller) -> controller.save());
      return;
    }
    unhandledPost(request, response);
  }

  @Override
  public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final String[] routeParameters = new String[]{null, null};
    if (PUT_processor_test_BookController_update.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new BookController(request, response), (controller) -> controller.update(id));
      return;
    }
    if (PUT_processor_test_GymController_update.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new GymController(request, response), (controller) -> controller.update(id));
      return;
    }
    unhandledPut(request, response);
  }

  @Override
  public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final String[] routeParameters = new String[]{null, null};
    if (DELETE_processor_test_BookController_delete.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new BookController(request, response), (controller) -> controller.delete(id));
      return;
    }
    if (DELETE_processor_test_GymController_delete.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new GymController(request, response), (controller) -> controller.delete(id));
      return;
    }
    unhandledDelete(request, response);
  }
}