/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pablocompany.proyectono1lfp.frontend.aplicacion;

import com.pablocompany.proyectono1lfp.backend.analizadorlexico.AnalizadorLexico;
import com.pablocompany.proyectono1lfp.backend.analizadorlexicorecursos.LectorEntradas;
import com.pablocompany.proyectono1lfp.backend.analizadorlexicorecursos.ManejadorArchivos;
import com.pablocompany.proyectono1lfp.backend.aplicacion.ColocarFondos;
import com.pablocompany.proyectono1lfp.backend.aplicacion.IlustrarLabels;
import com.pablocompany.proyectono1lfp.backend.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ConfigException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorEncontradoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author pablo
 */
public class MenuPrincipal extends javax.swing.JFrame {

    //Variable que permite saber que menu de operaciones se despliegara
    private int gestionVentanas;

    //instancia que permite subir archivos de texto
    private ManejadorArchivos manipuladorDirectorios;
    private LectorEntradas leerEntradas;

    //Atributo que permite saber si el archivo ya fue cargado
    //true si el archivo ya se cargo
    //false si el archivo no se ha cargado
    private boolean yaCargado;

    /**
     * Creates new form MenuPrincipal
     */
    public MenuPrincipal() {
        initComponents();

        this.setLocationRelativeTo(null);

        ColocarFondos pintarPanel = new ColocarFondos(this, this.panelPrincipal);

        pintarPanel.pintarPaneles("/com/pablocompany/proyectono1/recursosapp/images/overlay2.png");

        ImageIcon icono = new ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/perfildef.png"));

        IlustrarLabels labelPerfil = new IlustrarLabels(this.panelBarraPrincipal, 60, 60, "", this.lblPerfil);
        labelPerfil.cambiarLabel(icono);

        ImageIcon iconoMedio = new ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/insertar.png"));

        IlustrarLabels labelMedio = new IlustrarLabels(this.panelBarraPrincipal, 50, 50, "", this.lblEleccion);
        labelMedio.cambiarLabel(iconoMedio);

        this.txtLogBusquedas.setEditable(false);
        this.txtAreaDirectorioArchivo.setEditable(false);
        this.textLogErrores.setEditable(false);
        this.textEdicionArchivo.setEditable(true);
        this.textEdicionArchivo.setCaretColor(Color.BLACK);

        this.gestionVentanas = 0;
        this.yaCargado = false;
        this.txtBusquedas.setVisible(false);

        //Se instancia la clase para poder operar con archivos de texto
        this.manipuladorDirectorios = new ManejadorArchivos();

        //Instanica que permite leer los archivos
        this.leerEntradas = new LectorEntradas();

    }

    //===========================================APARTADO DE METODOS QUE SE UTILIZAN PARA DINAMIZAR LA UI===========================================
    //Metodo de UI que permite nombrar algo y no permite vacios
    private String pedirNombre(String mensaje) {
        String entrada = JOptionPane.showInputDialog(null, mensaje);

        // Si el usuario presiona "Cancelar" o deja vacío
        if (entrada == null || entrada.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No puedes dejarlo en blanco. Intenta de nuevo.");
            return pedirNombre(mensaje);
        }

        return entrada.trim().replace(" ", "_");
    }

    //Metodo que sirve para poder mostrar la seleccion de la busqueda de palabras
    //1 busqueda de palabras
    //0 reinicia la UI PRINCIPAL
    public void mostrarBusquedas() {

        ImageIcon iconoMedio = new ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/busquedaDatos.png"));

        IlustrarLabels labelMedio = new IlustrarLabels(this.panelBarraPrincipal, 50, 50, "", this.lblEleccion);
        labelMedio.cambiarLabel(iconoMedio);

        this.lblEleccionesDadas.setText("Busqueda de Patrones");

        //Reinicia el permiso para accionar botones
        this.gestionVentanas = 1;

        operarBusquedas();

        this.btnConfig.setBackground(new Color(0x323844));
        this.btnGenerarReportes.setBackground(new Color(0x323844));
        this.btnBusquedaPatrones.setBackground(new Color(0x2DB20C));

    }

