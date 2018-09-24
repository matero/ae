package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.common.collect.ImmutableList;

public final class UnindexedPostalAddressList extends ListField.Unindexed<PostalAddress> implements
        PostalAddressListField {

        private static final long serialVersionUID = 4957669042348341820L;

        public UnindexedPostalAddressList(final String canonicalName,
                                          final String description,
                                          final String property,
                                          final String field,
                                          final boolean required,
                                          final JsonStringNode jsonName,
                                          final String jsonPath,
                                          final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      PostalAddressJsonSerializer.ARRAY, constraints);
        }
}
