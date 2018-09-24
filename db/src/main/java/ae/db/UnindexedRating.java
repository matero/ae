package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Rating;
import com.google.common.collect.ImmutableList;

public final class UnindexedRating extends ScalarField.Unindexed<Rating> implements RatingField {

        private static final long serialVersionUID = -3187799006489398677L;

        public UnindexedRating(final String canonicalName,
                               final String description,
                               final String property,
                               final String field,
                               final boolean required,
                               final JsonStringNode jsonName,
                               final String jsonPath,
                               final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      RatingJsonSerializer.INSTANCE,
                      constraints);
        }
}
