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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class ParameterizedRoute implements java.io.Serializable {

    private static final long serialVersionUID = -5348318310957123564L;

    private final String uriPattern;
    private final Pattern regex;

    public ParameterizedRoute(final String uriPattern, final Pattern regex)
    {
        this.uriPattern = uriPattern;
        this.regex = regex;
    }

    @Override
    public String toString()
    {
        return "ParameterizedRoute{" + uriPattern + '}';
    }

    @Override
    public int hashCode()
    {
        return regex.hashCode();
    }

    @Override
    public boolean equals(final Object that)
    {
        if (this == that) {
            return true;
        }
        if (that instanceof ParameterizedRoute) {
            final ParameterizedRoute other = (ParameterizedRoute) that;
            return regex.equals(other.regex);
        }
        return false;
    }

    public boolean matches(final HttpServletRequest request, final String[] routeParameters)
    {
        if (request.getPathInfo() == null) {
            return false;
        }
        if (request.getPathInfo().isEmpty()) {
            return false;
        }
        final Matcher matcher = regex.matcher(request.getPathInfo());
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                routeParameters[i] = matcher.group(getPathParameterRegexGroupName(i));
            }
            return true;
        } else {
            return false;
        }
    }

    private String getPathParameterRegexGroupName(final int pathParameterIndex)
    {
        return "p" + pathParameterIndex;
    }
}
