/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono1lfp.backend.analizadorlexicorecursos;

import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Lexema;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Nodo;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.Sentencia;
import com.pablocompany.proyectono1lfp.backend.analizadorlexico.TokenEnum;
import com.pablocompany.proyectono1lfp.backend.aplicacion.CrearTableros;
import com.pablocompany.proyectono1lfp.backend.aplicacion.ModificarTabla;
import com.pablocompany.proyectono1lfp.backend.excepciones.ErrorPuntualException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author pablo
 */
public class GenerarReportes {

    //------------------APARTADO DE MANEJO DE DIRECTORIOS--------------
    private File guardarArchivo;

    private File directorioArchivo;

    private String pathDefinitivo = "Reportes" + File.separator;

    private final String PATH_PREDETERMINADO = "Reportes" + File.separator;
    //------------------FIN DEL APARTADO DE MANEJO DE DIRECTORIOS--------------

    //------------------APARTADO DE MANEJO DE ATRIBUTOS DEL REPORTE--------------
    //Atributo booleano para saber si hay errores
    //true si hay errores
    private boolean hayErrores;

    //Lista de errores 
    private ArrayList<String> listaErrores = new ArrayList<>(5000);

    //Listado de lexemas
    private ArrayList<String> listadoLexemas = new ArrayList<>(2000);

    //Listado de tokens
    private ArrayList<String> listadoTokens = new ArrayList<>(2000);

    //------------------FIN DEL APARTADO DE MANEJO DE ATRIBUTOS DEL REPORTE--------------
    public GenerarReportes() {

        this.hayErrores = false;
        //setPathPredeterminado();
    }

    //Metodo que retorna si el directorio predeterminado existe 
    //False no existe
    public boolean directorioExiste() {
        return this.directorioArchivo.exists();
    }

    //Metodo util para reestablecer el directorio predeterminado
    public final void setPathPredeterminado() {

        File folder = new File("Reportes");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.guardarArchivo = new File(PATH_PREDETERMINADO);
        this.directorioArchivo = folder;
        this.pathDefinitivo = PATH_PREDETERMINADO;

    }

    //Metodo util para poder mostrar las tokenizaciones de lexemas tokens normales
    public void generarReporteConteoLexemas(ArrayList<Sentencia> sentenciasListado, ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorPuntualException {

        this.hayErrores = false;
        //Cuenta los errores para ver si hay 
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (!lexemaEvaluado.getCadenaError().isBlank()) {
                    this.hayErrores = true;
                    break;
                }

            }

            if (this.hayErrores) {
                break;
            }

        }

        if (this.hayErrores) {
            throw new ErrorPuntualException("No se puede generar el reporte porque hay errores registrados");
        }

        crearTablero.vaciarTablero();

