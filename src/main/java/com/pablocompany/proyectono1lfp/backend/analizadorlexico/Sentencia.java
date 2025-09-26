/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class Sentencia {
    
    private ArrayList<Lexema> listaLexemas;
    private int filaSentencia; 

    public Sentencia(ArrayList<Lexema> listaLexema,int fila ) {
        this.listaLexemas = listaLexema;
        this.filaSentencia = fila;
        
    }
    
    
    //Metodo que retorna la linea en la que se encuentra la sentencia
    public int getFilaSentencia(){
        return this.filaSentencia;
    }
    
    //Metodo que permite acceder al lexema almacenado en la lista
    public Lexema getListaLexema(int indice){
        return this.listaLexemas.get(indice);
    }
    
    //Metodo que retorna el limite de los lexemas almacenados
    public int limiteLexemas(){
        return this.listaLexemas.size();
    }
    
    public ArrayList<Lexema> obtenerListadoLexemas(){
        return this.listaLexemas;
    }
    
    
}
