package biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estudiantes")
public class Estudiante {
  @Id
  @Column(columnDefinition = "INTEGER")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, unique = true)
  public String dni;

  @Column(nullable = false)
  public String nombre;

  public String email;

  public Estudiante() {}

  public Estudiante(String dni, String nombre, String email) {
    this.dni = dni;
    this.nombre = nombre;
    this.email = email;
  }

  public String toString() {
    return nombre + " · DNI " + dni;
  }
}
