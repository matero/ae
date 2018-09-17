package book;

import java.util.logging.Logger;
import javax.annotation.Generated;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

@Generated(
        value = "AE/web-processor",
        comments = "Build from specs at: src/test/resources/routes/routes.csv",
        date = "2017-02-23"
)
public final class Controller_Impl extends Controller {

    private static final Logger LOG = Logger.getLogger("book.Controller");

    public Controller_Impl(final HttpServletRequest request, final HttpServletResponse response)
    {
        setRequest(request);
        setResponse(response);
    }

    public Controller_Impl(final HttpServletRequest request, final HttpServletResponse response,
                           final WebContext templateContext, final TemplateEngine templateEngine)
    {
        setRequest(request);
        setResponse(response);
        setTemplateContext(templateContext);
        setTemplateEngine(templateEngine);
    }

    @Override
    protected Logger log()
    {
        return LOG;
    }
}
