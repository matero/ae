package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedDouble extends ScalarField.Unindexed<Double> implements DoubleField {

        private static final long serialVersionUID = 3484752651863970802L;

        public UnindexedDouble(final String canonicalName,
                               final String description,
                               final String property,
                               final String field,
                               final boolean required,
                               final JsonStringNode jsonName,
                               final String jsonPath,
                               final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      DoubleJsonSerializer.INSTANCE,
                      constraints);
        }
}
