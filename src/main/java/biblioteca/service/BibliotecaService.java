package biblioteca.service;

import biblioteca.exception.*;
import biblioteca.model.*;
import biblioteca.persistence.Database;
import java.time.LocalDate;
import java.util.*;

public class BibliotecaService {
  private final Database db;
  private final HashMap<String, Libro> catalogo = new HashMap<>();
  private final ArrayList<Prestamo> historial = new ArrayList<>();

  public BibliotecaService(Database db) {
    this.db = db;
    actualizar();
  }

  public void actualizar() {
    db.tx(
        em -> {
          catalogo.clear();
          for (Libro l :
              em.createQuery("SELECT l FROM Libro l ORDER BY l.titulo", Libro.class)
                  .getResultList()) catalogo.put(l.isbn, l);
          historial.clear();
          historial.addAll(
              em.createQuery(
                      "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.estudiante ORDER BY"
                          + " p.id DESC",
                      Prestamo.class)
                  .getResultList());
          return null;
        });
  }

  public List<Libro> libros() {
    return catalogo.values().stream().sorted(Comparator.comparing(l -> l.titulo)).toList();
  }

  public List<Prestamo> prestamos() {
    return List.copyOf(historial);
  }

  public List<Estudiante> estudiantes() {
    return db.tx(
        em ->
            em.createQuery("SELECT e FROM Estudiante e ORDER BY e.nombre", Estudiante.class)
                .getResultList());
  }

  public static String requerido(String s, String campo) {
    if (s == null || s.isBlank()) throw new IllegalArgumentException("Complete " + campo + ".");
    return s.trim();
  }

  public void guardarLibro(Long id, String isbn, String titulo, String autor, String categoria) {
    String code = requerido(isbn, "ISBN").replace("-", "").replace(" ", "").toUpperCase();
    if (!code.matches("(?:[0-9]{9}[0-9X]|[0-9]{13})"))
      throw new IllegalArgumentException(
          "El ISBN debe tener 10 o 13 caracteres numéricos (X final permitido en ISBN-10).");
    String t = requerido(titulo, "título"),
        a = requerido(autor, "autor"),
        c = requerido(categoria, "categoría");
    db.tx(
        em -> {
          if (!em.createQuery(
                  "SELECT l FROM Libro l WHERE l.isbn=:isbn AND (:id IS NULL OR l.id<>:id)",
                  Libro.class)
              .setParameter("isbn", code)
              .setParameter("id", id)
              .getResultList()
              .isEmpty()) throw new IllegalArgumentException("Ya existe ese ISBN.");
          Libro l = id == null ? new Libro() : em.find(Libro.class, id);
          if (l == null) throw new IllegalArgumentException("Libro inexistente.");
          l.isbn = code;
          l.titulo = t;
          l.autor = a;
          l.categoria = c;
          if (id == null) em.persist(l);
          return null;
        });
    actualizar();
  }

  public void eliminarLibro(Long id) {
    db.tx(
        em -> {
          if (em.createQuery("SELECT COUNT(p) FROM Prestamo p WHERE p.libro.id=:id", Long.class)
                  .setParameter("id", id)
                  .getSingleResult()
              > 0)
            throw new IllegalArgumentException(
                "El libro tiene historial de préstamos y debe conservarse.");
          em.remove(em.find(Libro.class, id));
          return null;
        });
    actualizar();
  }

