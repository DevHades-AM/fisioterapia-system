import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;
import Conexion.Conexion;

public class frmtyp extends javax.swing.JFrame {

    private final Color VERDE_OSCURO  = new Color(26, 58, 31);
    private final Color VERDE_MENU    = new Color(18, 38, 22);
    private final Color VERDE_HOVER   = new Color(35, 80, 42);
    private final Color VERDE_FILA    = new Color(30, 65, 35);
    private final Color DORADO        = new Color(201, 162, 39);
    private final Color DORADO_CLARO  = new Color(232, 200, 74);
    private final Color BLANCO        = new Color(255, 255, 255);
    private final Color TEXTO_MENU    = new Color(220, 220, 220);

    Connection con;
    int ciPaciente;
    DefaultTableModel modeloPaciente;
    DefaultTableModel modeloAsignados;

    private JTable JTableDatos, JTableT;
    // UNA SOLA instancia del combo — bug del original corregido
    private JComboBox<String> cbb;

    public frmtyp(int ciPaciente) {
        setTitle("Tratamientos y Patología");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Conexion c = new Conexion();
        con = c.getConexion();
        this.ciPaciente = ciPaciente;
        construirUI();
        configurarTablaDatos();
        configurarTablaAsignados();
        cargarDatosPaciente();
        cargarComboTratamientos();
        listarTratamientosAsignados();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        raiz.add(crearEncabezado("TRATAMIENTOS DEL PACIENTE"), BorderLayout.NORTH);
        raiz.add(crearMenu(), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(15, 15));
        contenido.setBackground(VERDE_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior — datos del paciente
        JPanel panelDatos = crearPanelTabla("DATOS DEL PACIENTE");
        JTableDatos = crearTabla();
        panelDatos.add(new JScrollPane(JTableDatos), BorderLayout.CENTER);
        panelDatos.setPreferredSize(new Dimension(0, 110));
        contenido.add(panelDatos, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 0));
        panelCentral.setBackground(VERDE_OSCURO);

        // Panel izquierdo — asignar tratamiento
        JPanel panelAsignar = new JPanel(new BorderLayout());
        panelAsignar.setBackground(VERDE_MENU);
        panelAsignar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblAsignar = new JLabel("ASIGNAR TRATAMIENTO");
        lblAsignar.setFont(new Font("Georgia", Font.BOLD, 15));
        lblAsignar.setForeground(DORADO);
        lblAsignar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAsignar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel lineaAsignar = new JPanel();
        lineaAsignar.setBackground(DORADO);
        lineaAsignar.setPreferredSize(new Dimension(0, 1));

        JPanel norteAsignar = new JPanel(new BorderLayout());
        norteAsignar.setBackground(VERDE_MENU);
        norteAsignar.add(lblAsignar, BorderLayout.CENTER);
        norteAsignar.add(lineaAsignar, BorderLayout.SOUTH);
        panelAsignar.add(norteAsignar, BorderLayout.NORTH);

        // Centro — combo (UNA sola instancia) + botón recargar
        JPanel panelCombo = new JPanel(new GridBagLayout());
        panelCombo.setBackground(VERDE_MENU);
        panelCombo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblTrat = new JLabel("Seleccione un tratamiento:");
        lblTrat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTrat.setForeground(DORADO_CLARO);
        panelCombo.add(lblTrat, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);

        // ── ÚNICA instancia del combo ──
        cbb = new JComboBox<>();
        cbb.setBackground(new Color(240, 240, 235));
        cbb.setForeground(new Color(20, 20, 20));
        cbb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbb.setPreferredSize(new Dimension(0, 38));
        cbb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton btnRecargar = new JButton("R");
        btnRecargar.setBackground(new Color(50, 50, 120));
        btnRecargar.setForeground(BLANCO);
        btnRecargar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRecargar.setPreferredSize(new Dimension(45, 38));
        btnRecargar.setFocusPainted(false);
        btnRecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecargar.setToolTipText("Recargar lista de tratamientos");
        btnRecargar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnRecargar.setBackground(new Color(70, 70, 160)); }
            public void mouseExited(MouseEvent e)  { btnRecargar.setBackground(new Color(50, 50, 120)); }
        });
        // Ahora sí recarga el combo correcto (cbb)
        btnRecargar.addActionListener(e -> {
            cargarComboTratamientos();
            JOptionPane.showMessageDialog(null, "Lista de tratamientos actualizada",
                "Recargado", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel panelComboRecargar = new JPanel(new BorderLayout(5, 0));
        panelComboRecargar.setBackground(VERDE_MENU);
        panelComboRecargar.add(cbb, BorderLayout.CENTER);
        panelComboRecargar.add(btnRecargar, BorderLayout.EAST);
        panelCombo.add(panelComboRecargar, gbc);

        gbc.gridy = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        JPanel espaciador = new JPanel();
        espaciador.setBackground(VERDE_MENU);
        panelCombo.add(espaciador, gbc);

        panelAsignar.add(panelCombo, BorderLayout.CENTER);

        // Botones
        JPanel panelBotonesWrapper = new JPanel(new GridLayout(4, 1, 0, 10));
        panelBotonesWrapper.setBackground(VERDE_MENU);

        JButton btnAsignar   = crearBotonAccion("ASIGNAR TRATAMIENTO",  DORADO,                  new Color(20, 20, 20));
        JButton btnEliminar  = crearBotonAccion("ELIMINAR TRATAMIENTO", new Color(120, 20, 20),  BLANCO);
        JButton btnPatologia = crearBotonAccion("PATOLOGÍAS",           new Color(0, 100, 100),  BLANCO);
        JButton btnImprimir  = crearBotonAccion("IMPRIMIR PDF",         new Color(50, 50, 120),  BLANCO);

        btnAsignar.addActionListener(e -> asignarTratamiento());
        btnEliminar.addActionListener(e -> eliminarTratamiento());
        btnPatologia.addActionListener(e -> { new frmtyp2(ciPaciente).setVisible(true); dispose(); });
        btnImprimir.addActionListener(e -> imprimirPDF());

        panelBotonesWrapper.add(btnAsignar);
        panelBotonesWrapper.add(btnEliminar);
        panelBotonesWrapper.add(btnPatologia);
        panelBotonesWrapper.add(btnImprimir);
        panelAsignar.add(panelBotonesWrapper, BorderLayout.SOUTH);
        panelCentral.add(panelAsignar);

        // Panel derecho — tratamientos asignados
        JPanel panelAsignados = crearPanelTabla("TRATAMIENTOS ASIGNADOS");
        JTableT = crearTabla();
        panelAsignados.add(new JScrollPane(JTableT), BorderLayout.CENTER);
        panelCentral.add(panelAsignados);

        contenido.add(panelCentral, BorderLayout.CENTER);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private void configurarTablaDatos() {
        modeloPaciente = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloPaciente.addColumn("C.I.");
        modeloPaciente.addColumn("Nombre");
        modeloPaciente.addColumn("Apellido");
        modeloPaciente.addColumn("Fecha");
        modeloPaciente.addColumn("Ocupación");
        JTableDatos.setModel(modeloPaciente);
    }

    private void configurarTablaAsignados() {
        modeloAsignados = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        modeloAsignados.addColumn("ID");
        modeloAsignados.addColumn("Nombre Tratamiento");
        JTableT.setModel(modeloAsignados);
    }

    private void cargarDatosPaciente() {
        modeloPaciente.setRowCount(0);
        String sql = "SELECT * FROM paciente WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ciPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    modeloPaciente.addRow(new Object[]{
                        rs.getInt("ci"), rs.getString("nombre"), rs.getString("apellido"),
                        rs.getDate("fecha"), rs.getString("ocupacion")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos del paciente\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboTratamientos() {
        String sql = "SELECT nombre_tratamiento FROM catalogo_tratamientos ORDER BY id_tratamiento ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            cbb.removeAllItems();
            cbb.addItem("Seleccione un tratamiento");
            while (rs.next()) cbb.addItem(rs.getString("nombre_tratamiento"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar tratamientos\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarTratamientosAsignados() {
        modeloAsignados.setRowCount(0);
        String sql = "SELECT pt.id_tratamiento, ct.nombre_tratamiento " +
                     "FROM paciente_tratamiento pt " +
                     "INNER JOIN catalogo_tratamientos ct ON pt.id_tratamiento = ct.id_tratamiento " +
                     "WHERE pt.ci_paciente = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ciPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloAsignados.addRow(new Object[]{
                        rs.getInt("id_tratamiento"), rs.getString("nombre_tratamiento")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar tratamientos asignados\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarTratamiento() {
        if (cbb.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un tratamiento válido");
            return;
        }
        String nombreTrat = cbb.getSelectedItem().toString();

        // Obtener ID del tratamiento
        int idTrat;
        String sqlID = "SELECT id_tratamiento FROM catalogo_tratamientos WHERE nombre_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlID)) {
            ps.setString(1, nombreTrat);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                idTrat = rs.getInt("id_tratamiento");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar que no esté ya asignado
        String sqlCheck = "SELECT * FROM paciente_tratamiento WHERE ci_paciente = ? AND id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, ciPaciente); ps.setInt(2, idTrat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Este tratamiento ya está asignado");
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar asignación\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insertar
        String sql = "INSERT INTO paciente_tratamiento (ci_paciente, id_tratamiento) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ciPaciente); ps.setInt(2, idTrat);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento asignado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listarTratamientosAsignados();
            cbb.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al asignar tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTratamiento() {
        int idTrat = -1;
        String nombreTrat = "";

        int filaSeleccionada = JTableT.getSelectedRow();
        if (filaSeleccionada != -1) {
            idTrat     = (int) modeloAsignados.getValueAt(filaSeleccionada, 0);
            nombreTrat = modeloAsignados.getValueAt(filaSeleccionada, 1).toString();
        } else if (cbb.getSelectedIndex() > 0) {
            nombreTrat = cbb.getSelectedItem().toString();
            String sqlID = "SELECT id_tratamiento FROM catalogo_tratamientos WHERE nombre_tratamiento = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlID)) {
                ps.setString(1, nombreTrat);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idTrat = rs.getInt("id_tratamiento");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al obtener tratamiento\nContacte al administrador",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null,
                "Seleccione un tratamiento de la tabla o del combo para eliminar",
                "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que esté asignado
        String sqlCheck = "SELECT * FROM paciente_tratamiento WHERE ci_paciente = ? AND id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, ciPaciente); ps.setInt(2, idTrat);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Este tratamiento no está asignado al paciente",
                        "No encontrado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar asignación\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(null,
            "¿Está seguro que desea eliminar el siguiente tratamiento asignado?\n\n" +
            "Tratamiento: " + nombreTrat + "\n\nEsta acción no se puede deshacer",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmar != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM paciente_tratamiento WHERE ci_paciente = ? AND id_tratamiento = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ciPaciente); ps.setInt(2, idTrat);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento eliminado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            listarTratamientosAsignados();
            cbb.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar tratamiento\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirPDF() {
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

            String escritorio = System.getProperty("user.home") + "/Desktop";
            java.io.File carpetaPDFs = new java.io.File(escritorio + "/PDF'S/Datos Pacientes");
            if (!carpetaPDFs.exists()) carpetaPDFs.mkdirs();
            String nombreArchivo = nombreCompleto + "_" + ciPaciente + ".pdf";

            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar PDF");
            fc.setCurrentDirectory(carpetaPDFs);
            fc.setSelectedFile(new java.io.File(carpetaPDFs, nombreArchivo));
            if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) path += ".pdf";

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(
                com.itextpdf.text.PageSize.LETTER, 50, 50, 50, 50);
            com.itextpdf.text.pdf.PdfWriter writer =
                com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(path));
            writer.setPageEvent(new com.itextpdf.text.pdf.PdfPageEventHelper() {
                @Override public void onEndPage(com.itextpdf.text.pdf.PdfWriter w, com.itextpdf.text.Document d) {
                    try {
                        String imgPath = getClass().getResource("/Imagenes/logo.png").getPath();
                        com.itextpdf.text.Image marca = com.itextpdf.text.Image.getInstance(imgPath);
                        com.itextpdf.text.pdf.PdfGState gs = new com.itextpdf.text.pdf.PdfGState();
                        gs.setFillOpacity(0.08f);
                        com.itextpdf.text.pdf.PdfContentByte canvas = w.getDirectContentUnder();
                        canvas.saveState(); canvas.setGState(gs);
                        float mw = 300f, mh = 300f;
                        float mx = (d.getPageSize().getWidth() - mw) / 2;
                        float my = (d.getPageSize().getHeight() - mh) / 2;
                        marca.scaleAbsolute(mw, mh); marca.setAbsolutePosition(mx, my);
                        canvas.addImage(marca); canvas.restoreState();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
            doc.open();

            com.itextpdf.text.Font fTitulo   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(30, 30, 30));
            com.itextpdf.text.Font fSeccion  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(50, 50, 50));
            com.itextpdf.text.Font fEtiqueta = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fValor    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);
            com.itextpdf.text.Font fHeader   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fTabla    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);

            // Encabezado
            com.itextpdf.text.pdf.PdfPTable headerTable = new com.itextpdf.text.pdf.PdfPTable(2);
            headerTable.setWidthPercentage(100); headerTable.setWidths(new float[]{4f, 1f});
            com.itextpdf.text.pdf.PdfPCell celdaTitulo = new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase("CENTRO DE FISIOTERAPIA Y KINESIOLOGÍA R&R", fTitulo));
            celdaTitulo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            celdaTitulo.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
            celdaTitulo.setPaddingTop(10);
            headerTable.addCell(celdaTitulo);
            String imgPath = getClass().getResource("/Imagenes/logo.png").getPath();
            com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(imgPath);
            logo.scaleAbsolute(70, 70);
            com.itextpdf.text.pdf.PdfPCell celdaLogo = new com.itextpdf.text.pdf.PdfPCell(logo);
            celdaLogo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            celdaLogo.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            headerTable.addCell(celdaLogo);
            doc.add(headerTable);
            agregarLineaPDF(doc);
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Datos paciente
            doc.add(new com.itextpdf.text.Paragraph("DATOS DEL PACIENTE", fSeccion));
            doc.add(new com.itextpdf.text.Paragraph(" "));
            String sqlP = "SELECT * FROM paciente WHERE ci = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlP)) {
                ps.setInt(1, ciPaciente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        agregarCampoPDF(doc, "Nombre:    ", rs.getString("nombre"),           fEtiqueta, fValor);
                        agregarCampoPDF(doc, "Apellido:  ", rs.getString("apellido"),          fEtiqueta, fValor);
                        agregarCampoPDF(doc, "C.I.:      ", String.valueOf(rs.getInt("ci")),   fEtiqueta, fValor);
                        agregarCampoPDF(doc, "Fecha Nac.:", rs.getDate("fecha").toString(),    fEtiqueta, fValor);
                        agregarCampoPDF(doc, "Ocupación: ", rs.getString("ocupacion"),         fEtiqueta, fValor);
                    }
                }
            }
            doc.add(new com.itextpdf.text.Paragraph(" "));
            agregarLineaPDF(doc);
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Tratamientos
            doc.add(new com.itextpdf.text.Paragraph("TRATAMIENTOS ASIGNADOS:", fSeccion));
            doc.add(new com.itextpdf.text.Paragraph(" "));
            com.itextpdf.text.pdf.PdfPTable tablaTrat = new com.itextpdf.text.pdf.PdfPTable(2);
            tablaTrat.setWidthPercentage(60); tablaTrat.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            tablaTrat.setWidths(new float[]{0.5f, 3f});
            com.itextpdf.text.pdf.PdfPCell hNum  = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("N°",          fHeader));
            com.itextpdf.text.pdf.PdfPCell hTrat = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Tratamiento", fHeader));
            hNum.setBorder(com.itextpdf.text.Rectangle.BOTTOM); hTrat.setBorder(com.itextpdf.text.Rectangle.BOTTOM);
            hNum.setPadding(4); hTrat.setPadding(4);
            tablaTrat.addCell(hNum); tablaTrat.addCell(hTrat);

            String sqlT = "SELECT ct.nombre_tratamiento FROM paciente_tratamiento pt " +
                          "INNER JOIN catalogo_tratamientos ct ON pt.id_tratamiento = ct.id_tratamiento " +
                          "WHERE pt.ci_paciente = ?";
            int num = 1; boolean hayTrat = false;
            try (PreparedStatement ps = con.prepareStatement(sqlT)) {
                ps.setInt(1, ciPaciente);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        hayTrat = true;
                        com.itextpdf.text.pdf.PdfPCell cNum  = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.valueOf(num), fTabla));
                        com.itextpdf.text.pdf.PdfPCell cTrat = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(rs.getString("nombre_tratamiento"), fTabla));
                        cNum.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);  cNum.setPadding(3);
                        cTrat.setBorder(com.itextpdf.text.Rectangle.NO_BORDER); cTrat.setPadding(3);
                        tablaTrat.addCell(cNum); tablaTrat.addCell(cTrat); num++;
                    }
                }
            }
            if (hayTrat) doc.add(tablaTrat);
            else doc.add(new com.itextpdf.text.Paragraph("No hay tratamientos asignados.", fValor));

            doc.add(new com.itextpdf.text.Paragraph(" "));
            agregarLineaPDF(doc);
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Patología
            doc.add(new com.itextpdf.text.Paragraph("PATOLOGIA:", fSeccion));
            doc.add(new com.itextpdf.text.Paragraph(" "));
            String sqlPat = "SELECT patologia FROM paciente WHERE ci = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlPat)) {
                ps.setInt(1, ciPaciente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getString("patologia") != null && !rs.getString("patologia").isEmpty())
                        doc.add(new com.itextpdf.text.Paragraph(rs.getString("patologia"), fValor));
                    else
                        doc.add(new com.itextpdf.text.Paragraph("Sin patología registrada.", fValor));
                }
            }
            doc.close();
            JOptionPane.showMessageDialog(null, "PDF generado correctamente en:\n" + path,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarLineaPDF(com.itextpdf.text.Document doc) throws Exception {
        com.itextpdf.text.pdf.PdfPTable linea = new com.itextpdf.text.pdf.PdfPTable(1);
        linea.setWidthPercentage(100);
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
        cell.setBorderWidthBottom(1f); cell.setBorderColorBottom(new com.itextpdf.text.BaseColor(30, 30, 30));
        cell.setBorderWidthTop(0); cell.setBorderWidthLeft(0); cell.setBorderWidthRight(0);
        cell.setMinimumHeight(5f);
        linea.addCell(cell); doc.add(linea);
    }

    private void agregarCampoPDF(com.itextpdf.text.Document doc, String etiqueta, String valor,
        com.itextpdf.text.Font fEtiqueta, com.itextpdf.text.Font fValor) throws Exception {
        com.itextpdf.text.Paragraph p = new com.itextpdf.text.Paragraph();
        p.add(new com.itextpdf.text.Chunk(etiqueta, fEtiqueta));
        p.add(new com.itextpdf.text.Chunk("  " + valor, fValor));
        p.setSpacingAfter(4f); doc.add(p);
    }

    // ── MÉTODOS UI ──
    private JPanel crearPanelTabla(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_MENU); panel.setBorder(BorderFactory.createLineBorder(DORADO, 1));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Georgia", Font.BOLD, 14)); lbl.setForeground(DORADO);
        lbl.setHorizontalAlignment(SwingConstants.CENTER); lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        lbl.setBackground(VERDE_MENU); lbl.setOpaque(true);
        JPanel lineaTitulo = new JPanel();
        lineaTitulo.setBackground(DORADO); lineaTitulo.setPreferredSize(new Dimension(0, 1));
        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(VERDE_MENU); norte.add(lbl, BorderLayout.CENTER); norte.add(lineaTitulo, BorderLayout.SOUTH);
        panel.add(norte, BorderLayout.NORTH); return panel;
    }

    private JTable crearTabla() {
        JTable tabla = new JTable();
        tabla.setBackground(VERDE_MENU); tabla.setForeground(BLANCO);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13)); tabla.setRowHeight(28);
        tabla.setGridColor(new Color(40, 80, 45)); tabla.setSelectionBackground(DORADO);
        tabla.setSelectionForeground(new Color(20, 20, 20));
        tabla.setShowHorizontalLines(true); tabla.setShowVerticalLines(false);
        tabla.getTableHeader().setBackground(VERDE_OSCURO); tabla.getTableHeader().setForeground(DORADO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) { setBackground(DORADO); setForeground(new Color(20, 20, 20)); }
                else     { setBackground(row % 2 == 0 ? VERDE_MENU : VERDE_FILA); setForeground(BLANCO); }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8)); return this;
            }
        });
        return tabla;
    }

    private JPanel crearEncabezado(String titulo) {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VERDE_MENU); encabezado.setPreferredSize(new Dimension(0, 92));
        JPanel inner = new JPanel(new BorderLayout()); inner.setBackground(VERDE_MENU);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 26)); lblTitulo.setForeground(DORADO);
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
        JPanel linea = new JPanel(); linea.setBackground(DORADO); linea.setPreferredSize(new Dimension(0, 2));
        encabezado.add(inner, BorderLayout.CENTER); encabezado.add(linea, BorderLayout.SOUTH);
        return encabezado;
    }

    private JPanel crearMenu() {
        JPanel menuConLinea = new JPanel(new BorderLayout());
        menuConLinea.setBackground(VERDE_MENU); menuConLinea.setPreferredSize(new Dimension(260, 0));
        JPanel panelMenu = new JPanel(new BorderLayout()); panelMenu.setBackground(VERDE_MENU);
        JPanel botonesPanel = new JPanel(new GridLayout(5, 1, 0, 0));
        botonesPanel.setBackground(VERDE_MENU); botonesPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));
        JPanel btnRegistro     = crearBotonMenu("Registro De Pacientes",  false);
        JPanel btnListado      = crearBotonMenu("Listado De Pacientes",    false);
        JPanel btnTratamientos = crearBotonMenu("Listado De Tratamientos", true);
        JPanel btnModificar    = crearBotonMenu("Modificar Datos",         false);
        JPanel btnAsistencias  = crearBotonMenu("Asistencias",            false);
        botonesPanel.add(btnRegistro); botonesPanel.add(btnListado);
        botonesPanel.add(btnTratamientos); botonesPanel.add(btnModificar); botonesPanel.add(btnAsistencias);
        panelMenu.add(botonesPanel, BorderLayout.NORTH);
        btnRegistro.addMouseListener(new MouseAdapter()     { public void mousePressed(MouseEvent e) { new frmpaciente().setVisible(true);    dispose(); } });
        btnListado.addMouseListener(new MouseAdapter()      { public void mousePressed(MouseEvent e) { new frmlistado().setVisible(true);      dispose(); } });
        btnTratamientos.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { new frmtratamientos().setVisible(true); dispose(); } });
        btnModificar.addMouseListener(new MouseAdapter()    { public void mousePressed(MouseEvent e) { new frmmodificar().setVisible(true);    dispose(); } });
        btnAsistencias.addMouseListener(new MouseAdapter()  { public void mousePressed(MouseEvent e) { new frmAsistencias().setVisible(true);  dispose(); } });
        JPanel panelVolver = new JPanel(new BorderLayout());
        panelVolver.setBackground(VERDE_MENU); panelVolver.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JPanel lineaVolver = new JPanel(); lineaVolver.setBackground(DORADO); lineaVolver.setPreferredSize(new Dimension(0, 1));
        panelVolver.add(lineaVolver, BorderLayout.NORTH);
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setBackground(new Color(120, 20, 20)); btnVolver.setForeground(BLANCO);
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DORADO, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        btnVolver.setFocusPainted(false); btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(0, 50));
        btnVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnVolver.setBackground(new Color(160, 30, 30)); }
            public void mouseExited(MouseEvent e)  { btnVolver.setBackground(new Color(120, 20, 20)); }
        });
        btnVolver.addActionListener(e -> { new frmtratamientos().setVisible(true); dispose(); });
        panelVolver.add(btnVolver, BorderLayout.SOUTH); panelMenu.add(panelVolver, BorderLayout.SOUTH);
        JPanel lineaDerecha = new JPanel(); lineaDerecha.setBackground(DORADO); lineaDerecha.setPreferredSize(new Dimension(2, 0));
        menuConLinea.add(panelMenu, BorderLayout.CENTER); menuConLinea.add(lineaDerecha, BorderLayout.EAST);
        return menuConLinea;
    }

    private JPanel crearBotonMenu(String texto, boolean activo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(activo ? VERDE_HOVER : VERDE_MENU);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR)); panel.setPreferredSize(new Dimension(0, 55));
        JPanel indicador = new JPanel(); indicador.setBackground(DORADO);
        indicador.setPreferredSize(new Dimension(4, 0)); indicador.setVisible(activo);
        panel.add(indicador, BorderLayout.WEST);
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(activo ? DORADO_CLARO : TEXTO_MENU);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); panel.add(lbl, BorderLayout.CENTER);
        if (!activo) {
            panel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { panel.setBackground(VERDE_HOVER); lbl.setForeground(DORADO_CLARO); indicador.setVisible(true); }
                public void mouseExited(MouseEvent e)  { panel.setBackground(VERDE_MENU);  lbl.setForeground(TEXTO_MENU);  indicador.setVisible(false); }
            });
        }
        return panel;
    }

    private JButton crearBotonAccion(String texto, Color fondo, Color fuente) {
        JButton btn = new JButton(texto); btn.setBackground(fondo); btn.setForeground(fuente);
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
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1240, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 660, Short.MAX_VALUE));
        pack();
    }
}