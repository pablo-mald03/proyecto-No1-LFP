/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorEncontradoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
//Clase remasterizada que brinda todas las expresiones regulares
//BRINDA MODULARIDAD PARA PODER MODIFICAR EL AUTOMATA
public class AutomataDeterminista {

    //APARTADO DE EXPRESIONES REGULARES QUE PERMITEN VALIDAR LOS ESTADOS DEL AUTOMATA FINITO DETERMINISTA
    //Expresiones regulares de comentarios
    private final String ER_COMENTARIO = "^//$";
    private final String ER_COMENTARIO_MULTI_INICIO = "^/\\*$";
    private final String ER_COMENTARIO_MULTI_FIN = "^\\*/$";

    //Expresion regular para identificar las palabras reservadas 
    private final String ER_PALABRAS_RESERVADAS = "^(SI|si|ENTONCES|entonces|PARA|para|ESCRIBIR|escribir)$";

    //Expresion regular que permite identificar los signos de puntuacion 
    private final String ER_SIGNOS_PUNTUACION = "^[.,:;]$";

    //Expresion regular que permite identificar los operadores aritmetico
    private final String ER_OPERADORES_MATEMATICOS = "^(\\+|\\-|\\*|/|%|=)$";

    //Expresion regular que permite identificar los signos de agrupacion
    private final String ER_SIGNOS_AGRUPACION = "^[(){}\\[\\]]$";

    //Expresion regular que permite identificar los digitos
    private final String ER_NUMEROS = "^(0|1|2|3|4|5|6|7|8|9)$";

    //Expresion regular que permite identificar si son letras minusculas
    private final String ER_MINUSCULAS = "^(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)$";

    //Expresion regular que permite identificar si son letras minusculas
    private final String ER_MAYUSCULAS = "^(A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)$";

    //Verifica que sea paralabra reservada
    public boolean estadoPalabrasReservadas(String palabra) {
        return palabra.matches(ER_PALABRAS_RESERVADAS);
    }

    //Verifica que sea operador matematico
    public boolean estadoOperadoresMatematicos(char operador) {
        return String.valueOf(operador).matches(ER_OPERADORES_MATEMATICOS);
    }

    //Verifica que sea signo de puntuacion
    public boolean estadoPuntuacion(String puntuar) {
        return puntuar.matches(ER_SIGNOS_PUNTUACION);
    }

    //Verifica que sea simbolo de agrupacion
    public boolean estadoAgrupacion(char agrupar) {
        return String.valueOf(agrupar).matches(ER_SIGNOS_AGRUPACION);
    }

    //Verifica que sea comentario de una sola linea
    public boolean estadoComentarioLinea(String entrada) {
        return entrada.matches(ER_COMENTARIO);
    }

    //Metodos de verificacion que permiten Analizarr el comentario fin
    public boolean estadoBloqueComentarioInicial(String inicio) {
        return inicio.matches(ER_COMENTARIO_MULTI_INICIO);
    }

    //Verifica que sea comentario multilinea
    public boolean estadoBloqueComentarioFin(String fin) {
        return fin.matches(ER_COMENTARIO_MULTI_FIN);
    }

    //Verifica que sean letras del alfaneto definido
    public boolean estadoLetras(String letra) {
        return letra.matches(ER_MINUSCULAS) || letra.matches(ER_MAYUSCULAS);
    }

    //Verifica que sean numeros de la gramatica definida
    public boolean estadoNumerico(String numero) {
        return numero.matches(ER_NUMEROS);
    }

    //=====================FIN DEL APARTADO DE METODOS QUE SIRVEN PARA PODER MOSTRAR EN LA UI LA CONFIGURACION====================
}
