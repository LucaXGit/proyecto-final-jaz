package com.tienda.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.tienda.dao.CarritoDAO;
import com.tienda.dao.OrdenDAO;
import com.tienda.model.CarritoItem;
import com.tienda.model.Orden;
import com.tienda.model.OrdenDetalle;
import com.tienda.security.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
    name = "OrdenServlet",
    urlPatterns = {"/api/ordenes/*"}
)
public class OrdenServlet extends HttpServlet {

    private final OrdenDAO ordenDAO = new OrdenDAO();
    private final CarritoDAO carritoDAO = new CarritoDAO();
    private final Gson gson = new Gson();

    private void configurarRespuesta(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader(
            "Access-Control-Allow-Methods",
            "GET, POST, OPTIONS"
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

    // ─── GET /api/ordenes o /api/ordenes/{id} ───
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        Map<String, Object> payload = validarToken(request, response);
        if (payload == null) return;

        long usuarioId = obtenerUsuarioId(payload);
        String rol = (String) payload.get("rol");
        String ruta = request.getPathInfo();

        try {
            // GET /api/ordenes/{id} → Detalle de una orden
            if (ruta != null && ruta.length() > 1 && !"/".equals(ruta)) {
                long ordenId = extraerIdDeRuta(request);

                if (ordenId <= 0) {
                    responderError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Se requiere un ID de orden válido."
                    );
                    return;
                }

                Orden orden = ordenDAO.buscarPorId(ordenId);

                if (orden == null) {
                    responderError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "La orden no fue encontrada."
                    );
                    return;
                }

                // Verificar que la orden pertenezca al usuario, excepto si es admin
                if (!"Admin".equalsIgnoreCase(rol) && orden.getUsuarioId() != usuarioId) {
                    responderError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "No tienes permiso para ver esta orden."
                    );
                    return;
                }

                Map<String, Object> cuerpo = new LinkedHashMap<>();
                cuerpo.put("success", true);
                cuerpo.put("orden", construirOrdenRespuesta(orden));

                escribirJson(response, HttpServletResponse.SC_OK, cuerpo);
                return;
            }

            // GET /api/ordenes → Listar órdenes
            List<Orden> ordenes;

            if ("Admin".equalsIgnoreCase(rol)) {
                ordenes = ordenDAO.listarTodas();
            } else {
                ordenes = ordenDAO.listarPorUsuario(usuarioId);
            }

            List<Map<String, Object>> listaRespuesta = new ArrayList<>();
            for (Orden orden : ordenes) {
                listaRespuesta.add(construirOrdenResumenRespuesta(orden));
            }

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("ordenes", listaRespuesta);

            escribirJson(response, HttpServletResponse.SC_OK, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al consultar órdenes: " + exception.getMessage());
            responderError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error interno al consultar las órdenes."
            );
        }
    }

    // ─── POST /api/ordenes ─── Checkout: crear orden desde el carrito
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        configurarRespuesta(response);

        Map<String, Object> payload = validarToken(request, response);
        if (payload == null) return;

        long usuarioId = obtenerUsuarioId(payload);

        try {
            // Obtener items del carrito
            List<CarritoItem> items = carritoDAO.listarPorUsuario(usuarioId);

            if (items.isEmpty()) {
                responderError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El carrito está vacío. Agrega productos antes de realizar la compra."
                );
                return;
            }

            // Crear la orden dentro de una transacción
            Orden orden = ordenDAO.crearOrden(usuarioId, items);

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("success", true);
            cuerpo.put("message", "¡Orden creada exitosamente!");
            cuerpo.put("orden", construirOrdenRespuesta(orden));

            escribirJson(response, HttpServletResponse.SC_CREATED, cuerpo);

        } catch (SQLException exception) {
            System.err.println("Error al crear orden: " + exception.getMessage());

            String mensaje = exception.getMessage();

            if (mensaje != null && mensaje.contains("Stock insuficiente")) {
                responderError(
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    mensaje
                );
            } else {
                responderError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo procesar la compra. " + (mensaje != null ? mensaje : "")
                );
            }
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
        Map<String, Object> payloadResult = JwtUtil.validarYDecodificar(token);

        if (payloadResult == null) {
            responderError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Token inválido o expirado."
            );
            return null;
        }

        return payloadResult;
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

    private Map<String, Object> construirOrdenResumenRespuesta(Orden orden) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id", orden.getId());
        mapa.put("usuarioId", orden.getUsuarioId());
        mapa.put("nombreUsuario", orden.getNombreUsuario());
        mapa.put("apellidoUsuario", orden.getApellidoUsuario());
        mapa.put("total", orden.getTotal());
        mapa.put("estado", orden.getEstado());
        mapa.put("fechaCreacion", orden.getFechaCreacion() != null
            ? orden.getFechaCreacion().toString() : null);
        return mapa;
    }

    private Map<String, Object> construirOrdenRespuesta(Orden orden) {
        Map<String, Object> mapa = construirOrdenResumenRespuesta(orden);

        List<Map<String, Object>> detallesRespuesta = new ArrayList<>();
        if (orden.getDetalles() != null) {
            for (OrdenDetalle detalle : orden.getDetalles()) {
                Map<String, Object> detalleMap = new LinkedHashMap<>();
                detalleMap.put("id", detalle.getId());
                detalleMap.put("productoId", detalle.getProductoId());
                detalleMap.put("nombreProducto", detalle.getNombreProducto());
                detalleMap.put("talla", detalle.getTalla());
                detalleMap.put("precioUnitario", detalle.getPrecioUnitario());
                detalleMap.put("cantidad", detalle.getCantidad());
                detalleMap.put("subtotal", detalle.getSubtotal());
                detallesRespuesta.add(detalleMap);
            }
        }

        mapa.put("detalles", detallesRespuesta);
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
