package com.tienda.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.tienda.dao.UsuarioDAO;
import com.tienda.dto.UsuarioLoginRequest;
import com.tienda.dto.UsuarioRegistroRequest;
import com.tienda.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
    name = "UsuarioServlet",
    urlPatterns = {"/UsuarioServlet/*"}
)
public class UsuarioServlet extends HttpServlet {

    private static final int FACTOR_COSTO_BCRYPT = 12;

    private static final Pattern PATRON_CORREO = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = new Gson();

    private void configurarRespuesta(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );
        response.setHeader(
            "Access-Control-Allow-Headers",
            "Content-Type, Accept"
        );
    }

    @Override
    protected void doOptions(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        if (!esSolicitudJson(request)) {
            responderError(
                response,
                HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                "El contenido de la solicitud debe enviarse como JSON."
            );
            return;
        }

        String ruta = request.getPathInfo();

        if (ruta == null || "/".equals(ruta)) {
            registrarUsuario(request, response);
            return;
        }

        if ("/login".equals(ruta)) {
            iniciarSesion(request, response);
            return;
        }

        responderError(
            response,
            HttpServletResponse.SC_NOT_FOUND,
            "La operación solicitada no existe."
        );
    }

    private void registrarUsuario(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UsuarioRegistroRequest datos;

        try {
            datos = gson.fromJson(
                request.getReader(),
                UsuarioRegistroRequest.class
            );
        } catch (Exception exception) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "El cuerpo de la solicitud contiene un JSON inválido."
            );
            return;
        }

        if (datos == null) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "No se recibieron datos para registrar al usuario."
            );
            return;
        }

        normalizarDatosRegistro(datos);

        Map<String, String> errores = validarRegistro(datos);

        if (!errores.isEmpty()) {
            responderValidacion(response, errores);
            return;
        }

        try {
            if (usuarioDAO.existeCorreo(datos.getCorreo())) {
                responderError(
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    "El correo electrónico ya está registrado."
                );
                return;
            }

            String passwordHash = BCrypt.hashpw(
                datos.getPassword(),
                BCrypt.gensalt(FACTOR_COSTO_BCRYPT)
            );

            Usuario usuario = new Usuario(
                datos.getNombre(),
                datos.getApellido(),
                datos.getCorreo(),
                passwordHash
            );

            Usuario usuarioRegistrado = usuarioDAO.insertar(usuario);

            responderRegistroExitoso(response, usuarioRegistrado);

        } catch (SQLIntegrityConstraintViolationException exception) {
            responderError(
                response,
                HttpServletResponse.SC_CONFLICT,
                "El correo electrónico ya está registrado."
            );

        } catch (SQLException exception) {
            System.err.println(
                "Error al registrar usuario: "
                    + exception.getMessage()
            );

            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No fue posible registrar al usuario."
            );

        } catch (Exception exception) {
            System.err.println(
                "Error inesperado al registrar usuario: "
                    + exception.getMessage()
            );

            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado al registrar al usuario."
            );
        }
    }

    private void iniciarSesion(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        UsuarioLoginRequest datos;

        try {
            datos = gson.fromJson(
                request.getReader(),
                UsuarioLoginRequest.class
            );
        } catch (Exception exception) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "El cuerpo de la solicitud contiene un JSON inválido."
            );
            return;
        }

        if (datos == null) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "No se recibieron credenciales para iniciar sesión."
            );
            return;
        }

        normalizarDatosLogin(datos);

        Map<String, String> errores = validarLogin(datos);

        if (!errores.isEmpty()) {
            responderValidacion(response, errores);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorCorreo(
                datos.getCorreo()
            );

            if (usuario == null
                    || usuario.getPasswordHash() == null
                    || !BCrypt.checkpw(
                        datos.getPassword(),
                        usuario.getPasswordHash()
                    )) {

                responderError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "El correo electrónico o la contraseña son incorrectos."
                );
                return;
            }

            responderLoginExitoso(response, usuario);

        } catch (IllegalArgumentException exception) {
            System.err.println(
                "Hash BCrypt inválido durante el login: "
                    + exception.getMessage()
            );

            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No fue posible iniciar sesión en este momento."
            );

        } catch (SQLException exception) {
            System.err.println(
                "Error al iniciar sesión: "
                    + exception.getMessage()
            );

            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No fue posible iniciar sesión en este momento."
            );

        } catch (Exception exception) {
            System.err.println(
                "Error inesperado al iniciar sesión: "
                    + exception.getMessage()
            );

            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado al iniciar sesión."
            );
        }
    }

    private boolean esSolicitudJson(HttpServletRequest request) {
        String contentType = request.getContentType();

        return contentType != null
            && contentType.toLowerCase().contains("application/json");
    }

    private void normalizarDatosRegistro(
            UsuarioRegistroRequest datos
    ) {
        datos.setNombre(limpiarTexto(datos.getNombre()));
        datos.setApellido(limpiarTexto(datos.getApellido()));
        datos.setCorreo(normalizarCorreo(datos.getCorreo()));
    }

    private void normalizarDatosLogin(
            UsuarioLoginRequest datos
    ) {
        datos.setCorreo(normalizarCorreo(datos.getCorreo()));
    }

    private String normalizarCorreo(String correo) {
        String correoLimpio = limpiarTexto(correo);

        if (correoLimpio == null) {
            return null;
        }

        return correoLimpio.toLowerCase();
    }

    private String limpiarTexto(String valor) {
        if (valor == null) {
            return null;
        }

        return valor.trim();
    }

    private Map<String, String> validarRegistro(
            UsuarioRegistroRequest datos
    ) {
        Map<String, String> errores = new LinkedHashMap<>();

        validarNombre(datos.getNombre(), errores);
        validarApellido(datos.getApellido(), errores);
        validarCorreo(datos.getCorreo(), errores);
        validarPasswordRegistro(datos.getPassword(), errores);

        return errores;
    }

    private Map<String, String> validarLogin(
            UsuarioLoginRequest datos
    ) {
        Map<String, String> errores = new LinkedHashMap<>();

        validarCorreo(datos.getCorreo(), errores);
        validarPasswordLogin(datos.getPassword(), errores);

        return errores;
    }

    private void validarNombre(
            String nombre,
            Map<String, String> errores
    ) {
        if (nombre == null || nombre.isBlank()) {
            errores.put(
                "nombre",
                "El nombre es obligatorio."
            );
            return;
        }

        if (nombre.length() < 2) {
            errores.put(
                "nombre",
                "El nombre debe contener al menos 2 caracteres."
            );
            return;
        }

        if (nombre.length() > 100) {
            errores.put(
                "nombre",
                "El nombre no puede superar los 100 caracteres."
            );
        }
    }

    private void validarApellido(
            String apellido,
            Map<String, String> errores
    ) {
        if (apellido == null || apellido.isBlank()) {
            errores.put(
                "apellido",
                "El apellido es obligatorio."
            );
            return;
        }

        if (apellido.length() < 2) {
            errores.put(
                "apellido",
                "El apellido debe contener al menos 2 caracteres."
            );
            return;
        }

        if (apellido.length() > 100) {
            errores.put(
                "apellido",
                "El apellido no puede superar los 100 caracteres."
            );
        }
    }

    private void validarCorreo(
            String correo,
            Map<String, String> errores
    ) {
        if (correo == null || correo.isBlank()) {
            errores.put(
                "correo",
                "El correo electrónico es obligatorio."
            );
            return;
        }

        if (correo.length() > 150) {
            errores.put(
                "correo",
                "El correo electrónico no puede superar los 150 caracteres."
            );
            return;
        }

        if (!PATRON_CORREO.matcher(correo).matches()) {
            errores.put(
                "correo",
                "El formato del correo electrónico no es válido."
            );
        }
    }

    private void validarPasswordRegistro(
            String password,
            Map<String, String> errores
    ) {
        if (password == null || password.isBlank()) {
            errores.put(
                "password",
                "La contraseña es obligatoria."
            );
            return;
        }

        if (password.length() < 8) {
            errores.put(
                "password",
                "La contraseña debe contener al menos 8 caracteres."
            );
            return;
        }

        if (password.length() > 72) {
            errores.put(
                "password",
                "La contraseña no puede superar los 72 caracteres."
            );
            return;
        }

        boolean contieneMayuscula = password
            .chars()
            .anyMatch(Character::isUpperCase);

        boolean contieneMinuscula = password
            .chars()
            .anyMatch(Character::isLowerCase);

        boolean contieneNumero = password
            .chars()
            .anyMatch(Character::isDigit);

        if (!contieneMayuscula
                || !contieneMinuscula
                || !contieneNumero) {

            errores.put(
                "password",
                "La contraseña debe incluir una mayúscula, una minúscula y un número."
            );
        }
    }

    private void validarPasswordLogin(
            String password,
            Map<String, String> errores
    ) {
        if (password == null || password.isBlank()) {
            errores.put(
                "password",
                "La contraseña es obligatoria."
            );
            return;
        }

        if (password.length() > 72) {
            errores.put(
                "password",
                "La contraseña no puede superar los 72 caracteres."
            );
        }
    }

    private void responderRegistroExitoso(
            HttpServletResponse response,
            Usuario usuario
    ) throws IOException {

        Map<String, Object> usuarioRespuesta =
            construirUsuarioRespuesta(usuario);

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("success", true);
        cuerpo.put(
            "message",
            "Usuario registrado correctamente."
        );
        cuerpo.put("usuario", usuarioRespuesta);

        escribirJson(
            response,
            HttpServletResponse.SC_CREATED,
            cuerpo
        );
    }

    private void responderLoginExitoso(
            HttpServletResponse response,
            Usuario usuario
    ) throws IOException {

        Map<String, Object> usuarioRespuesta =
            construirUsuarioRespuesta(usuario);

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("success", true);
        cuerpo.put(
            "message",
            "Inicio de sesión exitoso."
        );
        cuerpo.put("usuario", usuarioRespuesta);

        escribirJson(
            response,
            HttpServletResponse.SC_OK,
            cuerpo
        );
    }

    private Map<String, Object> construirUsuarioRespuesta(
            Usuario usuario
    ) {
        Map<String, Object> usuarioRespuesta = new LinkedHashMap<>();
        usuarioRespuesta.put("id", usuario.getId());
        usuarioRespuesta.put("nombre", usuario.getNombre());
        usuarioRespuesta.put("apellido", usuario.getApellido());
        usuarioRespuesta.put("correo", usuario.getCorreo());

        return usuarioRespuesta;
    }

    private void responderValidacion(
            HttpServletResponse response,
            Map<String, String> errores
    ) throws IOException {

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("success", false);
        cuerpo.put(
            "message",
            "Los datos proporcionados no son válidos."
        );
        cuerpo.put("errors", errores);

        escribirJson(
            response,
            HttpServletResponse.SC_BAD_REQUEST,
            cuerpo
        );
    }

    private void responderError(
            HttpServletResponse response,
            int codigoEstado,
            String mensaje
    ) throws IOException {

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("success", false);
        cuerpo.put("message", mensaje);

        escribirJson(response, codigoEstado, cuerpo);
    }

    private void escribirJson(
            HttpServletResponse response,
            int codigoEstado,
            Object cuerpo
    ) throws IOException {

        response.setStatus(codigoEstado);
        response.getWriter().print(gson.toJson(cuerpo));
        response.getWriter().flush();
    }
}