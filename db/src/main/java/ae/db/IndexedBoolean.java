package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.PropertyProjection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IndexedBoolean extends ScalarField.Indexed<Boolean> implements BooleanField, BooleanField.Filter {
  private final @NonNull FilterPredicate isTrue;
  private final @NonNull FilterPredicate isFalse;

  public IndexedBoolean(final @NonNull String canonicalName,
                        final @NonNull String description,
                        final @NonNull String property,
                        final @NonNull String field,
                        final @NonNull boolean required,
                        final @NonNull JsonStringNode jsonName,
                        final @NonNull String jsonPath,
                        final @Nullable Constraint... constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, BooleanJsonSerializer.INSTANCE,
          new PropertyProjection(property, Boolean.class), constraints);
    this.isTrue = new FilterPredicate(property, FilterOperator.EQUAL, Boolean.TRUE);
    this.isFalse = new FilterPredicate(property, FilterOperator.EQUAL, Boolean.FALSE);
  }

  @Override public @NonNull FilterPredicate isTrue() {
    return isTrue;
  }

  @Override public @NonNull FilterPredicate isFalse() {
    return isFalse;
  }
}
