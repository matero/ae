package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedLongList extends ListField.Unindexed<Long> implements LongListField {

        private static final long serialVersionUID = 6224773401050990886L;

        public UnindexedLongList(final String canonicalName,
                                 final String description,
                                 final String property,
                                 final String field,
                                 final boolean required,
                                 final JsonStringNode jsonName,
                                 final String jsonPath,
                                 final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      LongJsonSerializer.ARRAY,
                      constraints);
        }
}
