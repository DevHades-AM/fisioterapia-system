import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import Conexion.Conexion;

public class frmAsistencias extends javax.swing.JFrame {

    private final Color VERDE_OSCURO   = new Color(26, 58, 31);
    private final Color VERDE_MENU     = new Color(18, 38, 22);
    private final Color VERDE_HOVER    = new Color(35, 80, 42);
    private final Color VERDE_FILA     = new Color(30, 65, 35);
    private final Color DORADO         = new Color(201, 162, 39);
    private final Color DORADO_CLARO   = new Color(232, 200, 74);
    private final Color BLANCO         = new Color(255, 255, 255);
    private final Color TEXTO_MENU     = new Color(220, 220, 220);
    private final Color COLOR_ASISTIO  = new Color(0, 140, 0);
    private final Color COLOR_FALTO    = new Color(160, 0, 0);
    private final Color COLOR_SIN_MARCAR = new Color(40, 80, 45);

    Connection con;
    int ciPaciente = -1;
    DefaultTableModel modeloPaciente;

    private JTable JTableDatos;
    private JTextField txtCI;
    private JPanel panelCalendario;
    private JLabel lblMesAnio;
    private JLabel lblAsistio, lblFalto;
    private YearMonth mesActual;
    private JButton[][] botonesCalendario;
    private JLabel lblResumenTitulo;

