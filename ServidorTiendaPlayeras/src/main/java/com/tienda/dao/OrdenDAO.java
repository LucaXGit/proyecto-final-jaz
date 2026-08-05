package com.tienda.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tienda.model.CarritoItem;
import com.tienda.model.Orden;
import com.tienda.model.OrdenDetalle;

public class OrdenDAO {

    /**
     * Crea una orden a partir de los items del carrito dentro de una transacción.
     *
     * 1. Verifica stock suficiente para cada item
     * 2. Inserta el encabezado de la orden
     * 3. Inserta cada línea de detalle
     * 4. Descuenta el stock de cada producto
     * 5. Vacía el carrito del usuario
     * 6. COMMIT o ROLLBACK
     */
    public Orden crearOrden(long usuarioId, List<CarritoItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("El carrito está vacío.");
        }

        Connection conexion = obtenerConexion();

        try {
            conexion.setAutoCommit(false);

            // 1. Verificar stock suficiente para cada item
            for (CarritoItem item : items) {
                int stockActual = obtenerStockProducto(conexion, item.getProductoId());

                if (stockActual < item.getCantidad()) {
                    throw new SQLException(
                        "Stock insuficiente para '" + item.getNombreProducto()
                            + "'. Disponible: " + stockActual
                            + ", Solicitado: " + item.getCantidad()
                    );
                }
            }

            // 2. Calcular el total
            double total = 0;
            for (CarritoItem item : items) {
                total += item.getPrecio() * item.getCantidad();
            }

            // 3. Insertar encabezado de la orden
            String sqlOrden = """
                INSERT INTO ordenes (usuario_id, total, estado)
                VALUES (?, ?, 'Completada')
                """;

            long ordenId;

            try (PreparedStatement stOrden = conexion.prepareStatement(
                    sqlOrden, Statement.RETURN_GENERATED_KEYS)) {

                stOrden.setLong(1, usuarioId);
                stOrden.setDouble(2, total);
                stOrden.executeUpdate();

                try (ResultSet keys = stOrden.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No se pudo obtener el ID de la orden.");
                    }
                    ordenId = keys.getLong(1);
                }
            }