    //Metodo que genera la interaccion entre modificar el archivo de configuracion
    public void iniciarModoDepuracion() {

        ImageIcon iconoMedio = new ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/subirArchivo.png"));

        IlustrarLabels labelMedio = new IlustrarLabels(this.panelBarraPrincipal, 50, 50, "", this.lblEleccion);
        labelMedio.cambiarLabel(iconoMedio);
        this.lblEleccionesDadas.setText("Editar Configuracion");

        this.btnBusquedaPatrones.setBackground(new Color(0x323844));
        this.btnGenerarReportes.setBackground(new Color(0x323844));
        this.btnConfig.setBackground(new Color(0x2DB20C));
        modoDepuracion();

    }

    //Metodo que genera la interaccion entre generar reportes
    public void generarReportes() {

        ImageIcon iconoMedio = new ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/reportes.png"));

        IlustrarLabels labelMedio = new IlustrarLabels(this.panelBarraPrincipal, 50, 50, "", this.lblEleccion);
        labelMedio.cambiarLabel(iconoMedio);
        this.lblEleccionesDadas.setText("Generar Reportes");

        this.btnBusquedaPatrones.setBackground(new Color(0x323844));
        this.btnConfig.setBackground(new Color(0x323844));
        this.btnGenerarReportes.setBackground(new Color(0x2DB20C));
        operarReportes();

    }

    //Metodo que se encarga de regresar a la interfaz inicial
    public void regresarInicio() {

        ImageIcon iconoMedio = new ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/insertar.png"));

        IlustrarLabels labelMedio = new IlustrarLabels(this.panelBarraPrincipal, 50, 50, "", this.lblEleccion);
        labelMedio.cambiarLabel(iconoMedio);
        this.lblEleccionesDadas.setText("Edicion de Archivos");

        //Reinicia el permiso para accionar botones
        this.gestionVentanas = 0;

        this.btnBusquedaPatrones.setBackground(new Color(0x323844));
        this.btnConfig.setBackground(new Color(0x323844));
        this.btnGenerarReportes.setBackground(new Color(0x323844));

        reestablecerUI();

    }

    //===================================FIN DEL APARTADO DE METODOS QUE SE UTILIZAN PARA DINAMIZAR LA UI===========================================
    //Metodo que ayuda a poder tomar decisiones en el menu
    public boolean tomarDecision(String mensaje, String Titulo) {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                Titulo,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return opcion == JOptionPane.YES_OPTION;
    }

    //========================================REGION DE METODOS UTILIZADOS PARA CADA FUNCIONALIDAD==============================
    //Metodo que se utiliza para manejar todos los componentes y funciones previas al muestreo de busquedas
    public void operarBusquedas() {

        this.textEdicionArchivo.setEditable(false);
        this.btnAnalisis.setVisible(false);
        this.scrollErroresLog.setVisible(false);
        this.scrollBusquedas.setVisible(true);
        this.txtBusquedas.setText("");
        this.txtLogBusquedas.setText("");
        this.lblMostrarError.setText("Resultados de busqueda:");
        this.lblAnalisis.setText("Busqueda de patrones:");
        this.txtBusquedas.setVisible(true);
        this.btnSubirArchivo.setEnabled(false);
        this.btnGuardarArchivo.setText("Buscar Patron");
        this.btnQuitarArchivo.setEnabled(false);
        this.btnGuardarArchivo.setEnabled(true);

        this.txtBusquedas.requestFocusInWindow();

    }

    //Metodo que se utiliza para manejar todos los componentes y funciones previas a la configuracion de instrucciones
    public void modoDepuracion() {
        //Se despliega la ventana emergente para editar la configuracion
        /* EditarConfiguracion dialog = new EditarConfiguracion(this, true, leerEntradas, textEdicionArchivo, textLogErrores);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {

                if (gestionVentanas == 0) {
                    regresarInicio();
                }

                if (gestionVentanas == 1) {
                    mostrarBusquedas();
                }

            }
        });
        dialog.setVisible(true);*/

    }

