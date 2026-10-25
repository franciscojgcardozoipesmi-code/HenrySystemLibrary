package biblioteca.service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public final class Logger {
  private static final Logger INSTANCE = new Logger();

  private Logger() {}

  public static Logger getInstance() {
    return INSTANCE;
  }

  public synchronized void registrar(String evento) {
    try {
      Path dir = Path.of(System.getProperty("biblioteca.logDir", "logs"));
      Files.createDirectories(dir);
      Files.writeString(
          dir.resolve("biblioteca.log"),
          LocalDateTime.now() + " | " + evento.replaceAll("[\r\n]", " ") + System.lineSeparator(),
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.err.println("No se pudo escribir el registro: " + e.getMessage());
    }
  }
}
