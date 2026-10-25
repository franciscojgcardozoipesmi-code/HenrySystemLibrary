package biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
  @Id
  @Column(columnDefinition = "INTEGER")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, unique = true)
  public String isbn;

  @Column(nullable = false)
  public String titulo;

  @Column(nullable = false)
  public String autor;

  @Column(nullable = false)
  public String categoria;

  @Column(nullable = false)
  public boolean disponible = true;

  public Libro() {}

  public Libro(String isbn, String titulo, String autor, String categoria) {
    this.isbn = isbn;
    this.titulo = titulo;
    this.autor = autor;
    this.categoria = categoria;
  }

  public String toString() {
    return titulo + " · " + isbn;
  }
}
