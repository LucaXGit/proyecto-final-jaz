CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    talla VARCHAR(10) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

INSERT INTO productos (nombre, talla, precio, stock) VALUES
('Playera Oversize Negra', 'M', 299.99, 50),
('Playera Anime Blanca', 'G', 249.50, 35);

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'Usuario'
);

INSERT INTO usuarios (id, nombre, apellido, correo, password_hash, rol) VALUES
(1, 'Admin', 'MetroDrop', 'admin@mail.com', '$12$YhTbwRDIFScvWUf2Cg11G.zlxpoIeMwtzZgaODFf72cnqC4g3E/t.', 'Admin')
AS new ON DUPLICATE KEY UPDATE 
    correo = new.correo,
    password_hash = new.password_hash,
    rol = new.rol;

CREATE TABLE IF NOT EXISTS carrito_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_usuario_producto (usuario_id, producto_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ordenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Completada',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orden_detalle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orden_id INT NOT NULL,
    producto_id INT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    talla VARCHAR(10) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (orden_id) REFERENCES ordenes(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

