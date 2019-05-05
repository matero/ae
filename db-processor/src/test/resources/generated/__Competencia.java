package processor.test;

import static ae.db.DSL.*;

import ae.db.Attribute;
import ae.db.Field;
import ae.db.RootWithName;
import ae.db.Validation;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.Future;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Generated(
    value = "ae-db",
    date = "2017-02-23"
)
abstract class __Competencia extends RootWithName {
  protected static final Logger LOGGER = LoggerFactory.getLogger("processor.test.Competencia");

  static final Competencia m = new Competencia();

  final Name nombre = new Name(canonicalName("processor.test.Competencia.nombre"), fieldName("nombre"), jsonName("nombre"), jsonPath("nombre"), noConstraints);

  private final ImmutableList<Attribute> _attrs = ImmutableList.of(nombre);

  private final ImmutableList<Field<?>> _fields = ImmutableList.of();

  __Competencia() {
  }

  @Override
  protected final Logger logger() {
    return LOGGER;
  }

  @Override
  public final String kind() {
    return "Competencia";
  }

  @Override
  public final Name modelIdentifier() {
    return nombre;
  }

  @Override
  public final ImmutableList<Field<?>> modelFields() {
    return _fields;
  }

  @Override
  public final ImmutableList<Attribute> modelAttributes() {
    return _attrs;
  }

  @Override
  public JsonNode toJson(final Entity data) {
    if (null == data) {
      return JsonNodeFactories.nullNode();
    }
    return JsonNodeFactories.object(ImmutableList.of(nombre.makeJsonFieldFrom(data)));
  }

  @Override
  public final void updatePropertiesWithJsonContents(final Entity data, final JsonNode json) {
  }

  @Override
  protected final void doValidate(final Entity data, final Validation validation) {
    nombre.validate(data, validation);
  }

  final String nombre(final Entity data) {
    return nombre.read(data);
  }

  final String nombre(final Key key) {
    return nombre.read(key);
  }

  @Override
  protected final Future<Key> saveEntity(final Entity data) {
    return asyncDatastore().put(data);
  }

  @Override
  protected final Future<Void> deleteEntity(final Key key) {
    return asyncDatastore().delete(key);
  }

  @Override
  protected final Entity getEntity(final Key key) throws EntityNotFoundException {
    return datastore().get(key);
  }

  @Override
  protected final boolean checkExists(final Key key) {
    final Query.Filter f = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, key);
    final Query exists = makeQuery().setKeysOnly().setFilter(f);
    final Entity data = datastore().prepare(exists).asSingleEntity();
    return data != null;
  }

  @Override
  protected final MemcacheService memcache() {
    return MemcacheServiceFactory.getMemcacheService("otro_namespace");
  }

  @Override
  protected final AsyncMemcacheService asyncMemcache() {
    return MemcacheServiceFactory.getAsyncMemcacheService("otro_namespace");
  }
}
