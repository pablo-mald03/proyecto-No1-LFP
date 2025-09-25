/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 *
 * @author pablo
 */
public class ColocarFondos {
    
     private javax.swing.JFrame padre;

    private JPanel panelPintado;

    private javax.swing.JDialog modalDialog;
    
    private JInternalFrame parentPrincipal; 

    //constructor para pintar Frames 
    public ColocarFondos(JFrame padre, JPanel panelPrincipal) {
        this.padre = padre;
        this.panelPintado = panelPrincipal;

    }

    //Constructor especifico para pintar Modales
    public ColocarFondos(javax.swing.JDialog padre, JPanel PanelPrincipal) {
        this.modalDialog = padre;
        this.panelPintado = PanelPrincipal;

    }
    
    
    //Constructor especifico para pintar Frames internos
    public ColocarFondos(JInternalFrame padre, JPanel PanelPrincipal) {
        this.parentPrincipal = padre;
        this.panelPintado = PanelPrincipal;

    }

    //Metodo encargado de pintar los Frames internos 
    public void pintarInternos(String path) {
        PintarPaneles fondo = new PintarPaneles(path);

        fondo.setLayout(null);
        fondo.setSize(this.panelPintado.getSize()); 
        
        for (Component c : this.panelPintado.getComponents()) {
            fondo.add(c);
        }

        //Se reemplaza al panel anterior con el panel con la interfaz 
        this.parentPrincipal.getContentPane().remove(panelPintado);
        this.parentPrincipal.getContentPane().add(fondo);
        panelPintado = fondo;

        //Se revalida al panel que se llama 
        this.parentPrincipal.revalidate();
        this.parentPrincipal.repaint();
    }
    
    //Metodo encargado de pintar los Frames 
    public void pintarPaneles(String path) {
        PintarPaneles fondo = new PintarPaneles(path);

        fondo.setLayout(null);
        for (Component c : panelPintado.getComponents()) {
            fondo.add(c);
        }

        //Se reemplaza al panel anterior con el panel con la interfaz 
        this.padre.getContentPane().remove(panelPintado);
        this.padre.getContentPane().add(fondo);
        panelPintado = fondo;

        //Se revalida al panel que se llama 
        this.padre.revalidate();
        this.padre.repaint();
    }

    //Metodo encargado de pintar los modales
    public void pintarDialog(String path) {
        PintarPaneles fondo = new PintarPaneles(path);

        fondo.setLayout(null);
        for (Component c : panelPintado.getComponents()) {
            fondo.add(c);
        }

        //Se reemplaza al panel anterior con el panel con la interfaz 
        this.modalDialog.getContentPane().remove(panelPintado);
        this.modalDialog.getContentPane().add(fondo);
        panelPintado = fondo;

        //Se revalida al panel que se llama 
        this.modalDialog.revalidate();
        this.modalDialog.repaint();
    }
}
