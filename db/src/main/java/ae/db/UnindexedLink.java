package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Link;
import com.google.common.collect.ImmutableList;

public final class UnindexedLink extends ScalarField.Unindexed<Link> implements LinkField {

        private static final long serialVersionUID = -6096604411666239752L;

        public UnindexedLink(final String canonicalName,
                             final String description,
                             final String property,
                             final String field,
                             final boolean required,
                             final JsonStringNode jsonName,
                             final String jsonPath,
                             final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      LinkJsonSerializer.INSTANCE,
                      constraints);
        }
}
