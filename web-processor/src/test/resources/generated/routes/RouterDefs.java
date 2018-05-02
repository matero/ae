package processor.test;

import ae.web.Interpret;
import ae.web.OAuth2Flow;
import ae.web.ParameterizedRoute;
import ae.web.Route;
import ae.web.RouterServlet;
import book.Controller;
import com.google.appengine.api.datastore.Cursor;
import java.io.IOException;
import java.lang.Override;
import java.lang.String;
import java.util.regex.Pattern;
import javax.annotation.Generated;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Generated(
    value = "AE/web-processor",
    comments = "Build from specs at: src/test/resources/routes/routes.csv",
    date = "2017-02-23"
)
abstract class RouterDefs extends RouterServlet {
  private static final long serialVersionUID = 1487851200000L;

  private final Route GET_book_Controller_index = new Route("/books");

  private final Route GET_book_Controller_create = new Route("/books/create");

  private final ParameterizedRoute GET_book_Controller_show = new ParameterizedRoute("/books/${id}", Pattern.compile("/books/$(?<p0>[^/]+)"));

  private final ParameterizedRoute GET_book_Controller_edit = new ParameterizedRoute("/books/${id}/edit", Pattern.compile("/books/$(?<p0>[^/]+)/edit"));

  private final ParameterizedRoute GET_book_Controller_foo = new ParameterizedRoute("/books/${id}/{$arg}", Pattern.compile("/books/$(?<p0>[^/]+)/(?<p1>[^/]+)"));

  private final ParameterizedRoute GET_book_Controller_bar = new ParameterizedRoute("/books/${id}/{$c}/{$arg}", Pattern.compile("/books/$(?<p0>[^/]+)/(?<p1>[^/]+)/(?<p2>[^/]+)"));

  private final Route GET_gym_Controller_index = new Route("/gyms");

  private final Route GET_gym_Controller_create = new Route("/gyms/create");

  private final ParameterizedRoute GET_gym_Controller_show = new ParameterizedRoute("/gyms/${id}", Pattern.compile("/gyms/$(?<p0>[^/]+)"));

  private final ParameterizedRoute GET_gym_Controller_edit = new ParameterizedRoute("/gyms/${id}/edit", Pattern.compile("/gyms/$(?<p0>[^/]+)/edit"));

  private final Route POST_book_Controller_save = new Route("/books");

  private final Route POST_gym_Controller_save = new Route("/gyms");

  private final ParameterizedRoute PUT_book_Controller_update = new ParameterizedRoute("/books/${id}", Pattern.compile("/books/$(?<p0>[^/]+)"));

  private final ParameterizedRoute PUT_gym_Controller_update = new ParameterizedRoute("/gyms/${id}", Pattern.compile("/gyms/$(?<p0>[^/]+)"));

  private final ParameterizedRoute DELETE_book_Controller_delete = new ParameterizedRoute("/books/${id}", Pattern.compile("/books/$(?<p0>[^/]+)"));

  private final ParameterizedRoute DELETE_gym_Controller_delete = new ParameterizedRoute("/gyms/${id}", Pattern.compile("/gyms/$(?<p0>[^/]+)"));

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
                                                                                          ServletException, IOException {
    final String[] routeParameters = new String[3];
    if (GET_book_Controller_index.matches(request)) {
      handle(new Controller(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_book_Controller_create.matches(request)) {
      handle(new Controller(request, response), (controller) -> controller.create());
      return;
    }
    if (GET_book_Controller_show.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller(request, response, webContext(request, response), templateEngine()), (controller) -> controller.show(id));
      return;
    }
    if (GET_book_Controller_edit.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller(request, response, webContext(request, response), templateEngine()), (controller) -> controller.edit(id));
      return;
    }
    if (GET_book_Controller_foo.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      final String arg = Interpret.asString(routeParameters[1]);
      handle(new Controller(request, response), (controller) -> OAuth2Flow.Director.of(controller).authorize((c) -> c.foo(id,arg)));
      return;
    }
    if (GET_book_Controller_bar.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      final Cursor c = Interpret.asCursor(routeParameters[1]);
      final String arg = Interpret.asString(routeParameters[2]);
      handle(new Controller(request, response), (controller) -> controller.bar(id,c,arg));
      return;
    }
    if (GET_gym_Controller_index.matches(request)) {
      handle(new gym.Controller(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_gym_Controller_create.matches(request)) {
      handle(new gym.Controller(request, response), (controller) -> controller.create());
      return;
    }
    if (GET_gym_Controller_show.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new gym.Controller(request, response), (controller) -> controller.show(id));
      return;
    }
    if (GET_gym_Controller_edit.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new gym.Controller(request, response), (controller) -> controller.edit(id));
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
                                                                                           ServletException, IOException {
    if (POST_book_Controller_save.matches(request)) {
      handle(new Controller(request, response), (controller) -> controller.save());
      return;
    }
    if (POST_gym_Controller_save.matches(request)) {
      handle(new gym.Controller(request, response), (controller) -> controller.save());
      return;
    }
    unhandledPost(request, response);
  }

  @Override
  public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws
                                                                                          ServletException, IOException {
    final String[] routeParameters = new String[1];
    if (PUT_book_Controller_update.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller(request, response), (controller) -> controller.update(id));
      return;
    }
    if (PUT_gym_Controller_update.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new gym.Controller(request, response), (controller) -> controller.update(id));
      return;
    }
    unhandledPut(request, response);
  }

  @Override
  public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws
                                                                                             ServletException, IOException {
    final String[] routeParameters = new String[1];
    if (DELETE_book_Controller_delete.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller(request, response), (controller) -> controller.delete(id));
      return;
    }
    if (DELETE_gym_Controller_delete.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new gym.Controller(request, response), (controller) -> controller.delete(id));
      return;
    }
    unhandledDelete(request, response);
  }
}