    //Metodo que se utiliza para manejar todos los componentes y funciones previas a la generacion de reportes
    public void operarReportes() {
        //Se despliega la ventana emergente para La generacion de reportes
        /* MostrarReportes dialog = new MostrarReportes(this, true, leerEntradas);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {

                if (gestionVentanas == 0) {
                    regresarInicio();
                }

                if (gestionVentanas == 1) {
                    mostrarBusquedas();
                }

            }
        });
        dialog.setVisible(true);*/

    }

    //Metodo que se utiliza para manejar todos los componentes y funciones previas a la generacion de reportes
    public void reestablecerUI() {
        this.textEdicionArchivo.setEditable(true);
        this.btnAnalisis.setVisible(true);
        this.txtLogBusquedas.setText("");
        this.scrollErroresLog.setVisible(true);
        this.scrollBusquedas.setVisible(false);
        this.txtBusquedas.setText("");
        this.lblAnalisis.setText("Analizar Manualmente:");
        this.lblMostrarError.setText("Errores Encontrados:");
        this.txtBusquedas.setVisible(false);
        this.btnSubirArchivo.setEnabled(true);
        this.btnGuardarArchivo.setText("Guardar Texto");
        this.btnQuitarArchivo.setEnabled(true);

        if (this.yaCargado) {
            this.btnGuardarArchivo.setEnabled(false);
        }

    }

    //========================================FIN DE LA REGION DE METODOS UTILIZADOS PARA CADA FUNCIONALIDAD==============================
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        panelBarraPrincipal = new javax.swing.JPanel();
        lblEleccion = new javax.swing.JLabel();
        lblAdmin = new javax.swing.JLabel();
        lblPerfil = new javax.swing.JLabel();
        lblEleccionesDadas = new javax.swing.JLabel();
        lblHome = new javax.swing.JLabel();
        labelOperaciones1 = new javax.swing.JLabel();
        scrollAreaEdicion = new javax.swing.JScrollPane();
        textEdicionArchivo = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        labelDatos = new javax.swing.JLabel();
        btnSubirArchivo = new javax.swing.JButton();
        lblTitulo = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaDirectorioArchivo = new javax.swing.JTextArea();
        btnQuitarArchivo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        scrollErroresLog = new javax.swing.JScrollPane();
        textLogErrores = new javax.swing.JTextPane();
        lblMostrarError = new javax.swing.JLabel();
        btnGuardarArchivo = new javax.swing.JButton();
        btnAnalisis = new javax.swing.JButton();
        txtBusquedas = new javax.swing.JTextField();
        lblAnalisis = new javax.swing.JLabel();
        scrollBusquedas = new javax.swing.JScrollPane();
        txtLogBusquedas = new javax.swing.JTextPane();
        barraLateral = new javax.swing.JPanel();
        btnBusquedaPatrones = new javax.swing.JButton();
        btnGenerarReportes = new javax.swing.JButton();
        btnConfig = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Analizador Lexico");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelBarraPrincipal.setBackground(new java.awt.Color(57, 5, 87));
        panelBarraPrincipal.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        lblAdmin.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        lblAdmin.setForeground(new java.awt.Color(255, 255, 255));
        lblAdmin.setText("Usuario");

        lblEleccionesDadas.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        lblEleccionesDadas.setForeground(new java.awt.Color(255, 255, 255));
        lblEleccionesDadas.setText("Edicion de Archivos");

        lblHome.setBackground(new java.awt.Color(71, 7, 110));
        lblHome.setPreferredSize(new java.awt.Dimension(60, 60));

