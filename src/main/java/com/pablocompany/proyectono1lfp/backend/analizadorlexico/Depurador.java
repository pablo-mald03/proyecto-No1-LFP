/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import static com.pablocompany.proyectono1lfp.backend.analizadorlexico.TokenEnum.*;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
//Clase que permite depurar las entradas de texto
public class Depurador {

    //-> Indice de sentencia
    private int indiceSentencia;
    //-> Indice de lexema
    private int indiceLexema;

    //-> Indice de Nodo
    private int indiceNodo;

    //Log de muestra de estados
    private JTextPane paneDepuracion;

    //Va mostrando la palabra armada
    private StringBuilder lexemaOperado;

    //Referencia a las sentencias analizadas por el Analizador Lexico
    private ArrayList<Sentencia> listaSentencias;

    public Depurador(ArrayList<Sentencia> listaSentencias) {
        this.indiceSentencia = 0;
        this.indiceLexema = 0;
        this.indiceNodo = 0;
        this.listaSentencias = listaSentencias;
    }

    //Metodo que permite reniciar el depurador cuando se abandona el analisis
    public void reiniciarDepurador() {
        this.indiceSentencia = 0;
        this.indiceLexema = 0;
        this.indiceNodo = 0;
    }

    //Metodo que ayuda a establecer el pane de depuracion
    public void setPaneDepuracion(JTextPane paneDepuracion) {
        this.paneDepuracion = paneDepuracion;
    }

    //====================APARTADO DE METODOS QUE PERMITEN IR AVANZANDO ACORDE A LOS PASOS QUE REQUIERA EL DEPURADOR======================
    //Metodo que permite avanzar paso a paso hacia la derecha
    public void avanzarSiguientePaso() {

    }

    //Metodo que permite avanzar paso a paso hacia la izquierda
    public void regresarPaso() {

    }

    //Metodo que permite regresar al estado incial del lexema
    public void reiniciarAnalisisDepurado() {

    }

    //Metodo que permite viajar al estado final del lexema
    public void ejecutarFinal() {

    }

    //Submetodo que permite establecer el indice de la sentencia retornada para evaluar
    private Sentencia getSentenciaEvaluada() {
        return this.listaSentencias.get(this.indiceSentencia);
    }

    //Submetodo que permite establecer el indice de la sentencia retornada para evaluar
    private Lexema getLexemaEvaluado(Sentencia sentenciaDepurada) {
        return sentenciaDepurada.getListaLexema(this.indiceLexema);
    }

    //====================FIN DEL APARTADO DE METODOS QUE PERMITEN IR AVANZANDO ACORDE A LOS PASOS QUE REQUIERA EL DEPURADOR======================
    //METODO MAS IMPORTANTE PARA PODER GENERAR LAS NAVEGACIONES ENTRE ESTADOS DEL AUTOMATA PARA EL DEPURADOR
    public void ilustrarEstadosAutomata(Lexema lexemaEvaluado) {

        try {
            Color colorTexto = obtenerColorPorToken(lexemaEvaluado.getEstadoAnalisis());

            Color colorLexema = new Color(0x058C9E);

            insertarEstadoTransicion("Con " + lexemaEvaluado.getEstadoAnalisis().getContexto() + ": ", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion(" " + lexemaEvaluado.getLexema(), colorLexema, this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            Color colorEstados = new Color(0x0085A6);

            for (int i = 0; i < lexemaEvaluado.obtenerListaNodo().size(); i++) {

                Nodo nodoEvaluado = lexemaEvaluado.getValorNodo(i);

                insertarEstadoTransicion("Me movi del estado " + (i + 1) + " al estado " + (i + 2) + " con una ", colorEstados, this.paneDepuracion);

                insertarEstadoTransicion(String.valueOf(nodoEvaluado.getCaracter()), new Color(0x292724), this.paneDepuracion);

                insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            }

            insertarEstadoTransicion("Guardando token " + lexemaEvaluado.getEstadoAnalisis().getNombreToken() + ". Lexema: " + lexemaEvaluado.getLexema(), new Color(0x085717), this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("Reiniciando Automata...", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);
            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

        } catch (BadLocationException ex) {
            System.out.println("No se ha podido pintar el log de transiciones");
        }

    }

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

}
