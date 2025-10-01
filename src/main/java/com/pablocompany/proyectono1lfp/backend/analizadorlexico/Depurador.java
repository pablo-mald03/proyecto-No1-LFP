/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexico;

import static com.pablocompany.proyectono1lfp.backend.analizadorlexico.TokenEnum.*;
import com.pablocompany.proyectono1lfp.backend.excepciones.DepuradorException;
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

    //Atributo que permite indicar que el lexema se ha terminado de analizar
    private boolean yaTerminado;

    //Atributo que indica que la depuracion ha terminado
    private boolean depuracionTerminada;

    //Atributo comodin que permite evaluar cuando se ha decrementado el paso para evitar el delay
    private boolean estaDecrementado;

    //Atributo que permite evaluar cuando se ha regresado por completo el analisis al estado inicial
    private boolean estaReiniciado;

    //Atributo que indica si ya esta avanzado un paso
    private boolean estaAvanzado;

    //Atributo que indica si se ha adelantado el analisis hasta el final
    private boolean estaOmitido;

    public Depurador(ArrayList<Sentencia> listaSentencias) {
        this.indiceSentencia = 0;
        this.indiceLexema = 0;
        this.indiceNodo = 0;
        this.listaSentencias = listaSentencias;
        this.yaTerminado = false;
        this.depuracionTerminada = false;

        this.estaDecrementado = false;
        this.estaReiniciado = false;
        this.estaAvanzado = false;
        this.estaOmitido = false;
    }

    //Metodo que permite reniciar el depurador cuando se abandona el analisis
    public void reiniciarDepurador() {
        this.indiceSentencia = 0;
        this.indiceLexema = 0;
        this.indiceNodo = 0;
        this.yaTerminado = false;
        this.depuracionTerminada = false;
        this.estaDecrementado = false;
        this.estaReiniciado = false;
        this.estaAvanzado = false;
        this.estaOmitido = false;
    }

    //Metodo que ayuda a establecer el pane de depuracion
    public void setPaneDepuracion(JTextPane paneDepuracion) {
        this.paneDepuracion = paneDepuracion;
    }

    //====================APARTADO DE METODOS QUE PERMITEN IR AVANZANDO ACORDE A LOS PASOS QUE REQUIERA EL DEPURADOR======================
    //Metodo que permite avanzar paso a paso hacia la derecha
    public void avanzarSiguientePaso() throws DepuradorException {
        if (!verificarListados()) {
            throw new DepuradorException("No hay ningun lexema escrito");
        }

        int totalSentencias = this.listaSentencias.size();
        int totalLexemas = this.listaSentencias.get(this.indiceSentencia).limiteLexemas();
        int totalNodos = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLongitudNodo();

        if (this.indiceNodo == totalNodos - 1
                && this.indiceLexema == totalLexemas - 1
                && this.indiceSentencia == totalSentencias - 1) {

            this.yaTerminado = true;
            this.depuracionTerminada = true;
            ilustrarEstadosAutomata();
            this.yaTerminado = false;
            this.depuracionTerminada = false;
            return;
        }

        if (this.estaOmitido) {
            incrementarPaso();
            this.estaOmitido = false;

            if (this.estaDecrementado) {
                this.estaDecrementado = false;
            }

        }

        if (this.estaDecrementado) {
            incrementarPaso();
            this.estaDecrementado = false;

            if (estaReiniciado) {
                this.estaReiniciado = false;
            }
        }

        if (this.estaReiniciado) {
            incrementarPaso();
            this.estaReiniciado = false;

        }

        if (!this.estaAvanzado) {
            this.estaAvanzado = true;
        }

        if (this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLexema().isBlank()) {
            incrementarPaso();
        }

        ilustrarEstadosAutomata();

        incrementarPaso();

    }

    //Metodo que ayuda a incrementar el paso al avanzar en el depurador paso a paso
    private void incrementarPaso() {
        int totalSentencias = this.listaSentencias.size();

        this.indiceNodo++;

        // Recalcular siempre basado en la posición actual
        int totalLexemas = this.listaSentencias.get(this.indiceSentencia).limiteLexemas();
        int totalNodos = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLongitudNodo();

        if (this.indiceNodo >= totalNodos) {
            this.indiceNodo = 0;
            this.indiceLexema++;
        }

        if (this.indiceLexema >= totalLexemas) {
            this.indiceLexema = 0;
            this.indiceSentencia++;
        }

        if (this.indiceSentencia >= totalSentencias) {
            this.indiceSentencia = totalSentencias - 1;
            this.indiceLexema = this.listaSentencias.get(this.indiceSentencia).limiteLexemas() - 1;
            this.indiceNodo = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLongitudNodo() - 1;
            this.yaTerminado = true;
        }
    }

    //Metodo que permite avanzar paso a paso hacia la izquierda
    public void regresarPaso() throws DepuradorException {
        if (!verificarListados()) {
            throw new DepuradorException("No hay ningun lexema escrito");
        }

        if (this.indiceSentencia == 0 && this.indiceLexema == 0 && this.indiceNodo == 0) {
            ilustrarEstadosAutomata();
            return;
        }

        if (!this.estaDecrementado) {
            this.estaDecrementado = true;
        }

        boolean fueAfectado = false;

        if (this.estaOmitido) {
            decrementarPaso();
            this.estaOmitido = false;

            if (this.estaAvanzado) {
                this.estaAvanzado = false;
            }

            fueAfectado = true;

        }

        if (this.estaAvanzado) {
            decrementarPaso();
            this.estaAvanzado = false;

            if (this.estaOmitido) {
                this.estaOmitido = false;
            }
           
        }

        if (!fueAfectado) {
            decrementarPaso();
        }

        if (this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLexema().isBlank()) {
            decrementarPaso();
        }

        ilustrarEstadosAutomata();

    }

    //Metodo que ayuda a decrementar el paso en la depuracion
    private void decrementarPaso() {
        if (this.indiceSentencia == 0 && this.indiceLexema == 0 && this.indiceNodo == 0) {
            return;
        }

        this.indiceNodo--;

        if (this.indiceNodo < 0) {
            this.indiceLexema--;
            if (this.indiceLexema >= 0) {
                this.indiceNodo = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLongitudNodo() - 1;
            }
        }

        // Retroceder de lexemas
        if (this.indiceLexema < 0) {
            this.indiceSentencia--;
            if (this.indiceSentencia >= 0) {
                this.indiceLexema = this.listaSentencias.get(this.indiceSentencia).limiteLexemas() - 1;

                this.indiceNodo = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema).getLongitudNodo() - 1;
            }
        }
    }

    //Metodo que permite regresar al estado incial del lexema
    public void reiniciarAnalisisDepurado() throws DepuradorException {

        if (!verificarListados()) {
            throw new DepuradorException("No hay ningun lexema escrito");
        }

        this.indiceNodo = 0;

        Lexema lexemaEvaluado = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema);

        if (lexemaEvaluado.getLexema().isBlank()) {
            this.indiceLexema--;

            if (this.indiceLexema < 0) {
                this.indiceLexema = 0;
            }
        }

        if (!this.estaReiniciado) {
            this.estaReiniciado = true;
        }

        ilustrarEstadosAutomata();
    }

    //Metodo que permite viajar al estado final del lexema
    public void ejecutarFinal() throws DepuradorException {
        if (!verificarListados()) {
            throw new DepuradorException("No hay ningun lexema escrito");
        }

        Lexema lexemaEvaluado = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema);

        this.indiceNodo = lexemaEvaluado.getLongitudNodo() - 1;

        if (!this.estaOmitido) {
            this.estaOmitido = true;
        }

        ilustrarEstadosAutomata();

    }

    //Metodo que verifica que la lista tenga al menos una palabra y no solo espacios
    public boolean verificarListados() {

        for (Sentencia sentencia : this.listaSentencias) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getLexema().isBlank()) {
                    return true;
                }

            }

        }

        return false;

    }

    //====================FIN DEL APARTADO DE METODOS QUE PERMITEN IR AVANZANDO ACORDE A LOS PASOS QUE REQUIERA EL DEPURADOR======================
    //METODO MAS IMPORTANTE PARA PODER GENERAR LAS NAVEGACIONES ENTRE ESTADOS DEL AUTOMATA PARA EL DEPURADOR
    public void ilustrarEstadosAutomata() {

        try {

            limpiarAreaAnalisis();

            this.lexemaOperado = new StringBuilder();

            Lexema lexemaEvaluado = this.listaSentencias.get(this.indiceSentencia).getListaLexema(this.indiceLexema);

            Color colorTexto = obtenerColorPorToken(lexemaEvaluado.getEstadoAnalisis());

            insertarEstadoTransicion("Estado inicial: " + "1 " + "con el caracter inicial ", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            int estadoActual = this.indiceNodo;

            if (estadoActual >= lexemaEvaluado.getLongitudNodo()) {
                estadoActual = lexemaEvaluado.getLongitudNodo() - 1;
            }

            if (estadoActual < 0) {
                estadoActual = 0;
            }

            Color colorEstados = new Color(0x0085A6);

            for (int i = 0; i <= this.indiceNodo; i++) {

                Nodo nodoEvaluado = lexemaEvaluado.getValorNodo(i);

                if (!String.valueOf(nodoEvaluado.getCaracter()).isEmpty()) {

                    this.lexemaOperado.append(nodoEvaluado.getCaracter());

                } else {
                    this.lexemaOperado.append("\'  \'");
                }

            }

            String valorTransicionado = String.valueOf(lexemaEvaluado.getValorNodo(estadoActual).getCaracter());

            if (valorTransicionado.isBlank()) {
                valorTransicionado = "\'  \'";
            }

            insertarEstadoTransicion("Caracter Leido: " + valorTransicionado, new Color(0xBA6E02), this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("Estado Actual: " + (estadoActual + 1), colorEstados, this.paneDepuracion);
            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("Transita al estado " + (estadoActual + 2) + " con: " + valorTransicionado, colorEstados, this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("Lexema formado: ", new Color(0x61053B), this.paneDepuracion);

            insertarEstadoTransicion(String.valueOf(this.lexemaOperado.toString()), Color.BLACK, this.paneDepuracion);

            insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

            if (lexemaEvaluado.getLongitudNodo() - 1 == estadoActual) {
                this.yaTerminado = true;
            }

            if (this.yaTerminado) {

                insertarEstadoTransicion("Tipo de Token: ", new Color(0x0A8269), this.paneDepuracion);
                insertarEstadoTransicion(lexemaEvaluado.getEstadoAnalisis().getTipo(), colorTexto, this.paneDepuracion);
                insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

                insertarEstadoTransicion("Guardando token " + lexemaEvaluado.getEstadoAnalisis().getNombreToken() + ". Lexema: ", new Color(0x085717), this.paneDepuracion);

                insertarEstadoTransicion(lexemaEvaluado.getLexema(), colorTexto, this.paneDepuracion);

                this.yaTerminado = false;

                if (this.depuracionTerminada) {

                    insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);
                    insertarEstadoTransicion("\n", Color.BLACK, this.paneDepuracion);

                    insertarEstadoTransicion("DEPURACION FINALIZADA", new Color(0x989E08), this.paneDepuracion);

                }
            }

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

    //Metodo que trabaja en conjunto para poder ir pintando letra a letra
    private void limpiarAreaAnalisis() throws BadLocationException {
        StyledDocument doc = this.paneDepuracion.getStyledDocument();
        doc.remove(0, doc.getLength());

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
