package biblioteca.exception;

public class PrestamoVencidoException extends RuntimeException {
  public PrestamoVencidoException(String mensaje) {
    super(mensaje);
  }
}
