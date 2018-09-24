package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.datastore.Link;
import com.google.common.collect.ImmutableList;

public final class UnindexedLinkList extends ListField.Unindexed<Link> implements LinkListField {

        private static final long serialVersionUID = 2538938018604689357L;

        public UnindexedLinkList(final String canonicalName,
                                 final String description,
                                 final String property,
                                 final String field,
                                 final boolean required,
                                 final JsonStringNode jsonName,
                                 final String jsonPath,
                                 final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      LinkJsonSerializer.ARRAY,
                      constraints);
        }
}
