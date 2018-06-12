package book;

import ae.OAuth2;
import ae.Template;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.appengine.api.datastore.Cursor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public final class Controller extends ae.web.ControllerWithThymeleafSupport implements ae.web.OAuth2Flow {
  private static final Logger LOG = Logger.getLogger(Controller.class.getCanonicalName());

  public Controller(final HttpServletRequest request, final HttpServletResponse response) {
    super(request, response);
  }

  public Controller(final HttpServletRequest request,
                    final HttpServletResponse response,
                    final WebContext webContext,
                    final TemplateEngine templateEngine) {
    super(request, response, webContext, templateEngine);
  }

  @Override protected Logger log() {
    return LOG;
  }


  @Override public AuthorizationCodeFlow flow() {
    return null;
  }

  @Override public AuthorizationCodeRequestUrl authorizationUrl() {
    return null;
  }

  @Override public String redirectUri() throws ServletException, IOException {
    return null;
  }

  @Override public void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException {
    // nothing to do
  }

  public void index() {
    // nothing to do
  }

  public void create() {
    // nothing to do
  }

  public void save() {
    // nothing to do
  }

  @Template public void show(final long id) {
    // nothing to do
  }

  @Template public void edit(final long id) {
    // nothing to do
  }

  public void update(final long id) {
    // nothing to do
  }

  public void delete(final long id) {
    // nothing to do
  }

  @OAuth2 public void foo(final long id, final String arg) {
    // nothing to do
  }

  public void bar(final long id, final Cursor c, final String arg) {
    // nothing to do
  }
}
