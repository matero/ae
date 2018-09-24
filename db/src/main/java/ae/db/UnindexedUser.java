package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;

public final class UnindexedUser extends ScalarField.Unindexed<User> implements UserField {

        private static final long serialVersionUID = 6534274621850378942L;

        public UnindexedUser(final String canonicalName,
                             final String description,
                             final String property,
                             final String field,
                             final boolean required,
                             final JsonStringNode jsonName,
                             final String jsonPath,
                             final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      UserJsonSerializer.INSTANCE,
                      constraints);
        }
}
