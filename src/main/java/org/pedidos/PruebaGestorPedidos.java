package org.pedidos;

public class PruebaGestorPedidos {

    public static void main(String[] args) {

        // Insertar un pedido
        System.out.println("--- INSERTAR PEDIDO ---");
        String resultado1 = GestorPedidos.insertarPedido(200, 1);
        System.out.println("Resultado: " + resultado1);

        // Actualizar el pedido
        System.out.println("\n--- ACTUALIZAR PEDIDO ---");
        String resultado2 = GestorPedidos.actualizarPedido(200, "ENVIADO");
        System.out.println("Resultado: " + resultado2);

        // Eliminar el pedido
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        String resultado3 = GestorPedidos.eliminarPedido(200);
        System.out.println("Resultado: " + resultado3);

        // Intentar insertar con cliente que no existe
        System.out.println("\n--- INSERTAR CON CLIENTE INEXISTENTE ---");
        String resultado4 = GestorPedidos.insertarPedido(201, 999);
        System.out.println("Resultado: " + resultado4);
    }
}