package processor.test;

import java.lang.UnsupportedOperationException;
import java.util.logging.Logger;
import javax.annotation.Generated;

@Generated(
    value = "ae-db",
    date = "2017-02-23"
)
public final class m {
  private static final Logger LOG = Logger.getLogger("processor.test.m");

  static final Competencia Competencia;

  public static final Competidor Competidor;

  static {
    Competencia = new Competencia();
    LOG.finest("processor.test.m.Competencia defined");
  }
  static {
    Competidor = new Competidor();
    LOG.finest("processor.test.m.Competidor defined");
  }

  private m() {
    new UnsupportedOperationException("m can't be instantiated.");
  }
}