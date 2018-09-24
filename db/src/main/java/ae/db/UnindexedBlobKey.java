package ae.db;

import argo.jdom.JsonStringNode;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.ImmutableList;

public final class UnindexedBlobKey extends ScalarField.Unindexed<BlobKey> implements BlobKeyField {

        private static final long serialVersionUID = -7860860052089357932L;

        public UnindexedBlobKey(final String canonicalName,
                                final String description,
                                final String property,
                                final String field,
                                final boolean required,
                                final JsonStringNode jsonName,
                                final String jsonPath,
                                final JsonSerializer<BlobKey> jsonSerializer,
                                final ImmutableList<Constraint> constraints)
        {
                super(canonicalName, description, property, field, required, jsonName, jsonPath, jsonSerializer,
                      constraints);
        }
}
