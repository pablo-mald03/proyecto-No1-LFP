/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author pablo
 */
public class CrearTableros {

    private JTable tablero1;

    public CrearTableros(JTable tablero) {
        this.tablero1 = tablero;
    }

    //METODO PARA RECAPTAR LA REFERENCIA
    public void setTablero(JTable tablero) {
        this.tablero1 = tablero;
    }

    //Metodo que ayuda a generar tablas con titulos para reportes o lo que se requiera tener titulo
    public void tableroConTitulo(String[] titulo, int filas, int columnas, boolean redimension) {

        //Se le desactiva el modo edicion 
        DefaultTableModel modeloTablero = new DefaultTableModel(filas, columnas);

        tablero1.setModel(modeloTablero);

        //Se desactiva la edicion para el Jtable
        for (int i = 0; i < columnas; i++) {

            tablero1.getColumnModel().getColumn(i).setCellEditor(null);
        }

        //Prohibe las ediciones y que el usuario pueda modificar celdas 
        tablero1.setRowSelectionAllowed(false);
        tablero1.setColumnSelectionAllowed(false);
        tablero1.setCellSelectionEnabled(false);
        tablero1.setFocusable(false);
        tablero1.setRequestFocusEnabled(false);

        //Ciclo que vacia a los header 
        for (int i = 0; i < columnas; i++) {
            tablero1.getColumnModel().getColumn(i).setHeaderValue(titulo[i]);
        }

        JTableHeader tableHeader = tablero1.getTableHeader();
        tableHeader.setFont(new Font("Liberation Sans", Font.BOLD, 16));

        //Redimensiona el tablero 
        tablero1.getTableHeader().repaint();

        if (redimension) {
            redimensionarTablero();
        }

    }

    //METODO QUE SE ENCARGA DE GENERAR EL TABLERO  
    public void generarTablero(int filas, int columnas, boolean redimension) {

        //Se le desactiva el modo edicion 
        TableroModel modeloTablero = new TableroModel(filas, columnas);

        tablero1.setModel(modeloTablero);

        //Se desactiva la edicion para el Jtable
        for (int i = 0; i < columnas; i++) {

            tablero1.getColumnModel().getColumn(i).setCellEditor(null);
        }

        //Prohibe las ediciones y que el usuario pueda modificar celdas 
        tablero1.setRowSelectionAllowed(false);
        tablero1.setColumnSelectionAllowed(false);
        tablero1.setCellSelectionEnabled(false);
        tablero1.setFocusable(false);
        tablero1.setRequestFocusEnabled(false);

        //Ciclo que vacia a los header 
        for (int i = 0; i < columnas; i++) {
            tablero1.getColumnModel().getColumn(i).setHeaderValue("");
        }

        //Redimensiona el tablero 
        tablero1.getTableHeader().repaint();

        if (redimension) {
            redimensionarTablero();
        }

    }

    //Metodo que redimensiona el tablero acorde al contenido recibido
    public void redimensionarTablero() {

        int alturadisponible = tablero1.getParent().getHeight();

        int cantidadFilas = tablero1.getRowCount();

        if (alturadisponible > 0 && cantidadFilas > 0) {

            int alturaIdeal = alturadisponible / cantidadFilas;

            //Propiedades predefinidas 
            int alturaMin = 25;  // No más pequeño que esto
            int alturaMax = 120;

            int AlturaFinal = Math.max(alturaMin, Math.min(alturaIdeal, alturaMax));

            //Se definen las dimensiones apropiadas
            tablero1.setRowHeight(AlturaFinal);
        }

    }

    public void vaciarTablero() {
        TableroModel modeloTablero = new TableroModel(0, 0);
        tablero1.setModel(modeloTablero);
        tablero1.getTableHeader().repaint();
    }
}
