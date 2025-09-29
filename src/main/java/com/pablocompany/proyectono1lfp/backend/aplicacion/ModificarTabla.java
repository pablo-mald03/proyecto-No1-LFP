/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pablo
 */
public class ModificarTabla {

    //Atributo que apunta hacia el Jtable
    private JTable tableroModificado;

    public ModificarTabla(JTable tablero) {
        tableroModificado = tablero;

    }

    //METODO PARA RECAPTAR LA REFERENCIA
    public void referenciar(JTable tablero) {
        this.tableroModificado = tablero;
    }

    //Metodo que se encarga de reenderizar el tablero
    public void reendereizarTablero() {
        // Aplicar el renderer a todas las columnas
        for (int i = 0; i < tableroModificado.getColumnCount(); i++) {
            tableroModificado.getColumnModel().getColumn(i).setCellRenderer(new ImageRenderer());
        }
    }

    //METODOS QUE SE ENCARGAN DE DEFINIR O MODIFICAR LAS CELDAS ACORDE A LAS ACCIONES QUE SE EJECUTEN 
    public void colocarPanoramas(int fila, int columna, JLabel icono) {
        this.tableroModificado.setValueAt(icono, fila, columna);
    }

    //METODO PARA COLOCAR LETRAS SOBRE IMAGENES DEL TABLERO (PENDIENTE)
    public void celdaTexto(int fila, int columna, String valor, Color color) {

        JLabel etiquetanumero = new JLabel(valor, SwingConstants.CENTER);
        etiquetanumero.setFont(new Font("Liberation Sans", Font.BOLD, 20));
        etiquetanumero.setOpaque(true);
        etiquetanumero.setBackground(color);

        //Inserta El label en la columna 
        this.tableroModificado.setValueAt(etiquetanumero, fila, columna);
    }

    //Metodo exclusivo para crear labels con texto dentro del Jtable
    public void colocarTextos(int fila, int columna, String texto) {
        JLabel labelText = new JLabel(texto, SwingConstants.CENTER);
        labelText.setFont(new Font("Liberation Sans", Font.BOLD, 15));
        labelText.setOpaque(false);
        this.tableroModificado.setValueAt(labelText, fila, columna);
    }

    public void colocarTextoIcono(int fila, int columna, ImageIcon labelConIcono, String texto) {

        // Crear icono con el texto sobre la imagen
        Icon iconoConTexto = crearIconoConTexto(labelConIcono, texto);

        // Crear JLabel con ese nuevo icono
        JLabel labelModificado = new JLabel(iconoConTexto);
        labelModificado.setHorizontalAlignment(SwingConstants.CENTER);
        labelModificado.setVerticalAlignment(SwingConstants.CENTER);

        tableroModificado.setValueAt(labelModificado, fila, columna);
    }

    //Metodo que permite colocar textos con color de fondo al tablero
    public void colocarTextoColor(int fila, int columna, ImageIcon labelConIcono, String texto, Color color) {

        // Crear icono con el texto sobre la imagen
        Icon iconoConTexto = crearIconoConTexto(labelConIcono, texto, color);

        // Crear JLabel con ese nuevo icono
        JLabel labelModificado = new JLabel(iconoConTexto);
        labelModificado.setHorizontalAlignment(SwingConstants.CENTER);
        labelModificado.setVerticalAlignment(SwingConstants.CENTER);

        tableroModificado.setValueAt(labelModificado, fila, columna);
    }

    //Metodo que actualiza el tablero
    public void repintarTablero() {
        tableroModificado.revalidate();
        tableroModificado.repaint();
    }

    //Metodo para ilustrar la cantidad de objetos en el inventario 
    private Icon crearIconoConTexto(ImageIcon baseIcon, String texto) {
        int width = baseIcon.getIconWidth();
        int height = baseIcon.getIconHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graficaTexto = image.createGraphics();

        // Dibuja el icono base
        graficaTexto.drawImage(baseIcon.getImage(), 0, 0, null);

        int tamanioFuente = (int) (height * 0.35);

        graficaTexto.setFont(new Font("Liberation Sans", Font.BOLD, tamanioFuente));
        graficaTexto.setColor(Color.WHITE);

        FontMetrics fm = graficaTexto.getFontMetrics();

        int textWidth = fm.stringWidth(texto);
        int x = (width - textWidth) / 2;
        int y = (height + fm.getAscent()) / 2 - 4;

        graficaTexto.drawString(texto, x, y);

        graficaTexto.dispose();

        return new ImageIcon(image);
    }

    //Metodo para ilustrar la cantidad de objetos con el texto personalizado
    private Icon crearIconoConTexto(ImageIcon baseIcon, String texto, Color colorDado) {
        int width = baseIcon.getIconWidth();
        int height = baseIcon.getIconHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graficaTexto = image.createGraphics();

        // Dibuja el icono base
        graficaTexto.drawImage(baseIcon.getImage(), 0, 0, null);

        int tamanioFuente = (int) (height * 0.35);

        graficaTexto.setFont(new Font("Liberation Sans", Font.BOLD, tamanioFuente));
        graficaTexto.setColor(colorDado);

        FontMetrics fm = graficaTexto.getFontMetrics();

        int textWidth = fm.stringWidth(texto);
        int x = (width - textWidth) / 2;
        int y = (height + fm.getAscent()) / 2 - 4;

        graficaTexto.drawString(texto, x, y);

        graficaTexto.dispose();

        return new ImageIcon(image);
    }

    //Metodo que permite manejar un tablero dinamico en el que se generan consultas de SQL(NO EDITABLE)
    public DefaultTableModel generarConsulta(String sentenciaSQL, Connection referencia) throws SQLException {
        Connection conexion = referencia;

        String sql = sentenciaSQL;

        try (PreparedStatement ps = conexion.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY); ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();

            // Crear títulos dinámicos
            String[] titulos = new String[columnas];
            for (int i = 0; i < columnas; i++) {
                titulos[i] = metaData.getColumnLabel(i + 1);
            }

            // Crear modelo vacío con títulos
            // Crear modelo vacío con títulos
            DefaultTableModel modelo = new DefaultTableModel(titulos, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Llenar datos si hay resultados
            boolean hayDatos = false;
            while (rs.next()) {
                hayDatos = true;
                Object[] fila = new Object[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
            }

            // Si no hay datos, podrías mostrar un mensaje
            if (!hayDatos) {
                return null;
            }

            return modelo;
        }
    }

    //Retorna la instancia de tablero relacionada
    public JTable retornarTablero() {
        return this.tableroModificado;
    }

    //Metodo muy util para repintar celdas
    public void repintarCelda(int fila, int columna) {
        ((AbstractTableModel) tableroModificado.getModel()).fireTableCellUpdated(fila, columna);
    }

}
