package biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import biblioteca.service.Multas;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MultasTest {
  final LocalDate v = LocalDate.of(2026, 10, 10);

  @Test
  void mismoDiaSinMulta() {
    assertEquals(0, Multas.calcular(v, v));
  }

  @Test
  void antesDeVencerSinMulta() {
    assertEquals(0, Multas.calcular(v, v.minusDays(5)));
  }

  @Test
  void unDiaDeAtraso() {
    assertEquals(10000, Multas.calcular(v, v.plusDays(1)));
  }

  @Test
  void diezDias() {
    assertEquals(100000, Multas.calcular(v, v.plusDays(10)));
  }

  @Test
  void cambioDeMes() {
    assertEquals(20000, Multas.calcular(LocalDate.of(2026, 1, 31), LocalDate.of(2026, 2, 2)));
  }

  @Test
  void fechaNula() {
    assertThrows(NullPointerException.class, () -> Multas.calcular(v, null));
  }
}
