package biblioteca;

import biblioteca.persistence.Database;
import biblioteca.service.*;
import biblioteca.ui.BibliotecaController;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "biblioteca.capturas", matches = "true")
class CapturasTest {
  @Test
  void capturarPantallas() throws Exception {
    Path tmp = Files.createTempDirectory("biblioteca-capturas");
    System.setProperty("biblioteca.logDir", tmp.resolve("logs").toString());
    try (Database db = new Database(tmp.resolve("demo.db").toString())) {
      BibliotecaService s = new BibliotecaService(db);
      DatosDemostracion.cargar(s);
      CountDownLatch started = new CountDownLatch(1);
      Platform.startup(started::countDown);
      started.await(15, TimeUnit.SECONDS);
      CompletableFuture<Void> done = new CompletableFuture<>();
      Platform.runLater(
          () -> {
            try {
              Stage stage = new Stage();
              BibliotecaController c = new BibliotecaController(s, stage);
              Scene scene = c.crear();
              stage.setScene(scene);
              BibliotecaApplication.configurarVentana(stage);
              stage.show();
              Assertions.assertTrue(stage.isMaximized());
              Assertions.assertEquals(6, stage.getIcons().size());
              Assertions.assertFalse(stage.isFullScreen());

              Path out = Path.of("docs/capturas");
              Files.createDirectories(out);
              Runnable[] paginas = {
                c::inicio,
                c::libros,
                c::estudiantes,
                () -> c.prestamos(false),
                () -> c.prestamos(true),
                c::reportes
              };
              String[] nombres = {
                "01_inicio",
                "02_libros",
                "03_estudiantes",
                "04_prestamos",
                "05_devoluciones",
                "06_reportes"
              };
              for (int i = 0; i < paginas.length; i++) {
                paginas[i].run();
                scene.getRoot().setOpacity(1);
                ((javafx.scene.layout.BorderPane) scene.getRoot()).getCenter().setOpacity(1);
                scene.getRoot().applyCss();
                scene.getRoot().layout();
                ImageIO.write(
                    SwingFXUtils.fromFXImage(scene.snapshot(null), null),
                    "png",
                    out.resolve(nombres[i] + ".png").toFile());
              }
              javafx.scene.control.Alert aviso = biblioteca.ui.Dialogos.confirmacion(stage, "¿Cerrar Henry System Library?");
              aviso.show();
              aviso.getDialogPane().applyCss(); aviso.getDialogPane().layout();
              ImageIO.write(SwingFXUtils.fromFXImage(aviso.getDialogPane().snapshot(null,null),null), "png", out.resolve("07_confirmacion.png").toFile());
              aviso.close();
              Platform.runLater(() -> {
                try {
                  for (javafx.stage.Window w : new java.util.ArrayList<>(javafx.stage.Window.getWindows())) {
                    if (w != stage && w.isShowing()) {
                      javafx.scene.Parent pane = w.getScene().getRoot();
                      pane.applyCss(); pane.layout();
                      ImageIO.write(SwingFXUtils.fromFXImage(pane.snapshot(null,null),null), "png", out.resolve("08_formulario.png").toFile());
                      w.hide();
                    }
                  }
                } catch(Exception ex) { done.completeExceptionally(ex); }
              });
              java.lang.reflect.Method form = BibliotecaController.class.getDeclaredMethod("formLibro", biblioteca.model.Libro.class);
              form.setAccessible(true); form.invoke(c, new Object[]{null});
              stage.close();
              done.complete(null);
            } catch (Throwable e) {
              done.completeExceptionally(e);
            }
          });
      done.get(60, TimeUnit.SECONDS);
      Platform.exit();
    } finally {
      System.clearProperty("biblioteca.logDir");
    }
  }
}
