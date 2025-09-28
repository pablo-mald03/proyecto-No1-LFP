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

    //Atributo que permite saber en que posicion del arreglo esta posicionado el lexema
    private int posicionLexema;

    //AYUDA A DECLARAR EL ESTADO INICIAL AL QUE SERA SOMETIDO EN BASE A LA DETECCION DE PATRON
    private TokenEnum estadoAnalisis;

    //Atributo que permite especificar el caracter esperado
    private String cadenaEsperada;

    //Atributo que permite especificar LA SUGERENCIA DE PALABRAS(SOLO PARA PALABRAS RESERVADAS )
    private String sugerenciaEstimada;

    //Indica el lexema que causo el error
    private String lexemaError;

    //Indice que indica hasta donde llega el automata en su viaje de estados y permite reiniciarlo
    private int indiceViajeAFD;

    //ATRIBUTO QUE AYUDA A VALIDAR SI YA EL LEXAMA FUE COMPLETAMENTE PROCESADO
    //True si ya
    //False si solo ha sido cambiado de estado pero aun no confirmado
    private boolean yaDeclarado;

    public Lexema(String lexemaGenerado, int lineaCoordenada) {

        this.lexemaGenerado = lexemaGenerado;
        this.lineaCoordenada = lineaCoordenada;
        this.estadoAnalisis = TokenEnum.INDEFINIDO;
        this.yaDeclarado = false;
        this.lexemaError = "";
        this.indiceViajeAFD = 0;
    }

    //Metodo que sirve para poder saber de que estado transicionara
    public int getIndiceViajeAFD() {
        return indiceViajeAFD;
    }

    //Metodo que sirve para ir configurando el siguiente estado
    public void setIndiceViajeAFD(int indiceViajeAFD) {
        this.indiceViajeAFD = indiceViajeAFD;
    }

    //METODOS QUE PERMITEN SABER EN QUE POSICION DE LA SENTENCIA SE UBICA EL LEXEMA
    public int getPosicionLexema() {
        return posicionLexema;
    }

    public void setPosicionLexema(int posicionLexema) {
        this.posicionLexema = posicionLexema;
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
    public void setLexemaError(String lexemaEncontrado) {
        this.lexemaError = lexemaEncontrado;
    }

    //Metodo que retorna la cadena que provoco el error
    public String getCadenaError() {
        return this.lexemaError;
    }

    //Metodo que permite obtener el caracter o cadena esperada
    public String getCadenaEsperada() {
        return cadenaEsperada;
    }

    //Metodo quepermite cambiar el caracter esperado
    public void setCadenaEsperada(String caracterEsperado) {
        this.cadenaEsperada = caracterEsperado;
    }

    //Metodo que sirve para retornar el estado inicial del lexema
    public TokenEnum getEstadoAnalisis() {
        return this.estadoAnalisis;
    }

    //Metodo que sirve para hacer el set total en dado caso de reconocer el token rapido
    public void setEstadoAnalisis(TokenEnum tipoToken) {
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
            this.listaNodos.add(new Nodo(caracter, fila, columnaNodo, TokenEnum.INDEFINIDO));
            columnaNodo++;
        }
        return columnaNodo;

    }

    //Metodo que ayuda a hacer el set para declarar rapidamente a un lexema como tipo (SOLO UTIL CUANDO ES ALGO QUE HACE MATCH LITERAL)
    public void generalizarNodo(TokenEnum tipoToken) {

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

    //Metodo que sirve para saber si en el arreglo hay algun nodo con token
    public TokenEnum verificarTokenReconocido() {

        for (int i = 0; i < this.getLongitudNodo(); i++) {

            if (this.estadoAnalisis != TokenEnum.ERROR) {
                break;
            }

            Nodo nodoEvaluado = this.listaNodos.get(i);

            if (nodoEvaluado.getToken() != TokenEnum.ERROR) {
                return nodoEvaluado.getToken();
            }

        }

        return this.estadoAnalisis;
    }

}
