/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

/**
 *
 * @author pablo
 */
//Clase que permite darle un sentido a cada cadena de caracter
public class Nodo {

    //Representa el valor del caracter del nodo
    private char caracter;
    
    private int linea;
    private int columna;
    private TokenEnum tipo;
    //Permite ubicar cual fue el nodo que fallo
    //true es el causante del fallo
    private boolean comodinError;
    
    //Atributo que permite saber el espacio fisico ocupado por el nodo en la lista
    private int ubicacionFisica; 

    public Nodo(char caracter, int fila, int columna, TokenEnum tipo) {
        this.caracter = caracter;
        this.linea = fila;
        this.columna = columna;
        this.tipo = tipo;
        this.comodinError = false;
        this.ubicacionFisica = 0;
    }

    //Par de metodos que permiten saber la ubicacion lineal de los nodos
    public int getUbicacionFisica() {
        return ubicacionFisica;
    }

    public void setUbicacionFisica(int ubicacionFisica) {
        this.ubicacionFisica = ubicacionFisica;
    }
    
    //Ayudan a ubicar rapidamente cual es el token comodin para empezar a ciclar para atras
    public boolean esComodin() {
        return comodinError;
    }

    //Ayuda a hacer el set para comodin de error
    public void setComodin(boolean comodin) {
        this.comodinError = comodin;
    }

    //--------------------------APARTADO DE METODOS QUE SIRVEN PARA PODER SABER EL VALOR DE TOKEN O DE ESTADO---------------------
    public char getCaracter() {
        return caracter;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public TokenEnum getToken() {
        return tipo;
    }
    //--------------------------FIN DEL APARTADO DE METODOS QUE SIRVEN PARA PODER SABER EL VALOR DE TOKEN O DE ESTADO---------------------

    //--------------------------APARTADO DE METODOS QUE SIRVEN PARA PODER MODIFICAR EL VALOR DE TOKEN O DE ESTADO---------------------
    public void setCaracter(char caracter) {
        this.caracter = caracter;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public void setTipo(TokenEnum tipo) {
        this.tipo = tipo;
    }
    //--------------------------FIN DEL APARTADO DE METODOS QUE SIRVEN PARA PODER MODIFICAR EL VALOR DE TOKEN O DE ESTADO---------------------

}
