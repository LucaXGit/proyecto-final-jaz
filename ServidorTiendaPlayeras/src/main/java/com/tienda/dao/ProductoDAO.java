package com.tienda.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.tienda.model.Producto;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private MongoCollection<Document> getCollection() {
        return ConexionMongo.getDatabase().getCollection("productos");
    }

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                lista.add(mapToProducto(doc));
            }
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    public Producto buscarPorId(String id) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", new ObjectId(id))).first();
            if (doc != null) {
                return mapToProducto(doc);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Producto p) {
        try {
            Document doc = new Document()
                    .append("nombre", p.getNombre())
                    .append("talla", p.getTalla())
                    .append("precio", p.getPrecio())
                    .append("stock", p.getStock())
                    .append("imagenUrl", p.getImagenUrl())
                    .append("activo", p.isActivo());
            
            getCollection().insertOne(doc);
            
            // Si el objeto p necesita tener el id recién insertado:
            p.setId(doc.getObjectId("_id").toHexString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Producto p) {
        try {
            Document update = new Document("$set", new Document()
                    .append("nombre", p.getNombre())
                    .append("talla", p.getTalla())
                    .append("precio", p.getPrecio())
                    .append("stock", p.getStock())
                    .append("imagenUrl", p.getImagenUrl())
                    .append("activo", p.isActivo()));
            
            long count = getCollection().updateOne(Filters.eq("_id", new ObjectId(p.getId())), update).getModifiedCount();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String id) {
        try {
            long count = getCollection().deleteOne(Filters.eq("_id", new ObjectId(id))).getDeletedCount();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean vender(String id, int cantidad) {
        try {
            // Actualiza restando el stock SOLO si el stock actual es mayor o igual a la cantidad
            long count = getCollection().updateOne(
                    Filters.and(
                            Filters.eq("_id", new ObjectId(id)),
                            Filters.gte("stock", cantidad)
                    ),
                    Updates.inc("stock", -cantidad)
            ).getModifiedCount();
            
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error al vender producto: " + e.getMessage());
            return false;
        }
    }

    // Helper para mapear Document a Producto
    private Producto mapToProducto(Document doc) {
        Producto p = new Producto();
        p.setId(doc.getObjectId("_id").toHexString());
        p.setNombre(doc.getString("nombre"));
        p.setTalla(doc.getString("talla"));
        
        // Manejar precio (MongoDB guarda double a veces como int o double según el driver)
        Number precioNum = doc.get("precio", Number.class);
        p.setPrecio(precioNum != null ? precioNum.doubleValue() : 0.0);
        
        Number stockNum = doc.get("stock", Number.class);
        p.setStock(stockNum != null ? stockNum.intValue() : 0);
        
        p.setImagenUrl(doc.getString("imagenUrl"));
        
        Boolean activo = doc.getBoolean("activo");
        p.setActivo(activo != null ? activo : true); // por defecto true
        
        return p;
    }
}