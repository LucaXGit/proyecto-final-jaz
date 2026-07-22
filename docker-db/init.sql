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
