/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

/**
 *
 * @author pablo
 */
public class GestorLexer {
    
    private AnalizadorLexico lexer;

    //Retorna la referencia de lexer
    public AnalizadorLexico getLexer() {
        return lexer;
    }

    //Permite hacer el set del lexer
    public void setLexer(AnalizadorLexico lexer) {
        this.lexer = lexer;
    }
}
