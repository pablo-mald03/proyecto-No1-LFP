/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import java.util.ArrayList;
import javax.swing.JTextPane;

/**
 *
 * @author pablo
 */
//Clase que permite depurar las entradas de texto
public class Depurador {
    
    //-> Indice de sentencia
    private int indiceSentencia;
    //-> Indice de lexema
    private int indiceLexema;
    
    //-> Indice de Nodo
    private int indiceNodo;
    
    //Log de muestra de estados
    private JTextPane paneDepuracion;
    
    //Va mostrando la palabra armada
    private StringBuilder lexemaOperado;
    
    //Referencia a las sentencias analizadas por el Analizador Lexico
    private ArrayList<Sentencia> listaSentencias;

    public Depurador(ArrayList<Sentencia> listaSentencias) {
        this.indiceSentencia = 0;
        this.indiceLexema = 0;
        this.indiceNodo = 0;
        this.listaSentencias = listaSentencias;
    }
    
    //Metodo que permite reniciar el depurador cuando se abandona el analisis
    public void reiniciarDepurador(){
        this.indiceSentencia = 0;
        this.indiceLexema = 0;
        this.indiceNodo = 0;
    }
    
    
    
    
}
