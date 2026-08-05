package com.tienda.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.tienda.dao.CarritoDAO;
import com.tienda.model.CarritoItem;
import com.tienda.security.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
    name = "CarritoServlet",
    urlPatterns = {"/api/carrito/*"}
)
public class CarritoServlet extends HttpServlet {

    private final CarritoDAO carritoDAO = new CarritoDAO();
    private final Gson gson = new Gson();

    private void configurarRespuesta(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader(
            "Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS"
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

    // ─── GET /api/carrito ─── Listar items del carrito
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        Map<String, Object> payload = validarToken(request, response);
        if (payload == null) return;

        long usuarioId = obtenerUsuarioId(payload);

        try {
            List<CarritoItem> items = carritoDAO.listarPorUsuario(usuarioId);

            // Calcular total
            double total = 0;
            for (CarritoItem item : items) {
                total += item.getPrecio() * item.getCantidad();
            }

            // Construir respuesta
            java.util.List<Map<String, Object>> listaRespuesta = new java.util.ArrayList<>();
            for (CarritoItem item : items) {
                listaRespuesta.add(construirItemRespuesta(item));
            }

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("items", listaRespuesta);
            cuerpo.put("totalItems", items.size());
            cuerpo.put("total", total);

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al listar carrito: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error interno al obtener el carrito."
            );
        }
    }

    // ─── POST /api/carrito ─── Agregar item al carrito
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        Map<String, Object> payload = validarToken(request, response);
        if (payload == null) return;

        long usuarioId = obtenerUsuarioId(payload);

        // Leer JSON del body
        Map<String, Object> datos;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = gson.fromJson(request.getReader(), Map.class);
            datos = parsed;
        } catch (Exception exception) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "El cuerpo de la solicitud contiene un JSON inválido."
            );
            return;
        }

        if (datos == null || !datos.containsKey("productoId")) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Se requiere el campo 'productoId'."
            );
            return;
        }

        String productoId = String.valueOf(datos.get("productoId"));
        int cantidad = datos.containsKey("cantidad")
            ? ((Number) datos.get("cantidad")).intValue()
            : 1;

        if (cantidad < 1) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "La cantidad debe ser al menos 1."
            );
            return;
        }

        try {
            carritoDAO.agregarItem(usuarioId, productoId, cantidad);

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("message", "Producto agregado al carrito.");

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al agregar al carrito: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No se pudo agregar el producto al carrito."
            );
        }
    }

    // ─── PUT /api/carrito/{id} ─── Actualizar cantidad
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        Map<String, Object> payload = validarToken(request, response);
        if (payload == null) return;

        long usuarioId = obtenerUsuarioId(payload);

        long itemId = extraerIdDeRuta(request);
        if (itemId <= 0) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Se requiere un ID de item válido en la ruta."
            );
            return;
        }

        Map<String, Object> datos;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = gson.fromJson(request.getReader(), Map.class);
            datos = parsed;
        } catch (Exception exception) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "El cuerpo de la solicitud contiene un JSON inválido."
            );
            return;
        }

        if (datos == null || !datos.containsKey("cantidad")) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Se requiere el campo 'cantidad'."
            );
            return;
        }

        int cantidad = ((Number) datos.get("cantidad")).intValue();

        if (cantidad < 1) {
            responderError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "La cantidad debe ser al menos 1."
            );
            return;
        }

        try {
            boolean actualizado = carritoDAO.actualizarCantidad(itemId, usuarioId, cantidad);

            if (!actualizado) {
                responderError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "El item no fue encontrado en tu carrito."
                );
                return;
            }

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("message", "Cantidad actualizada.");

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al actualizar carrito: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No se pudo actualizar el item del carrito."
            );
        }
    }

    // ─── DELETE /api/carrito/{id} o /api/carrito ─── Eliminar item o vaciar
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        Map<String, Object> payload = validarToken(request, response);
        if (payload == null) return;

        long usuarioId = obtenerUsuarioId(payload);
        String ruta = request.getPathInfo();

        try {
            // Si no hay path o es "/", vaciar todo el carrito
            if (ruta == null || "/".equals(ruta)) {
                carritoDAO.vaciarCarrito(usuarioId);

                Map<String, Object> cuerpo = new LinkedHashMap<>();
                cuerpo.put("success", true);
                cuerpo.put("message", "Carrito vaciado correctamente.");

                escribirJson(response, HttpServletResponse.SC_OK, cuerpo);
                return;
            }

            // Si hay un ID, eliminar ese item específico
            long itemId = extraerIdDeRuta(request);
            if (itemId <= 0) {
                responderError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Se requiere un ID de item válido."
                );
                return;
            }

            boolean eliminado = carritoDAO.eliminarItem(itemId, usuarioId);

            if (!eliminado) {
                responderError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "El item no fue encontrado en tu carrito."
                );
                return;
            }

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("message", "Producto eliminado del carrito.");

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al eliminar del carrito: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No se pudo procesar la eliminación."
            );
        }
    }

    // ────────── Utilidades ──────────

    private Map<String, Object> validarToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String autorizacion = request.getHeader("Authorization");

        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            responderError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Se requiere un token de autenticación válido."
            );
            return null;
        }

        String token = autorizacion.substring(7);
        Map<String, Object> payload = JwtUtil.validarYDecodificar(token);

        if (payload == null) {
            responderError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Token inválido o expirado."
            );
            return null;
        }

        return payload;
    }

    private long obtenerUsuarioId(Map<String, Object> payload) {
        double idDouble = (double) payload.get("id");
        return (long) idDouble;
    }

    private long extraerIdDeRuta(HttpServletRequest request) {
        String ruta = request.getPathInfo();

        if (ruta == null || ruta.length() < 2) {
            return -1;
        }

        try {
            return Long.parseLong(ruta.substring(1));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private Map<String, Object> construirItemRespuesta(CarritoItem item) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", item.getId());
        mapa.put("productoId", item.getProductoId());
        mapa.put("nombreProducto", item.getNombreProducto());
        mapa.put("talla", item.getTalla());
        mapa.put("precio", item.getPrecio());
        mapa.put("cantidad", item.getCantidad());
        mapa.put("stock", item.getStock());
        mapa.put("subtotal", item.getPrecio() * item.getCantidad());
        return mapa;
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
