package com.example.jeancarla.bigsomer.helpers;

/**
 * Created by Jean Carla on 14/09/2016.
 */
public class VariablesURL {

    /**
     * Transición Home -> Detalle
     */
    public static final int CODIGO_DETALLE = 100;

    /**
     * Transición Detalle -> Actualización
     */
    public static final int CODIGO_ACTUALIZACION = 101;
    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta carácteristica.
     */
    private static final String PUERTO_HOST = "/aver2/encuestas/android/avertodo";
    /**
     * Dirección IP de genymotion o AVD
     */
    private static final String IP = "www.bigsomer.com/aver2/encuestas/android/avertodo/";
    /**
     * URLs del Web Service
     */

    public static final String GET = "http://" + IP + PUERTO_HOST + "/obtener_metas.php";
    public static final String GET_LOGIN = "http://" + IP + "login.php?nombre_usuario=";
    public static final String GET_LOGIN2 = "http://" + IP + "login2.php?nombre_usuario=";
    public static final String GET_USER = "http://" + IP + "/descargar_datos_usuario.php?nombre_usuario=";
    public static final String GET_TAREA = "http://" + IP + "descargar_tareas_2.php?id_agent=";
    public static final String GET_PICS = "http://" + IP + "check_pics.php";
    public static final String CHECK = "http://" + IP + "check.php";
    public static final String GET_CLI = "http://" + IP + "descargar_clientes.php";
    public static final String INSERT = "http://" + IP + "guardar_respuestas.php";
    public static final String INSERT_FOTOS = "http://" + IP + "guardar_fotos.php";
    public static final String INSERT_NEGA = "http://" + IP + "guardar_negativa.php";
    public static final String INSERT_VISITA = "http://" + IP + "guardar_visita.php";
    public static final String GET_FORM = "http://" + IP + "descargar_formularios_orden.php?tipo=";
    public static final String UPDATE = "http://" + IP + PUERTO_HOST + "/actualizar_meta.php";
    public static final String DELETE = "http://" + IP + PUERTO_HOST + "/borrar_meta.php";
    public static final String INSERT_LOGIN = "http://" + IP +"guardar_login.php";

    /**
     * Clave para el valor extra que representa al identificador de una meta
     */
    public static final String EXTRA_ID = "IDEXTRA";
}
