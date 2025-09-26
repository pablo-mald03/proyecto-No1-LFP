/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.excepciones;

/**
 *
 * @author pablo
 */
//Excepcion que representa cuando la configuracion del analizador lexico no funciona bien
public class ConfigException extends Exception{

    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }
    
    
    
}
