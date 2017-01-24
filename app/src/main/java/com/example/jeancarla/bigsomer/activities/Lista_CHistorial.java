package com.example.jeancarla.bigsomer.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.google.gson.Gson;

import java.util.List;

public class Lista_CHistorial extends AppCompatActivity {


    private SQLiteDatabase db;
    DBHelper crearBD;
    String id_ver, id_usuario, id_user;
    private static final String TAG = "MainActivity";
    private Gson gson=new Gson();
    private int c_bcp,c_bisa,c_bg,c_bnb, c_cliente, c_cliente_n;
    private Button btn_bcp,btn_bg,btn_bnb,btn_bisa;
    private LinearLayout parent, parentn;
    List<String> datosusuario;
    Funciones fu = new Funciones();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__chistorial);

        DBHelper admin = new DBHelper(getApplicationContext());
        SQLiteDatabase bd = admin.getWritableDatabase();

        parent = (LinearLayout) findViewById(R.id.parent);

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,25,0,0);
        Cursor fila = bd.rawQuery("SELECT * from cliente", null);
        if (fila.moveToFirst())
        {
            do
            {
                c_cliente=fu.hay_historial(Lista_CHistorial.this,fila.getString(2));
                c_cliente_n=fu.hay_historial_negativa(Lista_CHistorial.this,fila.getString(2));
                if (c_cliente!=0 ||c_cliente_n!=0){
                    int total=c_cliente+c_cliente_n;
                    Button b_cliente=new Button(getApplicationContext());
                    b_cliente.setText(fila.getString(1)+" ("+total+")");
                    b_cliente.setBackgroundResource(R.drawable.rectangulo_login);
                    b_cliente.setLayoutParams(params);
                    b_cliente.setTextColor(Color.WHITE);

                    parent.addView(b_cliente);

                    final String acceso_cliente=fila.getString(2);
                    b_cliente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(getApplicationContext(),Historial.class);
                            i.putExtra("cliente", acceso_cliente);
                            startActivity(i);
                        }
                    });
                }

            }while(fila.moveToNext());
        }

        bd.close();
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


}
