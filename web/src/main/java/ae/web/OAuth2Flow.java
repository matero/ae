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
package ae.web;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpResponseException;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.ServletException;

public interface OAuth2Flow {

    AuthorizationCodeFlow flow();

    default AuthorizationCodeRequestUrl authorizationUrl()
    {
        return flow().newAuthorizationUrl();
    }

    String redirectUri() throws ServletException, IOException;

    void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException;

    final class Director<H extends Controller & OAuth2Flow> {

        /**
         * Persisted credential associated with the current request or {@code null} for none.
         */
        private final Credential credential;
        private final H handler;

        Director(final Credential credential, final H handler)
        {
            this.credential = credential;
            this.handler = handler;
        }

        public void authorize(final HttpRequestHandler<H> action) throws ServletException, IOException
        {
            try {
                if (hasToken()) {
                    try {
                        action.accept(handler);
                    } catch (final HttpResponseException e) {
                        // if access token is null, assume it is because auth failed and we need to re-authorize
                        // but if access token is not null, it is some other problem
                        if (credential.getAccessToken() != null) {
                            throw e;
                        }
                    }
                }
            } finally {
                if (hasToken()) {
                    final AuthorizationCodeRequestUrl authorizationUrl = handler.authorizationUrl();
                    authorizationUrl.setRedirectUri(handler.redirectUri());
                    handler.onAuthorization(authorizationUrl);
                }
            }
        }

        private boolean hasToken()
        {
            return credential != null && credential.getAccessToken() != null;
        }

        public static <H extends Controller & OAuth2Flow> Director<H> of(final H handler)
        {
            final String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
            final AuthorizationCodeFlow flow = handler.flow();
            final Credential credential;
            try {
                credential = flow.loadCredential(userId);
            } catch (final IOException ex) {
                throw new IllegalStateException("Couldn't init the Google OAuth2 flow", ex);
            }
            return new Director<>(credential, handler);
        }
    }
}
