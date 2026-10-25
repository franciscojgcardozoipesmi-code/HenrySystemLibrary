package biblioteca.exception;

public class LibroNoDisponibleException extends RuntimeException {
  public LibroNoDisponibleException(String mensaje) {
    super(mensaje);
  }
}
