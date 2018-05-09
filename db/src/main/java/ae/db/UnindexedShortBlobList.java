package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class UnindexedShortBlobList extends ListField.Unindexed<ShortBlob> implements ShortBlobListField {
  public UnindexedShortBlobList(final @NonNull String canonicalName,
                                final @NonNull String description,
                                final @NonNull String property,
                                final @NonNull String field,
                                final boolean required,
                                final @NonNull JsonStringNode jsonName,
                                final @NonNull String jsonPath,
                                final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, ShortBlobJsonSerializer.ARRAY, constraints);
  }
}