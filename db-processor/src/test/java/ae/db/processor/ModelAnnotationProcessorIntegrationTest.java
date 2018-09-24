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
package ae.db.processor;

import com.google.testing.compile.Compilation;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import com.google.testing.compile.JavaFileObjects;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;

public class ModelAnnotationProcessorIntegrationTest {

    @Test
    public void should_compile_class_without_Model_annotation()
    {
        final Compilation compilation = javac()
                .withProcessors(new ModelProcessor())
                .compile(JavaFileObjects.forResource("ae/db/processor/NoModel.java"));
        assertThat(compilation)
                .succeeded();
    }

    @Test
    public void should_fail_when_class_doesnt_define_superclass()
    {
        final Compilation compilation = javac()
                .withProcessors(new ModelProcessor())
                .compile(JavaFileObjects.forResource("ae/db/processor/NoSuperClass.java"));
        assertThat(compilation)
                .failed();
        assertThat(compilation)
                .hadErrorContaining("No base class defined!");
    }

    @Test
    public void should_fail_when_class_doesnt_define_record()
    {
        final Compilation compilation = javac()
                .withProcessors(new ModelProcessor())
                .compile(JavaFileObjects.forResource("ae/db/processor/NoRecord.java"));
        assertThat(compilation)
                .failed();
        assertThat(compilation)
                .hadErrorContaining("No Record class found!");
    }

    @Test
    public void should_fail_when_class_defines_more_than_one_record()
    {
        final Compilation compilation = javac()
                .withProcessors(new ModelProcessor())
                .compile(JavaFileObjects.forResource("ae/db/processor/TooManyRecords.java"));
        assertThat(compilation)
                .failed();
        assertThat(compilation)
                .hadErrorContaining("Only one Record can be defined per Model!");
    }

    @Test
    public void should_be_able_to_process_complex_classes()
    {
        final Compilation compilation = javac()
                .withProcessors(new ModelProcessor(new GregorianCalendar(2017, Calendar.FEBRUARY, 23).getTime()))
                .compile(JavaFileObjects.forResource("ae/db/processor/Competidor.java"));
        assertThat(compilation)
                .succeeded();
        assertThat(compilation)
                .generatedSourceFile("processor.test.__Competencia")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/__Competencia.java"));
        assertThat(compilation)
                .generatedSourceFile("processor.test.__Competidor")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("generated/__Competidor.java"));
    }
}
