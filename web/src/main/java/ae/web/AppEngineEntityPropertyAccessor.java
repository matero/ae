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
package ae.web;

import com.google.appengine.api.datastore.Entity;
import java.util.Map;
import ognl.ASTProperty;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

public enum AppEngineEntityPropertyAccessor implements PropertyAccessor {
    INSTANCE;

    @Override
    public Object getProperty(final Map context, final Object target, final Object name) throws OgnlException
    {
        final Node node = currentNodeOf(context, name);
        final Entity entity = (Entity) target;
        final String access = name.toString();

        if (hasIndexedAccess(node)) {
            switch (access) {
                case "appId":
                    return entity.getAppId();
                case "key":
                    return entity.getKey();
                case "kind":
                    return entity.getKind();
                case "namespace":
                    return entity.getNamespace();
                case "parent":
                    return entity.getParent();
            }
        }
        final Object property = entity.getProperty(access);
        return property;
    }

    @Override
    public void setProperty(final Map context, final Object target, final Object name, final Object value)
    {
        throw new UnsupportedOperationException("properties should not be setted on Entities.");
    }

    @Override
    public String getSourceAccessor(final OgnlContext context, final Object target, final Object index)
    {
        final Node node = currentNodeOf(context, index);
        final String indexStr = index.toString();
        context.setCurrentAccessor(Entity.class);
        context.setCurrentType(Object.class);
        if ((index instanceof String) && !hasIndexedAccess(node)) {
            final String key = (indexStr.indexOf('"') >= 0 ? indexStr.replaceAll("\"", "") : indexStr);
            switch (key) {
                case "appId":
                    return ".getAppId()";
                case "key":
                    return ".getKey()";
                case "kind":
                    return ".getKind()";
                case "namespace":
                    return ".getNamespace()";
                case "parent":
                    return ".getParent()";
            }
        }
        return ".getProperty(" + indexStr + ')';
    }

    @Override
    public String getSourceSetter(final OgnlContext context, final Object target, final Object index)
    {
        throw new UnsupportedOperationException("properties should not be setted on Entities.");
    }

    private Node currentNodeOf(final Map context, final Object name) throws IllegalStateException
    {
        final OgnlContext ognlCtx = (OgnlContext) context;
        final Node currentNode = ognlCtx.getCurrentNode().jjtGetParent();
        if (currentNode == null) {
            throw new IllegalStateException("node is null for '" + name + '\'');
        }
        if (currentNode instanceof ASTProperty) {
            return currentNode;
        } else {
            return currentNode.jjtGetParent();
        }
    }

    private boolean hasIndexedAccess(final Node node)
    {
        if (node instanceof ASTProperty) {
            final ASTProperty astProperty = (ASTProperty) node;
            return astProperty.isIndexedAccess();
        }
        return false;
    }
}
