import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import Conexion.Conexion;

public class frmmodificar extends javax.swing.JFrame {

    private final Color VERDE_OSCURO  = new Color(26, 58, 31);
    private final Color VERDE_MENU    = new Color(18, 38, 22);
    private final Color VERDE_HOVER   = new Color(35, 80, 42);
    private final Color DORADO        = new Color(201, 162, 39);
    private final Color DORADO_CLARO  = new Color(232, 200, 74);
    private final Color BLANCO        = new Color(255, 255, 255);
    private final Color TEXTO_MENU    = new Color(220, 220, 220);

    Connection con;

    private JTextField txtNombre, txtApellido, txtFecha, txtOcupacion, txtCI, txtBuscarCI;

    public frmmodificar() {
        setTitle("Modificar Datos de Pacientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Conexion c = new Conexion();
        con = c.getConexion();
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        raiz.add(crearEncabezado("MODIFICAR DATOS DE PACIENTES"), BorderLayout.NORTH);
        raiz.add(crearMenu("modificar"), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBackground(VERDE_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setBackground(VERDE_MENU);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblBuscar = new JLabel("BUSCAR POR C.I.:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(DORADO);
        panelBusqueda.add(lblBuscar, BorderLayout.WEST);

        txtBuscarCI = new JTextField();
        txtBuscarCI.setBackground(new Color(240, 240, 235));
        txtBuscarCI.setForeground(new Color(20, 20, 20));
        txtBuscarCI.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarCI.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        // Enter activa búsqueda
        txtBuscarCI.addActionListener(e -> buscarPaciente());
        panelBusqueda.add(txtBuscarCI, BorderLayout.CENTER);

        JButton btnBuscar = crearBotonAccion("BUSCAR", DORADO, new Color(20, 20, 20));
        btnBuscar.addActionListener(e -> buscarPaciente());
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBtn.setBackground(VERDE_MENU);
        panelBtn.add(btnBuscar);
        panelBusqueda.add(panelBtn, BorderLayout.EAST);

        contenido.add(panelBusqueda, BorderLayout.NORTH);

        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setBackground(VERDE_OSCURO);

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(VERDE_MENU);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(20, 30, 30, 30)));
        tarjeta.setPreferredSize(new Dimension(650, 460));

        JLabel lblFormTitulo = new JLabel("DATOS A MODIFICAR");
        lblFormTitulo.setFont(new Font("Georgia", Font.BOLD, 18));
        lblFormTitulo.setForeground(DORADO);
        lblFormTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblFormTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel lineaTarjeta = new JPanel();
        lineaTarjeta.setBackground(DORADO);
        lineaTarjeta.setPreferredSize(new Dimension(0, 1));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(VERDE_MENU);
        norte.add(lblFormTitulo, BorderLayout.CENTER);
        norte.add(lineaTarjeta, BorderLayout.SOUTH);
        tarjeta.add(norte, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(5, 1, 0, 5));
        campos.setBackground(VERDE_MENU);
        campos.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        txtNombre    = new JTextField();
        txtApellido  = new JTextField();
        txtFecha     = new JTextField();
        txtOcupacion = new JTextField();
        // CI: solo lectura, muestra el CI buscado pero no se puede cambiar
        txtCI = new JTextField();
        txtCI.setEditable(false);

        txtNombre.setDocument(limitarTexto(50));
        txtApellido.setDocument(limitarTexto(50));
        txtFecha.setDocument(limitarTexto(10));
        txtOcupacion.setDocument(limitarTexto(50));

        // Deshabilitar hasta que se busque un paciente
        txtNombre.setEnabled(false);    txtNombre.setBackground(new Color(180, 180, 180));
        txtApellido.setEnabled(false);  txtApellido.setBackground(new Color(180, 180, 180));
        txtFecha.setEnabled(false);     txtFecha.setBackground(new Color(180, 180, 180));
        txtOcupacion.setEnabled(false); txtOcupacion.setBackground(new Color(180, 180, 180));
        txtCI.setBackground(new Color(180, 180, 180));

        campos.add(crearCampoConEtiqueta("Nombre",             txtNombre));
        campos.add(crearCampoConEtiqueta("Apellido",           txtApellido));
        campos.add(crearCampoConEtiqueta("Fecha (AAAA-MM-DD)", txtFecha));
        campos.add(crearCampoConEtiqueta("Ocupación",          txtOcupacion));
        campos.add(crearCampoConEtiqueta("C.I. (no editable)", txtCI));

        tarjeta.add(campos, BorderLayout.CENTER);

        JButton btnModificar = crearBotonAccion("MODIFICAR DATOS", DORADO, new Color(20, 20, 20));
        btnModificar.addActionListener(e -> modificarPaciente());

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setBackground(VERDE_MENU);
        sur.add(btnModificar);
        tarjeta.add(sur, BorderLayout.SOUTH);

        centrado.add(tarjeta);
        contenido.add(centrado, BorderLayout.CENTER);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, JTextField txt) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(VERDE_MENU);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(DORADO_CLARO);
        panel.add(lbl, BorderLayout.NORTH);

        txt.setForeground(new Color(20, 20, 20));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(txt, BorderLayout.CENTER);
        return panel;
    }

    private void buscarPaciente() {
        if (txtBuscarCI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un C.I. para buscar",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ci;
        try {
            ci = Integer.parseInt(txtBuscarCI.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El C.I. debe contener solo números",
                "C.I. inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT * FROM paciente WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtNombre.setText(rs.getString("nombre"));
                    txtApellido.setText(rs.getString("apellido"));
                    txtFecha.setText(rs.getDate("fecha").toString());
                    txtOcupacion.setText(rs.getString("ocupacion"));
                    txtCI.setText(String.valueOf(rs.getInt("ci")));

                    // Habilitar campos editables (CI queda bloqueado)
                    txtNombre.setEnabled(true);    txtNombre.setBackground(new Color(240, 240, 235));
                    txtApellido.setEnabled(true);  txtApellido.setBackground(new Color(240, 240, 235));
                    txtFecha.setEnabled(true);     txtFecha.setBackground(new Color(240, 240, 235));
                    txtOcupacion.setEnabled(true); txtOcupacion.setBackground(new Color(240, 240, 235));
                    // CI siempre gris: no se puede cambiar para no romper FK
                    txtCI.setBackground(new Color(180, 180, 180));
                } else {
                    JOptionPane.showMessageDialog(null,
                        "No se encontró ningún paciente con el C.I. " + ci,
                        "No encontrado", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error al buscar el paciente\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarPaciente() {
        if (txtBuscarCI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Primero busque un paciente antes de modificar",
                "Sin paciente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty() ||
            txtFecha.getText().trim().isEmpty()  || txtOcupacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        java.time.LocalDate fechaNac;
        try {
            fechaNac = java.time.LocalDate.parse(txtFecha.getText().trim());
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(null,
                "Formato de fecha incorrecto\nUse el formato: AAAA-MM-DD\nEjemplo: 2000-01-31",
                "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            txtFecha.requestFocus();
            return;
        }
        if (fechaNac.isAfter(java.time.LocalDate.now())) {
            JOptionPane.showMessageDialog(null,
                "La fecha de nacimiento no puede ser una fecha futura",
                "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            txtFecha.requestFocus();
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(null,
            "¿Está seguro que desea modificar los datos del paciente con C.I. " +
            txtBuscarCI.getText().trim() + "?",
            "Confirmar modificación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmar != JOptionPane.YES_OPTION) return;

        // UPDATE sin tocar el CI (es la PK)
        String sql = "UPDATE paciente SET nombre=?, apellido=?, fecha=?, ocupacion=? WHERE ci=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, txtApellido.getText().trim());
            ps.setDate(3, java.sql.Date.valueOf(txtFecha.getText().trim()));
            ps.setString(4, txtOcupacion.getText().trim());
            ps.setInt(5, Integer.parseInt(txtBuscarCI.getText().trim()));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Paciente modificado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el paciente para modificar",
                    "No encontrado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error al modificar el paciente\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");    txtNombre.setEnabled(false);    txtNombre.setBackground(new Color(180, 180, 180));
        txtApellido.setText("");  txtApellido.setEnabled(false);  txtApellido.setBackground(new Color(180, 180, 180));
        txtFecha.setText("");     txtFecha.setEnabled(false);     txtFecha.setBackground(new Color(180, 180, 180));
        txtOcupacion.setText(""); txtOcupacion.setEnabled(false); txtOcupacion.setBackground(new Color(180, 180, 180));
        txtCI.setText("");        txtCI.setBackground(new Color(180, 180, 180));
        txtBuscarCI.setText("");
        txtBuscarCI.requestFocus();
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

    private javax.swing.text.PlainDocument limitarTexto(int limite) {
        return new javax.swing.text.PlainDocument() {
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
                if (str == null || getLength() + str.length() > limite) return;
                super.insertString(offs, str, a);
            }
        };
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