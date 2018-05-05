package ae.db;

import argo.jdom.JsonStringNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class UnindexedString extends ScalarField.Unindexed<String> implements StringField {
  public UnindexedString(final @NonNull String canonicalName,
                         final @NonNull String description,
                         final @NonNull String property,
                         final @NonNull String field,
                         final boolean required,
                         final @NonNull JsonStringNode jsonName,
                         final @NonNull String jsonPath,
                         final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, StringJsonSerializer.INSTANCE, constraints);
  }
}
