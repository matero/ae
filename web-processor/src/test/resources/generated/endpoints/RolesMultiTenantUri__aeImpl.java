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
@WebServlet("/admin/tasks/*")
public final class Tasks__aeImpl extends Tasks {
  private static final long serialVersionUID = 1487851200000L;

  private static final Logger LOGGER = LoggerFactory.getLogger("test.Tasks");

  private final RouterServlet.Path GET_get = parameterizedPath("/admin/tasks/{id}", "/{id}", ImmutableList.of("id"), Pattern.compile("/(?<id>[^/]+)"));

  private final RouterServlet.Path GET_author = staticPath("/admin/tasks/author", "/author");

  private final RouterServlet.Path PUT_update = parameterizedPath("/admin/tasks/{id}", "/{id}", ImmutableList.of("id"), Pattern.compile("/(?<id>[^/]+)"));

  private final RouterServlet.Path DELETE_delete = parameterizedPath("/admin/tasks/{id}", "/{id}", ImmutableList.of("id"), Pattern.compile("/(?<id>[^/]+)"));

  @Override
  protected final Logger logger() {
    return LOGGER;
  }

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (indexPath.matches(request)) {
      index(request, response);
      return;
    }
    final Entity userData = getLoggedUser();
    if (GET_get.matches(request)) {
      if (!userRoleIs(userData, "sysadmin")) {
        notAuthorized(response);
        return;
      }
      useNamespace(getUserNamespace(userData));
      get(request, response);
      return;
    }
    if (GET_author.matches(request)) {
      if (!userRoleIsIn(userData, "sys", "cfg", "other")) {
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
      useNamespace(getUserNamespace(userData));
      save(request, response);
      return;
    }
    unhandledPost(request, response);
  }

  @Override
  public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (PUT_update.matches(request)) {
      update(request, response);
      return;
    }
    unhandledPut(request, response);
  }

  @Override
  public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws
      ServletException, IOException {
    if (DELETE_delete.matches(request)) {
      delete(request, response);
      return;
    }
    unhandledDelete(request, response);
  }
}