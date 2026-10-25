# Validación de la entrega

Fecha: 25/10/2026.

- Compilación del proyecto Maven con JDK 17: correcta.
- JUnit 5: 19 pruebas de negocio, patrones y persistencia aprobadas; cero fallos y cero errores.
- Ejecución adicional del perfil de capturas: 1 prueba aprobada y ocho capturas reales de JavaFX generadas con Monocle.
- Inspección visual: inicio, libros, estudiantes, préstamos, devoluciones y reportes. Rediseño rojo y blanco basado en la captura de Henry System Vet: menú blanco, bienvenida central y panel derecho de notificaciones. Se verificaron el ajuste de texto en las tablas y los módulos de biblioteca.
- Persistencia comprobada mediante cierre y reapertura de SQLite en las pruebas.
- Informe técnico: dos páginas, renderizadas y revisadas.
- Base de entrega: 8 libros, 5 estudiantes y 6 préstamos ficticios; integridad y relaciones verificadas.

La validación se realizó en Linux con Java 17. NetBeans y DB Browser no se ejecutaron en Windows en este entorno; se entrega configuración Maven y pasos de apertura para esas herramientas. El formulario Swing fue compilado; las capturas corresponden a JavaFX.

El enlace de GitHub debe publicarse desde la cuenta del alumno. No hay un repositorio remoto creado en esta entrega.

Corrección de inicio en NetBeans: App ahora es un lanzador Java normal y BibliotecaApplication contiene el ciclo de JavaFX. Se verificó el arranque con classpath hasta abrir el menú principal, con salida limpia. Las 19 pruebas y la captura de interfaz volvieron a pasar.

Se verificaron confirmación y formulario de alta con el tema rojo y blanco, en capturas reales. La captura de la aplicación comprueba que la ventana está maximizada y no está en pantalla completa. Se comprueba la carga de seis resoluciones del logo.

Inicio maximizado con controles de ventana y barra de tareas visibles. Logo del libro rojo incluido para ventana y barra de tareas, y para la ventana Swing. Prueba de arranque y capturas aprobada.