    public frmAsistencias() {
        setTitle("Registro de Asistencias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Conexion c = new Conexion();
        con = c.getConexion();
        mesActual = YearMonth.now();
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        raiz.add(crearEncabezado("REGISTRO DE ASISTENCIAS"), BorderLayout.NORTH);
        raiz.add(crearMenu("asistencias"), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBackground(VERDE_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(0, 8));
        panelBusqueda.setBackground(VERDE_MENU);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JPanel filaCampo = new JPanel(new BorderLayout(10, 0));
        filaCampo.setBackground(VERDE_MENU);

        JLabel lblBuscar = new JLabel("BUSCAR PACIENTE POR C.I.:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(DORADO);
        lblBuscar.setPreferredSize(new Dimension(230, 35));
        filaCampo.add(lblBuscar, BorderLayout.WEST);

        txtCI = new JTextField();
        txtCI.setBackground(new Color(240, 240, 235));
        txtCI.setForeground(new Color(20, 20, 20));
        txtCI.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCI.setPreferredSize(new Dimension(0, 35));
        txtCI.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        txtCI.addActionListener(e -> buscarPaciente());
        filaCampo.add(txtCI, BorderLayout.CENTER);
        panelBusqueda.add(filaCampo, BorderLayout.NORTH);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaBotones.setBackground(VERDE_MENU);

        JButton btnBuscar   = crearBotonAccion("BUSCAR", DORADO, new Color(20, 20, 20));
        JButton btnHistorial = crearBotonAccion("VER HISTORIAL", new Color(0, 100, 100), BLANCO);
        btnBuscar.addActionListener(e -> buscarPaciente());
        btnHistorial.addActionListener(e -> verHistorial());

        filaBotones.add(btnBuscar);
        filaBotones.add(btnHistorial);
        panelBusqueda.add(filaBotones, BorderLayout.SOUTH);
        contenido.add(panelBusqueda, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setBackground(VERDE_OSCURO);

        // Tabla datos paciente
        JPanel panelDatos = crearPanelTabla("DATOS DEL PACIENTE");
        JTableDatos = crearTabla();
        modeloPaciente = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloPaciente.addColumn("C.I.");
        modeloPaciente.addColumn("Nombre");
        modeloPaciente.addColumn("Apellido");
        modeloPaciente.addColumn("Ocupación");
        JTableDatos.setModel(modeloPaciente);
        panelDatos.add(new JScrollPane(JTableDatos), BorderLayout.CENTER);
        panelDatos.setPreferredSize(new Dimension(0, 100));
        panelCentral.add(panelDatos, BorderLayout.NORTH);

        // Panel inferior - calendario + resumen
        JPanel panelInferior = new JPanel(new GridLayout(1, 2, 15, 0));
        panelInferior.setBackground(VERDE_OSCURO);

        // Panel calendario
        JPanel panelCalendarioWrapper = new JPanel(new BorderLayout());
        panelCalendarioWrapper.setBackground(VERDE_MENU);
        panelCalendarioWrapper.setBorder(BorderFactory.createLineBorder(DORADO, 1));

        JLabel lblCalTitulo = new JLabel("CALENDARIO DE ASISTENCIAS");
        lblCalTitulo.setFont(new Font("Georgia", Font.BOLD, 14));
        lblCalTitulo.setForeground(DORADO);
        lblCalTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblCalTitulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        lblCalTitulo.setBackground(VERDE_MENU);
        lblCalTitulo.setOpaque(true);

        JPanel lineaCalTitulo = new JPanel();
        lineaCalTitulo.setBackground(DORADO);
        lineaCalTitulo.setPreferredSize(new Dimension(0, 1));

        JPanel norteCalTitulo = new JPanel(new BorderLayout());
        norteCalTitulo.setBackground(VERDE_MENU);
        norteCalTitulo.add(lblCalTitulo, BorderLayout.CENTER);
        norteCalTitulo.add(lineaCalTitulo, BorderLayout.SOUTH);

        // Navegación mes
        JPanel panelNav = new JPanel(new BorderLayout());
        panelNav.setBackground(VERDE_MENU);
        panelNav.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JButton btnAnterior  = crearBotonAccion("<", new Color(50, 50, 120), BLANCO);
        JButton btnSiguiente = crearBotonAccion(">", new Color(50, 50, 120), BLANCO);
        btnAnterior.setPreferredSize(new Dimension(50, 30));
        btnSiguiente.setPreferredSize(new Dimension(50, 30));

        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Georgia", Font.BOLD, 16));
        lblMesAnio.setForeground(DORADO_CLARO);

        btnAnterior.addActionListener(e -> {
            if (mesActual.isAfter(YearMonth.now().minusMonths(12))) {
                mesActual = mesActual.minusMonths(1);
                actualizarCalendario();
            } else {
                JOptionPane.showMessageDialog(null,
                    "No puede ver asistencias de más de 12 meses atrás",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnSiguiente.addActionListener(e -> {
            if (mesActual.isBefore(YearMonth.now())) {
                mesActual = mesActual.plusMonths(1);
                actualizarCalendario();
            } else {
                JOptionPane.showMessageDialog(null,
                    "No puede ver asistencias de meses futuros",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            }
        });

        panelNav.add(btnAnterior, BorderLayout.WEST);
        panelNav.add(lblMesAnio, BorderLayout.CENTER);
        panelNav.add(btnSiguiente, BorderLayout.EAST);

        // Días de la semana
        JPanel panelDiasSemana = new JPanel(new GridLayout(1, 7, 2, 2));
        panelDiasSemana.setBackground(VERDE_MENU);
        panelDiasSemana.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        String[] diasSemana = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (String dia : diasSemana) {
            JLabel lblDia = new JLabel(dia, SwingConstants.CENTER);
            lblDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblDia.setForeground(DORADO);
            panelDiasSemana.add(lblDia);
        }

        panelCalendario = new JPanel(new GridLayout(6, 7, 3, 3));
        panelCalendario.setBackground(VERDE_OSCURO);
        panelCalendario.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        botonesCalendario = new JButton[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setForeground(BLANCO);
                btn.setBackground(COLOR_SIN_MARCAR);
                btn.setBorder(BorderFactory.createLineBorder(new Color(40, 80, 45), 1));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                botonesCalendario[i][j] = btn;
                panelCalendario.add(btn);
            }
        }

        JPanel norteCalendario = new JPanel(new BorderLayout());
        norteCalendario.setBackground(VERDE_MENU);
        norteCalendario.add(norteCalTitulo, BorderLayout.NORTH);
        norteCalendario.add(panelNav, BorderLayout.CENTER);
        norteCalendario.add(panelDiasSemana, BorderLayout.SOUTH);

        panelCalendarioWrapper.add(norteCalendario, BorderLayout.NORTH);
        panelCalendarioWrapper.add(panelCalendario, BorderLayout.CENTER);

        JButton btnGuardar    = crearBotonAccion("GUARDAR ASISTENCIAS", DORADO, new Color(20, 20, 20));
        JButton btnImprimirCal = crearBotonAccion("IMPRIMIR MES", new Color(50, 50, 120), BLANCO);
        btnGuardar.addActionListener(e -> guardarAsistencias());
        btnImprimirCal.addActionListener(e -> imprimirCalendario());

        JPanel panelGuardar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelGuardar.setBackground(VERDE_MENU);
        panelGuardar.add(btnGuardar);
        panelGuardar.add(btnImprimirCal);
        panelCalendarioWrapper.add(panelGuardar, BorderLayout.SOUTH);
        panelInferior.add(panelCalendarioWrapper);

        // Panel resumen
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setBackground(VERDE_MENU);
        panelResumen.setBorder(BorderFactory.createLineBorder(DORADO, 1));

        lblResumenTitulo = new JLabel("RESUMEN DEL MES");
        lblResumenTitulo.setFont(new Font("Georgia", Font.BOLD, 14));
        lblResumenTitulo.setForeground(DORADO);
        lblResumenTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblResumenTitulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        lblResumenTitulo.setBackground(VERDE_MENU);
        lblResumenTitulo.setOpaque(true);

        JPanel lineaResumen = new JPanel();
        lineaResumen.setBackground(DORADO);
        lineaResumen.setPreferredSize(new Dimension(0, 1));

        JPanel norteResumen = new JPanel(new BorderLayout());
        norteResumen.setBackground(VERDE_MENU);
        norteResumen.add(lblResumenTitulo, BorderLayout.CENTER);
        norteResumen.add(lineaResumen, BorderLayout.SOUTH);
        panelResumen.add(norteResumen, BorderLayout.NORTH);

        JPanel contenidoResumen = new JPanel(new GridBagLayout());
        contenidoResumen.setBackground(VERDE_MENU);
        contenidoResumen.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(10, 0, 10, 0);

        gbc.gridy = 0;
        contenidoResumen.add(crearItemResumen("Asistió:", COLOR_ASISTIO), gbc);
        lblAsistio = new JLabel("0 días", SwingConstants.CENTER);
        lblAsistio.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAsistio.setForeground(COLOR_ASISTIO);
        gbc.gridy = 1;
        contenidoResumen.add(lblAsistio, gbc);

        gbc.gridy = 2;
        contenidoResumen.add(crearItemResumen("Faltó:", COLOR_FALTO), gbc);
        lblFalto = new JLabel("0 días", SwingConstants.CENTER);
        lblFalto.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblFalto.setForeground(COLOR_FALTO);
        gbc.gridy = 3;
        contenidoResumen.add(lblFalto, gbc);

        gbc.gridy = 4;
        JPanel separador = new JPanel();
        separador.setBackground(DORADO);
        separador.setPreferredSize(new Dimension(0, 1));
        contenidoResumen.add(separador, gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel panelInstrucciones = new JPanel(new GridLayout(4, 1, 0, 5));
        panelInstrucciones.setBackground(VERDE_MENU);
        panelInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblInstr = new JLabel("INSTRUCCIONES:", SwingConstants.CENTER);
        lblInstr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInstr.setForeground(DORADO_CLARO);
        panelInstrucciones.add(lblInstr);

        String[] instrucciones = {"1 clic → Asistió (Verde)", "2 clics → Faltó (Rojo)", "3 clics → Sin marcar"};
        Color[]  coloresInstr  = {COLOR_ASISTIO, COLOR_FALTO, TEXTO_MENU};
        for (int i = 0; i < instrucciones.length; i++) {
            JLabel lbl = new JLabel(instrucciones[i], SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(coloresInstr[i]);
            panelInstrucciones.add(lbl);
        }
        contenidoResumen.add(panelInstrucciones, gbc);

        panelResumen.add(contenidoResumen, BorderLayout.CENTER);
        panelInferior.add(panelResumen);

        panelCentral.add(panelInferior, BorderLayout.CENTER);
        contenido.add(panelCentral, BorderLayout.CENTER);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);

        actualizarCalendario();
    }

    private JPanel crearItemResumen(String texto, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(VERDE_MENU);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(color);
        panel.add(lbl);
        return panel;
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
            JOptionPane.showMessageDialog(null, "El C.I. debe contener solo números",
                "C.I. inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT * FROM paciente WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    modeloPaciente.setRowCount(0);
                    modeloPaciente.addRow(new Object[]{
                        rs.getInt("ci"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("ocupacion")
                    });
                    ciPaciente = rs.getInt("ci");
                    actualizarCalendario();
                } else {
                    JOptionPane.showMessageDialog(null,
                        "No se encontró ningún paciente con el C.I. " + ci,
                        "Paciente no encontrado", JOptionPane.WARNING_MESSAGE);
                    ciPaciente = -1;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el paciente\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCalendario() {
        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
        lblMesAnio.setText(nombreMes + " " + mesActual.getYear());

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                botonesCalendario[i][j].setText("");
                botonesCalendario[i][j].setBackground(new Color(25, 40, 27));
                botonesCalendario[i][j].setEnabled(false);
                for (ActionListener al : botonesCalendario[i][j].getActionListeners())
                    botonesCalendario[i][j].removeActionListener(al);
            }
        }

        LocalDate primerDia = mesActual.atDay(1);
        int diaSemana = primerDia.getDayOfWeek().getValue() - 1;
        int diasEnMes = mesActual.lengthOfMonth();

        // Cargar asistencias con RS/PS cerrados
        java.util.Map<Integer, String> asistencias = new java.util.HashMap<>();
        if (ciPaciente != -1) {
            String sql = "SELECT DAY(fecha), estado FROM asistencias " +
                         "WHERE ci_paciente = ? AND YEAR(fecha) = ? AND MONTH(fecha) = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, ciPaciente);
                ps.setInt(2, mesActual.getYear());
                ps.setInt(3, mesActual.getMonthValue());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) asistencias.put(rs.getInt(1), rs.getString(2));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cargar asistencias\nContacte al administrador",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        LocalDate hoy = LocalDate.now();
        int dia = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j < diaSemana) continue;
                if (dia > diasEnMes) break;

                final int diaFinal = dia;
                JButton btn = botonesCalendario[i][j];
                btn.setText(String.valueOf(dia));
                LocalDate fechaBoton = mesActual.atDay(dia);

                if (fechaBoton.isAfter(hoy)) {
                    btn.setEnabled(false);
                    btn.setBackground(new Color(25, 40, 27));
                    btn.setForeground(new Color(60, 80, 62));
                    dia++;
                    continue;
                }

                if (fechaBoton.isEqual(hoy))
                    btn.setBorder(BorderFactory.createLineBorder(DORADO, 2));
                else
                    btn.setBorder(BorderFactory.createLineBorder(new Color(40, 80, 45), 1));

                btn.setEnabled(true);
                asignarColorBoton(btn, asistencias.getOrDefault(dia, "sin_marcar"));

                btn.addActionListener(e -> {
                    String estadoActual = (String) btn.getClientProperty("estado");
                    String nuevoEstado;
                    switch (estadoActual == null ? "sin_marcar" : estadoActual) {
                        case "sin_marcar": nuevoEstado = "Asistio"; break;
                        case "Asistio":    nuevoEstado = "Falto";   break;
                        default:           nuevoEstado = "sin_marcar"; break;
                    }
                    asignarColorBoton(btn, nuevoEstado);
                    actualizarResumen();
                });
                dia++;
            }
        }
        actualizarResumen();
    }

    private void asignarColorBoton(JButton btn, String estado) {
        btn.putClientProperty("estado", estado);
        switch (estado) {
            case "Asistio":
                btn.setBackground(COLOR_ASISTIO); btn.setForeground(BLANCO); break;
            case "Falto":
                btn.setBackground(COLOR_FALTO);   btn.setForeground(BLANCO); break;
            default:
                btn.setBackground(COLOR_SIN_MARCAR); btn.setForeground(TEXTO_MENU); break;
        }
    }

    private void actualizarResumen() {
        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
        lblResumenTitulo.setText("RESUMEN — " + nombreMes + " " + mesActual.getYear());
        int asistio = 0, falto = 0;
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++) {
                String estado = (String) botonesCalendario[i][j].getClientProperty("estado");
                if ("Asistio".equals(estado)) asistio++;
                else if ("Falto".equals(estado)) falto++;
            }
        lblAsistio.setText(asistio + " días");
        lblFalto.setText(falto + " días");
    }

    private void guardarAsistencias() {
        if (ciPaciente == -1) {
            JOptionPane.showMessageDialog(null, "Primero busque un paciente antes de guardar asistencias",
                "Sin paciente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verificar registros existentes del mes
        String sqlCheck = "SELECT COUNT(*) FROM asistencias WHERE ci_paciente = ? AND YEAR(fecha) = ? AND MONTH(fecha) = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, ciPaciente);
            ps.setInt(2, mesActual.getYear());
            ps.setInt(3, mesActual.getMonthValue());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    int confirmar = JOptionPane.showConfirmDialog(null,
                        "Ya existen asistencias guardadas para este mes.\n¿Desea sobreescribir los registros existentes?",
                        "Confirmar sobreescritura", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirmar != JOptionPane.YES_OPTION) return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar asistencias\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Borrar mes actual
        String sqlDelete = "DELETE FROM asistencias WHERE ci_paciente = ? AND YEAR(fecha) = ? AND MONTH(fecha) = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
            ps.setInt(1, ciPaciente);
            ps.setInt(2, mesActual.getYear());
            ps.setInt(3, mesActual.getMonthValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al limpiar asistencias previas\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insertar nuevas
        String sqlInsert = "INSERT INTO asistencias (ci_paciente, fecha, estado) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
            LocalDate primerDia = mesActual.atDay(1);
            int diaSemana = primerDia.getDayOfWeek().getValue() - 1;
            int diasEnMes = mesActual.lengthOfMonth();
            int dia = 1;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 7; j++) {
                    if (i == 0 && j < diaSemana) continue;
                    if (dia > diasEnMes) break;
                    String estado = (String) botonesCalendario[i][j].getClientProperty("estado");
                    if (estado != null && !estado.equals("sin_marcar")) {
                        ps.setInt(1, ciPaciente);
                        ps.setDate(2, java.sql.Date.valueOf(mesActual.atDay(dia)));
                        ps.setString(3, estado);
                        ps.executeUpdate();
                    }
                    dia++;
                }
            }
            JOptionPane.showMessageDialog(null, "Asistencias guardadas correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar asistencias\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verHistorial() {
        if (ciPaciente == -1) {
            JOptionPane.showMessageDialog(null, "Primero busque un paciente",
                "Sin paciente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT MONTH(fecha) as mes, YEAR(fecha) as anio, " +
                     "SUM(CASE WHEN estado = 'Asistio' THEN 1 ELSE 0 END) as asistencias, " +
                     "SUM(CASE WHEN estado = 'Falto'   THEN 1 ELSE 0 END) as faltas " +
                     "FROM asistencias WHERE ci_paciente = ? " +
                     "GROUP BY YEAR(fecha), MONTH(fecha) ORDER BY anio DESC, mes DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ciPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                DefaultTableModel modeloHistorial = new DefaultTableModel() {
                    public boolean isCellEditable(int r, int c) { return false; }
                };
                modeloHistorial.addColumn("Mes");
                modeloHistorial.addColumn("Año");
                modeloHistorial.addColumn("Asistencias");
                modeloHistorial.addColumn("Faltas");
                String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                  "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    modeloHistorial.addRow(new Object[]{
                        meses[rs.getInt("mes")], rs.getInt("anio"),
                        rs.getInt("asistencias"), rs.getInt("faltas")
                    });
                }
                if (!hayDatos) {
                    JOptionPane.showMessageDialog(null, "No hay historial de asistencias para este paciente",
                        "Sin historial", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                JTable tablaHistorial = new JTable(modeloHistorial);
                tablaHistorial.setBackground(VERDE_MENU);
                tablaHistorial.setForeground(BLANCO);
                tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tablaHistorial.setRowHeight(28);
                tablaHistorial.getTableHeader().setBackground(VERDE_OSCURO);
                tablaHistorial.getTableHeader().setForeground(DORADO);
                tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
                JScrollPane scroll = new JScrollPane(tablaHistorial);
                scroll.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(null, scroll, "Historial de Asistencias", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirCalendario() {
        if (ciPaciente == -1) {
            JOptionPane.showMessageDialog(null, "Primero busque un paciente antes de imprimir",
                "Sin paciente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String nombreCompleto = "";
            String sqlNombre = "SELECT nombre, apellido FROM paciente WHERE ci = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlNombre)) {
                ps.setInt(1, ciPaciente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        nombreCompleto = rs.getString("nombre").trim() + "_" + rs.getString("apellido").trim();
                }
            }

            String escritorio;
            try {
                escritorio = javax.swing.filechooser.FileSystemView.getFileSystemView()
                    .getHomeDirectory().getAbsolutePath();
            } catch (Exception ex) {
                escritorio = System.getProperty("user.home") + "/Desktop";
            }

            java.io.File carpetaPDFs = new java.io.File(escritorio + "/PDF'S/Asistencias");
            if (!carpetaPDFs.exists()) carpetaPDFs.mkdirs();

            String nombreMesArchivo = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            nombreMesArchivo = nombreMesArchivo.substring(0, 1).toUpperCase() + nombreMesArchivo.substring(1);
            String nombreArchivo = nombreCompleto + "_" + ciPaciente + "_" + nombreMesArchivo + "_" + mesActual.getYear() + ".pdf";

            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar PDF");
            fc.setCurrentDirectory(carpetaPDFs);
            fc.setSelectedFile(new java.io.File(carpetaPDFs, nombreArchivo));
            if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) path += ".pdf";

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(
                com.itextpdf.text.PageSize.LETTER, 50, 50, 50, 50);
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(path));
            doc.open();

            com.itextpdf.text.Font fTitulo    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fSubtitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fNormal    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);
            com.itextpdf.text.Font fBold      = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fCalBold   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8,  com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fCalSmall  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 7);

            // Encabezado PDF
            com.itextpdf.text.pdf.PdfPTable headerTable = new com.itextpdf.text.pdf.PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{4f, 1f});
            com.itextpdf.text.pdf.PdfPCell celdaTitulo = new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase("CENTRO DE FISIOTERAPIA Y KINESIOLOGÍA R&R", fTitulo));
            celdaTitulo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            celdaTitulo.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
            celdaTitulo.setPaddingTop(10);
            headerTable.addCell(celdaTitulo);
            try {
                String imgPath = getClass().getResource("/Imagenes/logo.png").getPath();
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(imgPath);
                logo.scaleAbsolute(70, 70);
                com.itextpdf.text.pdf.PdfPCell celdaLogo = new com.itextpdf.text.pdf.PdfPCell(logo);
                celdaLogo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                celdaLogo.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                headerTable.addCell(celdaLogo);
            } catch (Exception ex) {
                com.itextpdf.text.pdf.PdfPCell vacia = new com.itextpdf.text.pdf.PdfPCell();
                vacia.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                headerTable.addCell(vacia);
            }
            doc.add(headerTable);

            // Línea separadora
            com.itextpdf.text.pdf.PdfPTable lineaSep = new com.itextpdf.text.pdf.PdfPTable(1);
            lineaSep.setWidthPercentage(100);
            com.itextpdf.text.pdf.PdfPCell cellLinea = new com.itextpdf.text.pdf.PdfPCell();
            cellLinea.setBorderWidthBottom(1.5f); cellLinea.setBorderWidthTop(0);
            cellLinea.setBorderWidthLeft(0);      cellLinea.setBorderWidthRight(0);
            cellLinea.setMinimumHeight(5f);
            lineaSep.addCell(cellLinea);
            doc.add(lineaSep);
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Datos paciente
            String sqlP = "SELECT nombre, apellido, fecha FROM paciente WHERE ci = ?";
            String nombrePaciente = "", apellidoPaciente = "";
            int edad = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlP)) {
                ps.setInt(1, ciPaciente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombrePaciente   = rs.getString("nombre");
                        apellidoPaciente = rs.getString("apellido");
                        edad = java.time.Period.between(rs.getDate("fecha").toLocalDate(), LocalDate.now()).getYears();
                    }
                }
            }

            com.itextpdf.text.pdf.PdfPTable tablaDatos = new com.itextpdf.text.pdf.PdfPTable(4);
            tablaDatos.setWidthPercentage(100);
            tablaDatos.setWidths(new float[]{1.2f, 2.5f, 0.8f, 0.8f});
            tablaDatos.setSpacingAfter(10f);
            com.itextpdf.text.pdf.PdfPCell[] celdas = {
                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Paciente", fBold)),
                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(nombrePaciente + " " + apellidoPaciente, fNormal)),
                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Edad", fBold)),
                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(edad + " años", fNormal))
            };
            for (com.itextpdf.text.pdf.PdfPCell cell : celdas) {
                cell.setPadding(5);
                cell.setBorderColor(new com.itextpdf.text.BaseColor(180, 180, 180));
                tablaDatos.addCell(cell);
            }
            doc.add(tablaDatos);

            // Tratamientos
            doc.add(new com.itextpdf.text.Paragraph("Tratamientos asignados:", fBold));
            doc.add(new com.itextpdf.text.Paragraph(" "));
            String sqlT = "SELECT ct.nombre_tratamiento FROM paciente_tratamiento pt " +
                          "INNER JOIN catalogo_tratamientos ct ON pt.id_tratamiento = ct.id_tratamiento " +
                          "WHERE pt.ci_paciente = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlT)) {
                ps.setInt(1, ciPaciente);
                try (ResultSet rs = ps.executeQuery()) {
                    com.itextpdf.text.List listaTrat = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
                    listaTrat.setIndentationLeft(20);
                    boolean hayTrat = false;
                    while (rs.next()) {
                        hayTrat = true;
                        listaTrat.add(new com.itextpdf.text.ListItem(rs.getString("nombre_tratamiento"), fNormal));
                    }
                    if (hayTrat) doc.add(listaTrat);
                    else doc.add(new com.itextpdf.text.Paragraph("Sin tratamientos asignados.", fNormal));
                }
            }
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Título calendario
            String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
            com.itextpdf.text.Paragraph tituloCal = new com.itextpdf.text.Paragraph(
                "Registro de Asistencias — " + nombreMes + " " + mesActual.getYear(), fSubtitulo);
            tituloCal.setSpacingAfter(8f);
            doc.add(tituloCal);

