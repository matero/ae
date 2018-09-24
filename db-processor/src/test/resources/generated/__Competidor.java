package processor.test;

import static ae.db.DSL.*;

import ae.db.Attr;
import ae.db.ChildWithId;
import ae.db.DateJsonSerializer;
import ae.db.Field;
import ae.db.IndexedDate;
import ae.db.IndexedEmail;
import ae.db.IndexedKeyList;
import ae.db.IndexedPhoneNumber;
import ae.db.IndexedString;
import ae.db.UnindexedText;
import ae.db.Validation;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableList;
import java.util.Date;
import java.util.List;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Generated(
    value = "ae-db",
    date = "2017-02-23"
)
abstract class __Competidor extends ChildWithId {
  protected static final Logger LOGGER = LoggerFactory.getLogger("processor.test.Competidor");

  public static final Competidor m = new Competidor();

  final Id personId = new Id(canonicalName("processor.test.Competidor.personId"), description("Person Id"), fieldName("personId"), jsonName("personId"), jsonPath("personId"), noConstraints);

  final Parent<Competencia> competencia = new Parent(Competencia.m, canonicalName("processor.test.Competidor.competencia"), description("Competencia"), fieldName("competencia"), nullable, jsonName("competencia"), jsonPath("competencia"), noConstraints);

  final IndexedString nombreVisible = new IndexedString(canonicalName("processor.test.Competidor.nombreVisible"), description("Nombre Visible"), propertyName("nombreVisible"), fieldName("nombreVisible"), nullable, jsonName("nombreVisible"), jsonPath("nombreVisible"), constraints(ae.db.NotBlankConstraint.ForString.INSTANCE));

  final IndexedString nombres = new IndexedString(canonicalName("processor.test.Competidor.nombres"), description("Nombres"), propertyName("nombres"), fieldName("nombres"), required, jsonName("nombres"), jsonPath("nombres"), constraints(ae.db.NotBlankConstraint.ForString.INSTANCE));

  final IndexedString apellidos = new IndexedString(canonicalName("processor.test.Competidor.apellidos"), description("Apellidos"), propertyName("apellidos"), fieldName("apellidos"), required, jsonName("apellidos"), jsonPath("apellidos"), constraints(ae.db.NotBlankConstraint.ForString.INSTANCE));

  final IndexedString prefijo = new IndexedString(canonicalName("processor.test.Competidor.prefijo"), description("Prefijo"), propertyName("prefijo"), fieldName("prefijo"), nullable, jsonName("prefijo"), jsonPath("prefijo"), noConstraints);

  final IndexedString sufijo = new IndexedString(canonicalName("processor.test.Competidor.sufijo"), description("Sufijo"), propertyName("sufijo"), fieldName("sufijo"), nullable, jsonName("sufijo"), jsonPath("sufijo"), noConstraints);

  final IndexedString apodo = new IndexedString(canonicalName("processor.test.Competidor.apodo"), description("Apodo"), propertyName("apodo"), fieldName("apodo"), nullable, jsonName("apodo"), jsonPath("apodo"), constraints(ae.db.NotBlankConstraint.ForString.INSTANCE));

  final IndexedDate nacimiento = new IndexedDate(canonicalName("processor.test.Competidor.nacimiento"), description("Nacimiento"), propertyName("nacimiento"), fieldName("nacimiento"), required, jsonName("nacimiento"), jsonPath("nacimiento"), new DateJsonSerializer("yyyy-MM-dd"), noConstraints);

  final IndexedString sexo = new IndexedString(canonicalName("processor.test.Competidor.sexo"), description("Sexo"), propertyName("sexo"), fieldName("sexo"), required, jsonName("sexo"), jsonPath("sexo"), noConstraints);

  final IndexedPhoneNumber telefonoPersonal = new IndexedPhoneNumber(canonicalName("processor.test.Competidor.telefonoPersonal"), description("Telefono"), propertyName("fono"), fieldName("telefonoPersonal"), nullable, jsonName("telefonoPersonal"), jsonPath("telefonoPersonal"), noConstraints);

