/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexicorecursos;

import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Lexema;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Nodo;
import java.awt.Color;
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

    //Metodo que sirve para poder dibujar las transiciones del automata con el lexema
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 100;   
        int y = 100;
        int radio = 40;

        int total = this.lexemaAutomata.getLongitudNodo();

        //Tirangulito de estado inicial jeje
        int[] tx = {x - 50, x - 20, x - 20};
        int[] ty = {y + radio / 2, y + radio / 2 - 10, y + radio / 2 + 10};
        g2d.fillPolygon(tx, ty, 3);

        for (int i = 0; i < total; i++) {
            Nodo nodoUbicado = this.lexemaAutomata.getValorNodo(i);

        
            g2d.drawOval(x, y, radio, radio);
            g2d.drawString("q" + i, x + 15, y + 25);

            
            if (i == total - 1) {
                g2d.drawOval(x - 3, y - 3, radio + 6, radio + 6);
            }

            // 🔹 Dibujar transición si no es el último
            if (i < total - 1) {
                int nextX = x + 100;

                //Se dibuja una linea recta 
                g2d.drawLine(x + radio, y + radio / 2, nextX, y + radio / 2);

                //Le agrega la flechita para que se vea como transicion
                int[] px = {nextX, nextX - 10, nextX - 10};
                int[] py = {y + radio / 2, y + radio / 2 - 5, y + radio / 2 + 5};
                g2d.fillPolygon(px, py, 3);

                
                g2d.drawString(String.valueOf(nodoUbicado.getCaracter()),
                        x + (nextX - x) / 2,
                        y + radio / 2 - 10);
            }

            x += 100; 
        }
    }
}
