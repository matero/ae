package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedBooleanList extends ListField.Unindexed<Boolean> implements BooleanListField {

        private static final long serialVersionUID = 2662937905239497982L;

        public UnindexedBooleanList(final String canonicalName,
                                    final String description,
                                    final String property,
                                    final String field,
                                    boolean required,
                                    final JsonStringNode jsonName,
                                    final String jsonPath,
                                    final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      BooleanJsonSerializer.ARRAY,
                      constraints);
        }
}
