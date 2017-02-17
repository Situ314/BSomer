package com.example.jeancarla.bigsomer.classes;
import java.util.Map;

import java.util.HashMap;


public class Tarea {

    private String id_ver;
    private String cliente;
    private String tipo_verificacion;
    private String id_tipo;
    private String solicitante;
    private String nombre;
    private String ci;
    private String direccion;
    private String zona;
    private String nombre_empresa;
    private String referencias;
    private String ub_latitud;
    private String ub_longitud;
    private String medidor;
    private String vip;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getIdtipo() {
        return id_tipo;
    }

    public void setIdtipo(String idtipo) {
        this.id_tipo = idtipo;
    }

    /**

     *
     * @return
     * The idVer
     */
    public String getIdVer() {
        return id_ver;
    }

    /**
     *
     * @param idVer
     * The id_ver
     */
    public void setIdVer(String idVer) {
        this.id_ver = idVer;
    }

    /**
     *
     * @return
     * The cliente
     */
    public String getCliente() {
        return cliente;
    }

    /**
     *
     * @param cliente
     * The cliente
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
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
     * The solicitante
     */
    public String getSolicitante() {
        return solicitante;
    }

    /**
     *
     * @param solicitante
     * The solicitante
     */
    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    /**
     *
     * @return
     * The nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     * The nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     * The ci
     */
    public String getCi() {
        return ci;
    }

    /**
     *
     * @param ci
     * The ci
     */
    public void setCi(String ci) {
        this.ci = ci;
    }

    /**
     *
     * @return
     * The direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     *
     * @param direccion
     * The direccion
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     *
     * @return
     * The zona
     */
    public String getZona() {
        return zona;
    }

    /**
     *
     * @param zona
     * The zona
     */
    public void setZona(String zona) {
        this.zona = zona;
    }

    /**
     *
     * @return
     * The nombreEmpresa
     */
    public String getNombreEmpresa() {
        return nombre_empresa;
    }

    /**
     *
     * @param nombreEmpresa
     * The nombre_empresa
     */
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombre_empresa = nombreEmpresa;
    }

    /**
     *
     * @return
     * The referencias
     */
    public String getReferencias() {
        return referencias;
    }

    /**
     *
     * @param referencias
     * The referencias
     */
    public void setReferencias(String referencias) {
        this.referencias = referencias;
    }

    /**
     *
     * @return
     * The ubLatitud
     */
    public String getUbLatitud() {
        return ub_latitud;
    }

    /**
     *
     * @param ubLatitud
     * The ub_latitud
     */
    public void setUbLatitud(String ubLatitud) {
        this.ub_latitud = ubLatitud;
    }

    /**
     *
     * @return
     * The ubLongitud
     */
    public String getUbLongitud() {
        return ub_longitud;
    }

    /**
     *
     * @param ubLongitud
     * The ub_longitud
     */
    public void setUbLongitud(String ubLongitud) {
        this.ub_longitud = ubLongitud;
    }

    /**
     *
     * @return
     * The medidor
     */
    public String getMedidor() {
        return medidor;
    }

    /**
     *
     * @param medidor
     * The medidor
     */
    public void setMedidor(String medidor) {
        this.medidor = medidor;
    }

    public String getVip() {
        return vip;
    }

    /**
     *
     * @param vip
     * The medidor
     */
    public void setVip(String vip) {
        this.vip = vip;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}