        javax.swing.GroupLayout panelBarraPrincipalLayout = new javax.swing.GroupLayout(panelBarraPrincipal);
        panelBarraPrincipal.setLayout(panelBarraPrincipalLayout);
        panelBarraPrincipalLayout.setHorizontalGroup(
            panelBarraPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBarraPrincipalLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAdmin)
                .addGap(472, 472, 472)
                .addComponent(lblEleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEleccionesDadas, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        panelBarraPrincipalLayout.setVerticalGroup(
            panelBarraPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBarraPrincipalLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblEleccionesDadas, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelBarraPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBarraPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBarraPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panelBarraPrincipalLayout.createSequentialGroup()
                            .addGroup(panelBarraPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblEleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(11, 11, 11)))
                    .addComponent(lblHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        labelOperaciones1.setFont(new java.awt.Font("Liberation Sans", 1, 30)); // NOI18N
        labelOperaciones1.setForeground(new java.awt.Color(83, 31, 11));
        labelOperaciones1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOperaciones1.setText("Archivo de entrada:");

        textEdicionArchivo.setBackground(new java.awt.Color(228, 228, 228));
        textEdicionArchivo.setFont(new java.awt.Font("Liberation Serif", 1, 20)); // NOI18N
        textEdicionArchivo.setForeground(new java.awt.Color(115, 112, 112));
        textEdicionArchivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textEdicionArchivoKeyReleased(evt);
            }
        });
        scrollAreaEdicion.setViewportView(textEdicionArchivo);

        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 5, 5, 5, new java.awt.Color(50, 56, 68)));
        jPanel1.setOpaque(false);

        labelDatos.setFont(new java.awt.Font("Liberation Sans", 1, 32)); // NOI18N
        labelDatos.setForeground(new java.awt.Color(83, 31, 11));
        labelDatos.setText("Requisitos de carga de archivos:");

        btnSubirArchivo.setBackground(new java.awt.Color(48, 148, 92));
        btnSubirArchivo.setFont(new java.awt.Font("Liberation Sans", 1, 22)); // NOI18N
        btnSubirArchivo.setForeground(new java.awt.Color(255, 255, 255));
        btnSubirArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/uploadFileIcon.png"))); // NOI18N
        btnSubirArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubirArchivoActionPerformed(evt);
            }
        });

        lblTitulo.setFont(new java.awt.Font("Liberation Sans", 1, 28)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(42, 48, 60));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitulo.setText("Directorio Archivo:");

        txtAreaDirectorioArchivo.setColumns(20);
        txtAreaDirectorioArchivo.setFont(new java.awt.Font("Liberation Sans", 0, 26)); // NOI18N
        txtAreaDirectorioArchivo.setRows(5);
        jScrollPane3.setViewportView(txtAreaDirectorioArchivo);

        btnQuitarArchivo.setBackground(new java.awt.Color(148, 47, 47));
        btnQuitarArchivo.setFont(new java.awt.Font("Liberation Sans", 1, 22)); // NOI18N
        btnQuitarArchivo.setForeground(new java.awt.Color(255, 255, 255));
        btnQuitarArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pablocompany/proyectono1/recursosapp/images/eliminarIcon.png"))); // NOI18N
        btnQuitarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarArchivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(labelDatos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSubirArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnQuitarArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDatos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubirArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuitarArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 5, 5, 5, new java.awt.Color(50, 56, 68)));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(null);

        textLogErrores.setBackground(new java.awt.Color(228, 228, 228));
        textLogErrores.setFont(new java.awt.Font("Liberation Serif", 1, 20)); // NOI18N
        textLogErrores.setForeground(new java.awt.Color(140, 1, 25));
        textLogErrores.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textLogErroresKeyReleased(evt);
            }
        });
        scrollErroresLog.setViewportView(textLogErrores);

        jPanel2.add(scrollErroresLog);
        scrollErroresLog.setBounds(10, 160, 660, 380);

        lblMostrarError.setFont(new java.awt.Font("Liberation Sans", 1, 32)); // NOI18N
        lblMostrarError.setForeground(new java.awt.Color(83, 31, 11));
        lblMostrarError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMostrarError.setText("Errores Encontrados:");
        jPanel2.add(lblMostrarError);
        lblMostrarError.setBounds(10, 120, 650, 37);

        btnGuardarArchivo.setBackground(new java.awt.Color(19, 115, 146));
        btnGuardarArchivo.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnGuardarArchivo.setText("Guardar Texto");
        btnGuardarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarArchivoActionPerformed(evt);
            }
        });
        jPanel2.add(btnGuardarArchivo);
        btnGuardarArchivo.setBounds(420, 60, 203, 50);

        btnAnalisis.setBackground(new java.awt.Color(46, 136, 80));
        btnAnalisis.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnAnalisis.setText("Analizar Texto");
        btnAnalisis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalisisActionPerformed(evt);
            }
        });
        jPanel2.add(btnAnalisis);
        btnAnalisis.setBounds(30, 60, 203, 50);

        txtBusquedas.setBackground(new java.awt.Color(228, 228, 228));
        txtBusquedas.setFont(new java.awt.Font("Liberation Serif", 0, 24)); // NOI18N
        txtBusquedas.setForeground(new java.awt.Color(63, 58, 58));
        jPanel2.add(txtBusquedas);
        txtBusquedas.setBounds(30, 60, 370, 50);

        lblAnalisis.setFont(new java.awt.Font("Liberation Sans", 1, 32)); // NOI18N
        lblAnalisis.setForeground(new java.awt.Color(83, 31, 11));
        lblAnalisis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAnalisis.setText("Analizar Manualmente:");
        jPanel2.add(lblAnalisis);
        lblAnalisis.setBounds(10, 10, 650, 37);

        txtLogBusquedas.setBackground(new java.awt.Color(228, 228, 228));
        txtLogBusquedas.setFont(new java.awt.Font("Liberation Serif", 1, 20)); // NOI18N
        txtLogBusquedas.setForeground(new java.awt.Color(140, 1, 25));
        txtLogBusquedas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLogBusquedasKeyReleased(evt);
            }
        });
        scrollBusquedas.setViewportView(txtLogBusquedas);

        jPanel2.add(scrollBusquedas);
        scrollBusquedas.setBounds(10, 160, 660, 380);

        barraLateral.setBackground(new java.awt.Color(57, 5, 87));
        barraLateral.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        barraLateral.setForeground(new java.awt.Color(0, 0, 0));

        btnBusquedaPatrones.setBackground(new java.awt.Color(50, 56, 68));
        btnBusquedaPatrones.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnBusquedaPatrones.setForeground(new java.awt.Color(255, 255, 255));
        btnBusquedaPatrones.setText("Busqueda de Patrones");
        btnBusquedaPatrones.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        btnBusquedaPatrones.setFocusable(false);
        btnBusquedaPatrones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBusquedaPatronesActionPerformed(evt);
            }
        });

        btnGenerarReportes.setBackground(new java.awt.Color(50, 56, 68));
        btnGenerarReportes.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnGenerarReportes.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarReportes.setText("Generar Reportes");
        btnGenerarReportes.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        btnGenerarReportes.setFocusable(false);
        btnGenerarReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarReportesActionPerformed(evt);
            }
        });

        btnConfig.setBackground(new java.awt.Color(50, 56, 68));
        btnConfig.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnConfig.setForeground(new java.awt.Color(255, 255, 255));
        btnConfig.setText("Iniciar Modo Depuracion");
        btnConfig.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)));
        btnConfig.setFocusable(false);
        btnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout barraLateralLayout = new javax.swing.GroupLayout(barraLateral);
        barraLateral.setLayout(barraLateralLayout);
        barraLateralLayout.setHorizontalGroup(
            barraLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barraLateralLayout.createSequentialGroup()
                .addGap(294, 294, 294)
                .addComponent(btnBusquedaPatrones, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGenerarReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(294, Short.MAX_VALUE))
        );
        barraLateralLayout.setVerticalGroup(
            barraLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barraLateralLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(barraLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBusquedaPatrones, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerarReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBarraPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollAreaEdicion, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)
                    .addComponent(labelOperaciones1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(barraLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addComponent(panelBarraPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(labelOperaciones1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollAreaEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(barraLateral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(panelPrincipal, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textEdicionArchivoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textEdicionArchivoKeyReleased

        int code = evt.getKeyCode();
        if (code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_SHIFT
                || code == KeyEvent.VK_ALT
                || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN
                || code == KeyEvent.VK_TAB || code == KeyEvent.VK_CAPS_LOCK) {
            return; // ignorar estas teclas
        }

        if (this.textEdicionArchivo.getText().isBlank()) {
            return;
        }

        try {
            //Detecta cada vez que se cambia una palabra
            this.leerEntradas.transformarTexto(this.textEdicionArchivo.getText(), this.textEdicionArchivo);
            this.leerEntradas.analizarEntradas(this.textEdicionArchivo, this.textLogErrores);

        } catch (AnalizadorLexicoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Ejecucion", JOptionPane.ERROR_MESSAGE);
        } catch (ConfigException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de pintado", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_textEdicionArchivoKeyReleased

    private void btnSubirArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubirArchivoActionPerformed

        try {
            //Ejecuta la accion para Elegir el archivo

            if (this.manipuladorDirectorios.elegirArchivoEntrada()) {

                ArrayList<String> listaObtenida = this.manipuladorDirectorios.convertirEntrada();

                this.leerEntradas.transformarTexto(this.textEdicionArchivo.getText(), this.textEdicionArchivo);

                this.btnGuardarArchivo.setEnabled(false);

                this.leerEntradas.setLista(listaObtenida, this.textEdicionArchivo);

                this.txtAreaDirectorioArchivo.setText(this.manipuladorDirectorios.getPath());
                this.yaCargado = true;

                this.leerEntradas.analizarEntradas(this.textEdicionArchivo, this.textLogErrores);

            }

        } catch (BadLocationException | AnalizadorLexicoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Ejecucion", JOptionPane.ERROR_MESSAGE);
        } catch (ConfigException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnSubirArchivoActionPerformed

    private void btnQuitarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarArchivoActionPerformed
        //Permite cerrar el archivo que ya fue editado durante el proceso guardando los datos
        if (this.yaCargado) {

            if (tomarDecision("Deseas cerrar este archivo cargado\nSe guardaran todos los cambios hechos", "Confirmar cierre")) {

                try {
                    String directorio = this.manipuladorDirectorios.getPath();

                    this.leerEntradas.transformarTexto(this.textEdicionArchivo.getText(), this.textEdicionArchivo);

                    ArrayList<String> lista = this.leerEntradas.getListado();

                    this.manipuladorDirectorios.guardarArchivo(directorio, lista);

                    this.txtAreaDirectorioArchivo.setText("");
                    this.manipuladorDirectorios.reiniciarPath();
                    this.textEdicionArchivo.setText("");
                    this.yaCargado = false;
                    this.btnGuardarArchivo.setEnabled(true);

                } catch (AnalizadorLexicoException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Guardado", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Todavia no has cargado ningun archivo para poder cerrarlo", "No hay ningun archivo cargado aun", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_btnQuitarArchivoActionPerformed

    private void textLogErroresKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textLogErroresKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_textLogErroresKeyReleased

    private void btnGuardarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarArchivoActionPerformed
        //EJECUTA LAS ACCIONES RESPECTIVAS PARA PODER GUARDAR UN ARCHIVO

        if (this.gestionVentanas == 0) {
            if (this.textEdicionArchivo.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "No puedes guardar un archivo en blanco\nEscribe algo para poder guardarlo", "No tienes contenido definido", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                String path = this.leerEntradas.exportarArchivo();

                if (path != null) {

                    this.leerEntradas.transformarTexto(this.textEdicionArchivo.getText(), this.textEdicionArchivo);

                    ArrayList<String> lista = this.leerEntradas.getListado();

                    String nombreArchivo = pedirNombre("Escriba el nombre del archivo a exportar\nNo deje el espacio vacio");

                    this.manipuladorDirectorios.exportarArchivoCreado(path, lista, nombreArchivo);

                    JOptionPane.showMessageDialog(this, "Archivo exportado correctamente", "Archivo exportado", JOptionPane.INFORMATION_MESSAGE);

                    return;
                }

            } catch (ErrorPuntualException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Patron no Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } catch (AnalizadorLexicoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Guardado", JOptionPane.ERROR_MESSAGE);
            }

        }

        if (this.gestionVentanas == 1) {

            if (this.txtBusquedas.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "No hay ninguna palabra escrita\nEscribe alguna palabra para poder buscarlo", "Texto de busqueda Vacio", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            AnalizadorLexico lexer = this.leerEntradas.getLexerActual();

            if (lexer == null) {
                JOptionPane.showMessageDialog(this, "No hay texto procesado\nPrimero escriba patrones para poder buscarlos", "Texto de busqueda Vacio", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                lexer.busquedaPatrones(this.txtLogBusquedas, this.txtBusquedas.getText().trim().split(" "));

            } catch (BadLocationException ex) {
                JOptionPane.showMessageDialog(this, "No se ha podido imprimir el texto de busqueda", "Error de pintado", JOptionPane.INFORMATION_MESSAGE);

            } catch (ErrorEncontradoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Texto de busqueda Vacio", JOptionPane.INFORMATION_MESSAGE);

            } catch (ErrorPuntualException ex1) {
                JOptionPane.showMessageDialog(this, ex1.getMessage(), "Patron no Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }//GEN-LAST:event_btnGuardarArchivoActionPerformed

    private void btnAnalisisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalisisActionPerformed
        //Boton manual que permite analizar el texto
        if (this.textEdicionArchivo.getText().isBlank()) {
            return;
        }

        try {
            //Detecta cada vez que se cambia una palabra
            this.leerEntradas.transformarTexto(this.textEdicionArchivo.getText(), this.textEdicionArchivo);
            this.leerEntradas.analizarEntradas(this.textEdicionArchivo, this.textLogErrores);

        } catch (AnalizadorLexicoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Ejecucion", JOptionPane.ERROR_MESSAGE);
        } catch (ConfigException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de pintado", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAnalisisActionPerformed

    private void txtLogBusquedasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLogBusquedasKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLogBusquedasKeyReleased

    private void btnBusquedaPatronesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBusquedaPatronesActionPerformed
        //Lleva a la opcion de buscar palabras
        mostrarBusquedas();
    }//GEN-LAST:event_btnBusquedaPatronesActionPerformed

    private void btnGenerarReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarReportesActionPerformed
        //Boton que permite generar los reportes
        // generarReportes();

    }//GEN-LAST:event_btnGenerarReportesActionPerformed

    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        //Boton que despliega las opciones para editar el config
        //iniciarModoDepuracion();
    }//GEN-LAST:event_btnConfigActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Codiciona que se debe de confirmar si se quiere cerrar la aplicacion
        if (tomarDecision("Esta seguro que quieres salir de la aplicacion?", "Salir de la aplicacion")) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barraLateral;
    private javax.swing.JButton btnAnalisis;
    private javax.swing.JButton btnBusquedaPatrones;
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnGenerarReportes;
    private javax.swing.JButton btnGuardarArchivo;
    private javax.swing.JButton btnQuitarArchivo;
    private javax.swing.JButton btnSubirArchivo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelDatos;
    private javax.swing.JLabel labelOperaciones1;
    private javax.swing.JLabel lblAdmin;
    private javax.swing.JLabel lblAnalisis;
    private javax.swing.JLabel lblEleccion;
    private javax.swing.JLabel lblEleccionesDadas;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblMostrarError;
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelBarraPrincipal;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JScrollPane scrollAreaEdicion;
    private javax.swing.JScrollPane scrollBusquedas;
    private javax.swing.JScrollPane scrollErroresLog;
    private javax.swing.JTextPane textEdicionArchivo;
    private javax.swing.JTextPane textLogErrores;
    private javax.swing.JTextArea txtAreaDirectorioArchivo;
    private javax.swing.JTextField txtBusquedas;
    private javax.swing.JTextPane txtLogBusquedas;
    // End of variables declaration//GEN-END:variables
}
