package com.tienda.model;

import java.sql.Timestamp;

public class CarritoItem {

    private long id;
    private long usuarioId;
    private int productoId;
    private String nombreProducto;
    private String talla;
    private double precio;
    private int cantidad;
    private int stock;
    private Timestamp fechaAgregado;

    public CarritoItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Timestamp getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(Timestamp fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}
