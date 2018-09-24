package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.common.collect.ImmutableList;

public final class UnindexedPhoneNumberList extends ListField.Unindexed<PhoneNumber> implements PhoneNumberListField {

        private static final long serialVersionUID = -6378104307500847934L;

        public UnindexedPhoneNumberList(final String canonicalName,
                                        final String description,
                                        final String property,
                                        final String field,
                                        final boolean required,
                                        final JsonStringNode jsonName,
                                        final String jsonPath,
                                        final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      PhoneNumberJsonSerializer.ARRAY,
                      constraints);
        }
}
