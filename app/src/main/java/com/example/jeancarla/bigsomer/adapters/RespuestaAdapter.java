package com.example.jeancarla.bigsomer.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeancarla.bigsomer.activities.Formulario_Historial;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Respuesta;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

/**
 * Created by Jean Carla on 18/11/2016.
 */
public class RespuestaAdapter extends RecyclerView.Adapter<RespuestaAdapter.RespuestaViewHolder>
        implements ItemClickListener {

    private Dialog dialog;
    private List<Respuesta> items;
    private Funciones fu = new Funciones();
    private MapView map;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    /*
Contexto donde actua el recycler view
 */
    private Context context;

    public RespuestaAdapter(List<Respuesta> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RespuestaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.historial_item, viewGroup, false);
        return new RespuestaViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(RespuestaViewHolder viewHolder, int i) {


        viewHolder.tarea.setText("Verificación nro. " + items.get(i).getId());
        String nombre = fu.get_nombre_direccion(context, items.get(i).getId());
        Log.e("SITU STRING: ", nombre);
        String[] nyd = nombre.split("!!");
        Log.e("SITU STRING: ", nyd[0] + "--" + nyd[1]);
        viewHolder.nombre.setText("" + nyd[0]);
        viewHolder.fecha.setText(items.get(i).getFecha_realizada());
        viewHolder.direccion.setText("                    " + nyd[1]);


        switch (items.get(i).getTipo_ver()) {

            case ("1"):
                viewHolder.iv.setImageResource(R.drawable.ic_dom);
                break;
            case ("5"):
                viewHolder.iv.setImageResource(R.drawable.ic_ind);
                break;
            case ("4"):
                viewHolder.iv.setImageResource(R.drawable.ic_dep);
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
        Log.e("CLICK ====>", items.get(position).getId());

        final String cliente = items.get(position).getAcceso_cliente();
        final String tipo = items.get(position).getTipo_ver();
        final String id_ver = items.get(position).getId();
        final String lat = items.get(position).getLat();
        final String lon = items.get(position).getLon();
        final String fecha = items.get(position).getFecha_realizada();
        Intent i = new Intent(context, Formulario_Historial.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", cliente);
        bundle.putSerializable("tipo", tipo);
        bundle.putSerializable("id_ver", id_ver);
        bundle.putSerializable("lat", lat);
        bundle.putSerializable("lon", lon);
        bundle.putSerializable("fecha", fecha);
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);

    }


    public static class RespuestaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, OnMapReadyCallback {
        // Campos respectivos de un item

        public SupportMapFragment mMapFragment;
        public TextView tarea;
        public TextView nombre;
        public TextView fecha;
        public TextView direccion;
        public ItemClickListener listener;
        public ItemLongClickListener listenerl;

        public ImageView iv;

        public RespuestaViewHolder(View v, ItemClickListener listener) {
            super(v);
            tarea = (TextView) v.findViewById(R.id.nro_tarea);
            nombre = (TextView) v.findViewById(R.id.nombre);
            fecha = (TextView) v.findViewById(R.id.zona);
            direccion = (TextView) v.findViewById(R.id.direccion);
            iv = (ImageView) v.findViewById(R.id.imageView2);
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
}


interface RItemClickListener {
    void onItemClick(View view, int position);
}
