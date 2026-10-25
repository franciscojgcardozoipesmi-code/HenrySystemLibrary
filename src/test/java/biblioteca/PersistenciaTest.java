package biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import biblioteca.exception.*;
import biblioteca.model.*;
import biblioteca.persistence.Database;
import biblioteca.report.*;
import biblioteca.service.*;
import java.nio.file.*;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class PersistenciaTest {
  @TempDir Path temp;
  Database db;
  BibliotecaService s;

  @BeforeEach
  void init() {
    System.setProperty("biblioteca.logDir", temp.resolve("logs").toString());
    db = new Database(temp.resolve("test.db").toString());
    s = new BibliotecaService(db);
    s.guardarLibro(
        null, "9780307474728", "Cien años de soledad", "Gabriel García Márquez", "Novela");
    s.guardarLibro(null, "9780134685991", "Effective Java", "Joshua Bloch", "Informática");
    s.guardarEstudiante(null, "40111222", "Ana Gómez", "ana@example.com");
  }

  @AfterEach
  void close() {
    db.close();
    System.clearProperty("biblioteca.logDir");
  }

  Long libro() {
    return s.libros().get(0).id;
  }

  Long estudiante() {
    return s.estudiantes().get(0).id;
  }

  @Test
  void crudYReapertura() {
    Long id = libro();
    s.guardarLibro(id, "9780307474728", "Título editado", "Autor", "Novela");
    db.close();
    db = new Database(temp.resolve("test.db").toString());
    s = new BibliotecaService(db);
    assertTrue(s.libros().stream().anyMatch(l -> l.titulo.equals("Título editado")));
    s.eliminarLibro(id);
    assertEquals(1, s.libros().size());
  }

  @Test
  void prestamoMismoDia() {
    LocalDate h = LocalDate.now();
    Long id = libro();
    s.guardarPrestamo(null, id, estudiante(), h, h);
    assertFalse(
        s.libros().stream().filter(l -> l.id.equals(id)).findFirst().orElseThrow().disponible);
    s.devolver(s.prestamos().get(0).id, h);
    assertEquals(0, s.prestamos().get(0).multaCentavos);
    assertTrue(
        s.libros().stream().filter(l -> l.id.equals(id)).findFirst().orElseThrow().disponible);
  }

  @Test
  void noPrestarDosVeces() {
    LocalDate h = LocalDate.now();
    s.guardarPrestamo(null, libro(), estudiante(), h, h.plusDays(7));
    assertThrows(
        LibroNoDisponibleException.class,
        () -> s.guardarPrestamo(null, libro(), estudiante(), h, h.plusDays(7)));
  }

  @Test
  void vencimientoYMultaPendiente() {
    LocalDate h = LocalDate.now();
    Long uno = libro(), otro = s.libros().get(1).id;
    s.guardarPrestamo(null, uno, estudiante(), h.minusDays(5), h.minusDays(2));
    assertThrows(
        PrestamoVencidoException.class,
        () -> s.guardarPrestamo(null, otro, estudiante(), h, h.plusDays(7)));
    Long id = s.prestamos().get(0).id;
    s.devolver(id, h);
    assertEquals(20000, s.prestamos().get(0).multaCentavos);
    assertThrows(
        MultaPendienteException.class,
        () -> s.guardarPrestamo(null, otro, estudiante(), h, h.plusDays(7)));
    s.pagar(id);
    s.guardarPrestamo(null, otro, estudiante(), h, h.plusDays(7));
    assertEquals(2, s.prestamos().size());
  }

  @Test
  void editarYCambiarLibro() {
    LocalDate h = LocalDate.now();
    Long primero = libro(), segundo = s.libros().get(1).id;
    s.guardarPrestamo(null, primero, estudiante(), h, h.plusDays(7));
    Long id = s.prestamos().get(0).id;
    s.guardarPrestamo(id, segundo, estudiante(), h, h.plusDays(10));
    assertTrue(
        s.libros().stream().filter(l -> l.id.equals(primero)).findFirst().orElseThrow().disponible);
    s.eliminarPrestamo(id);
    assertTrue(s.libros().stream().allMatch(l -> l.disponible));
  }

  @Test
  void validarDatos() {
    assertThrows(IllegalArgumentException.class, () -> s.guardarLibro(null, "abc", "A", "B", "C"));
    assertThrows(IllegalArgumentException.class, () -> s.guardarEstudiante(null, "abc", "Ana", ""));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            s.guardarPrestamo(
                null, libro(), estudiante(), LocalDate.now(), LocalDate.now().minusDays(1)));
  }

  @Test
  void conservarHistorial() {
    LocalDate h = LocalDate.now();
    Long l = libro();
    s.guardarPrestamo(null, l, estudiante(), h, h);
    Long id = s.prestamos().get(0).id;
    s.devolver(id, h);
    assertThrows(IllegalArgumentException.class, () -> s.eliminarLibro(l));
    assertThrows(IllegalArgumentException.class, () -> s.eliminarEstudiante(estudiante()));
    assertThrows(IllegalArgumentException.class, () -> s.eliminarPrestamo(id));
  }

  @Test
  void duplicados() {
    assertThrows(
        IllegalArgumentException.class,
        () -> s.guardarLibro(null, "9780307474728", "Otro", "Otro", "Otro"));
    assertThrows(
        IllegalArgumentException.class, () -> s.guardarEstudiante(null, "40111222", "Otro", ""));
  }

  @Test
  void reportes() {
    String inventario = ReporteFactory.crear(ReporteFactory.Tipo.INVENTARIO).generar(s);
    assertTrue(inventario.contains("Cien años"));
    assertTrue(
        ReporteFactory.crear(ReporteFactory.Tipo.ESTADISTICAS).generar(s).contains("Libros;2"));
    assertEquals(1, ReporteFactory.crear(ReporteFactory.Tipo.MOROSOS).generar(s).lines().count());
  }
}
