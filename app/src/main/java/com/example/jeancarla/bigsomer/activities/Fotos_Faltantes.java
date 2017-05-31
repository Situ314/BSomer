package com.example.jeancarla.bigsomer.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeancarla.bigsomer.DateDialog;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.adapters.FotoAdapter;
import com.example.jeancarla.bigsomer.classes.Respuesta;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Fotos_Faltantes extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    List<Respuesta> lista_mal = new ArrayList<>();
    List<Respuesta> lista = new ArrayList<>();
    Respuesta rspAnadir;
    Dialog dialogIncial, dialogBuscarId;
    Button btnDialogTodo, btnDialogFecha, btnDialogId;
    Funciones fu=new Funciones();
    String enviar_id, enviar_cliente, enviar_fecha;
    private FotoAdapter adapter;
    /*
    Instancia global del recycler view
     */
    private RecyclerView recyclerLstFotosFaltantes;

    /*
    instancia global del administrador
     */
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos__faltantes);

        //Mostrar diálogo para que el usuario elija
        //entre elegir ID,Fecha o TODO
        dialogIncial = new Dialog(Fotos_Faltantes.this);
        dialogIncial.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogIncial.setContentView(R.layout.dialog_opcionesfotos);
        dialogIncial.show();

        recyclerLstFotosFaltantes = (RecyclerView) findViewById(R.id.lst_tarea);
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        recyclerLstFotosFaltantes.setLayoutManager(lManager);

        btnDialogTodo = (Button) dialogIncial.findViewById(R.id.xtodas);
        btnDialogFecha = (Button) dialogIncial.findViewById(R.id.xfecha);
        btnDialogId = (Button) dialogIncial.findViewById(R.id.xid);

        btnDialogTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarTodo();
            }
        });

        btnDialogFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cargarxFecha();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDialogId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarxId();
            }
        });

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

    //Verifica TODAS las tareas guardadas en el celular
    public void cargarTodo(){

        lista = fu.get_todo_respuestas(getApplicationContext());
        dialogIncial.dismiss();
        new GetSinFotos().execute();

    }

    //Verifica desde una FECHA dada
    private String fecha="2016-12-20";
    private Date date_elegida;
    public void cargarxFecha() throws ParseException {
        FragmentManager fragmentManager = getFragmentManager();
        new DateDialog().show(fragmentManager, "DatePickerFragment");
    }

    @Override
    public void onDateSet(DatePicker objPicker, int year, int monthOfYear, int dayOfMonth) {
        // open your second dialogo or do anything you want
        fecha = year +"-"+ (monthOfYear+1)+"-"+dayOfMonth;
        Log.e("SITU: ","ENTROOOOOOOOO");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date_elegida = sdf.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        lista = fu.get_xfecha(getApplicationContext(),date_elegida);

        if(lista.isEmpty()){
            Intent intent = new Intent(this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(getApplicationContext(),"No hay ninguna verificación realizada en esas fechas",Toast.LENGTH_LONG).show();
            finish();
            startActivity(intent);
        }else
        new GetSinFotos().execute();

        Log.e("SITU LISTA: ", lista.toString());
        dialogIncial.dismiss();
    }

    //Veririca por ID
    private String id;
    public void cargarxId(){

        //Lanza un diálogo para introducir el ID
        dialogBuscarId = new Dialog(Fotos_Faltantes.this);
        dialogBuscarId.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBuscarId.setContentView(R.layout.dialog_dep);
        dialogBuscarId.show();

        TextView tv_titulo = (TextView) dialogBuscarId.findViewById(R.id.pregunta);
        EditText et_id = (EditText) dialogBuscarId.findViewById(R.id.respuesta);
        Button enviar = (Button) dialogBuscarId.findViewById(R.id.enviar_boton);
        Button cancelar = (Button) dialogBuscarId.findViewById(R.id.cancelar_boton);

        tv_titulo.setText("Elija que verificación desea buscar");
        id = et_id.getText().toString();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista = fu.get_xId(getApplicationContext(),id);
                if(lista.isEmpty()){
                    Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Toast.makeText(getApplicationContext(),"No hay ninguna verificación realizada con ese ID",Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(intent);
                }else
                new GetSinFotos().execute();

                dialogBuscarId.dismiss();
                dialogIncial.dismiss();

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuscarId.dismiss();
            }
        });

    }

    private ProgressDialog pDialog1;
    //Envía los ID seleccionados y devuelve una lista con los que le faltan alguna foto
    class GetSinFotos extends AsyncTask<String, String, String>{

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog1 = new ProgressDialog(Fotos_Faltantes.this);
            pDialog1.setMessage("Verificando si faltan fotos...");
            pDialog1.setIndeterminate(false);
            pDialog1.setCancelable(false);
            pDialog1.show();
        }
        protected String doInBackground(String... args) {


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            //ENVIAR DATOS A LA NUBE
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(VariablesURL.GET_PICS);
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int i = 0; i < lista.size(); i++) {

                rspAnadir = lista.get(i);
                enviar_cliente = lista.get(i).getAcceso_cliente();
                enviar_id = lista.get(i).getId();
                enviar_fecha = lista.get(i).getFecha_realizada();

                Log.e("SITU LISTA ", i+"--> "+enviar_cliente + " " + enviar_id + " " + enviar_fecha);

                entityBuilder.addTextBody("id_ver", enviar_id, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                entityBuilder.addTextBody("cliente", enviar_cliente, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                entityBuilder.addTextBody("fecha_realizada", enviar_fecha, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                try {
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    java.lang.String respuestaWeb = EntityUtils.toString(response
                            .getEntity());

                    Log.e("SITU RESPUESTA ", respuestaWeb);
                    if (!respuestaWeb.equals("BIEN")) {
                        lista_mal.add(rspAnadir);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Conexion", "NO");
                }
            }
            httpclient.getConnectionManager().shutdown();
            return null;
        }
        protected void onPostExecute (String file_url){
            pDialog1.dismiss();
            //Envía la lista al adapter si es que no está vacía
            if(!lista_mal.isEmpty())
            cargarLista();
            else{
                Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(getApplicationContext(),"No hay ninguna verificación a la cual le falte alguna foto",Toast.LENGTH_LONG).show();
                finish();
                startActivity(intent);
            }
        }
    }

    public void cargarLista(){
        //Envía la Lista para cargar los datos en el Custom Adapter FotoAdapter
        adapter = new FotoAdapter(lista_mal,getApplicationContext(),Fotos_Faltantes.this);
        recyclerLstFotosFaltantes.setAdapter(adapter);

    }
}
