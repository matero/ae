package ae.web;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

public class QueryParameter<T> extends RequestValueReader<T>
{
  public static final class NotDefined extends RuntimeException
  {
    public final HttpServletRequest request;

    NotDefined(final String parameterName, final HttpServletRequest request)
    {
      super(parameterName);
      this.request = request;
    }
  }

  private static <T> Supplier<T> nullDefaultValue()
  {
    return () -> null;
  }

  private final boolean required;
  private final Supplier<T> defaultValue;

  public QueryParameter(final String name, final ValueInterpreter<T> interpretValue)
  {
    this(name, false, interpretValue);
  }

  public QueryParameter(final String name, final boolean required, final ValueInterpreter<T> interpretValue)
  {
    this(name, required, interpretValue, nullDefaultValue());
  }

  public QueryParameter(final String name, final ValueInterpreter<T> interpretValue, final Supplier<T> defaultValue)
  {
    this(name, false, interpretValue, defaultValue);
  }

  public QueryParameter(final String name,
                        final boolean required,
                        final ValueInterpreter<T> interpretValue,
                        final Supplier<T> defaultValue)
  {
    super(name, interpretValue);
    this.required = required;
    this.defaultValue = defaultValue;
  }

  public final boolean isDefinedAt(final HttpServletRequest request)
  {
    return request.getParameterMap().containsKey(name);
  }

  @Override protected final T valueUndefined(final HttpServletRequest request)
  {
    if (required) {
      throw new NotDefined(name, request);
    }
    return defaultValue.get();
  }

  protected final String read(final HttpServletRequest request)
  {
    return request.getParameter(name);
  }
}
