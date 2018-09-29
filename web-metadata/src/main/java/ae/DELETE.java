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
 * Denotes an action method mapping to a DELETE request.
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
 * <li>{@code value is not defined ==> if the action method is named "delete" then "" is used, otherwise the method name is used.}</li>
 * </ul>
 * </ol>
 * 
 * samples of use:
 * <pre><code>
 * import javax.servlet.annotation.WebServlet;
 * import ae.*;
 *
 * @router(administration = "adm", application = "app", api = "api")
 * @WebServlet("/*")
 * public final class Router extends ApplicationRouter {
 * }
 * 
 * @controller
 * public final Sample1Controller extends  extends ae.web.ControllerWithThymeleafSupport {
 *        private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
 *
 *        public Controller(final HttpServletRequest request, final HttpServletResponse response)
 *        {
 *                super(request, response);
 *        }
 *
 *        public Controller(final HttpServletRequest request,
 *                          final HttpServletResponse response,
 *                          final WebContext templateContext,
 *                          final TemplateEngine templateEngine)
 *        {
 *                super(request, response, templateContext, templateEngine);
 *        }
 *
 *        @Override
 *        protected Logger logger()
 *        {
 *                return LOGGER;
 *        }
 * 
 *        @DELETE public void delete() {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/api/sample1"
 *          //   action name = ""
 *          //   action path = "/app/api/sample1" + "" = "/app/api/sample1"
 *        }
 * 
 *        @DELETE("{id}") public void delete(final long id) {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/api/sample1"
 *          //   action name = "/{id}"
 *          //   action path = "/app/api/sample1" + "/{id}" = "/app/api/sample1/{id}"
 *        }
 * 
 *        @DELETE public void del() {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/api/sample1"
 *          //   action name = "del"
 *          //   action path = "/app/api/sample1" + "/del" = "/app/api/sample1/del"
 *        }
 * 
 *        @DELETE("{id}") public void del(final long id) {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/api/sample1"
 *          //   action name = "/{id}"
 *          //   action path = "/app/api/sample1" + "/{id}" = "/app/api/sample1/{id}"
 *        }
 * 
 *        @DELETE("del/{id}") public void del(final long id) {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/api/sample1"
 *          //   action name = "/del/{id}"
 *          //   action path = "/app/api/sample1" + "/{id}" = "/app/api/sample1/{id}"
 *        }
 * 
 *        @DELETE("{name}") @admin public void delete(final String name) {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/adm/api/sample1"
 *          //   action name = "/del/{id}"
 *          //   action path = "/adm/api/sample1" + "/{id}" = "/adm/api/sample1/{name}"
 *        }
 * }
 * 
 * @controller
 * public final Sample2Controller extends  extends ae.web.ControllerWithThymeleafSupport {
 * 
 *        @DELETE @template public void delete() {
 *          // given:
 *          //   @template suppport -> it is considered a web-application action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/sample2"
 *          //   action name = ""
 *          //   action path = "/app/sample2" + "" = "/app/sample2"
 *        }
 * 
 *        @DELETE @template("{id}") public void delete(final long id) {
 *          // given:
 *          //   @template suppport -> it is considered a web-application action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/sample2"
 *          //   action name = "/{id}"
 *          //   action path = "/app/sample2" + "/{id}" = "/app/sample2/{id}"
 *        }
 * 
 *        @DELETE @template public void del() {
 *          // given:
 *          //   @template suppport -> it is considered a web-application action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/sample2"
 *          //   action name = "del"
 *          //   action path = "/app/sample2" + "/del" = "/app/sample2/del"
 *        }
 * 
 *        @DELETE @template("{id}") public void del(final long id) {
 *          // given:
 *          //   @template suppport -> it is considered a web-application action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/sample2"
 *          //   action name = "/{id}"
 *          //   action path = "/app/sample2" + "/{id}" = "/app/sample2/{id}"
 *        }
 * 
 *        @DELETE @template("del/{id}") public void del(final long id) {
 *          // given:
 *          //   @template suppport -> it is considered a web-application action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/app/sample2"
 *          //   action name = "/del/{id}"
 *          //   action path = "/app/sample2" + "/{id}" = "/app/sample2/{id}"
 *        }
 * 
 *        @DELETE("{name}") @admin public void del(final String name) {
 *          // given:
 *          //   no @template suppport -> it is considered an API action
 *          //   no @admin defined -> it is considered a regular application action
 *          // then:
 *          //   base path   = "/adm/api/sample2"
 *          //   action name = "/del/{id}"
 *          //   action path = "/adm/api/sample2/del" + "/{name}" = "/adm/api/sample2/del/{name}"
 *        }
 * }
 * </code></pre>
 * <em>note</em>: an action is considered to use template when it is annotated with {@code @ae.template(true)} or it is not
 * annotated with {@code @ae.template(false)} and the controller is annotated with {@code @ae.template(true)}.
 */
@Retention(SOURCE)
@Target(METHOD)
public @interface DELETE {
        /**@return  the path spec of the action. */
        String value() default "<UNDEFINED>";
}
