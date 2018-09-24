package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.common.collect.ImmutableList;

public final class IndexedStringList extends ListField.Indexed<String> implements StringListField {

        private static final long serialVersionUID = -487738674753127117L;

        public IndexedStringList(final String canonicalName,
                                 final String description,
                                 final String property,
                                 final String field,
                                 final boolean required,
                                 final JsonStringNode jsonName,
                                 final String jsonPath,
                                 final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      StringJsonSerializer.ARRAY,
                      new PropertyProjection(property, String.class), constraints);
        }
}
