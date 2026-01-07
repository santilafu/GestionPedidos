package org.interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import org.pedidos.GestorPedidos;

public class VentanaPedidos extends JFrame {

    // Componentes de la interfaz
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdPedido;
    private JTextField txtIdCliente;
    private JComboBox<String> comboEstado;
    private JLabel lblResultado;

    // Constructor
    public VentanaPedidos() {
        // Configurar la ventana
        setTitle("Gestión de Pedidos - Oracle");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centrar en pantalla

        // Crear los componentes
        crearComponentes();

        // Cargar los datos iniciales
        refrescarTabla();
    }

    private void crearComponentes() {
        // Panel principal con BorderLayout
        setLayout(new BorderLayout(10, 10));

        // PANEL SUPERIOR: La tabla
        String[] columnas = {"ID Pedido", "ID Cliente", "Fecha", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // No permitir editar directamente
            }
        };
        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Cuando se selecciona una fila, rellenar los campos
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaPedidos.getSelectedRow();
                if (fila >= 0) {
                    txtIdPedido.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtIdCliente.setText(modeloTabla.getValueAt(fila, 1).toString());
                    comboEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPedidos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Pedidos"));
        add(scrollTabla, BorderLayout.CENTER);

        // PANEL INFERIOR: Campos y botones
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de campos
        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        panelCampos.add(new JLabel("ID Pedido:"));
        txtIdPedido = new JTextField(8);
        panelCampos.add(txtIdPedido);

        panelCampos.add(new JLabel("ID Cliente:"));
        txtIdCliente = new JTextField(8);
        panelCampos.add(txtIdCliente);

        panelCampos.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(new String[]{"PENDIENTE", "ENVIADO", "ENTREGADO", "CANCELADO"});
        panelCampos.add(comboEstado);

        panelInferior.add(panelCampos);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnInsertar = new JButton("Insertar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnInsertar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnLimpiar);

        panelInferior.add(panelBotones);

        // Label para mostrar resultados
        lblResultado = new JLabel(" ");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 12));
        lblResultado.setHorizontalAlignment(SwingConstants.CENTER);
        panelInferior.add(lblResultado);

        add(panelInferior, BorderLayout.SOUTH);


        // ACCIONES DE LOS BOTONES

        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRefrescar.addActionListener(e -> refrescarTabla());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    // MÉTODOS DE ACCIÓN

    private void insertar() {
        try {
            int idPedido = Integer.parseInt(txtIdPedido.getText().trim());
            int idCliente = Integer.parseInt(txtIdCliente.getText().trim());

            String resultado = GestorPedidos.insertarPedido(idPedido, idCliente);
            mostrarResultado(resultado);
            refrescarTabla();

        } catch (NumberFormatException e) {
            mostrarResultado("ERROR: ID Pedido e ID Cliente deben ser números");
        }
    }

    private void actualizar() {
        try {
            int idPedido = Integer.parseInt(txtIdPedido.getText().trim());
            String estado = comboEstado.getSelectedItem().toString();

            String resultado = GestorPedidos.actualizarPedido(idPedido, estado);
            mostrarResultado(resultado);
            refrescarTabla();

        } catch (NumberFormatException e) {
            mostrarResultado("ERROR: ID Pedido debe ser un número");
        }
    }

    private void eliminar() {
        try {
            int idPedido = Integer.parseInt(txtIdPedido.getText().trim());

            // Confirmar antes de eliminar
            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que deseas eliminar el pedido " + idPedido + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar == JOptionPane.YES_OPTION) {
                String resultado = GestorPedidos.eliminarPedido(idPedido);
                mostrarResultado(resultado);
                refrescarTabla();
                limpiarCampos();
            }

        } catch (NumberFormatException e) {
            mostrarResultado("ERROR: ID Pedido debe ser un número");
        }
    }

    private void refrescarTabla() {
        // Limpiar la tabla
        modeloTabla.setRowCount(0);

        // Obtener los pedidos y añadirlos a la tabla
        ArrayList<String[]> pedidos = GestorPedidos.listarPedidos();
        for (String[] fila : pedidos) {
            modeloTabla.addRow(fila);
        }
    }

    private void limpiarCampos() {
        txtIdPedido.setText("");
        txtIdCliente.setText("");
        comboEstado.setSelectedIndex(0);
        tablaPedidos.clearSelection();
        lblResultado.setText(" ");
    }

    private void mostrarResultado(String mensaje) {
        lblResultado.setText(mensaje);

        // Cambiar color según el resultado
        if (mensaje.startsWith("OK")) {
            lblResultado.setForeground(new Color(0, 128, 0));  // Verde
        } else {
            lblResultado.setForeground(Color.RED);
        }
    }

    // MÉTODO MAIN
    public static void main(String[] args) {
        // Ejecutar en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            VentanaPedidos ventana = new VentanaPedidos();
            ventana.setVisible(true);
        });
    }
}