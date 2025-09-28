/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import static com.pablocompany.proyectono1lfp.backend.analizadorlexico.TokenEnum.*;
import com.pablocompany.proyectono1lfp.backend.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorGramaticoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
//Clase que permite hacer toda la interaccion entre la navegacion entre estados de analisis
//Permite crear un automata finito que va navegando entre sus propios estados para poder determinar el tipo de token
public class NavegarEstados {

    //Atributo que sirve para poder evaluar el lexema 
    private Lexema lexemaAnalisis;

    //==============================REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    //Permite tener la referencia de la gramatica del AFD
    private AutomataDeterminista automataDeterminista;

    //Atributo que sirve para exponer los errores
    private JTextPane logErrores;

    //Atributo que permite mostrar las tansiciones del automata
    private JTextPane logTransicionesAFD;

    //==============================FIN DE LA REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    public NavegarEstados(AutomataDeterminista constantesConfig, JTextPane logErrores, JTextPane logTransiciones) {
        this.automataDeterminista = constantesConfig;
        this.logErrores = logErrores;
        this.logTransicionesAFD = logTransiciones;
    }

    //Metodo que ayuda a tener permanentemente al tanto la referencia del config
    public void setConstantesConfig(AutomataDeterminista constantesAutomata) {
        this.automataDeterminista = constantesAutomata;
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
            this.lexemaAnalisis.setIndiceViajeAFD(0);

            //Metodo que permite anunciar el estado que se va a recorrer del lexema
            anunciarLexema(this.lexemaAnalisis);

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

                        this.lexemaAnalisis.setCadenaEsperada("Se esperaba un " + this.lexemaAnalisis.getEstadoAnalisis().getNombreToken());

                        for (int i = indiceError; i >= 0; i--) {

                            Nodo nodoError = this.lexemaAnalisis.getValorNodo(i);
                            nodoError.setTipo(TokenEnum.ERROR);
                        }

                        this.lexemaAnalisis.setLexemaError(ex.getMessage());

                        registroCadena.setLength(0);

                        if (indiceError + 1 < this.lexemaAnalisis.getLongitudNodo()) {
                            declararEstadoInicial(this.lexemaAnalisis, this.lexemaAnalisis.getValorNodo(indiceError + 1));
                        }

                        if (!this.lexemaAnalisis.getCadenaError().isBlank()) {
                            //Se registra el error en las transiciones
                            anunciarError(this.lexemaAnalisis);
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

        //Se encarga de anunciar cuando el AFD se reinicia
        anunciarFinalRecorrido(this.lexemaAnalisis);

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
        if (this.automataDeterminista.estadoLetras(String.valueOf(nodoCaracterInicio))) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.IDENTIFICADOR);
            return;
        }

        //Declara el estado como numerico (ESTE TIENE LA CAPACIDAD DE COMUNICARSE CON DECIMAL)
        if (this.automataDeterminista.estadoNumerico(String.valueOf(nodoCaracterInicio))) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.NUMERO);
            return;
        }

        //Define el estado como operador
        if (this.automataDeterminista.estadoOperadoresMatematicos(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.OPERADOR);
            return;
        }

        //Define el estado como agrupacion
        if (this.automataDeterminista.estadoAgrupacion(nodoCaracterInicio)) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.AGRUPACION);
            return;
        }

        //Define el estado como agrupacion
        if (this.automataDeterminista.estadoPuntuacion(String.valueOf(nodoCaracterInicio))) {
            lexemaParametro.setEstadoAnalisis(TokenEnum.PUNTUACION);
        }

    }

    //===========================APARTADO DEL METODO MAS IMPORTANTE DEL ANALIZADOR LEXICO=======================================
    //Metodo que permite avisar que hubo un erro en el lexema
    private void anunciarError(Lexema lexemaError) {
        try {
            Color colorEstados = new Color(0xB81D00);

            insertarEstadoTransicion("No se ha llegado al estado de aceptacion", colorEstados, this.logTransicionesAFD);
            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);

            insertarEstadoTransicion("Moviendo al estado de Error con la cadena: ", colorEstados, this.logTransicionesAFD);

            String[] cadenaError = lexemaError.getCadenaError().split("\\s+");

            int indiceError = cadenaError.length - 1;

            if (indiceError < 0) {
                indiceError = 0;
            }

            insertarEstadoTransicion(cadenaError[indiceError], new Color(0x292724), this.logTransicionesAFD);

            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);

            insertarEstadoTransicion("Reiniciando Automata...", Color.BLACK, this.logTransicionesAFD);

            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);
            
            lexemaError.setIndiceViajeAFD(0);

        } catch (BadLocationException ex) {
            System.out.println("No se ha podido pintar el log de transiciones");
        }

    }

    //Metodo que anunca el estado que recorrera el AFD
    private void anunciarLexema(Lexema lexemaOperado) {

        try {
            Color colorTexto = obtenerColorPorToken(lexemaOperado.getEstadoAnalisis());

            insertarEstadoTransicion("Con " + lexemaOperado.getEstadoAnalisis().getContexto() + ": ", Color.BLACK, this.logTransicionesAFD);

            insertarEstadoTransicion(" " + lexemaOperado.getLexema(), colorTexto, this.logTransicionesAFD);

            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);

        } catch (BadLocationException ex) {
            System.out.println("No se ha podido pintar el log de transiciones");
        }

    }

    //Metodo que permite ir ilusatrando estado a estado acorde a lo que vaya recorriendo el AFD
    private void anunciarEstadoRecorrido(Lexema lexemaActual, Nodo nodoEstado, String sujeto) {
        try {
            Color colorEstados = new Color(0x0085A6);

            int indiceTransicion = lexemaActual.getIndiceViajeAFD();

            insertarEstadoTransicion("Me movi del estado " + (indiceTransicion + 1) + " al estado " + (indiceTransicion + 2) + " con " + sujeto + " ", colorEstados, this.logTransicionesAFD);

            insertarEstadoTransicion(String.valueOf(nodoEstado.getCaracter()), new Color(0x292724), this.logTransicionesAFD);

            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);

            indiceTransicion++;

            lexemaActual.setIndiceViajeAFD(indiceTransicion);

        } catch (BadLocationException ex) {
            System.out.println("No se ha podido pintar el log de transiciones");
        }

    }

    //Metodo que permite anunciar el reinicio del automata y guardado del lexema
    private void anunciarFinalRecorrido(Lexema lexemaEvaluado) {
        try {

            if (!lexemaEvaluado.getCadenaError().isBlank()) {

                TokenEnum tokenEvaluado = lexemaEvaluado.verificarTokenReconocido();

                if (tokenEvaluado != TokenEnum.ERROR) {

                    insertarEstadoTransicion("Guardando token " + tokenEvaluado.getNombreToken() + ". Lexema: " + lexemaEvaluado.getLexema(), new Color(0x085717), this.logTransicionesAFD);

                } else {
                    insertarEstadoTransicion("Guardando token " + lexemaEvaluado.getEstadoAnalisis().getNombreToken() + ". Lexema: " + lexemaEvaluado.getLexema(), new Color(0x085717), this.logTransicionesAFD);
                }

            } else {
                insertarEstadoTransicion("Guardando token " + lexemaEvaluado.getEstadoAnalisis().getNombreToken() + ". Lexema: " + lexemaEvaluado.getLexema(), new Color(0x085717), this.logTransicionesAFD);
            }

            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);

            insertarEstadoTransicion("Reiniciando Automata...", Color.BLACK, this.logTransicionesAFD);

            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);
            insertarEstadoTransicion("\n", Color.BLACK, this.logTransicionesAFD);

        } catch (BadLocationException ex) {
            System.out.println("No se ha podido pintar el log de transiciones");
        }

    }

    //===========================FIN DEL APARTADO DEL METODO MAS IMPORTANTE DEL ANALIZADOR LEXICO=======================================
    //===========================APARTADO DE METODOS QUE SIRVEN PARA IR DECLARANDO ESTADOS================================
    //Metodo que permite comparar si son signos de agrupacion en conjunto
    public void estadoPuntuacion(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        if (!this.automataDeterminista.estadoPuntuacion(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter de Puntuacion no registrado en " + palabraEvaluada);
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "un");

        nodoActual.setTipo(TokenEnum.PUNTUACION);

    }

    //Metodo que permite comparar si son signos de agrupacion en conjunto
    public void estadoAgrupacion(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        if (!this.automataDeterminista.estadoAgrupacion(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Signo de agrupacion no registrado en " + palabraEvaluada);
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "una");

        nodoActual.setTipo(TokenEnum.AGRUPACION);

    }

    //Metodo que permite comparar si son operadores en conjunto
    public void estadoOperador(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        if (!this.automataDeterminista.estadoOperadoresMatematicos(caracterNodo)) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Operador no registrado en " + palabraEvaluada);
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "un");

        nodoActual.setTipo(TokenEnum.OPERADOR);

    }

    //Metodo que analiza los estados de identificadores
    //Este lanza error si se topa con algo fuera de la gramatica 
    public void estadoIdentificador(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        if (this.automataDeterminista.estadoAgrupacion(caracterNodo) || this.automataDeterminista.estadoOperadoresMatematicos(caracterNodo) || this.automataDeterminista.estadoPuntuacion(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter especial no permitido en " + palabraEvaluada);
        }

        if (!this.automataDeterminista.estadoLetras(String.valueOf(caracterNodo)) && !this.automataDeterminista.estadoNumerico(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" No es letra ni numero en " + palabraEvaluada);
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "una");

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

        if (this.automataDeterminista.estadoLetras(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Letras no permitidas en " + palabraEvaluada);
        }

        if (this.automataDeterminista.estadoAgrupacion(caracterNodo) || this.automataDeterminista.estadoOperadoresMatematicos(caracterNodo) || this.automataDeterminista.estadoPuntuacion(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter especial no permitido en " + palabraEvaluada);
        }

        if (!this.automataDeterminista.estadoNumerico(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter no admitido " + palabraEvaluada);
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "un");

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

        if (this.automataDeterminista.estadoLetras(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Letras no permitidas en " + palabraEvaluada);
        }

        if (!this.automataDeterminista.estadoNumerico(String.valueOf(caracterNodo)) && caracterNodo != '.') {

            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter no admitido " + palabraEvaluada);
        }

        if ((indice == lexemaUtilizado.getLongitudNodo() - 1) && lexemaUtilizado.getValorNodo(indice).getCaracter() == '.') {

            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Sin digitos despues del punto en " + palabraEvaluada);
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "un");

        nodoActual.setTipo(TokenEnum.DECIMAL);
    }

    //Metodo que permite analizar el estado de cadenas de texto
    //indice representa el indice en el nodo actual
    public void estadoCadena(Nodo nodoActual, Lexema lexemaUtilizado, int indice, String palabraEvaluada) throws ErrorGramaticoException {

        char caracterNodo = nodoActual.getCaracter();

        String valorVacio = String.valueOf(caracterNodo);

        if (!valorVacio.isBlank() && caracterNodo != '"' && !this.automataDeterminista.estadoLetras(String.valueOf(caracterNodo)) && !this.automataDeterminista.estadoNumerico(String.valueOf(caracterNodo)) && !this.automataDeterminista.estadoAgrupacion(caracterNodo) && !this.automataDeterminista.estadoOperadoresMatematicos(caracterNodo) && !this.automataDeterminista.estadoPuntuacion(String.valueOf(caracterNodo))) {
            nodoActual.setComodin(true);
            throw new ErrorGramaticoException(" Caracter no admitido en " + palabraEvaluada);
        }

        if (indice > 0 && lexemaUtilizado.getValorNodo(indice - 1).getToken() == TokenEnum.ERROR) {
            nodoActual.setTipo(TokenEnum.ERROR);
            return;
        }

        //Se anuncia el caracter con el que se mueve de estado el automata
        anunciarEstadoRecorrido(lexemaUtilizado, nodoActual, "una");

        nodoActual.setTipo(TokenEnum.CADENA);
    }

    //APARTADO DE METODOS DE PINTADO DE LAS TRANSICIONES DEL AUTOMATA (TOCA HACERLAS DESDE EL BE)
    // Método para insertar texto con un color específico
    private void insertarEstadoTransicion(String texto, Color color, JTextPane paneTransiciones) throws BadLocationException {

        StyledDocument doc = paneTransiciones.getStyledDocument();

        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color);

        doc.insertString(doc.getLength(), texto, estilo);

    }

    // Método que mapea el token a su color
    private Color obtenerColorPorToken(TokenEnum tipo) {
        switch (tipo) {
            case PALABRA_RESERVADA:
                return Color.BLUE;
            case IDENTIFICADOR:
                return new Color(0x6B4627);
            case NUMERO:
                return new Color(0x1FC23B);
            case DECIMAL:
                return Color.BLACK;
            case CADENA:
                return new Color(0xF0760E);
            case COMENTARIO_LINEA:
            case COMENTARIO_BLOQUE:
                return new Color(0x1B6615);
            case OPERADOR:
                return new Color(0xB5AB2D);
            case AGRUPACION:
                return new Color(0x991CB8);
            case PUNTUACION:
                return new Color(0x329481);
            case ERROR:
                return Color.RED;
            default:
                return new Color(0x9E7A7A);
        }
    }

    //===========================FIN DEL APARTADO DE METODOS QUE SIRVEN PARA IR DECLARANDO ESTADOS==============================
}
