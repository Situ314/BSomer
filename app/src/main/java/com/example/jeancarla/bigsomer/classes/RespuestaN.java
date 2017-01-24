package com.example.jeancarla.bigsomer.classes;

/**
 * Created by Jean Carla on 18/11/2016.
 */
public class RespuestaN {

    private String id;
    private String lat;
    private String lon;
    private String fecha_realizada;
    private String tipo_ver;
    private String acceso_cliente;
    private String fotos;
    private String nombre;
    private String ci;
    private String cargo;
    private String comentarios;
    private String estado;

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getFecha_realizada() {
        return fecha_realizada;
    }

    public void setFecha_realizada(String fecha_realizada) {
        this.fecha_realizada = fecha_realizada;
    }

    public String getTipo_ver() {
        return tipo_ver;
    }

    public void setTipo_ver(String tipo_ver) {
        this.tipo_ver = tipo_ver;
    }

    public String getAcceso_cliente() {
        return acceso_cliente;
    }

    public void setAcceso_cliente(String acceso_cliente) {
        this.acceso_cliente = acceso_cliente;
    }

    public String getFotos() {
        return fotos;
    }

    public void setFotos(String fotos) {
        this.fotos = fotos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
