package org.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {

    // Datos de conexión
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USUARIO = "system";
    private static final String PASSWORD = "Santilafu12";

    // Método para obtener la conexión
    public static Connection conectar() {
        Connection conexion = null;

        try {
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("✓ Conexión exitosa a Oracle");
        } catch (SQLException e) {
            System.out.println("✗ Error al conectar: " + e.getMessage());
        }

        return conexion;
    }

    // Método para cerrar la conexión
    public static void desconectar(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("✓ Conexión cerrada");
            } catch (SQLException e) {
                System.out.println("✗ Error al cerrar: " + e.getMessage());
            }
        }
    }
}