package com.example.jeancarla.bigsomer.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SiTu on 14/09/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String NOMBREBD = "bigsomer_bd.sqlite";
    //Versión de la base de datos
    public static final int VERSION = 1;
    //Nombre de la tabla (puede haber tantas como necesitemos)

    //Campo 1
    public static final String ID = "id";
    //Campo 2 (también puede haber tantos campos como queramos)s

    //Constructor
    public DBHelper(Context context) {super(context, NOMBREBD, null, VERSION);}
    //DBHelper(Context context) {
    //    super(context, "/mnt/sdcard/database_name.db", null, 0);
    //}

    //Aquí crearemos la base de datos

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table usuario_completo(id_usuario varchar primary key, nombre_usuario varchar, password varchar, nombres varchar,  apellido_1 varchar, apellido_2 varchar,ciudad varchar, ci varchar, telefono varchar)");
        db.execSQL("create table tarea(id_ver varchar primary key,tipo_verificacion varchar,id_tipo varchar,solicitante varchar, nombre varchar, ci varchar,direccion varchar,zona varchar,ciudad varchar, nombre_empresa varchar,  cliente  varchar, referencias varchar,ub_latitud varchar,  ub_longitud varchar , medidor varchar, vip varchar, f_asignacion varchar)");
        db.execSQL("create table formulario(id varchar,acceso varchar,tipo_verificacion varchar,pregunta varchar,n_foto_positiva varchar, n_foto_negativa varchar, firma varchar, tipo varchar, opciones varchar, idopciones varchar, dependientes varchar, visible varchar)");
        db.execSQL("create table formulario_respuestas(id_fr varchar primary key,lat varchar,lon varchar,fecha_realizada varchar, tipo_ver varchar, acceso_cliente varchar, fotos varchar, firma varchar, respuestas varchar, estado varchar)");
        db.execSQL("create table formulario_negativas(id_fr varchar primary key,lat varchar,lon varchar,fecha_realizada varchar, tipo_ver varchar, acceso_cliente varchar, fotos varchar, nombre varchar,ci varchar,cargo varchar,comentarios varchar, estado varchar)");
        db.execSQL("create table cliente(id_cli varchar,cliente varchar,acceso varchar primary key)");
        db.execSQL("create table visita(id_sol varchar,lat varchar,lon varchar, comentarios varchar, fecha_realizada varchar, fecha_regreso varchar, estado varchar)");
        db.execSQL("create table f_actualizada(fecha varchar)");
    }

    //Aquí se actualizará la base de datos
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
