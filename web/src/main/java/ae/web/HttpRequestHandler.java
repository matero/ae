package ae.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface public interface HttpRequestHandler
{
  void handle(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException;
}
