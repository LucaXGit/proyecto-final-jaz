package com.tienda.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.tienda.dao.UsuarioDAO;
import com.tienda.model.Usuario;
import com.tienda.security.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
    name = "UserServlet",
    urlPatterns = {"/api/users/*"}
)
public class UserServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = new Gson();

    private void configurarRespuesta(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader(
            "Access-Control-Allow-Methods",
            "GET, PUT, OPTIONS"
        );
        response.setHeader(
            "Access-Control-Allow-Headers",
            "Content-Type, Authorization, Accept"
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        // Validar Token JWT
        String autorizacion = request.getHeader("Authorization");
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            responderError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Se requiere un token de autenticación válido."
            );
            return;
        }

        String token = autorizacion.substring(7);
        Map<String, Object> payload = JwtUtil.validarYDecodificar(token);

        if (payload == null) {
            responderError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Token inválido o expirado."
            );
            return;
        }

        String ruta = request.getPathInfo();

        if ("/me".equals(ruta)) {
            obtenerPerfilPropio(response, payload);
            return;
        }

        if (ruta == null || "/".equals(ruta)) {
            listarUsuarios(request, response, payload);
            return;
        }

        responderError(
            response,
            HttpServletResponse.SC_NOT_FOUND,
            "La operación solicitada no existe."
        );
    }

    private void obtenerPerfilPropio(
            HttpServletResponse response,
            Map<String, Object> payload
    ) throws IOException {
        try {
            double idDouble = (double) payload.get("id");
            long id = (long) idDouble;

            Usuario usuario = usuarioDAO.buscarPorId(id);

            if (usuario == null) {
                responderError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "El usuario no existe en el sistema."
                );
                return;
            }

            Map<String, Object> usuarioRespuesta = construirUsuarioRespuesta(usuario);
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("usuario", usuarioRespuesta);

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al obtener perfil propio: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error interno al obtener los datos del perfil."
            );
        }
    }

    private void listarUsuarios(
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, Object> payload
    ) throws IOException {
        
        String rolUsuario = (String) payload.get("rol");

        if (!"Admin".equalsIgnoreCase(rolUsuario)) {
            responderError(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Acceso denegado. Se requiere rol de Administrador."
            );
            return;
        }

        try {
            String filtroRol = request.getParameter("rol");
            List<Usuario> lista;

            if (filtroRol != null && !filtroRol.isBlank()) {
                lista = usuarioDAO.listarPorRol(filtroRol);
            } else {
                lista = usuarioDAO.listarTodos();
            }

            java.util.List<Map<String, Object>> listaRespuesta = new java.util.ArrayList<>();
            for (Usuario u : lista) {
                listaRespuesta.add(construirUsuarioRespuesta(u));
            }

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("usuarios", listaRespuesta);

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al listar usuarios: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error interno al listar los usuarios."
            );
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        // Validar Token JWT
        String autorizacion = request.getHeader("Authorization");
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            responderError(response, HttpServletResponse.SC_UNAUTHORIZED, "Se requiere token.");
            return;
        }

        String token = autorizacion.substring(7);
        Map<String, Object> payload = JwtUtil.validarYDecodificar(token);
        if (payload == null) {
            responderError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido.");
            return;
        }

        String rolUsuario = (String) payload.get("rol");
        if (!"Admin".equalsIgnoreCase(rolUsuario)) {
            responderError(response, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
            return;
        }

        String ruta = request.getPathInfo();
        if (ruta == null || !ruta.matches("^/\\d+/rol$")) {
             responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Ruta inválida.");
             return;
        }

        long targetId;
        try {
            targetId = Long.parseLong(ruta.split("/")[1]);
        } catch (Exception e) {
             responderError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido.");
             return;
        }

        // Leer JSON
        Map<String, Object> datos;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = gson.fromJson(request.getReader(), Map.class);
            datos = parsed;
        } catch (Exception e) {
             responderError(response, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido.");
             return;
        }

        if (datos == null || !datos.containsKey("rol")) {
             responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Falta 'rol'.");
             return;
        }

        String nuevoRol = (String) datos.get("rol");
        if (!"Admin".equalsIgnoreCase(nuevoRol) && !"Usuario".equalsIgnoreCase(nuevoRol)) {
             responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Rol inválido.");
             return;
        }

        try {
             boolean actualizado = usuarioDAO.actualizarRol(targetId, nuevoRol);
             if (actualizado) {
                 Map<String, Object> resp = new LinkedHashMap<>();
                 resp.put("success", true);
                 resp.put("message", "Rol actualizado");
                 escribirJson(response, HttpServletResponse.SC_OK, resp);
             } else {
                 responderError(response, HttpServletResponse.SC_NOT_FOUND, "Usuario no hallado");
             }
        } catch (SQLException e) {
             responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno");
        }
    }

    private Map<String, Object> construirUsuarioRespuesta(Usuario usuario) {
        Map<String, Object> usuarioRespuesta = new LinkedHashMap<>();
        usuarioRespuesta.put("id", usuario.getId());
        usuarioRespuesta.put("nombre", usuario.getNombre());
        usuarioRespuesta.put("apellido", usuario.getApellido());
        usuarioRespuesta.put("correo", usuario.getCorreo());
        usuarioRespuesta.put("rol", usuario.getRol());
        return usuarioRespuesta;
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
