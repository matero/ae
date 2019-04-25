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
package ae.web.processor;

import ae.web.RouterServlet;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import java.util.regex.Pattern;

final class Route
{
  final HttpVerb verb;
  final String path;
  final String pattern;
  final String regex;
  final String handler;
  final ImmutableList<String> parameters;
  final boolean useCredentials;
  final String[] roles;
  final String namespace;

  Route(final String path,
        final HttpVerb verb,
        final String pattern,
        final boolean useCredentials,
        final String[] roles,
        final String namespace,
        final String handler)
  {
    this(path, verb, pattern, pattern, useCredentials, roles, namespace, handler, ImmutableList.of());
  }

  Route(final String path,
        final HttpVerb verb,
        final String pattern,
        final String regex,
        final boolean useCredentials,
        final String[] roles,
        final String namespace,
        final String handler,
        final ImmutableList<String> parameters)
  {
    this.path = path;
    this.verb = verb;
    this.pattern = pattern;
    this.regex = regex;
    this.useCredentials = useCredentials;
    if (roles == null) {
      this.roles = new String[0];
    } else {
      this.roles = roles.clone();
    }
    this.namespace = namespace;
    this.parameters = parameters;
    this.handler = handler;
  }

  @Override public int hashCode()
  {
    int hash = 3;
    hash = 17 * hash + this.verb.hashCode();
    hash = 17 * hash + this.pattern.hashCode();
    return hash;
  }

  @Override public boolean equals(final Object that)
  {
    if (this == that) {
      return true;
    }
    if (that instanceof Route) {
      final Route other = (Route) that;
      return this.pattern.equals(other.pattern) && this.verb == other.verb;
    }
    return false;
  }

  @Override public String toString()
  {
    return "Path{verb=" + verb + ", pattern='" + pattern + "'}";
  }

  boolean hasRoleConstrains()
  {
    return this.roles.length > 0;
  }

  String rolesExpr()
  {
    switch (this.roles.length) {
      case 1:
        return "userRoleIs(userData, $S)";
      case 2:
        return "userRoleIsIn(userData, $S, $S)";
      case 3:
        return "userRoleIsIn(userData, $S, $S, $S)";
      case 4:
        return "userRoleIsIn(userData, $S, $S, $S, $S)";
      default:
        throw new IllegalStateException("roles count not supported!");
    }
  }

  String routeField()
  {
    return this.verb.name() + '_' + handler;
  }

  boolean isParameterized()
  {
    return !parameters.isEmpty();
  }

  int parametersCount()
  {
    return parameters.size();
  }

  MethodSpec.Builder makeMatcher(final MethodSpec.Builder httpVerbHandler)
  {
    if (isIndex()) {
      return httpVerbHandler.beginControlFlow("if (indexPath.matches(request))", ClassName.bestGuess("ae.web.RouterServlet.IndexPath"));
    } else {
      return httpVerbHandler.beginControlFlow("if ($L.matches(request))", routeField());
    }
  }

  boolean hasNamespace()
  {
    return namespace != null && namespace.length() > 0;
  }

  CodeBlock namespaceStatment()
  {
    if ("#".equals(namespace)) {
      return CodeBlock.of("useNamespace(getUserNamespace(userData))");
    } else {
      return CodeBlock.of("useNamespace($S)", namespace);
    }
  }

  boolean isIndex()
  {
    return pattern == null || "".equals(pattern) || "/".equals(pattern);
  }

  FieldSpec makeField()
  {
    if (isIndex()) {
      return null;
    }

    final String pkg = RouterServlet.class.getPackage().getName();
    final String servlet = RouterServlet.class.getSimpleName();

    final ClassName pathClassName = ClassName.get(pkg, servlet, "Path");
    final FieldSpec.Builder property = FieldSpec.builder(pathClassName, routeField(), Modifier.PRIVATE, Modifier.FINAL);

    if (isParameterized()) {
      final String extraArgs = Strings.repeat(", $S", parametersCount() - 1);
      final String code = "parameterizedPath($S, $S, $T.of($S" + extraArgs + "), $T.compile($S))";
      final Object[] args = new Object[5 + parametersCount()];
      {
        int i = 0;
        args[i++] = this.path;
        args[i++] = pattern;
        args[i++] = ClassName.get(ImmutableList.class);
        for (final String parameter : parameters) {
          args[i++] = parameter;
        }
        args[i++] = ClassName.get(Pattern.class);
        args[i] = regex;
      }
      property.initializer(code, args);
    } else {
      property.initializer("staticPath($S, $S)", new Object[]{this.path, pattern});
    }
    return property.build();
  }

  boolean mustAccessUserData()
  {
    return hasRoleConstrains() || hasNamespace();
  }
}
