import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;
import Conexion.Conexion;

public class frmtratamientos extends javax.swing.JFrame {

    private final Color VERDE_OSCURO  = new Color(26, 58, 31);
    private final Color VERDE_MENU    = new Color(18, 38, 22);
    private final Color VERDE_HOVER   = new Color(35, 80, 42);
    private final Color VERDE_FILA    = new Color(30, 65, 35);
    private final Color DORADO        = new Color(201, 162, 39);
    private final Color DORADO_CLARO  = new Color(232, 200, 74);
    private final Color BLANCO        = new Color(255, 255, 255);
    private final Color TEXTO_MENU    = new Color(220, 220, 220);

    Connection con;
    DefaultTableModel modeloTratamientos;
    DefaultTableModel modeloBuscado;

    private JTable tablaTratamientos, tablaBuscado;
    private JTextField txtCI;

    public frmtratamientos() {
        setTitle("Listado de Tratamientos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Conexion c = new Conexion();
        con = Conexion.getConexion();
        construirUI();
        listarTodosTratamientos();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        raiz.add(crearEncabezado("LISTADO DE TRATAMIENTOS"), BorderLayout.NORTH);
        raiz.add(crearMenu("tratamientos"), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBackground(VERDE_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setBackground(VERDE_MENU);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblBuscar = new JLabel("BUSCAR PACIENTE POR C.I.:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(DORADO);
        panelBusqueda.add(lblBuscar, BorderLayout.WEST);

        txtCI = new JTextField();
        txtCI.setBackground(new Color(240, 240, 235));
        txtCI.setForeground(new Color(20, 20, 20));
        txtCI.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCI.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        txtCI.addActionListener(e -> buscarPaciente());
        panelBusqueda.add(txtCI, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonesPanel.setBackground(VERDE_MENU);

        JButton btnBuscar     = crearBotonAccion("BUSCAR PACIENTE",           DORADO,                  new Color(20, 20, 20));
        JButton btnTyP        = crearBotonAccion("TRATAMIENTOS Y PATOLOGÍA",  new Color(0, 100, 100),  BLANCO);
        JButton btnModificarT = crearBotonAccion("MODIFICAR TRATAMIENTOS",    new Color(50, 50, 120),  BLANCO);

        btnBuscar.addActionListener(e -> buscarPaciente());
        btnTyP.addActionListener(e -> abrirTyP());
        btnModificarT.addActionListener(e -> {
            new frmmodtratamientos().setVisible(true);
            dispose();
        });

        botonesPanel.add(btnBuscar);
        botonesPanel.add(btnTyP);
        botonesPanel.add(btnModificarT);
        panelBusqueda.add(botonesPanel, BorderLayout.EAST);
        contenido.add(panelBusqueda, BorderLayout.NORTH);

        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 15, 0));
        panelTablas.setBackground(VERDE_OSCURO);

        JPanel panelTrat = crearPanelTabla("CATÁLOGO DE TRATAMIENTOS");
        modeloTratamientos = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloTratamientos.addColumn("ID");
        modeloTratamientos.addColumn("Nombre Tratamiento");
        tablaTratamientos = crearTabla(modeloTratamientos);
        panelTrat.add(new JScrollPane(tablaTratamientos), BorderLayout.CENTER);
        panelTablas.add(panelTrat);

        JPanel panelBuscado = crearPanelTabla("DATOS DEL PACIENTE");
        modeloBuscado = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloBuscado.addColumn("Nombre");
        modeloBuscado.addColumn("Apellido");
        modeloBuscado.addColumn("Fecha");
        modeloBuscado.addColumn("Ocupación");
        modeloBuscado.addColumn("C.I.");
        tablaBuscado = crearTabla(modeloBuscado);
        panelBuscado.add(new JScrollPane(tablaBuscado), BorderLayout.CENTER);
        panelTablas.add(panelBuscado);

        contenido.add(panelTablas, BorderLayout.CENTER);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private void listarTodosTratamientos() {
        modeloTratamientos.setRowCount(0);
        String sql = "SELECT * FROM catalogo_tratamientos ORDER BY id_tratamiento ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloTratamientos.addRow(new Object[]{
                    rs.getInt("id_tratamiento"),
                    rs.getString("nombre_tratamiento")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar tratamientos\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPaciente() {
        if (txtCI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese un número de C.I. para buscar",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ci;
        try {
            ci = Integer.parseInt(txtCI.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El C.I. debe contener solo números, sin letras ni símbolos",
                "C.I. inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloBuscado.setRowCount(0);
        String sql = "SELECT * FROM paciente WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    modeloBuscado.addRow(new Object[]{
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("fecha"),
                        rs.getString("ocupacion"),
                        rs.getInt("ci")
                    });
                } else {
                    JOptionPane.showMessageDialog(null,
                        "No se encontró ningún paciente con el C.I. " + ci,
                        "Paciente no encontrado", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el paciente\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTyP() {
        if (txtCI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese un número de C.I. para buscar",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ci;
        try {
            ci = Integer.parseInt(txtCI.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El C.I. debe contener solo números, sin letras ni símbolos",
                "C.I. inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT ci FROM paciente WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    new frmtyp(ci).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                        "No se encontró ningún paciente con el C.I. " + ci,
                        "Paciente no encontrado", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el paciente\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    // ── MENÚ SIN LISTENERS DUPLICADOS ──
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
        JButton btnVolver = new JButton("VOLVER AL MENÚ");
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
        btnVolver.addActionListener(e -> { new frmmenu().setVisible(true); dispose(); });
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