package com.example.jeancarla.bigsomer.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeancarla.bigsomer.classes.Cliente;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.classes.Formulario;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.example.jeancarla.bigsomer.helpers.VolleySingleton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteDatabase db;
    private Gson gson = new Gson();
    private static final String TAG = "CHECK";
    private ProgressDialog pDialog2;
    private int index_enviar = 0;

    private String[] enviar_positivas;
    private List<String[]> lst_respuestas = new ArrayList<>();
    private List<String[]> lst_negativas = new ArrayList<>();
    private String id_fr, lat, lon, fotos, firma, respuestas, fecha_realizada, tipo_ver, acceso_cliente, nombre, ci, cargo, comentarios;
    private int pendientes, pendientes_n;
    private String quees;
    DBHelper crearBD;
    String id_ver, id_user;

    Button lst_tareas, sincronizar, historial, fotos_faltantes;

    List<String> datosusuario;
    Funciones fu = new Funciones();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        pushDB();

       // boolean intentar = fu.check_connection(getApplicationContext());
        lst_tareas = (Button) findViewById(R.id.lista_tareas);
        sincronizar = (Button) findViewById(R.id.sincronizar);
        historial = (Button) findViewById(R.id.historial);
        fotos_faltantes = (Button) findViewById(R.id.fotos);

        historial.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        datosusuario = fu.DomsinFoto(MenuPrincipal.this, null);
        pendientes = fu.hay_tareas_pendientes(getApplicationContext());
        pendientes_n = fu.hay_negativas_pendientes(getApplicationContext());

        int total = pendientes + pendientes_n;

        sincronizar.setText("SINCRONIZAR (" + total + ")");
        id_user = fu.consulta_id_usuario(MenuPrincipal.this, "");
        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        id_ver = mngr.getDeviceId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Colocar el nombre y la foto del usuario en la cabecera del Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        System.out.print(datosusuario.get(3) + " " + datosusuario.get(4) + " " + datosusuario.get(5));
        TextView navUsername = (TextView) header.findViewById(R.id.nombre_usuario);
        navUsername.setText(datosusuario.get(3) + " " + datosusuario.get(4) + " " + datosusuario.get(5));
        TextView datos = (TextView) header.findViewById(R.id.datos);
        datos.setText(datosusuario.get(6) + " - " + datosusuario.get(7));
        ImageView foto = (ImageView) header.findViewById(R.id.foto);

        Picasso.with(getApplicationContext()).load("http://bgaver.s3.amazonaws.com/usuarios_fotos/" + datosusuario.get(0) + ".jpg").resize(100, 100).into(foto);

        //TAREA DE LOS BOTONES
        lst_tareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fu.hay_tareas(getApplicationContext()) > 0) {
                    Intent i = new Intent(getApplicationContext(), Lista_Clientes.class);
                    finish();
                    startActivity(i);
                } else
                    Toast.makeText(getApplicationContext()
                            , "No tiene tareas cargadas en este momento", Toast.LENGTH_LONG).show();
            }
        });

        sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizarPendientes();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirTareas(id_user);
                pedirClientes();
            }
        });

        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fu.hay_enviadas(getApplicationContext()) > 0) {
                    Intent i = new Intent(getApplicationContext(), Lista_CHistorial.class);
                    finish();
                    startActivity(i);
                } else
                    Toast.makeText(getApplicationContext()
                            , "No tiene verificaciones realizadas en este momento", Toast.LENGTH_LONG).show();
            }
        });

        fotos_faltantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fu.hay_enviadas(getApplicationContext()) > 0) {
                    Intent i = new Intent(getApplicationContext(), Fotos_Faltantes.class);
                    finish();
                    startActivity(i);
                } else
                    Toast.makeText(getApplicationContext()
                            , "No tiene verificaciones realizadas en este momento", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_encuesta) {
            // Handle the camera action
        } else if (id == R.id.nav_verif) {

        } else if (id == R.id.nav_inspeccion) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_credencial) {

            Intent i = new Intent(getApplicationContext(), Credencial.class);
            finish();
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ProgressDialog progressDialog;

    private void pedirTareas(String usua) {

        // Añadir parámetro a la URL del web service
        String newURL = VariablesURL.GET_TAREA + usua;
        progressDialog = new ProgressDialog(MenuPrincipal.this);
        progressDialog.setMessage("Descargando Tareas....");
        progressDialog.show();
        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        (String) null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuesta(response);
                                progressDialog.dismiss();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley en pedir Datos: " + error.getMessage());
                                Toast.makeText(getApplicationContext(), "Error al Descargar las tareas, inténtelo más tarde...", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                )
        );

    }

    /**
     * Procesa cada uno de los estados posibles de la
     * respuesta enviada desde el servidor
     *
     * @param response Objeto Json
     */
    private void procesarRespuesta(JSONObject response) {
        try {
            crearBD = new DBHelper(MenuPrincipal.this);
            db = crearBD.getWritableDatabase();
            ContentValues values1 = new ContentValues();
            System.out.println("SOMETHING ==============" + response.toString());
            String estado = response.getString("correcto");

            if (estado.equals("1")) {

                JSONArray mensaje = response.getJSONArray("verificaciones");
                Tarea[] tarea = gson.fromJson(mensaje.toString(), Tarea[].class);


                for (int i = 0; i < tarea.length; i++) {

                    values1.put("id_ver", tarea[i].getIdVer());
                    values1.put("tipo_verificacion", tarea[i].getTipoVerificacion());
                    values1.put("id_tipo", tarea[i].getIdtipo());
                    values1.put("solicitante", tarea[i].getSolicitante());
                    values1.put("nombre", tarea[i].getNombre());
                    values1.put("ci", tarea[i].getCi());
                    values1.put("direccion", tarea[i].getDireccion());
                    values1.put("zona", tarea[i].getZona());
                    values1.put("nombre_empresa", tarea[i].getNombreEmpresa());
                    values1.put("cliente", tarea[i].getCliente());
                    values1.put("referencias", tarea[i].getReferencias());
                    values1.put("ub_latitud", tarea[i].getUbLatitud());
                    values1.put("ub_longitud", tarea[i].getUbLongitud());
                    values1.put("medidor", tarea[i].getMedidor());


                    Log.i("valor a base", values1.toString());
                    db.insert("tarea", null, values1);


                }
                Toast.makeText(getApplicationContext(), "Tareas descargadas exitosamente...", Toast.LENGTH_LONG).show();
                verFormularios();
                pushDB();
                // db.insert("usuario_completo", null, values1);
            } else {
                Toast.makeText(getApplicationContext(), "No tiene tareas asignadas en este momento...", Toast.LENGTH_LONG).show();

            }
            db.close();
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private ProgressDialog progressDialog2;

    private void verFormularios() {

        progressDialog2 = new ProgressDialog(MenuPrincipal.this);
        progressDialog2.setMessage("Descargando Formularios....");
        progressDialog2.show();
        List<String> tareas = new ArrayList<>();
        tareas = fu.get_tipo_cliente(getApplicationContext());
        // Log.e("TAREAS A FORMULARIOS ",tareas.toString());

        for (int i = 0; i < tareas.size(); i++) {
            String[] datos = tareas.get(i).split("-");
            String cliente = datos[0];
            String tipo = datos[1];

            pedirFormularios(cliente, tipo);
        }
        progressDialog2.dismiss();
    }

    private void pedirFormularios(String cliente, String tipo) {

        // Añadir parámetro a la URL del web service
        String newURL = VariablesURL.GET_FORM + tipo + "&cliente=" + cliente;

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        (String) null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuestaFormularios(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley en pedir Datos: " + error.getMessage());
                                Toast.makeText(getApplicationContext(), "Error al Descargar las Formularios, inténtelo más tarde...", Toast.LENGTH_LONG).show();
                                progressDialog2.dismiss();
                            }
                        }
                )
        );

    }

    /**
     * Procesa cada uno de los estados posibles de la
     * respuesta enviada desde el servidor
     *
     * @param response Objeto Json
     */
    private void procesarRespuestaFormularios(JSONObject response) {
        try {
            crearBD = new DBHelper(MenuPrincipal.this);
            db = crearBD.getWritableDatabase();
            ContentValues values1 = new ContentValues();
            System.out.println("SOMETHING ==============" + response.toString());
            JSONArray mensaje = response.getJSONArray("formularios");

            Formulario[] form = gson.fromJson(mensaje.toString(), Formulario[].class);

            //   adapter = new TareaAdapter(Arrays.asList(tarea),getApplicationContext());

            // lista.setAdapter(adapter);

            for (int i = 0; i < form.length; i++) {

                values1.put("id", form[i].getId());
                values1.put("acceso", form[i].getAcceso());
                values1.put("tipo_verificacion", form[i].getTipoVerificacion());
                values1.put("pregunta", form[i].getPregunta());
                values1.put("n_foto_positiva", form[i].getN_foto_positiva());
                values1.put("n_foto_negativa", form[i].getN_foto_negativa());
                values1.put("firma", form[i].getFirma());
                values1.put("tipo", form[i].getTipo());
                values1.put("opciones", form[i].getOpciones());
                values1.put("idopciones", form[i].getIdopciones());
                values1.put("dependientes", form[i].getDependientes());
                values1.put("visible", form[i].getVisible());

                //Log.i("valor a base", values1.toString());
                db.insert("formulario", null, values1);
            }
            Toast.makeText(getApplicationContext(), "Formularios descargados exitosamente...", Toast.LENGTH_LONG).show();
            pushDB();
            // db.insert("usuario_completo", null, values1);
            db.close();
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void pushDB() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath());
            File data = Environment.getDataDirectory();

            if (directory.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/bigsomer_bd.sqlite";
                String backupDBPath = "todo.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(directory, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    private void pedirClientes() {

        // Añadir parámetro a la URL del web service
        String newURL = VariablesURL.GET_CLI;
        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        (String) null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuestaCliente(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley en pedir Datos: " + error.getMessage());
                                Toast.makeText(getApplicationContext(), "Error al Descargar las tareas, inténtelo más tarde...", Toast.LENGTH_LONG).show();
                            }
                        }
                )
        );

    }

    private void procesarRespuestaCliente(JSONObject response) {
        try {
            crearBD = new DBHelper(MenuPrincipal.this);
            db = crearBD.getWritableDatabase();
            ContentValues values1 = new ContentValues();
            System.out.println("SOMETHING ==============" + response.toString());
            JSONArray mensaje = response.getJSONArray("clientes");

            Cliente[] cli = gson.fromJson(mensaje.toString(), Cliente[].class);

            //   adapter = new TareaAdapter(Arrays.asList(tarea),getApplicationContext());

            // lista.setAdapter(adapter);

            for (int i = 0; i < cli.length; i++) {

                values1.put("id_cli", cli[i].getId_cli());
                values1.put("cliente", cli[i].getCliente());
                values1.put("acceso", cli[i].getAcceso());

                //Log.i("valor a base", values1.toString());
                db.insert("cliente", null, values1);
            }
            // Toast.makeText(getApplicationContext(),"Formularios descargados exitosamente...",Toast.LENGTH_LONG).show();
            pushDB();
            // db.insert("usuario_completo", null, values1);
            db.close();
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }


    public void sincronizarPendientes() {

        crearBD = new DBHelper(MenuPrincipal.this);
        db = crearBD.getWritableDatabase();
        String[] user = new String[]{"pendiente"};
        Cursor fila = db.rawQuery("SELECT * FROM formulario_respuestas WHERE estado =?", user);
        if (fila.moveToFirst()) {
            do {
                id_fr = fila.getString(0);
                lat = fila.getString(1);
                lon = fila.getString(2);
                fecha_realizada = fila.getString(3);
                tipo_ver = fila.getString(4);
                acceso_cliente = fila.getString(5);
                fotos = fila.getString(6);
                firma = fila.getString(7);
                respuestas = fila.getString(8);

                String[] v_respuestas = {id_fr, lat, lon, fecha_realizada, tipo_ver, acceso_cliente, fotos, firma, respuestas};
                lst_respuestas.add(v_respuestas);
                // Log.e("SITU: ", respuestas);
                //  enviarPendientes();
            } while (fila.moveToNext());

            //      Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
            //       finish();
            //      startActivity(i);
        } else {
           // Toast.makeText(getApplicationContext(), "No hay tareas positivas a sincronizar por el momento", Toast.LENGTH_LONG).show();
        }
            /*Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
            finish();
            startActivity(i);
            db.close();*/
        Cursor fila2 = db.rawQuery("SELECT * FROM formulario_negativas WHERE estado =?", user);
        if (fila2.moveToFirst()) {
            do {
                id_fr = fila2.getString(0);
                lat = fila2.getString(1);
                lon = fila2.getString(2);
                fecha_realizada = fila2.getString(3);
                tipo_ver = fila2.getString(4);
                acceso_cliente = fila2.getString(5);
                fotos = fila2.getString(6);
                nombre = fila2.getString(7);
                ci = fila2.getString(8);
                cargo = fila2.getString(9);
                comentarios = fila2.getString(10);

                String[] v_negativas = {id_fr, lat, lon, fecha_realizada, tipo_ver, acceso_cliente, fotos, nombre, ci, cargo, comentarios};
                lst_negativas.add(v_negativas);

                //     Log.e("SITU: ", respuestas);
                //enviarPendientesNegativas();
            } while (fila2.moveToNext());


        } else {
            //           Toast.makeText(getApplicationContext(), "No hay tareas negativas a sincronizar por el momento", Toast.LENGTH_LONG).show();
        }

        if(!lst_respuestas.isEmpty()){
            new Guardar_Pendientes().execute();
        }else
        if(!lst_negativas.isEmpty())
        {
            new Guardar_Negativas().execute();
        }else
        {
            Toast.makeText(getApplicationContext(),"No hay verificaciones a ser sincronizadas",Toast.LENGTH_LONG).show();
        }

    }

    /*
        public void sincronizarPendientes(){

            crearBD = new DBHelper(MenuPrincipal.this);
            db = crearBD.getWritableDatabase();
            String[] user = new String[]{"pendiente"};
            Cursor fila = db.rawQuery("SELECT * FROM formulario_respuestas WHERE estado =?", user);
            if (fila.moveToFirst()) {
                do {
                    id_fr=fila.getString(0);
                    lat=fila.getString(1);
                    lon=fila.getString(2);
                    fecha_realizada=fila.getString(3);
                    tipo_ver=fila.getString(4);
                    acceso_cliente=fila.getString(5);
                    fotos=fila.getString(6);
                    respuestas=fila.getString(7);

                    Log.e("SITU: ", respuestas);
                    enviarPendientes();
                } while (fila.moveToNext());

               Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
             /   finish();
                startActivity(i);

            } else {

                Toast.makeText(getApplicationContext(),"No hay tareas positivas a sincronizar por el momento", Toast.LENGTH_LONG).show();
            }

            Cursor fila2 = db.rawQuery("SELECT * FROM formulario_negativas WHERE estado =?", user);
            if (fila2.moveToFirst()) {
                do {
                    id_fr=fila2.getString(0);
                    lat=fila2.getString(1);
                    lon=fila2.getString(2);
                    fecha_realizada=fila2.getString(3);
                    tipo_ver=fila2.getString(4);
                    acceso_cliente=fila2.getString(5);
                    fotos=fila2.getString(6);
                    nombre=fila2.getString(7);
                    ci=fila2.getString(8);
                    cargo=fila2.getString(9);
                    comentarios=fila2.getString(10);

                    Log.e("SITU: ", respuestas);
                    enviarPendientesNegativas();
                } while (fila2.moveToNext());

                Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                finish();
                startActivity(i);

            } else {

                Toast.makeText(getApplicationContext(),"No hay tareas positivas a sincronizar por el momento", Toast.LENGTH_LONG).show();
            }


        }*/
    private String resultado = "problemas";
    private ProgressDialog progressDialogP, progressF;

    class Guardar_Pendientes extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogP = new ProgressDialog(MenuPrincipal.this);
            progressDialogP.setMessage("Enviando datos...");
            progressDialogP.setIndeterminate(false);
            progressDialogP.setCancelable(false);
            progressDialogP.show();
        }

        protected String doInBackground(String... args) {
            //   enviar_positivas=lst_respuestas.get(index);

            for (String[] enviar_positivas : lst_respuestas) {
                HashMap<String, String> map = new HashMap<>();// Mapeo previo

                map.put("id_ver", enviar_positivas[0]);
                map.put("lat", enviar_positivas[1] + "");
                map.put("lon", enviar_positivas[2] + "");
                map.put("fecha_realizada", enviar_positivas[3]);
                map.put("tipo_ver", enviar_positivas[4]);
                map.put("acceso_cliente", enviar_positivas[5]);
                map.put("respuestas", enviar_positivas[8]);

                fotos = enviar_positivas[6];
                firma = enviar_positivas[7];
                JSONObject jobject = new JSONObject(map);

                Log.e("SITU ENVIAR: ", jobject.toString());

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(VariablesURL.INSERT);
                    httppost.setEntity(new StringEntity(jobject.toString(), "UTF-8"));
                    HttpResponse response = httpclient.execute(httppost);
                    java.lang.String respuestaWeb = EntityUtils.toString(response.getEntity());

                    System.out.println("La web me muestra+ ////////////  " + respuestaWeb);

                    ContentValues values1 = new ContentValues();
                    values1 = new ContentValues();
                    String sfotos = fotos.substring(1, fotos.length() - 1);

                    String[] resplit = sfotos.split(", ");
                    List<String> lst_fotos = Arrays.asList(sfotos.split(", "));

                    Log.e("SITU FOTOS", lst_fotos.toString());
                    //   progressF = new ProgressDialog(MenuPrincipal.this);
                    //  progressF.setMessage("Enviando Fotos de la Verificación Nro. "+enviar_positivas[0]+", por favor espere ....");
                    //  progressF.show();
                    try {
                        StrictMode.ThreadPolicy policyf = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policyf);

                        //ENVIAR DATOS A LA NUBE
                        HttpClient httpclientf = new DefaultHttpClient();
                        HttpPost httppostf = new HttpPost(VariablesURL.INSERT_FOTOS);
                        MultipartEntityBuilder entityBuilderf = MultipartEntityBuilder.create();
                        entityBuilderf.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


                        //SUBIR LAS FOTOS
                        try {
                            for (int i = 0; i < lst_fotos.size(); i++) {

                                File file = new File(lst_fotos.get(i));
                                entityBuilderf.addBinaryBody("fotoUp", file);

                                HttpEntity entityf = entityBuilderf.build();
                                httppostf.setEntity(entityf);
                                HttpResponse response2 = httpclientf.execute(httppostf);
                                java.lang.String respuestaWebf = EntityUtils.toString(response2
                                        .getEntity());

                                System.out.println("La web me muestra+ ////////////  " + respuestaWebf);
                            }
                            if(!firma.equals("")){
                                File file = new File(firma);
                                entityBuilderf.addBinaryBody("fotoUp", file);

                                HttpEntity entityf = entityBuilderf.build();
                                httppostf.setEntity(entityf);
                                HttpResponse response2 = httpclientf.execute(httppostf);
                                java.lang.String respuestaWebf = EntityUtils.toString(response2
                                        .getEntity());

                                System.out.println("La web me muestra+ FIRMA////////////  " + respuestaWebf);
                            }

                            httpclient.getConnectionManager().shutdown();
                            resultado = "ok";
                            ContentValues values3 = new ContentValues();
                            values3.put("estado", "subida");
                            db.update("formulario_respuestas", values3, "id_fr" + " = ?", new String[]{enviar_positivas[0]});

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Conexion", "NO");
                        }
                        //resultado2 = "ok";
                    } catch (Exception e) {

                        resultado = "problemas";
                    }

                } catch (Exception e) {
                    System.out.println("aqui_se_tranca");
                    resultado = "problemas";
                }


            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            progressDialogP.dismiss();

            if (resultado.equals("ok")) {
                Toast.makeText(getApplicationContext(), "Datos de verificación Guardados", Toast.LENGTH_SHORT).show();
                if (!lst_negativas.isEmpty())
                    new Guardar_Negativas().execute();
                else {
                    Intent a = new Intent(getApplicationContext(), MenuPrincipal.class);
                    finish();
                    startActivity(a);
                }

            } else if (resultado.equals("problemas")) {
                Toast.makeText(getApplicationContext(), "Problemas de conexión,  vuelva a intentarlo más tarde...", Toast.LENGTH_SHORT).show();
                Intent a = new Intent(getApplicationContext(), MenuPrincipal.class);
                finish();
                startActivity(a);
            }
        }
    }

    class Guardar_Negativas extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogP = new ProgressDialog(MenuPrincipal.this);
            progressDialogP.setMessage("Enviando datos...");
            progressDialogP.setIndeterminate(false);
            progressDialogP.setCancelable(false);
            progressDialogP.show();
        }

        protected String doInBackground(String... args) {
            //   enviar_positivas=lst_respuestas.get(index);
            resultado2 = "problemas";
            for (String[] enviar_negativas : lst_negativas) {
                HashMap<String, String> map = new HashMap<>();// Mapeo previo

                map.put("id_ver", enviar_negativas[0]);
                map.put("lat", enviar_negativas[1]);
                map.put("lon", enviar_negativas[2]);
                map.put("fecha_realizada", enviar_negativas[3]);
                map.put("tipo_ver", enviar_negativas[4]);
                map.put("acceso_cliente", enviar_negativas[5]);
                map.put("nombre_informante", enviar_negativas[7]);
                map.put("ci_informante", enviar_negativas[8]);
                map.put("cargo_informante", enviar_negativas[9]);
                map.put("comentarios", enviar_negativas[10]);

                fotos = enviar_negativas[6];

                JSONObject jobject = new JSONObject(map);

                Log.e("SITU ENVIAR: ", jobject.toString());

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(VariablesURL.INSERT_NEGA);
                    httppost.setEntity(new StringEntity(jobject.toString(), "UTF-8"));
                    HttpResponse response = httpclient.execute(httppost);
                    java.lang.String respuestaWeb = EntityUtils.toString(response.getEntity());

                    System.out.println("La web me muestra+ ////////////  " + respuestaWeb);


                    ContentValues values1 = new ContentValues();
                    values1 = new ContentValues();
                    String sfotos = fotos.substring(1, fotos.length() - 1);

                    String[] resplit = sfotos.split(", ");
                    List<String> lst_fotos = Arrays.asList(sfotos.split(", "));

                    Log.e("SITU FOTOS", lst_fotos.toString());
                    //   progressF = new ProgressDialog(MenuPrincipal.this);
                    //  progressF.setMessage("Enviando Fotos de la Verificación Nro. "+enviar_positivas[0]+", por favor espere ....");
                    //  progressF.show();
                    try {
                        StrictMode.ThreadPolicy policyf = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policyf);

                        //ENVIAR DATOS A LA NUBE
                        HttpClient httpclientf = new DefaultHttpClient();
                        HttpPost httppostf = new HttpPost(VariablesURL.INSERT_FOTOS);
                        MultipartEntityBuilder entityBuilderf = MultipartEntityBuilder.create();
                        entityBuilderf.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


                        //SUBIR LAS FOTOS
                        try {
                            for (int i = 0; i < lst_fotos.size(); i++) {

                                File file = new File(lst_fotos.get(i));
                                entityBuilderf.addBinaryBody("fotoUp", file);

                                HttpEntity entityf = entityBuilderf.build();
                                httppostf.setEntity(entityf);
                                HttpResponse response2 = httpclientf.execute(httppostf);
                                java.lang.String respuestaWebf = EntityUtils.toString(response2
                                        .getEntity());

                                System.out.println("La web me muestra+ ////////////  " + respuestaWebf);
                            }
                            httpclient.getConnectionManager().shutdown();
                            resultado2 = "ok";
                            ContentValues values3 = new ContentValues();
                            values3.put("estado", "subida");
                            db.update("formulario_negativas", values3, "id_fr" + " = ?", new String[]{enviar_negativas[0]});

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Conexion", "NO");
                        }
                        //resultado2 = "ok";
                    } catch (Exception e) {

                        resultado2 = "problemas";
                    }

                } catch (Exception e) {
                    System.out.println("aqui_se_tranca");
                    resultado2 = "problemas";
                }

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            progressDialogP.dismiss();

            if (resultado2.equals("ok")) {
                Toast.makeText(getApplicationContext(), "Datos de verificación negativa Guardados", Toast.LENGTH_SHORT).show();
                Intent a = new Intent(getApplicationContext(), MenuPrincipal.class);
                finish();
                startActivity(a);
            } else if (resultado2.equals("problemas")) {
                Toast.makeText(getApplicationContext(), "Problemas de conexión,  vuelva a intentarlo más tarde...", Toast.LENGTH_SHORT).show();
                Intent a = new Intent(getApplicationContext(), MenuPrincipal.class);
                finish();
                startActivity(a);
            }
        }
    }

    private ProgressDialog progressDialogN;

    public void enviarPendientesNegativas() {
        //    progressDialogN = new ProgressDialog(MenuPrincipal.this);
        //   progressDialogN.setMessage("Enviando Verificación "+id_fr+" ....");
        //    progressDialogN.show();

        for (String[] enviar_negativas : lst_negativas) {

            HashMap<String, String> map = new HashMap<>();// Mapeo previo

            map.put("id_ver", enviar_negativas[0]);
            map.put("lat", enviar_negativas[1]);
            map.put("lon", enviar_negativas[2]);
            map.put("fecha_realizada", enviar_negativas[3]);
            map.put("tipo_ver", enviar_negativas[4]);
            map.put("acceso_cliente", enviar_negativas[5]);
            map.put("nombre_informante", enviar_negativas[7]);
            map.put("ci_informante", enviar_negativas[8]);
            map.put("cargo_informante", enviar_negativas[9]);
            map.put("comentarios", enviar_negativas[10]);

            JSONObject jobject = new JSONObject(map);

            Log.e("SITU ENVIAR: ", jobject.toString());
            // Actualizar datos en el servidor
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            VariablesURL.INSERT_NEGA,
                            jobject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Procesar la respuesta del servidor
                                    //      progressDialogN.dismiss();
                                    procesarRespuestaN(response);

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("SITU", "Error Volley: " + error.getMessage());
                                    Toast.makeText(getApplicationContext(), "No se pudo comunicar con el servidor en este momento, inténtelo más tarde...", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                                    finish();
                                    startActivity(i);
                                    db.close();
                                    //       progressDialogN.dismiss();
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            headers.put("Accept", "application/json");
                            return headers;
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8" + getParamsEncoding();
                        }
                    }
            );
        }
    }


    private void procesarRespuestaN(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":

                    Log.e("SITU ENVIAR: ", "BIEN");

                    break;

                case "2":
                    // Mostrar mensaje
                    Log.e("SITU ENVIAR: ", "MAL");

                    break;
            }
            if (!fotos.equals("[]")) {
                quees = "N";
                new Guardarfotos().execute();
            } else {
                ContentValues values2 = new ContentValues();
                values2.put("estado", "subida");
                db.update("formulario_negativas", values2, "id_fr" + " = ?", new String[]{id_fr});
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String resultado2 = "problemas";
    // VariablesURL variables = new VariablesURL();

    class Guardarfotos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            //      pDialog2 = new ProgressDialog(MenuPrincipal.this);
            //     pDialog2.setMessage("Enviando verificación Nro."+id_fr+" por favor espere...");
            //       pDialog2.setIndeterminate(false);
            //       pDialog2.setCancelable(false);
            //      pDialog2.show();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(MenuPrincipal.this);
            db = crearBD.getWritableDatabase();
            ContentValues values1 = new ContentValues();
            values1 = new ContentValues();
            String sfotos = fotos.substring(1, fotos.length() - 1);

            String[] resplit = sfotos.split(", ");
            List<String> lst_fotos = Arrays.asList(sfotos.split(", "));

            Log.e("SITU FOTOS", lst_fotos.toString());
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //ENVIAR DATOS A LA NUBE
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(VariablesURL.INSERT_FOTOS);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


                //SUBIR LAS FOTOS
                try {
                    for (int i = 0; i < lst_fotos.size(); i++) {

                        File file = new File(lst_fotos.get(i));
                        entityBuilder.addBinaryBody("fotoUp", file);

                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        java.lang.String respuestaWeb = EntityUtils.toString(response
                                .getEntity());

                        System.out.println("La web me muestra+ ////////////  " + respuestaWeb);
                    }
                    httpclient.getConnectionManager().shutdown();
                    resultado2 = "ok";
                    ContentValues values2 = new ContentValues();
                    values2.put("estado", "subida");
                    if (quees.equals("P"))
                        db.update("formulario_respuestas", values2, "id_fr" + " = ?", new String[]{id_fr});
                    if (quees.equals("N"))
                        db.update("formulario_negativas", values2, "id_fr" + " = ?", new String[]{id_fr});
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Conexion", "NO");
                }
                //resultado2 = "ok";
            } catch (Exception e) {

                resultado2 = "problemas";
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            //    pDialog2.dismiss();
            //		pushDB();
            if (resultado2.equals("ok"))
                Toast.makeText(getApplicationContext(), "Verificación " + id_fr + " subida correctamente", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "No se subió la verificación " + id_fr + ", por favor intente SINCRONIZAR más tarde", Toast.LENGTH_LONG).show();

            db.close();
        }
    }
}


