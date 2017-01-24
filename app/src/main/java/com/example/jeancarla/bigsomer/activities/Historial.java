package com.example.jeancarla.bigsomer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Respuesta;
import com.example.jeancarla.bigsomer.classes.RespuestaN;
import com.example.jeancarla.bigsomer.adapters.RespuestaAdapter;
import com.example.jeancarla.bigsomer.adapters.RespuestaNAdapter;

import java.util.ArrayList;
import java.util.List;

public class Historial extends AppCompatActivity {


    /*
Adaptador del recycler view
*/
    private RespuestaAdapter adapter;
    private RespuestaNAdapter adapter_n;

    private List<Respuesta> tareas=new ArrayList<>();
    private List<RespuestaN> tareas_n=new ArrayList<>();

    private TextView nega;
    /*
    Instancia global del recycler view
     */
    private RecyclerView lista, lista_n;

    /*
    instancia global del administrador
     */
    private RecyclerView.LayoutManager lManager, lManagern;
    Funciones fu = new Funciones();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        lista = (RecyclerView) findViewById(R.id.lst_tarea);
        lista_n = (RecyclerView) findViewById(R.id.lst_tarea_n);

        nega = (TextView) findViewById(R.id.negaa);
        nega.setVisibility(View.GONE);
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        lManagern = new LinearLayoutManager(getApplicationContext());
        lista.setLayoutManager(lManager);
        lista_n.setLayoutManager(lManagern);

        String acceso_cliente = getIntent().getStringExtra("cliente");

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cargarLista(acceso_cliente);
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, Lista_CHistorial.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
    }

    public void cargarLista(String ac) {
        tareas = fu.llenar_historial(getApplicationContext(), ac);
        tareas_n = fu.llenar_historial_n(getApplicationContext(), ac);
        //Log.e("AQUIIIII",tareas[0].getIdVer());
        adapter = new RespuestaAdapter(tareas, getApplicationContext());
        adapter_n = new RespuestaNAdapter(tareas_n, getApplicationContext());

        if (tareas != null)
            lista.setAdapter(adapter);
        if (tareas_n != null){
            lista_n.setAdapter(adapter_n);
            nega.setVisibility(View.VISIBLE);
        }

    }
}