  final IndexedPhoneNumber telefonoEmergencias = new IndexedPhoneNumber(canonicalName("processor.test.Competidor.telefonoEmergencias"), description("Telefono de Emergencia"), propertyName("emergencia"), fieldName("telefonoEmergencias"), nullable, jsonName("telefonoEmergencias"), jsonPath("telefonoEmergencias"), noConstraints);

  final IndexedEmail email = new IndexedEmail(canonicalName("processor.test.Competidor.email"), description("Email"), propertyName("email"), fieldName("email"), required, jsonName("email"), jsonPath("email"), noConstraints);

  final IndexedEmail emailEmergencias = new IndexedEmail(canonicalName("processor.test.Competidor.emailEmergencias"), description("Email Emergencias"), propertyName("emailEmergencias"), fieldName("emailEmergencias"), nullable, jsonName("emailEmergencias"), jsonPath("emailEmergencias"), noConstraints);

  final UnindexedText info = new UnindexedText(canonicalName("processor.test.Competidor.info"), description("Info"), propertyName("info"), fieldName("info"), nullable, jsonName("info"), jsonPath("info"), constraints(ae.db.NotBlankConstraint.ForText.INSTANCE));

  final IndexedKeyList participaciones = new IndexedKeyList(canonicalName("processor.test.Competidor.participaciones"), description("Participaciones"), propertyName("participaciones"), fieldName("participaciones"), nullable, jsonName("participaciones"), jsonPath("participaciones"), noConstraints);

  private final ImmutableList<Attr> _attrs = ImmutableList.of(personId, competencia, nombreVisible, nombres, apellidos, prefijo, sufijo, apodo, nacimiento, sexo, telefonoPersonal, telefonoEmergencias, email, emailEmergencias, info, participaciones);

  private final ImmutableList<Field<?>> _fields = ImmutableList.of(nombreVisible, nombres, apellidos, prefijo, sufijo, apodo, nacimiento, sexo, telefonoPersonal, telefonoEmergencias, email, emailEmergencias, info, participaciones);

  __Competidor() {
    super("competidores");
  }

  @Override
  protected final Logger logger() {
    return LOGGER;
  }

  @Override
  public final Id modelIdentifier() {
    return personId;
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
    return JsonNodeFactories.object(ImmutableList.of(personId.makeJsonFieldFrom(data),nombreVisible.makeJsonFieldFrom(data),nombres.makeJsonFieldFrom(data),apellidos.makeJsonFieldFrom(data),prefijo.makeJsonFieldFrom(data),sufijo.makeJsonFieldFrom(data),apodo.makeJsonFieldFrom(data),nacimiento.makeJsonFieldFrom(data),sexo.makeJsonFieldFrom(data),telefonoPersonal.makeJsonFieldFrom(data),telefonoEmergencias.makeJsonFieldFrom(data),email.makeJsonFieldFrom(data),emailEmergencias.makeJsonFieldFrom(data),info.makeJsonFieldFrom(data),participaciones.makeJsonFieldFrom(data)));
  }

  @Override
  public final void updatePropertiesWithJsonContents(final Entity data, final JsonNode json) {
    nombreVisible.write(data, json);
    nombres.write(data, json);
    apellidos.write(data, json);
    prefijo.write(data, json);
    sufijo.write(data, json);
    apodo.write(data, json);
    nacimiento.write(data, json);
    sexo.write(data, json);
    telefonoPersonal.write(data, json);
    telefonoEmergencias.write(data, json);
    email.write(data, json);
    emailEmergencias.write(data, json);
    info.write(data, json);
    participaciones.write(data, json);
  }

  @Override
  protected final void doValidate(final Entity data, final Validation validation) {
    nombreVisible.validate(data, validation);
    nombres.validate(data, validation);
    apellidos.validate(data, validation);
    apodo.validate(data, validation);
    nacimiento.validate(data, validation);
    sexo.validate(data, validation);
    telefonoPersonal.validate(data, validation);
    telefonoEmergencias.validate(data, validation);
    email.validate(data, validation);
    emailEmergencias.validate(data, validation);
    info.validate(data, validation);
  }

  @Override
  public final Parent modelParent() {
    return competencia;
  }

