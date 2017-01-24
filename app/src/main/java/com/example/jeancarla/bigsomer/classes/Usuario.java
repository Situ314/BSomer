package com.example.jeancarla.bigsomer.classes;

/**
 * Created by Jean Carla on 14/09/2016.
 */
public class Usuario {

    String id;
    String nombre_usuario;
    String password;
    String nombres;
    String apellido_1;
    String apellido_2;
    String ciudad;
    String ci;
    String telefono;

    public Usuario() {

    }

    public Usuario(String id_usuario, String nombre_usuario, String password, String nombres, String apellido_1, String apellido_2, String ciudad, String ci, String telefono) {
        this.id= id_usuario;
        this.nombre_usuario = nombre_usuario;
        this.password = password;
        this.nombres = nombres;
        this.apellido_1 = apellido_1;
        this.apellido_2 = apellido_2;
        this.ciudad = ciudad;
        this.ci = ci;
        this.telefono = telefono;
    }

    public String getId_usuario() {
        return id;
    }

    public void setId_usuario(String id_usuario) {
        this.id = id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido_1() {
        return apellido_1;
    }

    public void setApellido_1(String apellido_1) {
        this.apellido_1 = apellido_1;
    }

    public String getApellido_2() {
        return apellido_2;
    }

    public void setApellido_2(String apellido_2) {
        this.apellido_2 = apellido_2;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
