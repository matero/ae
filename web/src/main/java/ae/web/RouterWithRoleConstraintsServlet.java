/*
 * The MIT License
 *
 * Copyright 2018 juanjo.
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

import java.util.Collection;

public abstract class RouterWithRoleConstraintsServlet extends RouterServlet {
        protected RouterWithRoleConstraintsServlet()
        {
                // nothing to do
        }

        protected boolean loggedUserHas(final String role)
        {
                return loggedUserRoles().contains(role);
        }

        protected boolean loggedUserHasOneOf(final String r1, final String r2)
        {
                final Collection<String> roles = loggedUserRoles();
                return roles.contains(r1) || roles.contains(r2);
        }

        protected boolean loggedUserHasOneOf(final String r1, final String r2, final String r3)
        {
                final Collection<String> roles = loggedUserRoles();
                return roles.contains(r1) || roles.contains(r2) || roles.contains(r3);
        }

        protected boolean loggedUserHasOneOf(final String r1, final String r2, final String r3, final String r4)
        {
                final Collection<String> roles = loggedUserRoles();
                return roles.contains(r1) || roles.contains(r2) || roles.contains(r3) || roles.contains(r4);
        }

        protected boolean loggedUserHasOneOf(final String r1,
                                             final String r2,
                                             final String r3,
                                             final String r4,
                                             final String r5)
        {
                final Collection<String> roles = loggedUserRoles();
                return roles.contains(r1)
                        || roles.contains(r2)
                        || roles.contains(r3)
                        || roles.contains(r4)
                        || roles.contains(r5);
        }

        protected abstract Collection<String> loggedUserRoles();
}
