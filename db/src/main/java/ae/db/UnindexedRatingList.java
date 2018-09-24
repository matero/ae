package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Rating;
import com.google.common.collect.ImmutableList;

public final class UnindexedRatingList extends ListField.Unindexed<Rating> implements RatingListField {

        private static final long serialVersionUID = -632009788479183267L;

        public UnindexedRatingList(final String canonicalName,
                                   final String description,
                                   final String property,
                                   final String field,
                                   final boolean required,
                                   final JsonStringNode jsonName,
                                   final String jsonPath,
                                   final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      RatingJsonSerializer.ARRAY,
                      constraints);
        }
}
