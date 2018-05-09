package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Rating;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class IndexedRating extends ScalarField.Indexed<Rating> implements RatingField {
  public IndexedRating(final @NonNull String canonicalName,
                       final @NonNull String description,
                       final @NonNull String property,
                       final @NonNull String field,
                       final boolean required,
                       final @NonNull JsonStringNode jsonName,
                       final @NonNull String jsonPath,
                       final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, RatingJsonSerializer.INSTANCE,
          new PropertyProjection(property, Rating.class), constraints);
  }
}
