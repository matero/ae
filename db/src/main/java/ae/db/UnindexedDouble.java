package ae.db;

import argo.jdom.JsonStringNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class UnindexedDouble extends ScalarField.Unindexed<Double> implements DoubleField {
  public UnindexedDouble(final @NonNull String canonicalName,
                         final @NonNull String description,
                         final @NonNull String property,
                         final @NonNull String field,
                         final boolean required,
                         final @NonNull JsonStringNode jsonName,
                         final @NonNull String jsonPath,
                         final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, DoubleJsonSerializer.INSTANCE, constraints);
  }
}