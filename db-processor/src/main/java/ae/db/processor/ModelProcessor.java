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

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import ae.annotation.processor.AnnotationProcessor;
import ae.model;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

@AutoService(Processor.class)
@SupportedAnnotationTypes("ae.model")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ModelProcessor extends AnnotationProcessor {

  public ModelProcessor()
  {
    this(new Date());
  }

  ModelProcessor(final Date today)
  {
    super(today);
  }

  @Override public @RequiresNonNull("processingEnv") boolean processAnnotations(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
  {
    if (!isInitialized()) {
      throw new IllegalStateException("not initialized!");
    }

    info("START @ae.db.Model processing");
    final Set<? extends Element> models = roundEnv.getElementsAnnotatedWith(model.class);
    if (models.isEmpty()) {
      info("DONE, no classes annotated  with @ae.db.Model to process");
    } else {
      final MetaModels metamodels;
      try {
        metamodels = metaModelFor(models);
      } catch (final ModelException e) {
        error(e);
        info("HALT processing %d classes annotated with @ae.db.Model", models.size());
        return true;
      } catch (final RuntimeException e) {
        error(e);
        info("HALT processing %d classes annotated with @ae.db.Model", models.size());
        return true;
      }
      generateCode(metamodels);
      info("DONE processing %d classes annotated with @ae.db.Model", models.size());
    }
    return true;
  }

  @RequiresNonNull("processingEnv") MetaModels metaModelFor(final Set<? extends Element> models)
  {
    final MetaModels.Builder modelsBuilder = MetaModels.builder();
    final ModelInterpreter interpreter = new ModelInterpreter(processingEnv);

    for (final Element modelElement : models) {
      final TypeElement model = (TypeElement) modelElement;
      final String modelQualifiedName = model.getQualifiedName().toString();

      info("reading meta-data of [%s].", modelQualifiedName);
      modelsBuilder.add(interpreter.read(modelElement));
      info("meta-data of [%s] read.", modelQualifiedName);
    }

    return modelsBuilder.build();
  }

  @RequiresNonNull("processingEnv") void generateCode(final MetaModels models)
  {
    generateCode("'base models'", models, new ModelBaseClassCodeGenerator());
  }

  @RequiresNonNull("processingEnv") void generateCode(final String name, final MetaModels models, final CodeGenerator codeGenerator)
  {
    info("generating [%s]", name);
    for (final JavaFile generatedCode : codeGenerator.generateCode(models, today)) {
      try {
        generatedCode.writeTo(processingEnv.getFiler());
      } catch (final IOException e) {
        throw new IllegalStateException("could not generate " + name, e);
      }
    }
  }

  @RequiresNonNull("processingEnv") final void error(final ModelException failure)
  {
    if (failure.element == null) {
      error(message(failure));
    } else {
      message(Diagnostic.Kind.ERROR, message(failure), failure.element);
    }
  }
}
