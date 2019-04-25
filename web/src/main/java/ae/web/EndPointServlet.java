package ae.web;

import ae.db.Validation;
import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.saj.InvalidSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.Date;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class EndPointServlet extends RouterServlet
{

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

  public synchronized static final void use(final JsonFormatter jsonFormatter)
  {
    if (jsonFormatter == null) {
      throw new NullPointerException("jsonFormatter");
    }
    JSON_FORMATTER = PRETTY_JSON;
  }

  protected EndPointServlet()
  {
    // nothing to do
  }

  /* request manipulation ************************************************** */

  /**
   * Reads the text at the request body.
   *
   * @return the raw text at the {@link HttpServletRequest} associated to the controller.
   * @throws IOException if some problem occurs while reading the text.
   */
  protected String readBody(final HttpServletRequest request)
      throws IOException
  {
    final BufferedReader reader = request.getReader();
    final StringBuilder requestBody = new StringBuilder();
    {
      String line;
      while ((line = reader.readLine()) != null) {
        requestBody.append(line);
      }
    }
    return requestBody.toString();
  }

  protected JsonNode readJson(final HttpServletRequest request)
      throws IOException, ServletException
  {
    try {
      return readJson(request.getReader());
    } catch (final InvalidSyntaxException e) {
      throw new ServletException(e);
    }
  }

  protected JsonNode readJson(final Reader reader)
      throws IOException, InvalidSyntaxException
  {
    return JDOM_PARSER.parse(reader);
  }

  protected JsonNode readJson(final String content)
      throws IOException, InvalidSyntaxException
  {
    return JDOM_PARSER.parse(content);
  }

  /* response manipulation ************************************************* */
  protected static String to(final String location)
  {
    return location;
  }

  protected static int withStatus(final int httpStatusCode)
  {
    return httpStatusCode;
  }

  protected static final String location(final String value)
  {
    return value;
  }

  protected void forward(final String location, final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException
  {
    logger().debug("Forwarding (to '{}')", location);
    try {
      request.getRequestDispatcher(location).forward(request, response);
    } catch (final IOException e) {
      logger().warn("Forward failure", e);
    }
  }

  /**
   * Trigger a browser redirectTo
   *
   * @param location Where to redirectTo
   */
  protected void redirect(final String location, final HttpServletResponse response)
  {
    logger().debug("Redirecting ('Found', '{}' to '{}'.", HttpServletResponse.SC_FOUND, location);
    try {
      response.sendRedirect(location);
    } catch (final IOException e) {
      logger().warn("Redirect failure", e);
    }
  }

  /**
   * Trigger a browser redirectTo named specific http 3XX status code.
   *
   * @param location       Where to redirectTo permanently
   * @param httpStatusCode the http status code
   */
  protected void redirect(final HttpServletResponse response, final String location, final StatusCode httpStatusCode)
  {
    redirect(response, to(location), withStatus(httpStatusCode.value));
  }

  /**
   * Trigger a browser redirectTo named specific http 3XX status code.
   *
   * @param location       Where to redirectTo permanently
   * @param httpStatusCode the http status code
   */
  protected void redirect(final HttpServletResponse response, final String location, final int httpStatusCode)
  {
    logger().debug("Redirecting ('{}' to '{}').", httpStatusCode, location);
    response.setStatus(httpStatusCode);
    response.setHeader("Location", location);
    response.setHeader("Connection", "close");
    try {
      response.sendError(httpStatusCode);
    } catch (final IOException e) {
      logger().warn("Exception when trying to redirect permanently", e);
    }
  }

  protected String format(final JsonNode json)
  {
    return JSON_FORMATTER.format(json);
  }

  /* contents updaters */
  protected void writeHtml(final HttpServletResponse response, final CharSequence content)
      throws ServletException, IOException
  {
    set(response, ContentType.TEXT_HTML);
    send(response, content);
  }

  protected void writeXhtml(final HttpServletResponse response, final CharSequence content)
      throws ServletException, IOException
  {
    set(response, ContentType.TEXT_XHTML);
    send(response, content);
  }

  protected void renderJson(final HttpServletResponse response, final JsonNode json)
      throws ServletException, IOException
  {
    writeJson(response, JSON_FORMATTER.format(json));
  }

  protected void writeJson(final HttpServletResponse response, final CharSequence content)
      throws ServletException, IOException
  {
    set(response, ContentType.APPLICATION_JSON);
    send(response, content);
  }

  protected void writeText(final HttpServletResponse response, final CharSequence content)
      throws ServletException, IOException
  {
    set(response, ContentType.TEXT_PLAIN);
    send(response, content);
  }

  /**
   * Writes the string content directly to the response.
   * <p>
   * This method commits the response.
   *
   * @param content the content to write into the response.
   * @throws javax.servlet.ServletException if the response is already committed.
   * @throws java.io.IOException
   */
  void send(final HttpServletResponse response, final CharSequence content)
      throws ServletException, IOException
  {
    if (response.isCommitted()) {
      throw new ServletException("The response has already been committed");
    }
    if (content == null) {
      commit(response, null);
    } else {
      commit(response, content.toString());
    }
  }

  void commit(final HttpServletResponse response, final String content)
      throws IOException
  {
    if (response.getContentType() == null) {
      set(response, ContentType.TEXT_HTML);
    }
    if (content != null) {
      response.setContentLength(content.getBytes().length);
      response.getWriter().append(content);
    }
    set(response, StatusCode.OK);
  }

  protected void unprocessableEntity(final HttpServletResponse response)
      throws IOException
  {
    response.sendError(422);
  }

  protected void unprocessableEntity(final HttpServletResponse response, final Validation error)
      throws IOException
  {
    final String json = JSON_FORMATTER.format(error.asJson());
    set(response, ContentType.APPLICATION_JSON);
    response.sendError(422, json);
  }

  protected void notFound(final HttpServletResponse response)
      throws IOException
  {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  protected static final class StatusCode implements Serializable
  {
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

    private StatusCode(final int value)
    {
      this.value = value;
    }

    public static final StatusCode of(final int value)
    {
      if (value < 0) {
        throw new IllegalArgumentException("value < 0");
      }
      return new StatusCode(value);
    }

    @Override
    public int hashCode()
    {
      return Integer.hashCode(value);
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj instanceof StatusCode) {
        final StatusCode other = (StatusCode) obj;
        return value == other.value;
      }
      return false;
    }

    @Override
    public String toString()
    {
      return "StatusCode{" + value + '}';
    }
  }

  protected void set(final HttpServletResponse response, final StatusCode statusCode)
  {
    response.setStatus(statusCode.value);
  }

  protected static final class ContentType implements Serializable
  {

    private static final long serialVersionUID = 1854942737463033587L;

    public static final ContentType APPLICATION_FORM_URLENCODED = new ContentType(
        "application/x-www-form-urlencoded");
    public static final ContentType APPLICATION_JSON = new ContentType("application/json");
    public static final ContentType APPLICATION_XML = new ContentType("application/xml");
    public static final ContentType APPLICATION_X_YAML = new ContentType("application/x-yaml");
    public static final ContentType TEXT_HTML = new ContentType("text/html");
    public static final ContentType TEXT_XHTML = new ContentType("text/xhtml");
    public static final ContentType TEXT_PLAIN = new ContentType("text/plain");
    public static final ContentType APPLICATION_OCTET_STREAM = new ContentType("application/octet-stream");
    public static final ContentType MULTIPART_FORM_DATA = new ContentType("multipart/form-data");

    private final String value;

    private ContentType(final String value)
    {
      this.value = value;
    }

    public static final ContentType of(final String value)
    {
      return new ContentType(value);
    }

    @Override
    public int hashCode()
    {
      return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj instanceof ContentType) {
        final ContentType other = (ContentType) obj;
        return value.equals(other.value);
      }
      return false;
    }

    @Override
    public String toString()
    {
      return "ContentType{" + value + '}';
    }
  }

  protected void set(final HttpServletResponse response, final ContentType contentType)
  {
    response.setContentType(contentType.value);
  }

  protected final static class Header implements Serializable
  {

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

    private Header(final String value)
    {
      this.name = value;
    }

    public static final Header of(final String value)
    {
      return new Header(value);
    }

    @Override
    public int hashCode()
    {
      return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (obj instanceof Header) {
        final Header other = (Header) obj;
        return name.equals(other.name);
      }
      return false;
    }

    @Override
    public String toString()
    {
      return "Header{" + name + '}';
    }
  }

  protected void set(final HttpServletResponse response, final Header header, final String value)
  {
    response.setHeader(header.name, value);
  }

  protected void set(final HttpServletResponse response, final Header header, final int value)
  {
    response.setIntHeader(header.name, value);
  }

  protected void set(final HttpServletResponse response, final Header header, final Date value)
  {
    response.setDateHeader(header.name, value.getTime());
  }

  protected void set(final HttpServletResponse response, final Header header, final long timestamp)
  {
    response.setDateHeader(header.name, timestamp);
  }

  protected void add(final HttpServletResponse response, final Header header, final String value)
  {
    response.addHeader(header.name, value);
  }

  protected void add(final HttpServletResponse response, final Header header, final int value)
  {
    response.addIntHeader(header.name, value);
  }

  protected void add(final HttpServletResponse response, final Header header, final Date value)
  {
    response.addDateHeader(header.name, value.getTime());
  }

  protected void add(final HttpServletResponse response, final Header header, final long timestamp)
  {
    response.addDateHeader(header.name, timestamp);
  }

  protected final <T> T get(final HttpServletRequest request, final QueryParameter<T> parameter)
  {
    return parameter.of(request);
  }

  protected final boolean has(final HttpServletRequest request, final QueryParameter<?> parameter)
  {
    return parameter.isDefinedAt(request);
  }

  protected final <T> T get(final HttpServletRequest request, final RequestAttribute<T> parameter)
  {
    return parameter.of(request);
  }

  protected final boolean has(final HttpServletRequest request, final RequestAttribute<?> parameter)
  {
    return parameter.isDefinedAt(request);
  }

  protected static final ValueInterpreter<String> trimmed = Interpret::asTrimmedString;

  protected static <T> Supplier<T> byDefault(final T defaultValue)
  {
    return () -> defaultValue;
  }

  protected static final boolean required = true;

  protected static final boolean notRequired = true;
}
