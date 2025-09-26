/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import com.pablocompany.proyectono1lfp.backend.excepciones.ConfigException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorEncontradoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JTextPane;
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
    private ConfigDatos constantesConfig;

    //==============================FIN DE LA REGION DE APARTADOS DE CONSTANTES GRAMATICA======================================
    //Estructura dinamica encargada de almacenar por completo caracter a caracter
    private ArrayList<Sentencia> listaSentencias = new ArrayList<>(5000);

    //Atributo que permite referenciar a la modificacion del JTextPane
    private JTextPane areaAnalisis;

    private NavegarEstados analizarEstados;

    private JTextPane logErrores;

    //Se conserva una lista para poder dar el paso al analisis de datos (SOLO ES PROVISIONAL)
    private ArrayList<String> listaEntrada = new ArrayList<>(6000);

    public AnalizadorLexico(JTextPane areaAnalisis, ArrayList<String> listaExtraida, JTextPane paneErrores, ConfigDatos configuracion) throws ConfigException {
        this.areaAnalisis = areaAnalisis;

        this.listaEntrada = listaExtraida;

        this.constantesConfig = configuracion;

        this.logErrores = paneErrores;

        this.analizarEstados = new NavegarEstados(this.constantesConfig, paneErrores);

    }

    //Metodo que permite que se pueda acceder a la lista sentencias
    public ArrayList<Sentencia> getListadoSentencias(){
        return this.listaSentencias;
    }
    
    //Metodo que permite inicializar la separacion de lexemas FINALIZADO
    public void descomponerLexemas(JTextPane pane) throws BadLocationException {

        pane.setText("");

        //Ciclo que permite recorrer linea por linea para ir generando las instancias e indicar en que linea estan 
        for (int i = 0; i < listaEntrada.size(); i++) {
            int linea = i + 1;

            String filaTexto = listaEntrada.get(i).trim();

            StringBuilder cadenaCompleta = new StringBuilder();

            boolean entreComillas = false;

            ArrayList<Lexema> lexemaSeparado = new ArrayList<>(5000);

            if (filaTexto.isBlank()) {
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

                } else if (Character.isWhitespace(caracter) && !entreComillas) {

                    if (cadenaCompleta.length() > 0) {
                        lexemaSeparado.add(new Lexema(cadenaCompleta.toString(), linea));
                        cadenaCompleta.setLength(0);
                    }
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

        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            int columna = 1;

            for (int j = 0; j < sentenciaActiva.limiteLexemas(); j++) {

                Lexema lexemaDado = sentenciaActiva.getListaLexema(j);

                int fila = lexemaDado.getFilaCoordenada();

                String palabra = lexemaDado.getLexema();

                if (palabra.isBlank()) {
                    continue;
                }

                //Metodo que se encarga de separar todos los nodos
                columna = lexemaDado.separarNodos(palabra, columna, fila);
                columna++;
            }

        }

        recorrerAnalisis();

    }

    //============================REGION QUE PERMITE EL ANALISIS DE CADA LEXEMA CON SUS RESPECTIVOS NODOS===========================
    //Metodo principal y unico para analizar cada lexema moviendose entre estados
    public void recorrerAnalisis() throws BadLocationException {

        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            for (int j = 0; j < sentenciaActiva.limiteLexemas(); j++) {

                Lexema lexemaDado = sentenciaActiva.getListaLexema(j);

                String palabra = lexemaDado.getLexema();

                //Evita todas las lineas en blanco que existan
                if (palabra.isBlank()) {
                    continue;
                }

                if (!lexemaDado.esYaDeclarado()) {

                    //Metodo que busca generalizar o hacer match lo mas rapido posible con un token del json
                    //Aprovecha para declarar comentarios y permitir seguir analizando los estados
                    if (buscarGeneralizaciones(lexemaDado, sentenciaActiva, this.listaSentencias, (sentenciaActiva.getFilaSentencia() - 1))) {
                        continue;
                    }

                    //Continua viajando entre estados si no es un token que se puede generalizar
                    this.analizarEstados.setConstantesConfig(this.constantesConfig);
                    this.analizarEstados.iniciarViajeEstados(lexemaDado);

                }
            }

        }

        pintarLogSalida(this.areaAnalisis, true);

    }

    //Metodo que sirve cuando la cadena se compone de cierta forma que el token esta escrito literal como en el .json
    public boolean buscarGeneralizaciones(Lexema lexemaActual, Sentencia lineaPosicionada, ArrayList<Sentencia> listaSentencias, int iterador) {

        //Detecta si es palabra reservada directamente
        if (this.constantesConfig.esPalabrasReservadas(lexemaActual.getLexema())) {
            lexemaActual.generalizarNodo(Token.PALABRA_RESERVADA);
            lexemaActual.setYaDeclarado(true);
            return true;
        }

        //Detecta si es operador directamente
        if (lexemaActual.getLongitudNodo() == 1 && this.constantesConfig.esOperadores(lexemaActual.getLexema().charAt(0))) {
            lexemaActual.generalizarNodo(Token.OPERADOR);
            lexemaActual.setYaDeclarado(true);
            return true;
        }

        //Detecta si es signo de agrupacion directamente
        if (lexemaActual.getLongitudNodo() == 1 && this.constantesConfig.esAgrupacion(lexemaActual.getLexema().charAt(0))) {
            lexemaActual.generalizarNodo(Token.AGRUPACION);
            lexemaActual.setYaDeclarado(true);
            return true;
        }

        //Detecta si es signo de puntuacion directamente
        if (lexemaActual.getLongitudNodo() == 1 && this.constantesConfig.esPuntuacion(lexemaActual.getLexema().charAt(0))) {
            lexemaActual.generalizarNodo(Token.PUNTUACION);
            lexemaActual.setYaDeclarado(true);
            return true;
        }

        if (lexemaActual.getLongitudNodo() > 1) {

            //Detecta si es comentario directamente DE UNA LINEA
            String lexemaInicial = String.valueOf(lexemaActual.getValorNodo(0).getCaracter()) + String.valueOf(lexemaActual.getValorNodo(1).getCaracter());

            if (this.constantesConfig.esComentarioLinea(lexemaInicial)) {

                for (Lexema posicion : lineaPosicionada.obtenerListadoLexemas()) {

                    posicion.generalizarNodo(Token.COMENTARIO_LINEA);
                    posicion.setYaDeclarado(true);
                }

                return true;
            }

            if (lexemaActual.getValorNodo(0).getCaracter() == '/' && lexemaActual.getValorNodo(1).getCaracter() != '/' && lexemaActual.getValorNodo(1).getCaracter() != '*') {
                lexemaActual.generalizarNodo(Token.ERROR);
                lexemaActual.setYaDeclarado(true);
                lexemaActual.setLexemaError(lexemaActual.getLexema() + " Formato no apropiado de comentario. NO TOKEN");
                return true;
            }

        }

        //Busca la generalidad de poder generar un comentario multilinea
        if (lexemaActual.getLongitudNodo() > 1) {

            //Detecta si es comentario directamente DE UNA LINEA
            String cadenaLexema = String.valueOf(lexemaActual.getValorNodo(0).getCaracter()) + String.valueOf(lexemaActual.getValorNodo(1).getCaracter());

            if (this.constantesConfig.esBloqueComentarioInicial(cadenaLexema)) {

                lexemaActual.generalizarNodo(Token.COMENTARIO_BLOQUE);
                lexemaActual.setYaDeclarado(true);

                boolean finHallado = false;

                for (int i = iterador; i < listaSentencias.size(); i++) {

                    Sentencia sentenciaIndex = listaSentencias.get(i);

                    for (Lexema posicion : sentenciaIndex.obtenerListadoLexemas()) {

                        int indice = posicion.getLongitudNodo() - 1;

                        posicion.generalizarNodo(Token.COMENTARIO_BLOQUE);
                        posicion.setYaDeclarado(true);

                        if (posicion.getLongitudNodo() > 1) {

                            String lineaCierre = String.valueOf(posicion.getValorNodo(indice - 1).getCaracter()) + String.valueOf(posicion.getValorNodo(indice).getCaracter());

                            if (this.constantesConfig.esBloqueComentarioFin(lineaCierre)) {
                                finHallado = true;

                                break;

                            }
                        }

                    }

                    if (finHallado) {
                        break;
                    }

                }

                return true;
            }

        }

        return false;
    }

    //============================FIN DE LA REGION QUE PERMITE EL ANALISIS DE CADA LEXEMA CON SUS RESPECTIVOS NODOS===========================
    //METODO UNICO QUE SIRVE PARA COLOREAR LOS LOG DE SALIDA
    public void pintarLogSalida(JTextPane paneAnalisis, boolean enAnalisis) throws BadLocationException {

        int caret = paneAnalisis.getCaretPosition();
        int lineaCaret = paneAnalisis.getDocument().getDefaultRootElement().getElementIndex(caret);
        int columnaCaret = caret - paneAnalisis.getDocument().getDefaultRootElement().getElement(lineaCaret).getStartOffset();

        //int posicionCaret = this.areaAnalisis.getCaretPosition();
        limpiarAreaAnalisis(paneAnalisis);

        for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            for (Lexema lexemaDado : sentenciaActiva.obtenerListadoLexemas()) {

                if (lexemaDado.getLexema().isBlank()) {
                    continue;
                }

                for (Nodo nodo : lexemaDado.obtenerListaNodo()) {

                    Color colorTexto = obtenerColorPorToken(nodo.getToken());

                    insertarToken(String.valueOf(nodo.getCaracter()), colorTexto, paneAnalisis);

                }
                insertarToken(" ", Color.BLACK, paneAnalisis);
            }

            insertarToken("\n", Color.BLACK, paneAnalisis);

        }

        try {

            Element root = paneAnalisis.getDocument().getDefaultRootElement();
            if (lineaCaret < root.getElementCount()) {

                int nuevaPos = root.getElement(lineaCaret).getStartOffset() + Math.min(columnaCaret, root.getElement(lineaCaret).getEndOffset() - root.getElement(lineaCaret).getStartOffset() - 1);
                paneAnalisis.setCaretPosition(nuevaPos);
            }

        } catch (Exception e) {
            paneAnalisis.setCaretPosition(0);
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
    private Color obtenerColorPorToken(Token tipo) {
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
    public void busquedaPatrones(JTextPane paneBusqueda, String [] palabraBuscada) throws BadLocationException, ErrorEncontradoException, ErrorPuntualException {

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
        
        //BUSQUEDA SIMPLE DE MATCH RAPIDO
        /*for (int i = 0; i < this.listaSentencias.size(); i++) {
            Sentencia sentenciaBuscada = this.listaSentencias.get(i);

            for (int j = 0; j < sentenciaBuscada.limiteLexemas(); j++) {

                Lexema lexemaActual = sentenciaBuscada.getListaLexema(j);

                if (lexemaActual.getLexema().equals(palabraBuscada)) {
                    hayCoincidencia = true;

                    int fila = lexemaActual.getFilaCoordenada() - 1;

                    if (fila < 0) {
                        fila = 0;
                    }

                    int columnaInicio = lexemaActual.getValorNodo(0).getColumna() - 1;
                    if (columnaInicio < 0) {
                        columnaInicio = 0;
                    }
                    int columnaFin = lexemaActual.getValorNodo(lexemaActual.getLongitudNodo() - 1).getColumna();
                    if (columnaFin < 0) {
                        columnaFin = 0;
                    }

                    resaltar(paneBusqueda, fila, columnaInicio, columnaFin);
                }

            }

        }
        */
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
    
}
