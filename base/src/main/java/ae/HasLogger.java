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
package ae;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Little wrapper around JDK logger, requires you to know the Logger at construction time.
 */
public abstract class HasLogger {
  protected HasLogger() {
    // nothing to do
  }

  protected abstract Logger logger();

  protected void log(final LogRecord record) {
    logger().log(record);
  }

  protected void log(final Level level, final String msg) {
    logger().log(level, msg);
  }

  protected void log(final Level level, final Supplier<String> msgSupplier) {
    logger().log(level, msgSupplier);
  }

  protected void log(final Level level, final String msg, final Object param) {
    logger().log(level, msg, param);
  }

  protected void log(final Level level, final String msg, final Object... params) {
    logger().log(level, msg, params);
  }

  protected void log(final Level level, final String msg, final Throwable thrown) {
    logger().log(level, msg, thrown);
  }

  protected void log(final Level level, final Throwable thrown, final Supplier<String> msgSupplier) {
    logger().log(level, thrown, msgSupplier);
  }

  protected void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg) {
    logger().logp(level, sourceClass, sourceMethod, msg);
  }

  protected void logp(final Level level, final String sourceClass, final String sourceMethod, final Supplier<String> msgSupplier) {
    logger().logp(level, sourceClass, sourceMethod, msgSupplier);
  }

  protected void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Object param) {
    logger().logp(level, sourceClass, sourceMethod, msg, param);
  }

  protected void logp(final Level level,
                      final String sourceClass,
                      final String sourceMethod,
                      final String msg,
                      final Object... params) {
    logger().logp(level, sourceClass, sourceMethod, msg, params);
  }

  protected void logp(final Level level,
                      final String sourceClass,
                      final String sourceMethod,
                      final String msg,
                      final Throwable thrown) {
    logger().logp(level, sourceClass, sourceMethod, msg, thrown);
  }

  protected void logp(final Level level,
                      final String sourceClass,
                      final String sourceMethod,
                      final Throwable thrown,
                      final Supplier<String> msgSupplier) {
    logger().logp(level, sourceClass, sourceMethod, thrown, msgSupplier);
  }

  protected void logrb(final Level level,
                       final String sourceClass,
                       final String sourceMethod,
                       final ResourceBundle bundle,
                       final String msg,
                       final Throwable thrown) {
    logger().logrb(level, sourceClass, sourceMethod, bundle, msg, thrown);
  }

  protected void logEntering(final String sourceClass, final String sourceMethod) {
    logger().entering(sourceClass, sourceMethod);
  }

  protected void logEntering(final String sourceClass, final String sourceMethod, final Object param) {
    logger().entering(sourceClass, sourceMethod, param);
  }

  protected void logEntering(final String sourceClass, final String sourceMethod, final Object... params) {
    logger().entering(sourceClass, sourceMethod, params);
  }

  protected void logExiting(final String sourceClass, final String sourceMethod) {
    logger().exiting(sourceClass, sourceMethod);
  }

  protected void logExiting(final String sourceClass, final String sourceMethod, final Object result) {
    logger().exiting(sourceClass, sourceMethod, result);
  }

  protected void logThrowing(final String sourceClass, final String sourceMethod, final Throwable thrown) {
    logger().throwing(sourceClass, sourceMethod, thrown);
  }

  protected boolean isLoggable(final Level level) {
    return logger().isLoggable(level);
  }
}
