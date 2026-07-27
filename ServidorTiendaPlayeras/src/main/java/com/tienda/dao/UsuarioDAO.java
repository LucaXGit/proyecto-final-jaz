package com.tienda.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.tienda.model.Usuario;

public class UsuarioDAO {

    /**
     * Verifica si ya existe un usuario registrado con el correo indicado.
     *
     * @param correo correo electrónico normalizado
     * @return true si el correo ya está registrado
     * @throws SQLException si ocurre un error al consultar MySQL
     */
    public boolean existeCorreo(String correo) throws SQLException {
        String sql = """
            SELECT 1
            FROM usuarios
            WHERE LOWER(correo) = LOWER(?)
            LIMIT 1
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, correo);

            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }
        }
    }

    /**
     * Busca un usuario mediante su correo electrónico.
     *
     * El passwordHash se recupera únicamente para validar la contraseña
     * dentro del backend mediante BCrypt. Este dato nunca debe incluirse
     * en una respuesta JSON.
     *
     * @param correo correo electrónico normalizado
     * @return usuario encontrado o null si el correo no existe
     * @throws SQLException si ocurre un error al consultar MySQL
     */
    public Usuario buscarPorCorreo(String correo) throws SQLException {
        String sql = """
            SELECT
                id,
                nombre,
                apellido,
                correo,
                password_hash
            FROM usuarios
            WHERE LOWER(correo) = LOWER(?)
            LIMIT 1
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, correo);

            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }

                Usuario usuario = new Usuario();
                usuario.setId(resultado.getLong("id"));
                usuario.setNombre(resultado.getString("nombre"));
                usuario.setApellido(resultado.getString("apellido"));
                usuario.setCorreo(resultado.getString("correo"));
                usuario.setPasswordHash(resultado.getString("password_hash"));

                return usuario;
            }
        }
    }

    /**
     * Inserta un nuevo usuario y recupera el identificador generado
     * automáticamente por MySQL.
     *
     * @param usuario usuario con nombre, apellido, correo y passwordHash
     * @return usuario registrado con su id generado
     * @throws SQLException si ocurre un error durante la inserción
     */
    public Usuario insertar(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuarios (
                nombre,
                apellido,
                correo,
                password_hash
            )
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(
                 sql,
                 Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getApellido());
            statement.setString(3, usuario.getCorreo());
            statement.setString(4, usuario.getPasswordHash());

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException(
                    "No se pudo registrar el usuario: ninguna fila fue insertada."
                );
            }

            try (ResultSet clavesGeneradas = statement.getGeneratedKeys()) {
                if (!clavesGeneradas.next()) {
                    throw new SQLException(
                        "El usuario fue insertado, pero no se obtuvo el id generado."
                    );
                }

                usuario.setId(clavesGeneradas.getLong(1));
                return usuario;
            }
        }
    }

    /**
     * Obtiene una conexión válida o lanza una excepción controlada.
     *
     * @return conexión activa con MySQL
     * @throws SQLException si no es posible establecer la conexión
     */
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