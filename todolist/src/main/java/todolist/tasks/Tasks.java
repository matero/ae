package todolist.tasks;

import ae.GET;
import ae.endpoint;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@endpoint abstract class Tasks extends todolist.EndPoint
{
  @GET void index(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException
  {
    final List<Entity> entities = Task.m.all(getFetchOptions(request));
    renderJson(response, Task.m.toJson(entities));
  }
}