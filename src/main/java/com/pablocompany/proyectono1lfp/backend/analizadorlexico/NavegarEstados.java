/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import com.pablocompany.proyectono1lfp.backend.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorGramaticoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import javax.swing.JTextPane;

/**
 *
 * @author pablo
 */
//Clase que permite hacer toda la interaccion entre la navegacion entre estados de analisis
//Permite crear un automata finito que va navegando entre sus propios estados para poder determinar el tipo de token

public class NavegarEstados {
    
    
    private Lexema lexemaAnalisis;

    //==============================REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    // Letras
    private final String ABECEDARIO = "abcdefghijklmnopqrstuvwxyz";
    private final String DIGITOS = "0123456789";
    //Permite tener la referencia a los datos del json
    private ConfigDatos constantesConfig;

    //Atributo que sirve para exponer los errores
    private JTextPane logErrores;

    //==============================FIN DE LA REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    public NavegarEstados(ConfigDatos constantesConfig, JTextPane logErrores) {
        this.constantesConfig = constantesConfig;
        this.logErrores = logErrores;
    }

    //Metodo simplificado para simular el match de caracteres
    //Detecta si existe el caracter en la gramatica
    public boolean esLetra(char c) {
        String caracter = String.valueOf(c).toLowerCase();
        return ABECEDARIO.contains(caracter);
    }

    public boolean esDigito(char c) {
        String caracter = String.valueOf(c).toLowerCase();
        return DIGITOS.contains(caracter);
    }

    //Metodo que ayuda a tener permanentemente al tanto la referencia del config
    public void setConstantesConfig(ConfigDatos constantesConfig) {
        this.constantesConfig = constantesConfig;
        if (this.lexemaAnalisis != null) {
            this.lexemaAnalisis = null;
        }
    }

    //Metodo PRINCIPAL que ejecuta el viaje entre estados de analisis 
    public void iniciarViajeEstados(Lexema lexemaActual) {

        this.logErrores.setText("");

        this.lexemaAnalisis = lexemaActual;

        if (lexemaActual.getLexema().isBlank()) {
            return;
        }

        try {
            declararEstadoInicial(this.lexemaAnalisis, this.lexemaAnalisis.getValorNodo(0));

            StringBuilder registroCadena = new StringBuilder();

            for (int j = 0; j < this.lexemaAnalisis.getLongitudNodo(); j++) {

                Nodo nodoAnalizado = this.lexemaAnalisis.getValorNodo(j);

                try {

                    if (nodoAnalizado.getToken() != TokenEnum.ERROR) {
                        registroCadena.append(nodoAnalizado.getCaracter());

                        viajarEstado(nodoAnalizado, this.lexemaAnalisis.getEstadoAnalisis(), this.lexemaAnalisis, j, String.valueOf(registroCadena));
                    }

                } catch (ErrorGramaticoException ex) {

                    try {

                        int indiceError = this.lexemaAnalisis.getIndiceError();

                        this.lexemaAnalisis.getValorNodo(indiceError).setComodin(false);

                        for (int i = indiceError; i >= 0; i--) {

                            Nodo nodoError = this.lexemaAnalisis.getValorNodo(i);
                            nodoError.setTipo(TokenEnum.ERROR);
                        }

                        this.lexemaAnalisis.setLexemaError(ex.getMessage());

                        registroCadena.setLength(0);

                        if (indiceError + 1 < this.lexemaAnalisis.getLongitudNodo()) {
                            declararEstadoInicial(this.lexemaAnalisis, this.lexemaAnalisis.getValorNodo(indiceError + 1));
                        }

                    } catch (AnalizadorLexicoException ex1) {
                        System.out.println("No se encontro indice sdfsdfds" + ex1.getMessage());
                    }

                } catch (ErrorPuntualException ex2) {
                    System.out.println("Caracter no registrado en la gramatica " + ex2.getMessage());
                }

            }

        } catch (ErrorPuntualException ex) {
            try {

                int indiceError = this.lexemaAnalisis.getIndiceError();

                this.lexemaAnalisis.getValorNodo(indiceError).setComodin(false);

                for (int i = indiceError; i >= 0; i--) {

                    Nodo nodoError = this.lexemaAnalisis.getValorNodo(i);
                    nodoError.setTipo(TokenEnum.ERROR);
                }

                this.logErrores.setText(this.logErrores.getText() + ex.getMessage() + "\n");

            } catch (AnalizadorLexicoException ex1) {
                System.out.println("No se encontro indice " + ex1.getMessage());
            }

        }

        if (this.lexemaAnalisis.getValorNodo(0).getToken() == TokenEnum.ERROR) {
            this.lexemaAnalisis.setEstadoAnalisis(TokenEnum.ERROR);
        }

    }

    //Metodo que sirve para seleccionar estado al que va estar viajando el analizador
    public void viajarEstado(Nodo nodoActual, TokenEnum tokenDeclarado, Lexema lexemaInicial, int iteracion, String cadenaEvaluada) throws ErrorGramaticoException, ErrorPuntualException {

        switch (tokenDeclarado) {

            case IDENTIFICADOR:
                estadoIdentificador(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;

            case NUMERO:
                estadoNumerico(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;

            case DECIMAL:
                estadoDecimal(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;

            case CADENA:
                estadoCadena(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;

            case OPERADOR:
                estadoOperador(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;

            case AGRUPACION:
                estadoAgrupacion(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;
                
            case PUNTUACION:
                estadoPuntuacion(nodoActual, lexemaInicial, iteracion, cadenaEvaluada);
                break;

            default:
                throw new ErrorPuntualException(String.valueOf(nodoActual.getCaracter()));

        }

    }

    //Metodo util para declarar el estado inicial de inicio de lectura PENDIENTE
    public void declararEstadoInicial(Lexema lexemaParametro, Nodo nodoReferencia) throws ErrorPuntualException {

        Nodo nodoAnalisis = nodoReferencia;

        char nodoCaracterInicio = nodoAnalisis.getCaracter();

        if (lexemaParametro.getEstadoAnalisis() == TokenEnum.CADENA) {
            return;
        }

        if (String.valueOf(nodoCaracterInicio).equals("\"")) {

            int limite = lexemaParametro.getLongitudNodo();

            if (limite < 2) {
                nodoAnalisis.setTipo(TokenEnum.ERROR);
                nodoAnalisis.setComodin(true);
                throw new ErrorPuntualException(lexemaParametro.getLexema() + " " + lexemaParametro.getLexema() + " TOKEN " + TokenEnum.CADENA.getTipo() + "= \"");
            }

            Nodo nodoTemporal = lexemaParametro.getValorNodo(lexemaParametro.getLongitudNodo() - 1);

            char nodoCaracterFin = nodoTemporal.getCaracter();

            if (!String.valueOf(nodoCaracterFin).equals("\"")) {
                nodoTemporal.setTipo(TokenEnum.ERROR);
                nodoTemporal.setComodin(true);
                throw new ErrorPuntualException(lexemaParametro.getLexema() + " No tiene comillas de cierre. NO TOKEN");
            }

            lexemaParametro.setEstadoAnalisis(TokenEnum.CADENA);
            return;

        }

        Nodo nodoTemporal1 = lexemaParametro.getValorNodo(lexemaParametro.getLongitudNodo() - 1);

        char nodoCaracterTemporalFin = nodoTemporal1.getCaracter();

        //ultima condicion necesaria para la deteccion de cadenas de texto
        if (!String.valueOf(nodoCaracterInicio).equals("\"") && String.valueOf(nodoCaracterTemporalFin).equals("\"")) {
            nodoTemporal1.setTipo(TokenEnum.ERROR);
            nodoTemporal1.setComodin(true);
            throw new ErrorPuntualException(lexemaParametro.getLexema() + " No tiene comillas de apertura. NO TOKEN");
        }

        //Declara el estado de analisis como identificador
        if (esLetra(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.IDENTIFICADOR);
            return;
        }

        //Declara el estado como numerico (ESTE TIENE LA CAPACIDAD DE COMUNICARSE CON DECIMAL)
        if (esDigito(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.NUMERO);
            return;
        }

        //Define el estado como operador
        if (this.constantesConfig.esOperadores(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.OPERADOR);
            return;
        }

        //Define el estado como agrupacion
        if (this.constantesConfig.esAgrupacion(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.AGRUPACION);
            return;
        }
        
        //Define el estado como agrupacion
        if (this.constantesConfig.esPuntuacion(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.PUNTUACION);
        }

    }

    //===========================APARTADO DE METODOS QUE SIRVEN PARA IR DECLARANDO ESTADOS================================
    //Metodo que permite comparar si son signos de agrupacion en conjunto
    public void estadoPuntuacion(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();
        
        if (!this.constantesConfig.esPuntuacion(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter de Puntuacion no registrado en " + palabraEvaluada);
        }

        nodoActual.setTipo(TokenEnum.PUNTUACION);

    }
    
    //Metodo que permite comparar si son signos de agrupacion en conjunto
    public void estadoAgrupacion(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();
        
        if (!this.constantesConfig.esAgrupacion(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Signo de agrupacion no registrado en " + palabraEvaluada);
        }

        nodoActual.setTipo(TokenEnum.AGRUPACION);

    }
    //Metodo que permite comparar si son operadores en conjunto
    public void estadoOperador(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        if (!this.constantesConfig.esOperadores(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Operador no registrado en " + palabraEvaluada);
        }

        nodoActual.setTipo(TokenEnum.OPERADOR);

    }

    //Metodo que analiza los estados de identificadores
    //Este lanza error si se topa con algo fuera de la gramatica 
    public void estadoIdentificador(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        if (this.constantesConfig.esAgrupacion(caracterNodo) || this.constantesConfig.esOperadores(caracterNodo) || this.constantesConfig.esPuntuacion(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter especial no permitido en " + palabraEvaluada);
        }

        if (!esLetra(caracterNodo) && !esDigito(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" No es letra ni numero en " + palabraEvaluada);
        }

        nodoActual.setTipo(TokenEnum.IDENTIFICADOR);

    }

    //Metodo que analiza los estados numericos
    //Este estado se puede comunicar con el estado decimal
    public void estadoNumerico(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        //Se comunica con el estado decimal para poder evaluar que sea decimal
        if (caracterNodo == '.') {
            lexemaUtilizado.setEstadoAnalisis(TokenEnum.DECIMAL);

            //DECLARA EL VALOR COMO DECIMAL HACIA ATRAS SIEMRE Y CUANDO NO SEA ERROR
            for (int i = indice; i >= 0; i--) {
                Nodo nodoComparacion = lexemaUtilizado.getValorNodo(i);

                if (nodoComparacion.getToken() != TokenEnum.ERROR) {
                    nodoComparacion.setTipo(TokenEnum.DECIMAL);
                }

            }

            estadoDecimal(nodoActual, lexemaUtilizado, indice, palabraEvaluada);
            return;
        }

        if (esLetra(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Letras no permitidas en " + palabraEvaluada);
        }

        if (this.constantesConfig.esAgrupacion(caracterNodo) || this.constantesConfig.esOperadores(caracterNodo) || this.constantesConfig.esPuntuacion(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter especial no permitido en " + palabraEvaluada);
        }

        if (!esDigito(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter no admitido " + palabraEvaluada);
        }

        nodoActual.setTipo(TokenEnum.NUMERO);

    }

    public void estadoDecimal(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        int contadorPuntos = 0;

        //Se condiciona a que lexicamente no existen decimales con doble punto decimal
        for (int i = 0; i < lexemaUtilizado.getLongitudNodo(); i++) {
            Nodo nodoComparacion = lexemaUtilizado.getValorNodo(i);

            if (nodoComparacion.getToken() != TokenEnum.ERROR && nodoComparacion.getCaracter() == '.') {
                contadorPuntos++;
            }

        }

        if (contadorPuntos > 1) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" No se puede poner doble punto decimal en " + palabraEvaluada);
        }

        if (esLetra(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Letras no permitidas en " + palabraEvaluada);
        }

        if (!esDigito(caracterNodo) && caracterNodo != '.') {

            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter no admitido " + palabraEvaluada);
        }

        if ((indice == lexemaUtilizado.getLongitudNodo() - 1) && lexemaUtilizado.getValorNodo(indice).getCaracter() == '.') {

            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Sin digitos despues del punto en " + palabraEvaluada);
        }

        nodoActual.setTipo(TokenEnum.DECIMAL);
    }

    //Metodo que permite analizar el estado de cadenas de texto
    //indice representa el indice en el nodo actual
    public void estadoCadena(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        String valorVacio = String.valueOf(caracterNodo);

        if (!valorVacio.isBlank() && caracterNodo != '"' && !esLetra(caracterNodo) && !esDigito(caracterNodo) && !this.constantesConfig.esAgrupacion(caracterNodo) && !this.constantesConfig.esOperadores(caracterNodo) && !this.constantesConfig.esPuntuacion(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter no admitido en " + palabraEvaluada);
        }

        if (indice > 0 && lexemaUtilizado.getValorNodo(indice - 1).getToken() == TokenEnum.ERROR) {
            nodoActual.setTipo(TokenEnum.ERROR);
            return;
        }

        nodoActual.setTipo(TokenEnum.CADENA);
    }

    //===========================FIN DEL APARTADO DE METODOS QUE SIRVEN PARA IR DECLARANDO ESTADOS==============================
    
}
