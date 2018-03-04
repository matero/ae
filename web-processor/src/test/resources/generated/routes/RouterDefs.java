
package processor.test;

import ae.web.route.ParameterizedRoute;
import ae.web.route.Route;
import ae.web.route.RouterServlet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import entrename.gyms.Create;
import entrename.gyms.Destroy;
import entrename.gyms.List;
import entrename.gyms.Show;
import entrename.gyms.Update;
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

  private final ParameterizedRoute GET_entrename_gyms_Show_route = new ParameterizedRoute("/gyms/{id: [0-9]+}", Pattern.compile("/gyms/(?<p0>[0-9]+)"), ImmutableList.of("id"));

  private final Route GET_entrename_gyms_List_route = new Route("/gyms");

  private final Route POST_entrename_gyms_Create_route = new Route("/gyms");

  private final ParameterizedRoute PUT_entrename_gyms_Update_route = new ParameterizedRoute("/gyms/{id: :digit:+}", Pattern.compile("/gyms/(?<p0>\\p{Digit}+)"), ImmutableList.of("id"));

  private final ParameterizedRoute DELETE_entrename_gyms_Destroy_route = new ParameterizedRoute("/gyms/{id: [0-9]+}", Pattern.compile("/gyms/(?<p0>[0-9]+)"), ImmutableList.of("id"));

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final ImmutableMap.Builder<String, String> routeParameters = ImmutableMap.builder();
    if (GET_entrename_gyms_Show_route.matches(request, routeParameters)) {
      new Show(request, response, routeParameters.build()).handle();
      return;
    }
    if (GET_entrename_gyms_List_route.matches(request)) {
      new List(request, response).handle();
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (POST_entrename_gyms_Create_route.matches(request)) {
      new Create(request, response).handle();
      return;
    }
    unhandledPost(request, response);
  }

  @Override
  public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final ImmutableMap.Builder<String, String> routeParameters = ImmutableMap.builder();
    if (PUT_entrename_gyms_Update_route.matches(request, routeParameters)) {
      new Update(request, response, routeParameters.build()).handle();
      return;
    }
    unhandledPut(request, response);
  }

  @Override
  public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final ImmutableMap.Builder<String, String> routeParameters = ImmutableMap.builder();
    if (DELETE_entrename_gyms_Destroy_route.matches(request, routeParameters)) {
      new Destroy(request, response, routeParameters.build()).handle();
      return;
    }
    unhandledDelete(request, response);
  }
}