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
package ae.web.processor;

import ae.endpoint;
import ae.multitenant;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static javax.lang.model.util.ElementFilter.methodsIn;

class RoutesReader
{
  private static final String[] NO_ROLES = {};

  private static final String API = "/api";
  private static final String ADMIN = "/admin";

  private final Messager messager;

  private final EndPointSpec.Builder routes;

  private boolean success;

  RoutesReader(final Messager messager, final EndPointSpec.Builder routes)
  {
    this.messager = messager;
    this.routes = routes;
  }

  /**
   * @param endpoint
   * @return null if no processing done (reports errors on such case), the route add
   */
  boolean buildRoutesFor(final TypeElement endpoint)
  {
    this.success = true;
    final String baseUri = makeEndpointPath(getEndpointPathKind(endpoint), endpoint);
    routes.path(baseUri + "/*");
    for (final ExecutableElement method : methodsIn(endpoint.getEnclosedElements())) {
      for (final HttpVerb httpVerb : HttpVerb.values()) {
        buildRoute(endpoint, baseUri, httpVerb, method);
      }
    }
    return this.success;
  }

  private String getEndpointPathKind(TypeElement endpoint)
  {
    if (isAdmin(endpoint)) {
      return ADMIN;
    } else {
      return API;
    }
  }

  void buildRoute(final TypeElement endpoint, final String baseUri, final HttpVerb httpVerb, final ExecutableElement method)
  {
    final String path = httpVerb.getPath(method);
    if (path == null) {
      return;
    }
    makeRoute(httpVerb,
              getUri(baseUri, method, path),
              handlerPath(method, path),
              isOAuth2(method, endpoint),
              getRoles(method, endpoint),
              getNamespace(method, endpoint),
              method);
  }

  private String getUri(final String baseUri, final ExecutableElement method, final String path)
  {
    return makeUri(baseUri, handlerPath(method, path));
  }

  boolean isAdmin(final TypeElement endpoint)
  {
    return endpoint.getAnnotation(ae.endpoint.class).admin();
  }

  boolean isOAuth2(final ExecutableElement method, final TypeElement endpoint)
  {
    ae.oauth2 oauth2 = method.getAnnotation(ae.oauth2.class);
    if (oauth2 == null) {
      oauth2 = endpoint.getAnnotation(ae.oauth2.class);
    }
    if (oauth2 == null) {
      return false;
    } else {
      return oauth2.value();
    }
  }

  String[] getRoles(final ExecutableElement method, final TypeElement endpoint)
  {
    ae.roles roles = method.getAnnotation(ae.roles.class);
    if (roles == null) {
      roles = endpoint.getAnnotation(ae.roles.class);
    }
    if (roles == null) {
      return NO_ROLES;
    } else {
      return roles.value();
    }
  }

  String getNamespace(final ExecutableElement method, final TypeElement endpoint)
  {
    multitenant namespace = method.getAnnotation(multitenant.class);
    if (namespace == null) {
      namespace = endpoint.getAnnotation(multitenant.class);
    }
    if (namespace == null) {
      return "";
    } else {
      return namespace.value();
    }
  }

  private void makeRoute(final HttpVerb verb,
                         final String endPointUri,
                         final String handlerUri,
                         final boolean useCredentials,
                         final String[] roles,
                         final String namespace,
                         final ExecutableElement method)
  {
    final PathSpec path = PathSpec.from(endPointUri, handlerUri);
    this.routes.addRoute(path.makeRoute(verb, method, useCredentials, roles, namespace));
  }

  private void error(final String message, final Element e)
  {
    this.messager.printMessage(Diagnostic.Kind.ERROR, message, e);
    this.success = false;
  }

  private String makeUri(final String parentPath, final String childPath)
  {
    if (childPath.startsWith("/")) {
      if (parentPath.endsWith("/")) {
        return parentPath + childPath.substring(1);
      } else {
        return parentPath + childPath;
      }
    } else {
      if (parentPath.endsWith("/")) {
        return parentPath + childPath;
      } else {
        if ("".equals(childPath)) {
          return parentPath;
        } else {
          return parentPath + '/' + childPath;
        }
      }
    }
  }

  private String handlerPath(final ExecutableElement method, final String path)
  {
    if ("<UNDEFINED>".equals(path)) {
      final String actionName = method.getSimpleName().toString();
      switch (actionName) {
        case "index":
        case "save":
        case "update":
        case "delete":
          return "";
        default:
          return '/' + actionName;
      }
    } else {
      return path.startsWith("/") ? path : '/' + path;
    }
  }

  private String makeEndpointPath(final String parentPath, final TypeElement endpoint)
  {
    final String path = getPath(endpoint);
    if (path == null) {
      error("@endpoint.path can't be null", endpoint);
    } else {
      if ("<UNDEFINED>".equals(path)) {
        final Name endpointClassName = endpoint.getSimpleName();
        final String classname = endpointClassName.toString();
        return makeUri(parentPath, classname.toLowerCase());
      } else {
        return makeUri(parentPath, path);
      }
    }
    return "";
  }

  private String getPath(final TypeElement endpoint)
  {
    final endpoint metadata = endpoint.getAnnotation(endpoint.class);
    return metadata.value();
  }
}