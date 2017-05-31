package com.example.jeancarla.bigsomer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;

public class Lista_Clientes extends AppCompatActivity {

    //
    private int cantidad_tareas;
    private LinearLayout parent;
    private Dialog dialog;
    private String fecha_actualizada;
    Funciones fu = new Funciones();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__clientes);

        DBHelper admin = new DBHelper(getApplicationContext());
        SQLiteDatabase bd = admin.getWritableDatabase();

        parent = (LinearLayout) findViewById(R.id.parent);

        //Obtener fecha en la cual se actualizó la lista
        fecha_actualizada = fu.get_fecha_actualizada(getApplicationContext());

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,25,0,0);

        //Cambiar el TextView a la fecha en la cual se actualizó por última vez
        String [] v_fecha = fecha_actualizada.split(" ");
        TextView txt = new TextView(getApplicationContext());
        txt.setText("Esta .lista fue actualizada el "+v_fecha[0]+" a horas: "+v_fecha[1]);
        txt.setTextColor(getResources().getColor(R.color.colorAccent));
        txt.setBackgroundResource(R.drawable.fondo_margen);
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txt.setLayoutParams(params);
        parent.addView(txt);

        //Cursor para obtener la lista de clientes
        Cursor fila = bd.rawQuery("SELECT * from cliente", null);
        if (fila.moveToFirst())
        {
            do
            {
                //Comprueba si el cliente tiene tareas
                cantidad_tareas =fu.hay_tareas_cliente(Lista_Clientes.this,fila.getString(2));
                if (cantidad_tareas !=0){
                    Button b_cliente=new Button(getApplicationContext());
                    b_cliente.setText(fila.getString(1)+" ("+ cantidad_tareas +")");
                    b_cliente.setBackgroundResource(R.drawable.rectangulo_login);
                    b_cliente.setLayoutParams(params);
                    b_cliente.setTextColor(Color.WHITE);

                    parent.addView(b_cliente);

                    final String acceso_cliente=fila.getString(2);
                    b_cliente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Verifica si el celular tiene la hora y fecha configurada con Google
                            int comprueba_hora=android.provider.Settings.Global.getInt(getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);

                            //Si no lo tiene, pues pide al usuario que habilite esta opció, caso contrario no deja hacerla
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
                            //Si está bien deja proceder a la siguiente pantalla
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
