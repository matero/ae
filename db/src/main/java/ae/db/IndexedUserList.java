package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;

public final class IndexedUserList extends ListField.Indexed<User> implements UserListField {

        private static final long serialVersionUID = -268265786711305388L;

        public IndexedUserList(final String canonicalName,
                               final String description,
                               final String property,
                               final String field,
                               final boolean required,
                               final JsonStringNode jsonName,
                               final String jsonPath,
                               final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      UserJsonSerializer.ARRAY,
                      new PropertyProjection(property, User.class), constraints);
        }
}
