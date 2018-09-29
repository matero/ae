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
package ae;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.Target;

/**
 * Denotes an action method mapping to a GET request.
 * 
 * If it starts with {@code '/'} then the path is taken literally, avoiding to precalculate any part of the path.
 * 
 * Other way, the path solving goes like this:
 * <ol>
 * <li>determine the base path:</li>
 * <ul>
 * <li>{@code uses template ==> <router.application_path>/<controller_path>}</li>
 * <li>{@code it doesn't use template ==> <router.administration_path>/<controller_path>}</li>
 * </ul>
 * <li>determine the action name:</li>
 * <ul>
 * <li>{@code value is defined ==> value is used (and the web-parameters are solved)}, prepended with '/'</li>
 * <li>{@code value is not defined ==> if the action method is named "index"/"indexHtml"/"get" then "" is used, otherwise the method name is used.}</li>
 * </ul>
 * </ol>
 * 
 * <em>note</em>: an action is considered to use template when it is annotated with {@code @ae.template(true)} or it is not
 * annotated with {@code @ae.template(false)} and the controller is annotated with {@code @ae.template(true)}.
 */
@Retention(SOURCE)
@Target(METHOD)
public @interface GET {
        /**@return  the path spec of the action. */
        String value() default "<UNDEFINED>";
}
