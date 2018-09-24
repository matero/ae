package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.common.collect.ImmutableList;

public final class IndexedString extends ScalarField.Indexed<String> implements StringField {

        private static final long serialVersionUID = -5623708141725608545L;

        public IndexedString(final String canonicalName,
                             final String description,
                             final String property,
                             final String field,
                             final boolean required,
                             final JsonStringNode jsonName,
                             final String jsonPath,
                             final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      StringJsonSerializer.INSTANCE,
                      new PropertyProjection(property, String.class), constraints);
        }
}
