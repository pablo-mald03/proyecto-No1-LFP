/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

/**
 *
 * @author pablo
 */
public enum TokenEnum {
    
    INDEFINIDO("INDEFINIDO","la indeterminacion"),
    IDENTIFICADOR("IDENTIFICADOR","el identificador"),
    NUMERO("NUMERO","el numero"),
    DECIMAL("DECIMAL","el decimal"),
    CADENA("CADENA","la cadena de texto"),
    PALABRA_RESERVADA("PALABRA_RESERVADA","la palabra reservada"),
    PUNTUACION("PUNTUACION","el signo puntuacion"),
    OPERADOR("OPERADOR","el operador"),
    AGRUPACION("AGRUPACION","el signo de agrupacion"),
    COMENTARIO_LINEA("COMENTARIO_LINEA","el comentario de linea"),
    COMENTARIO_BLOQUE("COMENTARIO_BLOQUE","el comentario de bloque"),
    ERROR("ERROR","el error");

    //Representa el tipo de token
    private String tipo;
    
    //Atributo que representa el contexto del token
    private String contexto;

    private TokenEnum(String valor, String contextoDado) {
        this.tipo = valor;
        this.contexto = contextoDado;
    }

    public String getTipo() {
        return this.tipo;
    }
    
    public String getContexto() {
        return this.contexto;
    }
}
