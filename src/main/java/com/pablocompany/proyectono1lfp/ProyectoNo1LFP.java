/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.pablocompany.proyectono1lfp;

import java.util.Scanner;

/**
 *
 * @author pablo
 */
public class ProyectoNo1LFP {

    public static void main(String[] args) {

        //Prueba de comparacion de expresiones regulares
        System.out.println("Escriba ");

        Scanner prueba = new Scanner(System.in);

        String pruebaPalabra = prueba.nextLine().trim();

        if (pruebaPalabra.matches("^a(a|b|c|d|e)*$")) {

            System.out.println("Se coincide");
        } else {
            System.out.println("no coincide");
        }

    }

}
