import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;
import Conexion.Conexion;

public class frmtyp2 extends javax.swing.JFrame {

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

    private JTable JTableDatos;
    private JTextArea jtxta;

    public frmtyp2(int ciPaciente) {
        setTitle("Patologías del Paciente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Conexion c = new Conexion();
        con = c.getConexion();
        this.ciPaciente = ciPaciente;
        construirUI();
        configurarTablaDatos();
        cargarDatosPaciente();
        cargarPatologia();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        raiz.add(crearEncabezado("PATOLOGÍA DEL PACIENTE"), BorderLayout.NORTH);
        raiz.add(crearMenu(), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBackground(VERDE_OSCURO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior — datos del paciente
        JPanel panelDatos = crearPanelTabla("DATOS DEL PACIENTE");
        JTableDatos = crearTabla();
        panelDatos.add(new JScrollPane(JTableDatos), BorderLayout.CENTER);
        panelDatos.setPreferredSize(new Dimension(0, 110));
        contenido.add(panelDatos, BorderLayout.NORTH);

        // Panel central — patología
        JPanel panelPatologia = new JPanel(new BorderLayout());
        panelPatologia.setBackground(VERDE_MENU);
        panelPatologia.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblPatologia = new JLabel("PATOLOGÍA");
        lblPatologia.setFont(new Font("Georgia", Font.BOLD, 15));
        lblPatologia.setForeground(DORADO);
        lblPatologia.setHorizontalAlignment(SwingConstants.CENTER);
        lblPatologia.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel lineaPat = new JPanel();
        lineaPat.setBackground(DORADO);
        lineaPat.setPreferredSize(new Dimension(0, 1));

        JPanel nortePat = new JPanel(new BorderLayout());
        nortePat.setBackground(VERDE_MENU);
        nortePat.add(lblPatologia, BorderLayout.CENTER);
        nortePat.add(lineaPat, BorderLayout.SOUTH);
        panelPatologia.add(nortePat, BorderLayout.NORTH);

        jtxta = new JTextArea();
        jtxta.setBackground(new Color(240, 240, 235));
        jtxta.setForeground(new Color(20, 20, 20));
        jtxta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jtxta.setLineWrap(true);
        jtxta.setWrapStyleWord(true);
        jtxta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JScrollPane scrollPat = new JScrollPane(jtxta);
        scrollPat.setBorder(null);
        panelPatologia.add(scrollPat, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(VERDE_MENU);

        JButton btnGuardar  = crearBotonAccion("GUARDAR PATOLOGÍA", DORADO,                  new Color(20, 20, 20));
        JButton btnLimpiar  = crearBotonAccion("LIMPIAR",           new Color(120, 20, 20),  BLANCO);
        JButton btnImprimir = crearBotonAccion("IMPRIMIR PDF",      new Color(50, 50, 120),  BLANCO);

        btnGuardar.addActionListener(e -> guardarPatologia());
        btnLimpiar.addActionListener(e -> {
            if (jtxta.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El campo ya está vacío", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirmar = JOptionPane.showConfirmDialog(null,
                "¿Está seguro que desea limpiar el campo de patología?\nNOTA: Esto solo limpia la pantalla.\nDebe guardar para que el PDF refleje el cambio.",
                "Confirmar limpieza", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirmar != JOptionPane.YES_OPTION) return;
            jtxta.setText("");
            JOptionPane.showMessageDialog(null, "Campo limpiado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        btnImprimir.addActionListener(e -> imprimirPDF());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnImprimir);
        panelPatologia.add(panelBotones, BorderLayout.SOUTH);

        contenido.add(panelPatologia, BorderLayout.CENTER);
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

    private void cargarPatologia() {
        String sql = "SELECT patologia FROM paciente WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ciPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String pat = rs.getString("patologia");
                    if (pat != null && !pat.isEmpty()) jtxta.setText(pat);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar patología\nContacte al administrador",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarPatologia() {
        if (jtxta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Escriba una patología antes de guardar");
            return;
        }
        String sql = "UPDATE paciente SET patologia = ? WHERE ci = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, jtxta.getText().trim());
            ps.setInt(2, ciPaciente);
            int filas = ps.executeUpdate();
            if (filas > 0)
                JOptionPane.showMessageDialog(null, "Patología guardada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(null, "No se encontró el paciente", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la patología\nContacte al administrador",
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

            // Patología desde BD (no desde textarea, para asegurar consistencia)
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
        cell.setMinimumHeight(5f); linea.addCell(cell); doc.add(linea);
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
        JPanel lineaTitulo = new JPanel(); lineaTitulo.setBackground(DORADO); lineaTitulo.setPreferredSize(new Dimension(0, 1));
        JPanel norte = new JPanel(new BorderLayout()); norte.setBackground(VERDE_MENU);
        norte.add(lbl, BorderLayout.CENTER); norte.add(lineaTitulo, BorderLayout.SOUTH);
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
        btnVolver.addActionListener(e -> { new frmtyp(ciPaciente).setVisible(true); dispose(); });
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