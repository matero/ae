package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.ShortBlob;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class IndexedShortBlobList extends ListField.Indexed<ShortBlob> implements ShortBlobListField {
  public IndexedShortBlobList(final @NonNull String canonicalName,
                              final @NonNull String description,
                              final @NonNull String property,
                              final @NonNull String field,
                              final boolean required,
                              final @NonNull JsonStringNode jsonName,
                              final @NonNull String jsonPath,
                              final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, ShortBlobJsonSerializer.ARRAY,
          new PropertyProjection(property, ShortBlob.class), constraints);
  }
}