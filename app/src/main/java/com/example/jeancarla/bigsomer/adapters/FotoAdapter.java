package com.example.jeancarla.bigsomer.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.activities.Formulario_Historial;
import com.example.jeancarla.bigsomer.activities.Fotos_Faltantes;
import com.example.jeancarla.bigsomer.activities.MenuPrincipal;
import com.example.jeancarla.bigsomer.classes.Respuesta;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Jean Carla on 21/12/2016.
 */
public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.RespuestaViewHolder>
        implements ItemClickListener {

    DBHelper crearBD;
    private SQLiteDatabase db;
    private Dialog dialog;
    private List<Respuesta> items;
    private Funciones fu = new Funciones();
    private MapView map;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private List<String> fotos;
    private String id_click, fotos_click;
    private String []fotos_v;
    /*
Contexto donde actua el recycler view
 */
    private Context context;
    private Activity aContext;

    public FotoAdapter(List<Respuesta> items, Context context, Activity aContext) {
        this.context = context;
        this.items = items;
        this.aContext = aContext;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RespuestaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.foto_item, viewGroup, false);
        return new RespuestaViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(RespuestaViewHolder viewHolder, int i) {

        viewHolder.tarea.setText("Verificación nro. " + items.get(i).getId());
        viewHolder.fecha.setText(items.get(i).getFecha_realizada());
        viewHolder.nombre.setText(fu.get_cliente(context,items.get(i).getAcceso_cliente()));
        viewHolder.nro_fotos.setText(fu.get_nrofotos_posi(context,items.get(i).getAcceso_cliente(),items.get(i).getTipo_ver()));

    }

    /**
     * Sobrescritura del método de la interfaz {@link ItemClickListener}
     *
     * @param view     item actual
     * @param position posición del item actual
     */
    @Override
    public void onItemClick(View view, int position) {
        //DetailActivity.launch(
        //        (Activity) context, items.get(position).getIdMeta());
        Log.e("CLICK ====>", items.get(position).getId());

        final String cliente = items.get(position).getAcceso_cliente();
        final String tipo = items.get(position).getTipo_ver();
        final String id_ver = items.get(position).getId();
        final String lat = items.get(position).getLat();
        final String lon = items.get(position).getLon();
        final String fecha = items.get(position).getFecha_realizada();

        id_click = items.get(position).getId();
        fotos_click = items.get(position).getFotos();
        Log.e("SITU FOTOS ",fotos_click);
        fotos_click = fotos_click.replace("[","").replace("]","");
        Log.e("SITU FOTOS2 ",fotos_click);
        fotos_v = fotos_click.split("\\, ");
       // Log.e("SITU VECTOR ",fotos_v);
        new Guardarfotos().execute();

    }


    public static class RespuestaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, OnMapReadyCallback {
        // Campos respectivos de un item

        public SupportMapFragment mMapFragment;
        public TextView tarea;
        public TextView nombre;
        public TextView fecha;
        public TextView nro_fotos;
        public ItemClickListener listener;
        public ItemLongClickListener listenerl;

        public ImageView iv;

        public RespuestaViewHolder(View v, ItemClickListener listener) {
            super(v);
            tarea = (TextView) v.findViewById(R.id.nro_tarea);
            nombre = (TextView) v.findViewById(R.id.nombre);
            fecha = (TextView) v.findViewById(R.id.zona);
            nro_fotos = (TextView) v.findViewById(R.id.nro_fotos);
            this.listener = listener;

            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
        }
    }

    private ProgressDialog pDialog2;
    private String resultado2 = "problemas";
    class Guardarfotos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(aContext);
            pDialog2.setMessage("Enviando fotos...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(context);
            db = crearBD.getWritableDatabase();
            ContentValues values1 = new ContentValues();
            values1 = new ContentValues();

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
                    for (int i = 0; i < fotos_v.length; i++) {

                        File file = new File(fotos_v[i]);
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
            pDialog2.dismiss();
            //		pushDB();
            if (resultado2.equals("ok"))
                Toast.makeText(context, "Fotos subidas correctamente", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "No se subió las fotos, por favor intente SINCRONIZAR más tarde", Toast.LENGTH_LONG).show();

            Intent i = new Intent(context, MenuPrincipal.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            aContext.finish();
            context.startActivity(i);
       }

    }
}


interface FItemClickListener {
    void onItemClick(View view, int position);
}

