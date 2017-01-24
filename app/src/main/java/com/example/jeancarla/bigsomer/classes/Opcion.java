package com.example.jeancarla.bigsomer.classes;

/**
 * Created by Jean Carla on 07/11/2016.
 */
public class Opcion {

    private String ID;
    private String valor;
    private String dependientes;

    public Opcion(String ID, String valor, String dependientes) {
        this.ID = ID;
        this.valor = valor;
        this.dependientes = dependientes;
    }

    public Opcion() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDependientes() {
        return dependientes;
    }

    public void setDependientes(String dependientes) {
        this.dependientes = dependientes;
    }
}
