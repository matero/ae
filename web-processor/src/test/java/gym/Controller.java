package gym;

import ae.OAuth2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller extends ae.web.ControllerWithThymeleafSupport {
  private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

  public Controller(final HttpServletRequest request, final HttpServletResponse response) {
    super(request, response);
  }

  public Controller(final HttpServletRequest request,
                    final HttpServletResponse response,
                    final WebContext webContext,
                    final TemplateEngine templateEngine) {
    super(request, response, webContext, templateEngine);
  }

  @Override protected final Logger log() {
    return LOG;
  }

  public void index() {
    // nothing to do
  }

  public void create() {
    // nothing to do
  }

  public void save() {
    // nothing to do
  }

  public void show(final long id) {
    // nothing to do
  }

  public void edit(final long id) {
    // nothing to do
  }

  public void update(final long id) {
    // nothing to do
  }

  public void delete(final long id) {
    // nothing to do
  }

  @OAuth2 public void saracatunga(final String name) {
    // nothing to do
  }
}
