package biblioteca;

import biblioteca.persistence.Database;
import biblioteca.service.BibliotecaService;
import java.time.LocalDate;

/** Carga opcional de ejemplos en una base vacía; nunca reinicia una base existente. */
public final class DatosDemostracion {
  private DatosDemostracion() {}
  public static void main(String[] args) {
    try (Database db = new Database()) { cargar(new BibliotecaService(db)); }
  }
  public static void cargar(BibliotecaService s) {
    if (!s.libros().isEmpty() || !s.estudiantes().isEmpty() || !s.prestamos().isEmpty())
      throw new IllegalStateException("La demostración solo se carga en una base vacía.");
    String[][] libros = {
      {"9780307474728","Cien años de soledad","Gabriel García Márquez","Novela"},
      {"9788422616337","El principito","Antoine de Saint-Exupéry","Literatura"},
      {"9780134685991","Effective Java","Joshua Bloch","Informática"},
      {"9788497592208","1984","George Orwell","Novela"},
      {"9788491050296","Don Quijote de la Mancha","Miguel de Cervantes","Literatura"},
      {"9780132350884","Clean Code","Robert C. Martin","Informática"},
      {"9789500720321","Ficciones","Jorge Luis Borges","Cuentos"},
      {"9789500391057","Rayuela","Julio Cortázar","Novela"}
    };
    for(String[] l:libros) s.guardarLibro(null,l[0],l[1],l[2],l[3]);
    String[][] estudiantes = {
      {"40111222","Ana Gómez","ana@example.com"},
      {"41222333","Bruno López","bruno@example.com"},
      {"42333444","Carla Pérez","carla@example.com"},
      {"43444555","Diego Fernández","diego@example.com"},
      {"44555666","Elena Rodríguez","elena@example.com"}
    };
    for(String[] e:estudiantes)s.guardarEstudiante(null,e[0],e[1],e[2]);
    LocalDate hoy=LocalDate.now();
    prestar(s,libros[0][0],estudiantes[0][0],hoy.minusDays(4),hoy.plusDays(2));
    prestar(s,libros[1][0],estudiantes[1][0],hoy.minusDays(10),hoy.minusDays(3));
    prestar(s,libros[2][0],estudiantes[2][0],hoy.minusDays(8),hoy.minusDays(2));
    s.devolver(s.prestamos().get(0).id,hoy);
    prestar(s,libros[3][0],estudiantes[3][0],hoy.minusDays(12),hoy.minusDays(4));
    Long pagado=s.prestamos().get(0).id;s.devolver(pagado,hoy);s.pagar(pagado);
    prestar(s,libros[4][0],estudiantes[4][0],hoy,hoy);
    s.devolver(s.prestamos().get(0).id,hoy);
    prestar(s,libros[5][0],estudiantes[4][0],hoy,hoy.plusDays(10));
  }
  private static void prestar(BibliotecaService s,String isbn,String dni,LocalDate inicio,LocalDate fin){
    Long libro=s.libros().stream().filter(l->l.isbn.equals(isbn)).findFirst().orElseThrow().id;
    Long estudiante=s.estudiantes().stream().filter(e->e.dni.equals(dni)).findFirst().orElseThrow().id;
    s.guardarPrestamo(null,libro,estudiante,inicio,fin);
  }
}
