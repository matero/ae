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

import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ae.db.ActiveEntity;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

abstract class Handler extends ae.HasLogger implements RequestHandler {
  protected final HttpServletRequest request;
  protected final HttpServletResponse response;

  /* construction and initialization *************************************** */
  Handler(final HttpServletRequest request, final HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  @Override public void handle() throws ServletException, IOException {
    try {
      setup();
      h();
    } finally {
      teardown();
    }
  }

  protected void setup() throws ServletException, IOException {
    // nothing to do
  }

  protected abstract void h() throws ServletException, IOException;

  protected void teardown() throws ServletException, IOException {
    // nothing to do
  }

  @Override protected Logger logger() {
    return Logger.getLogger(getClass().getCanonicalName());
  }

  /* request manipulation ************************************************** */
  protected String readBody() throws ServletException, IOException {
    final BufferedReader reader = request.getReader();
    final StringBuilder requestBody = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      requestBody.append(line);
    }

    return requestBody.toString();
  }

  protected JsonNode readJson() throws IOException, ServletException {
    try {
      return readJson(request.getReader());
    } catch (final InvalidSyntaxException e) {
      throw new ServletException(e);
    }
  }

  protected JsonNode readJson(final Reader reader) throws IOException, InvalidSyntaxException {
    return JDOM_PARSER.parse(reader);
  }

  protected JsonNode readJson(final String content) throws IOException, InvalidSyntaxException {
    return JDOM_PARSER.parse(content);
  }

  /* response manipulation ************************************************* */
  protected static String to(final String location) {
    return location;
  }

  protected static int withStatus(final int httpStatusCode) {
    return httpStatusCode;
  }

  /**
   * Trigger a browser redirect
   *
   * @param location Where to redirect
   */
  protected void redirect(final String location) {
    if (isLoggable(Level.FINE)) {
      logFine("Redirecting ('Found', " + HttpServletResponse.SC_FOUND + " to " + location + ')');
    }
    try {
      response.sendRedirect(location);
    } catch (final IOException e) {
      log(Level.WARNING, "Redirect failure", e);
    }
  }

  /**
   * Trigger a browser redirect named specific http 3XX status code.
   *
   * @param location Where to redirect permanently
   * @param httpStatusCode the http status code
   */
  protected void redirect(final String location, final int httpStatusCode) {
    if (isLoggable(Level.FINE)) {
      logFine("Redirecting (" + httpStatusCode + " to " + location + ')');
    }
    response.setStatus(httpStatusCode);
    response.setHeader("Location", location);
    response.setHeader("Connection", "close");
    try {
      response.sendError(httpStatusCode);
    } catch (final IOException e) {
      log(Level.WARNING, "Exception when trying to redirect permanently", e);
    }
  }

  protected String format(final JsonNode json) {
    return JSON_FORMATTER.format(json);
  }

  /* contents updaters */
  protected void writeHtml(final CharSequence content) throws ServletException, IOException {
    set(ContentType.TEXT_HTML);
    send(content);
  }

  protected void writeXhtml(final CharSequence content) throws ServletException, IOException {
    set(ContentType.TEXT_XHTML);
    send(content);
  }

  protected void renderJson(final JsonNode json) throws ServletException, IOException {
    writeJson(JSON_FORMATTER.format(json));
  }

  protected void writeJson(final CharSequence content) throws ServletException, IOException {
    set(ContentType.APPLICATION_JSON);
    send(content);
  }

  protected void writeText(final CharSequence content) throws ServletException, IOException {
    set(ContentType.TEXT_PLAIN);
    send(content);
  }

  /**
   * Writes the string content directly to the response.
   *
   * This method commits the response.
   *
   * @param response the response to write.
   * @param content the content to write into the response.
   * @throws javax.servlet.ServletException if the response is already committed.
   * @throws java.io.IOException
   */
  void send(final CharSequence content) throws ServletException, IOException {
    if (response.isCommitted()) {
      throw new ServletException("The response has already been committed");
    }
    if (content == null) {
      commit(null);
    } else {
      commit(content.toString());
    }
  }

  void commit(final String content) throws IOException {
    if (response.getContentType() == null) {
      set(ContentType.TEXT_HTML);
    }
    if (content != null) {
      response.setContentLength(content.getBytes().length);
      response.getWriter().append(content);
    }
    set(StatusCode.OK);
  }

  protected void unprocessableEntity() {
    response.setStatus(422);
  }

