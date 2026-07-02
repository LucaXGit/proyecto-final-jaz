package com.tienda.dao;

import com.tienda.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // 1. READ (CONSULTA): Consultar todos los productos
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setTalla(rs.getString("talla"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    // 2. CREATE (INSERTAR): Agregar una nueva playera
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO productos (nombre, talla, precio, stock) VALUES (?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTalla());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // 3. UPDATE (ACTUALIZAR): Modificar los datos de una playera
    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, talla = ?, precio = ?, stock = ? WHERE id = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTalla());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getId());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // 4. DELETE (ELIMINAR): Borrar una playera permanentemente
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    // Método modificado para restar una cantidad específica al inventario
    public boolean vender(int id, int cantidad) {
        // Resta la cantidad solicitada siempre y cuando haya suficiente stock
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            ps.setInt(3, cantidad);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si la venta se completó con éxito
            
        } catch (SQLException e) {
            System.out.println("Error al vender: " + e.getMessage());
            return false;
        }
    }
}