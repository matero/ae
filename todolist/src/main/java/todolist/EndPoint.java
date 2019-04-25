package todolist;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import todolist.users.User;

public abstract class EndPoint extends ae.web.AppEngineEndPointServlet
{
  protected EndPoint()
  {
  }

  @Override protected Entity getLoggedUser()
      throws EntityNotFoundException
  {
    final com.google.appengine.api.users.User user = currentUser();
    if (user == null) {
      return null;
    } else {
      return User.m.getByName(user.getUserId());
    }
  }

  @Override protected String getUserRole(final Entity userData)
  {
    return User.m.rol.of(userData);
  }

  @Override protected String getUserNamespace(final Entity userData)
  {
    return User.m.empresa.of(userData);
  }
}
