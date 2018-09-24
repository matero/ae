package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.collect.ImmutableList;

public final class UnindexedShortBlob extends ScalarField.Unindexed<ShortBlob> implements ShortBlobField {

        private static final long serialVersionUID = 7918653512683914794L;

        public UnindexedShortBlob(final String canonicalName,
                                  final String description,
                                  final String property,
                                  final String field,
                                  final boolean required,
                                  final JsonStringNode jsonName,
                                  final String jsonPath,
                                  final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      ShortBlobJsonSerializer.INSTANCE, constraints);
        }
}
