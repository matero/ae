package book;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.appengine.api.datastore.Cursor;

import javax.servlet.ServletException;
import java.io.IOException;

public class Controller extends ae.web.ControllerWithThymeleafSupport implements ae.web.OAuth2Flow {
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

  @Template public void htmlIndex() {
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
