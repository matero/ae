package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.ImmutableList;

public final class UnindexedGeoPtList extends ListField.Unindexed<GeoPt> implements GeoPtListField {

        private static final long serialVersionUID = -6840370698343983055L;

        public UnindexedGeoPtList(final String canonicalName,
                                  final String description,
                                  final String property,
                                  final String field,
                                  final boolean required,
                                  final JsonStringNode jsonName,
                                  final String jsonPath,
                                  final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      GeoPtJsonSerializer.ARRAY,
                      constraints);
        }
}
