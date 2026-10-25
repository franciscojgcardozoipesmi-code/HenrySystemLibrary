package biblioteca.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class Multas {
  public static final long TARIFA_CENTAVOS = 10000; // $100 por día

  private Multas() {}

  public static long calcular(LocalDate vencimiento, LocalDate devolucion) {
    Objects.requireNonNull(vencimiento);
    Objects.requireNonNull(devolucion);
    return Math.multiplyExact(
        Math.max(0, ChronoUnit.DAYS.between(vencimiento, devolucion)), TARIFA_CENTAVOS);
  }

  public static String pesos(long centavos) {
    return String.format(java.util.Locale.forLanguageTag("es-AR"), "$ %,.2f", centavos / 100.0);
  }
}
