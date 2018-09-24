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

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ae.db.EmailConstraint;
import ae.db.NotBlankConstraint;
import ae.Record;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import javax.lang.model.type.DeclaredType;

abstract class ModelAttributeInterpreter {

        final Types types;
        final Elements elements;

        final TypeMirror categoryClass;
        final TypeMirror emailClass;
        final TypeMirror postalAddressClass;
        final TypeMirror phoneNumberClass;
        final TypeMirror stringClass;
        final TypeMirror textClass;
        final TypeMirror listClass;

        ModelAttributeInterpreter(final ProcessingEnvironment environment)
        {
                this(environment.getTypeUtils(), environment.getElementUtils());
        }

        ModelAttributeInterpreter(final Types typeUtils, final Elements elements)
        {
                this.types = typeUtils;
                this.elements = elements;
                this.listClass = elements.getTypeElement(List.class.getCanonicalName()).asType();
                this.categoryClass = elements.getTypeElement(Category.class.getCanonicalName()).asType();
                this.emailClass = elements.getTypeElement(Email.class.getCanonicalName()).asType();
                this.postalAddressClass = elements.getTypeElement(PostalAddress.class.getCanonicalName()).asType();
                this.phoneNumberClass = elements.getTypeElement(PhoneNumber.class.getCanonicalName()).asType();
                this.stringClass = elements.getTypeElement(String.class.getCanonicalName()).asType();
                this.textClass = elements.getTypeElement(Text.class.getCanonicalName()).asType();
        }

        void checkModifiersOf(final VariableElement variable, final Class<? extends Annotation> annotation)
                throws ModelException
        {
                final Set<Modifier> modifiers = variable.getModifiers();
                if (modifiers.contains(Modifier.STATIC)) {
                        throw new ModelException(variable, "only member fields can be annotated with @" + annotation.
                                                 getCanonicalName());
                }
                if (modifiers.contains(Modifier.TRANSIENT)) {
                        throw new ModelException(variable, "transient fields can not be annotated with @" + annotation.
                                                 getCanonicalName());
                }
                if (modifiers.contains(Modifier.VOLATILE)) {
                        throw new ModelException(variable, "volatile fields can not be annotated with @" + annotation.
                                                 getCanonicalName());
                }
        }

        ImmutableSet< Modifier> modifiersOf(final VariableElement variable)
        {
                final ImmutableSet.Builder<Modifier> modifiers = ImmutableSet.builder();
                modifiers.addAll(variable.getModifiers());
                modifiers.add(Modifier.FINAL);
                return modifiers.build();
        }

        String nameOf(final VariableElement variable)
        {
                return variable.getSimpleName().toString();
        }

        String descriptionOf(final VariableElement variable)
        {
                final Record.description descr = variable.getAnnotation(Record.description.class);
                if (descr == null) {
                        return descriptionAt(variable.getSimpleName().toString());
                } else {
                        return descr.value();
                }
        }

        String descriptionAt(final String text)
        {
                final String[] splitted = text.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
                final StringBuilder sb = new StringBuilder().append(splitted[0].substring(0, 1).toUpperCase()).append(
                        splitted[0].substring(1));
                for (int i = 1; i < splitted.length; i++) {
                        sb.append(' ').append(splitted[i]);
                }
                return sb.toString();
        }

        TypeName typeNameOf(final VariableElement variable)
        {
                final TypeMirror type = variable.asType();
                if (type.getKind().isPrimitive()) {
                        return ClassName.get(type);
                }
                if (TypeKind.DECLARED == type.getKind()) {
                        String typename = type.toString();
                        if ("java.util.List".equals(typename) || typename.startsWith("java.util.List<")) {
                                final DeclaredType listElement = (DeclaredType) type;
                                final List<? extends TypeMirror> listElementClass = listElement.getTypeArguments();
                                final TypeName t;
                                if (listElementClass.isEmpty()) {
                                        t = ClassName.get(Object.class);
                                } else {
                                        final TypeMirror asType = listElementClass.get(0);
                                        t = ClassName.get(asType);
                                }
                                return ParameterizedTypeName.get(ClassName.get(List.class), t);
                        } else {
                                return ClassName.get(type);
                        }
                }
                throw new ModelException(variable, "type not supported");
        }

        boolean isRequired(final VariableElement variable)
        {
                return variable.getAnnotation(Record.required.class) != null;
        }

        boolean shouldIgnoreAtJsonSerialization(final VariableElement variable)
        {
                final Record.json json = variable.getAnnotation(Record.json.class);
                if (json == null) {
                        return false;
                }
                return json.ignore();
        }

        String jsonFormat(final VariableElement variable)
        {
                final Record.json json = variable.getAnnotation(Record.json.class);
                if (json == null) {
                        return "";
                }
                if (json.format() == null) {
                        return "";
                }
                return json.format();
        }

        List<MetaConstraint> constraintsOf(final VariableElement variable)
        {
                final LinkedList<MetaConstraint> constraints = new LinkedList<>();

                {
                        final Record.notBlank notBlank = variable.getAnnotation(Record.notBlank.class);
                        if (notBlank != null) {
                                constraints.add(new MetaConstraint(notBlankExpressionFor(variable)));
                        }
                }
                {
                        final Record.email email = variable.getAnnotation(Record.email.class);
                        if (email != null) {
                                constraints.add(new MetaConstraint(emailExpressionFor(variable)));
                        }
                }
                return constraints;
        }

        String notBlankExpressionFor(final VariableElement variable)
        {
                final TypeMirror type = variable.asType();
                if (type.equals(stringClass)) {
                        return NotBlankConstraint.ForString.class.getCanonicalName() + ".INSTANCE";
                }
                if (type.equals(textClass)) {
                        return NotBlankConstraint.ForText.class.getCanonicalName() + ".INSTANCE";
                }
                if (type.equals(emailClass)) {
                        throw new ModelException(variable,
                                                 "Email fields can't be blank by definition; maybe you mean @Required");
                }
                if (type.equals(categoryClass)) {
                        throw new ModelException(variable,
                                                 "Category fields can't be blank by definition; maybe you mean @Required");
                }
                if (type.equals(postalAddressClass)) {
                        throw new ModelException(variable,
                                                 "PostalAddress fields can't be blank by definition; maybe you mean @Required");
                }
                if (type.equals(phoneNumberClass)) {
                        throw new ModelException(variable,
                                                 "PhoneNumber fields can't be blank by definition; maybe you mean @Required");
                }
                throw new ModelException(variable, "@NotBlank isn't applycable on " + type.toString() + " fields");
        }

        String emailExpressionFor(final VariableElement variable)
        {
                final TypeMirror type = variable.asType();
                if (type.equals(stringClass)) {
                        return EmailConstraint.ForString.class.getCanonicalName() + ".INSTANCE";
                }
                if (type.equals(emailClass)) {
                        throw new ModelException(variable,
                                                 "Email fields are allways checked as valid emails. Please remove @Email constraint from this field.");
                }
                throw new ModelException(variable, "@Email isn't applycable on " + type.toString() + " fields");
        }
}
