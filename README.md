# Henry System Library
Sistema de Gestión de Biblioteca y Préstamos. Parcial integrador de Programación III, IPESMI 2026, docente José Lutgen.

## Abrir y ejecutar en NetBeans
1. Descomprimí el ZIP completo en una carpeta con permiso de escritura.
2. Instalá un JDK 17 o 21 de 64 bits (por ejemplo Adoptium). NetBeans debe usar ese JDK para el proyecto.
3. NetBeans → Archivo → Abrir proyecto → seleccioná la carpeta **HenrySystemLibrary**, donde está `pom.xml`.
4. Esperá la descarga de dependencias Maven; la primera vez requiere Internet. No hay que instalar JavaFX por separado ni añadir JAR manualmente.
5. Clic derecho en el proyecto → **Clean and Build** y luego **Run**. `nbactions.xml` configura `javafx:run`.
6. Alternativa por terminal dentro de la carpeta del proyecto: `mvn clean test` y `mvn javafx:run`.
7. No se necesita usuario o contraseña. El menú principal se abre directamente.

Si NetBeans ejecuta una clase equivocada, usá Ejecutar → configurar proyecto → acciones → Run Project → objetivos `process-classes javafx:run`. Clase principal: `biblioteca.App`. No agregues `module-info.java`.

## DB Browser for SQLite
El gestor solicitado es **DB Browser for SQLite**; el motor es SQLite y la persistencia del programa se hace con JPA/Hibernate.
- Base entregada: `data/biblioteca.db`, con 8 libros, 5 estudiantes y 6 préstamos ficticios para demostración.
- Abrila con DB Browser → Abrir base de datos. La ruta es relativa a la carpeta desde la que ejecutás Maven. Ejecutá siempre desde la raíz del proyecto.
- `sql/01_crear_tablas.sql`: tablas, claves foráneas e índice que impide dos préstamos activos para el mismo libro.
- `sql/02_datos_ejemplo.sql`: carga opcional de tres libros y tres estudiantes. Ejecutar una sola vez en una base vacía; guardar cambios en DB Browser antes de abrir la aplicación.
- `sql/03_consultas.sql`: SELECT con relaciones y ejemplo de UPDATE revertido.
- Para modificar la base externamente, cerrá la aplicación primero. No cambies manualmente la disponibilidad de un libro con préstamo activo.
- Si se elimina el archivo de base, la aplicación recrea las tablas vacías al arrancar. No elimina ni reinicia datos en cada ejecución.

## Módulos
- **Inicio**: bienvenida y notificaciones de préstamos vencidos, próximos vencimientos y multas pendientes.
- **Libros**: alta, consulta, edición y eliminación si no tiene historial. ISBN único; título, autor y categoría obligatorios. TableView, búsqueda y TreeView dinámico por categoría.
- **Estudiantes**: alta, consulta, edición y eliminación si no tiene historial. DNI único de 7 a 9 números; nombre obligatorio y correo opcional validado.
- **Préstamos**: CRUD de préstamos activos, selectores con buscadores para libro y estudiante, fechas, estados y filtros. Cada registro representa el préstamo de un ejemplar identificado por su ISBN.
- **Devoluciones y multas**: devolver, consultar multa y registrar su pago. El cálculo usa $100 por día completo después del vencimiento; devolver el mismo día del vencimiento no genera multa.
- **Reportes**: inventario, morosos y estadísticas; vista previa y exportación CSV UTF-8. Botón adicional para abrir el formulario Swing de exportación, con eventos y Layout Managers.

