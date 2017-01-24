package com.example.jeancarla.bigsomer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Usuario;
import com.squareup.picasso.Picasso;

public class Credencial extends AppCompatActivity {

    TextView nombre, ci, ciudad, telefono;
    ImageView foto;
    String snombre,sci,sciudad,stelefono;

    Funciones fu = new Funciones();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credencial);

        nombre = (TextView) findViewById (R.id.t_nombre);
        ci = (TextView) findViewById(R.id.t_ci);
        ciudad = (TextView) findViewById(R.id.t_ciudad);
        telefono = (TextView) findViewById(R.id.t_telefono);

        foto = (ImageView) findViewById(R.id.foto);

        Usuario u=fu.get_usuario(getApplicationContext());

        snombre = u.getNombres()+" "+u.getApellido_1()+" "+u.getApellido_2();
        sci = u.getCi();
        sciudad = u.getCiudad();
        stelefono = u.getTelefono();

        nombre.setText(snombre);
        ci.setText(sci);
        ciudad.setText(sciudad);
        telefono.setText(stelefono);

        Picasso.with(getApplicationContext()).load("http://bgaver.s3.amazonaws.com/usuarios_fotos/" + u.getId_usuario() + ".jpg").into(foto);

        setToolbar();

        if (getSupportActionBar() != null) // Habilitar up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapser =
                (CollapsingToolbarLayout) findViewById(R.id.collapser);
        collapser.setTitle("Credencial");
    }


    /*evento para el boton BACK*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MenuPrincipal.class);
        finish();
        startActivity(i);
    }

    private void setToolbar() {
        // Añadir la Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Credencial");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(getApplicationContext(),MenuPrincipal.class);
        finish();
        startActivity(i);
        return true;
    }
}
