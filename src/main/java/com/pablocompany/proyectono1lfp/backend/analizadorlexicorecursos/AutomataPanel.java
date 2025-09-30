/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexicorecursos;

import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Lexema;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Nodo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author pablo
 */
//Clase encargada de crear la imagen de los estados del automata 
public class AutomataPanel extends JPanel {

    //Atributo que permite saber el lexema que se va a diagramar 
    private Lexema lexemaAutomata;

    public AutomataPanel(Lexema lexemaReconocido) {
        this.lexemaAutomata = lexemaReconocido;
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        int total = lexemaAutomata.getLongitudNodo();
        int ancho = 180 * total;
        int alto = 300;
        return new Dimension(ancho, alto);
    }

    //Metodo que sirve para poder dibujar las transiciones del automata con el lexema
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 100;
        int y = 100;
        int radio = 80; // 🔹 ahora más grande

        int total = this.lexemaAutomata.getLongitudNodo();

        // Tirangulito de estado inicial
        int[] tx = {x - 20, x - 50, x - 50};
        int[] ty = {y + radio / 2, y + radio / 2 - 10, y + radio / 2 + 10};
        g2d.fillPolygon(tx, ty, 3);

        // 🔹 Fuente más grande
        g2d.setFont(new Font("Liberation Sans", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();

        int numeroEstado = 0;
        for (int i = 0; i < total; i++) {
            Nodo nodoUbicado = this.lexemaAutomata.getValorNodo(i);

            // Dibuja el estado
            g2d.drawOval(x, y, radio, radio);

            // Texto centrado en la bolita
            String textoEstado = "q" + i;
            int textWidth = fm.stringWidth(textoEstado);
            int textHeight = fm.getAscent();
            int textX = x + (radio - textWidth) / 2;
            int textY = y + (radio + textHeight) / 2 - 5;
            g2d.drawString(textoEstado, textX, textY);

            // 🔹 Dibujar transición si no es el último
            if (i < total) {
                int nextX = x + 150;

                // Línea recta
                g2d.drawLine(x + radio, y + radio / 2, nextX, y + radio / 2);

                // Flechita
                int[] px = {nextX, nextX - 10, nextX - 10};
                int[] py = {y + radio / 2, y + radio / 2 - 5, y + radio / 2 + 5};
                g2d.fillPolygon(px, py, 3);

                // Etiqueta de transición centrada
                String trans = String.valueOf(nodoUbicado.getCaracter());
                
                if(trans.isBlank()){
                    trans = "\'  \'";
                }
 
                int transWidth = fm.stringWidth(trans);
                int midX = x + radio + ((nextX - (x + radio)) / 2);
                g2d.drawString(trans, midX - transWidth / 2, y + radio / 2 - 10);
            }

            x += 150;

            numeroEstado = i;
        }
      
        g2d.drawOval(x, y, radio, radio);

    
        String textoEstado = "q" + (numeroEstado + 1);
        int textWidth = fm.stringWidth(textoEstado);
        int textHeight = fm.getAscent();
        int textX = x + (radio - textWidth) / 2;
        int textY = y + (radio + textHeight) / 2 - 5;
        g2d.drawString(textoEstado, textX, textY);
        
        g2d.drawOval(x - 3, y - 3, radio + 7, radio + 7);

    }
    
}
