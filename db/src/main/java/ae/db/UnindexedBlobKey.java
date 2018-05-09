package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class UnindexedBlobKey extends ScalarField.Unindexed<BlobKey> implements BlobKeyField {
  public UnindexedBlobKey(final @NonNull String canonicalName,
                          final @NonNull String description,
                          final @NonNull String property,
                          final @NonNull String field,
                          final boolean required,
                          final @NonNull JsonStringNode jsonName,
                          final @NonNull String jsonPath,
                          final @NonNull JsonSerializer<BlobKey> jsonSerializer,
                          final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer, constraints);
  }
}