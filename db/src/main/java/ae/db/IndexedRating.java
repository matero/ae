package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Rating;
import com.google.common.collect.ImmutableList;

public final class IndexedRating extends ScalarField.Indexed<Rating> implements RatingField {

        private static final long serialVersionUID = -5627937621013357958L;

        public IndexedRating(final String canonicalName,
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
                      new PropertyProjection(property, Rating.class), constraints);
        }
}
