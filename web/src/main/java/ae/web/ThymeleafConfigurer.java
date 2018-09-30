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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.utils.SystemProperty;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import ognl.OgnlRuntime;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebListener
public final class ThymeleafConfigurer implements javax.servlet.ServletContextListener {

        @Override
        public void contextInitialized(final ServletContextEvent event)
        {
                OgnlRuntime.setSecurityManager(null);
                OgnlRuntime.setPropertyAccessor(Entity.class, AppEngineEntityPropertyAccessor.INSTANCE);

                final ServletContext servletContext = event.getServletContext();
                ThymeleafTemplateEngine.set(servletContext, templateEngine(servletContext));
        }

        private TemplateEngine templateEngine(final ServletContext servletContext)
        {
                final TemplateEngine engine = new TemplateEngine();
                engine.setTemplateResolver(templateResolver(servletContext));
                engine.addDialect(new LayoutDialect());
                return engine;
        }

        private ITemplateResolver templateResolver(final ServletContext servletContext)
        {
                final AbstractConfigurableTemplateResolver resolver;
                if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
                        resolver = new ServletContextTemplateResolver(servletContext);
                } else {
                        resolver = new FileTemplateResolver();
                }

                // HTML is the default mode
                resolver.setTemplateMode(TemplateMode.HTML);

                // interpret "home" to "${templates-dir}/home.html"
                final String templatesDir = System.getenv("templates-dir");
                resolver.setPrefix(templatesDir);
                resolver.setSuffix(".html");

                // Set template cache TTL to 1 hour.
                resolver.setCacheTTLMs(3600000L);

                // Cache is set to true by default. Set to false if you want templates to
                // be automatically updated when modified.
                resolver.setCacheable(shouldCacheTemplates());
                
                return resolver;
        }
        
        private boolean shouldCacheTemplates()
        {
                final String cache = System.getenv("cache-templates");
                return Boolean.parseBoolean(cache);
        }

        @Override
        public void contextDestroyed(final ServletContextEvent event)
        {
                ThymeleafTemplateEngine.set(event.getServletContext(), null);
        }
}