        if (!this.listadoLexemas.isEmpty()) {
            this.listadoLexemas.clear();
        }

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getLexema().isBlank()) {

                    this.listadoLexemas.add(lexemaEvaluado.getEstadoAnalisis().getTipo());
                    this.listadoLexemas.add(lexemaEvaluado.getLexema());

                    String coordenada = "\"F(";

                    coordenada += String.valueOf(lexemaEvaluado.getFilaCoordenada()) + ") , C( ";

                    coordenada += String.valueOf(lexemaEvaluado.getValorNodo(0).getColumna()) + "-" + String.valueOf(lexemaEvaluado.getValorNodo(lexemaEvaluado.getLongitudNodo() - 1).getColumna()) + " )\"";

                    this.listadoLexemas.add(coordenada);

                }

            }

        }

        if (this.listadoLexemas.isEmpty()) {
            throw new ErrorPuntualException("No se ha encontrado ningun lexema ");
        }

        String[] titulos = {"Nombre Token", "Lexema", "Posicion"};
        crearTablero.tableroConTitulo(titulos, this.listadoLexemas.size() / 3, 3, true);
        modificarTabla.reendereizarTablero();

        int iterador = 0;

        for (int i = 0; i < this.listadoLexemas.size(); i += 3) {

            String simbolo = this.listadoLexemas.get(i);
            String lexema = this.listadoLexemas.get(i + 1);
            String posicion = this.listadoLexemas.get(i + 2);

            modificarTabla.colocarTextos(iterador, 0, simbolo);
            modificarTabla.colocarTextos(iterador, 1, lexema);
            modificarTabla.colocarTextos(iterador, 2, posicion);
            iterador++;
        }

    }

    //Metodo util para reiniciar todas las listas al entrar
    public void reiniciarListas() {
        if (!this.listadoLexemas.isEmpty()) {
            this.listadoLexemas.clear();
        }

        if (!this.listaErrores.isEmpty()) {
            this.listaErrores.clear();
        }
        if (!this.listadoTokens.isEmpty()) {
            this.listadoTokens.clear();
        }
    }

    //Metodo util para poder mostrar los conteos de lexemas las veces que aparecen
    public void generarReporteTokenizacionLexemas(ArrayList<Sentencia> sentenciasListado, ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorPuntualException {

        this.hayErrores = false;
        //Cuenta los errores para ver si hay 
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (!lexemaEvaluado.getCadenaError().isBlank()) {
                    this.hayErrores = true;
                    break;
                }

            }

            if (this.hayErrores) {
                break;
            }

        }

        if (this.hayErrores) {
            throw new ErrorPuntualException("No se puede generar el reporte porque hay errores registrados");
        }

        crearTablero.vaciarTablero();

        if (!this.listadoTokens.isEmpty()) {
            this.listadoTokens.clear();
        }

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getLexema().isBlank()) {

                    if (!tokenYaExistente(lexemaEvaluado.getLexema(), lexemaEvaluado.getEstadoAnalisis().getTipo())) {

                        this.listadoTokens.add(lexemaEvaluado.getLexema());
                        this.listadoTokens.add(lexemaEvaluado.getEstadoAnalisis().getTipo());

                        int cantidadVeces = contarLexemas(lexemaEvaluado, sentenciasListado);

                        this.listadoTokens.add(String.valueOf(cantidadVeces));
                    }

                }

            }

        }

        if (this.listadoTokens.isEmpty()) {
            throw new ErrorPuntualException("No se ha encontrado ningun lexema ");
        }

        String[] titulos = {"Lexema", "Tipo de Token", "Cantidad"};
        crearTablero.tableroConTitulo(titulos, this.listadoTokens.size() / 3, 3, true);
        modificarTabla.reendereizarTablero();

        int iterador = 0;

        for (int i = 0; i < this.listadoTokens.size(); i += 3) {

            String lexema = this.listadoTokens.get(i);
            String tipo = this.listadoTokens.get(i + 1);
            String cantidad = this.listadoTokens.get(i + 2);

            modificarTabla.colocarTextos(iterador, 0, lexema);
            modificarTabla.colocarTextos(iterador, 1, tipo);
            modificarTabla.colocarTextos(iterador, 2, cantidad);
            iterador++;
        }

    }

    //Metodo que valida que el pese al lexema que se encuentre sea de diferente tipo de token 
    private boolean tokenYaExistente(String palabra, String token) {

        for (int i = 0; i < this.listadoTokens.size(); i += 3) {

            String lexema = this.listadoTokens.get(i);
            String tipo = this.listadoTokens.get(i + 1);

            if (palabra.equals(lexema) && token.equals(tipo)) {
                return true;
            }

        }

        return false;
    }

    //Metodo que ayuda a contar la cantidad de veces que aparecen los lexemas en el texto
    private int contarLexemas(Lexema lexemaUbicado, ArrayList<Sentencia> sentenciasListado) {

        int contadorVeces = 0;
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaComparado = sentencia.getListaLexema(i);

                if (!lexemaComparado.getLexema().isBlank()) {

                    if (lexemaUbicado.getLexema().equals(lexemaComparado.getLexema()) && lexemaUbicado.getEstadoAnalisis() == lexemaComparado.getEstadoAnalisis()) {
                        contadorVeces++;
                    }

                }

            }

        }

        return contadorVeces;
    }

    //Metodo util para poder mostrar los errores en pantalla en la tabla 
    public void generarReporteGeneral(ArrayList<Sentencia> sentenciasListado, JLabel labelCantidadError, JLabel labelPorcentaje, JTextArea cantidadTokensArea) throws ErrorPuntualException {

        //Solo se encarga de actualizar el estado de errores y de paso lo cuenta
        int cantidadErrores = 0;
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (!lexemaEvaluado.getCadenaError().isBlank()) {
                    if (!this.hayErrores) {
                        this.hayErrores = true;
                    }

                    cantidadErrores++;
                }

            }

        }

        labelCantidadError.setText("Cantidad de Errores: " + cantidadErrores);

        ArrayList<String> listaTokens = new ArrayList<>();
        listaTokens.add(TokenEnum.IDENTIFICADOR.getTipo());
        listaTokens.add(TokenEnum.NUMERO.getTipo());
        listaTokens.add(TokenEnum.DECIMAL.getTipo());
        listaTokens.add(TokenEnum.CADENA.getTipo());
        listaTokens.add(TokenEnum.PALABRA_RESERVADA.getTipo());
        listaTokens.add(TokenEnum.PUNTUACION.getTipo());
        listaTokens.add(TokenEnum.OPERADOR.getTipo());
        listaTokens.add(TokenEnum.AGRUPACION.getTipo());
        listaTokens.add(TokenEnum.COMENTARIO_LINEA.getTipo());
        listaTokens.add(TokenEnum.COMENTARIO_BLOQUE.getTipo());
        listaTokens.add(TokenEnum.ERROR.getTipo());

        //Cuenta todos los lexemas escritos
        int lexemasEncontrados = 0;

        //Cuenta todos los errores encontrados
        int erroresEncontrados = 0;

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                lexemasEncontrados++;

                if (!lexemaEvaluado.getCadenaError().isBlank()) {

                    erroresEncontrados++;
                }

                for (Nodo nodoRecorrido : lexemaEvaluado.obtenerListaNodo()) {

                    if (listaTokens.contains(nodoRecorrido.getToken().getTipo())) {

                        listaTokens.remove(nodoRecorrido.getToken().getTipo());

                    }

                }

            }

        }

        int totalValidos = lexemasEncontrados - erroresEncontrados;
        double porcentajeCalificacion = (totalValidos * 100.0) / lexemasEncontrados;

        labelPorcentaje.setText("Porcentaje Tokens Validos: " + Math.floor(porcentajeCalificacion) + "%");

        cantidadTokensArea.setText("");

        if (listaTokens.isEmpty()) {
            cantidadTokensArea.setText("Tokens no utilizados: SE HAN UTILIZADO TODOS LOS TOKENS");
        } else {

            cantidadTokensArea.setText("Tokens no utilizados: ");

            for (String listaToken : listaTokens) {
                cantidadTokensArea.setText(cantidadTokensArea.getText() + " | " + listaToken);

            }

        }

    }

    //Metodo util para poder mostrar los errores en pantalla en la tabla 
    public void generarReporteErrores(ArrayList<Sentencia> sentenciasListado, ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorPuntualException {

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (!lexemaEvaluado.getCadenaError().isBlank()) {
                    this.hayErrores = true;
                    break;
                }

            }

            if (this.hayErrores) {
                break;
            }

        }

        if (!this.hayErrores) {
            throw new ErrorPuntualException("No hay ningun error registrado en el analisis");
        }

        if (!this.listaErrores.isEmpty()) {
            this.listaErrores.clear();
        }

        crearTablero.vaciarTablero();

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getCadenaError().isBlank()) {

                    this.listaErrores.add(lexemaEvaluado.getCadenaError());

                    String coordenada = "\"(";
                    coordenada += String.valueOf(lexemaEvaluado.getFilaCoordenada()) + " , ";

                    int columnaTope = 0;

                    for (Nodo nodoRecorrido : lexemaEvaluado.obtenerListaNodo()) {

                        if (nodoRecorrido.getToken() != TokenEnum.ERROR) {
                            break;
                        }

                        columnaTope = nodoRecorrido.getColumna();

                    }

                    coordenada += String.valueOf(columnaTope) + ")\"";

                    this.listaErrores.add(coordenada);
                    
                    this.listaErrores.add(lexemaEvaluado.getCadenaEsperada());

                }

            }

        }

        if (this.listaErrores.isEmpty()) {
            throw new ErrorPuntualException("No se han encontrado errores");
        }

        String[] titulos = {"Cadena de Error", "Posicion","Descripcion"};
        crearTablero.tableroConTitulo(titulos, this.listaErrores.size() / 3, 3, true);
        modificarTabla.reendereizarTablero();

        int iterador = 0;

        for (int i = 0; i < this.listaErrores.size(); i += 3) {

            String simbolo = this.listaErrores.get(i);
            String posicion = this.listaErrores.get(i + 1);
            String descripcion = this.listaErrores.get(i + 2);

            modificarTabla.colocarTextos(iterador, 0, simbolo);
            modificarTabla.colocarTextos(iterador, 1, posicion);
            modificarTabla.colocarTextos(iterador, 2, descripcion);
            iterador++;
        }

    }

    //Metodo que permite comunicar a la UI con la interaccion para generar reporte de errores
    public void generarReporteErrores() throws ErrorPuntualException {
        reportarErroresCSV(this.listaErrores, "ReporteErrores", "Cadena_Error,Posicion");
    }

    //Metodo que permite comunicar a la UI con la interaccion para generar reporte de errores
    public void generarReporteSinErroresTokens() throws ErrorPuntualException {
        if (this.listadoTokens.isEmpty()) {
            throw new ErrorPuntualException("No hay reporte de Cantidad de Tokens cargado aun\nGenere primero el reporte para poder exportarlo");
        }
        reportarSinErroresCSV(this.listadoTokens, "ReporteCantidadLexemas", "Lexema,Tipo_Token,Cantidad", "Cantidad de Tokens");
    }

    //Metodo que permite comunicar a la UI con la interaccion para generar reporte de errores
    public void generarReporteSinErroresLexemas() throws ErrorPuntualException {
        if (this.listadoLexemas.isEmpty()) {
            throw new ErrorPuntualException("No hay reporte de Lexemas cargado aun\nGenere primero el reporte para poder exportarlo");
        }

        reportarSinErroresCSV(this.listadoLexemas, "ReporteLexemas", "Nombre_Token,Lexema,Posicion", "Lexemas");
    }

    //Metodo que permite exportar .csv de los errores
    public void reportarErroresCSV(ArrayList<String> lista, String nombreArchivo, String headersArchivo) throws ErrorPuntualException {

        if (this.listaErrores.isEmpty()) {
            throw new ErrorPuntualException("No hay reporte de errores cargado aun\nGenere primero el reporte para poder exportarlo");
        }

        if (!this.hayErrores) {
            throw new ErrorPuntualException("No hay ningun error registrado en el analisis");
        }

        if (!directorioExiste()) {
            setPathPredeterminado();
        }

        //Se genera la hora de exportacion para evitar duplicados
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fechaHora = ahora.format(formatter);

        try (FileWriter writer = new FileWriter(this.pathDefinitivo + nombreArchivo + "_" + fechaHora + ".csv")) {
            writer.append(headersArchivo + "\n");

            for (int i = 0; i < lista.size(); i += 2) {
                String campo1 = lista.get(i);
                String campo2 = lista.get(i + 1);

                writer.append(campo1).append(",")
                        .append(campo2).append(",")
                        .append("\n");

            }

        } catch (IOException e) {
            throw new ErrorPuntualException("No se ha podido exportar el reporte" + e.getMessage());
        }
    }

    //Metodo que permite exportar .csv de los errores
    public void reportarSinErroresCSV(ArrayList<String> lista, String nombreArchivo, String headersArchivo, String nombramiento) throws ErrorPuntualException {

        if (this.hayErrores) {
            throw new ErrorPuntualException("Hay errores registrados en el analisis\nNo puedes exportar el reporte");
        }

        if (!directorioExiste()) {
            setPathPredeterminado();
        }

        //Se genera la hora de exportacion para evitar duplicados
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fechaHora = ahora.format(formatter);

        try (FileWriter writer = new FileWriter(this.pathDefinitivo + nombreArchivo + "_" + fechaHora + ".csv")) {
            writer.append(headersArchivo + "\n");

            for (int i = 0; i < lista.size(); i += 3) {
                String campo1 = lista.get(i);
                String campo2 = lista.get(i + 1);
                String campo3 = lista.get(i + 2);

                writer.append(campo1).append(",")
                        .append(campo2).append(",")
                        .append(campo3).append("\n");

            }

        } catch (IOException e) {
            throw new ErrorPuntualException("No se ha podido exportar el reporte" + e.getMessage());
        }
    }

}
