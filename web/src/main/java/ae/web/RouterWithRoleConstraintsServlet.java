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

public abstract class RouterWithRoleConstraintsServlet extends RouterServlet {
        private static long serialVersionUID = 1954259488750500149L;

        protected RouterWithRoleConstraintsServlet()
        {
                // nothing to do
        }

        protected abstract boolean loggedUserHas(String role);

        protected abstract boolean loggedUserHasOneOf(String r1, String r2);

        protected abstract boolean loggedUserHasOneOf(String r1, String r2, String r3);

        protected abstract boolean loggedUserHasOneOf(String r1, String r2, String r3, String r4);

        protected abstract boolean loggedUserHasOneOf(String r1, String r2, String r3, String r4, String r5);
}
