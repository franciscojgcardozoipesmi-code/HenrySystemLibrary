package biblioteca.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

/** Ventanas modales con la misma identidad visual que la aplicación. */
public final class Dialogos {
  private Dialogos() {}

  public static Alert confirmacion(Window owner, String mensaje) {
    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
    a.getButtonTypes().setAll(new ButtonType("Sí", ButtonBar.ButtonData.YES),
        new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE));
    a.setContentText(mensaje);
    aplicar(a, owner, "Confirmar acción");
    return a;
  }

  public static boolean confirmar(Window owner, String mensaje) {
    return confirmacion(owner, mensaje).showAndWait()
        .map(b -> b.getButtonData() == ButtonBar.ButtonData.YES).orElse(false);
  }

  public static void aviso(Window owner, String mensaje) {
    Alert a = new Alert(Alert.AlertType.WARNING);
    a.getButtonTypes().setAll(new ButtonType("Entendido", ButtonBar.ButtonData.OK_DONE));
    a.setContentText(mensaje == null ? "No se pudo completar la operación." : mensaje);
    aplicar(a, owner, "Revisá los datos");
    a.showAndWait();
  }

  public static void aplicar(Dialog<?> d, Window owner, String titulo) {
    if (owner != null) d.initOwner(owner);
    d.initStyle(StageStyle.TRANSPARENT);
    d.setTitle("Henry System Library · " + titulo);
    DialogPane pane = d.getDialogPane();
    pane.getStylesheets().add(Dialogos.class.getResource("/style.css").toExternalForm());
    pane.getStyleClass().add("library-dialog");
    pane.setGraphic(null);
    Label marca = new Label("H / LIBRARY");
    marca.getStyleClass().add("dialog-brand");
    Label title = new Label(titulo);
    title.getStyleClass().add("dialog-title");
    VBox textos = new VBox(5, marca, title);
    Region espacio = new Region(); HBox.setHgrow(espacio, Priority.ALWAYS);
    Button cerrar = new Button("×"); cerrar.getStyleClass().add("dialog-close");
    cerrar.setOnAction(e -> d.close());
    HBox header = new HBox(15, textos, espacio, cerrar);
    header.getStyleClass().add("dialog-header");
    header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    pane.setHeader(header);
    double[] drag = new double[2];
    header.setOnMousePressed(e -> {drag[0]=e.getSceneX();drag[1]=e.getSceneY();});
    header.setOnMouseDragged(e -> {Window w=pane.getScene().getWindow();w.setX(e.getScreenX()-drag[0]);w.setY(e.getScreenY()-drag[1]);});
    for (ButtonType b : pane.getButtonTypes()) {
      pane.lookupButton(b).getStyleClass().add(
          b.getButtonData()==ButtonBar.ButtonData.YES || b.getButtonData()==ButtonBar.ButtonData.OK_DONE ? "primary" : "secondary");
    }
    d.setOnShown(e -> {pane.getScene().setFill(Color.TRANSPARENT);});
  }
}
