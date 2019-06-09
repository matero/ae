/*
 * The MIT License
 *
 * Copyright 2018 AppEngine.
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

import ae.db.ActiveEntity;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.ImmutableList;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

public abstract class AppEngineEndPointServlet extends EndPointServlet
{
  protected AppEngineEndPointServlet()
  {
    // nothing to do
  }

  protected boolean userRoleIs(final Entity userData, final String role)
  {
    final String r = getUserRole(userData);
    return r.equals(role);
  }

  protected boolean userRoleIsIn(final Entity userData, final String r1, final String r2)
  {
    final String r = getUserRole(userData);
    return r.equals(r1) || r.equals(r2);
  }

  protected boolean userRoleIsIn(final Entity userData, final String r1, final String r2, final String r3)
  {
    final String r = getUserRole(userData);
    return r.equals(r1) || r.equals(r2) || r.equals(r3);
  }

  protected boolean userRoleIsIn(final Entity userData,
                                 final String r1,
                                 final String r2,
                                 final String r3,
                                 final String r4)
  {
    final String r = getUserRole(userData);
    return r.equals(r1) || r.equals(r2) || r.equals(r3) || r.equals(r4);
  }

  protected abstract String getUserRole(final Entity userData);

  protected abstract String getUserNamespace(final Entity userData);

  protected abstract Entity getLoggedUser()
      throws EntityNotFoundException;

  protected void useNamespace(final String namespace)
  {
    if (NamespaceManager.get() == null) {
      NamespaceManager.set(namespace);
    }
  }

  protected String loginURL(final HttpServletRequest request)
  {
    return loginURL(request.getRequestURI());
  }

  protected String loginURL(final String destinationURL)
  {
    return UserServiceFactory.getUserService().createLoginURL(destinationURL);
  }

  protected String loginURL(final String destinationURL, final String authDomain)
  {
    return UserServiceFactory.getUserService().createLoginURL(destinationURL, authDomain);
  }

  protected String logoutURL(final HttpServletRequest request)
  {
    return logoutURL(request.getRequestURI());
  }

  protected String logoutURL(final String destinationURL)
  {
    return UserServiceFactory.getUserService().createLogoutURL(destinationURL);
  }

  protected String logoutURL(final String destinationURL, final String authDomain)
  {
    return UserServiceFactory.getUserService().createLogoutURL(destinationURL, authDomain);
  }

  protected boolean isUserLoggedIn()
  {
    return UserServiceFactory.getUserService().isUserLoggedIn();
  }

  protected boolean isUserAdmin()
  {
    return UserServiceFactory.getUserService().isUserAdmin();
  }

  protected final String currentUserId()
  {
    final User current = currentUser();
    if (current == null) {
      return null;
    } else {
      return current.getUserId();
    }
  }

  protected final User currentUser()
  {
    return UserServiceFactory.getUserService().getCurrentUser();
  }

  private static final JsonStringNode cursor = JsonNodeFactories.string("cursor");
  private static final JsonStringNode data = JsonNodeFactories.string("data");

  protected JsonNode buildPage(final ActiveEntity ae, final QueryResultList<Entity> page)
  {
    return JsonNodeFactories.object(
        JsonNodeFactories.field(cursor, JsonNodeFactories.string(page.getCursor().toWebSafeString())),
        JsonNodeFactories.field(data, ae.toJson(page))
    );
  }

  protected DatastoreService datastore()
  {
    return DatastoreServiceFactory.getDatastoreService();
  }

  protected AsyncDatastoreService asyncDatastore()
  {
    return DatastoreServiceFactory.getAsyncDatastoreService();
  }

  protected interface option
  {
    QueryParameter<Integer> chunk = new QueryParameter<>(name("chunk"), notRequired, Interpret::asInteger);
    QueryParameter<Cursor> end = new QueryParameter<>(name("end"), notRequired, Interpret::asCursor);
    QueryParameter<Integer> limit = new QueryParameter<>(name("limit"), notRequired, Interpret::asInteger);
    QueryParameter<Integer> offset = new QueryParameter<>(name("offset"), notRequired, Interpret::asInteger);
    QueryParameter<Integer> prefetch = new QueryParameter<>(name("prefetch"), notRequired, Interpret::asInteger);
    QueryParameter<Cursor> start = new QueryParameter<>(name("start"), notRequired, Interpret::asCursor);
    QueryParameter<List<String>> fields = new QueryParameter(name("fields"), notRequired, Interpret::asStringList);
  }

  protected final FetchOptions getFetchOptions(final HttpServletRequest request)
  {
    final FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

    if (has(request, option.chunk)) {
      fetchOptions.chunkSize(get(request, option.chunk));
    }
    if (has(request, option.end)) {
      fetchOptions.endCursor(get(request, option.end));
    }
    if (has(request, option.limit)) {
      fetchOptions.limit(get(request, option.limit));
    }
    if (has(request, option.offset)) {
      fetchOptions.offset(get(request, option.offset));
    }
    if (has(request, option.prefetch)) {
      fetchOptions.prefetchSize(get(request, option.prefetch));
    }
    if (has(request, option.start)) {
      fetchOptions.startCursor(get(request, option.start));
    }

    return fetchOptions;
  }

  private static final QueryParameter<String> sort;

  static {
    sort = new QueryParameter<>(name("sort"), notRequired, Interpret::asString);
  }

  protected final Iterable<Query.SortPredicate> getSorts(final HttpServletRequest request)
  {
    if (has(request, sort)) {
      final String sortPredicatesSeparatedByCommas = get(request, sort);

      if (!sortPredicatesSeparatedByCommas.isEmpty()) {
        final String[] sortPredicates = sortPredicatesSeparatedByCommas.split(",");
        switch (sortPredicates.length) {
          case 1:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]));
          case 2:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(
                                        sortPredicates[1]));
          case 3:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]));
          case 4:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]));
          case 5:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]));
          case 6:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]));
          case 7:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]),
                                    asQuerySortPredicate(sortPredicates[6]));
          case 8:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]),
                                    asQuerySortPredicate(sortPredicates[6]));
          case 9:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]),
                                    asQuerySortPredicate(sortPredicates[6]),
                                    asQuerySortPredicate(sortPredicates[7]),
                                    asQuerySortPredicate(sortPredicates[8]));
          case 10:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]),
                                    asQuerySortPredicate(sortPredicates[6]),
                                    asQuerySortPredicate(sortPredicates[7]),
                                    asQuerySortPredicate(sortPredicates[8]),
                                    asQuerySortPredicate(sortPredicates[9]));
          case 11:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]),
                                    asQuerySortPredicate(sortPredicates[6]),
                                    asQuerySortPredicate(sortPredicates[7]),
                                    asQuerySortPredicate(sortPredicates[8]),
                                    asQuerySortPredicate(sortPredicates[9]),
                                    asQuerySortPredicate(sortPredicates[10]));
          case 12:
            return ImmutableList.of(asQuerySortPredicate(sortPredicates[0]),
                                    asQuerySortPredicate(sortPredicates[1]),
                                    asQuerySortPredicate(sortPredicates[2]),
                                    asQuerySortPredicate(sortPredicates[3]),
                                    asQuerySortPredicate(sortPredicates[4]),
                                    asQuerySortPredicate(sortPredicates[5]),
                                    asQuerySortPredicate(sortPredicates[6]),
                                    asQuerySortPredicate(sortPredicates[7]),
                                    asQuerySortPredicate(sortPredicates[8]),
                                    asQuerySortPredicate(sortPredicates[9]),
                                    asQuerySortPredicate(sortPredicates[10]),
                                    asQuerySortPredicate(sortPredicates[11]));
          default:
            throw new IllegalArgumentException(
                "too much sort predicates defined at request (sortedBy="
                    + sortPredicatesSeparatedByCommas
                    + ')');
        }
      }
    }
    return ImmutableList.of();
  }

  protected final Query.SortPredicate asQuerySortPredicate(final String value)
  {
    switch (value.charAt(0)) {
      case '-':
        return new Query.SortPredicate(value.substring(1), Query.SortDirection.DESCENDING);
      case '+':
        return new Query.SortPredicate(value.substring(1), Query.SortDirection.ASCENDING);
      default:
        return new Query.SortPredicate(value, Query.SortDirection.ASCENDING);
    }
  }
}
