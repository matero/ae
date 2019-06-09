package todolist.users;

import ae.GET;
import ae.web.Interpret;
import ae.web.QueryParameter;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

@ae.endpoint abstract class Auth extends todolist.EndPoint
{
  private static QueryParameter<URL> url = new QueryParameter<>(name("url"), Interpret::asUrl);

  @GET void login(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException
  {
    final String redirectToUrl = getRedirectUrlFor(request);
    final String loginURL = loginURL(redirectToUrl);
    final JsonNode data = asJsonData(loginURL);
    renderJson(response, data);
  }

  @GET void logout(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException
  {
    final String redirectToUrl = getRedirectUrlFor(request);
    final String logoutURL = logoutURL(redirectToUrl);
    final JsonNode data = asJsonData(logoutURL);
    renderJson(response, data);
  }

  JsonNode asJsonData(final String redirectToUrl)
  {
    return JsonNodeFactories.object(JsonNodeFactories.field("url", JsonNodeFactories.string(redirectToUrl)));
  }

  String getRedirectUrlFor(HttpServletRequest request)
  {
    final URL redirectUrl = get(request, url);
    if (redirectUrl == null) {
      return loginURL(request.getRequestURI());
    } else {
      return loginURL(redirectUrl.toString());
    }
  }
}
