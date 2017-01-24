package com.example.jeancarla.bigsomer.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.example.jeancarla.bigsomer.adapters.TareaAdapter;

import java.util.List;

public class Lista_Tareas extends AppCompatActivity {

    /*
Adaptador del recycler view
*/
    private TareaAdapter adapter;
    private Dialog dialog;
    /*
    Instancia global del recycler view
     */
    private RecyclerView lista;

    /*
    instancia global del administrador
     */
    private RecyclerView.LayoutManager lManager;
    Funciones fu=new Funciones();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__tareas);

        lista = (RecyclerView) findViewById(R.id.lst_tarea);
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        lista.setLayoutManager(lManager);

        String acceso_cliente = getIntent().getStringExtra("cliente");

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cargarLista(acceso_cliente);
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

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, Lista_Clientes.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return true;
    }

    public void cargarLista(String ac){
        List<Tarea> tareas = fu.llenar_tarea(getApplicationContext(),ac);
        //Log.e("AQUIIIII",tareas[0].getIdVer());
        adapter = new TareaAdapter(tareas,getApplicationContext());
        lista.setAdapter(adapter);


    }
}
