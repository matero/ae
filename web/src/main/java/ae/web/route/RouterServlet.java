/*
 * The MIT License
 *
 * Copyright (c) 2018 ActiveEngine.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ae.web.route;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpResponseException;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ae.web.RequestHandler;
import ae.web.ThymeleafTemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public abstract class RouterServlet extends HttpServlet {
  protected RouterServlet() {
  }

  protected void unhandledDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doDelete(request, response);
  }

  protected void unhandledPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doPut(request, response);
  }

  protected void unhandledPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doPost(request, response);
  }

  protected void unhandledGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doGet(request, response);
  }

  protected void unhandledHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doHead(request, response);
  }

  protected void unhandledOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doOptions(request, response);
  }

  protected void unhandledTrace(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    super.doTrace(request, response);
  }

  protected WebContext webContext(final HttpServletRequest request, final HttpServletResponse response) {
    return new WebContext(request, response, getServletContext());
  }

  protected TemplateEngine templateEngine() {
    return ThymeleafTemplateEngine.get(getServletContext());
  }

  protected static class OAuth2FlowDirector {
    /**
     * Persisted credential associated with the current request or {@code null} for none.
     */
    private final Credential credential;
    private final RequestHandler.WithOAuth2Flow handler;

    OAuth2FlowDirector(Credential credential, RequestHandler.WithOAuth2Flow handler) {
      this.credential = credential;
      this.handler = handler;
    }

    public void authorize() throws ServletException, IOException {
      try {
        if (credential != null && credential.getAccessToken() != null) {
          try {
            handler.handle();
          } catch (final HttpResponseException e) {
            // if access token is null, assume it is because auth failed and we need to re-authorize
            // but if access token is not null, it is some other problem
            if (credential.getAccessToken() != null) {
              throw e;
            }
          }
        }
      } finally {
        if (credential == null || credential.getAccessToken() == null) {
          final AuthorizationCodeRequestUrl authorizationUrl = handler.flow().newAuthorizationUrl();
          authorizationUrl.setRedirectUri(handler.redirectUri());
          handler.onAuthorization(authorizationUrl);
        }
      }
    }

    public static OAuth2FlowDirector of(final RequestHandler.WithOAuth2Flow handler) {
      final String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
      final AuthorizationCodeFlow flow = handler.flow();
      final Credential credential;
      try {
        credential = flow.loadCredential(userId);
      } catch (final IOException ex) {
        throw new IllegalStateException("Couldn't init the Google OAuth2 flow", ex);
      }
      return new OAuth2FlowDirector(credential, handler);
    }
  }
}
