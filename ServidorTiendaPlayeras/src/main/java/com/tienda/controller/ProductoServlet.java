package com.tienda.controller;

import com.google.gson.Gson;
import com.tienda.dao.ProductoDAO;
import com.tienda.model.Producto;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    private final ProductoDAO dao = new ProductoDAO();
    private final Gson gson = new Gson();

    private void configurarCORS(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json;charset=UTF-8");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        configurarCORS(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        
        String json = "[]";
        try {
            List<Producto> lista = dao.listar();
            json = this.gson.toJson(lista);
        } catch (Exception e) {
            System.out.println("Error en doGet: " + e.getMessage());
        }
        
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        
        String accion = request.getParameter("accion");
        boolean exito = false;

        try {
            if ("crear".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String talla = request.getParameter("talla");
                double precio = Double.parseDouble(request.getParameter("precio"));
                int stock = Integer.parseInt(request.getParameter("stock"));
                String imagenUrl = request.getParameter("imagenUrl");
                boolean activo = Boolean.parseBoolean(request.getParameter("activo"));

                Producto nuevo = new Producto();
                nuevo.setNombre(nombre);
                nuevo.setTalla(talla);
                nuevo.setPrecio(precio);
                nuevo.setStock(stock);
                nuevo.setImagenUrl(imagenUrl);
                nuevo.setActivo(activo);

                exito = dao.insertar(nuevo); 

            } else if ("vender".equals(accion)) {
                String id = request.getParameter("id");
                int cantidad = 1; 
                if (request.getParameter("cantidad") != null) {
                    cantidad = Integer.parseInt(request.getParameter("cantidad"));
                }
                
                exito = dao.vender(id, cantidad);
            }
        } catch (Exception e) {
            System.out.println("Error en doPost Servlet: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\": " + exito + "}");
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        boolean exito = false;
        
        try {
            String id = request.getParameter("id");
            String nombre = request.getParameter("nombre");
            String talla = request.getParameter("talla");
            double precio = Double.parseDouble(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            String imagenUrl = request.getParameter("imagenUrl");
            boolean activo = Boolean.parseBoolean(request.getParameter("activo"));

            Producto editado = new Producto();
            editado.setId(id);
            editado.setNombre(nombre);
            editado.setTalla(talla);
            editado.setPrecio(precio);
            editado.setStock(stock);
            editado.setImagenUrl(imagenUrl);
            editado.setActivo(activo);

            exito = dao.actualizar(editado);
        } catch (Exception e) {
            System.out.println("Error en doPut Servlet: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\": " + exito + "}");
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        boolean exito = false;
        
        try {
            String id = request.getParameter("id");
            exito = dao.eliminar(id);
        } catch (Exception e) {
            System.out.println("Error en doDelete Servlet: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\": " + exito + "}");
            out.flush();
        }
    }
}