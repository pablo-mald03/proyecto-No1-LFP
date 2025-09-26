/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

/**
 *
 * @author pablo
 */
//Clase que permite crear las lineas para poderlas ver visualmente en el editor
public class LineNumberView extends JPanel {

    private final JTextComponent textComponent;
    private final Font font;

    public LineNumberView(JTextComponent textComponent) {
        this.textComponent = textComponent;
        this.font = new Font("Monospaced", Font.PLAIN, 20);
        setFont(font);
        setPreferredSize(new Dimension(40, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics fm = g.getFontMetrics(font);
        int fontHeight = fm.getHeight();

        try {
            Rectangle clip = g.getClipBounds();
            int startOffset = textComponent.viewToModel2D(new Point2D.Double(0, clip.y));
            int endOffset = textComponent.viewToModel2D(new Point2D.Double(0, clip.y + clip.height));

            Element root = textComponent.getDocument().getDefaultRootElement();
            int startLine = root.getElementIndex(startOffset);
            int endLine = root.getElementIndex(endOffset);

            int y = clip.y - (clip.y % fontHeight) + fm.getAscent();

            for (int line = startLine; line <= endLine; line++) {
                g.drawString(String.valueOf(line + 1), 5, y);
                y += fontHeight;
            }
        } catch (Exception e) {
            System.out.println("Error al pintar números de línea: " + e.getMessage());
        }
    }

}