  final long personId(final Entity data) {
    return personId.read(data);
  }

  final long personId(final Key key) {
    return personId.read(key);
  }

  final Key competencia(final Entity data) {
    return competencia.read(data);
  }

  final Key competencia(final Key key) {
    return competencia.read(key);
  }

  final String nombreVisible(final Entity data) {
    return nombreVisible.read(data);
  }

  final void nombreVisible(final Entity data, final String newValue) {
    nombreVisible.write(data, newValue);
  }

  final String nombres(final Entity data) {
    return nombres.read(data);
  }

  final void nombres(final Entity data, final String newValue) {
    nombres.write(data, newValue);
  }

  final String apellidos(final Entity data) {
    return apellidos.read(data);
  }

  final void apellidos(final Entity data, final String newValue) {
    apellidos.write(data, newValue);
  }

  final String prefijo(final Entity data) {
    return prefijo.read(data);
  }

  final void prefijo(final Entity data, final String newValue) {
    prefijo.write(data, newValue);
  }

  final String sufijo(final Entity data) {
    return sufijo.read(data);
  }

  final void sufijo(final Entity data, final String newValue) {
    sufijo.write(data, newValue);
  }

  final String apodo(final Entity data) {
    return apodo.read(data);
  }

  final void apodo(final Entity data, final String newValue) {
    apodo.write(data, newValue);
  }

  final Date nacimiento(final Entity data) {
    return nacimiento.read(data);
  }

  final void nacimiento(final Entity data, final Date newValue) {
    nacimiento.write(data, newValue);
  }

  final String sexo(final Entity data) {
    return sexo.read(data);
  }

  final void sexo(final Entity data, final String newValue) {
    sexo.write(data, newValue);
  }

  final PhoneNumber telefonoPersonal(final Entity data) {
    return telefonoPersonal.read(data);
  }

  final void telefonoPersonal(final Entity data, final PhoneNumber newValue) {
    telefonoPersonal.write(data, newValue);
  }

  final PhoneNumber telefonoEmergencias(final Entity data) {
    return telefonoEmergencias.read(data);
  }

  final void telefonoEmergencias(final Entity data, final PhoneNumber newValue) {
    telefonoEmergencias.write(data, newValue);
  }

  final Email email(final Entity data) {
    return email.read(data);
  }

  final void email(final Entity data, final Email newValue) {
    email.write(data, newValue);
  }

  final Email emailEmergencias(final Entity data) {
    return emailEmergencias.read(data);
  }

  final void emailEmergencias(final Entity data, final Email newValue) {
    emailEmergencias.write(data, newValue);
  }

  final Text info(final Entity data) {
    return info.read(data);
  }

  final void info(final Entity data, final Text newValue) {
    info.write(data, newValue);
  }

  final List<Key> participaciones(final Entity data) {
    return participaciones.read(data);
  }

  final void participaciones(final Entity data, final List<Key> newValue) {
    participaciones.write(data, newValue);
  }

  final Competidor.Builder with(final Entity parent) {
    return new Builder(make(parent));
  }

  final Competidor.Builder with(final Key parentKey) {
    return new Builder(make(parentKey));
  }

  final Competidor.Builder with(final Entity parent, final long personId) {
    return new Builder(make(parent, personId));
  }

  final Competidor.Builder with(final Key parentKey, final long personId) {
    return new Builder(make(parentKey, personId));
  }

  public Competidor.Wrapper wrap(final Entity data) {
    return new Wrapper(data);
  }

  public final class Builder {
    final Entity entity;

    Builder(final Entity entity) {
      this.entity = entity;
    }

    Entity build() {
      return entity;
    }

    final Competidor.Builder nombreVisible(final String value) {
      nombreVisible.write(this.entity, value);
      return this;
    }

    final Competidor.Builder nombres(final String value) {
      nombres.write(this.entity, value);
      return this;
    }

    final Competidor.Builder apellidos(final String value) {
      apellidos.write(this.entity, value);
      return this;
    }

    final Competidor.Builder prefijo(final String value) {
      prefijo.write(this.entity, value);
      return this;
    }

