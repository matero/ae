package test;

import ae.web.RouterServlet;
import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Generated;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Generated(
    value = "AE/web-processor",
    comments = "",
    date = "2017-02-23"
)
@WebServlet("/api/tasks/*")
public final class Tasks__aeImpl extends Tasks {
  private static final long serialVersionUID = 1487851200000L;

  private static final Logger LOGGER = LoggerFactory.getLogger("test.Tasks");

  private final RouterServlet.Path GET_get = parameterizedPath("/api/tasks/{id}", "/{id}", ImmutableList.of("id"), Pattern.compile("/(?<id>[^/]+)"));

  private final RouterServlet.Path GET_author = staticPath("/api/tasks/author", "/author");

  private final RouterServlet.Path PUT_update = parameterizedPath("/api/tasks/{id}", "/{id}", ImmutableList.of("id"), Pattern.compile("/(?<id>[^/]+)"));

  private final RouterServlet.Path DELETE_delete = parameterizedPath("/api/tasks/{id}", "/{id}", ImmutableList.of("id"), Pattern.compile("/(?<id>[^/]+)"));

  @Override
  protected final Logger logger() {
    return LOGGER;
  }

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final Entity userData = getLoggedUser();
    if (indexPath.matches(request)) {
      if (!userRoleIsIn(userData, "user", "configurator")) {
        notAuthorized(response);
        return;
      }
      index(request, response);
      return;
    }
    if (GET_get.matches(request)) {
      if (!userRoleIsIn(userData, "user", "configurator")) {
        notAuthorized(response);
        return;
      }
      get(request, response);
      return;
    }
    if (GET_author.matches(request)) {
      if (!userRoleIsIn(userData, "user", "configurator")) {
        notAuthorized(response);
        return;
      }
      author(request, response);
      return;
    }
    unhandledGet(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final Entity userData = getLoggedUser();
    if (indexPath.matches(request)) {
      if (!userRoleIsIn(userData, "user", "configurator")) {
        notAuthorized(response);
        return;
      }
      save(request, response);
      return;
    }
    unhandledPost(request, response);
  }

  @Override
  public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final Entity userData = getLoggedUser();
    if (PUT_update.matches(request)) {
      if (!userRoleIsIn(userData, "user", "configurator")) {
        notAuthorized(response);
        return;
      }
      update(request, response);
      return;
    }
    unhandledPut(request, response);
  }

  @Override
  public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    final Entity userData = getLoggedUser();
    if (DELETE_delete.matches(request)) {
      if (!userRoleIsIn(userData, "user", "configurator")) {
        notAuthorized(response);
        return;
      }
      delete(request, response);
      return;
    }
    unhandledDelete(request, response);
  }
}