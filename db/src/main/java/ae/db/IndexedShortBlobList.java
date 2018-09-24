package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.collect.ImmutableList;

public final class IndexedShortBlobList extends ListField.Indexed<ShortBlob> implements ShortBlobListField {

        private static final long serialVersionUID = -5571408916142175485L;

        public IndexedShortBlobList(final String canonicalName,
                                    final String description,
                                    final String property,
                                    final String field,
                                    final boolean required,
                                    final JsonStringNode jsonName,
                                    final String jsonPath,
                                    final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      ShortBlobJsonSerializer.ARRAY,
                      new PropertyProjection(property, ShortBlob.class), constraints);
        }
}
