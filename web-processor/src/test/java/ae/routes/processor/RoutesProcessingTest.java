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
import java.time.ZoneId;
import java.util.*;

import org.junit.Test;

public class RoutesProcessingTest {

        static final Date GENERATION_DATE;

        static {
                final GregorianCalendar calendar = new GregorianCalendar(2017, Calendar.FEBRUARY, 23, 12, 0);
                calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC-3")));
                GENERATION_DATE = calendar.getTime();
        }

        final Compiler compiler = javac().withProcessors(
                new ControllerAndRoutesCompiler(GENERATION_DATE, new RoutersCodeBuilder()));

        @Test
        public void should_compile_multiple_verb_routes()
        {
                final Compilation compilation = compiler.compile(
                        JavaFileObjects.forResource("RouterAndController.java")
                );
                assertThat(compilation).succeeded();
                assertThat(compilation)
                        .generatedSourceFile("processor.test.SigexRouter")
                        .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/routes/SigexRouter.java"));
        }
        
        
        @Test
        public void should_compile_with_roles_constraints_defined()
        {
                final Compilation compilation = compiler.compile(
                        JavaFileObjects.forResource("RolesConstraintsController.java")
                );
                assertThat(compilation).succeeded();
                assertThat(compilation)
                        .generatedSourceFile("processor.test.RolesRouter")
                        .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/routes/RolesRouter.java"));
        }
}
