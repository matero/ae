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

import com.google.common.collect.ImmutableSet;
import javax.lang.model.element.Modifier;

abstract class MetaData {

        final String name;
        final ImmutableSet<Modifier> modifiers;
        final Modifier visibility;

        MetaData(final String name, final Iterable<Modifier> modifiers)
        {
                this(name, modifiers, findVisibilityAt(modifiers));
        }

        MetaData(final String name, final Iterable<Modifier> modifiers, final Modifier visibility)
        {
                this.name = name;
                this.modifiers = ImmutableSet.copyOf(modifiers);
                this.visibility = visibility;
        }

        private static Modifier findVisibilityAt(final Iterable<Modifier> elementModifiers)
        {
                for (final Modifier modifier : elementModifiers) {
                        switch (modifier) {
                                case PRIVATE:
                                case PROTECTED:
                                case PUBLIC:
                                        return modifier;
                        }
                }
                return null;
        }
}