  protected void notFound() {
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /* ************************************************************************
   * GAE users management
   */
  protected String loginURL(final HttpServletRequest request) {
    return loginURL(request.getRequestURI());
  }

  protected String loginURL(final String destinationURL) {
    return UserServiceFactory.getUserService().createLoginURL(destinationURL);
  }

  protected String loginURL(final String destinationURL, final String authDomain) {
    return UserServiceFactory.getUserService().createLoginURL(destinationURL, authDomain);
  }

  protected String logoutURL(final HttpServletRequest request) {
    return logoutURL(request.getRequestURI());
  }

  protected String logoutURL(final String destinationURL) {
    return UserServiceFactory.getUserService().createLogoutURL(destinationURL);
  }

  protected String logoutURL(final String destinationURL, final String authDomain) {
    return UserServiceFactory.getUserService().createLogoutURL(destinationURL, authDomain);
  }

  protected boolean isUserLoggedIn() {
    return UserServiceFactory.getUserService().isUserLoggedIn();
  }

  protected boolean isUserAdmin() {
    return UserServiceFactory.getUserService().isUserAdmin();
  }

  protected final String currentUserId() {
    final User current = currentUser();
    if (current == null) {
      return null;
    }
    return current.getUserId();
  }

  protected final User currentUser() {
    return UserServiceFactory.getUserService().getCurrentUser();
  }

  public static class ParameterNotDefinedException extends RuntimeException {
    public ParameterNotDefinedException(final String parameterName) {
      super(parameterName);
    }
  }

  protected static final JdomParser JDOM_PARSER;

  static {
    JDOM_PARSER = new JdomParser();
  }
  protected static final PrettyJsonFormatter PRETTY_JSON;

  static {
    PRETTY_JSON = new PrettyJsonFormatter();
  }
  protected static final CompactJsonFormatter COMPACT_JSON;

  static {
    COMPACT_JSON = new CompactJsonFormatter();
  }
  private static JsonFormatter JSON_FORMATTER;

  static {
    JSON_FORMATTER = PRETTY_JSON;
  }

  public synchronized static final void usePrettyJsonFormat() {
    JSON_FORMATTER = PRETTY_JSON;
  }

  public synchronized static final void useCompactJsonFormat() {
    JSON_FORMATTER = COMPACT_JSON;
  }

  protected static final class StatusCode {
    public static final StatusCode OK = new StatusCode(200);
    public static final StatusCode CREATED = new StatusCode(201);
    public static final StatusCode ACCEPTED = new StatusCode(202);
    public static final StatusCode PARTIAL_INFO = new StatusCode(203);
    public static final StatusCode NO_RESPONSE = new StatusCode(204);
    public static final StatusCode MOVED = new StatusCode(301);
    public static final StatusCode FOUND = new StatusCode(302);
    public static final StatusCode METHOD = new StatusCode(303);
    public static final StatusCode NOT_MODIFIED = new StatusCode(304);
    public static final StatusCode BAD_REQUEST = new StatusCode(400);
    public static final StatusCode UNAUTHORIZED = new StatusCode(401);
    public static final StatusCode PAYMENT_REQUIRED = new StatusCode(402);
    public static final StatusCode FORBIDDEN = new StatusCode(403);
    public static final StatusCode NOT_FOUND = new StatusCode(404);
    public static final StatusCode METHOD_NOT_ALLOWED = new StatusCode(405);
    public static final StatusCode CONFLICT = new StatusCode(409);
    public static final StatusCode GONE = new StatusCode(410);
    public static final StatusCode UNPROCESSABLE_ENTITY = new StatusCode(422);
    public static final StatusCode INTERNAL_ERROR = new StatusCode(500);
    public static final StatusCode NOT_IMPLEMENTED = new StatusCode(501);
    public static final StatusCode OVERLOADED = new StatusCode(502);
    public static final StatusCode SERVICE_UNAVAILABLE = new StatusCode(503);
    public static final StatusCode GATEWAY_TIMEOUT = new StatusCode(504);

    private final int value;

    private StatusCode(final int value) {
      this.value = value;
    }

    public static final StatusCode of(final int value) {
      if (value < 0) {
        throw new IllegalArgumentException("value < 0");
      }
      return new StatusCode(value);
    }

    @Override public int hashCode() {
      return Integer.hashCode(value);
    }

    @Override public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof StatusCode) {
        final StatusCode other = (StatusCode) obj;
        return value == other.value;
      }
      return false;
    }

