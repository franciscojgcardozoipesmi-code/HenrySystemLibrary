package biblioteca;

import biblioteca.persistence.Database;
import biblioteca.service.BibliotecaService;
import biblioteca.ui.BibliotecaController;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BibliotecaApplication extends Application {
  private Database db;

  @Override
  public void start(Stage stage) {
    try {
      db = new Database();
      BibliotecaController controller = new BibliotecaController(new BibliotecaService(db), stage);
      stage.setTitle("Henry System Library · Gestión de Biblioteca y Préstamos");
      stage.setScene(controller.crear());
      configurarVentana(stage);
      stage.show();
      stage.setMaximized(true);
    } catch (Exception e) {
      e.printStackTrace();
      biblioteca.ui.Dialogos.aviso(stage, "No se pudo iniciar el sistema.\n" + e.getMessage());
      javafx.application.Platform.exit();
    }
  }

  public static void configurarVentana(Stage stage) {
    javafx.geometry.Rectangle2D area = javafx.stage.Screen.getPrimary().getVisualBounds();
    double ancho = Math.min(1220, area.getWidth() * 0.90);
    double alto = Math.min(740, area.getHeight() * 0.88);
    if (stage.getIcons().isEmpty()) {
      for (int size : new int[]{16,32,48,64,128,256}) {
        stage.getIcons().add(new javafx.scene.image.Image(
          BibliotecaApplication.class.getResourceAsStream("/imagenes/logo-" + size + ".png")));
      }
    }
    stage.setFullScreen(false);
    stage.setMaximized(true);
    stage.setWidth(ancho);
    stage.setHeight(alto);
    stage.setMinWidth(Math.min(1020, ancho));
    stage.setMinHeight(Math.min(600, alto));
  }

  @Override
  public void stop() {
    if (db != null) db.close();
  }

}
