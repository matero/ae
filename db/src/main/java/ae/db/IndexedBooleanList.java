package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class IndexedBooleanList extends ListField.Indexed<Boolean> implements BooleanListField, BooleanField.Filter {
  private final @NonNull FilterPredicate isTrue;
  private final @NonNull FilterPredicate isFalse;

  public IndexedBooleanList(final @NonNull String canonicalName,
                            final @NonNull String description,
                            final @NonNull String property,
                            final @NonNull String field,
                            final boolean required,
                            final @NonNull JsonStringNode jsonName,
                            final @NonNull String jsonPath,
                        final @NonNull ImmutableList<Constraint> constraints) {
    super(canonicalName, description, property, field, required, jsonName, jsonPath, BooleanJsonSerializer.ARRAY,
          new PropertyProjection(property, Boolean.class), constraints);
    this.isTrue = new FilterPredicate(property, FilterOperator.EQUAL, Boolean.TRUE);
    this.isFalse = new FilterPredicate(property, FilterOperator.EQUAL, Boolean.FALSE);
  }

  @Override public @NonNull FilterPredicate isTrue() { return isTrue; }

  @Override public @NonNull FilterPredicate isFalse() { return isFalse; }
}
