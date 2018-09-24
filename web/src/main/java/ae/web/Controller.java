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
import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

public abstract class Controller {

        protected final HttpServletRequest request;
        protected final HttpServletResponse response;

        protected Controller(final HttpServletRequest request, final HttpServletResponse response)
        {
                this.request = request;
                this.response = response;
        }

        /**
         * Configures the controller before it manages a request and generate a response.
         *
         * Any initialization, validation, dependency injection, must go here. By default it doesn't do anything.
         */
        protected void setup()
        {
                // nothing to do
        }

        /**
         * Frees resources taken by the controller.
         *
         * By default it doesn't do anything.
         */
        protected void teardown()
        {
                // nothing to do
        }

        /**
         * @return the logger to be used at the controller.
         */
        protected abstract Logger logger();

        /* request manipulation ************************************************** */
        /**
         * Reads the text at the request body.
         *
         * @return the raw text at the {@link HttpServletRequest} associated to the controller.
         * @throws IOException if some problem occurs while reading the text.
         */
        protected String readBody() throws IOException
        {
                final BufferedReader reader = request.getReader();
                final StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                }
                return requestBody.toString();
        }

        protected JsonNode readJson() throws IOException, ServletException
        {
                try {
                        return readJson(request.getReader());
                } catch (final InvalidSyntaxException e) {
                        throw new ServletException(e);
                }
        }

        protected JsonNode readJson(final Reader reader) throws IOException, InvalidSyntaxException
        {
                return JDOM_PARSER.parse(reader);
        }

        protected JsonNode readJson(final String content) throws IOException, InvalidSyntaxException
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

        protected void forward(final String location) throws ServletException
        {
                if (logger().isTraceEnabled()) {
                        logger().trace("Forwarding (to " + location + ")");
                }
                try {
                        request.getRequestDispatcher(location).forward(request, response);
                } catch (final IOException e) {
                        if (logger().isWarnEnabled()) {
                                logger().warn("Forward failure", e);
                        }
                }
        }

        /**
         * Trigger a browser redirectTo
         *
         * @param location Where to redirectTo
         */
        protected void redirect(final String location)
        {
                if (logger().isTraceEnabled()) {
                        logger().trace(
                                "Redirecting ('Found', " + HttpServletResponse.SC_FOUND + " to " + location + ')');
                }
                try {
                        response.sendRedirect(location);
                } catch (final IOException e) {
                        if (logger().isWarnEnabled()) {
                                logger().warn("Redirect failure", e);
                        }
                }
        }

        /**
         * Trigger a browser redirectTo named specific http 3XX status code.
         *
         * @param location Where to redirectTo permanently
         * @param httpStatusCode the http status code
         */
        protected void redirect(final String location, final StatusCode httpStatusCode)
        {
                redirect(location, httpStatusCode.value);
        }

        /**
         * Trigger a browser redirectTo named specific http 3XX status code.
         *
         * @param location Where to redirectTo permanently
         * @param httpStatusCode the http status code
         */
        protected void redirect(final String location, final int httpStatusCode)
        {
                if (logger().isTraceEnabled()) {
                        logger().trace("Redirecting (" + httpStatusCode + " to " + location + ')');
                }
                response.setStatus(httpStatusCode);
                response.setHeader("Location", location);
                response.setHeader("Connection", "close");
                try {
                        response.sendError(httpStatusCode);
                } catch (final IOException e) {
                        if (logger().isWarnEnabled()) {
                                logger().warn("Exception when trying to redirect permanently", e);
                        }
                }
        }

        protected String format(final JsonNode json)
        {
                return JSON_FORMATTER.format(json);
        }

        /* contents updaters */
        protected void writeHtml(final CharSequence content) throws ServletException, IOException
        {
                set(ContentType.TEXT_HTML);
                send(content);
        }

        protected void writeXhtml(final CharSequence content) throws ServletException, IOException
        {
                set(ContentType.TEXT_XHTML);
                send(content);
        }

        protected void renderJson(final JsonNode json) throws ServletException, IOException
        {
                writeJson(JSON_FORMATTER.format(json));
        }

        protected void writeJson(final CharSequence content) throws ServletException, IOException
        {
                set(ContentType.APPLICATION_JSON);
                send(content);
        }

        protected void writeText(final CharSequence content) throws ServletException, IOException
        {
                set(ContentType.TEXT_PLAIN);
                send(content);
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
        void send(final CharSequence content) throws ServletException, IOException
        {
                if (response.isCommitted()) {
                        throw new ServletException("The response has already been committed");
                }
                if (content == null) {
                        commit(null);
                } else {
                        commit(content.toString());
                }
        }

        void commit(final String content) throws IOException
        {
                if (response.getContentType() == null) {
                        set(ContentType.TEXT_HTML);
                }
                if (content != null) {
                        response.setContentLength(content.getBytes().length);
                        response.getWriter().append(content);
                }
                set(StatusCode.OK);
        }

        protected void unprocessableEntity()
        {
                response.setStatus(422);
        }

        protected void notFound()
        {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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

        public static class ParameterNotDefinedException extends RuntimeException {

                private static final long serialVersionUID = 7433715562801447600L;

                public ParameterNotDefinedException(final String parameterName)
                {
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

        public synchronized static final void usePrettyJsonFormat()
        {
                JSON_FORMATTER = PRETTY_JSON;
        }

        public synchronized static final void useCompactJsonFormat()
        {
                JSON_FORMATTER = COMPACT_JSON;
        }

        protected static final class StatusCode implements Serializable {

                private static final long serialVersionUID = -6286655551639670356L;

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

        protected void set(final StatusCode statusCode)
        {
                response.setStatus(statusCode.value);
        }

        protected static final class ContentType implements Serializable {

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

        protected void set(final ContentType contentType)
        {
                response.setContentType(contentType.value);
        }

        protected final static class Header implements Serializable {

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

        protected void set(final Header header, final String value)
        {
                response.setHeader(header.name, value);
        }

        protected void set(final Header header, final int value)
        {
                response.setIntHeader(header.name, value);
        }

        protected void set(final Header header, final Date value)
        {
                response.setDateHeader(header.name, value.getTime());
        }

        protected void set(final Header header, final long timestamp)
        {
                response.setDateHeader(header.name, timestamp);
        }

        protected void add(final Header header, final String value)
        {
                response.addHeader(header.name, value);
        }

        protected void add(final Header header, final int value)
        {
                response.addIntHeader(header.name, value);
        }

        protected void add(final Header header, final Date value)
        {
                response.addDateHeader(header.name, value.getTime());
        }

        protected void add(final Header header, final long timestamp)
        {
                response.addDateHeader(header.name, timestamp);
        }

        protected final <T> T get(final HttpParameter<T> parameter)
        {
                return parameter.of(request);
        }

        protected final boolean has(final HttpParameter<?> parameter)
        {
                return parameter.isDefinedAt(request);
        }

        public static abstract class HttpParameter<T> {

                @FunctionalInterface
                public interface ValueInterpreter<T> {

                        T from(String rawValue);
                }

                private final String name;
                private final boolean required;
                private final Supplier<T> defaultValue;
                private final ValueInterpreter<T> interpretValue;

                protected HttpParameter(final String name, final boolean required,
                                        final ValueInterpreter<T> interpretValue,
                                        final Supplier<T> defaultValue)
                {
                        this.name = name;
                        this.required = required;
                        this.defaultValue = defaultValue;
                        this.interpretValue = interpretValue;
                }

                public final boolean isDefinedAt(final HttpServletRequest request)
                {
                        return request.getParameterMap().containsKey(name);
                }

                public final boolean isDefinedAt(final Map<String, String> parameters)
                {
                        return parameters.containsKey(name);
                }

                public final T of(final HttpServletRequest request)
                {
                        if (isDefinedAt(request)) {
                                return interpret(read(request));
                        } else {
                                if (required) {
                                        throw new ParameterNotDefinedException(name);
                                }
                                return defaultValue.get();
                        }
                }

                public final T of(final Map<String, String> parameters)
                {
                        if (isDefinedAt(parameters)) {
                                return interpret(read(parameters));
                        } else {
                                if (required) {
                                        throw new ParameterNotDefinedException(name);
                                }
                                return defaultValue.get();
                        }
                }

                protected final T interpret(String raw)
                {
                        return interpretValue.from(raw);
                }

                protected final String read(final HttpServletRequest request)
                {
                        return request.getParameter(name);
                }

                protected final String read(final Map<String, String> parameters)
                {
                        return parameters.get(name);
                }
        }

        protected static final HttpParameter.ValueInterpreter<String> trimmed = Interpret::asTrimmedString;

        protected static final String name(final String value)
        {
                if (value.isEmpty()) {
                        throw new IllegalArgumentException("name must be a defined non empty string");
                }
                return value;
        }

        protected static <T> Supplier<T> byDefault(final T defaultValue)
        {
                return () -> defaultValue;
        }

        protected static final boolean required = true;

        protected static final boolean notRequired = true;

        public static final class StringParameter extends HttpParameter<String> {

                static final ValueInterpreter<String> DEFAULT_INTERPRETER = Interpret::asString;
                static final Supplier<String> NULL_DEFAULT_VALUE = () -> null;

                public StringParameter(final String name)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public StringParameter(final String name, boolean isRequired)
                {
                        this(name, isRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public StringParameter(final String name, boolean isRequired,
                                       final ValueInterpreter<String> interpretValue)
                {
                        this(name, isRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public StringParameter(final String name, final ValueInterpreter<String> interpretValue)
                {
                        this(name, notRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public StringParameter(final String name, final ValueInterpreter<String> interpretValue,
                                       final Supplier<String> defaultValue)
                {
                        this(name, notRequired, interpretValue, defaultValue);
                }

                public StringParameter(final String name, final Supplier<String> defaultValue)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public StringParameter(final String name, final boolean isRequired, final Supplier<String> defaultValue)
                {
                        super(name, isRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public StringParameter(final String name, final boolean required,
                                       final ValueInterpreter<String> interpretValue,
                                       final Supplier<String> defaultValue)
                {
                        super(name, required, interpretValue, defaultValue);
                }
        }

        public static final class UrlParameter extends HttpParameter<URL> {

                static final ValueInterpreter<URL> DEFAULT_INTERPRETER = Interpret::asUrl;
                static final Supplier<URL> NULL_DEFAULT_VALUE = () -> null;

                public UrlParameter(final String name)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public UrlParameter(final String name, boolean isRequired)
                {
                        this(name, isRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public UrlParameter(final String name, boolean isRequired, final ValueInterpreter<URL> interpretValue)
                {
                        this(name, isRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public UrlParameter(final String name, final ValueInterpreter<URL> interpretValue)
                {
                        this(name, notRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public UrlParameter(final String name, final ValueInterpreter<URL> interpretValue,
                                    final Supplier<URL> defaultValue)
                {
                        this(name, notRequired, interpretValue, defaultValue);
                }

                public UrlParameter(final String name, final Supplier<URL> defaultValue)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public UrlParameter(final String name, final boolean isRequired, final Supplier<URL> defaultValue)
                {
                        super(name, isRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public UrlParameter(final String name, final boolean required,
                                    final ValueInterpreter<URL> interpretValue,
                                    final Supplier<URL> defaultValue)
                {
                        super(name, required, interpretValue, defaultValue);
                }
        }

        public static final class CursorParameter extends HttpParameter<Cursor> {

                static final ValueInterpreter<Cursor> DEFAULT_INTERPRETER = Interpret::asCursor;
                static final Supplier<Cursor> NULL_DEFAULT_VALUE = () -> null;

                public CursorParameter(final String name)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public CursorParameter(final String name, boolean isRequired)
                {
                        this(name, isRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public CursorParameter(final String name, boolean isRequired,
                                       final ValueInterpreter<Cursor> interpretValue)
                {
                        this(name, isRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public CursorParameter(final String name, final ValueInterpreter<Cursor> interpretValue)
                {
                        this(name, notRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public CursorParameter(final String name, final ValueInterpreter<Cursor> interpretValue,
                                       final Supplier<Cursor> defaultValue)
                {
                        this(name, notRequired, interpretValue, defaultValue);
                }

                public CursorParameter(final String name, final Supplier<Cursor> defaultValue)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public CursorParameter(final String name, final boolean isRequired, final Supplier<Cursor> defaultValue)
                {
                        super(name, isRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public CursorParameter(final String name, final boolean required,
                                       final ValueInterpreter<Cursor> interpretValue,
                                       final Supplier<Cursor> defaultValue)
                {
                        super(name, required, interpretValue, defaultValue);
                }
        }

        public static final class IntegerParameter extends HttpParameter<Integer> {

                static final ValueInterpreter<Integer> DEFAULT_INTERPRETER = Interpret::asInteger;
                static final Supplier<Integer> NULL_DEFAULT_VALUE = () -> null;

                public IntegerParameter(final String name)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public IntegerParameter(final String name, boolean isRequired)
                {
                        this(name, isRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public IntegerParameter(final String name, boolean isRequired,
                                        final ValueInterpreter<Integer> interpretValue)
                {
                        this(name, isRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public IntegerParameter(final String name, final ValueInterpreter<Integer> interpretValue)
                {
                        this(name, notRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public IntegerParameter(final String name, final ValueInterpreter<Integer> interpretValue,
                                        final Supplier<Integer> defaultValue)
                {
                        this(name, notRequired, interpretValue, defaultValue);
                }

                public IntegerParameter(final String name, final Supplier<Integer> defaultValue)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public IntegerParameter(final String name, final boolean isRequired,
                                        final Supplier<Integer> defaultValue)
                {
                        super(name, isRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public IntegerParameter(final String name, final boolean required,
                                        final ValueInterpreter<Integer> interpretValue,
                                        final Supplier<Integer> defaultValue)
                {
                        super(name, required, interpretValue, defaultValue);
                }
        }

        public static final class LongParameter extends HttpParameter<Long> {

                static final ValueInterpreter<Long> DEFAULT_INTERPRETER = Interpret::asLong;
                static final Supplier<Long> NULL_DEFAULT_VALUE = () -> null;

                public LongParameter(final String name)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public LongParameter(final String name, boolean isRequired)
                {
                        this(name, isRequired, DEFAULT_INTERPRETER, NULL_DEFAULT_VALUE);
                }

                public LongParameter(final String name, boolean isRequired, final ValueInterpreter<Long> interpretValue)
                {
                        this(name, isRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public LongParameter(final String name, final ValueInterpreter<Long> interpretValue)
                {
                        this(name, notRequired, interpretValue, NULL_DEFAULT_VALUE);
                }

                public LongParameter(final String name, final ValueInterpreter<Long> interpretValue,
                                     final Supplier<Long> defaultValue)
                {
                        this(name, notRequired, interpretValue, defaultValue);
                }

                public LongParameter(final String name, final Supplier<Long> defaultValue)
                {
                        this(name, notRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public LongParameter(final String name, final boolean isRequired, final Supplier<Long> defaultValue)
                {
                        super(name, isRequired, DEFAULT_INTERPRETER, defaultValue);
                }

                public LongParameter(final String name, final boolean required,
                                     final ValueInterpreter<Long> interpretValue,
                                     final Supplier<Long> defaultValue)
                {
                        super(name, required, interpretValue, defaultValue);
                }
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

        protected interface option {

                IntegerParameter chunk = new IntegerParameter("chunk", notRequired);
                CursorParameter end = new CursorParameter("end", notRequired);
                IntegerParameter limit = new IntegerParameter("limit", notRequired);
                IntegerParameter offset = new IntegerParameter("offset", notRequired);
                IntegerParameter prefetch = new IntegerParameter("prefetch", notRequired);
                CursorParameter start = new CursorParameter("start", notRequired);
        }

        protected final FetchOptions requestFetchOptions()
        {
                final FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

                if (has(option.chunk)) {
                        fetchOptions.chunkSize(get(option.chunk));
                }
                if (has(option.end)) {
                        fetchOptions.endCursor(get(option.end));
                }
                if (has(option.limit)) {
                        fetchOptions.limit(get(option.limit));
                }
                if (has(option.offset)) {
                        fetchOptions.offset(get(option.offset));
                }
                if (has(option.prefetch)) {
                        fetchOptions.prefetchSize(get(option.prefetch));
                }
                if (has(option.start)) {
                        fetchOptions.startCursor(get(option.start));
                }

                return fetchOptions;
        }

        private static final StringParameter sort = new StringParameter("sort", notRequired);

        protected final Iterable<Query.SortPredicate> requestSorts()
        {
                if (has(sort)) {
                        final String sortPredicatesSeparatedByCommas = get(sort);

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
