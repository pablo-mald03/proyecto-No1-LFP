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
    private final String ER_COMENTARIO = "^//$";
    private final String ER_COMENTARIO_MULTI_INICIO = "^/*$";
    private final String ER_COMENTARIO_MULTI_FIN = "^*/$";
    
    
    
     //DIRECTORIO PRINCIPAL
    private final String CONFIG_PATH = "configuracion/config.json";

    private ArrayList<String> palabrasReservadas = new ArrayList<>();
    private ArrayList<String> operadores = new ArrayList<>();
    private ArrayList<String> puntuacion = new ArrayList<>();
    private ArrayList<String> agrupacion = new ArrayList<>();

    //PATH CONSTANTE QUE PERMITE REINICIAR POR DEFECTO LA CONFIGURACION SI EN DADO CASO YA HAYA SIDO MODIFICADA
    private final List<String> CONFIG_DEFAULT = new ArrayList<String>() {
        {
            add("{");
            add(" \"palabrasReservadas\": [\"SI\",\"si\",\"ENTONCES\",\"entonces\",\"PARA\",\"para\"],");
            add("");
            add(" \"operadores\": [\"+\",\"-\",\"*\",\"/\",\"%\",\"=\"],");
            add(" \"puntuacion\": [\".\", \",\",\";\",\":\"],");
            add(" \"agrupacion\": [\"(\",\")\",\"[\",\"]\",\"{\",\"}\"],");
            add(" \"comentarios\": {");
            add("   \"linea\": \"//\",");
            add("   \"bloqueInicio\": \"/*\",");
            add("   \"bloqueFin\": \"*/\"");
            add("");
            add("}");
            add("");
            add("");
            add("");
            add("}");
        }
    };

    private Comentarios comments;

    //Lista temporal que permite mostrar en pantalla el config
    private ArrayList<String> listaTemporal = new ArrayList<>(5000);

    public AutomataDeterminista() {
        this.comments = new Comentarios();
    }

    //Verifica que sea paralabra reservada
    public boolean esPalabrasReservadas(String palabra) {
        return palabrasReservadas.contains(palabra);
    }

    //Verifica que sea operador matematico
    public boolean esOperadores(char operador) {
        return operadores.contains(String.valueOf(operador));
    }

    //Verifica que sea signo de puntuacion
    public boolean esPuntuacion(char puntuar) {
        return puntuacion.contains(String.valueOf(puntuar));
    }

    //Verifica que sea simbolo de agrupacion
    public boolean esAgrupacion(char agrupar) {
        return agrupacion.contains(String.valueOf(agrupar));
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

    // Método para inicializar archivo config.json si no existe
    private void initConfig() {
        File archivo = new File(CONFIG_PATH);

        if (!archivo.getParentFile().exists()) {
            archivo.getParentFile().mkdirs();
        }

        if (!archivo.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String linea : CONFIG_DEFAULT) {
                    bw.write(linea);
                    bw.newLine();
                }
            } catch (IOException ex) {
                System.out.println("Esta pendiente");
            }
        }
    }

    //Metodo que se encarga de leer y procesar todo a arrayList
    public void cargarDesdeJson() {

        if (!this.palabrasReservadas.isEmpty()) {
            this.palabrasReservadas.clear();
        }

        if (!this.operadores.isEmpty()) {
            this.operadores.clear();
        }
        if (!this.puntuacion.isEmpty()) {
            this.puntuacion.clear();
        }
        if (!this.agrupacion.isEmpty()) {
            this.agrupacion.clear();
        }

        initConfig();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea.trim());
            }

            String json = sb.toString();

            this.palabrasReservadas = extraerArray(json, "palabrasReservadas");
            this.operadores = extraerArray(json, "operadores");
            this.puntuacion = extraerArray(json, "puntuacion");
            this.agrupacion = extraerArray(json, "agrupacion");

            cargarComentarios(json);

        } catch (IOException ex) {
            
            //PENDIENTE
            
            System.out.println("Pendiente de tratar");
        }

    }

    //Metodo que sirve para extraer del .json toda la configuracion de tokens
    private ArrayList<String> extraerArray(String json, String clave) {
        ArrayList<String> lista = new ArrayList<>();
        int pos = json.indexOf("\"" + clave + "\"");
        if (pos == -1) {
            return lista;
        }

        pos = json.indexOf("[", pos);
        if (pos == -1) {
            return lista;
        }

        int fin = pos + 1;
        boolean dentroComillas = false;
        StringBuilder elemento = new StringBuilder();

        while (fin < json.length()) {
            char c = json.charAt(fin);
            if (c == '"') {
                dentroComillas = !dentroComillas;
            } else if (c == ',' && !dentroComillas) {
                if (elemento.length() > 0) {
                    lista.add(elemento.toString().trim());
                    elemento.setLength(0);
                }
            } else if (c == ']' && !dentroComillas) {
                if (elemento.length() > 0) {
                    lista.add(elemento.toString().trim());
                }
                break;
            } else {
                elemento.append(c);
            }
            fin++;
        }

        return lista;

    }

