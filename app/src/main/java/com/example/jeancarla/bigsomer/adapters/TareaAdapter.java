package com.example.jeancarla.bigsomer.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.jeancarla.bigsomer.activities.Formulario_Negativa;
import com.example.jeancarla.bigsomer.activities.Formulario_Positiva;
import com.example.jeancarla.bigsomer.activities.Formulario_Visita;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.activities.Mapa_Detalles;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jean Carla on 17/10/2016.
 */
public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder>
        implements ItemClickListener, ItemLongClickListener {

    private Dialog dialog;
    private List<Tarea> items;
    private Funciones fu = new Funciones();
    private MapView map;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private String [] v_fecha;
    private String f_asignacion, fecha_faltante;
    private Calendar fechaYhora = Calendar.getInstance();
    /*
Contexto donde actua el recycler view
 */
    private Context context;

    public TareaAdapter(List<Tarea> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public TareaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tarea_item, viewGroup, false);
        return new TareaViewHolder(v, this, this);
    }

    @Override
    public void onBindViewHolder(TareaViewHolder viewHolder, int i) {

        Calendar c = Calendar.getInstance();

        viewHolder.tarea.setText("Tarea nro. " + items.get(i).getIdVer());
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.zona.setText(items.get(i).getZona());
        viewHolder.direccion.setText("                    " + items.get(i).getDireccion());

        long diferencia = 2880 - Long.parseLong(items.get(i).getF_asignacion());

        Log.e("DIFERENCIA: ",diferencia+"");
        if(diferencia < 0){
            viewHolder.iv.setBackgroundResource(R.color.colorAccent);
        }else if(diferencia < 240){
            viewHolder.iv.setBackgroundResource(R.color.colorWarning);
        }else if (diferencia < 1440){
            viewHolder.iv.setBackgroundResource(R.color.colorMid);
        }

        if(!items.get(i).getVip().equals("2"))
            viewHolder.iv_vip.setVisibility(View.GONE);

        switch (items.get(i).getIdtipo()) {

            case ("1"):
                viewHolder.iv.setImageResource(R.drawable.tic_dom);
                break;
            case ("5"):
                viewHolder.iv.setImageResource(R.drawable.tic_ind);
                break;
            case ("4"):
                viewHolder.iv.setImageResource(R.drawable.tic_dep);
                break;
            case ("10"):
                viewHolder.iv.setImageResource(R.drawable.tic_vscompra);
                break;
            case ("11"):
                viewHolder.iv.setImageResource(R.drawable.tic_vscons);
                break;
            case ("12"):
                viewHolder.iv.setImageResource(R.drawable.tic_vsref);
                break;
        }
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
        Log.e("CLICK ====>", items.get(position).getIdVer());

        dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verificacion);
        dialog.show();


        Button posi = (Button) dialog.findViewById(R.id.positiva);
        Button nega = (Button) dialog.findViewById(R.id.negativa);
        Button visita = (Button) dialog.findViewById(R.id.visita);

        final String cliente = items.get(position).getCliente();
        final String tipo = items.get(position).getIdtipo();
        final String id_ver = items.get(position).getIdVer();
        posi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Formulario_Positiva.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cliente", cliente);
                bundle.putSerializable("tipo", tipo);
                bundle.putSerializable("id_ver", id_ver);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        });

        nega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Formulario_Negativa.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cliente", cliente);
                bundle.putSerializable("tipo", tipo);
                bundle.putSerializable("id_ver", id_ver);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        });

        visita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Formulario_Visita.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("id_ver", id_ver);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        });
    }


    @Override
    public boolean onItemLongClick(View view, int position) {
        final Double lat, lon;
        lat = fu.Convertir_Datos(items.get(position).getUbLatitud());
        lon = fu.Convertir_Datos(items.get(position).getUbLongitud());

        final String nombre, medidor, tarea, direccion, ci, comentarios, nombre_empresa;
        final String cliente = items.get(position).getCliente();
        final String tipo = items.get(position).getIdtipo();
        final String id_ver = items.get(position).getIdVer();

        nombre = items.get(position).getNombre();
        medidor = items.get(position).getMedidor();
        tarea = items.get(position).getIdVer();
        direccion = items.get(position).getDireccion();
        ci = items.get(position).getCi();
        comentarios = items.get(position).getReferencias();
        nombre_empresa = items.get(position).getNombreEmpresa();

        long diferencia = 2880 - Long.parseLong(items.get(position).getF_asignacion());
        long diff = diferencia * 60000;

        if(diff > 0) {
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = diff / daysInMilli;
            diff = diff % daysInMilli;

            long elapsedHours = diff / hoursInMilli;
            diff = diff % hoursInMilli;

            long elapsedMinutes = diff / minutesInMilli;
            diff = diff % minutesInMilli;

            long elapsedSeconds = diff / secondsInMilli;

            fecha_faltante = elapsedDays + " días " + elapsedHours + " horas " + elapsedMinutes + " minutos";
        }
        else{
            fecha_faltante = "Esta verificación ya se encuentra con RETRASO";
        }

        Intent i = new Intent(context, Mapa_Detalles.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", cliente);
        bundle.putSerializable("tipo", tipo);
        bundle.putSerializable("id_ver", id_ver);
        bundle.putSerializable("lat", lat);
        bundle.putSerializable("lon", lon);
        bundle.putSerializable("nombre", nombre);
        bundle.putSerializable("medidor", medidor);
        bundle.putSerializable("tarea", tarea);
        bundle.putSerializable("direccion", direccion);
        bundle.putSerializable("nombre_empresa",nombre_empresa);
        bundle.putSerializable("ci", ci);
        bundle.putSerializable("comentarios", comentarios);
        bundle.putSerializable("falta", fecha_faltante);
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);

        Log.e("LONG CLICK ====>", items.get(position).getIdVer());

        return true;
    }


    public static class TareaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, OnMapReadyCallback {
        // Campos respectivos de un item

        public SupportMapFragment mMapFragment;
        public TextView tarea;
        public TextView nombre;
        public TextView zona;
        public TextView direccion;
        public ItemClickListener listener;
        public ItemLongClickListener listenerl;
        public LinearLayout advertencia;

        public ImageView iv;
        public ImageView iv_vip;

        public TareaViewHolder(View v, ItemClickListener listener, ItemLongClickListener longlistener) {
            super(v);
            tarea = (TextView) v.findViewById(R.id.nro_tarea);
            nombre = (TextView) v.findViewById(R.id.nombre);
            zona = (TextView) v.findViewById(R.id.zona);
            direccion = (TextView) v.findViewById(R.id.direccion);
            iv = (ImageView) v.findViewById(R.id.imageView2);
            iv_vip = (ImageView) v.findViewById(R.id.iv_vip);
            //advertencia = (LinearLayout) v.findViewById(R.id.tarda);
            this.listener = listener;
            this.listenerl = longlistener;

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            listenerl.onItemLongClick(v, getAdapterPosition());
            return true;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
        }
    }
}


interface ItemClickListener {
    void onItemClick(View view, int position);
}

interface ItemLongClickListener {
    boolean onItemLongClick(View view, int position);
}
