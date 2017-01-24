package com.example.jeancarla.bigsomer.adapters;

import android.app.Activity;
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
import android.widget.TextView;


import com.example.jeancarla.bigsomer.activities.Formulario_Negativa;
import com.example.jeancarla.bigsomer.activities.Formulario_verif;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.activities.Mapa_Detalles;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

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


        viewHolder.tarea.setText("Tarea nro. " + items.get(i).getIdVer());
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.zona.setText(items.get(i).getZona());
        viewHolder.direccion.setText("                    " + items.get(i).getDireccion());


        switch (items.get(i).getTipoVerificacion()) {

            case ("domiciliaria"):
                viewHolder.iv.setImageResource(R.drawable.ic_dom);
                break;
            case ("laboral independiente"):
                viewHolder.iv.setImageResource(R.drawable.ic_ind);
                break;
            case ("laboral dependiente"):
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
        Log.e("CLICK ====>", items.get(position).getIdVer());

        dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verificacion);
        dialog.show();


        Button posi = (Button) dialog.findViewById(R.id.positiva);
        Button nega = (Button) dialog.findViewById(R.id.negativa);


        final String cliente = items.get(position).getCliente();
        final String tipo = items.get(position).getIdtipo();
        final String id_ver = items.get(position).getIdVer();
        posi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Formulario_verif.class);
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

        public ImageView iv;

        public TareaViewHolder(View v, ItemClickListener listener, ItemLongClickListener longlistener) {
            super(v);
            tarea = (TextView) v.findViewById(R.id.nro_tarea);
            nombre = (TextView) v.findViewById(R.id.nombre);
            zona = (TextView) v.findViewById(R.id.zona);
            direccion = (TextView) v.findViewById(R.id.direccion);
            iv = (ImageView) v.findViewById(R.id.imageView2);
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
