/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pablo
 */
public class TableroModel extends AbstractTableModel {

    private Object[][] data;

    public TableroModel(int filas, int columnas) {
        data = new Object[filas][columnas];
    }

    @Override
    public int getRowCount() {
        return data.length;

    }

    @Override
    public int getColumnCount() {
        if (data.length > 0) {
            return data[0].length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < getRowCount()
                && columnIndex >= 0 && columnIndex < getColumnCount()) {
            return data[rowIndex][columnIndex];
        }
        return null;
    }

    public void setValueAt(Object value, int row, int column) {
        if (row >= 0 && row < getRowCount() && column >= 0 && column < getColumnCount()) {
            data[row][column] = value;
            fireTableCellUpdated(row, column);
        }
    }
}
