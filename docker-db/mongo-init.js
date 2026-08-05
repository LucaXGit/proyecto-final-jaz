db = db.getSiblingDB('tienda_playeras');

db.createCollection('productos');

const productos = [];
const tallas = ['S', 'M', 'L', 'XL'];
const nombresBase = [
    'Playera Básica', 'Playera Estampada', 'Playera Deportiva', 'Playera Cuello V',
    'Playera Polo', 'Playera Manga Larga', 'Playera Vintage', 'Playera Minimalista'
];
const colores = ['Roja', 'Azul', 'Negra', 'Blanca', 'Gris', 'Verde', 'Amarilla', 'Naranja'];

for (let i = 1; i <= 200; i++) {
    const nombre = `${nombresBase[Math.floor(Math.random() * nombresBase.length)]} ${colores[Math.floor(Math.random() * colores.length)]}`;
    const talla = tallas[Math.floor(Math.random() * tallas.length)];
    const precio = Math.floor(Math.random() * 500) + 150; // de 150 a 649
    const stock = Math.floor(Math.random() * 90) + 10; // de 10 a 99

    productos.push({
        nombre: nombre,
        talla: talla,
        precio: precio,
        stock: stock,
        activo: true,
        imagenUrl: `https://picsum.photos/seed/playera${i}/300/300`
    });
}

db.productos.insertMany(productos);
