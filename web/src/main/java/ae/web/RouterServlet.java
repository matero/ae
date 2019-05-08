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

import ae.web.RouterServlet.Path;
import com.google.appengine.api.datastore.Cursor;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RouterServlet extends javax.servlet.http.HttpServlet
{

  protected interface Path extends Serializable
  {

    boolean matches(final HttpServletRequest request);
  }

  protected RouterServlet()
  {
    // nothing more to do
  }

  /**
   * @return the logger to be used at the controller.
   */
  protected abstract Logger logger();

  protected void unhandledGet(final HttpServletRequest request,
                              final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doGet(request, response);
  }

  protected void unhandledDelete(final HttpServletRequest request,
                                 final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doDelete(request, response);
  }

  protected void unhandledPut(final HttpServletRequest request,
                              final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doPut(request, response);
  }

  protected void unhandledPost(final HttpServletRequest request,
                               final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doPost(request, response);
  }

  protected void unhandledHead(final HttpServletRequest request,
                               final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doHead(request, response);
  }

  protected void unhandledOptions(final HttpServletRequest request,
                                  final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doOptions(request, response);
  }

  protected void unhandledTrace(final HttpServletRequest request,
                                final HttpServletResponse response)
      throws ServletException, IOException
  {
    super.doTrace(request, response);
  }

  protected void notAuthorized(final HttpServletResponse response)
  {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  protected static Path indexPath = IndexPath.INSTANCE;

  protected static Path staticPath(final String uri,
                                   final String path)
  {
    return new StaticPath(uri, path);
  }

  protected static Path parameterizedPath(final String uri,
                                          final String pattern,
                                          final ImmutableList<String> parameters,
                                          final Pattern regex)
  {
    return new ParameterizedPath(uri, pattern, parameters, regex);
  }

  /*
  request query parameters factories
   */
  protected static final Required required = Required.YES;

  protected static final Required notRequired = Required.NO;

  protected static final QueryParameter<Cursor> CursorQueryParameter(final String name)
  {
    return new QueryParameter<>(name, notRequired, Interpret::asCursor, nullDefaultValue());
  }

  protected static final QueryParameter<Cursor> CursorQueryParameter(final String name, final Required required)
  {
    return new QueryParameter<>(name, required, Interpret::asCursor, nullDefaultValue());
  }

  protected static final QueryParameter<Cursor> CursorQueryParameter(final String name, final Required required, final Cursor defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asCursor, makeDefaultValueProvider(defaultValue));
  }

  protected static final QueryParameter<Cursor> CursorQueryParameter(final String name, final Required required, final Supplier<Cursor> defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asCursor, defaultValue);
  }

  protected static final QueryParameter<Integer> IntegerQueryParameter(final String name)
  {
    return new QueryParameter<>(name, notRequired, Interpret::asInteger, nullDefaultValue());
  }

  protected static final QueryParameter<Integer> IntegerQueryParameter(final String name, final Required required)
  {
    return new QueryParameter<>(name, required, Interpret::asInteger, nullDefaultValue());
  }

  protected static final QueryParameter<Integer> IntegerQueryParameter(final String name, final Required required, final Integer defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asInteger, makeDefaultValueProvider(defaultValue));
  }

  protected static final QueryParameter<Integer> IntegerQueryParameter(final String name, final Required required, final Supplier<Integer> defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asInteger, defaultValue);
  }

  protected static final QueryParameter<Long> LongQueryParameter(final String name)
  {
    return new QueryParameter<>(name, notRequired, Interpret::asLong, nullDefaultValue());
  }

  protected static final QueryParameter<Long> LongQueryParameter(final String name, final Required required)
  {
    return new QueryParameter<>(name, required, Interpret::asLong, nullDefaultValue());
  }

  protected static final QueryParameter<Long> LongQueryParameter(final String name, final Required required, final Long defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asLong, makeDefaultValueProvider(defaultValue));
  }

  protected static final QueryParameter<Long> LongQueryParameter(final String name, final Required required, final Supplier<Long> defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asLong, defaultValue);
  }

  protected static final QueryParameter<String> StringQueryParameter(final String name)
  {
    return new QueryParameter<>(name, notRequired, Interpret::asString, nullDefaultValue());
  }

  protected static final QueryParameter<String> StringQueryParameter(final String name, final Required required)
  {
    return new QueryParameter<>(name, required, Interpret::asString, nullDefaultValue());
  }

  protected static final QueryParameter<String> StringQueryParameter(final String name, final Required required, final String defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asString, makeDefaultValueProvider(defaultValue));
  }

  protected static final QueryParameter<String> StringQueryParameter(final String name, final Required required, final Supplier<String> defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asString, defaultValue);
  }

  protected static final QueryParameter<List<String>> StringListQueryParameter(final String name)
  {
    return new QueryParameter<>(name, notRequired, Interpret::asStringList, nullDefaultValue());
  }

  protected static final QueryParameter<List<String>> StringListQueryParameter(final String name, final Required required)
  {
    return new QueryParameter<>(name, required, Interpret::asStringList, nullDefaultValue());
  }

  protected static final QueryParameter<List<String>> StringListQueryParameter(final String name, final Required required, final Collection<String> defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asStringList, makeListDefaultValueProvider(defaultValue));
  }

  protected static final QueryParameter<List<String>> StringListQueryParameter(final String name, final Required required, final String... defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asStringList, makeListDefaultValueProvider(defaultValue));
  }

  protected static final QueryParameter<List<String>> StringListQueryParameter(final String name, final Required required, final Supplier<List<String>> defaultValue)
  {
    return new QueryParameter<>(name, required, Interpret::asStringList, defaultValue);
  }

  protected static final <T> Supplier<T> makeDefaultValueProvider(final T defaultValue)
  {
    if (defaultValue == null) {
      return nullDefaultValue();
    } else {
      return () -> defaultValue;
    }
  }

  protected static final <T> Supplier<List<T>> makeListDefaultValueProvider(final T... defaultValue)
  {
    if (defaultValue == null) {
      return nullDefaultValue();
    } else if (defaultValue.length == 0) {
      return () -> new ArrayList<>(0);
    } else {
      final List<T> result = new ArrayList<>(Arrays.asList(defaultValue));
      return () -> new ArrayList<>(result);
    }
  }

  protected static final <T> Supplier<List<T>> makeListDefaultValueProvider(final Collection<T> defaultValue)
  {
    if (defaultValue == null) {
      return nullDefaultValue();
    } else if (defaultValue.isEmpty()) {
      return () -> new ArrayList<>(0);
    } else {
      final List<T> result = new ArrayList<>(defaultValue);
      return () -> new ArrayList<>(result);
    }
  }

  protected static final <T> Supplier<T> nullDefaultValue()
  {
    return () -> null;
  }
}

