package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.ImmutableList;

public final class UnindexedBlobKeyList extends ListField.Unindexed<BlobKey> implements BlobKeyListField {

        private static final long serialVersionUID = 4093091897353135013L;

        public UnindexedBlobKeyList(final String canonicalName,
                                    final String description,
                                    final String property,
                                    final String field,
                                    final boolean required,
                                    final JsonStringNode jsonName,
                                    final String jsonPath,
                                    final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath,
                      BlobKeyJsonSerializer.ARRAY,
                      constraints);
        }
}
