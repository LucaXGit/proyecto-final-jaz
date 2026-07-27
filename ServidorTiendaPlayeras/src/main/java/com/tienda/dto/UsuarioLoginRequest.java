package com.tienda.dto;

public class UsuarioLoginRequest {

    private String correo;
    private String password;

    public UsuarioLoginRequest() {
    }

    public UsuarioLoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}