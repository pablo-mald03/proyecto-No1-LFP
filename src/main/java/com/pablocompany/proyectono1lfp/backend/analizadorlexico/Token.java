/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

/**
 *
 * @author pablo
 */
public enum Token {
    
    INDEFINIDO("INDEFINIDO"),
    IDENTIFICADOR("IDENTIFICADOR"),
    NUMERO("NUMERO"),
    DECIMAL("DECIMAL"),
    CADENA("CADENA"),
    PALABRA_RESERVADA("PALABRA_RESERVADA"),
    PUNTUACION("PUNTUACION"),
    OPERADOR("OPERADOR"),
    AGRUPACION("AGRUPACION"),
    COMENTARIO_LINEA("COMENTARIO_LINEA"),
    COMENTARIO_BLOQUE("COMENTARIO_BLOQUE"),
    ERROR("ERROR");

    private String tipo;

    private Token(String valor) {
        this.tipo = valor;
    }

    public String getTipo() {
        return this.tipo;
    }
}
