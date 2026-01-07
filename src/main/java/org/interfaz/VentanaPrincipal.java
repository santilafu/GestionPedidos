package org.interfaz;

import org.pedidos.GestorClientes;
import org.pedidos.GestorPedidos;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {


    // COLORES DEL DISEÑO
    private static final Color COLOR_FONDO = new Color(18, 18, 24);
    private static final Color COLOR_PANEL = new Color(28, 28, 36);
    private static final Color COLOR_PANEL_CLARO = new Color(38, 38, 48);
    private static final Color COLOR_BORDE = new Color(58, 58, 68);
    private static final Color COLOR_TEXTO = new Color(240, 240, 245);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 160, 175);
    private static final Color COLOR_ACENTO = new Color(99, 102, 241);  // Indigo
    private static final Color COLOR_ACENTO_HOVER = new Color(129, 132, 255);

    // Colores de estado
    private static final Color COLOR_PENDIENTE = new Color(249, 115, 22);   // Naranja
    private static final Color COLOR_ENVIADO = new Color(234, 179, 8);      // Amarillo
    private static final Color COLOR_ENTREGADO = new Color(34, 197, 94);    // Verde
    private static final Color COLOR_CANCELADO = new Color(239, 68, 68);    // Rojo


    // COMPONENTES PRINCIPALES
    private JTable tablaPedidos;
    private DefaultTableModel modeloPedidos;
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;
    private JTextField txtBuscar;
    private JComboBox<String> comboFiltroEstado;
    private JLabel lblTotalPedidos, lblPendientes, lblEnviados, lblEntregados, lblCancelados;

    // Componentes formulario pedidos
    private JComboBox<String> comboClientes;
    private JComboBox<String> comboEstado;
    private JLabel lblResultado;
    private ArrayList<String[]> listaClientes;
    private int pedidoSeleccionadoId = -1;

    // Componentes formulario clientes
    private JTextField txtNombreCliente, txtEmailCliente, txtTelefonoCliente;
    private JLabel lblResultadoCliente;
    private int clienteSeleccionadoId = -1;

    // CONSTRUCTOR
    public VentanaPrincipal() {
        configurarVentana();
        crearInterfaz();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Pedidos");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void crearInterfaz() {
        setLayout(new BorderLayout(0, 0));

        // Header
        add(crearHeader(), BorderLayout.NORTH);

        // Contenido principal con pestañas
        JTabbedPane pestanas = crearPestanas();
        add(pestanas, BorderLayout.CENTER);
    }


    // HEADER
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        header.setPreferredSize(new Dimension(0, 70));

        // Logo y título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panelTitulo.setOpaque(false);

        JLabel iconoApp = new JLabel("📦");
        iconoApp.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        panelTitulo.add(iconoApp);

        JLabel titulo = new JLabel("Gestión de Pedidos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(COLOR_TEXTO);
        panelTitulo.add(titulo);

        header.add(panelTitulo, BorderLayout.WEST);

        // Panel de estadísticas rápidas
        JPanel panelStats = crearPanelEstadisticasHeader();
        header.add(panelStats, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelEstadisticasHeader() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panel.setOpaque(false);

        lblTotalPedidos = crearStatLabel("Total", "0", COLOR_ACENTO);
        lblPendientes = crearStatLabel("Pendientes", "0", COLOR_PENDIENTE);
        lblEnviados = crearStatLabel("Enviados", "0", COLOR_ENVIADO);
        lblEntregados = crearStatLabel("Entregados", "0", COLOR_ENTREGADO);
        lblCancelados = crearStatLabel("Cancelados", "0", COLOR_CANCELADO);

        panel.add(lblTotalPedidos);
        panel.add(crearSeparadorVertical());
        panel.add(lblPendientes);
        panel.add(lblEnviados);
        panel.add(lblEntregados);
        panel.add(lblCancelados);

        return panel;
    }

    private JLabel crearStatLabel(String titulo, String valor, Color color) {
        JLabel label = new JLabel("<html><center><span style='font-size:9px;color:#888;'>" + titulo +
                "</span><br><span style='font-size:14px;color:" + colorToHex(color) + ";'>" +
                valor + "</span></center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return label;
    }

    private JSeparator crearSeparadorVertical() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 40));
        sep.setForeground(COLOR_BORDE);
        return sep;
    }


    // PESTAÑAS
    private JTabbedPane crearPestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setBackground(COLOR_FONDO);
        pestanas.setForeground(COLOR_TEXTO);
        pestanas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Personalizar apariencia de pestañas
        pestanas.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = COLOR_ACENTO;
                lightHighlight = COLOR_ACENTO;
                shadow = COLOR_BORDE;
                darkShadow = COLOR_BORDE;
                focus = COLOR_ACENTO;
            }
        });

        pestanas.addTab("  📋 Pedidos  ", crearPanelPedidos());
        pestanas.addTab("  👥 Clientes  ", crearPanelClientes());

        return pestanas;
    }


    // PANEL DE PEDIDOS
    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Barra superior con búsqueda y filtros
        panel.add(crearBarraBusquedaPedidos(), BorderLayout.NORTH);

        // Contenido principal: tabla + formulario
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelTablaPedidos(),
                crearPanelFormularioPedido());
        splitPane.setDividerLocation(750);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_FONDO);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearBarraBusquedaPedidos() {
        JPanel barra = new JPanel(new BorderLayout(15, 0));
        barra.setBackground(COLOR_PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        // Búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda.setOpaque(false);

        JLabel iconoBuscar = new JLabel("🔍");
        iconoBuscar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        panelBusqueda.add(iconoBuscar);

        txtBuscar = crearCampoTexto("Buscar por cliente o ID...", 250);
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarPedidos();
            }
        });
        panelBusqueda.add(txtBuscar);

        barra.add(panelBusqueda, BorderLayout.WEST);

        // Filtro por estado
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelFiltros.setOpaque(false);

        JLabel lblFiltro = new JLabel("Estado:");
        lblFiltro.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelFiltros.add(lblFiltro);

        comboFiltroEstado = new JComboBox<>(new String[]{"Todos", "PENDIENTE", "ENVIADO", "ENTREGADO", "CANCELADO"});
        estilizarComboBox(comboFiltroEstado);
        comboFiltroEstado.addActionListener(e -> filtrarPedidos());
        panelFiltros.add(comboFiltroEstado);

        JButton btnRefrescar = crearBotonIcono("🔄", "Refrescar", COLOR_PANEL_CLARO);
        btnRefrescar.addActionListener(e -> cargarDatos());
        panelFiltros.add(btnRefrescar);

        barra.add(panelFiltros, BorderLayout.EAST);

        return barra;
    }

    private JPanel crearPanelTablaPedidos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // Crear tabla
        String[] columnas = {"ID", "Cliente", "Email", "Teléfono", "Fecha", "Estado"};
        modeloPedidos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedidos = new JTable(modeloPedidos);
        estilizarTabla(tablaPedidos);

        // Renderer personalizado para colores de estado
        tablaPedidos.getColumnModel().getColumn(5).setCellRenderer(new EstadoCellRenderer());

        // Ajustar anchos de columna
        tablaPedidos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaPedidos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaPedidos.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaPedidos.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaPedidos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaPedidos.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Listener de selección
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaPedidos.getSelectedRow();
                if (fila >= 0) {
                    pedidoSeleccionadoId = Integer.parseInt(modeloPedidos.getValueAt(fila, 0).toString());
                    String estado = modeloPedidos.getValueAt(fila, 5).toString();
                    comboEstado.setSelectedItem(estado);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaPedidos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_PANEL);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelFormularioPedido() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Título
        JLabel titulo = new JLabel("Gestionar Pedido");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(20));

        // Nuevo pedido
        JLabel lblNuevo = new JLabel("Crear nuevo pedido");
        lblNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNuevo.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblNuevo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblNuevo);
        panel.add(Box.createVerticalStrut(10));

        JLabel lblCliente = new JLabel("Seleccionar cliente:");
        lblCliente.setForeground(COLOR_TEXTO);
        lblCliente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblCliente);
        panel.add(Box.createVerticalStrut(5));

        comboClientes = new JComboBox<>();
        estilizarComboBox(comboClientes);
        comboClientes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboClientes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(comboClientes);
        panel.add(Box.createVerticalStrut(15));

        JButton btnNuevoPedido = crearBoton("➕  Crear Pedido", COLOR_ACENTO);
        btnNuevoPedido.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNuevoPedido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnNuevoPedido.addActionListener(e -> crearPedido());
        panel.add(btnNuevoPedido);

        panel.add(Box.createVerticalStrut(30));
        panel.add(crearSeparadorHorizontal());
        panel.add(Box.createVerticalStrut(20));

        // Actualizar estado
        JLabel lblActualizar = new JLabel("Actualizar pedido seleccionado");
        lblActualizar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblActualizar.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblActualizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblActualizar);
        panel.add(Box.createVerticalStrut(10));

        JLabel lblEstado = new JLabel("Nuevo estado:");
        lblEstado.setForeground(COLOR_TEXTO);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblEstado);
        panel.add(Box.createVerticalStrut(5));

        comboEstado = new JComboBox<>(new String[]{"PENDIENTE", "ENVIADO", "ENTREGADO", "CANCELADO"});
        estilizarComboBox(comboEstado);
        comboEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(comboEstado);
        panel.add(Box.createVerticalStrut(15));

        JPanel panelBotonesEstado = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotonesEstado.setOpaque(false);
        panelBotonesEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotonesEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton btnActualizar = crearBoton("✏️  Actualizar", COLOR_ENVIADO);
        btnActualizar.addActionListener(e -> actualizarPedido());
        panelBotonesEstado.add(btnActualizar);

        JButton btnEliminar = crearBoton("🗑️  Eliminar", COLOR_CANCELADO);
        btnEliminar.addActionListener(e -> eliminarPedido());
        panelBotonesEstado.add(btnEliminar);

        panel.add(panelBotonesEstado);

        panel.add(Box.createVerticalStrut(20));

        // Label resultado
        lblResultado = new JLabel(" ");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblResultado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblResultado);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // PANEL DE CLIENTES
    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Contenido principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelTablaClientes(),
                crearPanelFormularioCliente());
        splitPane.setDividerLocation(750);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_FONDO);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelTablaClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        String[] columnas = {"ID", "Nombre", "Email", "Teléfono"};
        modeloClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloClientes);
        estilizarTabla(tablaClientes);

        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(120);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    clienteSeleccionadoId = Integer.parseInt(modeloClientes.getValueAt(fila, 0).toString());
                    txtNombreCliente.setText(modeloClientes.getValueAt(fila, 1).toString());
                    txtEmailCliente.setText(modeloClientes.getValueAt(fila, 2).toString());
                    txtTelefonoCliente.setText(modeloClientes.getValueAt(fila, 3).toString());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_PANEL);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelFormularioCliente() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titulo = new JLabel("Gestionar Cliente");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(20));

        // Campos
        panel.add(crearLabelCampo("Nombre *"));
        txtNombreCliente = crearCampoTexto("Nombre completo", 0);
        txtNombreCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtNombreCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtNombreCliente);
        panel.add(Box.createVerticalStrut(15));

        panel.add(crearLabelCampo("Email"));
        txtEmailCliente = crearCampoTexto("correo@ejemplo.com", 0);
        txtEmailCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtEmailCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtEmailCliente);
        panel.add(Box.createVerticalStrut(15));

        panel.add(crearLabelCampo("Teléfono"));
        txtTelefonoCliente = crearCampoTexto("666 123 456", 0);
        txtTelefonoCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtTelefonoCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtTelefonoCliente);
        panel.add(Box.createVerticalStrut(25));

        // Botones
        JButton btnNuevo = crearBoton("➕  Nuevo Cliente", COLOR_ACENTO);
        btnNuevo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNuevo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnNuevo.addActionListener(e -> crearCliente());
        panel.add(btnNuevo);
        panel.add(Box.createVerticalStrut(10));

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton btnActualizar = crearBoton("✏️  Actualizar", COLOR_ENVIADO);
        btnActualizar.addActionListener(e -> actualizarCliente());
        panelBotones.add(btnActualizar);

        JButton btnEliminar = crearBoton("🗑️  Eliminar", COLOR_CANCELADO);
        btnEliminar.addActionListener(e -> eliminarCliente());
        panelBotones.add(btnEliminar);

        panel.add(panelBotones);
        panel.add(Box.createVerticalStrut(10));

        JButton btnLimpiar = crearBoton("🔄  Limpiar campos", COLOR_PANEL_CLARO);
        btnLimpiar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLimpiar.addActionListener(e -> limpiarCamposCliente());
        panel.add(btnLimpiar);

        panel.add(Box.createVerticalStrut(20));

        lblResultadoCliente = new JLabel(" ");
        lblResultadoCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblResultadoCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblResultadoCliente);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel crearLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(COLOR_TEXTO);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // ==========================================
    // COMPONENTES ESTILIZADOS
    // ==========================================
    private JTextField crearCampoTexto(String placeholder, int ancho) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(COLOR_PANEL_CLARO);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        if (ancho > 0) {
            campo.setPreferredSize(new Dimension(ancho, 38));
        }

        // Placeholder
        campo.setText(placeholder);
        campo.setForeground(COLOR_TEXTO_SECUNDARIO);
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(COLOR_TEXTO);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(COLOR_TEXTO_SECUNDARIO);
                }
            }
        });

        return campo;
    }

    private String getTextoReal(JTextField campo, String placeholder) {
        String texto = campo.getText().trim();
        return texto.equals(placeholder) ? "" : texto;
    }

    private void estilizarComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(COLOR_PANEL_CLARO);
        combo.setForeground(COLOR_TEXTO);
        combo.setBorder(new LineBorder(COLOR_BORDE, 1, true));
        combo.setPreferredSize(new Dimension(150, 38));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? COLOR_ACENTO : COLOR_PANEL_CLARO);
                setForeground(COLOR_TEXTO);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private JButton crearBotonIcono(String icono, String tooltip, Color color) {
        JButton boton = new JButton(icono);
        boton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        boton.setBackground(color);
        boton.setForeground(COLOR_TEXTO);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setToolTipText(tooltip);

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_BORDE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setBackground(COLOR_PANEL);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setRowHeight(45);
        tabla.setSelectionBackground(COLOR_ACENTO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(COLOR_PANEL_CLARO);
        header.setForeground(COLOR_TEXTO);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDE));
        header.setPreferredSize(new Dimension(0, 45));
    }

    private JSeparator crearSeparadorHorizontal() {
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setForeground(COLOR_BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // RENDERER PARA ESTADOS CON COLOR
    private class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);

            String estado = value != null ? value.toString() : "";
            Color colorFondo;

            switch (estado) {
                case "PENDIENTE":
                    colorFondo = COLOR_PENDIENTE;
                    break;
                case "ENVIADO":
                    colorFondo = COLOR_ENVIADO;
                    break;
                case "ENTREGADO":
                    colorFondo = COLOR_ENTREGADO;
                    break;
                case "CANCELADO":
                    colorFondo = COLOR_CANCELADO;
                    break;
                default:
                    colorFondo = COLOR_PANEL_CLARO;
            }

            if (isSelected) {
                label.setBackground(COLOR_ACENTO);
            } else {
                label.setBackground(colorFondo);
            }
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            return label;
        }
    }

    // ==========================================
    // LÓGICA DE NEGOCIO
    // ==========================================
    private void cargarDatos() {
        cargarPedidos();
        cargarClientes();
        cargarComboClientes();
        actualizarEstadisticas();
    }

    private void cargarPedidos() {
        modeloPedidos.setRowCount(0);
        ArrayList<String[]> pedidos = GestorPedidos.listarPedidosCompleto();
        for (String[] fila : pedidos) {
            modeloPedidos.addRow(fila);
        }
    }

    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        ArrayList<String[]> clientes = GestorClientes.listarClientes();
        for (String[] fila : clientes) {
            modeloClientes.addRow(fila);
        }
    }

    private void cargarComboClientes() {
        comboClientes.removeAllItems();
        listaClientes = GestorClientes.obtenerClientesCombo();
        for (String[] cliente : listaClientes) {
            comboClientes.addItem(cliente[1]);
        }
    }

    private void actualizarEstadisticas() {
        int total = 0, pendientes = 0, enviados = 0, entregados = 0, cancelados = 0;

        for (int i = 0; i < modeloPedidos.getRowCount(); i++) {
            total++;
            String estado = modeloPedidos.getValueAt(i, 5).toString();
            switch (estado) {
                case "PENDIENTE": pendientes++; break;
                case "ENVIADO": enviados++; break;
                case "ENTREGADO": entregados++; break;
                case "CANCELADO": cancelados++; break;
            }
        }

        actualizarStatLabel(lblTotalPedidos, "Total", String.valueOf(total), COLOR_ACENTO);
        actualizarStatLabel(lblPendientes, "Pendientes", String.valueOf(pendientes), COLOR_PENDIENTE);
        actualizarStatLabel(lblEnviados, "Enviados", String.valueOf(enviados), COLOR_ENVIADO);
        actualizarStatLabel(lblEntregados, "Entregados", String.valueOf(entregados), COLOR_ENTREGADO);
        actualizarStatLabel(lblCancelados, "Cancelados", String.valueOf(cancelados), COLOR_CANCELADO);
    }

    private void actualizarStatLabel(JLabel label, String titulo, String valor, Color color) {
        label.setText("<html><center><span style='font-size:9px;color:#888;'>" + titulo +
                "</span><br><span style='font-size:14px;color:" + colorToHex(color) + ";'>" +
                valor + "</span></center></html>");
    }

    private void filtrarPedidos() {
        String busqueda = getTextoReal(txtBuscar, "Buscar por cliente o ID...").toLowerCase();
        String filtroEstado = comboFiltroEstado.getSelectedItem().toString();

        modeloPedidos.setRowCount(0);
        ArrayList<String[]> pedidos = GestorPedidos.listarPedidosCompleto();

        for (String[] fila : pedidos) {
            boolean coincideBusqueda = busqueda.isEmpty() ||
                    fila[0].toLowerCase().contains(busqueda) ||
                    fila[1].toLowerCase().contains(busqueda);

            boolean coincideEstado = filtroEstado.equals("Todos") ||
                    fila[5].equals(filtroEstado);

            if (coincideBusqueda && coincideEstado) {
                modeloPedidos.addRow(fila);
            }
        }

        actualizarEstadisticas();
    }

    // Acciones de pedidos
    private void crearPedido() {
        if (comboClientes.getSelectedIndex() < 0 || listaClientes.isEmpty()) {
            mostrarResultado(lblResultado, "Selecciona un cliente", false);
            return;
        }

        int idCliente = Integer.parseInt(listaClientes.get(comboClientes.getSelectedIndex())[0]);
        String resultado = GestorPedidos.insertarPedido(idCliente, 999);
        mostrarResultado(lblResultado, resultado, resultado.startsWith("OK"));
        cargarDatos();
    }

    private void actualizarPedido() {
        if (pedidoSeleccionadoId < 0) {
            mostrarResultado(lblResultado, "Selecciona un pedido de la tabla", false);
            return;
        }

        String estado = comboEstado.getSelectedItem().toString();
        String resultado = GestorPedidos.actualizarPedido(pedidoSeleccionadoId, estado);
        mostrarResultado(lblResultado, resultado, resultado.startsWith("OK"));
        cargarDatos();
    }

    private void eliminarPedido() {
        if (pedidoSeleccionadoId < 0) {
            mostrarResultado(lblResultado, "Selecciona un pedido de la tabla", false);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el pedido #" + pedidoSeleccionadoId + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            String resultado = GestorPedidos.eliminarPedido(pedidoSeleccionadoId);
            mostrarResultado(lblResultado, resultado, resultado.startsWith("OK"));
            pedidoSeleccionadoId = -1;
            cargarDatos();
        }
    }

    // Acciones de clientes
    private void crearCliente() {
        String nombre = getTextoReal(txtNombreCliente, "Nombre completo");
        String email = getTextoReal(txtEmailCliente, "correo@ejemplo.com");
        String telefono = getTextoReal(txtTelefonoCliente, "666 123 456");

        if (nombre.isEmpty()) {
            mostrarResultado(lblResultadoCliente, "El nombre es obligatorio", false);
            return;
        }

        String resultado = GestorClientes.insertarCliente(nombre, email, telefono);
        mostrarResultado(lblResultadoCliente, resultado, resultado.startsWith("OK"));
        if (resultado.startsWith("OK")) {
            limpiarCamposCliente();
            cargarDatos();
        }
    }

    private void actualizarCliente() {
        if (clienteSeleccionadoId < 0) {
            mostrarResultado(lblResultadoCliente, "Selecciona un cliente de la tabla", false);
            return;
        }

        String nombre = getTextoReal(txtNombreCliente, "Nombre completo");
        String email = getTextoReal(txtEmailCliente, "correo@ejemplo.com");
        String telefono = getTextoReal(txtTelefonoCliente, "666 123 456");

        String resultado = GestorClientes.actualizarCliente(clienteSeleccionadoId, nombre, email, telefono);
        mostrarResultado(lblResultadoCliente, resultado, resultado.startsWith("OK"));
        cargarDatos();
    }

    private void eliminarCliente() {
        if (clienteSeleccionadoId < 0) {
            mostrarResultado(lblResultadoCliente, "Selecciona un cliente de la tabla", false);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este cliente?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            String resultado = GestorClientes.eliminarCliente(clienteSeleccionadoId);
            mostrarResultado(lblResultadoCliente, resultado, resultado.startsWith("OK"));
            if (resultado.startsWith("OK")) {
                limpiarCamposCliente();
                clienteSeleccionadoId = -1;
                cargarDatos();
            }
        }
    }

    private void limpiarCamposCliente() {
        txtNombreCliente.setText("Nombre completo");
        txtNombreCliente.setForeground(COLOR_TEXTO_SECUNDARIO);
        txtEmailCliente.setText("correo@ejemplo.com");
        txtEmailCliente.setForeground(COLOR_TEXTO_SECUNDARIO);
        txtTelefonoCliente.setText("666 123 456");
        txtTelefonoCliente.setForeground(COLOR_TEXTO_SECUNDARIO);
        tablaClientes.clearSelection();
        clienteSeleccionadoId = -1;
    }

    private void mostrarResultado(JLabel label, String mensaje, boolean exito) {
        label.setText(mensaje);
        label.setForeground(exito ? COLOR_ENTREGADO : COLOR_CANCELADO);
    }

    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    // ==========================================
    // MAIN
    // ==========================================
    public static void main(String[] args) {
        // Usar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}