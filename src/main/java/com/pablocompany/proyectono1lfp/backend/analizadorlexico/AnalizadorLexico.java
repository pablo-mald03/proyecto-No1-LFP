/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import com.pablocompany.proyectono1lfp.backend.excepciones.ConfigException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorEncontradoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
public class AnalizadorLexico {

    //==============================REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    //Permite tener la referencia a los datos del json
    private AutomataDeterminista automataFinitoDeterminista;

    //==============================FIN DE LA REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    //Estructura dinamica encargada de almacenar por completo caracter a caracter
    private ArrayList<Sentencia> listaSentencias = new ArrayList<>(5000);

    //Atributo que permite referenciar a la modificacion del JTextPane
    private JTextPane areaAnalisis;

    private NavegarEstados analizarEstados;

    private JTextPane logErrores;

    //Atributo que permite conservar la referencia para el log de transiciones
    private JTextPane logTransiciones;

    //Se conserva una lista para poder dar el paso al analisis de datos (SOLO ES PROVISIONAL)
    private ArrayList<String> listaEntrada = new ArrayList<>(6000);

    public AnalizadorLexico(JTextPane areaAnalisis, ArrayList<String> listaExtraida, JTextPane paneErrores, JTextPane logAreaTransicion, AutomataDeterminista configuracion) throws ConfigException {
        this.areaAnalisis = areaAnalisis;

        this.listaEntrada = listaExtraida;

        automataFinitoDeterminista = configuracion;

        this.logErrores = paneErrores;

        this.logTransiciones = logAreaTransicion;

        this.analizarEstados = new NavegarEstados(automataFinitoDeterminista, paneErrores, this.logTransiciones);

    }

    //Metodo que permite que se pueda acceder a la lista sentencias
    public ArrayList<Sentencia> getListadoSentencias() {
        return this.listaSentencias;
    }

    //Metodo que permite inicializar la separacion de lexemas
    public void descomponerLexemas(JTextPane paneError) throws BadLocationException {

        paneError.setText("");

        //Ciclo que permite recorrer linea por linea para ir generando las instancias e indicar en que linea estan 
        for (int i = 0; i < listaEntrada.size(); i++) {
            int linea = i + 1;

            String filaTexto = listaEntrada.get(i);

            StringBuilder cadenaCompleta = new StringBuilder();

            boolean entreComillas = false;

            ArrayList<Lexema> lexemaSeparado = new ArrayList<>(5000);

            if (filaTexto == null || filaTexto.isEmpty()) {
                lexemaSeparado.add(new Lexema("", linea));
                this.listaSentencias.add(new Sentencia(lexemaSeparado, linea));
                continue;
            }

            //Ciclo que permite ir armanndo los lexemas
            for (int j = 0; j < filaTexto.length(); j++) {

                char caracter = filaTexto.charAt(j);

                if (caracter == '\"') {

                    entreComillas = !entreComillas;
                    cadenaCompleta.append(caracter);

                } else if (caracter == '\t' && !entreComillas) {

                    lexemaSeparado.add(new Lexema("      ", linea));

                } else if (Character.isWhitespace(caracter) && !entreComillas) {

                    if (cadenaCompleta.length() > 0) {
                        lexemaSeparado.add(new Lexema(cadenaCompleta.toString(), linea));
                        cadenaCompleta.setLength(0);
                    }

                    lexemaSeparado.add(new Lexema(String.valueOf(caracter), linea));

                } else {
                    cadenaCompleta.append(caracter);
                }

            }

            if (cadenaCompleta.length() > 0) {
                lexemaSeparado.add(new Lexema(cadenaCompleta.toString(), linea));
                cadenaCompleta.setLength(0);
            }

            if (!lexemaSeparado.isEmpty()) {
                this.listaSentencias.add(new Sentencia(lexemaSeparado, linea));
            }

        }

        //Metodo encargado de poder procesar y convertir todos los lexemas
        int ubicacionFisica = 0;
        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            int columna = 1;

            for (int j = 0; j < sentenciaActiva.limiteLexemas(); j++) {

                Lexema lexemaDado = sentenciaActiva.getListaLexema(j);

                lexemaDado.setPosicionLexema(j);

                int fila = lexemaDado.getFilaCoordenada();

                String palabra = lexemaDado.getLexema();

                if (palabra == null) {

                    ubicacionFisica += 1;
                    continue;
                }

                if (palabra.isEmpty()) {

                    ubicacionFisica += 1;
                    continue;
                }

                //Metodo que se encarga de separar todos los nodos
                columna = lexemaDado.separarNodos(palabra, columna, fila);
                ubicacionFisica = lexemaDado.generarEspacioFisico(ubicacionFisica);
                lexemaDado.calcularOffsets();

                columna++;
            }

        }