    @Override public String toString() {
      return "StatusCode{" + value + '}';
    }
  }

  protected void set(final StatusCode statusCode) {
    response.setStatus(statusCode.value);
  }

  protected static final class ContentType {
    public static final ContentType APPLICATION_FORM_URLENCODED = new ContentType("application/x-www-form-urlencoded");
    public static final ContentType APPLICATION_JSON = new ContentType("application/json");
    public static final ContentType APPLICATION_XML = new ContentType("application/xml");
    public static final ContentType APPLICATION_X_YAML = new ContentType("application/x-yaml");
    public static final ContentType TEXT_HTML = new ContentType("text/html");
    public static final ContentType TEXT_XHTML = new ContentType("text/xhtml");
    public static final ContentType TEXT_PLAIN = new ContentType("text/plain");
    public static final ContentType APPLICATION_OCTET_STREAM = new ContentType("application/octet-stream");
    public static final ContentType MULTIPART_FORM_DATA = new ContentType("multipart/form-data");

    private final String value;

    private ContentType(final String value) {
      this.value = value;
    }

    public static final ContentType of(final String value) {
      if (value == null) {
        throw new NullPointerException("value");
      }
      return new ContentType(value);
    }

    @Override public int hashCode() {
      return value.hashCode();
    }

    @Override public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof ContentType) {
        final ContentType other = (ContentType) obj;
        return value.equals(other.value);
      }
      return false;
    }

    @Override public String toString() {
      return "ContentType{" + value + '}';
    }
  }

  protected void set(final ContentType contentType) {
    response.setContentType(contentType.value);
  }

  protected final static class Header {
    public static final Header ACCEPT = new Header("Accept");
    public static final Header ACCEPT_CHARSET = new Header("Accept-Charset");
    public static final Header ACCEPT_ENCODING = new Header("Accept-Encoding");
    public static final Header ACCEPT_LANGUAGE = new Header("Accept-Language");
    public static final Header ACCEPT_DATETIME = new Header("Accept-Datetime");
    public static final Header AUTHORIZATION = new Header("Authorization");
    public static final Header PRAGMA = new Header("Pragma");
    public static final Header CACHE_CONTROL = new Header("Cache-Control");
    public static final Header CONNECTION = new Header("Connection");
    public static final Header CONTENT_TYPE = new Header("Content-Type");
    public static final Header CONTENT_LENGTH = new Header("Content-Length");
    public static final Header CONTENT_MD5 = new Header("Content-MD5");
    public static final Header CONTENT_DISPOSITION = new Header("Content-Disposition");
    public static final Header DATE = new Header("Date");
    public static final Header ETAG = new Header("Etag");
    public static final Header EXPIRES = new Header("Expires");
    public static final Header IF_MATCH = new Header("If-Match");
    public static final Header IF_MODIFIED_SINCE = new Header("If-Modified-Since");
    public static final Header IF_NONE_MATCH = new Header("If-None-Match");
    public static final Header USER_AGENT = new Header("User-Agent");
    public static final Header HOST = new Header("Host");
    public static final Header LAST_MODIFIED = new Header("Last-Modified");
    public static final Header LOCATION = new Header("Location");

    private final String name;

    private Header(final String value) {
      this.name = value;
    }

    public static final Header of(final String value) {
      if (value == null) {
        throw new NullPointerException("value");
      }
      return new Header(value);
    }

    @Override public int hashCode() {
      return name.hashCode();
    }

    @Override public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof Header) {
        final Header other = (Header) obj;
        return name.equals(other.name);
      }
      return false;
    }

    @Override public String toString() {
      return "Header{" + name + '}';
    }
  }

  protected void set(final Header header, final String value) {
    response.setHeader(header.name, value);
  }

  protected void set(final Header header, final int value) {
    response.setIntHeader(header.name, value);
  }

  protected void set(final Header header, final Date value) {
    response.setDateHeader(header.name, value.getTime());
  }

  protected void set(final Header header, final long timestamp) {
    response.setDateHeader(header.name, timestamp);
  }

  protected void add(final Header header, final String value) {
    response.addHeader(header.name, value);
  }

  protected void add(final Header header, final int value) {
    response.addIntHeader(header.name, value);
  }

  protected void add(final Header header, final Date value) {
    response.addDateHeader(header.name, value.getTime());
  }

  protected void add(final Header header, final long timestamp) {
    response.addDateHeader(header.name, timestamp);
  }

  protected static String name(final String value) {
    return value;
  }

  protected static boolean trimmed(final boolean value) {
    return value;
  }

  public static abstract class HttpParameter<T, C extends HttpParameter<T, C>> {

    private final String name;
    private boolean required;
    private Supplier<T> defaultValue;

    protected HttpParameter(final String name, final boolean required, final Supplier<T> defaultValue) {
      this.name = name;
      this.required = required;
      this.defaultValue = defaultValue;
    }

    public final boolean isDefinedAt(final HttpServletRequest request) {
      return request.getParameterMap().containsKey(name);
    }

    public final boolean isDefinedAt(final Map<String, String> parameters) {
      return parameters.containsKey(name);
    }

    public final T of(final HttpServletRequest request) {
      if (isDefinedAt(request)) {
        return interpret(read(request));
      } else {
        if (required) {
          throw new ParameterNotDefinedException(name);
        }
        return defaultValue.get();
      }
    }

    public final T of(final Map<String, String> parameters) {
      if (isDefinedAt(parameters)) {
        return interpret(read(parameters));
      } else {
        if (required) {
          throw new ParameterNotDefinedException(name);
        }
        return defaultValue.get();
      }
    }

    protected abstract T interpret(String value);

    protected String read(final HttpServletRequest request) {
      return request.getParameter(name);
    }

    protected String read(final Map<String, String> parameters) {
      return parameters.get(name);
    }

    public final C required(final boolean value) {
      required = value;
      return (C) this;
    }

    public final C defaultValue(final T value) {
      return defaultValue(() -> value);
    }

    public final C defaultValue(final Supplier<T> valueSupplier) {
      defaultValue = valueSupplier;
      return (C) this;
    }
  }

  public static final class StringParameter extends HttpParameter<String, StringParameter> {
    public static StringParameter named(final String name) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      return new StringParameter(name);
    }

    private boolean trimmed;

    StringParameter(final String name) {
      super(name, false, () -> null);
      this.trimmed = true;
    }

    public StringParameter trimmed(final boolean value) {
      this.trimmed = value;
      return this;
    }

    @Override protected String interpret(final String value) {
      if (trimmed) {
        return value == null ? null : value.trim();
      } else {
        return value;
      }
    }
  }

  public static final class UrlParameter extends HttpParameter<URL, UrlParameter> {
    public static UrlParameter named(final String name) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      return new UrlParameter(name);
    }

    UrlParameter(final String name) {
      super(name, false, () -> null);
    }

    @Override protected URL interpret(final String value) {
      if (value == null) {
        return null;
      }
      try {
        return new URL(value);
      } catch (final MalformedURLException ex) {
        throw new IllegalArgumentException("malformed URL", ex);
      }
    }
  }

  public static final class CursorParameter extends HttpParameter<Cursor, CursorParameter> {
    public static CursorParameter named(final String name) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      return new CursorParameter(name);
    }

    CursorParameter(final String name) {
      super(name, false, () -> null);
    }

    @Override protected Cursor interpret(final String value) {
      if (value == null) {
        return null;
      }
      return Cursor.fromWebSafeString(value);
    }
  }

  public static final class IntegerParameter extends HttpParameter<Integer, IntegerParameter> {
    public static IntegerParameter named(final String name) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      return new IntegerParameter(name);
    }

    IntegerParameter(final String name) {
      super(name, false, () -> null);
    }

    @Override protected Integer interpret(final String value) {
      if (value == null) {
        return null;
      }
      return Integer.valueOf(value);
    }
  }

  public static final class LongParameter extends HttpParameter<Long, LongParameter> {
    public static LongParameter named(final String name) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      return new LongParameter(name);
    }

    LongParameter(final String name) {
      super(name, false, () -> null);
    }

    @Override protected Long interpret(final String value) {
      if (value == null) {
        return null;
      }
      return Long.valueOf(value);
    }
  }

  private static final JsonStringNode cursor = JsonNodeFactories.string("cursor");
  private static final JsonStringNode data = JsonNodeFactories.string("data");

  protected JsonNode buildPage(final ActiveEntity ae, final QueryResultList<Entity> page) {
    return JsonNodeFactories.object(
            JsonNodeFactories.field(cursor, JsonNodeFactories.string(page.getCursor().toWebSafeString())),
            JsonNodeFactories.field(data, ae.toJson(page))
    );
  }

  protected Key save(final Entity data) {
    return DatastoreServiceFactory.getDatastoreService().put(data);
  }

  protected Future<Key> asyncSave(final Entity data) {
    return DatastoreServiceFactory.getAsyncDatastoreService().put(data);
  }
}
