package biblioteca.ui;

import biblioteca.model.*;
import biblioteca.report.*;
import biblioteca.service.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;

public class BibliotecaController {
  private final BibliotecaService service;
  private final Stage stage;
  private final BorderPane root = new BorderPane();
  private final VBox content = new VBox(20);
  private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private String filtroEstado = "Todos";
  private String categoria = "Todas las categorías";

  public BibliotecaController(BibliotecaService s, Stage stage) {
    this.service = s;
    this.stage = stage;
  }

  public Scene crear() {
    VBox sidebar = new VBox(8);
    sidebar.getStyleClass().add("sidebar");
    sidebar.setPrefWidth(235);
    sidebar.setMinWidth(235);
    javafx.scene.shape.SVGPath libro = new javafx.scene.shape.SVGPath();
    libro.setContent(
        "M 0 8 Q 24 0 48 12 Q 72 0 96 8 L 96 66 Q 72 58 48 70 Q 24 58 0 66 Z M 48 12 L 48 70 M 10"
            + " 20 Q 24 16 37 21 M 59 21 Q 73 16 86 20 M 10 32 Q 24 28 37 33 M 59 33 Q 73 28 86"
            + " 32");
    libro.setFill(javafx.scene.paint.Color.TRANSPARENT);
    libro.setStroke(javafx.scene.paint.Color.web("#bd2937"));
    libro.setStrokeWidth(2.4);
    Label logo = new Label("Henry");
    logo.getStyleClass().add("brand");
    Label nombre = new Label("System Library");
    nombre.getStyleClass().add("brand-name");
    VBox marca = new VBox(2, libro, logo, nombre);
    marca.setAlignment(Pos.CENTER);
    marca.getStyleClass().add("logo-area");
    Label heading = new Label("Menú principal");
    heading.getStyleClass().add("menu-heading");
    heading.setMaxWidth(Double.MAX_VALUE);
    sidebar
        .getChildren()
        .addAll(
            marca,
            heading,
            menu("Inicio", this::inicio),
            menu("Libros", this::libros),
            menu("Estudiantes", this::estudiantes),
            menu("Préstamos", () -> prestamos(false)),
            menu("Devoluciones y multas", () -> prestamos(true)),
            menu("Reportes", this::reportes));
    Button salir =
        menu(
            "Salir del sistema",
            () -> {
              if (confirmar("¿Cerrar Henry System Library?")) javafx.application.Platform.exit();
            });
    salir.getStyleClass().add("exit-nav");
    sidebar.getChildren().add(salir);
    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);
    Label pie = new Label("Programación III · IPESMI");
    pie.getStyleClass().add("sidebar-footer");
    sidebar.getChildren().addAll(spacer, pie);
    root.setLeft(sidebar);
    content.getStyleClass().add("workspace");
    root.setCenter(content);
    Scene scene = new Scene(root, 1180, 680);
    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    inicio();
    return scene;
  }

  private HBox barraSuperior() {
    Label contexto = new Label("Biblioteca  |  Gestión de préstamos");
    contexto.getStyleClass().add("session-label");
    Region espacio = new Region();
    HBox.setHgrow(espacio, Priority.ALWAYS);
    Button vencidos =
        button(
            "Vencidos",
            () -> {
              filtroEstado = "Vencido";
              prestamos(false);
            },
            "top-action");
    Button reportes = button("Reportes", this::reportes, "top-action");
    HBox barra = new HBox(12, contexto, espacio, vencidos, reportes);
    barra.setAlignment(Pos.CENTER_LEFT);
    barra.setMinHeight(52);
    return barra;
  }

  private Button menu(String t, Runnable r) {
    Button b = button(t, r, "nav");
    b.setMaxWidth(Double.MAX_VALUE);
    javafx.scene.shape.SVGPath icono = new javafx.scene.shape.SVGPath();
    icono.setContent(
        switch (t) {
          case "Inicio" ->
              "M 1 8 L 8 1 L 15 8 M 3 7 L 3 16 L 13 16 L 13 7 M 6 16 L 6 10 L 10 10 L 10 16";
          case "Libros" -> "M 1 2 L 7 4 L 7 16 L 1 14 Z M 9 4 L 15 2 L 15 14 L 9 16 Z";
          case "Estudiantes" ->
              "M 8 1 A 3 3 0 1 0 8 7 A 3 3 0 1 0 8 1 M 1 16 Q 1 9 8 9 Q 15 9 15 16";
          case "Préstamos" -> "M 1 3 L 12 3 M 9 0 L 12 3 L 9 6 M 15 13 L 4 13 M 7 10 L 4 13 L 7 16";
          case "Devoluciones y multas" ->
              "M 8 1 A 7 7 0 1 0 8 15 A 7 7 0 1 0 8 1 M 8 4 L 8 8 L 12 10";
          case "Reportes" -> "M 1 16 L 16 16 M 3 13 L 3 9 M 8 13 L 8 5 M 13 13 L 13 1";
          default -> "M 7 1 L 1 1 L 1 15 L 7 15 M 6 8 L 16 8 M 12 4 L 16 8 L 12 12";
        });
    icono.setFill(javafx.scene.paint.Color.TRANSPARENT);
    icono.setStroke(javafx.scene.paint.Color.WHITE);
    icono.setStrokeWidth(1.5);
    b.setGraphic(icono);
    b.setGraphicTextGap(8);
    b.setWrapText(true);
    return b;
  }

  private Button button(String t, Runnable r, String style) {
    Button b = new Button(t);
    b.getStyleClass().add(style);
    b.setOnAction(e -> safe(r));
    return b;
  }

  private void safe(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      String m = e.getMessage();
      Dialogos.aviso(stage, m == null ? e.getClass().getSimpleName() : m);
    }
  }

  private void page(String title, String subtitle) {
    content.getChildren().clear();
    Label t = new Label(title);
    t.getStyleClass().add("page-title");
    Label sub = new Label(subtitle);
    sub.setWrapText(true);
    sub.getStyleClass().add("subtitle");
    content.getChildren().addAll(barraSuperior(), t, sub);
    FadeTransition fade = new FadeTransition(Duration.millis(180), content);
    fade.setFromValue(0.35);
    fade.setToValue(1);
    fade.play();
  }

  private VBox card(Node... children) {
    VBox v = new VBox(14, children);
    v.getStyleClass().add("card");
    return v;
  }

  private void fill(Node node) {
    content.getChildren().add(node);
    VBox.setVgrow(node, Priority.ALWAYS);
  }

  private String fecha(LocalDate d) {
    return d == null ? "—" : formato.format(d);
  }

  private <T> TableView<T> table() {
    TableView<T> t = new TableView<>();
    t.setMinHeight(110);
    t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    t.setPlaceholder(new Label("Sin registros para mostrar"));
    return t;
  }

  private <T> void col(TableView<T> t, String name, Function<T, String> f) {
    TableColumn<T, String> c = new TableColumn<>(name);
    c.setCellValueFactory(x -> new ReadOnlyStringWrapper(f.apply(x.getValue())));
    c.setMinWidth(name.equals("ISBN") ? 140 : 85);
    c.setCellFactory(
        column ->
            new TableCell<T, String>() {
              {
                setWrapText(true);
              }

              @Override
              protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : value);
                setTooltip(empty || value == null ? null : new Tooltip(value));
              }
            });
    t.getColumns().add(c);
  }

  private <T> T selected(TableView<T> t) {
    T x = t.getSelectionModel().getSelectedItem();
    if (x == null) throw new IllegalArgumentException("Seleccione una fila de la tabla.");
    return x;
  }

  private boolean confirmar(String m) {
    return Dialogos.confirmar(stage, m);
  }

  public void inicio() {
    service.actualizar();
    content.getChildren().clear();
    content.getChildren().add(barraSuperior());
    Label welcome = new Label("Bienvenido a");
    welcome.getStyleClass().add("welcome-kicker");
    Label name = new Label("Henry System\nLibrary");
    name.getStyleClass().add("welcome-name");
    name.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    Label subtitle = new Label("Gestión de Biblioteca y Préstamos");
    subtitle.getStyleClass().add("welcome-subtitle");
    subtitle.setWrapText(true);
    subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    HBox shortcuts =
        new HBox(
            12,
            button("Nuevo préstamo", () -> formPrestamo(null), "hero-action"),
            button("Ver libros", this::libros, "hero-action"));
    shortcuts.setAlignment(Pos.CENTER);
    VBox hero = new VBox(18, welcome, name, subtitle, shortcuts);
    hero.setAlignment(Pos.CENTER);
    hero.setMinWidth(260);
    HBox.setHgrow(hero, Priority.ALWAYS);
    long vencidos = service.prestamos().stream().filter(p -> p.estado().equals("Vencido")).count();
    long proximos =
        service.prestamos().stream()
            .filter(
                p ->
                    p.devolucion == null
                        && !p.vencimiento.isBefore(LocalDate.now())
                        && !p.vencimiento.isAfter(LocalDate.now().plusDays(3)))
            .count();
    long multas =
        service.prestamos().stream()
            .filter(p -> p.devolucion != null && !p.pagada && p.multaCentavos > 0)
            .count();
    Label title = new Label("Notificaciones");
    title.getStyleClass().add("notifications-title");
    VBox avisos =
        new VBox(
            20,
            title,
            aviso(
                "Préstamos vencidos",
                vencidos == 0 ? "Sin préstamos vencidos" : vencidos + " préstamo(s) por devolver",
                () -> {
                  filtroEstado = "Vencido";
                  prestamos(false);
                }),
            aviso(
                "Próximos vencimientos",
                proximos == 0
                    ? "Sin vencimientos próximos"
                    : proximos + " préstamo(s) en los próximos 3 días",
                () -> prestamos(false)),
            aviso(
                "Multas pendientes",
                multas == 0 ? "Sin multas pendientes" : multas + " multa(s) pendiente(s)",
                () -> {
                  filtroEstado = "Multa pendiente";
                  prestamos(true);
                }));
    avisos.getStyleClass().add("notifications");
    avisos.setPrefWidth(295);
    avisos.setMinWidth(270);
    avisos.setMaxWidth(295);
    avisos.setMaxHeight(Region.USE_PREF_SIZE);
    HBox body = new HBox(20, hero, avisos);
    body.setAlignment(Pos.CENTER);
    VBox.setVgrow(body, Priority.ALWAYS);
    content.getChildren().add(body);
  }

  private VBox aviso(String titulo, String texto, Runnable abrir) {
    Label t = new Label(titulo);
    t.getStyleClass().add("notice-title");
    Label descripcion = new Label(texto);
    descripcion.setWrapText(true);
    descripcion.setMinHeight(Region.USE_PREF_SIZE);
    VBox panel = new VBox(12, t, descripcion);
    panel.getStyleClass().add("notice");
    panel.setOnMouseClicked(e -> safe(abrir));
    return panel;
  }

  public void libros() {
    service.actualizar();
    page(
        "Gestión de libros",
        "Buscá por ISBN, título o autor. Seleccioná una categoría para filtrar.");
    TextField search = new TextField();
    search.setPromptText("Buscar libro...");
    HBox.setHgrow(search, Priority.ALWAYS);
    TableView<Libro> t = table();
    col(t, "ISBN", l -> l.isbn);
    col(t, "Título", l -> l.titulo);
    col(t, "Autor", l -> l.autor);
    col(t, "Categoría", l -> l.categoria);
    col(t, "Disponibilidad", l -> l.disponible ? "Disponible" : "Prestado");
    t.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    t.setFixedCellSize(64);
    double[] widths = {140, 170, 170, 110, 125};
    for (int i=0;i<widths.length;i++) {
      t.getColumns().get(i).setPrefWidth(widths[i]);
      t.getColumns().get(i).setMinWidth(widths[i]);
    }
    TreeItem<String> all = new TreeItem<>("Todas las categorías");
    all.setExpanded(true);
    service.libros().stream()
        .map(l -> l.categoria)
        .distinct()
        .sorted()
        .forEach(c -> all.getChildren().add(new TreeItem<>(c)));
    TreeView<String> tree = new TreeView<>(all);
    tree.setPrefWidth(205);
    tree.setMinWidth(165);
    categoria = "Todas las categorías";
    Runnable filter =
        () -> {
          String q = search.getText().toLowerCase(Locale.ROOT);
          t.setItems(
              FXCollections.observableArrayList(
                  service.libros().stream()
                      .filter(
                          l ->
                              categoria.equals("Todas las categorías")
                                  || l.categoria.equals(categoria))
                      .filter(
                          l ->
                              (l.isbn + " " + l.titulo + " " + l.autor)
                                  .toLowerCase(Locale.ROOT)
                                  .contains(q))
                      .toList()));
        };
    search.textProperty().addListener((a, b, c) -> filter.run());
    tree.getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (a, b, c) -> {
              categoria = c == null ? "Todas las categorías" : c.getValue();
              filter.run();
            });
    filter.run();
    HBox toolbar = new HBox(10, search, button("+ Nuevo libro", () -> formLibro(null), "primary"));
    HBox body = new HBox(16, tree, t);
    HBox.setHgrow(t, Priority.ALWAYS);
    VBox.setVgrow(body, Priority.ALWAYS);
    HBox actions =
        new HBox(
            10,
            button("Editar seleccionado", () -> formLibro(selected(t)), "edit"),
            button(
                "Eliminar seleccionado",
                () -> {
                  Libro l = selected(t);
                  if (confirmar("¿Eliminar el libro «" + l.titulo + "»?")) {
                    service.eliminarLibro(l.id);
                    libros();
                  }
                },
                "danger"));
    fill(card(toolbar, body, actions));
  }

  public void estudiantes() {
    page("Gestión de estudiantes", "Registrá los lectores y sus datos de contacto.");
    TextField search = new TextField();
    search.setPromptText("Buscar por nombre, DNI o correo...");
    TableView<Estudiante> t = table();
    col(t, "DNI", e -> e.dni);
    col(t, "Nombre completo", e -> e.nombre);
    col(t, "Correo", e -> e.email == null ? "—" : e.email);
    Runnable filter =
        () ->
            t.setItems(
                FXCollections.observableArrayList(
                    service.estudiantes().stream()
                        .filter(
                            e ->
                                (e.dni + " " + e.nombre + " " + e.email)
                                    .toLowerCase(Locale.ROOT)
                                    .contains(search.getText().toLowerCase(Locale.ROOT)))
                        .toList()));
    search.textProperty().addListener((a, b, c) -> filter.run());
    filter.run();
    HBox actions =
        new HBox(
            10,
            button("+ Nuevo estudiante", () -> formEstudiante(null), "primary"),
            button("Editar", () -> formEstudiante(selected(t)), "edit"),
            button(
                "Eliminar",
                () -> {
                  Estudiante e = selected(t);
                  if (confirmar("¿Eliminar a " + e.nombre + "?")) {
                    service.eliminarEstudiante(e.id);
                    estudiantes();
                  }
                },
                "danger"));
    VBox box = card(search, t, actions);
    VBox.setVgrow(t, Priority.ALWAYS);
    fill(box);
  }

  public void prestamos(boolean devoluciones) {
    service.actualizar();
    page(
        devoluciones ? "Devoluciones y multas" : "Gestión de préstamos",
        devoluciones
            ? "Registrá devoluciones y pagos. Multa: $100 por cada día de atraso."
            : "Creá préstamos y consultá el historial completo.");
    TextField search = new TextField();
    search.setPromptText("Buscar por estudiante, DNI, libro o ISBN...");
    ComboBox<String> estado =
        new ComboBox<>(
            FXCollections.observableArrayList(
                "Todos", "Activo", "Vencido", "Devuelto", "Multa pendiente"));
    estado.setValue(filtroEstado);
    filtroEstado = "Todos";
    HBox.setHgrow(search, Priority.ALWAYS);
    TableView<Prestamo> t = table();
    col(t, "N°", p -> p.id.toString());
    t.getColumns().get(0).setMinWidth(45);
    col(t, "Libro", p -> p.libro.titulo);
    col(t, "Estudiante", p -> p.estudiante.nombre);
    col(t, "Inicio", p -> fecha(p.inicio));
    col(t, "Vencimiento", p -> fecha(p.vencimiento));
    col(t, "Devolución", p -> fecha(p.devolucion));
    col(t, "Estado", Prestamo::estado);
    col(
        t,
        "Multa / Pago",
        p ->
            Multas.pesos(p.devolucion == null ? service.deuda(p) : p.multaCentavos)
                + (p.devolucion != null && p.multaCentavos > 0
                    ? (p.pagada ? " · Pagada" : " · Pendiente")
                    : ""));
    Runnable filter =
        () -> {
          String q = search.getText().toLowerCase(Locale.ROOT), st = estado.getValue();
          t.setItems(
              FXCollections.observableArrayList(
                  service.prestamos().stream()
                      .filter(
                          p ->
                              (p.libro.titulo
                                      + " "
                                      + p.libro.isbn
                                      + " "
                                      + p.estudiante.nombre
                                      + " "
                                      + p.estudiante.dni)
                                  .toLowerCase(Locale.ROOT)
                                  .contains(q))
                      .filter(
                          p ->
                              st.equals("Todos")
                                  || st.equals(p.estado())
                                  || st.equals("Multa pendiente")
                                      && p.devolucion != null
                                      && !p.pagada
                                      && p.multaCentavos > 0)
                      .toList()));
        };
    search.textProperty().addListener((a, b, c) -> filter.run());
    estado.setOnAction(e -> filter.run());
    filter.run();
    FlowPane actions = new FlowPane(10, 10);
    actions
        .getChildren()
        .addAll(
            button("+ Nuevo préstamo", () -> formPrestamo(null), "primary"),
            button("Editar", () -> formPrestamo(selected(t)), "edit"),
            button("Devolver", () -> formDevolucion(selected(t)), "primary"),
            button(
                "Registrar pago",
                () -> {
                  Prestamo p = selected(t);
                  if (confirmar("¿Confirmar el cobro de " + Multas.pesos(service.deuda(p)) + "?")) {
                    service.pagar(p.id);
                    prestamos(devoluciones);
                  }
                },
                "secondary"),
            button(
                "Eliminar préstamo",
                () -> {
                  Prestamo p = selected(t);
                  if (confirmar(
                      "¿Eliminar el préstamo activo N° "
                          + p.id
                          + "? El libro quedará disponible.")) {
                    service.eliminarPrestamo(p.id);
                    prestamos(devoluciones);
                  }
                },
                "danger"));
    VBox box = card(new HBox(12, search, estado), t, actions);
    VBox.setVgrow(t, Priority.ALWAYS);
    fill(box);
  }

  private TextField input(String value) {
    TextField f = new TextField(value == null ? "" : value);
    f.setPrefWidth(340);
    return f;
  }

  private GridPane grid() {
    GridPane g = new GridPane();
    g.setHgap(18);
    g.setVgap(16);
    return g;
  }

  private void row(GridPane g, int i, String label, Node f) {
    g.add(new Label(label), 0, i);
    g.add(f, 1, i);
    GridPane.setHgrow(f, Priority.ALWAYS);
  }

  private void dialog(String title, GridPane g, Runnable save) {
    Dialog<Void> d = new Dialog<>();
    d.setResizable(false);
    VBox box = new VBox(18, new Label("Los campos con * son obligatorios."), g);
    box.setPadding(new Insets(18));
    d.getDialogPane().setContent(box);
    ButtonType ok = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
    d.getDialogPane().getButtonTypes().addAll(ok, new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE));
    Dialogos.aplicar(d, stage, title);
    d.getDialogPane()
        .lookupButton(ok)
        .addEventFilter(
            javafx.event.ActionEvent.ACTION,
            e -> {
              try {
                save.run();
              } catch (Exception ex) {
                e.consume();
                Dialogos.aviso(d.getDialogPane().getScene().getWindow(), ex.getMessage());
              }
            });
    d.showAndWait();
  }

  private void formLibro(Libro l) {
    TextField isbn = input(l == null ? "" : l.isbn),
        titulo = input(l == null ? "" : l.titulo),
        autor = input(l == null ? "" : l.autor),
        cat = input(l == null ? "" : l.categoria);
    GridPane g = grid();
    row(g, 0, "ISBN *", isbn);
    row(g, 1, "Título *", titulo);
    row(g, 2, "Autor *", autor);
    row(g, 3, "Categoría *", cat);
    dialog(
        l == null ? "Nuevo libro" : "Editar libro",
        g,
        () -> {
          service.guardarLibro(
              l == null ? null : l.id,
              isbn.getText(),
              titulo.getText(),
              autor.getText(),
              cat.getText());
          libros();
        });
  }

  private void formEstudiante(Estudiante e) {
    TextField dni = input(e == null ? "" : e.dni),
        nombre = input(e == null ? "" : e.nombre),
        email = input(e == null ? "" : e.email);
    dni.setTextFormatter(
        new TextFormatter<String>(c -> c.getControlNewText().matches("[0-9]*") ? c : null));
    GridPane g = grid();
    row(g, 0, "DNI *", dni);
    row(g, 1, "Nombre completo *", nombre);
    row(g, 2, "Correo electrónico", email);
    dialog(
        e == null ? "Nuevo estudiante" : "Editar estudiante",
        g,
        () -> {
          service.guardarEstudiante(
              e == null ? null : e.id, dni.getText(), nombre.getText(), email.getText());
          estudiantes();
        });
  }

  private <T> VBox selector(ComboBox<T> combo, List<T> items) {
    TextField buscar = input("");
    buscar.setPromptText("Escribí para buscar...");
    combo.setItems(FXCollections.observableArrayList(items));
    combo.setMaxWidth(Double.MAX_VALUE);
    buscar
        .textProperty()
        .addListener(
            (a, b, c) -> {
              T value = combo.getValue();
              combo.setItems(
                  FXCollections.observableArrayList(
                      items.stream()
                          .filter(
                              x ->
                                  x.toString()
                                      .toLowerCase(Locale.ROOT)
                                      .contains(c.toLowerCase(Locale.ROOT)))
                          .toList()));
              combo.setValue(value);
            });
    return new VBox(6, buscar, combo);
  }

  private void formPrestamo(Prestamo p) {
    if (p != null && p.devolucion != null)
      throw new IllegalArgumentException("El préstamo ya fue devuelto.");
    ComboBox<Libro> libro = new ComboBox<>();
    ComboBox<Estudiante> estudiante = new ComboBox<>();
    VBox
        lb =
            selector(
                libro,
                service.libros().stream()
                    .filter(l -> l.disponible || p != null && l.id.equals(p.libro.id))
                    .toList()),
        es = selector(estudiante, service.estudiantes());
    if (p != null) {
      libro.getItems().stream()
          .filter(l -> l.id.equals(p.libro.id))
          .findFirst()
          .ifPresent(libro::setValue);
      estudiante.getItems().stream()
          .filter(e -> e.id.equals(p.estudiante.id))
          .findFirst()
          .ifPresent(estudiante::setValue);
    }
    DatePicker inicio = new DatePicker(p == null ? LocalDate.now() : p.inicio),
        vence = new DatePicker(p == null ? LocalDate.now().plusDays(7) : p.vencimiento);
    inicio.setEditable(false);
    vence.setEditable(false);
    GridPane g = grid();
    row(g, 0, "Libro disponible *", lb);
    row(g, 1, "Estudiante *", es);
    row(g, 2, "Fecha de inicio *", inicio);
    row(g, 3, "Vencimiento *", vence);
    dialog(
        p == null ? "Nuevo préstamo" : "Editar préstamo",
        g,
        () -> {
          service.guardarPrestamo(
              p == null ? null : p.id,
              libro.getValue() == null ? null : libro.getValue().id,
              estudiante.getValue() == null ? null : estudiante.getValue().id,
              inicio.getValue(),
              vence.getValue());
          prestamos(false);
        });
  }

  private void formDevolucion(Prestamo p) {
    if (p.devolucion != null) throw new IllegalArgumentException("Este préstamo ya fue devuelto.");
    DatePicker f = new DatePicker(LocalDate.now());
    f.setEditable(false);
    Label multa = new Label(Multas.pesos(Multas.calcular(p.vencimiento, f.getValue())));
    f.valueProperty()
        .addListener(
            (a, b, c) ->
                multa.setText(c == null ? "—" : Multas.pesos(Multas.calcular(p.vencimiento, c))));
    GridPane g = grid();
    row(g, 0, "Libro", new Label(p.libro.titulo));
    row(g, 1, "Fecha de devolución *", f);
    row(g, 2, "Multa calculada", multa);
    dialog(
        "Registrar devolución",
        g,
        () -> {
          service.devolver(p.id, f.getValue());
          prestamos(true);
        });
  }

  public void reportes() {
    page(
        "Reportes de biblioteca",
        "Vista previa y exportación CSV. También incluye un formulario Swing de exportación.");
    ComboBox<ReporteFactory.Tipo> tipos =
        new ComboBox<>(FXCollections.observableArrayList(ReporteFactory.Tipo.values()));
    tipos.setValue(ReporteFactory.Tipo.INVENTARIO);
    TextArea area = new TextArea();
    area.setEditable(false);
    area.getStyleClass().add("report-preview");
    Runnable generar =
        () -> {
          service.actualizar();
          area.setText(ReporteFactory.crear(tipos.getValue()).generar(service));
        };
    tipos.setOnAction(e -> generar.run());
    generar.run();
    Button export =
        button(
            "Exportar CSV",
            () -> {
              generar.run();
              FileChooser chooser = new FileChooser();
              chooser.setTitle("Guardar reporte");
              chooser.setInitialFileName(tipos.getValue().name().toLowerCase() + ".csv");
              chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
              java.io.File f = chooser.showSaveDialog(stage);
              if (f != null)
                try {
                  Files.writeString(
                      f.toPath(),
                      "\ufeff" + area.getText(),
                      java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception e) {
                  throw new IllegalStateException("No se pudo exportar: " + e.getMessage());
                }
            },
            "primary");
    Button swing =
        button(
            "Exportar con Swing",
            () -> {
              generar.run();
              SwingExportar.abrir(area.getText());
            },
            "secondary");
    VBox box = card(new HBox(12, tipos, export, swing), area);
    VBox.setVgrow(area, Priority.ALWAYS);
    fill(box);
  }
}
