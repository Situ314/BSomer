package com.example.jeancarla.bigsomer.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jeancarla.bigsomer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa_Detalles extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private Dialog dialog;
    private Spinner mMapTypeSelector;
    private String nombre,medidor,tarea,direccion,ci,comentarios,acceso_cliente,tipo_ver,id_ver, nombre_empresa;
    private TextView txtnombre,txtmedidor,txttarea,txtdireccion,txtci,txtcomentarios, label;
    private double lat,lon;
    private static final int LOCATION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa__detalles);

        acceso_cliente = getIntent().getStringExtra("cliente");
        tipo_ver = getIntent().getStringExtra("tipo");
        id_ver = getIntent().getStringExtra("id_ver");
        lat = getIntent().getDoubleExtra("lat",lat);
        lon = getIntent().getDoubleExtra("lon",lon);
        nombre = getIntent().getStringExtra("nombre");
        medidor = getIntent().getStringExtra("medidor");
        tarea = getIntent().getStringExtra("tarea");
        direccion = getIntent().getStringExtra("direccion");
        ci = getIntent().getStringExtra("ci");
        comentarios = getIntent().getStringExtra("comentarios");
        nombre_empresa = getIntent().getStringExtra("nombre_empresa");

        txtnombre=(TextView)findViewById(R.id.t_nombre);
        txtci=(TextView)findViewById(R.id.t_ci);
        txtmedidor=(TextView)findViewById(R.id.t_medidor);
        txtdireccion=(TextView)findViewById(R.id.t_direccion);
        txtcomentarios=(TextView)findViewById(R.id.t_referencias);
        label=(TextView)findViewById(R.id.label);

        txtnombre.setText(nombre);
        txtcomentarios.setText(comentarios);
        txtci.setText(ci);
        txtdireccion.setText(direccion);

        //Toast.makeText(getApplicationContext(),lat+" "+lon,Toast.LENGTH_LONG).show();

        if (tipo_ver.equals("1")) {
            txtmedidor.setText(medidor);
        }
        else{
            label.setText("Razón Social: ");
            txtmedidor.setText(nombre_empresa);
        }
        setToolbar();
        if (getSupportActionBar() != null) // Habilitar up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapser =
                (CollapsingToolbarLayout) findViewById(R.id.collapser);
        collapser.setTitle("Tarea Nro: "+tarea);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        // Registrar escucha onMapReadyCallback
        // Setear escucha al FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog=new Dialog(Mapa_Detalles.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_verificacion);
                        dialog.show();

                        Button posi=(Button) dialog.findViewById(R.id.positiva);
                        Button nega = (Button) dialog.findViewById(R.id.negativa);

                        posi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(getApplicationContext(),Formulario_Positiva.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("cliente",acceso_cliente);
                                bundle.putSerializable("tipo",tipo_ver);
                                bundle.putSerializable("id_ver",id_ver);
                                i.putExtras(bundle);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(i);
                            }
                        });

                        nega.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(getApplicationContext(),Formulario_Negativa.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("cliente",acceso_cliente);
                                bundle.putSerializable("tipo",tipo_ver);
                                bundle.putSerializable("id_ver",id_ver);
                                i.putExtras(bundle);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                       //AQUI IR A AVERIGUAR
                    }
                }
        );
    }

    private void setToolbar() {
        // Añadir la Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tarea nro. "+tarea);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }


        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        LatLng direccion = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions()
                .position(direccion)
                .title("Tarea nro. "+tarea));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(direccion)
                .zoom(15)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


}
