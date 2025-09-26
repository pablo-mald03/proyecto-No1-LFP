/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

/**
 *
 * @author pablo
 */
//Clase que permite implementar la funcionalidad de mostrar a tiempo real donde se ubica el cursor
public class EditorTexto {

    private final JTextPane textPane;
    private final JScrollPane scrollPane;
    private final JLabel statusBar;

    public EditorTexto(JTextPane textPane, JScrollPane scrollPane, JLabel statusBar) {
        this.textPane = textPane;
        this.scrollPane = scrollPane;
        this.statusBar = statusBar;

        LineNumberView lineNumbers = new LineNumberView(textPane);

        scrollPane.setRowHeaderView(lineNumbers);

        textPane.addCaretListener(e -> updateStatusBar());

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumbers.repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumbers.repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumbers.repaint();
            }
        });
    }

    private void updateStatusBar() {
        int pos = textPane.getCaretPosition();
        Element root = textPane.getDocument().getDefaultRootElement();
        int fila = root.getElementIndex(pos);
        int col = pos - root.getElement(fila).getStartOffset();
        statusBar.setText("Fila: " + (fila + 1) + " | Columna: " + (col + 1));
    }

}
