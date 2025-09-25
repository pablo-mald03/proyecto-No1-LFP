/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.aplicacion;

import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author pablo
 */
public class IlustrarLabels {

    private JPanel panelPadre;

    private int alto;
    private int ancho;

    //Ayuda a saber cuantos Labels se van a imprimir 
    private int cantidadLabels;

    private String arregloPaths[];

    private boolean randomizarLabels;

    private JLabel labelPasado;

    private ImageIcon iconoImagen;

    //False no randomiza las imagenes
    //True randomiza las imagenes 
    public IlustrarLabels(JPanel panelPadre, int alto, int ancho, int cantidadlabels, String[] arreglo, boolean random) {
        this.panelPadre = panelPadre;
        this.panelPadre.setLayout(null);
        this.alto = alto;
        this.ancho = ancho;

        this.cantidadLabels = cantidadlabels;

        this.arregloPaths = arreglo;

        this.randomizarLabels = random;
    }

    //Constructor para imprimir labels con imagen multiples 
    public IlustrarLabels(JPanel panelPadre, int alto, int ancho, int cantidadlabels, ImageIcon labelImagen, boolean random) {
        this.panelPadre = panelPadre;
        this.panelPadre.setLayout(null);
        this.alto = alto;
        this.ancho = ancho;

        this.cantidadLabels = cantidadlabels;

        this.iconoImagen = labelImagen;

        this.randomizarLabels = random;
    }

    public IlustrarLabels(JPanel panelPadre, int alto, int ancho, String texto, JLabel label) {
        this.panelPadre = panelPadre;
        //this.panelPadre.setLayout(null);
        this.alto = alto;
        this.ancho = ancho;
        this.labelPasado = label;
    }

    public boolean puedeRandomizar() {
        return this.randomizarLabels;
    }

    //Metodo que se encarga de llevar la logistica de crear y ubicar patos 
    public void ubicarIcono(int coordx, int coordy, String path) {
        Image imagenEscalada = new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(this.ancho, this.alto, Image.SCALE_SMOOTH);

        ImageIcon iconoRedimensionado = new ImageIcon(imagenEscalada);

        JLabel labelIlustrado = new JLabel(iconoRedimensionado);

        labelIlustrado.setLocation(coordx, coordy);
        labelIlustrado.setSize(this.ancho, this.alto);
        labelIlustrado.setVisible(true);

        this.panelPadre.add(labelIlustrado);
        this.panelPadre.revalidate();
        this.panelPadre.repaint();
    }

    public void cambiarLabel(ImageIcon imagen) {
        Image imagenEscalada = imagen.getImage().getScaledInstance(this.ancho, this.alto, Image.SCALE_SMOOTH);
        ImageIcon iconoRedimensionado = new ImageIcon(imagenEscalada);

        this.labelPasado.setIcon(iconoRedimensionado);
        this.labelPasado.repaint();
    }

    //Metodo que se encarga de llevar la logistica de crear y ubicar patos 
    public void ubicarIcono(int coordx, int coordy, ImageIcon imagen) {
        Image imagenEscalada = imagen.getImage().getScaledInstance(this.ancho, this.alto, Image.SCALE_SMOOTH);

        ImageIcon iconoRedimensionado = new ImageIcon(imagenEscalada);

        JLabel labelIlustrado = new JLabel(iconoRedimensionado);

        labelIlustrado.setLocation(coordx, coordy);
        labelIlustrado.setSize(this.ancho, this.alto);
        labelIlustrado.setVisible(true);

        this.panelPadre.add(labelIlustrado);
        this.panelPadre.revalidate();
        this.panelPadre.repaint();
    }

    //Metodo para imprimir imagenes con solo el icono
    public void imprimirImagenesIcono() {

        panelPadre.removeAll();
        panelPadre.revalidate();
        panelPadre.repaint();

        Random randomizable = new Random();

        int margen = 10;

        //Delimita la generacion de patos en el Eje X
        int AnchominX = margen;

        int AnchomaxX = this.panelPadre.getWidth() - this.ancho - margen;

        //Deilimita la generacion de patos en el Eje Y
        int AlturaminY = margen;

        int AlturamaxY = this.panelPadre.getHeight() - this.alto - margen;

        for (int i = 0; i < this.cantidadLabels; i++) {

            try {

                int coordx = randomizable.nextInt((AnchomaxX - AnchominX) + 1) + AnchominX;
                int coordy = randomizable.nextInt((AlturamaxY - AlturaminY) + 1) + AlturaminY;

                ubicarIcono(coordx, coordy, this.iconoImagen);

            } catch (Exception e) {
                break;
            }

        }

    }

    public void imprimirImagenes() {
        panelPadre.removeAll();
        panelPadre.revalidate();
        panelPadre.repaint();

        Random randomizable = new Random();

        int margen = 10;

        //Delimita la generacion de patos en el Eje X
        int AnchominX = margen;

        int AnchomaxX = this.panelPadre.getWidth() - this.ancho - margen;

        //Deilimita la generacion de patos en el Eje Y
        int AlturaminY = margen;

        int AlturamaxY = this.panelPadre.getHeight() - this.alto - margen;

        for (int i = 0; i < this.cantidadLabels; i++) {

            try {

                int coordx = randomizable.nextInt((AnchomaxX - AnchominX) + 1) + AnchominX;
                int coordy = randomizable.nextInt((AlturamaxY - AlturaminY) + 1) + AlturaminY;

                String campo = this.arregloPaths[this.arregloPaths.length - 1];
                if (puedeRandomizar()) {
                    int imgRandom = randomizable.nextInt(this.arregloPaths.length);

                    campo = this.arregloPaths[imgRandom];
                }

                ubicarIcono(coordx, coordy, campo);

            } catch (Exception e) {
                break;
            }

        }
    }

}
