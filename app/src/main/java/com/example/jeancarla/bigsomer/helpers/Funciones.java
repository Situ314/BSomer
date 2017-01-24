package com.example.jeancarla.bigsomer.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.activities.Formulario_Historial;
import com.example.jeancarla.bigsomer.classes.Formulario;
import com.example.jeancarla.bigsomer.classes.Opcion;
import com.example.jeancarla.bigsomer.classes.Respuesta;
import com.example.jeancarla.bigsomer.classes.RespuestaN;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.example.jeancarla.bigsomer.classes.Usuario;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Jean Carla on 20/09/2016.
 */
public class Funciones {

    public String Retornar_usuario(Context context, String usuario, String contrasenhia) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] user = new String[]{usuario, contrasenhia};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT id_usuario FROM bisa_usuario WHERE nombre_usuario=? AND password=?", user);
        if (fila.moveToFirst()) {
            do {
                mostrar = "" + fila.getInt(0);

            } while (fila.moveToNext());


            bd.close();
            return mostrar;
        } else {
            bd.close();

            return "no";
        }


    }

    public String consulta_hay_usuarios(Context context, String nulo) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        //id_usuario, nombre_usuario varchar, password varchar, nombres varchar,  apellido_1 varchar,
        // apellido_2 varchar,ciudad varchar, ci varchar, telefono varchar

        String[] user = new String[]{nulo};
        Cursor fila = bd.rawQuery("SELECT * FROM bisa_usuario WHERE id_usuario !=?", user);
        if (fila.moveToFirst()) {

            bd.close();
            return "si";
        } else {
            bd.close();

            return "no";
        }

    }

    public List<String> DomsinFoto(Context context, String usuario) {
        List<String> domList = new ArrayList<String>();
        // Select All Query

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] user = new String[]{usuario};
        //id_usuario, nombre_usuario varchar, password varchar, nombres varchar,  apellido_1 varchar,
        // apellido_2 varchar,ciudad varchar, ci varchar, telefono varchar
        Cursor cursor = bd.rawQuery("SELECT * FROM usuario_completo", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                domList.add(cursor.getString(0));
                domList.add(cursor.getString(1));
                domList.add(cursor.getString(2));
                domList.add(cursor.getString(3));
                domList.add(cursor.getString(4));
                domList.add(cursor.getString(5));
                domList.add(cursor.getString(6));
                domList.add(cursor.getString(7));
                domList.add(cursor.getString(8));

            } while (cursor.moveToNext());
        }// return contact list
        return domList;
    }


    public String consulta_id_usuario(Context context, String nulo) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] user = new String[]{nulo};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT id_usuario FROM usuario_completo WHERE id_usuario !=?", user);
        if (fila.moveToFirst()) {
            do {
                mostrar = "" + fila.getString(0);

            } while (fila.moveToNext());


            return mostrar;
        } else {
            bd.close();

            return "no";
        }

    }


    public int hay_tareas(Context context) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] cl = new String[]{"0"};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * from tarea", null);
        if (fila.moveToFirst()) {
            do {
                mostrar = fila.getInt(0);
                contador = contador + 1;

            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }


    public int hay_enviadas(Context context) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] cl = new String[]{"0"};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * from formulario_respuestas", null);
        if (fila.moveToFirst()) {
            do {
                mostrar = fila.getInt(0);
                contador = contador + 1;

            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }

    public String nro_fotosn(Context context, String acceso_cliente, String tipo_ver) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String contador="";


        String[] user = new String[]{acceso_cliente, tipo_ver};
        Cursor fila = bd.rawQuery("SELECT n_foto_negativa FROM formulario WHERE acceso=? AND tipo_verificacion=?", user);
        if (fila.moveToFirst()) {
            do {
                contador = fila.getString(0);
            } while (fila.moveToNext());

            bd.close();
        } else {
            Log.e("ERROR FETCHING ", "oooooops");
            bd.close();
        }
        return contador;
    }

    public int hay_tareas_cliente(Context context, String cliente) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] cl = new String[]{cliente};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * FROM tarea WHERE cliente=? AND id_ver NOT IN (SELECT id_fr FROM formulario_respuestas) " +
                "AND id_ver NOT IN (SELECT id_fr FROM formulario_negativas)", cl);
        if (fila.moveToFirst()) {
            do {

                mostrar = fila.getInt(0);
                contador = contador + 1;

            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }

    public String get_cliente(Context context, String cliente) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] cl = new String[]{cliente};
        String nombre="";
        Cursor fila = bd.rawQuery("SELECT cliente FROM cliente WHERE acceso =?", cl);
        if (fila.moveToFirst()) {
            do {
               nombre = fila.getString(0);
            } while (fila.moveToNext());
        }
        bd.close();
        return nombre;
    }

    public String get_nrofotos_posi(Context context, String cliente, String tipo) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] cl = new String[]{cliente,tipo};
        String nombre="";
        Cursor fila = bd.rawQuery("SELECT n_foto_positiva FROM formulario WHERE acceso =? AND tipo_verificacion =?", cl);
        if (fila.moveToFirst()) {
            do {
                nombre = fila.getString(0);
            } while (fila.moveToNext());
        }
        bd.close();
        return nombre;
    }


    public int hay_historial(Context context, String cliente) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] cl = new String[]{cliente};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * FROM formulario_respuestas WHERE acceso_cliente=?", cl);
        if (fila.moveToFirst()) {
            do {

                mostrar = fila.getInt(0);
                contador = contador + 1;

            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }

    public int hay_historial_negativa(Context context, String cliente) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] cl = new String[]{cliente};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * FROM formulario_negativas WHERE acceso_cliente=?", cl);
        if (fila.moveToFirst()) {
            do {

                mostrar = fila.getInt(0);
                contador = contador + 1;

            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }

    public int hay_tareas_pendientes(Context context) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] cl = new String[]{"pendiente"};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * from formulario_respuestas where estado=?", cl);
        if (fila.moveToFirst()) {
            do {
                mostrar = fila.getInt(0);
                contador = contador + 1;
            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }

    public int hay_negativas_pendientes(Context context) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] cl = new String[]{"pendiente"};
        int mostrar = 0;
        int contador = 0;

        Cursor fila = bd.rawQuery("SELECT * from formulario_negativas where estado=?", cl);
        if (fila.moveToFirst()) {
            do {
                mostrar = fila.getInt(0);
                contador = contador + 1;
            } while (fila.moveToNext());
        }
        bd.close();
        return contador;
    }

    public String hay_usuarios(Context context) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String mostrar="nada";
        Cursor fila = bd.rawQuery("SELECT nombre_usuario,password from usuario_completo", null);
        if (fila.moveToFirst()) {
            do {
                mostrar = fila.getString(0)+"-"+fila.getString(1);
            } while (fila.moveToNext());
        }
        bd.close();
        return mostrar;
    }


    public Formulario getFormulario(String id, List<Formulario> lst){

        Formulario f=null;
        for(int i = 0; i < lst.size(); i++){
            if(lst.get(i).getId().equals(id))
                f = lst.get(i);
        }
        return f;
    }

    public String get_nombre_direccion(Context context,String id) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] cl = new String[]{id};
        String devolver="";

        Cursor fila = bd.rawQuery("SELECT nombre,direccion from tarea where id_ver=?", cl);
        if (fila.moveToFirst()) {
            do {
                devolver = fila.getString(0)+"!!"+fila.getString(1);
            } while (fila.moveToNext());
        }
        bd.close();
        return devolver;
    }

    public Usuario get_usuario(Context context) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] cl = new String[]{""};
        int mostrar = 0;
        int contador = 0;
        Usuario u=new Usuario();
        Cursor fila = bd.rawQuery("SELECT * from usuario_completo", null);
        if (fila.moveToFirst()) {
            do {

                u.setId_usuario(fila.getString(0));
                u.setNombre_usuario(fila.getString(1));
                u.setPassword(fila.getString(2));
                u.setNombres(fila.getString(3));
                u.setApellido_1(fila.getString(4));
                u.setApellido_2(fila.getString(5));
                u.setCiudad(fila.getString(6));
                u.setCi(fila.getString(7));
                u.setTelefono(fila.getString(8));

            } while (fila.moveToNext());
        }
        bd.close();
        return u;
    }

    public void eliminar_tarea(Context context, String cliente) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String[] cl = new String[]{cliente};
        bd.delete("tarea", "id_ver=?", cl);
        bd.close();
    }

    public Double Convertir_Datos(String cadena) {
        double aDouble = Double.parseDouble(cadena);
        System.out.println(aDouble);
        return aDouble;
    }

    public List<Tarea> llenar_tarea(Context context, String cliente) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        List<Tarea> tareas = new ArrayList<Tarea>();
        Tarea[] tarea = new Tarea[100];
        String[] user = new String[]{cliente};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT * FROM tarea WHERE cliente =? AND id_ver NOT IN (SELECT id_fr FROM formulario_respuestas) AND id_ver NOT IN (SELECT id_fr FROM formulario_negativas)", user);
        if (fila.moveToFirst()) {
            do {
                tarea[i] = new Tarea();
                tarea[i].setIdVer(fila.getString(0));
                tarea[i].setTipoVerificacion(fila.getString(1));
                tarea[i].setIdtipo(fila.getString(2));
                tarea[i].setSolicitante(fila.getString(3));
                tarea[i].setNombre(fila.getString(4));
                tarea[i].setCi(fila.getString(5));
                tarea[i].setDireccion(fila.getString(6));
                tarea[i].setZona(fila.getString(7));
                tarea[i].setNombreEmpresa(fila.getString(9));
                tarea[i].setCliente(fila.getString(10));
                tarea[i].setReferencias(fila.getString(11));
                tarea[i].setUbLatitud(fila.getString(12));
                tarea[i].setUbLongitud(fila.getString(13));
                tarea[i].setMedidor(fila.getString(14));

                tareas.add(tarea[i]);
                i++;

            } while (fila.moveToNext());

            return tareas;
        } else {
            bd.close();

            return null;
        }

    }

    public List<Respuesta> llenar_historial(Context context, String cliente) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        List<Respuesta> tareas = new ArrayList<Respuesta>();
        Respuesta[] tarea = new Respuesta[100];
        String[] user = new String[]{cliente};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT * FROM formulario_respuestas WHERE acceso_cliente =?", user);
        if (fila.moveToFirst()) {
            do {

                tarea[i] = new Respuesta();
                tarea[i].setId(fila.getString(0));
                tarea[i].setLat(fila.getString(1));
                tarea[i].setLon(fila.getString(2));
                tarea[i].setFecha_realizada(fila.getString(3));
                tarea[i].setTipo_ver(fila.getString(4));
                tarea[i].setAcceso_cliente(fila.getString(5));
                tarea[i].setFotos(fila.getString(6));
                tarea[i].setRespuestas(fila.getString(7));
                tarea[i].setEstado(fila.getString(8));

                tareas.add(tarea[i]);
                i++;

            } while (fila.moveToNext());

            return tareas;
        } else {
            bd.close();

            return null;
        }

    }

    public List<Respuesta> get_todo_respuestas(Context context) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();
        int i = 0;
        List<Respuesta> tareas = new ArrayList<Respuesta>();
        Respuesta[] tarea = new Respuesta[100];
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT * FROM formulario_respuestas", null);
        if (fila.moveToFirst()) {
            do {

                tarea[i] = new Respuesta();
                tarea[i].setId(fila.getString(0));
                tarea[i].setLat(fila.getString(1));
                tarea[i].setLon(fila.getString(2));
                tarea[i].setFecha_realizada(fila.getString(3));
                tarea[i].setTipo_ver(fila.getString(4));
                tarea[i].setAcceso_cliente(fila.getString(5));
                tarea[i].setFotos(fila.getString(6));
                tarea[i].setRespuestas(fila.getString(7));
                tarea[i].setEstado(fila.getString(8));

                tareas.add(tarea[i]);
                i++;

            } while (fila.moveToNext());

            return tareas;
        } else {
            bd.close();

            return null;
        }

    }

    public List<Respuesta> get_xfecha(Context context, Date fecha) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();
        int i = 0;
        List<Respuesta> tareas = new ArrayList<Respuesta>();
        Respuesta[] tarea = new Respuesta[100];
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT * FROM formulario_respuestas", null);
        if (fila.moveToFirst()) {
            do {

                String []comparar = fila.getString(3).split(" ");
                String fecha_verif = comparar[0];
                Log.e("SITU FECHA: ",fecha_verif);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                try {
                   // Date date_consulta = sdf.parse(fecha);
                    Date date_verif = sdf.parse(fecha_verif);

                    if (date_verif.after(fecha) || date_verif.equals(fecha)){
                        tarea[i] = new Respuesta();
                        tarea[i].setId(fila.getString(0));
                        tarea[i].setLat(fila.getString(1));
                        tarea[i].setLon(fila.getString(2));
                        tarea[i].setFecha_realizada(fila.getString(3));
                        tarea[i].setTipo_ver(fila.getString(4));
                        tarea[i].setAcceso_cliente(fila.getString(5));
                        tarea[i].setFotos(fila.getString(6));
                        tarea[i].setRespuestas(fila.getString(7));
                        tarea[i].setEstado(fila.getString(8));

                        tareas.add(tarea[i]);
                        i++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } while (fila.moveToNext());

            return tareas;
        } else {
            bd.close();

            return tareas;
        }

    }

    public List<Respuesta> get_xId(Context context, String Id) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();
        int i = 0;
        List<Respuesta> tareas = new ArrayList<Respuesta>();
        Respuesta[] tarea = new Respuesta[100];
        String[] user = new String[]{Id};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT * FROM formulario_respuestas WHERE id_fr =?", user);
        if (fila.moveToFirst()) {
            do {
                        tarea[i] = new Respuesta();
                        tarea[i].setId(fila.getString(0));
                        tarea[i].setLat(fila.getString(1));
                        tarea[i].setLon(fila.getString(2));
                        tarea[i].setFecha_realizada(fila.getString(3));
                        tarea[i].setTipo_ver(fila.getString(4));
                        tarea[i].setAcceso_cliente(fila.getString(5));
                        tarea[i].setFotos(fila.getString(6));
                        tarea[i].setRespuestas(fila.getString(7));
                        tarea[i].setEstado(fila.getString(8));

                        tareas.add(tarea[i]);
                        i++;
            } while (fila.moveToNext());

            return tareas;
        } else {
            bd.close();

            return tareas;
        }

    }

    public Opcion get_nombre(List<Opcion> lst_op, String value){
        Opcion nombre=new Opcion();
        for (Opcion o:lst_op){
            if(o.getID().equals(value))
                nombre = o;
        }

        return nombre;
    }

    public ArrayList<String> get_respuestas(Context context, String verif) throws JSONException {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        ArrayList<String> list = new ArrayList<String>();
        RespuestaN[] tarea = new RespuestaN[100];
        String[] user = new String[]{verif};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT respuestas FROM formulario_respuestas WHERE id_fr =?", user);
        if (fila.moveToFirst()) {
            do {
                JSONArray jsonArray = new JSONArray(fila.getString(0));
                Log.e("SITU JSON: ", ""+jsonArray.length()+" "+jsonArray.toString());
                list = new ArrayList<String>();
             //   JSONArray json = (JSONArray)jsonArray;
                if (jsonArray != null) {
                    int len = jsonArray.length();
                    for (int j=0;j<len;j++){
                        if(jsonArray.get(i) == null)
                            list.add(" ");
                        else
                        list.add(jsonArray.get(j).toString());
                    }
                }

            } while (fila.moveToNext());

            return list;
        } else {
            bd.close();

            return null;
        }

    }

    public ArrayList<String> get_respuesta_negativas(Context context, String verif) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        ArrayList<String> list = new ArrayList<String>();
        RespuestaN[] tarea = new RespuestaN[100];
        String[] user = new String[]{verif};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT respuestas FROM formulario_negativas WHERE id_fr =?", user);
        if (fila.moveToFirst()) {
            do {


            } while (fila.moveToNext());

            return list;
        } else {
            bd.close();

            return null;
        }

    }

    public String get_fotos(Context context, String verif) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
       String fotos="";
        String[] user = new String[]{verif};
        Cursor fila = bd.rawQuery("SELECT fotos FROM formulario_respuestas WHERE id_fr =?", user);
        if (fila.moveToFirst()) {
            do {
                   fotos = fila.getString(0);

            } while (fila.moveToNext());

            return fotos;
        } else {
            bd.close();

            return null;
        }

    }

    public String get_visible(Context context, String verif) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        String visible="";
        String[] user = new String[]{verif};
        Cursor fila = bd.rawQuery("SELECT visible FROM formulario WHERE id =?", user);
        if (fila.moveToFirst()) {
            do {
                visible = fila.getString(0);

            } while (fila.moveToNext());

            return visible;
        } else {
            bd.close();

            return null;
        }

    }

    public boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            Log.e("app",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean check_connection(Context context){

        boolean check = false;
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.test);

        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "drawable");

        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }

        if (doSave) {
            saveBitmapToFile(dir,"test.png",bm,Bitmap.CompressFormat.PNG,100);
        }
        else {
            Log.e("app","Couldn't create target directory.");
        }

         StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //ENVIAR DATOS A LA NUBE
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(VariablesURL.CHECK);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        //SUBIR LAS FOTOS
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "drawable"+File.separator+"test.png");
                entityBuilder.addBinaryBody("fotoUp", file);

                HttpEntity entity = entityBuilder.build();
                httppost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            java.lang.String respuestaWeb = EntityUtils.toString(response
                    .getEntity());
            System.out.println("La web me muestra+ ////////////  " + respuestaWeb);

            Log.e("SITU RESPUESTA IMG ",respuestaWeb);
            if(!respuestaWeb.equals("0"))
                check = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
            httpclient.getConnectionManager().shutdown();
            //resultado2 = "ok";
        return check;
    }

    public String get_fotos_negativas(Context context, String verif) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        String fotos="";
        String[] user = new String[]{verif};
        Cursor fila = bd.rawQuery("SELECT fotos FROM formulario_negativas WHERE id_fr =?", user);
        if (fila.moveToFirst()) {
            do {
                fotos = fila.getString(0);

            } while (fila.moveToNext());

            return fotos;
        } else {
            bd.close();

            return null;
        }

    }

    public List<RespuestaN> llenar_historial_n(Context context, String cliente) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        List<RespuestaN> tareas = new ArrayList<RespuestaN>();
        RespuestaN[] tarea = new RespuestaN[100];
        String[] user = new String[]{cliente};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT * FROM formulario_negativas WHERE acceso_cliente =?", user);
        if (fila.moveToFirst()) {
            do {

                tarea[i] = new RespuestaN();
                tarea[i].setId(fila.getString(0));
                tarea[i].setLat(fila.getString(1));
                tarea[i].setLon(fila.getString(2));
                tarea[i].setFecha_realizada(fila.getString(3));
                tarea[i].setTipo_ver(fila.getString(4));
                tarea[i].setAcceso_cliente(fila.getString(5));
                tarea[i].setFotos(fila.getString(6));
                tarea[i].setNombre(fila.getString(7));
                tarea[i].setCi(fila.getString(8));
                tarea[i].setCargo(fila.getString(9));
                tarea[i].setComentarios(fila.getString(10));
                tarea[i].setEstado(fila.getString(11));

                tareas.add(tarea[i]);
                i++;

            } while (fila.moveToNext());

            return tareas;
        } else {
            bd.close();

            return null;
        }

    }

    public List<String> get_tipo_cliente(Context context) {

        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        List<String> tareas = new ArrayList<String>();
        Tarea[] tarea = new Tarea[100];
        String[] user = new String[]{"0"};
        String mostrar = "";
        Cursor fila = bd.rawQuery("SELECT id_tipo, cliente FROM tarea", null);
        if (fila.moveToFirst()) {
            do {
                mostrar = "";
                mostrar = fila.getString(1) + "-" + fila.getString(0);
                if (!tareas.contains(mostrar)) {
                    String m = mostrar;
                    String[] d = m.split("-");
                    String cl = d[0];
                    String it = d[1];
                    String[] check = new String[]{cl, it};
                    Cursor fila2 = bd.rawQuery("Select * FROM formulario WHERE acceso=? AND tipo_verificacion=?", check);
                    if (!fila2.moveToFirst()) {
                        tareas.add(mostrar);
                    }
                }
            } while (fila.moveToNext());

            Log.e("MOOOOOOOOO", tareas.toString());
            bd.close();
            return tareas;
        } else {
            bd.close();

            return null;
        }

    }

    String id_ciudad, id_depto, nombre_ciudad;

    public String insertar_ciudades(Context context, String cadena) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        StringTokenizer divididor = new StringTokenizer(cadena, ":");
        int contador = divididor.countTokens();
        final String[] vector = cadena.split(":");
        for (int i = 0; i < contador; i++) {
            id_ciudad = vector[0];
            // id_depto=vector[1];
            nombre_ciudad = vector[1];
        }

        ContentValues values1 = new ContentValues();
        values1 = new ContentValues();
        values1.put("id_ciudad", id_ciudad);
        //values1.put("id_depto",id_depto);
        values1.put("nombre_ciudad", nombre_ciudad);
        bd.insert("departamento_ciudad", null, values1);
        bd.close();


        //return  id_ciudad+"///"+id_depto+"///"+nombre_ciudad;
        return nombre_ciudad;
    }


    String id_agencia, nombre_agencia, id_ciudad1;

    public String insertar_agencias(Context context, String cadena) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();

        StringTokenizer divididor = new StringTokenizer(cadena, "@");
        int contador = divididor.countTokens();
        final String[] vector = cadena.split("@");
        for (int i = 0; i < contador; i++) {
            id_agencia = vector[0];
            nombre_agencia = vector[1];
            id_ciudad1 = vector[2];
        }

        ContentValues values1 = new ContentValues();
        values1 = new ContentValues();
        values1.put("id_agencia", id_agencia);
        values1.put("nombre_agencia", nombre_agencia);
        values1.put("id_ciudad", id_ciudad1);
        bd.insert("bisa_agencia", null, values1);
        bd.close();
        return id_agencia + "////" + nombre_agencia + "////" + id_ciudad;

    }


    public String retornar_ciudad(Context context, String cadena) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] user = new String[]{cadena};
        String mostrar = "";

        Cursor fila = bd.rawQuery("SELECT ciudad from ciudad where ciudad=?", user);
        if (fila.moveToFirst()) {
            do {
                mostrar = "" + fila.getString(0);

            } while (fila.moveToNext());
        }
        return mostrar;

    }


    public String retornar_id_ciudad(Context context, String cadena) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] user = new String[]{cadena};
        String mostrar = "";

        Cursor fila = bd.rawQuery("SELECT id_ciudad from ciudad where ciudad=?", user);
        if (fila.moveToFirst()) {
            do {
                mostrar = "" + fila.getString(0);

            } while (fila.moveToNext());
        }
        return mostrar;

    }


    public String retornar_cadena_agencias(Context context, String cadena) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] user = new String[]{cadena};
        String mostrar = "";

        Cursor fila = bd.rawQuery("SELECT nombre_agencia from bisa_agencia where id_ciudad=?", user);
        if (fila.moveToFirst()) {
            do {
                mostrar = mostrar + fila.getString(0) + "#";

            } while (fila.moveToNext());
        }
        return mostrar;

    }


    public String retornar_id_agencia(Context context, String cadena) {
        DBHelper admin = new DBHelper(context);
        SQLiteDatabase bd = admin.getWritableDatabase();


        String[] user = new String[]{cadena};
        String mostrar = "";

        Cursor fila = bd.rawQuery("SELECT id_agencia from bisa_agencia where nombre_agencia=?", user);
        if (fila.moveToFirst()) {
            do {
                mostrar = "" + fila.getString(0);

            } while (fila.moveToNext());
        }
        return mostrar;

    }

}