enum IndexPath implements Path
{
  INSTANCE;

  @Override
  public boolean matches(final HttpServletRequest request)
  {
    final String pathInfo = request.getPathInfo();
    return null == pathInfo || "".equals(pathInfo) || "/".equals(pathInfo);
  }

  @Override
  public String toString()
  {
    return "Path{'/'}";
  }
}

final class StaticPath implements Path
{

  private final String uri;
  private final String path;

  StaticPath(final String uri,
             final String path)
  {
    this.uri = uri;
    this.path = path;
  }

  @Override
  public String toString()
  {
    return "Path{'" + uri + "'}";
  }

  @Override
  public int hashCode()
  {
    return uri.hashCode();
  }

  @Override
  public boolean equals(final Object that)
  {
    if (this == that) {
      return true;
    }
    if (that instanceof StaticPath) {
      final StaticPath other = (StaticPath) that;
      return uri.equals(other.uri);
    }
    return false;
  }

  @Override
  public boolean matches(final HttpServletRequest request)
  {
    return path.equals(request.getPathInfo());
  }
}

final class ParameterizedPath implements Path
{

  private final String uri;
  private final String pattern;
  private final Pattern regex;
  private final ImmutableList<String> parameters;

  ParameterizedPath(final String uri,
                    final String pattern,
                    final ImmutableList<String> parameters,
                    final Pattern regex)
  {
    this.uri = uri;
    this.pattern = pattern;
    this.regex = regex;
    this.parameters = parameters;
  }

  @Override
  public String toString()
  {
    return "Path{'" + pattern + "'}";
  }

  @Override
  public int hashCode()
  {
    return pattern.hashCode();
  }

  @Override
  public boolean equals(final Object that)
  {
    if (this == that) {
      return true;
    }
    if (that instanceof ParameterizedPath) {
      final ParameterizedPath other = (ParameterizedPath) that;
      return uri.equals(other.uri);
    }
    return false;
  }

  public boolean matches(final HttpServletRequest request)
  {
    if (request.getPathInfo() == null) {
      return false;
    }
    final Matcher matcher = regex.matcher(request.getPathInfo());
    if (matcher.matches()) {
      for (final String parameter : parameters) {
        request.setAttribute(parameter, matcher.group(parameter));
      }
      return true;
    } else {
      return false;
    }
  }
}
