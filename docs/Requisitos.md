# Correspondencia con la consigna

| Requisito | Implementación |
|---|---|
| JavaFX + JPA | App, BibliotecaController, Database, persistence.xml |
| Libro con ISBN, título, autor, disponible | model/Libro.java, CRUD en BibliotecaService y módulo Libros |
| Préstamo con libro, estudiante y fechas | model/Prestamo.java, model/Estudiante.java y formularios |
| CRUD de préstamos | guardarPrestamo, prestamos y eliminarPrestamo; historial devuelto protegido |
| Multas por atraso | Multas.calcular, devolver, pagar |
| HashMap<String, Libro> | BibliotecaService.catalogo |
| ArrayList<Prestamo> | BibliotecaService.historial |
| Tres excepciones propias | exception/; lanzadas desde reglas de préstamo |
| Logger Singleton | service/Logger.java; registra altas, modificaciones, bajas, devoluciones y pagos |
| Factory de reportes | report/ReporteFactory.java y Reporte |
| JUnit 5 con bordes | MultasTest, PatronesTest, PersistenciaTest |
| TableView de libros | BibliotecaController.libros |
| TreeView de categorías | BibliotecaController.libros, árbol dinámico |
| CSS | src/main/resources/style.css |
| Formulario Swing, eventos y Layout Manager | SwingExportar, botón en Reportes |
| Animaciones y MVC | FadeTransition, model / ui / service / persistence |
| EntityManager y JPQL | Database.tx y BibliotecaService |
| SQL | sql/01, 02 y 03 |
| Capturas reales | docs/capturas |
| Informe hasta 2 páginas | docs/Informe_tecnico.pdf |
| Repositorio GitHub | Paso pendiente del titular de la cuenta; docs/GITHUB.md |