## Reglas de negocio
- Una fila de Libro representa un ejemplar. La consigna no pide múltiples copias con el mismo ISBN.
- La disponibilidad cambia al prestar/devolver, cambiar el libro de un préstamo o eliminar un préstamo activo. No se modifica libremente desde el formulario del libro.
- Inicio no futuro, vencimiento igual o posterior al inicio; devolución entre inicio y hoy.
- Un estudiante con un préstamo vencido debe devolverlo antes de solicitar otro. Si esa devolución genera multa, debe pagarla antes de un préstamo nuevo.
- La multa estimada de un préstamo activo se calcula hasta hoy; al devolver, queda fijada. El reporte de morosos incluye atraso vigente y multas devueltas impagas.
- Las devoluciones se conservan: no se editan ni eliminan, y no se eliminan libros/estudiantes que tengan historial. Esto preserva la trazabilidad; los préstamos activos sí admiten edición y eliminación con confirmación.
- Las transacciones fallidas se revierten. Se usan parámetros JPQL y claves foráneas.

## Organización MVC
`model/`: entidades Libro, Estudiante y Prestamo. `ui/`: vista JavaFX y controlador de eventos. `service/`: reglas, colecciones, Logger y cálculo. `persistence/`: EntityManagerFactory y transacciones. `report/`: Factory e implementaciones. `exception/`: excepciones propias. Recursos: CSS, configuración JPA y esquema SQL.

## Pruebas
`mvn test` ejecuta JUnit 5: Singleton y escritura de Logger; tipos y nulo de Factory; multa en fecha, anticipada, un día tarde, cambio de mes y nulo; persistencia SQLite, CRUD, reapertura, devoluciones, disponibilidad, bloqueos, duplicados y conservación de historial.
Las pruebas usan bases temporales y no tocan `data/biblioteca.db`.
Las capturas de `docs/capturas` corresponden a la aplicación real con datos temporales de demostración, separados de la base entregada.
El perfil opcional `mvn -Pcapturas -Dtest=CapturasTest test` regenera capturas JavaFX mediante Monocle en modo headless; no hace falta para usar el programa.

## Entregables del Word
- Código fuente Maven/NetBeans completo: este proyecto.
- SQL: carpeta `sql`.
- Capturas: `docs/capturas`.
- Informe técnico: `docs/Informe_tecnico.pdf`, máximo dos páginas.
- Guía de exposición y correspondencia de requisitos: carpeta `docs`.
- **Pendiente personal**: publicar el repositorio en GitHub y entregar su enlace al docente `@joselut`. No se inventó un enlace ni se publicó en una cuenta ajena. Ver `docs/GITHUB.md`.

Autor del trabajo para presentación: Cardozo, Francisco José Gabriel. Revisá el código y practicá su explicación antes de entregar, porque la consigna indica trabajo individual y defensa en clase.

## Corrección de arranque JavaFX en NetBeans
La clase de entrada sigue siendo `biblioteca.App`, que ahora es un lanzador Java normal. La aplicación JavaFX está en `BibliotecaApplication`. Esto permite iniciar tanto con `javafx:run` como con la acción de NetBeans que utiliza `exec-maven-plugin` y el classpath de Maven. No se requiere instalar un SDK JavaFX manualmente. Después de actualizar el proyecto, ejecutá Clean and Build y Run Project.

## Versión preparada para demostración
Incluye datos en Libros, Estudiantes, Préstamos, Devoluciones y multas, Inicio y Reportes. Los ejemplos representan préstamos activos, un vencido, una devolución sin multa, una multa impaga y otra pagada. Las fechas de estos ejemplos se fijan al preparar la entrega; los vencimientos activos avanzan normalmente con el calendario. No se reinician ni se reinsertan datos al abrir el programa.

La aplicación se abre MAXIMIZADA, conservando el título, los controles de ventana y la barra de tareas de Windows. No utiliza pantalla completa. El logo del libro rojo se incluye en varias resoluciones para el ícono de la ventana y la barra de tareas. Las confirmaciones, advertencias y formularios tienen cabecera roja y botones acordes al sistema.

Descomprimí esta entrega en una carpeta nueva para usar la base de demostración. Así conservás por separado los datos de tus pruebas anteriores. Ver `docs/Demostracion.md` para el recorrido sugerido.
