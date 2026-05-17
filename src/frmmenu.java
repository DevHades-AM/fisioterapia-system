import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frmmenu extends javax.swing.JFrame {

    private final Color VERDE_OSCURO  = new Color(26, 58, 31);
    private final Color VERDE_MENU    = new Color(18, 38, 22);
    private final Color VERDE_HOVER   = new Color(35, 80, 42);
    private final Color DORADO        = new Color(201, 162, 39);
    private final Color DORADO_CLARO  = new Color(232, 200, 74);
    private final Color BLANCO        = new Color(255, 255, 255);
    private final Color TEXTO_MENU    = new Color(220, 220, 220);

    public frmmenu() {
        setTitle("Centro de Fisioterapia y Kinesiología R&R");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(VERDE_OSCURO);

        // ── ENCABEZADO ──
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VERDE_MENU);
        encabezado.setPreferredSize(new Dimension(0, 92));

        JPanel encabezadoInner = new JPanel(new BorderLayout());
        encabezadoInner.setBackground(VERDE_MENU);

        JLabel lblTitulo = new JLabel("CENTRO DE FISIOTERAPIA Y KINESIOLOGÍA R&R");
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 28));
        lblTitulo.setForeground(DORADO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0));
        encabezadoInner.add(lblTitulo, BorderLayout.CENTER);

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
        encabezadoInner.add(lblLogo, BorderLayout.EAST);

        JPanel lineaDorada = new JPanel();
        lineaDorada.setBackground(DORADO);
        lineaDorada.setPreferredSize(new Dimension(0, 2));

        encabezado.add(encabezadoInner, BorderLayout.CENTER);
        encabezado.add(lineaDorada, BorderLayout.SOUTH);
        raiz.add(encabezado, BorderLayout.NORTH);

        // ── MENÚ LATERAL ──
        JPanel menuConLinea = new JPanel(new BorderLayout());
        menuConLinea.setBackground(VERDE_MENU);
        menuConLinea.setPreferredSize(new Dimension(260, 0));

        JPanel panelMenu = new JPanel(new BorderLayout());
        panelMenu.setBackground(VERDE_MENU);

        JPanel botonesPanel = new JPanel(new GridLayout(5, 1, 0, 0));
        botonesPanel.setBackground(VERDE_MENU);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));

        JPanel btnRegistro     = crearBotonMenu("Registro De Pacientes",  false);
        JPanel btnListado      = crearBotonMenu("Listado De Pacientes",    false);
        JPanel btnTratamientos = crearBotonMenu("Listado De Tratamientos", false);
        JPanel btnModificar    = crearBotonMenu("Modificar Datos",         false);
        JPanel btnAsistencias  = crearBotonMenu("Asistencias",            false);

        botonesPanel.add(btnRegistro);
        botonesPanel.add(btnListado);
        botonesPanel.add(btnTratamientos);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnAsistencias);

        panelMenu.add(botonesPanel, BorderLayout.NORTH);

        // ── BOTÓN SALIR ──
        JPanel panelSalir = new JPanel(new BorderLayout());
        panelSalir.setBackground(VERDE_MENU);
        panelSalir.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JPanel lineaSalir = new JPanel();
        lineaSalir.setBackground(DORADO);
        lineaSalir.setPreferredSize(new Dimension(0, 1));
        panelSalir.add(lineaSalir, BorderLayout.NORTH);

        JButton btnSalir = new JButton("SALIR");
        btnSalir.setBackground(new Color(120, 20, 20));
        btnSalir.setForeground(BLANCO);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSalir.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DORADO, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setPreferredSize(new Dimension(0, 50));
        btnSalir.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnSalir.setBackground(new Color(160, 30, 30)); }
            public void mouseExited(MouseEvent e)  { btnSalir.setBackground(new Color(120, 20, 20)); }
        });
        btnSalir.addActionListener(e -> System.exit(0));
        panelSalir.add(btnSalir, BorderLayout.SOUTH);
        panelMenu.add(panelSalir, BorderLayout.SOUTH);

        JPanel lineaDerecha = new JPanel();
        lineaDerecha.setBackground(DORADO);
        lineaDerecha.setPreferredSize(new Dimension(2, 0));

        menuConLinea.add(panelMenu, BorderLayout.CENTER);
        menuConLinea.add(lineaDerecha, BorderLayout.EAST);

        // ── NAVEGACIÓN — un solo listener por botón ──
        btnRegistro.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { new frmpaciente().setVisible(true); dispose(); }
        });
        btnListado.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { new frmlistado().setVisible(true); dispose(); }
        });
        btnTratamientos.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { new frmtratamientos().setVisible(true); dispose(); }
        });
        btnModificar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { new frmmodificar().setVisible(true); dispose(); }
        });
        btnAsistencias.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { new frmAsistencias().setVisible(true); dispose(); }
        });

        raiz.add(menuConLinea, BorderLayout.WEST);

        // ── CONTENIDO (imagen de fondo) ──
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(VERDE_OSCURO);

        JLabel lblFondo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon icon = new ImageIcon(
                        getClass().getResource("/Imagenes/Fisioterapia.png"));
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception ex) {}
            }
        };
        panelContenido.add(lblFondo, BorderLayout.CENTER);
        raiz.add(panelContenido, BorderLayout.CENTER);

        setContentPane(raiz);
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
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(VERDE_HOVER);
                    lbl.setForeground(DORADO_CLARO);
                    indicador.setVisible(true);
                }
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(VERDE_MENU);
                    lbl.setForeground(TEXTO_MENU);
                    indicador.setVisible(false);
                }
            });
        }
        return panel;
    }

    public static void main(String args[]) {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(() -> new frmmenu().setVisible(true));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1240, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}