  public void guardarEstudiante(Long id, String dni, String nombre, String email) {
    String d = requerido(dni, "DNI"),
        n = requerido(nombre, "nombre"),
        e = email == null ? "" : email.trim();
    if (!d.matches("[0-9]{7,9}"))
      throw new IllegalArgumentException("DNI: ingrese entre 7 y 9 números.");
    if (!n.matches("[\\p{L} .'-]+"))
      throw new IllegalArgumentException(
          "El nombre solo admite letras, espacios, apóstrofe, punto y guion.");
    if (!e.isEmpty() && !e.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Correo electrónico inválido.");
    db.tx(
        em -> {
          if (!em.createQuery(
                  "SELECT e FROM Estudiante e WHERE e.dni=:d AND (:id IS NULL OR e.id<>:id)",
                  Estudiante.class)
              .setParameter("d", d)
              .setParameter("id", id)
              .getResultList()
              .isEmpty()) throw new IllegalArgumentException("Ya existe ese DNI.");
          Estudiante s = id == null ? new Estudiante() : em.find(Estudiante.class, id);
          s.dni = d;
          s.nombre = n;
          s.email = e;
          if (id == null) em.persist(s);
          return null;
        });
    actualizar();
  }

  public void eliminarEstudiante(Long id) {
    db.tx(
        em -> {
          if (em.createQuery(
                      "SELECT COUNT(p) FROM Prestamo p WHERE p.estudiante.id=:id", Long.class)
                  .setParameter("id", id)
                  .getSingleResult()
              > 0)
            throw new IllegalArgumentException("El estudiante tiene historial de préstamos.");
          em.remove(em.find(Estudiante.class, id));
          return null;
        });
    actualizar();
  }

  private void verificarDeuda(jakarta.persistence.EntityManager em, Long estudiante, Long excluir) {
    for (Prestamo p :
        em.createQuery("SELECT p FROM Prestamo p WHERE p.estudiante.id=:e", Prestamo.class)
            .setParameter("e", estudiante)
            .getResultList()) {
      if (Objects.equals(p.id, excluir)) continue;
      if (p.devolucion != null && p.multaCentavos > 0 && !p.pagada)
        throw new MultaPendienteException(
            "El estudiante tiene una multa pendiente. Registre el pago antes de un nuevo"
                + " préstamo.");
      if (p.devolucion == null && p.vencimiento.isBefore(LocalDate.now()))
        throw new PrestamoVencidoException(
            "El estudiante tiene un préstamo vencido. Debe devolverlo antes de solicitar otro.");
    }
  }

  public void guardarPrestamo(
      Long id, Long libro, Long estudiante, LocalDate inicio, LocalDate vence) {
    if (libro == null || estudiante == null || inicio == null || vence == null)
      throw new IllegalArgumentException("Complete todos los campos obligatorios.");
    if (inicio.isAfter(LocalDate.now()) || vence.isBefore(inicio))
      throw new IllegalArgumentException(
          "El inicio no puede ser futuro y el vencimiento no puede ser anterior al inicio.");
    db.tx(
        em -> {
          Prestamo p = id == null ? new Prestamo() : em.find(Prestamo.class, id);
          if (p == null) throw new IllegalArgumentException("Préstamo inexistente.");
          if (p.devolucion != null)
            throw new IllegalArgumentException("Un préstamo devuelto no puede editarse.");
          verificarDeuda(em, estudiante, id);
          Libro l = em.find(Libro.class, libro);
          Estudiante e = em.find(Estudiante.class, estudiante);
          if (l == null || e == null)
            throw new IllegalArgumentException("Seleccione libro y estudiante existentes.");
          if (!l.disponible && (p.libro == null || !p.libro.id.equals(libro)))
            throw new LibroNoDisponibleException("El libro seleccionado ya está prestado.");
          if (p.libro != null && !p.libro.id.equals(libro)) p.libro.disponible = true;
          p.libro = l;
          p.estudiante = e;
          p.inicio = inicio;
          p.vencimiento = vence;
          l.disponible = false;
          if (id == null) em.persist(p);
          return null;
        });
    Logger.getInstance()
        .registrar(
            (id == null ? "PRÉSTAMO CREADO" : "PRÉSTAMO MODIFICADO")
                + " libro="
                + libro
                + " estudiante="
                + estudiante);
    actualizar();
  }

  public void devolver(Long id, LocalDate fecha) {
    db.tx(
        em -> {
          Prestamo p = em.find(Prestamo.class, id);
          if (p == null || p.devolucion != null)
            throw new IllegalArgumentException("Seleccione un préstamo activo.");
          if (fecha == null || fecha.isBefore(p.inicio) || fecha.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La devolución debe estar entre el inicio y hoy.");
          p.devolucion = fecha;
          p.multaCentavos = Multas.calcular(p.vencimiento, fecha);
          p.pagada = p.multaCentavos == 0;
          p.libro.disponible = true;
          return null;
        });
    Logger.getInstance().registrar("DEVOLUCIÓN préstamo=" + id + " fecha=" + fecha);
    actualizar();
  }

  public void pagar(Long id) {
    db.tx(
        em -> {
          Prestamo p = em.find(Prestamo.class, id);
          if (p == null || p.devolucion == null || p.pagada || p.multaCentavos == 0)
            throw new IllegalArgumentException("Seleccione una devolución con multa pendiente.");
          p.pagada = true;
          return null;
        });
    Logger.getInstance().registrar("PAGO MULTA préstamo=" + id);
    actualizar();
  }

  public void eliminarPrestamo(Long id) {
    db.tx(
        em -> {
          Prestamo p = em.find(Prestamo.class, id);
          if (p == null) throw new IllegalArgumentException("Préstamo inexistente.");
          if (p.devolucion != null)
            throw new IllegalArgumentException("Las devoluciones se conservan como historial.");
          p.libro.disponible = true;
          em.remove(p);
          return null;
        });
    Logger.getInstance().registrar("PRÉSTAMO ELIMINADO id=" + id);
    actualizar();
  }

  public long deuda(Prestamo p) {
    return p.devolucion == null
        ? Multas.calcular(p.vencimiento, LocalDate.now())
        : p.pagada ? 0 : p.multaCentavos;
  }
}
