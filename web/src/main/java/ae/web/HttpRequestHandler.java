package ae.web;

import javax.servlet.ServletException;
import java.io.IOException;

@FunctionalInterface
public interface HttpRequestHandler<C extends Controller> {

    /**
     * Performs this operation on the given handler.
     *
     * @param handler the handler to manipulate
     * @throws ServletException if anything goes wrong.
     * @throws IOException if it can not write the response.
     */
    void accept(C handler) throws ServletException, IOException;
}
