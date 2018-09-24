package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedDoubleList extends ListField.Unindexed<Double> implements DoubleListField {

        private static final long serialVersionUID = -2934730715702867169L;

        public UnindexedDoubleList(final String canonicalName,
                                   final String description,
                                   final String property,
                                   final String field,
                                   final boolean required,
                                   final JsonStringNode jsonName,
                                   final String jsonPath,
                                   final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      DoubleJsonSerializer.ARRAY,
                      constraints);
        }
}
