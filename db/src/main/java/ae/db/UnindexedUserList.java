package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;

public final class UnindexedUserList extends ListField.Unindexed<User> implements UserListField {

        private static final long serialVersionUID = 5280406182600610375L;

        public UnindexedUserList(final String canonicalName,
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
                      constraints);
        }
}
