package ae.web.processor;

import com.google.common.collect.ImmutableList;

import javax.lang.model.element.ExecutableElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PathSpec
{
  // Matches: {id} AND {id: .*?}
  // group(1) extracts the name of the group (in that case "id").
  // group(3) extracts the regex if defined
  private static final Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

  // This regex matches everything in between path slashes.
  private static final String VARIABLE_ROUTES_DEFAULT_REGEX = "(?<%s>[^/]+)";

  final String path;
  final String pattern;
  final String regex;
  final ImmutableList<String> parameters;

  PathSpec(final String path, final String pattern)
  {
    this(path, pattern, null, ImmutableList.of());
  }

  PathSpec(final String path, final String pattern, final String regex, final ImmutableList<String> parameters)
  {
    this.path = path;
    this.pattern = pattern;
    this.regex = regex;
    this.parameters = parameters;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 53 * hash + this.pattern.hashCode();
    return hash;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj instanceof PathSpec) {
      final PathSpec other = (PathSpec) obj;
      return this.pattern.equals(other.pattern);
    }
    return false;
  }

  boolean isStatic()
  {
    return parameters.isEmpty();
  }

  static PathSpec from(final String uri, final String urlPattern)
  {
    final String regex = findRegex(urlPattern);
    if (regex == null) {
      return new PathSpec(uri, urlPattern);
    } else {
      final ImmutableList<String> parameterNames = findParameterNames(urlPattern);
      return new PathSpec(uri, urlPattern, regex, parameterNames);
    }
  }

  /**
   * Transforms an url pattern like "/{name}/id/*" into a regex like "/([^/]*)/id/*."
   * <p/>
   * Also handles regular expressions if defined inside endpoints: For instance "/users/{username: [a-zA-Z][a-zA-Z_0-9]}" becomes "/users/
   * ([a-zA-Z][a-zA-Z_0-9])"
   *
   * @return The converted regex with default matching regex - or the regex specified by the user.
   */
  static String findRegex(final String urlPattern)
  {
    final StringBuffer buffer = new StringBuffer();
    final Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(urlPattern);
    int pathParameterIndex = 0;

    while (matcher.find()) {
      // By convention group 3 is the regex if provided by the user.
      // If it is not provided by the user the group 3 is null.
      final String parameter = matcher.group(1);
      final String namedVariablePartOfRoute = matcher.group(3);
      final String namedVariablePartOfORouteReplacedWithRegex;

      if (namedVariablePartOfRoute != null) {
        // we convert that into a regex matcher group itself
        final String variableRegex = replacePosixClasses(namedVariablePartOfRoute);
        namedVariablePartOfORouteReplacedWithRegex = String.format("(?<%s>%s)", parameter, Matcher.quoteReplacement(variableRegex));
      } else {
        // we convert that into the default namedVariablePartOfRoute regex group
        namedVariablePartOfORouteReplacedWithRegex = String.format(VARIABLE_ROUTES_DEFAULT_REGEX, parameter);
      }
      // we replace the current namedVariablePartOfRoute group
      matcher.appendReplacement(buffer, namedVariablePartOfORouteReplacedWithRegex);
      pathParameterIndex++;
    }

    if (pathParameterIndex == 0) {
      // when no "dynamic" part found, no regex is found ;)
      return null;
    } else {
      // .. and we append the tail to complete the stringBuffer
      matcher.appendTail(buffer);

      return buffer.toString();
    }
  }

  /**
   * Replace any specified POSIX character classes with the Java equivalent.
   *
   * @param input
   * @return a Java regex
   */
  final static String replacePosixClasses(final String input)
  {
    return input
               .replace(":alnum:", "\\p{Alnum}")
               .replace(":alpha:", "\\p{L}")
               .replace(":ascii:", "\\p{ASCII}")
               .replace(":digit:", "\\p{Digit}")
               .replace(":xdigit:", "\\p{XDigit}");
  }

  /**
   * Extracts the name of the parameters from a path
   * <p/>
   * /{my_id}/{my_name}
   * <p/>
   * would return a List with "my_id" and "my_name"
   *
   * @param uriPattern
   * @return a list with the names of all parameters in the url pattern
   */
  final static ImmutableList<String> findParameterNames(final String uriPattern)
  {
    final ImmutableList.Builder<String> parameters = ImmutableList.builder();

    final Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(uriPattern);
    while (matcher.find()) {
      // group(1) is the name of the group. Must be always there...
      // "/assets/{file}" and "/assets/{file:[a-zA-Z][a-zA-Z_0-9]}"
      // will return file.
      parameters.add(matcher.group(1));
    }

    return parameters.build();
  }

  public Route makeRoute(final HttpVerb verb,
                         final ExecutableElement method,
                         final boolean useCredentials,
                         final String[] roles,
                         final String namespace)
  {
    final String handler = method.getSimpleName().toString();
    if (isStatic()) {
      return new Route(path, verb, pattern, useCredentials, roles, namespace, handler);
    } else {
      return new Route(path, verb, pattern, regex, useCredentials, roles, namespace, handler, parameters);
    }
  }
}

