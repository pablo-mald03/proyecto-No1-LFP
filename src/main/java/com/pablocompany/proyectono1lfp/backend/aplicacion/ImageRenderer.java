/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author pablo
 */
public class ImageRenderer extends javax.swing.table.DefaultTableCellRenderer{
    
     @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof JLabel) {
            JLabel label = (JLabel) value;

            if (label.getIcon() instanceof ImageIcon) {
                
                ImageIcon originalIcon = (ImageIcon) label.getIcon();
                
                int ancho = table.getColumnModel().getColumn(column).getWidth();
                int alto = table.getRowHeight(row);

                // Redimensiona SOLO si cambia el tamaño para no hacerlo innecesariamente
                Image imagenEscalada = originalIcon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(imagenEscalada));
                label.setHorizontalAlignment(JLabel.CENTER); 
            }

            return label;
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
