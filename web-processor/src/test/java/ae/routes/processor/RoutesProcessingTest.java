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
package ae.routes.processor;

import com.google.testing.compile.Compilation;
import static com.google.testing.compile.CompilationSubject.assertThat;
import com.google.testing.compile.Compiler;
import static com.google.testing.compile.Compiler.javac;
import com.google.testing.compile.JavaFileObjects;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;

public class RoutesProcessingTest {
  final Compiler compiler = javac().withProcessors(
          new RoutesCompiler(new GregorianCalendar(2017, Calendar.FEBRUARY, 23).getTime())
  );

  @Test
  public void should_compile_simple_routes() {
    final Compilation compilation = compiler.compile(
            JavaFileObjects.forSourceString(
                    "AppRouter",
                    "package processor.test;\n"
                    + "@ae.Router(routes=\"src/test/resources/routes/simple_routes.csv\")\n"
                    + "public final class AppRouter extends RouterDefs {}"
            )
    );
    assertThat(compilation).succeeded();
    assertThat(compilation)
            .generatedSourceFile("processor.test.RouterDefs")
            .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/simple_routes/RouterDefs.java"));
  }

  @Test
  public void should_compile_multiple_verb_routes() {
    final Compilation compilation = compiler.compile(
            JavaFileObjects.forSourceString(
                    "AppRouter",
                    "package processor.test;\n"
                    + "@ae.Router(routes=\"src/test/resources/routes/routes.csv\")\n"
                    + "public final class AppRouter extends RouterDefs {}"
            )
    );
    assertThat(compilation).succeeded();
    assertThat(compilation)
            .generatedSourceFile("processor.test.RouterDefs")
            .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/routes/RouterDefs.java"));
  }

  @Test
  public void should_compile_multiple_verb_and_packages_routes() {
    final Compilation compilation = compiler.compile(
            JavaFileObjects.forSourceString(
                    "AppRouter",
                    "package processor.test;\n"
                    + "@ae.Router(routes=\"src/test/resources/routes/multiple_verb_and_package.csv\")\n"
                    + "public final class AppRouter extends RouterDefs {}"
            )
    );
    assertThat(compilation).succeeded();
    assertThat(compilation)
            .generatedSourceFile("processor.test.RouterDefs")
            .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/multiple_verb_and_package/RouterDefs.java"));
  }
}
