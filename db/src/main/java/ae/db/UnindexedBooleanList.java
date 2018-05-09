package ae.db;

import argo.jdom.JsonStringNode;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class UnindexedBooleanList extends ListField.Unindexed<Boolean> implements BooleanListField {
  public UnindexedBooleanList(final @NonNull String canonicalName,
                              final @NonNull String description,
                              final @NonNull String property,
                              final @NonNull String field,
                              boolean required,
                              final @NonNull JsonStringNode jsonName,
                              final @NonNull String jsonPath,
                        final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, BooleanJsonSerializer.ARRAY, constraints);
  }
}