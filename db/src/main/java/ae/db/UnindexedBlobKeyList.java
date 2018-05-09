package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class UnindexedBlobKeyList extends ListField.Unindexed<BlobKey> implements BlobKeyListField {
  public UnindexedBlobKeyList(final @NonNull String canonicalName,
                              final @NonNull String description,
                              final @NonNull String property,
                              final @NonNull String field,
                              final boolean required,
                              final @NonNull JsonStringNode jsonName,
                              final @NonNull String jsonPath,
                              final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, BlobKeyJsonSerializer.ARRAY, constraints);
  }
}