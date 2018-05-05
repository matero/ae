package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Link;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class UnindexedLinkList extends ListField.Unindexed<Link> implements LinkListField {
  public UnindexedLinkList(final @NonNull String canonicalName,
                           final @NonNull String description,
                           final @NonNull String property,
                           final @NonNull String field,
                           final boolean required,
                           final @NonNull JsonStringNode jsonName,
                           final @NonNull String jsonPath,
                           final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, LinkJsonSerializer.ARRAY, constraints);
  }
}