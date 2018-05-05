package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Rating;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class IndexedRating extends ScalarField.Indexed<Rating> implements RatingField {
  public IndexedRating(final @NonNull String canonicalName,
                       final @NonNull String description,
                       final @NonNull String property,
                       final @NonNull String field,
                       final boolean required,
                       final @NonNull JsonStringNode jsonName,
                       final @NonNull String jsonPath,
                       final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, RatingJsonSerializer.INSTANCE,
          new PropertyProjection(property, Rating.class), constraints);
  }
}