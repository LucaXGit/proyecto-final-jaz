package com.tienda.model;

public class Usuario {

    private long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String passwordHash;
    private String rol;

    public Usuario() {
    }

    public Usuario(
            long id,
            String nombre,
            String apellido,
            String correo,
            String passwordHash,
            String rol
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Usuario(
            long id,
            String nombre,
            String apellido,
            String correo,
            String passwordHash
    ) {
        this(id, nombre, apellido, correo, passwordHash, "Usuario");
    }

    public Usuario(
            String nombre,
            String apellido,
            String correo,
            String passwordHash,
            String rol
    ) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Usuario(
            String nombre,
            String apellido,
            String correo,
            String passwordHash
    ) {
        this(nombre, apellido, correo, passwordHash, "Usuario");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}