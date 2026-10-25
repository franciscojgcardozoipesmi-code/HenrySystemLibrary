-- Consulta JPQL equivalente implementada en BibliotecaService.
SELECT p.id,l.titulo,e.nombre,p.inicio,p.vencimiento,p.devolucion FROM prestamos p JOIN libros l ON l.id=p.libro_id JOIN estudiantes e ON e.id=p.estudiante_id;
SELECT * FROM libros WHERE disponible=1;
-- Ejemplo de UPDATE dentro de una transacción revertida para no alterar datos.
BEGIN TRANSACTION;
UPDATE libros SET categoria='Literatura' WHERE id=1;
ROLLBACK;
