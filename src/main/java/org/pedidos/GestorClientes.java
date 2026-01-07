package org.pedidos;

import org.conexion.ConexionOracle;

import java.sql.*;
import java.util.ArrayList;

public class GestorClientes {

    // ==========================================
    // LISTAR TODOS LOS CLIENTES
    // ==========================================
    public static ArrayList<String[]> listarClientes() {
        ArrayList<String[]> clientes = new ArrayList<>();
        Connection conexion = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "SELECT id_cliente, nombre, email, telefono FROM clientes ORDER BY nombre";
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = String.valueOf(rs.getInt("id_cliente"));
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("email") != null ? rs.getString("email") : "";
                fila[3] = rs.getString("telefono") != null ? rs.getString("telefono") : "";
                clientes.add(fila);
            }

        } catch (Exception e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return clientes;
    }

    // ==========================================
    // OBTENER CLIENTES PARA COMBOBOX (id - nombre)
    // ==========================================
    public static ArrayList<String[]> obtenerClientesCombo() {
        ArrayList<String[]> clientes = new ArrayList<>();
        Connection conexion = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "SELECT id_cliente, nombre FROM clientes ORDER BY nombre";
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String[] cliente = new String[2];
                cliente[0] = String.valueOf(rs.getInt("id_cliente"));
                cliente[1] = rs.getString("nombre");
                clientes.add(cliente);
            }

        } catch (Exception e) {
            System.out.println("Error al obtener clientes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return clientes;
    }

    // ==========================================
    // INSERTAR CLIENTE (ID automático)
    // ==========================================
    public static String insertarCliente(String nombre, String email, String telefono) {
        String resultado = "";
        Connection conexion = null;
        CallableStatement stmt = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "{CALL gestionar_cliente(?, ?, ?, ?, ?, ?, ?)}";
            stmt = conexion.prepareCall(sql);

            stmt.setString(1, "I");                    // Acción: Insertar
            stmt.setNull(2, Types.INTEGER);            // ID (automático)
            stmt.setString(3, nombre);                 // Nombre
            stmt.setString(4, email.isEmpty() ? null : email);   // Email
            stmt.setString(5, telefono.isEmpty() ? null : telefono); // Teléfono
            stmt.registerOutParameter(6, Types.VARCHAR);   // Resultado
            stmt.registerOutParameter(7, Types.INTEGER);   // ID generado

            stmt.execute();

            resultado = stmt.getString(6);

        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return resultado;
    }

    // ==========================================
    // ACTUALIZAR CLIENTE
    // ==========================================
    public static String actualizarCliente(int idCliente, String nombre, String email, String telefono) {
        String resultado = "";
        Connection conexion = null;
        CallableStatement stmt = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "{CALL gestionar_cliente(?, ?, ?, ?, ?, ?, ?)}";
            stmt = conexion.prepareCall(sql);

            stmt.setString(1, "U");
            stmt.setInt(2, idCliente);
            stmt.setString(3, nombre);
            stmt.setString(4, email.isEmpty() ? null : email);
            stmt.setString(5, telefono.isEmpty() ? null : telefono);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.registerOutParameter(7, Types.INTEGER);

            stmt.execute();

            resultado = stmt.getString(6);

        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return resultado;
    }

    // ==========================================
    // ELIMINAR CLIENTE
    // ==========================================
    public static String eliminarCliente(int idCliente) {
        String resultado = "";
        Connection conexion = null;
        CallableStatement stmt = null;

        try {
            conexion = ConexionOracle.conectar();

            String sql = "{CALL gestionar_cliente(?, ?, ?, ?, ?, ?, ?)}";
            stmt = conexion.prepareCall(sql);

            stmt.setString(1, "D");
            stmt.setInt(2, idCliente);
            stmt.setNull(3, Types.VARCHAR);
            stmt.setNull(4, Types.VARCHAR);
            stmt.setNull(5, Types.VARCHAR);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.registerOutParameter(7, Types.INTEGER);

            stmt.execute();

            resultado = stmt.getString(6);

        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) { }
            ConexionOracle.desconectar(conexion);
        }

        return resultado;
    }
}