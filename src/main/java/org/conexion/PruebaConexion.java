package org.conexion;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        // Intentamos conectar
        Connection conexion = ConexionOracle.conectar();

        // Si la conexión no es null, funcionó
        if (conexion != null) {
            System.out.println("¡Todo listo para trabajar con Oracle!");
        }

        // Cerramos la conexión
        ConexionOracle.desconectar(conexion);
    }
}



