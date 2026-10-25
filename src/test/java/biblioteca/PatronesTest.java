package biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import biblioteca.report.ReporteFactory;
import biblioteca.service.Logger;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class PatronesTest {
  @TempDir Path temp;

  @Test
  void singleton() {
    assertSame(Logger.getInstance(), Logger.getInstance());
  }

  @Test
  void loggerEscribeSinSobrescribir() throws Exception {
    System.setProperty("biblioteca.logDir", temp.toString());
    try {
      Logger.getInstance().registrar("PRÉSTAMO 1");
      Logger.getInstance().registrar("DEVOLUCIÓN 1");
      String s = Files.readString(temp.resolve("biblioteca.log"));
      assertTrue(s.contains("PRÉSTAMO 1"));
      assertTrue(s.contains("DEVOLUCIÓN 1"));
      assertEquals(2, s.lines().count());
    } finally {
      System.clearProperty("biblioteca.logDir");
    }
  }

  @Test
  void factoryCreaTiposDistintos() {
    assertInstanceOf(
        ReporteFactory.Morosos.class, ReporteFactory.crear(ReporteFactory.Tipo.MOROSOS));
    assertInstanceOf(
        ReporteFactory.Inventario.class, ReporteFactory.crear(ReporteFactory.Tipo.INVENTARIO));
    assertInstanceOf(
        ReporteFactory.Estadisticas.class, ReporteFactory.crear(ReporteFactory.Tipo.ESTADISTICAS));
  }

  @Test
  void factoryRechazaNulo() {
    assertThrows(IllegalArgumentException.class, () -> ReporteFactory.crear(null));
  }
}
