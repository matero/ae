package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;

public final class UnindexedBoolean extends ScalarField.Unindexed<Boolean> implements BooleanField {

  private static final long serialVersionUID = 2631155401980700380L;

  public UnindexedBoolean(final String canonicalName,
                          final String property,
                          final String field,
                          final boolean required,
                          final JsonStringNode jsonName,
                          final String jsonPath,
                          final ImmutableList<Constraint> constraints)
  {
    super(canonicalName, property, field, required, jsonName, jsonPath,
          BooleanJsonSerializer.INSTANCE,
          constraints);
  }
}