            // 4. Insertar cada línea de detalle
            String sqlDetalle = """
                INSERT INTO orden_detalle
                    (orden_id, producto_id, nombre_producto, talla, precio_unitario, cantidad, subtotal)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement stDetalle = conexion.prepareStatement(sqlDetalle)) {
                for (CarritoItem item : items) {
                    double subtotal = item.getPrecio() * item.getCantidad();

                    stDetalle.setLong(1, ordenId);
                    stDetalle.setInt(2, item.getProductoId());
                    stDetalle.setString(3, item.getNombreProducto());
                    stDetalle.setString(4, item.getTalla());
                    stDetalle.setDouble(5, item.getPrecio());
                    stDetalle.setInt(6, item.getCantidad());
                    stDetalle.setDouble(7, subtotal);
                    stDetalle.addBatch();
                }
                stDetalle.executeBatch();
            }

            // 5. Descontar stock de cada producto
            String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

            try (PreparedStatement stStock = conexion.prepareStatement(sqlStock)) {
                for (CarritoItem item : items) {
                    stStock.setInt(1, item.getCantidad());
                    stStock.setInt(2, item.getProductoId());
                    stStock.setInt(3, item.getCantidad());
                    stStock.addBatch();
                }

                int[] resultados = stStock.executeBatch();

                for (int i = 0; i < resultados.length; i++) {
                    if (resultados[i] == 0) {
                        throw new SQLException(
                            "No se pudo descontar el stock del producto '"
                                + items.get(i).getNombreProducto() + "'."
                        );
                    }
                }
            }

            // 6. Vaciar el carrito del usuario
            String sqlVaciar = "DELETE FROM carrito_items WHERE usuario_id = ?";

            try (PreparedStatement stVaciar = conexion.prepareStatement(sqlVaciar)) {
                stVaciar.setLong(1, usuarioId);
                stVaciar.executeUpdate();
            }

            conexion.commit();

            // Construir objeto Orden de respuesta
            Orden orden = new Orden();
            orden.setId(ordenId);
            orden.setUsuarioId(usuarioId);
            orden.setTotal(total);
            orden.setEstado("Completada");

            List<OrdenDetalle> detalles = new ArrayList<>();
            for (CarritoItem item : items) {
                OrdenDetalle detalle = new OrdenDetalle();
                detalle.setOrdenId(ordenId);
                detalle.setProductoId(item.getProductoId());
                detalle.setNombreProducto(item.getNombreProducto());
                detalle.setTalla(item.getTalla());
                detalle.setPrecioUnitario(item.getPrecio());
                detalle.setCantidad(item.getCantidad());
                detalle.setSubtotal(item.getPrecio() * item.getCantidad());
                detalles.add(detalle);
            }

            orden.setDetalles(detalles);

            return orden;

        } catch (SQLException exception) {
            try {
                conexion.rollback();
            } catch (SQLException rollbackException) {
                System.err.println("Error en rollback: " + rollbackException.getMessage());
            }
            throw exception;

        } finally {
            try {
                conexion.setAutoCommit(true);
                conexion.close();
            } catch (SQLException closeException) {
                System.err.println("Error al cerrar conexión: " + closeException.getMessage());
            }
        }
    }

    /**
     * Lista las órdenes de un usuario específico (sin detalles).
     */
    public List<Orden> listarPorUsuario(long usuarioId) throws SQLException {
        String sql = """
            SELECT
                o.id,
                o.usuario_id,
                o.total,
                o.estado,
                o.fecha_creacion,
                u.nombre AS nombre_usuario,
                u.apellido AS apellido_usuario
            FROM ordenes o
            INNER JOIN usuarios u ON o.usuario_id = u.id
            WHERE o.usuario_id = ?
            ORDER BY o.fecha_creacion DESC
            """;

        List<Orden> ordenes = new ArrayList<>();

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, usuarioId);

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    ordenes.add(mapearOrden(resultado));
                }
            }
        }

        return ordenes;
    }

    /**
     * Lista todas las órdenes del sistema (para el admin).
     */
    public List<Orden> listarTodas() throws SQLException {
        String sql = """
            SELECT
                o.id,
                o.usuario_id,
                o.total,
                o.estado,
                o.fecha_creacion,
                u.nombre AS nombre_usuario,
                u.apellido AS apellido_usuario
            FROM ordenes o
            INNER JOIN usuarios u ON o.usuario_id = u.id
            ORDER BY o.fecha_creacion DESC
            """;

        List<Orden> ordenes = new ArrayList<>();

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                ordenes.add(mapearOrden(resultado));
            }
        }

        return ordenes;
    }

    /**
     * Busca una orden por su ID e incluye los detalles completos.
     */
    public Orden buscarPorId(long ordenId) throws SQLException {
        String sqlOrden = """
            SELECT
                o.id,
                o.usuario_id,
                o.total,
                o.estado,
                o.fecha_creacion,
                u.nombre AS nombre_usuario,
                u.apellido AS apellido_usuario
            FROM ordenes o
            INNER JOIN usuarios u ON o.usuario_id = u.id
            WHERE o.id = ?
            """;

        String sqlDetalles = """
            SELECT
                id,
                orden_id,
                producto_id,
                nombre_producto,
                talla,
                precio_unitario,
                cantidad,
                subtotal
            FROM orden_detalle
            WHERE orden_id = ?
            ORDER BY id ASC
            """;

        try (Connection conexion = obtenerConexion()) {

            Orden orden = null;

            try (PreparedStatement stOrden = conexion.prepareStatement(sqlOrden)) {
                stOrden.setLong(1, ordenId);

                try (ResultSet resultado = stOrden.executeQuery()) {
                    if (resultado.next()) {
                        orden = mapearOrden(resultado);
                    }
                }
            }

            if (orden == null) {
                return null;
            }

            List<OrdenDetalle> detalles = new ArrayList<>();

            try (PreparedStatement stDetalle = conexion.prepareStatement(sqlDetalles)) {
                stDetalle.setLong(1, ordenId);

                try (ResultSet resultado = stDetalle.executeQuery()) {
                    while (resultado.next()) {
                        OrdenDetalle detalle = new OrdenDetalle();
                        detalle.setId(resultado.getLong("id"));
                        detalle.setOrdenId(resultado.getLong("orden_id"));
                        detalle.setProductoId(resultado.getInt("producto_id"));
                        detalle.setNombreProducto(resultado.getString("nombre_producto"));
                        detalle.setTalla(resultado.getString("talla"));
                        detalle.setPrecioUnitario(resultado.getDouble("precio_unitario"));
                        detalle.setCantidad(resultado.getInt("cantidad"));
                        detalle.setSubtotal(resultado.getDouble("subtotal"));
                        detalles.add(detalle);
                    }
                }
            }

            orden.setDetalles(detalles);
            return orden;
        }
    }

    private int obtenerStockProducto(Connection conexion, int productoId) throws SQLException {
        String sql = "SELECT stock FROM productos WHERE id = ?";

        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, productoId);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("stock");
                }
            }
        }

        throw new SQLException("El producto con ID " + productoId + " no existe.");
    }

    private Orden mapearOrden(ResultSet resultado) throws SQLException {
        Orden orden = new Orden();
        orden.setId(resultado.getLong("id"));
        orden.setUsuarioId(resultado.getLong("usuario_id"));
        orden.setTotal(resultado.getDouble("total"));
        orden.setEstado(resultado.getString("estado"));
        orden.setFechaCreacion(resultado.getTimestamp("fecha_creacion"));
        orden.setNombreUsuario(resultado.getString("nombre_usuario"));
        orden.setApellidoUsuario(resultado.getString("apellido_usuario"));
        return orden;
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
