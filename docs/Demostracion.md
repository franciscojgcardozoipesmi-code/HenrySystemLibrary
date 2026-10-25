# Datos listos para la exposición

La base incluida contiene 8 libros, 5 estudiantes y 6 préstamos ficticios. No es necesario ejecutar SQL para verlos.

| Estudiante | Libro | Caso de demostración |
|---|---|---|
| Ana Gómez | Cien años de soledad | Activo, vence dos días después de la preparación |
| Bruno López | El principito | Vencido, tres días de atraso al preparar la base |
| Carla Pérez | Effective Java | Devuelto, multa de $200 pendiente |
| Diego Fernández | 1984 | Devuelto, multa de $400 pagada |
| Elena Rodríguez | Don Quijote de la Mancha | Devuelto el mismo día, sin multa |
| Elena Rodríguez | Clean Code | Activo, vence diez días después de la preparación |

Ficciones y Rayuela están disponibles para nuevas operaciones. Los libros devueltos también vuelven a estar disponibles.

## Recorrido sugerido

1. Inicio: mostrar avisos de vencidos, próximos vencimientos y multas pendientes. Los avisos se calculan según la fecha actual.
2. Libros: filtrar por categoría y buscar un título. Mostrar disponibles y prestados.
3. Estudiantes: buscar a Bruno y revisar sus datos.
4. Préstamos: filtrar Vencido. Bruno no puede solicitar otro libro hasta devolver el vencido y pagar la multa que corresponda.
5. Devoluciones y multas: seleccionar la devolución de Carla y registrar el pago de $200. La devolución de Diego ya figura pagada y la de Elena tiene multa cero.
6. Crear un préstamo de Ficciones para Diego. Luego devolverlo el mismo día para mostrar que no genera multa.
7. Reportes: abrir inventario, morosos y estadísticas. Exportar un CSV y abrir el formulario Swing.
8. Cerrar: mostrar el cuadro de confirmación rojo y blanco y pulsar No para seguir.

La multa del préstamo vencido de Bruno aumenta $100 por día; los $300 iniciales no son un importe permanente. Las multas ya devueltas conservan sus importes.

Los datos se guardan al operar. Para repetir la demostración desde cero, conservar una copia original del ZIP y extraerla en otra carpeta; el programa no restablece datos automáticamente.
