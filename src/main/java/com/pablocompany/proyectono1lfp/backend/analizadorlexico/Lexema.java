/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import com.pablocompany.proyectono1lfp.backend.excepciones.AnalizadorLexicoException;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
//Clase encargada de manejar palabra por palabra solo contemplando la linea en la que va para reportes
public class Lexema {
    
     //Estructura dinamica encargada de almacenar por completo caracter a caracter
    private ArrayList<Nodo> listaNodos = new ArrayList<>(5000);

    //Atributo que representa el lexema completo de la palabra
    private String lexemaGenerado;

    //Representa la linea en la que se encuetra situado el lexema
    private int lineaCoordenada;

    //AYUDA A DECLARAR EL ESTADO INICIAL AL QUE SERA SOMETIDO EN BASE A LA DETECCION DE PATRON
    private Token estadoAnalisis;
    
    private String lexemaError; 

    //ATRIBUTO QUE AYUDA A VALIDAR SI YA EL LEXAMA FUE COMPLETAMENTE PROCESADO
    //True si ya
    //False si solo ha sido cambiado de estado pero aun no confirmado
    private boolean yaDeclarado;

    public Lexema(String lexemaGenerado, int lineaCoordenada) {

        this.lexemaGenerado = lexemaGenerado;
        this.lineaCoordenada = lineaCoordenada;
        this.estadoAnalisis = Token.INDEFINIDO;
        this.yaDeclarado = false;
        this.lexemaError = "";
    }

    //Retorna el lexema por si se necesita 
    public String getLexema() {
        return lexemaGenerado;
    }

    //Metodo que retorna la longitud del arraylist de nodo
    public int getLongitudNodo() {
        return this.listaNodos.size();
    }
    
    //Metodo que sirve para ir agregando las cadenas de error detectadas
    public void setLexemaError(String lexemaEncontrado){
        this.lexemaError = lexemaEncontrado;
    }
    
    //Metodo que retorna la cadena que provoco el error
    public String getCadenaError(){
        return this.lexemaError;
    }
    
    
    //Metodo que sirve para retornar el estado inicial del lexema
    public Token getEstadoAnalisis() {
        return this.estadoAnalisis;
    }

    //Metodo que sirve para hacer el set total en dado caso de reconocer el token rapido
    public void setEstadoAnalisis(Token tipoToken) {
        this.estadoAnalisis = tipoToken;
    }

    //Retorna si el lexema ya fue declarado por completo 
    public boolean esYaDeclarado() {
        return this.yaDeclarado;
    }

    //Estableque que el lexema ya fue declarado por completo
    public void setYaDeclarado(boolean declaracion) {
        this.yaDeclarado = declaracion;
    }

    //Permite saber en todo momento la fila del lexema
    public int getFilaCoordenada() {
        return lineaCoordenada;
    }

    //Metodo que retorna la lista de nodos
    public ArrayList<Nodo> obtenerListaNodo() {
        return this.listaNodos;
    }

    //Metodo que retorna el valor del nodo
    public Nodo getValorNodo(int indice) {
        return this.listaNodos.get(indice);
    }

    //Metodo encargado para ir clasificando los estados y tipos de todas las letras que componen el lexema
    public int separarNodos(String palabra, int columna, int fila) {

        int columnaNodo = columna;

        for (int i = 0; i < palabra.length(); i++) {

            char caracter = palabra.charAt(i);
            this.listaNodos.add(new Nodo(caracter, fila, columnaNodo, Token.INDEFINIDO));
            columnaNodo++;
        }
        return columnaNodo;

    }

    //Metodo que ayuda a hacer el set para declarar rapidamente a un lexema como tipo (SOLO UTIL CUANDO ES ALGO QUE HACE MATCH LITERAL)
    public void generalizarNodo(Token tipoToken) {

        for (Nodo nodito : this.listaNodos) {
            nodito.setTipo(tipoToken);
        }

        this.setEstadoAnalisis(tipoToken);
    }

    //Metodo que sirve para poder ubicar el indice del nodo que causo el error
    public int getIndiceError() throws AnalizadorLexicoException {

        for (int i = 0; i < listaNodos.size(); i++) {

            Nodo nodoIterado = this.listaNodos.get(i);

            if (nodoIterado.esComodin()) {

                nodoIterado.setComodin(false);
                return i;
            }

        }

        throw new AnalizadorLexicoException("No se ha encontrado el indice del nodo");

    }
    
}