    final Competidor.Builder sufijo(final String value) {
      sufijo.write(this.entity, value);
      return this;
    }

    final Competidor.Builder apodo(final String value) {
      apodo.write(this.entity, value);
      return this;
    }

    final Competidor.Builder nacimiento(final Date value) {
      nacimiento.write(this.entity, value);
      return this;
    }

    final Competidor.Builder sexo(final String value) {
      sexo.write(this.entity, value);
      return this;
    }

    final Competidor.Builder telefonoPersonal(final PhoneNumber value) {
      telefonoPersonal.write(this.entity, value);
      return this;
    }

    final Competidor.Builder telefonoEmergencias(final PhoneNumber value) {
      telefonoEmergencias.write(this.entity, value);
      return this;
    }

    final Competidor.Builder email(final Email value) {
      email.write(this.entity, value);
      return this;
    }

    final Competidor.Builder emailEmergencias(final Email value) {
      emailEmergencias.write(this.entity, value);
      return this;
    }

    final Competidor.Builder info(final Text value) {
      info.write(this.entity, value);
      return this;
    }

    final Competidor.Builder participaciones(final List<Key> value) {
      participaciones.write(this.entity, value);
      return this;
    }
  }

  public final class Wrapper {
    public final Entity entity;

    Wrapper(final Entity entity) {
      this.entity = entity;
    }

    final String nombreVisible() {
      return nombreVisible.read(this.entity);
    }

    final Competidor.Wrapper nombreVisible(final String value) {
      nombreVisible.write(this.entity, value);
      return this;
    }

    final String nombres() {
      return nombres.read(this.entity);
    }

    final Competidor.Wrapper nombres(final String value) {
      nombres.write(this.entity, value);
      return this;
    }

    final String apellidos() {
      return apellidos.read(this.entity);
    }

    final Competidor.Wrapper apellidos(final String value) {
      apellidos.write(this.entity, value);
      return this;
    }

    final String prefijo() {
      return prefijo.read(this.entity);
    }

    final Competidor.Wrapper prefijo(final String value) {
      prefijo.write(this.entity, value);
      return this;
    }

    final String sufijo() {
      return sufijo.read(this.entity);
    }

    final Competidor.Wrapper sufijo(final String value) {
      sufijo.write(this.entity, value);
      return this;
    }

    final String apodo() {
      return apodo.read(this.entity);
    }

    final Competidor.Wrapper apodo(final String value) {
      apodo.write(this.entity, value);
      return this;
    }

    final Date nacimiento() {
      return nacimiento.read(this.entity);
    }

    final Competidor.Wrapper nacimiento(final Date value) {
      nacimiento.write(this.entity, value);
      return this;
    }

    final String sexo() {
      return sexo.read(this.entity);
    }

    final Competidor.Wrapper sexo(final String value) {
      sexo.write(this.entity, value);
      return this;
    }

    final PhoneNumber telefonoPersonal() {
      return telefonoPersonal.read(this.entity);
    }

    final Competidor.Wrapper telefonoPersonal(final PhoneNumber value) {
      telefonoPersonal.write(this.entity, value);
      return this;
    }

    final PhoneNumber telefonoEmergencias() {
      return telefonoEmergencias.read(this.entity);
    }

    final Competidor.Wrapper telefonoEmergencias(final PhoneNumber value) {
      telefonoEmergencias.write(this.entity, value);
      return this;
    }

    final Email email() {
      return email.read(this.entity);
    }

    final Competidor.Wrapper email(final Email value) {
      email.write(this.entity, value);
      return this;
    }

    final Email emailEmergencias() {
      return emailEmergencias.read(this.entity);
    }

    final Competidor.Wrapper emailEmergencias(final Email value) {
      emailEmergencias.write(this.entity, value);
      return this;
    }

    final Text info() {
      return info.read(this.entity);
    }

    final Competidor.Wrapper info(final Text value) {
      info.write(this.entity, value);
      return this;
    }

    final List<Key> participaciones() {
      return participaciones.read(this.entity);
    }

    final Competidor.Wrapper participaciones(final List<Key> value) {
      participaciones.write(this.entity, value);
      return this;
    }
  }
}
