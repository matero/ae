package ae;

import com.google.appengine.api.datastore.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEndPoint extends ae.web.AppEngineEndPointServlet
{
  protected TestEndPoint() {
    // nothing to do
  }

  @Override protected String getUserRole(final Entity userData)
  {
    return "role";
  }

  @Override protected String getUserNamespace(final Entity userData)
  {
    return "test";
  }

  @Override protected Entity getLoggedUser()
  {
    return new Entity("test-user");
  }

  @Override protected Logger logger()
  {
    return LoggerFactory.getLogger(getClass());
  }
}
