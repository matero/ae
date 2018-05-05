package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class IndexedDoubleList extends ListField.Indexed<Double> implements DoubleListField {
  public IndexedDoubleList(final @NonNull String canonicalName,
                           final @NonNull String description,
                           final @NonNull String property,
                           final @NonNull String field,
                           final boolean required,
                           final @NonNull JsonStringNode jsonName,
                           final @NonNull String jsonPath,
                           final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, DoubleJsonSerializer.ARRAY, new PropertyProjection(property, Double.class), constraints);
  }
}