import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;
import Conexion.Conexion;

public class frmmodtratamientos extends javax.swing.JFrame {

    private final Color VERDE_OSCURO  = new Color(26, 58, 31);
    private final Color VERDE_MENU    = new Color(18, 38, 22);
    private final Color VERDE_HOVER   = new Color(35, 80, 42);
    private final Color VERDE_FILA    = new Color(30, 65, 35);
    private final Color DORADO        = new Color(201, 162, 39);
    private final Color DORADO_CLARO  = new Color(232, 200, 74);
    private final Color BLANCO        = new Color(255, 255, 255);
    private final Color TEXTO_MENU    = new Color(220, 220, 220);

    Connection con;
    DefaultTableModel modeloT;
    boolean busquedaRealizada = false;
    boolean ordenAlfabetico  = false;

    private JTable tablaT;
    private JTextField txtID, txtTratamiento;

    public frmmodtratamientos() {
        setTitle("Modificar Tratamientos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Conexion c = new Conexion();
        con = c.getConexion();
        construirUI();
        listarTodosTratamientos();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        raiz.add(crearEncabezado("MODIFICAR TRATAMIENTOS"), BorderLayout.NORTH);
        raiz.add(crearMenu("tratamientos"), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(15, 15));
        contenido.setBackground(VERDE_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel izquierdo - formulario
        JPanel panelFormulario = new JPanel(new BorderLayout());
        panelFormulario.setBackground(VERDE_MENU);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panelFormulario.setPreferredSize(new Dimension(420, 0));

        JLabel lblTitulo = new JLabel("GESTIÓN DE TRATAMIENTOS");
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 16));
        lblTitulo.setForeground(DORADO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel lineaTitulo = new JPanel();
        lineaTitulo.setBackground(DORADO);
        lineaTitulo.setPreferredSize(new Dimension(0, 1));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(VERDE_MENU);
        norte.add(lblTitulo, BorderLayout.CENTER);
        norte.add(lineaTitulo, BorderLayout.SOUTH);
        panelFormulario.add(norte, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(VERDE_MENU);
        campos.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);

        txtID = new JTextField();
        txtTratamiento = new JTextField();
        // Campo nombre habilitado desde el inicio — flujo directo sin doble clic
        txtTratamiento.setEnabled(true);
        txtTratamiento.setBackground(new Color(240, 240, 235));

        gbc.gridy = 0;
        campos.add(crearCampoConEtiqueta("ID del Tratamiento:", txtID), gbc);

        gbc.gridy = 1;
        campos.add(crearCampoConEtiqueta("Nombre del Tratamiento:", txtTratamiento), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        campos.add(new JPanel() {{ setBackground(VERDE_MENU); }}, gbc);

        panelFormulario.add(campos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 10));
        panelBotones.setBackground(VERDE_MENU);

        JButton btnBuscar    = crearBotonAccion("BUSCAR",    new Color(0, 100, 100),  BLANCO);
        JButton btnGuardar   = crearBotonAccion("GUARDAR",   DORADO,                  new Color(20, 20, 20));
        JButton btnModificar = crearBotonAccion("MODIFICAR", new Color(50, 50, 120),  BLANCO);
        JButton btnEliminar  = crearBotonAccion("ELIMINAR",  new Color(120, 20, 20),  BLANCO);

        btnBuscar.addActionListener(e -> buscarTratamiento());
        btnGuardar.addActionListener(e -> guardarTratamiento());
        btnModificar.addActionListener(e -> modificarTratamiento());
        btnEliminar.addActionListener(e -> eliminarTratamiento());

        panelBotones.add(btnBuscar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelFormulario.add(panelBotones, BorderLayout.SOUTH);

        contenido.add(panelFormulario, BorderLayout.WEST);

        // Panel derecho - tabla
        JPanel panelTabla = crearPanelTabla("CATÁLOGO DE TRATAMIENTOS");
        modeloT = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloT.addColumn("ID");
        modeloT.addColumn("Nombre Tratamiento");
        tablaT = crearTabla(modeloT);

        JPanel panelOrden = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelOrden.setBackground(VERDE_MENU);
        JButton btnOrden = crearBotonAccion("⇅  CAMBIAR ORDEN", new Color(60, 60, 60), DORADO_CLARO);
        btnOrden.addActionListener(e -> {
            ordenAlfabetico = !ordenAlfabetico;
            listarTodosTratamientos();
            btnOrden.setText(ordenAlfabetico ? "⇅  ORDEN: A-Z" : "⇅  ORDEN: 1-9");
        });
        panelOrden.add(btnOrden);

        JPanel tablaConOrden = new JPanel(new BorderLayout());
        tablaConOrden.setBackground(VERDE_MENU);
        tablaConOrden.add(panelOrden, BorderLayout.NORTH);
        tablaConOrden.add(new JScrollPane(tablaT), BorderLayout.CENTER);
        panelTabla.add(tablaConOrden, BorderLayout.CENTER);

        contenido.add(panelTabla, BorderLayout.CENTER);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private void listarTodosTratamientos() {
        modeloT.setRowCount(0);
        String orden = ordenAlfabetico ? "nombre_tratamiento ASC" : "id_tratamiento ASC";
        String sql = "SELECT * FROM catalogo_tratamientos ORDER BY " + orden;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloT.addRow(new Object[]{
                    rs.getInt("id_tratamiento"),
                    rs.getString("nombre_tratamiento")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar tratamientos\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarTratamiento() {
        // Rehabilitar ID antes de buscar
        txtID.setEnabled(true);
        txtID.setBackground(new Color(240, 240, 235));
        busquedaRealizada = false;
        if (txtID.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID para buscar",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(txtID.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe contener solo números",
                "ID inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT * FROM catalogo_tratamientos WHERE id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtTratamiento.setText(rs.getString("nombre_tratamiento"));
                    busquedaRealizada = true;
                    // Bloquear ID para no confundir al editar
                    txtID.setEnabled(false);
                    txtID.setBackground(new Color(180, 180, 180));
                    txtTratamiento.setEnabled(true);
                    txtTratamiento.setBackground(new Color(240, 240, 235));
                    txtTratamiento.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null,
                        "No se encontró ningún tratamiento con el ID " + id,
                        "No encontrado", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al buscar\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarTratamiento() {
        if (txtID.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un ID para el nuevo tratamiento",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(txtID.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe contener solo números",
                "ID inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtTratamiento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el nombre del tratamiento",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (busquedaRealizada) {
            JOptionPane.showMessageDialog(null,
                "Ya existe ese tratamiento, use MODIFICAR para cambiar su nombre",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verificar que el ID no exista ya
        String sqlCheck = "SELECT id_tratamiento FROM catalogo_tratamientos WHERE id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null,
                        "Ya existe un tratamiento con ese ID\nUse BUSCAR para modificarlo",
                        "ID duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar el ID\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "INSERT INTO catalogo_tratamientos VALUES(?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, txtTratamiento.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento guardado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listarTodosTratamientos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarTratamiento() {
        if (!busquedaRealizada) {
            JOptionPane.showMessageDialog(null, "Primero busque un tratamiento con el botón BUSCAR",
                "Sin búsqueda previa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtTratamiento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el nuevo nombre del tratamiento",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(txtID.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un número válido",
                "ID inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nuevoNombre = txtTratamiento.getText().trim();

        // Verificar nombre duplicado
        String sqlNombre = "SELECT id_tratamiento FROM catalogo_tratamientos WHERE nombre_tratamiento = ? AND id_tratamiento != ?";
        try (PreparedStatement ps = con.prepareStatement(sqlNombre)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Ya existe un tratamiento con ese nombre",
                        "Nombre duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar el nombre\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE catalogo_tratamientos SET nombre_tratamiento = ? WHERE id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento modificado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listarTodosTratamientos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar el tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTratamiento() {
        if (txtID.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el ID del tratamiento a eliminar",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(txtID.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe contener solo números",
                "ID inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreTrat;
        String sqlCheck = "SELECT nombre_tratamiento FROM catalogo_tratamientos WHERE id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null,
                        "No existe ningún tratamiento con el ID " + id,
                        "No encontrado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nombreTrat = rs.getString("nombre_tratamiento");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar el tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(null,
            "¿Está seguro que desea eliminar el tratamiento?\n\n" +
            "ID: " + id + "\nNombre: " + nombreTrat + "\n\nEsta acción no se puede deshacer",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmar != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM catalogo_tratamientos WHERE id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento eliminado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listarTodosTratamientos();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtID.setText("");
        txtID.setEnabled(true);
        txtID.setBackground(new Color(240, 240, 235));
        txtTratamiento.setText("");
        txtTratamiento.setEnabled(true);
        txtTratamiento.setBackground(new Color(240, 240, 235));
        busquedaRealizada = false;
        txtID.requestFocus();
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, JTextField txt) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(VERDE_MENU);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(DORADO_CLARO);
        panel.add(lbl, BorderLayout.NORTH);
        txt.setForeground(new Color(20, 20, 20));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(0, 35));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(txt, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTabla(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_MENU);
        panel.setBorder(BorderFactory.createLineBorder(DORADO, 1));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Georgia", Font.BOLD, 14));
        lbl.setForeground(DORADO);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        lbl.setBackground(VERDE_MENU);
        lbl.setOpaque(true);
        JPanel lineaTitulo = new JPanel();
        lineaTitulo.setBackground(DORADO);
        lineaTitulo.setPreferredSize(new Dimension(0, 1));
        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(VERDE_MENU);
        norte.add(lbl, BorderLayout.CENTER);
        norte.add(lineaTitulo, BorderLayout.SOUTH);
        panel.add(norte, BorderLayout.NORTH);
        return panel;
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(VERDE_MENU);
        tabla.setForeground(BLANCO);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setGridColor(new Color(40, 80, 45));
        tabla.setSelectionBackground(DORADO);
        tabla.setSelectionForeground(new Color(20, 20, 20));
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.getTableHeader().setBackground(VERDE_OSCURO);
        tabla.getTableHeader().setForeground(DORADO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) { setBackground(DORADO); setForeground(new Color(20, 20, 20)); }
                else { setBackground(row % 2 == 0 ? VERDE_MENU : VERDE_FILA); setForeground(BLANCO); }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
        return tabla;
    }

    private JPanel crearEncabezado(String titulo) {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VERDE_MENU);
        encabezado.setPreferredSize(new Dimension(0, 92));
        JPanel inner = new JPanel(new BorderLayout());
        inner.setBackground(VERDE_MENU);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 26));
        lblTitulo.setForeground(DORADO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0));
        inner.add(lblTitulo, BorderLayout.CENTER);
        JLabel lblLogo;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Imagenes/logo.png"));
            Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            lblLogo = new JLabel(new ImageIcon(img));
        } catch (Exception e) {
            lblLogo = new JLabel("R&R");
            lblLogo.setForeground(DORADO);
        }
        lblLogo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        inner.add(lblLogo, BorderLayout.EAST);
        JPanel linea = new JPanel();
        linea.setBackground(DORADO);
        linea.setPreferredSize(new Dimension(0, 2));
        encabezado.add(inner, BorderLayout.CENTER);
        encabezado.add(linea, BorderLayout.SOUTH);
        return encabezado;
    }

    private JPanel crearMenu(String activo) {
        JPanel menuConLinea = new JPanel(new BorderLayout());
        menuConLinea.setBackground(VERDE_MENU);
        menuConLinea.setPreferredSize(new Dimension(260, 0));
        JPanel panelMenu = new JPanel(new BorderLayout());
        panelMenu.setBackground(VERDE_MENU);
        JPanel botonesPanel = new JPanel(new GridLayout(5, 1, 0, 0));
        botonesPanel.setBackground(VERDE_MENU);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));
        JPanel btnRegistro     = crearBotonMenu("Registro De Pacientes",  activo.equals("registro"));
        JPanel btnListado      = crearBotonMenu("Listado De Pacientes",    activo.equals("listado"));
        JPanel btnTratamientos = crearBotonMenu("Listado De Tratamientos", activo.equals("tratamientos"));
        JPanel btnModificar    = crearBotonMenu("Modificar Datos",         activo.equals("modificar"));
        JPanel btnAsistencias  = crearBotonMenu("Asistencias",            activo.equals("asistencias"));
        botonesPanel.add(btnRegistro);
        botonesPanel.add(btnListado);
        botonesPanel.add(btnTratamientos);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnAsistencias);
        panelMenu.add(botonesPanel, BorderLayout.NORTH);
        if (!activo.equals("registro"))
            btnRegistro.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { new frmpaciente().setVisible(true); dispose(); } });
        if (!activo.equals("listado"))
            btnListado.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { new frmlistado().setVisible(true); dispose(); } });
        if (!activo.equals("tratamientos"))
            btnTratamientos.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { new frmtratamientos().setVisible(true); dispose(); } });
        if (!activo.equals("modificar"))
            btnModificar.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { new frmmodificar().setVisible(true); dispose(); } });
        if (!activo.equals("asistencias"))
            btnAsistencias.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { new frmAsistencias().setVisible(true); dispose(); } });
        JPanel panelVolver = new JPanel(new BorderLayout());
        panelVolver.setBackground(VERDE_MENU);
        panelVolver.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JPanel lineaVolver = new JPanel();
        lineaVolver.setBackground(DORADO);
        lineaVolver.setPreferredSize(new Dimension(0, 1));
        panelVolver.add(lineaVolver, BorderLayout.NORTH);
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setBackground(new Color(120, 20, 20));
        btnVolver.setForeground(BLANCO);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DORADO, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(0, 50));
        btnVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnVolver.setBackground(new Color(160, 30, 30)); }
            public void mouseExited(MouseEvent e)  { btnVolver.setBackground(new Color(120, 20, 20)); }
        });
        btnVolver.addActionListener(e -> { new frmtratamientos().setVisible(true); dispose(); });
        panelVolver.add(btnVolver, BorderLayout.SOUTH);
        panelMenu.add(panelVolver, BorderLayout.SOUTH);
        JPanel lineaDerecha = new JPanel();
        lineaDerecha.setBackground(DORADO);
        lineaDerecha.setPreferredSize(new Dimension(2, 0));
        menuConLinea.add(panelMenu, BorderLayout.CENTER);
        menuConLinea.add(lineaDerecha, BorderLayout.EAST);
        return menuConLinea;
    }

    private JPanel crearBotonMenu(String texto, boolean activo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(activo ? VERDE_HOVER : VERDE_MENU);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(0, 55));
        JPanel indicador = new JPanel();
        indicador.setBackground(DORADO);
        indicador.setPreferredSize(new Dimension(4, 0));
        indicador.setVisible(activo);
        panel.add(indicador, BorderLayout.WEST);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(activo ? DORADO_CLARO : TEXTO_MENU);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        panel.add(lbl, BorderLayout.CENTER);
        if (!activo) {
            panel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { panel.setBackground(VERDE_HOVER); lbl.setForeground(DORADO_CLARO); indicador.setVisible(true); }
                public void mouseExited(MouseEvent e)  { panel.setBackground(VERDE_MENU);  lbl.setForeground(TEXTO_MENU);  indicador.setVisible(false); }
            });
        }
        return panel;
    }

    private JButton crearBotonAccion(String texto, Color fondo, Color fuente) {
        JButton btn = new JButton(texto);
        btn.setBackground(fondo);
        btn.setForeground(fuente);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fondo.darker(), 1), BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(fondo.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(fondo); }
        });
        return btn;
    }

    public static void main(String args[]) {
        try { com.formdev.flatlaf.FlatDarkLaf.setup(); } catch (Exception ex) { ex.printStackTrace(); }
        java.awt.EventQueue.invokeLater(() -> new frmmenu().setVisible(true));
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1240, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 660, Short.MAX_VALUE));
        pack();
    }
}