package ae.db.processor;

import static ae.db.DSL.*;

import ae.db.Attribute;
import ae.db.Field;
import ae.db.IndexedString;
import ae.db.RootWithName;
import ae.db.UnindexedString;
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
abstract class __Provincia extends RootWithName {
  protected static final Logger LOGGER = LoggerFactory.getLogger("ae.db.processor.Provincia");

  static final Provincia m = new Provincia();

  final Name codigo = new Name(canonicalName("ae.db.processor.Provincia.codigo"), fieldName("codigo"), jsonName("codigo"), jsonPath("codigo"), noConstraints);

  final IndexedString nombre = new IndexedString(canonicalName("ae.db.processor.Provincia.nombre"), propertyName("nombre"), fieldName("nombre"), nullable, jsonName("nombre"), jsonPath("nombre"), noConstraints);

  final UnindexedString capital = new UnindexedString(canonicalName("ae.db.processor.Provincia.capital"), propertyName("capital"), fieldName("capital"), nullable, jsonName("capital"), jsonPath("capital"), noConstraints);

  private final ImmutableList<Attribute> _attrs = ImmutableList.of(codigo, nombre, capital);

  private final ImmutableList<Field<?>> _fields = ImmutableList.of(nombre, capital);

  __Provincia() {
  }

  @Override
  protected final Logger logger() {
    return LOGGER;
  }

  @Override
  public final String kind() {
    return "provincias";
  }

  @Override
  public final Name modelIdentifier() {
    return codigo;
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
    return JsonNodeFactories.object(ImmutableList.of(codigo.makeJsonFieldFrom(data),nombre.makeJsonFieldFrom(data),capital.makeJsonFieldFrom(data)));
  }

  @Override
  public final void updatePropertiesWithJsonContents(final Entity data, final JsonNode json) {
    nombre.write(data, json);
    capital.write(data, json);
  }

  @Override
  protected final void doValidate(final Entity data, final Validation validation) {
    codigo.validate(data, validation);
  }

  final String codigo(final Entity data) {
    return codigo.read(data);
  }

  final String codigo(final Key key) {
    return codigo.read(key);
  }

  final String nombre(final Entity data) {
    return nombre.read(data);
  }

  final void nombre(final Entity data, final String newValue) {
    nombre.write(data, newValue);
  }

  final String capital(final Entity data) {
    return capital.read(data);
  }

  final void capital(final Entity data, final String newValue) {
    capital.write(data, newValue);
  }

  final Provincia.Builder with(final String codigo) {
    return new Builder(make(codigo));
  }

  Provincia.Wrapper wrap(final Entity data) {
    return new Wrapper(data);
  }

  final class Builder {
    final Entity entity;

    Builder(final Entity entity) {
      this.entity = entity;
    }

    final Entity build() {
      return entity;
    }

    final Provincia.Builder nombre(final String value) {
      nombre.write(this.entity, value);
      return this;
    }

    final Provincia.Builder capital(final String value) {
      capital.write(this.entity, value);
      return this;
    }
  }

  final class Wrapper {
    public final Entity entity;

    Wrapper(final Entity entity) {
      this.entity = entity;
    }

    final String nombre() {
      return nombre.read(this.entity);
    }

    final Provincia.Wrapper nombre(final String value) {
      nombre.write(this.entity, value);
      return this;
    }

    final String capital() {
      return capital.read(this.entity);
    }

    final Provincia.Wrapper capital(final String value) {
      capital.write(this.entity, value);
      return this;
    }
  }
}
