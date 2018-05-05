package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PropertyProjection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class IndexedLink extends ScalarField.Indexed<Link> implements LinkField {
  public IndexedLink(final @NonNull String canonicalName,
                     final @NonNull String description,
                     final @NonNull String property,
                     final @NonNull String field,
                     final boolean required,
                     final @NonNull JsonStringNode jsonName,
                     final @NonNull String jsonPath,
                     final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, LinkJsonSerializer.INSTANCE, new PropertyProjection(property, Link.class), constraints);
  }
}