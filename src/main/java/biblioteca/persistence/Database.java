package biblioteca.persistence;

import jakarta.persistence.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

public final class Database implements AutoCloseable {
  private final EntityManagerFactory factory;

  public Database() {
    this("data/biblioteca.db");
  }

  public Database(String archivo) {
    try {
      Path f = Path.of(archivo).toAbsolutePath();
      Files.createDirectories(f.getParent());
      Class.forName("org.sqlite.JDBC");
      try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + f);
          Statement st = c.createStatement()) {
        st.execute("PRAGMA foreign_keys=ON");
        String sql =
            new String(
                Objects.requireNonNull(getClass().getResourceAsStream("/schema.sql"))
                    .readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8);
        for (String s : sql.split(";")) if (!s.isBlank()) st.execute(s);
      }
      factory =
          Persistence.createEntityManagerFactory(
              "biblioteca",
              Map.of(
                  "jakarta.persistence.jdbc.url",
                  "jdbc:sqlite:" + f + "?foreign_keys=on&busy_timeout=5000"));
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo abrir la base de datos: " + e.getMessage(), e);
    }
  }

  public synchronized <T> T tx(Function<EntityManager, T> action) {
    EntityManager em = factory.createEntityManager();
    try {
      em.getTransaction().begin();
      T r = action.apply(em);
      em.getTransaction().commit();
      return r;
    } catch (RuntimeException e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  public void close() {
    factory.close();
  }
}
