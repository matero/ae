package ae.db.processor;

import ae.Record;
import ae.model;

@model(kind = "provincias") class Provincia extends __Provincia
{
  private static final class R extends Record
  {
    @id String codigo;
    @indexed String nombre;
    String capital;
  }
}