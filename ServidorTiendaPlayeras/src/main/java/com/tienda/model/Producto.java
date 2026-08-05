package com.tienda.model;

public class Producto {
    private String id;
    private String nombre;
    private String talla;
    private double precio;
    private int stock;
    private String imagenUrl;
    private boolean activo;

    public Producto() {
    }

    public Producto(String id, String nombre, String talla, double precio, int stock, String imagenUrl, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.talla = talla;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}