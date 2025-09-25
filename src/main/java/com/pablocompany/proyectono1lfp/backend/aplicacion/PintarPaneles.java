/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author pablo
 */
public class PintarPaneles extends JPanel {
    
    
    //Variable que establece la imagen que se pinta en el panel
    private Image imagen;

    
    public PintarPaneles(String path) {
        imagen = new ImageIcon(getClass().getResource(path)).getImage();
     }

    //Metodo que pinta los componentes 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