            // Calendario grid 7 columnas
            com.itextpdf.text.pdf.PdfPTable tablaCal = new com.itextpdf.text.pdf.PdfPTable(7);
            tablaCal.setWidthPercentage(100);
            String[] diasSem = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
            for (String dia : diasSem) {
                com.itextpdf.text.pdf.PdfPCell cellDia = new com.itextpdf.text.pdf.PdfPCell(
                    new com.itextpdf.text.Phrase(dia, fCalBold));
                cellDia.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cellDia.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
                cellDia.setPadding(4);
                tablaCal.addCell(cellDia);
            }

            java.util.Map<Integer, String> asistencias = new java.util.HashMap<>();
            String sqlA = "SELECT DAY(fecha), estado FROM asistencias " +
                          "WHERE ci_paciente = ? AND YEAR(fecha) = ? AND MONTH(fecha) = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlA)) {
                ps.setInt(1, ciPaciente);
                ps.setInt(2, mesActual.getYear());
                ps.setInt(3, mesActual.getMonthValue());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) asistencias.put(rs.getInt(1), rs.getString(2));
                }
            }

            LocalDate primerDia = mesActual.atDay(1);
            int diaSemana = primerDia.getDayOfWeek().getValue() - 1;
            int diasEnMes = mesActual.lengthOfMonth();

            for (int i = 0; i < diaSemana; i++) {
                com.itextpdf.text.pdf.PdfPCell vacia = new com.itextpdf.text.pdf.PdfPCell();
                vacia.setMinimumHeight(50f);
                vacia.setBackgroundColor(new com.itextpdf.text.BaseColor(245, 245, 245));
                tablaCal.addCell(vacia);
            }
            for (int dia = 1; dia <= diasEnMes; dia++) {
                String estado = asistencias.getOrDefault(dia, "sin_marcar");
                com.itextpdf.text.pdf.PdfPCell cellDiaCal = new com.itextpdf.text.pdf.PdfPCell();
                cellDiaCal.setMinimumHeight(50f); cellDiaCal.setPadding(3);
                com.itextpdf.text.Paragraph pDia = new com.itextpdf.text.Paragraph(String.valueOf(dia), fCalBold);
                pDia.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cellDiaCal.addElement(pDia);
                com.itextpdf.text.Paragraph pEstado = new com.itextpdf.text.Paragraph();
                pEstado.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                pEstado.setSpacingBefore(4f);
                if ("Asistio".equals(estado)) {
                    pEstado.add(new com.itextpdf.text.Chunk("Asistió", fCalSmall));
                    cellDiaCal.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 230, 230));
                } else if ("Falto".equals(estado)) {
                    pEstado.add(new com.itextpdf.text.Chunk("No asistió", fCalSmall));
                }
                cellDiaCal.addElement(pEstado);
                tablaCal.addCell(cellDiaCal);
            }
            int celdasRestantes = (7 - ((diaSemana + diasEnMes) % 7)) % 7;
            for (int i = 0; i < celdasRestantes; i++) {
                com.itextpdf.text.pdf.PdfPCell vacia = new com.itextpdf.text.pdf.PdfPCell();
                vacia.setMinimumHeight(50f);
                vacia.setBackgroundColor(new com.itextpdf.text.BaseColor(245, 245, 245));
                tablaCal.addCell(vacia);
            }
            doc.add(tablaCal);
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Resumen final
            int totalAsistio = 0, totalFalto = 0;
            for (String est : asistencias.values()) {
                if ("Asistio".equals(est)) totalAsistio++;
                else if ("Falto".equals(est)) totalFalto++;
            }
            com.itextpdf.text.pdf.PdfPTable tablaResumen = new com.itextpdf.text.pdf.PdfPTable(3);
            tablaResumen.setWidthPercentage(60);
            tablaResumen.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            com.itextpdf.text.pdf.PdfPCell rTitulo = new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase("Resumen del mes", fBold));
            rTitulo.setColspan(3);
            rTitulo.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
            rTitulo.setPadding(5);
            tablaResumen.addCell(rTitulo);
            com.itextpdf.text.pdf.PdfPCell rAsis  = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Asistencias: " + totalAsistio, fNormal));
            com.itextpdf.text.pdf.PdfPCell rFalt  = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Faltas: " + totalFalto, fNormal));
            com.itextpdf.text.pdf.PdfPCell rTotal = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Total: " + (totalAsistio + totalFalto), fNormal));
            rAsis.setPadding(5); rFalt.setPadding(5); rTotal.setPadding(5);
            tablaResumen.addCell(rAsis); tablaResumen.addCell(rFalt); tablaResumen.addCell(rTotal);
            doc.add(tablaResumen);
            doc.close();

            JOptionPane.showMessageDialog(null, "PDF generado correctamente en:\n" + path,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar el PDF\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── MÉTODOS UI (encabezado, menú, botones) ──
    private JPanel crearPanelTabla(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_MENU);
        panel.setBorder(BorderFactory.createLineBorder(DORADO, 1));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Georgia", Font.BOLD, 14));
        lbl.setForeground(DORADO);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        lbl.setBackground(VERDE_MENU); lbl.setOpaque(true);
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

    private JTable crearTabla() {
        JTable tabla = new JTable();
        tabla.setBackground(VERDE_MENU); tabla.setForeground(BLANCO);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.setGridColor(new Color(40, 80, 45));
        tabla.setSelectionBackground(DORADO); tabla.setSelectionForeground(new Color(20, 20, 20));
        tabla.setShowHorizontalLines(true); tabla.setShowVerticalLines(false);
        tabla.getTableHeader().setBackground(VERDE_OSCURO); tabla.getTableHeader().setForeground(DORADO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) { setBackground(DORADO); setForeground(new Color(20, 20, 20)); }
                else     { setBackground(row % 2 == 0 ? VERDE_MENU : VERDE_FILA); setForeground(BLANCO); }
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
        } catch (Exception e) { lblLogo = new JLabel("R&R"); lblLogo.setForeground(DORADO); }
        lblLogo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        inner.add(lblLogo, BorderLayout.EAST);
        JPanel linea = new JPanel();
        linea.setBackground(DORADO); linea.setPreferredSize(new Dimension(0, 2));
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
        botonesPanel.add(btnRegistro); botonesPanel.add(btnListado);
        botonesPanel.add(btnTratamientos); botonesPanel.add(btnModificar); botonesPanel.add(btnAsistencias);
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
        lineaVolver.setBackground(DORADO); lineaVolver.setPreferredSize(new Dimension(0, 1));
        panelVolver.add(lineaVolver, BorderLayout.NORTH);
        JButton btnVolver = new JButton("VOLVER AL MENÚ");
        btnVolver.setBackground(new Color(120, 20, 20)); btnVolver.setForeground(BLANCO);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DORADO, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        btnVolver.setFocusPainted(false); btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(0, 50));
        btnVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnVolver.setBackground(new Color(160, 30, 30)); }
            public void mouseExited(MouseEvent e)  { btnVolver.setBackground(new Color(120, 20, 20)); }
        });
        btnVolver.addActionListener(e -> { new frmmenu().setVisible(true); dispose(); });
        panelVolver.add(btnVolver, BorderLayout.SOUTH);
        panelMenu.add(panelVolver, BorderLayout.SOUTH);
        JPanel lineaDerecha = new JPanel();
        lineaDerecha.setBackground(DORADO); lineaDerecha.setPreferredSize(new Dimension(2, 0));
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
        indicador.setBackground(DORADO); indicador.setPreferredSize(new Dimension(4, 0));
        indicador.setVisible(activo); panel.add(indicador, BorderLayout.WEST);
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
        btn.setBackground(fondo); btn.setForeground(fuente);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fondo.darker(), 1), BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
        pack();
    }
}