package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedStringList extends ListField.Unindexed<String> implements StringListField {

        private static final long serialVersionUID = -5131120856431383869L;

        public UnindexedStringList(final String canonicalName,
                                   final String description,
                                   final String property,
                                   final String field,
                                   final boolean required,
                                   final JsonStringNode jsonName,
                                   final String jsonPath,
                                   final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      StringJsonSerializer.ARRAY,
                      constraints);
        }
}
