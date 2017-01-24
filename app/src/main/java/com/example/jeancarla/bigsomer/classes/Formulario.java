package com.example.jeancarla.bigsomer.classes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean Carla on 21/10/2016.
 */
public class Formulario {

    private String id;
    private String acceso;
    private String tipo_verificacion;
    private String pregunta;
    private String n_foto_positiva;
    private String n_foto_negativa;
    private String firma;
    private String tipo;
    private String opciones;
    private String idopciones;
    private String dependientes;
    private String visible;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getN_foto_positiva() {
        return n_foto_positiva;
    }

    public void setN_foto_positiva(String n_foto_positiva) {
        this.n_foto_positiva = n_foto_positiva;
    }

    public String getN_foto_negativa() {
        return n_foto_negativa;
    }

    public void setN_foto_negativa(String n_foto_negativa) {
        this.n_foto_negativa = n_foto_negativa;
    }

    public String getIdopciones() {
        return idopciones;
    }

    public void setIdopciones(String idopciones) {
        this.idopciones = idopciones;
    }

    /**
     *
     * @return
     * The acceso
     */
    public String getAcceso() {
        return acceso;
    }

    /**
     *
     * @param acceso
     * The acceso
     */
    public void setAcceso(String acceso) {
        this.acceso = acceso;
    }

    /**
     *
     * @return
     * The tipoVerificacion
     */
    public String getTipoVerificacion() {
        return tipo_verificacion;
    }

    /**
     *
     * @param tipoVerificacion
     * The tipo_verificacion
     */
    public void setTipoVerificacion(String tipoVerificacion) {
        this.tipo_verificacion = tipoVerificacion;
    }

    /**
     *
     * @return
     * The pregunta
     */
    public String getPregunta() {
        return pregunta;
    }

    /**
     *
     * @param pregunta
     * The pregunta
     */
    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    /**
     *
     * @return
     * The tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     *
     * @param tipo
     * The tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     *
     * @return
     * The opciones
     */
    public String getOpciones() {
        return opciones;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    /**
     *
     * @param opciones
     * The opciones

     */
    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getDependientes() {
        return dependientes;
    }

    public void setDependientes(String dependientes) {
        this.dependientes = dependientes;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
}


