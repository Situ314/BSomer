package com.example.jeancarla.bigsomer.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.example.jeancarla.bigsomer.helpers.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class Lista_Clientes extends AppCompatActivity {

    private SQLiteDatabase db;
    DBHelper crearBD;
    String id_ver, id_usuario, id_user;
    private static final String TAG = "MainActivity";
    private Gson gson=new Gson();
    private int c_bcp,c_bisa,c_bg,c_bnb, c_cliente;
    private Button btn_bcp,btn_bg,btn_bnb,btn_bisa;
    private LinearLayout parent;
    private Dialog dialog;
    List<String> datosusuario;
    Funciones fu = new Funciones();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__clientes);

        DBHelper admin = new DBHelper(getApplicationContext());
        SQLiteDatabase bd = admin.getWritableDatabase();

        parent = (LinearLayout) findViewById(R.id.parent);

        String[] cl=new String[]{"0"};
        int mostrar=0;
        int contador=0;
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,25,0,0);


        Cursor fila = bd.rawQuery("SELECT * from cliente", null);
        if (fila.moveToFirst())
        {
            do
            {
                c_cliente=fu.hay_tareas_cliente(Lista_Clientes.this,fila.getString(2));
                if (c_cliente!=0){
                    Button b_cliente=new Button(getApplicationContext());
                    b_cliente.setText(fila.getString(1)+" ("+c_cliente+")");
                    b_cliente.setBackgroundResource(R.drawable.rectangulo_login);
                    b_cliente.setLayoutParams(params);
                    b_cliente.setTextColor(Color.WHITE);

                    parent.addView(b_cliente);

                    final String acceso_cliente=fila.getString(2);
                    b_cliente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int comprueba_hora=android.provider.Settings.Global.getInt(getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);

                            if(comprueba_hora==0) {
                                dialog = new Dialog(Lista_Clientes.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_fechahora);
                                dialog.setCancelable(false);
                                dialog.show();

                                Button posi = (Button) dialog.findViewById(R.id.si_boton);

                                posi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
                                        dialog.dismiss();
                                        startActivityForResult(i, 0);
                                    }
                                });
                            }
                            else {
                                Intent i = new Intent(getApplicationContext(), Lista_Tareas.class);
                                i.putExtra("cliente", acceso_cliente);
                                startActivity(i);
                            }
                        }
                    });
                }

            }while(fila.moveToNext());
        }
        bd.close();
 /*       id_user=fu.consulta_id_usuario(Lista_Clientes.this,"");
        c_bcp=fu.hay_tareas_cliente(Lista_Clientes.this,"11");
        c_bisa=fu.hay_tareas_cliente(Lista_Clientes.this,"17");
        c_bnb=fu.hay_tareas_cliente(Lista_Clientes.this,"14");
        c_bg=fu.hay_tareas_cliente(Lista_Clientes.this,"7");


        btn_bcp = (Button) findViewById(R.id.btn_bcp);
        btn_bg = (Button) findViewById(R.id.btn_bg);
        btn_bisa = (Button) findViewById(R.id.btn_bisa);
        btn_bnb = (Button) findViewById(R.id.btn_bnb);

        btn_bcp.setVisibility(View.GONE);
        btn_bg.setVisibility(View.GONE);
        btn_bnb.setVisibility(View.GONE);
        btn_bisa.setVisibility(View.GONE);

        btn_bcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Lista_Tareas.class);
                i.putExtra("cliente", "11");
                startActivity(i);
            }
        });

        btn_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Lista_Tareas.class);
                i.putExtra("cliente", "7");
                startActivity(i);
            }
        });

        btn_bisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Lista_Tareas.class);
                i.putExtra("cliente", "17");
                startActivity(i);
            }
        });

        btn_bnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Lista_Tareas.class);
                i.putExtra("cliente", "14");
                startActivity(i);
            }
        });

        if(c_bcp > 0) {
            btn_bcp.setVisibility(View.VISIBLE);
            btn_bcp.setText("  BANCO DE CRÉDITO (" + c_bcp + ")");
        }

        if(c_bnb > 0) {
            btn_bnb.setVisibility(View.VISIBLE);
            btn_bnb.setText("  BANCO NACIONAL DE BOLIVIA (" + c_bnb + ")");
        }

        if(c_bisa > 0) {
            btn_bisa.setVisibility(View.VISIBLE);
            btn_bisa.setText("  BANCO BISA (" + c_bisa + ")");
        }

        if(c_bg > 0) {
            btn_bg.setVisibility(View.VISIBLE);
            btn_bg.setText("  BANCO GANADERO (" + c_bg + ")");
        }


        // Usar un administrador para LinearLayout
*/
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
    }

    private void pedirTareas(String usua)
    {

        // Añadir parámetro a la URL del web service
        String newURL = VariablesURL.GET_TAREA + usua;

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
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley en pedir Datos: " + error.getMessage());
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
    private void procesarRespuesta(JSONObject response){
        try {
        crearBD = new DBHelper(Lista_Clientes.this);
        db = crearBD.getWritableDatabase();
        ContentValues values1 = new ContentValues();
        System.out.println("SOMETHING ==============" + response.toString());
        JSONArray mensaje = response.getJSONArray("verificaciones");

        Tarea[] tarea = gson.fromJson(mensaje.toString(), Tarea[].class);

     //   adapter = new TareaAdapter(Arrays.asList(tarea),getApplicationContext());

       // lista.setAdapter(adapter);

            for(int i=0;i<tarea.length;i++){

                values1.put("id_ver",tarea[i].getIdVer());
                values1.put("tipo_verificacion",tarea[i].getTipoVerificacion());
                values1.put("solicitante",tarea[i].getSolicitante());
                values1.put("nombre",tarea[i].getNombre());
                values1.put("ci",tarea[i].getCi());
                values1.put("direccion",tarea[i].getDireccion());
                values1.put("zona",tarea[i].getZona());
                values1.put("nombre_empresa",tarea[i].getNombreEmpresa());
                values1.put("cliente",tarea[i].getCliente());
                values1.put("referencias",tarea[i].getReferencias());
                values1.put("ub_latitud",tarea[i].getUbLatitud());
                values1.put("ub_longitud",tarea[i].getUbLongitud());
                values1.put("medidor",tarea[i].getMedidor());


                Log.i("valor a base", values1.toString());
                db.insert("tarea",null,values1);
            }

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
}
