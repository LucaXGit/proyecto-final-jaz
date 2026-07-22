package com.tienda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {

    private static final String HOST = obtenerVariable("DB_HOST", "db");
    private static final String PORT = obtenerVariable("DB_PORT", "3306");
    private static final String DATABASE = obtenerVariable("DB_NAME", "tienda_playeras");
    private static final String USER = obtenerVariable("DB_USER", "tienda_user");
    private static final String PASSWORD = obtenerVariable("DB_PASSWORD", "tienda_password");

    private static final String URL = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        HOST,
        PORT,
        DATABASE
    );

    private Conexion() {
    }

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException exception) {
            System.err.println(
                "Error al conectar con MySQL en "
                    + HOST + ":" + PORT + "/" + DATABASE
                    + ". Detalle: " + exception.getMessage()
            );
            return null;
        }
    }

    private static String obtenerVariable(
        String nombre,
        String valorPredeterminado
    ) {
        String valor = System.getenv(nombre);

        return valor == null || valor.isBlank()
            ? valorPredeterminado
            : valor;
    }
}
