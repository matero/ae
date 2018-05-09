package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class IndexedBlobKeyList extends ListField.Indexed<BlobKey> implements BlobKeyListField {
  public IndexedBlobKeyList(final @NonNull String canonicalName,
                            final @NonNull String description,
                            final @NonNull String property,
                            final @NonNull String field,
                            final boolean required,
                            final @NonNull JsonStringNode jsonName,
                            final @NonNull String jsonPath,
                            final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, BlobKeyJsonSerializer.ARRAY, new PropertyProjection(property, BlobKey.class), constraints);
  }
}