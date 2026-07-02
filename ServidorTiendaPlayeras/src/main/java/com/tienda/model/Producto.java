package com.tienda.model;

public class Producto {
    // Atributos privados (Encapsulamiento para Orientación a Objetos)
    private int id;
    private String nombre;
    private String talla;
    private double precio;
    private int stock;

    // Constructor vacío (Obligatorio para librerías de mapeo como GSON)
    public Producto() {
    }

    // Constructor lleno para crear objetos rápidamente
    public Producto(int id, String nombre, String talla, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.talla = talla;
        this.precio = precio;
        this.stock = stock;
    }

    // Métodos Getter y Setter (Atributos y métodos solicitados en Código 1)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}