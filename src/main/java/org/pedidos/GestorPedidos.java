package org.pedidos;

import org.conexion.ConexionOracle;

import java.sql.*;
import java.util.ArrayList;

public class GestorPedidos {

    // ==========================================
    // LISTAR PEDIDOS CON INFORMACIÓN COMPLETA
    // ==========================================
    public static ArrayList<String[]> listarPedidosCompleto() {
        ArrayList<String[]> pedidos = new ArrayList<>();
        Connection conexion = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conexion = ConexionOracle.conectar();

            // JOIN para obtener toda la información del cliente
            String sql = "SELECT p.id_pedido, c.nombre, c.email, c.telefono, " +
                    "TO_CHAR(p.fecha, 'DD/MM/YYYY') as fecha, p.estado " +
                    "FROM pedidos p " +
                    "JOIN clientes c ON p.id_cliente = c.id_cliente " +
                    "ORDER BY p.id_pedido DESC";  // Más recientes primero

            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = String.valueOf(rs.getInt("id_pedido"));
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("email") != null ? rs.getString("email") : "-";
                fila[3] = rs.getString("telefono") != null ? rs.getString("telefono") : "-";
                fila[4] = rs.getString("fecha");
                fila[5] = rs.getString("estado");
                pedidos.add(fila);
            }

        } catch (Exception e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return pedidos;
    }

    // ==========================================
    // LISTAR PEDIDOS (versión simple con nombre cliente)
    // ==========================================
    public static ArrayList<String[]> listarPedidos() {
        ArrayList<String[]> pedidos = new ArrayList<>();
        Connection conexion = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "SELECT p.id_pedido, c.nombre, p.fecha, p.estado " +
                    "FROM pedidos p " +
                    "JOIN clientes c ON p.id_cliente = c.id_cliente " +
                    "ORDER BY p.id_pedido";

            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = String.valueOf(rs.getInt("id_pedido"));
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getDate("fecha").toString();
                fila[3] = rs.getString("estado");
                pedidos.add(fila);
            }

        } catch (Exception e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return pedidos;
    }

    // ==========================================
    // INSERTAR PEDIDO (ID automático)
    // ==========================================
    public static String insertarPedido(int idCliente, int i) {
        String resultado = "";
        Connection conexion = null;
        CallableStatement stmt = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "{CALL gestionar_pedido(?, ?, ?, ?, ?, ?)}";
            stmt = conexion.prepareCall(sql);

            stmt.setString(1, "I");              // Acción: Insertar
            stmt.setNull(2, Types.INTEGER);      // ID pedido (no se usa, es automático)
            stmt.setInt(3, idCliente);           // ID cliente
            stmt.setNull(4, Types.VARCHAR);      // Estado (no se usa)
            stmt.registerOutParameter(5, Types.VARCHAR);  // Resultado
            stmt.registerOutParameter(6, Types.INTEGER);  // ID generado

            stmt.execute();

            resultado = stmt.getString(5);

        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return resultado;
    }

    // ==========================================
    // ACTUALIZAR PEDIDO
    // ==========================================
    public static String actualizarPedido(int idPedido, String nuevoEstado) {
        String resultado = "";
        Connection conexion = null;
        CallableStatement stmt = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "{CALL gestionar_pedido(?, ?, ?, ?, ?, ?)}";
            stmt = conexion.prepareCall(sql);

            stmt.setString(1, "U");
            stmt.setInt(2, idPedido);
            stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, nuevoEstado);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.registerOutParameter(6, Types.INTEGER);

            stmt.execute();

            resultado = stmt.getString(5);

        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return resultado;
    }

    // ==========================================
    // ELIMINAR PEDIDO
    // ==========================================
    public static String eliminarPedido(int idPedido) {
        String resultado = "";
        Connection conexion = null;
        CallableStatement stmt = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "{CALL gestionar_pedido(?, ?, ?, ?, ?, ?)}";
            stmt = conexion.prepareCall(sql);

            stmt.setString(1, "D");
            stmt.setInt(2, idPedido);
            stmt.setNull(3, Types.INTEGER);
            stmt.setNull(4, Types.VARCHAR);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.registerOutParameter(6, Types.INTEGER);

            stmt.execute();

            resultado = stmt.getString(5);

        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return resultado;
    }
}