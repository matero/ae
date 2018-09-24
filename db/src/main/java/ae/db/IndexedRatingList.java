package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Rating;
import com.google.common.collect.ImmutableList;

public final class IndexedRatingList extends ListField.Indexed<Rating> implements RatingListField {

        private static final long serialVersionUID = 8457052140483648895L;

        public IndexedRatingList(final String canonicalName,
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
                      new PropertyProjection(property, Rating.class), constraints);
        }
}
