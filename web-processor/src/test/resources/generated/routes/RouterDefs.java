package processor.test;

import ae.web.Interpret;
import ae.web.OAuth2Flow;
import ae.web.ParameterizedRoute;
import ae.web.Route;
import ae.web.RouterServlet;
import book.Controller_Impl;
import com.google.appengine.api.datastore.Cursor;
import java.io.IOException;
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

  private final Route GET_book_Controller_htmlIndex = new Route("/book");

  private final ParameterizedRoute GET_book_Controller_show = new ParameterizedRoute("/book/${id}", Pattern.compile("/book/$(?<p0>[^/]+)"));

  private final ParameterizedRoute GET_book_Controller_edit = new ParameterizedRoute("/book/${id}/edit", Pattern.compile("/book/$(?<p0>[^/]+)/edit"));

  private final ParameterizedRoute GET_book_Controller_foo = new ParameterizedRoute("/book/${id}/{$arg}", Pattern.compile("/book/$(?<p0>[^/]+)/(?<p1>[^/]+)"));

  private final ParameterizedRoute GET_book_Controller_bar = new ParameterizedRoute("/book/${id}/{$c}/{$arg}", Pattern.compile("/book/$(?<p0>[^/]+)/(?<p1>[^/]+)/(?<p2>[^/]+)"));

  private final Route GET_book_Controller_create = new Route("/book/create");

  private final Route GET_client_Controller_htmlIndex = new Route("/client");

  private final Route GET_client_Controller_create = new Route("/client/create");

  private final Route GET_gym_Controller_htmlIndex = new Route("/gym");

  private final ParameterizedRoute GET_gym_Controller_show = new ParameterizedRoute("/gym/${id}", Pattern.compile("/gym/$(?<p0>[^/]+)"));

  private final ParameterizedRoute GET_gym_Controller_edit = new ParameterizedRoute("/gym/${id}/edit", Pattern.compile("/gym/$(?<p0>[^/]+)/edit"));

  private final Route GET_gym_Controller_create = new Route("/gym/create");

  private final Route POST_book_Controller_save = new Route("/book");

  private final Route POST_client_Controller_save = new Route("/client");

  private final Route POST_gym_Controller_save = new Route("/gym");

  private final ParameterizedRoute PUT_book_Controller_update = new ParameterizedRoute("/book/${id}", Pattern.compile("/book/$(?<p0>[^/]+)"));

  private final ParameterizedRoute PUT_gym_Controller_update = new ParameterizedRoute("/gym/${id}", Pattern.compile("/gym/$(?<p0>[^/]+)"));

  private final ParameterizedRoute DELETE_book_Controller_delete = new ParameterizedRoute("/book/${id}", Pattern.compile("/book/$(?<p0>[^/]+)"));

  private final ParameterizedRoute DELETE_gym_Controller_delete = new ParameterizedRoute("/gym/${id}", Pattern.compile("/gym/$(?<p0>[^/]+)"));

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final String[] routeParameters = new String[]{null, null, null, null};
    if (GET_book_Controller_htmlIndex.matches(request)) {
      if ("text/html".equals(request.getHeader("Accept"))) {
        handle(new Controller_Impl(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
        return;
      }
      handle(new Controller_Impl(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_book_Controller_show.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller_Impl(request, response, webContext(request, response), templateEngine()), (controller) -> controller.show(id));
      return;
    }
    if (GET_book_Controller_edit.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller_Impl(request, response, webContext(request, response), templateEngine()), (controller) -> controller.edit(id));
      return;
    }
    if (GET_book_Controller_foo.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      final String arg = Interpret.asString(routeParameters[1]);
      handle(new Controller_Impl(request, response), (controller) -> OAuth2Flow.Director.of(controller).authorize((c) -> c.foo(id,arg)));
      return;
    }
    if (GET_book_Controller_bar.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      final Cursor c = Interpret.asCursor(routeParameters[1]);
      final String arg = Interpret.asString(routeParameters[2]);
      handle(new Controller_Impl(request, response), (controller) -> controller.bar(id,c,arg));
      return;
    }
    if (GET_book_Controller_create.matches(request)) {
      handle(new Controller_Impl(request, response), (controller) -> controller.create());
      return;
    }
    if (GET_client_Controller_htmlIndex.matches(request)) {
      if ("text/html".equals(request.getHeader("Accept"))) {
        handle(new client.Controller_Impl(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
        return;
      }
      handle(new client.Controller_Impl(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_client_Controller_create.matches(request)) {
      handle(new client.Controller_Impl(request, response, webContext(request, response), templateEngine()), (controller) -> controller.create());
      return;
    }
    if (GET_gym_Controller_htmlIndex.matches(request)) {
      if ("text/html".equals(request.getHeader("Accept"))) {
        handle(new gym.Controller_Impl(request, response, webContext(request, response), templateEngine()), (controller) -> controller.htmlIndex());
        return;
      }
      handle(new gym.Controller_Impl(request, response), (controller) -> controller.index());
      return;
    }
    if (GET_gym_Controller_show.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new gym.Controller_Impl(request, response), (controller) -> controller.show(id));
      return;
    }
    if (GET_gym_Controller_edit.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new gym.Controller_Impl(request, response), (controller) -> controller.edit(id));
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (POST_book_Controller_save.matches(request)) {
      handle(new Controller_Impl(request, response), (controller) -> controller.save());
      return;
    }
    if (POST_client_Controller_save.matches(request)) {
      handle(new client.Controller_Impl(request, response), (controller) -> controller.save());
      return;
    }
    unhandledPost(request, response);
  }

  @Override
  public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final String[] routeParameters = new String[]{null, null};
    if (PUT_book_Controller_update.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller_Impl(request, response), (controller) -> controller.update(id));
      return;
    }
    unhandledPut(request, response);
  }

  @Override
  public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final String[] routeParameters = new String[]{null, null};
    if (DELETE_book_Controller_delete.matches(request, routeParameters)) {
      final long id = Interpret.asPrimitiveLong(routeParameters[0]);
      handle(new Controller_Impl(request, response), (controller) -> controller.delete(id));
      return;
    }
    unhandledDelete(request, response);
  }
}
