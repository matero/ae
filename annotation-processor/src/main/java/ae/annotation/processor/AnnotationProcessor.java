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
package ae.annotation.processor;

import java.util.Date;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public abstract class AnnotationProcessor extends AbstractProcessor {

  protected final Date today;

  protected AnnotationProcessor(final Date today)
  {
    this.today = today;
  }

  @RequiresNonNull("processingEnv") protected void error(final Element element, final Throwable failure)
  {
    message(Diagnostic.Kind.ERROR, message(failure), element);
  }

  @RequiresNonNull("processingEnv")
  protected void error(final Element element, final String errorMessage)
  {
    message(Diagnostic.Kind.ERROR, errorMessage, element);
  }

  @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
  {
    if (isInitialized()) {
      return processAnnotations(annotations, roundEnvironment);
    }
    return false;
  }

  @RequiresNonNull("processingEnv") protected abstract boolean processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment);

  @Override protected synchronized @EnsuresNonNullIf(expression = "this.processingEnv", result = true) boolean isInitialized()
  {
    return super.isInitialized() && processingEnv != null;
  }

  @RequiresNonNull("processingEnv")
  protected final void info(final Element element, final String infoMessage, final Object... args)
  {
    info(element, String.format(infoMessage, args));
  }

  @RequiresNonNull("processingEnv")
  protected final void info(final String infoMessage, final Object... args)
  {
    message(Diagnostic.Kind.NOTE, String.format(infoMessage, args));
  }

  @RequiresNonNull("processingEnv")
  protected final void info(final Element element, final String infoMessage)
  {
    message(Diagnostic.Kind.NOTE, String.format("[%s] %s", getClass().getSimpleName(), infoMessage), element);
  }

  @RequiresNonNull("processingEnv")
  protected void error(final Throwable failure)
  {
    error(message(failure));
  }

  @RequiresNonNull("processingEnv")
  protected void error(final String message)
  {
    message(Diagnostic.Kind.ERROR, message);
  }

  /**
   * Prints a message of the specified kind.
   *
   * @param kind the kind of message
   * @param msg the message, or an empty string if none
   */
  @RequiresNonNull("processingEnv")
  protected final void message(final Diagnostic.Kind kind, final CharSequence msg)
  {
    processingEnv.getMessager().printMessage(kind, msg);
  }

  /**
   * Prints a message of the specified kind at the location of the element.
   *
   * @param kind the kind of message
   * @param msg the message, or an empty string if none
   * @param e the element to use as a position hint
   */
  @RequiresNonNull("processingEnv")
  protected final void message(final Diagnostic.Kind kind, final CharSequence msg, final Element e)
  {
    processingEnv.getMessager().printMessage(kind, msg, e);
  }

  /**
   * Prints a message of the specified kind at the location of the annotation mirror of the annotated element.
   *
   * @param kind the kind of message
   * @param msg the message, or an empty string if none
   * @param e the annotated element
   * @param a the annotation to use as a position hint
   */
  @RequiresNonNull("processingEnv")
  protected final void message(final Diagnostic.Kind kind, final CharSequence msg, final Element e, final AnnotationMirror a)
  {
    processingEnv.getMessager().printMessage(kind, msg, e, a);
  }

  /**
   * Prints a message of the specified kind at the location of the annotation value inside the annotation mirror of the annotated element.
   *
   * @param kind the kind of message
   * @param msg the message, or an empty string if none
   * @param e the annotated element
   * @param a the annotation containing the annotation value
   * @param v the annotation value to use as a position hint
   */
  @RequiresNonNull("processingEnv")
  protected final void message(final Diagnostic.Kind kind, final CharSequence msg, final Element e, final AnnotationMirror a, final AnnotationValue v)
  {
    processingEnv.getMessager().printMessage(kind, msg, e, a, v);
  }

  protected final String message(final Throwable t)
  {
    final String msg = t.getMessage();
    return msg == null ? "unknown error" : msg;
  }

  @RequiresNonNull("processingEnv")
  protected String readSuperClassCannonicalName(final TypeMirror superClass)
  {
    return processingEnv.getTypeUtils().asElement(superClass).getSimpleName().toString();
  }
}
