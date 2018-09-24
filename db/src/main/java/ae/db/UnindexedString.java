package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedString extends ScalarField.Unindexed<String> implements StringField {

        private static final long serialVersionUID = -7890466161914195419L;

        public UnindexedString(final String canonicalName,
                               final String description,
                               final String property,
                               final String field,
                               final boolean required,
                               final JsonStringNode jsonName,
                               final String jsonPath,
                               final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      StringJsonSerializer.INSTANCE,
                      constraints);
        }
}
