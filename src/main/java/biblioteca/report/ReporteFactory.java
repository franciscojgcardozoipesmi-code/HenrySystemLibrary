package biblioteca.report;

import biblioteca.model.*;
import biblioteca.service.*;

public final class ReporteFactory {
  public enum Tipo {
    MOROSOS,
    INVENTARIO,
    ESTADISTICAS
  }

  private ReporteFactory() {}

  public static Reporte crear(Tipo tipo) {
    if (tipo == null) throw new IllegalArgumentException("Seleccione el tipo de reporte.");
    return switch (tipo) {
      case MOROSOS -> new Morosos();
      case INVENTARIO -> new Inventario();
      case ESTADISTICAS -> new Estadisticas();
    };
  }

  static String csv(String s) {
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }

  public static class Morosos implements Reporte {
    public String generar(BibliotecaService s) {
      StringBuilder b = new StringBuilder("Préstamo;Estudiante;DNI;Libro;Estado;Deuda ARS\n");
      for (Prestamo p : s.prestamos())
        if (s.deuda(p) > 0)
          b.append(p.id)
              .append(';')
              .append(csv(p.estudiante.nombre))
              .append(';')
              .append(p.estudiante.dni)
              .append(';')
              .append(csv(p.libro.titulo))
              .append(';')
              .append(p.estado())
              .append(';')
              .append(String.format(java.util.Locale.ROOT, "%.2f", s.deuda(p) / 100.0))
              .append('\n');
      return b.toString();
    }
  }

  public static class Inventario implements Reporte {
    public String generar(BibliotecaService s) {
      StringBuilder b = new StringBuilder("ISBN;Título;Autor;Categoría;Disponible\n");
      for (Libro l : s.libros())
        b.append(l.isbn)
            .append(';')
            .append(csv(l.titulo))
            .append(';')
            .append(csv(l.autor))
            .append(';')
            .append(csv(l.categoria))
            .append(';')
            .append(l.disponible ? "Sí" : "No")
            .append('\n');
      return b.toString();
    }
  }

  public static class Estadisticas implements Reporte {
    public String generar(BibliotecaService s) {
      return "Indicador;Valor\nLibros;"
          + s.libros().size()
          + "\nDisponibles;"
          + s.libros().stream().filter(l -> l.disponible).count()
          + "\nEstudiantes;"
          + s.estudiantes().size()
          + "\nPréstamos históricos;"
          + s.prestamos().size()
          + "\nActivos;"
          + s.prestamos().stream().filter(p -> p.devolucion == null).count()
          + "\nVencidos;"
          + s.prestamos().stream().filter(p -> p.estado().equals("Vencido")).count()
          + "\nDeuda total ARS;"
          + String.format(
              java.util.Locale.ROOT,
              "%.2f",
              s.prestamos().stream().mapToLong(s::deuda).sum() / 100.0)
          + "\n";
    }
  }
}
