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
                password_hash,
                rol
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
                usuario.setRol(resultado.getString("rol"));

                return usuario;
            }
        }
    }

    /**
     * Busca un usuario por su identificador primario.
     *
     * @param id identificador único del usuario
     * @return usuario encontrado o null si no existe
     * @throws SQLException si ocurre un error al consultar MySQL
     */
    public Usuario buscarPorId(long id) throws SQLException {
        String sql = """
            SELECT
                id,
                nombre,
                apellido,
                correo,
                password_hash,
                rol
            FROM usuarios
            WHERE id = ?
            LIMIT 1
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setLong(1, id);

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
                usuario.setRol(resultado.getString("rol"));

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
                password_hash,
                rol
            )
            VALUES (?, ?, ?, ?, ?)
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
            statement.setString(5, usuario.getRol() != null ? usuario.getRol() : "Usuario");

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
     * Obtiene la lista completa de todos los usuarios registrados.
     *
     * @return lista de usuarios
     * @throws SQLException si ocurre un error al consultar MySQL
     */
    public java.util.List<Usuario> listarTodos() throws SQLException {
        String sql = """
            SELECT
                id,
                nombre,
                apellido,
                correo,
                rol
            FROM usuarios
            ORDER BY id ASC
            """;

        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(resultado.getLong("id"));
                usuario.setNombre(resultado.getString("nombre"));
                usuario.setApellido(resultado.getString("apellido"));
                usuario.setCorreo(resultado.getString("correo"));
                usuario.setRol(resultado.getString("rol"));
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    /**
     * Obtiene la lista de usuarios filtrados por su rol.
     *
     * @param rol rol del usuario (Admin, Usuario, etc.)
     * @return lista de usuarios
     * @throws SQLException si ocurre un error al consultar MySQL
     */
    public java.util.List<Usuario> listarPorRol(String rol) throws SQLException {
        String sql = """
            SELECT
                id,
                nombre,
                apellido,
                correo,
                rol
            FROM usuarios
            WHERE LOWER(rol) = LOWER(?)
            ORDER BY id ASC
            """;

        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, rol);

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(resultado.getLong("id"));
                    usuario.setNombre(resultado.getString("nombre"));
                    usuario.setApellido(resultado.getString("apellido"));
                    usuario.setCorreo(resultado.getString("correo"));
                    usuario.setRol(resultado.getString("rol"));
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }

    /**
     * Actualiza el rol de un usuario.
     *
     * @param id identificador del usuario
     * @param rol nuevo rol a asignar (ej. 'Admin', 'Usuario')
     * @return true si se actualizó correctamente, false si el usuario no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean actualizarRol(long id, String rol) throws SQLException {
        String sql = """
            UPDATE usuarios
            SET rol = ?
            WHERE id = ?
            """;

        try (Connection conexion = obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, rol);
            statement.setLong(2, id);

            return statement.executeUpdate() > 0;
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