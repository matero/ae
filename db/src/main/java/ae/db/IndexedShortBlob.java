package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.collect.ImmutableList;

public final class IndexedShortBlob extends ScalarField.Indexed<ShortBlob> implements ShortBlobField {

  private static final long serialVersionUID = -4231951895598821573L;

  public IndexedShortBlob(final String canonicalName,
                          final String property,
                          final String field,
                          final boolean required,
                          final JsonStringNode jsonName,
                          final String jsonPath,
                          final ImmutableList<Constraint> constraints)
  {
    super(canonicalName, property, field, required, jsonName, jsonPath,
          ShortBlobJsonSerializer.INSTANCE,
          new PropertyProjection(property, ShortBlob.class), constraints);
  }
}
