package processor.test;

import static ae.db.DSL.*;

import ae.db.Attr;
import ae.db.Field;
import ae.db.RootWithName;
import ae.db.Validation;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.common.collect.ImmutableList;
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

  final Name nombre = new Name(canonicalName("processor.test.Competencia.nombre"), description("Nombre"), fieldName("nombre"), jsonName("nombre"), jsonPath("nombre"), noConstraints);

  private final ImmutableList<Attr> _attrs = ImmutableList.of(nombre);

  private final ImmutableList<Field<?>> _fields = ImmutableList.of();

  __Competencia() {
    super("Competencia");
  }

  @Override
  protected final Logger logger() {
    return LOGGER;
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
  public final ImmutableList<Attr> modelAttributes() {
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
}
