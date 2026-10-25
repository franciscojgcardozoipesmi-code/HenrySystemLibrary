# Recorrido para la exposición

1. Abrí el proyecto Maven en NetBeans, señalá `pom.xml`, la versión de Java, JavaFX, Hibernate, SQLite y JUnit 5.
2. Ejecutá el programa. Mostrá el menú y las notificaciones de biblioteca. Explicá que conserva la identidad rojo y blanco del proyecto Henry System Vet.
3. En Libros cargá un ejemplar: ISBN `9780307474728`, título Cien años de soledad, autor Gabriel García Márquez, categoría Novela. Mostrá buscador, edición y árbol de categorías.
4. En Estudiantes cargá un lector con DNI numérico. Probá intentar dejar el nombre vacío y explicá la validación.
5. Creá un préstamo para hoy con vencimiento hoy. El libro deja de estar disponible. Devolvelo hoy: la multa debe ser cero.
6. Con otro ejemplar y otro lector, creá un préstamo con inicio hace 10 días y vencimiento hace 3. El estado será Vencido. Al devolver hoy generará $300 de multa.
7. Intentá un préstamo nuevo para ese lector: MultaPendienteException. Registrá el pago y repetí el préstamo.
8. Si el vencido aún no se devolvió, la excepción será PrestamoVencidoException. LibroNoDisponibleException protege la regla de libro ocupado aun si se llama al servicio desde fuera de la interfaz; el selector visual solo muestra disponibles.
9. Abrí Reportes. Mostrá inventario, morosos, estadísticas, exportación CSV y el formulario Swing.
10. Abrí `logs/biblioteca.log`, señalá que Logger tiene constructor privado y una única instancia compartida.
11. Cerrá el sistema y abrí `data/biblioteca.db` en DB Browser. Mostrá las relaciones. Reiniciá la aplicación para demostrar persistencia.
12. En NetBeans ejecutá Test. Explicá el borde préstamo/devolución el mismo día, Factory y Singleton.

## Preguntas frecuentes

**¿Dónde está JPA?** En las anotaciones @Entity, @Id, @ManyToOne y en Database: EntityManagerFactory, EntityManager y transacciones. Los listados y controles usan JPQL parametrizado; SQLite se inicializa con DDL por JDBC, pero el CRUD del dominio pasa por JPA.

**¿Para qué HashMap y ArrayList?** El catálogo se indexa por ISBN en `HashMap<String, Libro>`; los préstamos se cargan en `ArrayList<Prestamo>`. Son una vista en memoria sincronizada después de cada operación; la fuente persistente es SQLite.

**¿Dónde está MVC?** Entidades como modelo, controles JavaFX como vista y BibliotecaController como controlador de eventos; las reglas están extraídas a BibliotecaService para probarlas sin interfaz.

**¿Qué hace Factory?** Recibe el tipo y crea una implementación de Reporte: Morosos, Inventario o Estadisticas. La interfaz no necesita implementar cada reporte.

**¿Cómo calcula la multa?** Días entre vencimiento y devolución, con mínimo cero, multiplicados por 10.000 centavos ($100). Se guardan centavos enteros para evitar errores de redondeo.

**¿Qué eventos y layouts usa Swing?** ActionListener en Guardar/Cerrar, BorderLayout en la ventana y FlowLayout en paneles. JavaFX usa BorderPane, VBox, HBox, GridPane y FlowPane.

**¿Qué animación hay?** FadeTransition al entrar a cada módulo; los estilos se centralizan en style.css.

**¿Por qué no se borra un préstamo devuelto?** Para conservar historial y pagos; el CRUD permite eliminar préstamos activos creados por error.
