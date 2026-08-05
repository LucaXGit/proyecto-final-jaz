package com.tienda.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tienda.model.CarritoItem;

public class CarritoDAO {

    /**
     * Lista los items del carrito de un usuario con datos del producto (JOIN).
     */
    public List<CarritoItem> listarPorUsuario(long usuarioId) throws SQLException {
        String sql = """
            SELECT
                ci.id,
                ci.usuario_id,
                ci.producto_id,
                ci.cantidad,
                ci.fecha_agregado,
                p.nombre AS nombre_producto,
                p.talla,
                p.precio,
                p.stock
            FROM carrito_items ci
            INNER JOIN productos p ON ci.producto_id = p.id
            WHERE ci.usuario_id = ?
            ORDER BY ci.fecha_agregado DESC
            """;

        List<CarritoItem> items = new ArrayList<>();

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, usuarioId);

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    CarritoItem item = new CarritoItem();
                    item.setId(resultado.getLong("id"));
                    item.setUsuarioId(resultado.getLong("usuario_id"));
                    item.setProductoId(resultado.getInt("producto_id"));
                    item.setCantidad(resultado.getInt("cantidad"));
                    item.setFechaAgregado(resultado.getTimestamp("fecha_agregado"));
                    item.setNombreProducto(resultado.getString("nombre_producto"));
                    item.setTalla(resultado.getString("talla"));
                    item.setPrecio(resultado.getDouble("precio"));
                    item.setStock(resultado.getInt("stock"));
                    items.add(item);
                }
            }
        }

        return items;
    }

    /**
     * Agrega un producto al carrito. Si ya existe, suma la cantidad.
     * Usa INSERT ... ON DUPLICATE KEY UPDATE.
     */
    public void agregarItem(long usuarioId, int productoId, int cantidad) throws SQLException {
        String sql = """
            INSERT INTO carrito_items (usuario_id, producto_id, cantidad)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE cantidad = cantidad + VALUES(cantidad)
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, usuarioId);
            statement.setInt(2, productoId);
            statement.setInt(3, cantidad);

            statement.executeUpdate();
        }
    }

    /**
     * Actualiza la cantidad de un item del carrito,
     * verificando que pertenezca al usuario.
     */
    public boolean actualizarCantidad(long itemId, long usuarioId, int cantidad) throws SQLException {
        String sql = """
            UPDATE carrito_items
            SET cantidad = ?
            WHERE id = ? AND usuario_id = ?
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, cantidad);
            statement.setLong(2, itemId);
            statement.setLong(3, usuarioId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un item del carrito verificando que pertenezca al usuario.
     */
    public boolean eliminarItem(long itemId, long usuarioId) throws SQLException {
        String sql = """
            DELETE FROM carrito_items
            WHERE id = ? AND usuario_id = ?
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, itemId);
            statement.setLong(2, usuarioId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Elimina todos los items del carrito de un usuario.
     */
    public void vaciarCarrito(long usuarioId) throws SQLException {
        String sql = "DELETE FROM carrito_items WHERE usuario_id = ?";

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, usuarioId);
            statement.executeUpdate();
        }
    }

    /**
     * Cuenta el número de items distintos en el carrito de un usuario.
     */
    public int contarItems(long usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM carrito_items WHERE usuario_id = ?";

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, usuarioId);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }
        }

        return 0;
    }

    private Connection obtenerConexion() throws SQLException {
        Connection conexion = Conexion.getConexion();

        if (conexion == null) {
            throw new SQLException(
                "No fue posible establecer la conexión con la base de datos."
            );
        }

        return conexion;
    }
}
