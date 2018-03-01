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

import javax.servlet.http.HttpServletRequest;

public final class Route implements java.io.Serializable {
  private final String uri;

  public Route(final String routeUri) { uri = routeUri; }

  @Override public String toString() { return "Route{" + uri + '}'; }

  @Override public int hashCode() { return uri.hashCode(); }

  @Override public boolean equals(final Object that) {
    if (this == that) {
      return true;
    }
    if (that instanceof Route) {
      final Route other = (Route) that;
      return uri.equals(other.uri);
    }
    return false;
  }

  public final boolean matches(final HttpServletRequest request) {
    if (request.getPathInfo() == null) {
      return false;
    }
    if (request.getPathInfo().isEmpty()) {
      return false;
    }
    return uri.equals(request.getPathInfo());
  }
}
