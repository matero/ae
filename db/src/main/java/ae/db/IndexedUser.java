package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.users.User;
import com.google.common.collect.ImmutableList;

public final class IndexedUser extends ScalarField.Indexed<User> implements UserField {

  private static final long serialVersionUID = 2419449018659318423L;

  public IndexedUser(final String canonicalName,
                     final String property,
                     final String field,
                     final boolean required,
                     final JsonStringNode jsonName,
                     final String jsonPath,
                     final ImmutableList<Constraint> constraints)
  {
    super(canonicalName, property, field, required, jsonName, jsonPath,
          UserJsonSerializer.INSTANCE,
          new PropertyProjection(property, User.class), constraints);
  }
}