//Metodo que se encarga de instanciar todos los comentarios PENDIENTE
    private void cargarComentarios(String cargado) {

        String json = cargado;

        int inicio = json.indexOf("\"comentarios\"") + "\"comentarios\"".length();
        inicio = json.indexOf("{", inicio);

        int fin = json.indexOf("}", inicio);

        String comentariosJson = json.substring(inicio + 1, fin).trim();

        String linea = null;
        String bloqueInicio = null;
        String bloqueFin = null;

        for (String parte : comentariosJson.split(",")) {
            String[] keyValue = parte.split(":");
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            switch (key) {
                case "linea" ->
                    linea = value;
                case "bloqueInicio" ->
                    bloqueInicio = value;
                case "bloqueFin" ->
                    bloqueFin = value;
            }
        }

        this.comments.setLinea(linea);
        this.comments.setBloqueInicio(bloqueInicio);
        this.comments.setBloqueFin(bloqueFin);

    }

    //=====================APARTADO DE METODOS QUE SIRVEN PARA PODER MOSTRAR EN LA UI LA CONFIGURACION========================
    //Metodo que permite transformar todo el texto de entrada de config al arreglo 
    public void mostrarConfiguracion(JTextPane paneLog) throws ErrorPuntualException, BadLocationException, IOException {

        if (!this.listaTemporal.isEmpty()) {
            this.listaTemporal.clear();
        }

       initConfig();

        try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                this.listaTemporal.add(linea);

            }
        } catch (IOException ex) {
            throw new ErrorPuntualException("No se ha podido procesar el archivo seleccionado");
        }

        if (this.listaTemporal.isEmpty()) {
            throw new ErrorPuntualException("El archivo esta vacio");
        }
        //METODO UNICO QUE SIRVE PARA MOSTRAR EL CONFIG EN PANTALLA
        pintarLogConfig(paneLog);

    }

    //Permite validar que el archivo tenga el formato correcto
    public boolean comprobarEntradas(String texto) throws ErrorEncontradoException {

        if (!this.listaTemporal.isEmpty()) {
            this.listaTemporal.clear();
        }

        try (BufferedReader bufer = new BufferedReader(new StringReader(texto))) {
            String linea;
            while ((linea = bufer.readLine()) != null) {
                this.listaTemporal.add(linea);
            }
        } catch (IOException ex) {
            throw new ErrorEncontradoException("No se ha podido procesar el texto de entrada");
        }

        return validarConfig(this.listaTemporal);
    }

    //Metodo que sirve para validar que el formato se mantenga integro
    public boolean validarConfig(ArrayList<String> lineas) throws ErrorEncontradoException {
        int llaves = 0, corchetes = 0, comillas = 0;

        boolean tienePalabras = false;
        boolean tieneOperadores = false;
        boolean tienePuntuacion = false;
        boolean tieneAgrupacion = false;
        boolean tieneComentarios = false;

        for (String linea : lineas) {
            // Contar balance de llaves y corchetes
            for (char c : linea.toCharArray()) {
                if (c == '{') {
                    llaves++;
                }
                if (c == '}') {
                    llaves--;
                }
                if (c == '[') {
                    corchetes++;
                }
                if (c == ']') {
                    corchetes--;
                }
                if (c == '"') {
                    comillas++;
                }
            }

            // Validar existencia de bloques obligatorios
            if (linea.contains("\"palabrasReservadas\"")) {
                tienePalabras = true;
            }
            if (linea.contains("\"operadores\"")) {
                tieneOperadores = true;
            }
            if (linea.contains("\"puntuacion\"")) {
                tienePuntuacion = true;
            }
            if (linea.contains("\"agrupacion\"")) {
                tieneAgrupacion = true;
            }
            if (linea.contains("\"comentarios\"")) {
                tieneComentarios = true;
            }
        }

        if (llaves != 0) {
            throw new ErrorEncontradoException("Llaves Sin cierre o apertura");
        }

        if (corchetes != 0) {
            throw new ErrorEncontradoException("Corchetes Sin cierre o apertura");
        }
        if (comillas % 2 != 0) {
            throw new ErrorEncontradoException("Comillas Sin cierre o apertura");
        }

        // Revisar secciones obligatorias
        if (!tienePalabras) {
            throw new ErrorEncontradoException("Falta sección 'palabrasReservadas'\nNo puedes cambiarle nombre");
        }
        if (!tieneOperadores) {
            throw new ErrorEncontradoException("Falta sección 'operadores'\nNo puedes cambiarle nombre");
        }
        if (!tienePuntuacion) {
            throw new ErrorEncontradoException("Falta sección 'puntuacion'\nNo puedes cambiarle nombre");
        }
        if (!tieneAgrupacion) {
            throw new ErrorEncontradoException("Falta sección 'agrupacion'\nNo puedes cambiarle nombre");
        }
        if (!tieneComentarios) {
            throw new ErrorEncontradoException("Falta sección 'comentarios'");
        }

        // Validar que la sección comentarios sea EXACTA
        String bloqueEsperado = "   \"comentarios\": {\n     \"linea\": \"//\",\n     \"bloqueInicio\": \"/*\",\n     \"bloqueFin\": \"*/\"\n   }";

        StringBuilder bloqueLeido = new StringBuilder();
        boolean dentroComentarios = false;
        for (String linea : lineas) {
            if (linea.contains("\"comentarios\"")) {
                dentroComentarios = true;
            }
            if (dentroComentarios) {
                bloqueLeido.append(linea.trim()).append("\n");
                if (linea.contains("}")) {
                    dentroComentarios = false;
                }
            }
        }

        if (!bloqueLeido.toString().contains("\"linea\": \"//\"")
                || !bloqueLeido.toString().contains("\"bloqueInicio\": \"/*\"")
                || !bloqueLeido.toString().contains("\"bloqueFin\": \"*/\"")) {
            throw new ErrorEncontradoException(" NO PUEDES ALTERAR LA SECCION 'comentarios' alterada.");
        }
        return true;
    }

    //Metodo que se encarga de mostrar el archivo de configuracion en pantalla
    public void pintarLogConfig(JTextPane paneAnalisis) throws BadLocationException {

        if (!paneAnalisis.getText().isBlank()) {
            limpiarArea(paneAnalisis);
        }

        for (int i = 0; i < this.listaTemporal.size(); i++) {
            String palabra = this.listaTemporal.get(i);

            boolean dentroComillas = false;

            for (int j = 0; j < palabra.length(); j++) {
                char caracter = palabra.charAt(j);

                // si es comilla, alternamos el estado
                if (caracter == '"') {
                    dentroComillas = !dentroComillas;
                    insertarPalabra(String.valueOf(caracter), Color.BLUE, paneAnalisis); // comillas azules por ejemplo
                    continue;
                }

                if (dentroComillas) {
                    // todo lo que está dentro de comillas, lo pintamos del mismo color
                    insertarPalabra(String.valueOf(caracter), new Color(0x297318), paneAnalisis);
                } else {
                    // fuera de comillas, analizamos el carácter
                    Color color = obtenerColorPorCaracter(caracter);
                    insertarPalabra(String.valueOf(caracter), color, paneAnalisis);
                }
            }

            insertarPalabra("\n", Color.BLACK, paneAnalisis);
        }

        paneAnalisis.setCaretPosition(0);

    }

    // Método que mapea el token a su color
    private Color obtenerColorPorCaracter(char tipo) {
        switch (tipo) {
            case '{':
            case '}':
                return new Color(0xF0760E);
            case '[':
            case ']':
                return new Color(0x6B4627);
            case ':':
                return new Color(0xFF00FF);
            case ',':
                return new Color(0x999999);
            default:
                return new Color(0x9E7A7A);
        }
    }

    //Metodo que trabaja en conjunto para poder ir pintando letra a letra
    private void limpiarArea(JTextPane paneAnalisis) throws BadLocationException {
        StyledDocument doc = paneAnalisis.getStyledDocument();
        doc.remove(0, doc.getLength());

    }

    // Método para insertar texto con un color específico
    private void insertarPalabra(String texto, Color color, JTextPane paneAnalisis) throws BadLocationException {

        StyledDocument doc = paneAnalisis.getStyledDocument();
        // Crear estilo temporal
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color);
        // Inserta al final del documento
        doc.insertString(doc.getLength(), texto, estilo);

    }

    //Metodo que permite reiniciar a los ajustes por defecto el config
    public void reiniciarPredeterminado(JTextPane paneConfig) throws BadLocationException {

        if (!this.listaTemporal.isEmpty()) {
            this.listaTemporal.clear();
        }

        this.palabrasReservadas.clear();
        this.operadores.clear();
        this.puntuacion.clear();
        this.agrupacion.clear();

        for (String reinicio : CONFIG_DEFAULT) {
            this.listaTemporal.add(reinicio);
        }

        pintarLogConfig(paneConfig);

    }

    //METODO UNICO QUE PERMITE SETEAR EL ARCHIVO CONFIG PARA ESCRIBIR EN EL LOS NUEVOS CAMBIOS
    public void guardarCambios() throws ErrorPuntualException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_PATH))) {

            for (String linea : this.listaTemporal) {
                bw.write(linea);
                bw.newLine(); // salto de línea para mantener el formato
            }

        } catch (IOException ex) {
            throw new ErrorPuntualException("No se ha podido guardar la configuración en " + CONFIG_PATH);
        }

    }

    //=====================FIN DEL APARTADO DE METODOS QUE SIRVEN PARA PODER MOSTRAR EN LA UI LA CONFIGURACION====================
    
}
