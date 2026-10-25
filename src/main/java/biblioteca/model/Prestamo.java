package biblioteca.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
public class Prestamo {
  @Id
  @Column(columnDefinition = "INTEGER")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "libro_id")
  public Libro libro;

  @ManyToOne(optional = false)
  @JoinColumn(name = "estudiante_id")
  public Estudiante estudiante;

  @Column(nullable = false)
  public LocalDate inicio;

  @Column(nullable = false)
  public LocalDate vencimiento;

  public LocalDate devolucion;

  @Column(nullable = false)
  public long multaCentavos;

  @Column(nullable = false)
  public boolean pagada;

  public Prestamo() {}

  public String estado() {
    return devolucion != null
        ? "Devuelto"
        : vencimiento.isBefore(LocalDate.now()) ? "Vencido" : "Activo";
  }
}
