-- Datos semilla iniciales para evaluación y pruebas inmediatas
-- Caso 1: Pacientes (Salud / Prevención de Anemia Virú)
INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) VALUES ('70123456', 'Carlos', 'Mendoza', 5, 9.8);
INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) VALUES ('70987654', 'María', 'Rojas', 4, 12.5);
INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) VALUES ('71234567', 'Luis', 'García', 6, 10.2);

-- Caso 2: Productos (Almacén e Inventario)
INSERT INTO productos (codigo, nombre, precio, stock, categoria) VALUES ('PROD-001', 'Laptop Lenovo ThinkPad', 2850.00, 12, 'Computo');
INSERT INTO productos (codigo, nombre, precio, stock, categoria) VALUES ('PROD-002', 'Mouse Inalámbrico Logitech', 85.50, 3, 'Perifericos');
INSERT INTO productos (codigo, nombre, precio, stock, categoria) VALUES ('PROD-003', 'Teclado Mecánico Redragon', 190.00, 15, 'Perifericos');
INSERT INTO productos (codigo, nombre, precio, stock, categoria) VALUES ('PROD-004', 'Monitor IPS 24 Pulgadas', 650.00, 2, 'Monitores');

-- Caso 3: Plantilla Comodín / Activos Tecnológicos ODS 9
INSERT INTO items_genericos (codigo_identificador, denominacion, valor_numerico_principal, cantidad_entera) VALUES ('ACT-01', 'Servidor Blade Dell PowerEdge', 14500.00, 4);
INSERT INTO items_genericos (codigo_identificador, denominacion, valor_numerico_principal, cantidad_entera) VALUES ('ACT-02', 'Switch Cisco Catalyst 48 Puertos', 3200.00, 18);
