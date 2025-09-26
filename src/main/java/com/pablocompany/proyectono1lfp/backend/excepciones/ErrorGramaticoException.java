/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.excepciones;

/**
 *
 * @author pablo
 */
//Excepcion que representa el fallo cuando una cadena analizada no cumple con el formato esperado
public class ErrorGramaticoException extends Exception{

    public ErrorGramaticoException() {
    }

    public ErrorGramaticoException(String message) {
        super(message);
    }
    
    
    
}
