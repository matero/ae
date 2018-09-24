package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.collect.ImmutableList;

public final class UnindexedShortBlobList extends ListField.Unindexed<ShortBlob> implements ShortBlobListField {

        private static final long serialVersionUID = -1184152658102688463L;

        public UnindexedShortBlobList(final String canonicalName,
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
                      constraints);
        }
}
