/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

/**
 *
 * @author pablo
 */
//Clase que representa los diferentes tipos de comentarios
public class Comentarios {
    
    
    private String linea;
    private String bloqueInicio;
    private String bloqueFin;

    // getters y setters para retornar los tipos de comentarios que existen
    public String getComentarioLinea() {
        return linea;
    }

    public String getBloqueInicio() {
        return bloqueInicio;
    }

    //Retorna el caracter de bloque de linea 
    public String getBloqueFin() {
        return bloqueFin;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public void setBloqueFin(String bloqueFin) {
        this.bloqueFin = bloqueFin;
    }

    public void setBloqueInicio(String bloqueInicio) {
        this.bloqueInicio = bloqueInicio;
    }
    
}
