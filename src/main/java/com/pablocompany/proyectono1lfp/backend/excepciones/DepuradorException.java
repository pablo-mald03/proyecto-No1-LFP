/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.excepciones;

/**
 *
 * @author pablo
 */
//Excepcion que permite saber el momento en el que se llega al tope de cierto lexema
public class DepuradorException extends Exception{

    public DepuradorException() {
    }

    public DepuradorException(String message) {
        super(message);
    }
    
    
    
}
