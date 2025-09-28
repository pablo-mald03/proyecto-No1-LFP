/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexicorecursos;

import com.pablocompany.proyectono1lfp.backend.analizadorlexico.AnalizadorLexico;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.ConfigDatos;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.GestorLexer;
import com.pablocompany.proyectono1lfp.backend.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ConfigException;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
public class LectorEntradas {
    //Atributo que permite almacenar en una estructura dinamica todas las lineas de texto registradas
    //Se le da un preset para evitar mucho consumo de memoria por clonacion

    private ArrayList<String> listaTexto = new ArrayList<>(6000);

    //Clase que intercomunica al lexer actual para poderlo operar internamente en UI
    private GestorLexer lexerActual;

    //------------------Subregion de gramatica extraidas del config.json----------------------------
    private ConfigDatos constantesConfig;

    //private GenerarReportes generacionReportes;
    public LectorEntradas() {
        this.constantesConfig = new ConfigDatos();

        this.constantesConfig.cargarDesdeJson();
        this.lexerActual = new GestorLexer();
        // this.generacionReportes = new GenerarReportes();
    }

    //------------------Fin de la Subregion de gramatica extraidas del config.json----------------------------
    //Metodo que permite transformar todo el texto de entrada al arreglo 
    public void transformarTexto(String texto, JTextPane paneLog) throws AnalizadorLexicoException {

        if (!listaTexto.isEmpty()) {
            listaTexto.clear();
        }

        try (BufferedReader bufer = new BufferedReader(new StringReader(texto))) {
            String linea;
            while ((linea = bufer.readLine()) != null) {
                listaTexto.add(linea);
            }
        } catch (IOException ex) {
            throw new AnalizadorLexicoException("No se ha podido procesar el texto de entrada");
        }
    }

    //Metodo mas importante para poder analizar el texto y procesarlo
    public void analizarEntradas(JTextPane paneLog, JTextPane logErrores, JTextPane logTransiciones) throws BadLocationException, ConfigException {

        //Valida si la lista viene vacia o si la principal cadena de entrada esta vacia
        if (this.listaTexto.isEmpty()) {
            return;
        }

        //Referencia a metodo foreach en streams para poder validar que no se procese algo totalmente vacio
        boolean todoVacio = this.listaTexto.stream().allMatch(String::isBlank);

        if (todoVacio) {
            return;
        }

        AnalizadorLexico automata = new AnalizadorLexico(paneLog, this.listaTexto, logErrores, logTransiciones, this.constantesConfig);
        automata.descomponerLexemas(logErrores);

        this.lexerActual.setLexer(automata);

    }

    //Metodo set que permite referenciar el arreglo extraido hacia el interno de la clase
    public void setLista(ArrayList<String> listaParametro, JTextPane paneLog) {
        this.listaTexto = listaParametro;
    }

    //Metodo que retorna el listado de textos almacenados en el componente
    public ArrayList<String> getListado() {
        return this.listaTexto;
    }

    //Metodo que permite pasar por parametro un arraylist e imprimirlo en un JtextPane
    public void imprimirLog(ArrayList<String> listaExtraida, JTextPane textPane) throws BadLocationException {
        StyledDocument doc = textPane.getStyledDocument();
        for (int i = 0; i < listaExtraida.size(); i++) {
            doc.insertString(doc.getLength(), listaExtraida.get(i) + "\n", null);
        }
    }

    //Metodo que permite retornar el analizador lexico instanciado en el momento
    public AnalizadorLexico getLexerActual() {
        return this.lexerActual.getLexer();
    }

    //Metodo que retorna la referencia de config
    public ConfigDatos getDatosConfig() {
        return this.constantesConfig;
    }

    //METODO UTILIZADO PARA EXPORTAR EL TEXTO ESCRITO EN EL LOG DE EDICION
    public String exportarArchivo() throws ErrorPuntualException {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona el directorio de Almacenamiento");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int resultado = chooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Actualiza tu clase con el path seleccionado

            File seleccionado = chooser.getSelectedFile();
            if (seleccionado.exists()) {

                return seleccionado.getAbsolutePath() + File.separator;

            } else {
                throw new ErrorPuntualException("El directorio seleccionado no existe");

            }

        }

        return null;
    }

    //Metodo utilizado para obtener la instancia y generar el reporte 
    /* public GenerarReportes getGenerarReportes() {
        return this.generacionReportes;
    }*/
}