        recorrerAnalisis();

    }

    //============================REGION QUE PERMITE EL ANALISIS DE CADA LEXEMA CON SUS RESPECTIVOS NODOS===========================
    //Metodo principal y unico para analizar cada lexema moviendose entre estados
    public void recorrerAnalisis() throws BadLocationException {

        this.logTransiciones.setText("");
        limpiarLexemasSugerencia(this.areaAnalisis);

        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            for (int j = 0; j < sentenciaActiva.limiteLexemas(); j++) {

                Lexema lexemaDado = sentenciaActiva.getListaLexema(j);

                if (!lexemaDado.esYaDeclarado()) {

                    //Metodo que busca generalizar o hacer match lo mas rapido posible con un token del json
                    //Aprovecha para declarar comentarios y permitir seguir analizando los estados
                    if (buscarGeneralizacionesAFD(lexemaDado, sentenciaActiva, this.listaSentencias, (sentenciaActiva.getFilaSentencia() - 1))) {
                        continue;
                    }

                    //Continua viajando entre estados si no es un token que se puede generalizar
                    this.analizarEstados.setConstantesConfig(this.automataFinitoDeterminista);
                    this.analizarEstados.iniciarViajeEstados(lexemaDado);

                }
            }

        }

        pintarLogSalida(this.areaAnalisis, true);

    }

    //Metodo que sirve cuando la cadena se compone de cierta forma que el token esta escrito literal como en el .json
    public boolean buscarGeneralizacionesAFD(Lexema lexemaActual, Sentencia lineaPosicionada, ArrayList<Sentencia> listaSentencias, int iterador) {

        //Detecta si es palabra reservada directamente
        if (this.automataFinitoDeterminista.estadoPalabrasReservadas(lexemaActual.getLexema())) {
            lexemaActual.generalizarNodo(TokenEnum.PALABRA_RESERVADA);
            lexemaActual.setYaDeclarado(true);
            ilustrarEstadosAutomata(lexemaActual);
            return true;
        }

        String palabraSugerida = sugerirPalabra(lexemaActual);

        if (palabraSugerida != null) {
            lexemaActual.generalizarNodo(TokenEnum.ERROR);
            lexemaActual.setYaDeclarado(true);
            ilustrarEstadosAutomata(lexemaActual);
            recomendarPalabra(this.areaAnalisis, lexemaActual, palabraSugerida);
            return true;
        }

        //Detecta si es operador directamente
        if (lexemaActual.getLongitudNodo() == 1 && this.automataFinitoDeterminista.estadoOperadoresMatematicos(lexemaActual.getLexema().charAt(0))) {
            lexemaActual.generalizarNodo(TokenEnum.OPERADOR);
            lexemaActual.setYaDeclarado(true);
            ilustrarEstadosAutomata(lexemaActual);
            return true;
        }

        //Detecta si es signo de agrupacion directamente
        if (lexemaActual.getLongitudNodo() == 1 && this.automataFinitoDeterminista.estadoAgrupacion(lexemaActual.getLexema().charAt(0))) {
            lexemaActual.generalizarNodo(TokenEnum.AGRUPACION);
            lexemaActual.setYaDeclarado(true);
            ilustrarEstadosAutomata(lexemaActual);
            return true;
        }

        //Detecta si es signo de puntuacion directamente
        if (lexemaActual.getLongitudNodo() == 1 && this.automataFinitoDeterminista.estadoPuntuacion(String.valueOf(lexemaActual.getLexema().charAt(0)))) {
            lexemaActual.generalizarNodo(TokenEnum.PUNTUACION);
            lexemaActual.setYaDeclarado(true);
            ilustrarEstadosAutomata(lexemaActual);
            return true;
        }

        //Detecta si existe un comentario de linea en cualquier linea o posicion que se encuentre 
        if (lexemaActual.getLongitudNodo() > 1) {
            //Detecta si es comentario directamente DE UNA LINEA
            String lexemaInicial = String.valueOf(lexemaActual.getValorNodo(0).getCaracter()) + String.valueOf(lexemaActual.getValorNodo(1).getCaracter());

            if (this.automataFinitoDeterminista.estadoComentarioLinea(lexemaInicial)) {

                int filaUbicacionLexema = lexemaActual.getPosicionLexema();

                ArrayList<Lexema> listadoSentenciaLexemaComentario = lineaPosicionada.obtenerListadoLexemas();

                for (int k = filaUbicacionLexema; k < listadoSentenciaLexemaComentario.size(); k++) {

                    Lexema lexemaUbicadoComentado = listadoSentenciaLexemaComentario.get(k);
                    lexemaUbicadoComentado.generalizarNodo(TokenEnum.COMENTARIO_LINEA);
                    lexemaUbicadoComentado.setYaDeclarado(true);
                }

                return true;
            }

            if (lexemaActual.getValorNodo(0).getCaracter() == '/' && lexemaActual.getValorNodo(1).getCaracter() != '/' && lexemaActual.getValorNodo(1).getCaracter() != '*') {

                int filaUbicacionLexema = lexemaActual.getPosicionLexema();

                ArrayList<Lexema> listadoSentenciaLexemaComentario = lineaPosicionada.obtenerListadoLexemas();

                for (int k = filaUbicacionLexema; k < listadoSentenciaLexemaComentario.size(); k++) {

                    Lexema lexemaUbicadoComentado = listadoSentenciaLexemaComentario.get(k);

                    lexemaUbicadoComentado.generalizarNodo(TokenEnum.ERROR);
                    lexemaUbicadoComentado.setYaDeclarado(true);
                    lexemaUbicadoComentado.setLexemaError(lexemaActual.getLexema() + " Formato no apropiado de comentario. NO TOKEN");
                }

                return true;
            }

        }

        //Busca la generalidad de poder generar un comentario multilinea 
        if (lexemaActual.getLongitudNodo() > 1) {

            //Detecta si es comentario directamente DE UNA LINEA
            String cadenaLexema = String.valueOf(lexemaActual.getValorNodo(0).getCaracter()) + String.valueOf(lexemaActual.getValorNodo(1).getCaracter());

            if (this.automataFinitoDeterminista.estadoBloqueComentarioInicial(cadenaLexema)) {

                lexemaActual.generalizarNodo(TokenEnum.COMENTARIO_BLOQUE);
                lexemaActual.setYaDeclarado(true);

                boolean finHallado = false;

                int filaLexemaBloque = lexemaActual.getPosicionLexema();

                ArrayList<Lexema> listadoSentenciaLexemaBloque = lineaPosicionada.obtenerListadoLexemas();

                for (int k = filaLexemaBloque; k < listadoSentenciaLexemaBloque.size(); k++) {

                    Lexema lexemaUbicadoComentado = listadoSentenciaLexemaBloque.get(k);
                    lexemaUbicadoComentado.generalizarNodo(TokenEnum.COMENTARIO_BLOQUE);
                    lexemaUbicadoComentado.setYaDeclarado(true);

                    int indice = lexemaUbicadoComentado.getLongitudNodo() - 1;

                    if (lexemaUbicadoComentado.getLongitudNodo() > 1) {

                        String lineaCierre = String.valueOf(lexemaUbicadoComentado.getValorNodo(indice - 1).getCaracter()) + String.valueOf(lexemaUbicadoComentado.getValorNodo(indice).getCaracter());

                        if (this.automataFinitoDeterminista.estadoBloqueComentarioFin(lineaCierre)) {
                            finHallado = true;
                            break;

                        }
                    }

                }

                //Ciclo que permite recorrer las lineas consecuentes al comentario multilinea 
                for (int i = iterador + 1; i < listaSentencias.size(); i++) {

                    if (finHallado) {
                        break;
                    }

                    Sentencia sentenciaIndex = listaSentencias.get(i);

                    for (Lexema posicion : sentenciaIndex.obtenerListadoLexemas()) {

                        int indice = posicion.getLongitudNodo() - 1;

                        posicion.generalizarNodo(TokenEnum.COMENTARIO_BLOQUE);
                        posicion.setYaDeclarado(true);

                        if (posicion.getLongitudNodo() > 1) {

                            String lineaCierre = String.valueOf(posicion.getValorNodo(indice - 1).getCaracter()) + String.valueOf(posicion.getValorNodo(indice).getCaracter());

                            if (this.automataFinitoDeterminista.estadoBloqueComentarioFin(lineaCierre)) {
                                finHallado = true;
                                break;

                            }
                        }

                    }

                    if (finHallado) {
                        break;
                    }
                }

                //Condicion que declara el error si en dado caso el comentario de bloque no tiene cierre
                if (!finHallado) {
                    declararComentarioBloque(lexemaActual, lineaPosicionada, listaSentencias, iterador);
                }

                return true;
            }

        }

        return false;
    }

    //SUBMETODO que permite declarar que el comentario de bloque NO TIENE CIERRE
    private void declararComentarioBloque(Lexema lexemaActual, Sentencia lineaPosicionada, ArrayList<Sentencia> listaSentencias, int iterador) {

        int filaLexemaBloque = lexemaActual.getPosicionLexema();

        ArrayList<Lexema> listadoSentenciaLexemaBloque = lineaPosicionada.obtenerListadoLexemas();

        for (int k = filaLexemaBloque; k < listadoSentenciaLexemaBloque.size(); k++) {

            Lexema comentarioBloqueUbicado = listadoSentenciaLexemaBloque.get(k);
            comentarioBloqueUbicado.generalizarNodo(TokenEnum.ERROR);
            comentarioBloqueUbicado.setYaDeclarado(true);

            comentarioBloqueUbicado.setLexemaError(lexemaActual.getLexema() + "... Comentario de bloque sin cierre. Fila " + comentarioBloqueUbicado.getFilaCoordenada());
        }

        for (int i = iterador + 1; i < listaSentencias.size(); i++) {

            Sentencia sentenciaIndex = listaSentencias.get(i);

            for (Lexema posicion : sentenciaIndex.obtenerListadoLexemas()) {
                posicion.generalizarNodo(TokenEnum.ERROR);
                posicion.setYaDeclarado(true);
                posicion.setLexemaError(lexemaActual.getLexema() + "... Comentario de bloque sin cierre. Fila " + posicion.getFilaCoordenada());
            }

        }

    }

    //METODO MAS IMPORTANTE PARA PODER GENERAR LAS NAVEGACIONES ENTRE ESTADOS DEL AUTOMATA
    public void ilustrarEstadosAutomata(Lexema lexemaEvaluado) {

        try {
            Color colorTexto = obtenerColorPorToken(lexemaEvaluado.getEstadoAnalisis());

            insertarToken("Con " + lexemaEvaluado.getEstadoAnalisis().getContexto() + ": ", Color.BLACK, this.logTransiciones);

            insertarToken(" " + lexemaEvaluado.getLexema(), colorTexto, this.logTransiciones);

            insertarToken("\n", Color.BLACK, this.logTransiciones);

            Color colorEstados = new Color(0x0085A6);

            for (int i = 0; i < lexemaEvaluado.obtenerListaNodo().size(); i++) {

                Nodo nodoEvaluado = lexemaEvaluado.getValorNodo(i);

                insertarToken("Me movi del estado " + (i + 1) + " al estado " + (i + 2) + " con una ", colorEstados, this.logTransiciones);

                insertarToken(String.valueOf(nodoEvaluado.getCaracter()), new Color(0x292724), this.logTransiciones);

                insertarToken("\n", Color.BLACK, this.logTransiciones);

            }

            insertarToken("Guardando token " + lexemaEvaluado.getEstadoAnalisis().getNombreToken() + ". Lexema: " + lexemaEvaluado.getLexema(), new Color(0x085717), this.logTransiciones);

            insertarToken("\n", Color.BLACK, this.logTransiciones);

            insertarToken("Reiniciando Automata...", Color.BLACK, this.logTransiciones);

            insertarToken("\n", Color.BLACK, this.logTransiciones);
            insertarToken("\n", Color.BLACK, this.logTransiciones);

        } catch (BadLocationException ex) {
            System.out.println("No se ha podido pintar el log de transiciones");
        }

    }

    //============================FIN DE LA REGION QUE PERMITE EL ANALISIS DE CADA LEXEMA CON SUS RESPECTIVOS NODOS===========================
    //METODO UNICO QUE SIRVE PARA COLOREAR LOS LOG DE SALIDA
    public void pintarLogSalida(JTextPane paneAnalisis, boolean enAnalisis) throws BadLocationException {

        int caretOffset = paneAnalisis.getCaretPosition();
        StyledDocument doc = paneAnalisis.getStyledDocument();

        // Línea y columna reales antes de limpiar
        int lineaCaret = doc.getDefaultRootElement().getElementIndex(caretOffset);
        int columnaCaret = caretOffset - doc.getDefaultRootElement()
                .getElement(lineaCaret)
                .getStartOffset();

        limpiarAreaAnalisis(paneAnalisis);

        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            for (Lexema lexemaDado : sentenciaActiva.obtenerListadoLexemas()) {

                if (lexemaDado.getLexema().equals(" ") && lexemaDado.getEstadoAnalisis() == TokenEnum.INDEFINIDO) {
                    insertarToken(" ", Color.BLACK, paneAnalisis);
                    continue;
                }

                for (Nodo nodo : lexemaDado.obtenerListaNodo()) {

                    Color colorTexto = obtenerColorPorToken(nodo.getToken());

                    insertarToken(String.valueOf(nodo.getCaracter()), colorTexto, paneAnalisis);

                }
            }

            insertarToken("\n", Color.BLACK, paneAnalisis);

        }

        //Se restaura la posicion del caret
        StyledDocument newDoc = paneAnalisis.getStyledDocument();
        Element root = newDoc.getDefaultRootElement();
        if (lineaCaret < root.getElementCount()) {
            Element lineElem = root.getElement(lineaCaret);
            int start = lineElem.getStartOffset();
            int end = lineElem.getEndOffset();
            int nuevaPos = start + Math.min(columnaCaret, end - start - 1);
            paneAnalisis.setCaretPosition(nuevaPos);
        } else {
            paneAnalisis.setCaretPosition(newDoc.getLength());
        }

        mostrarErrores(enAnalisis);

    }

    //Metodo encargado de imprimir los errores en el log de errores
    //True refresca todo el log con el lexer
    //False no hace nada porque esta en busquedas
    private void mostrarErrores(boolean enAnalisis) {

        if (!enAnalisis) {
            return;
        }

        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            for (Lexema lexemaDado : sentenciaActiva.obtenerListadoLexemas()) {

                if (lexemaDado.getLexema().isBlank()) {
                    continue;
                }

                if (!lexemaDado.getCadenaError().isBlank()) {
                    this.logErrores.setText(this.logErrores.getText() + lexemaDado.getLexema() + " <- Error, en " + lexemaDado.getCadenaError() + "\n");
                }

            }

        }

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

    //Metodo que trabaja en conjunto para poder ir pintando letra a letra
    private void limpiarAreaAnalisis(JTextPane paneAnalisis) throws BadLocationException {
        StyledDocument doc = paneAnalisis.getStyledDocument();
        doc.remove(0, doc.getLength());

    }

    // Método para insertar texto con un color específico
    private void insertarToken(String texto, Color color, JTextPane paneAnalisis) throws BadLocationException {

        StyledDocument doc = paneAnalisis.getStyledDocument();
        // Crear estilo temporal
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color);
        // Inserta al final del documento
        doc.insertString(doc.getLength(), texto, estilo);

    }

    //===========================APARTADO PARA GESTIONAR LAS BUSQUEDAS EN EL TEXTO========================================
    public void busquedaPatrones(JTextPane paneBusqueda, String[] palabraBuscada) throws BadLocationException, ErrorEncontradoException, ErrorPuntualException {

        pintarLogSalida(paneBusqueda, false);

        if (this.listaSentencias.isEmpty()) {
            throw new ErrorEncontradoException("El texto esta vacio");
        }

        Highlighter highlighter = paneBusqueda.getHighlighter();
        highlighter.removeAllHighlights();

        //Variable importante para saber si minimo hay una coincidencia
        boolean hayCoincidencia = false;

        for (int i = 0; i < palabraBuscada.length; i++) {
            String patron = palabraBuscada[i];

            hayCoincidencia = busquedaSofisticada(patron, paneBusqueda);

        }

        //busqueda sofisticada para encontrar cualquier palabra
        if (hayCoincidencia) {
            return;
        }

        //busqueda sofisticada para encontrar cualquier palabra
        if (!hayCoincidencia) {
            throw new ErrorPuntualException("No existen patrones relacionados");
        }

    }

    private boolean busquedaSofisticada(String palabraBuscada, JTextPane paneBusqueda) throws BadLocationException {
        boolean encontrado = false;
        for (int i = 0; i < this.listaSentencias.size(); i++) {
            Sentencia sentenciaBuscada = this.listaSentencias.get(i);

            for (int j = 0; j < sentenciaBuscada.limiteLexemas(); j++) {
                Lexema lexemaActual = sentenciaBuscada.getListaLexema(j);

                // Recorremos caracter por caracter
                for (int k = 0; k <= lexemaActual.getLongitudNodo() - palabraBuscada.length(); k++) {

                    boolean match = true;

                    for (int m = 0; m < palabraBuscada.length(); m++) {
                        Nodo nodoActual = lexemaActual.getValorNodo(k + m);
                        char caracter = nodoActual.getCaracter();

                        if (caracter != palabraBuscada.charAt(m)) {
                            match = false;
                            break;
                        }
                    }

                    if (match) {
                        // Coordenadas exactas
                        Nodo nodoInicio = lexemaActual.getValorNodo(k);
                        Nodo nodoFin = lexemaActual.getValorNodo(k + palabraBuscada.length() - 1);

                        int fila = nodoInicio.getLinea() - 1;
                        if (fila < 0) {
                            fila = 0;
                        }

                        int columnaInicio = nodoInicio.getColumna() - 1;
                        if (columnaInicio < 0) {
                            columnaInicio = 0;
                        }

                        int columnaFin = nodoFin.getColumna();
                        if (columnaFin < 0) {
                            columnaFin = 0;
                        }

                        resaltar(paneBusqueda, fila, columnaInicio, columnaFin);
                        encontrado = true;
                    }
                }

            }
        }

        return encontrado;

    }

    //Metodo que se encarga de resaltar la palabra que se busca
    public void resaltar(JTextPane textPane, int linea, int startColumna, int endColumna) throws BadLocationException {
        Highlighter highlighter = textPane.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        int startOffset = getLineaInicioFin(textPane, linea, startColumna);
        int endOffset = getLineaInicioFin(textPane, linea, endColumna);

        highlighter.addHighlight(startOffset, endOffset, painter);
    }

    //Metodo que permite ubicar la coordenada para resaltar
    private int getLineaInicioFin(JTextPane textPane, int linea, int columna) throws BadLocationException {
        Element root = textPane.getDocument().getDefaultRootElement();
        Element lineElem = root.getElement(linea); // obtiene la línea
        int start = lineElem.getStartOffset();
        return start + columna;
    }

    //===========================FIN DEL APARTADO PARA GESTIONAR LAS BUSQUEDAS EN EL TEXTO========================================
    //=================REGION QUE PERMITE RECONOCER LAS PALABRAS RESERVADAS EN CUALQUIER POSICION DE LA ER============================
    //Metodo que permite detectar si es una palabra reservada en cualquier posicion
    public String sugerirPalabra(Lexema lexemaEntrada) {

        String[] reservadas = this.automataFinitoDeterminista.getPalabrasReservadasArray();
        String sugerencia = null;
        int mejorDistancia = Integer.MAX_VALUE;

        for (String palabra : reservadas) {
            int distancia = metodoLevenshteinOrden(lexemaEntrada.getLexema(), palabra);

            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                sugerencia = palabra;
            }
        }

        if (sugerencia != null) {
            double similitud = 1.0 - ((double) mejorDistancia
                    / Math.max(lexemaEntrada.getLexema().length(), sugerencia.length()));

            if (lexemaEntrada.getLexema().length() < sugerencia.length() / 2) {
                return null;
            }

            double umbral = 0.7;
            if (similitud >= umbral && lexemaEntrada.getLexema().length() >= 2) {
                return sugerencia;
            }

        }

        return null;
    }

    //Metodo de implementacion basica de la distancia de Levenshtein 
    /*
        Metodologia que permite reconocer errores ortograficos
        Agrega un caracter 
        Quita el caracter cambiandolo por otro
        De esta forma se permite calcular el numero minimo de operaciones para que una cadena 
        se convierta en la otra 
    
        METODO MUY INTERESANTE DE LEER Y APRENDER. En lo personal no sabia pero ahora se una tecnica para recomendar palabras
     */
    public int metodoLevenshteinOrden(String a, String b) {
        int[][] dependencia = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dependencia[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dependencia[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int costo = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dependencia[i][j] = Math.min(
                        Math.min(dependencia[i - 1][j] + 1, dependencia[i][j - 1] + 1),
                        dependencia[i - 1][j - 1] + costo
                );
            }
        }

        return dependencia[a.length()][b.length()];
    }

    //Metodo que permite mostrar las sugerencias en el cuadro de edicion
    public void recomendarPalabra(JTextPane logEdicion, Lexema palabraReservada, String sugerencia) {
        List<Lexema> lista = (List<Lexema>) logEdicion.getClientProperty("lexemasErroneos");
        if (lista == null) {
            lista = new ArrayList<>();
            logEdicion.putClientProperty("lexemasErroneos", lista);
        }

        palabraReservada.setSugerenciaEstimada(sugerencia);
        lista.add(palabraReservada);
    }

    //Metodo que permite ir limpiando la lista por cada vez que se escribe a tiempo real
    public void limpiarLexemasSugerencia(JTextPane pane) {
        List<Lexema> lista = (List<Lexema>) pane.getClientProperty("lexemasErroneos");
        if (lista != null) {
            lista.clear();
        }
        pane.putClientProperty("lexemaLastShown", null);
    }

    //=================FIN DE LA REGION QUE PERMITE RECONOCER LAS PALABRAS RESERVADAS EN CUALQUIER POSICION DE LA ER=============================
}
