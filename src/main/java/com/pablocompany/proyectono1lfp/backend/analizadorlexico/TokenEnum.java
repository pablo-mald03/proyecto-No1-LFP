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
    
    INDEFINIDO("INDEFINIDO","la indeterminacion", "indefinido"),
    IDENTIFICADOR("IDENTIFICADOR","el identificador", "identificador"),
    NUMERO("NUMERO","el numero", "numero"),
    DECIMAL("DECIMAL","el decimal", "numero decimal"),
    CADENA("CADENA","la cadena de texto", "cadena de texto"),
    PALABRA_RESERVADA("PALABRA_RESERVADA","la palabra reservada", "palabra reservada"),
    PUNTUACION("PUNTUACION","el signo puntuacion", "puntuacion"),
    OPERADOR("OPERADOR","el operador", "operador"),
    AGRUPACION("AGRUPACION","el signo de agrupacion", "agrupacion"),
    COMENTARIO_LINEA("COMENTARIO_LINEA","el comentario de linea", "comentario de linea"),
    COMENTARIO_BLOQUE("COMENTARIO_BLOQUE","el comentario de bloque", "comentario de bloque"),
    ERROR("ERROR","el error", "error");

    //Representa el tipo de token
    private String tipo;
    
    //Atributo que representa el contexto del token
    private String contexto;
    
    //Atributo que representa el tipo de token en minuscula
    private String nombreToken;

    private TokenEnum(String valor, String contextoDado, String nombreDado) {
        this.tipo = valor;
        this.contexto = contextoDado;
        this.nombreToken = nombreDado;
    }

    public String getTipo() {
        return this.tipo;
    }
    
    public String getContexto() {
        return this.contexto;
    }

    public String getNombreToken() {
        return nombreToken;
    }
    
    
    
}
