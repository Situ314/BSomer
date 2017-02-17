package com.example.jeancarla.bigsomer.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.example.jeancarla.bigsomer.helpers.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Formulario_Visita extends AppCompatActivity {

    private EditText et_comentarios, et_fecha;

    private Button btn_elegir;

    private double lat = 0;
    private double lon = 0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;
    private ImageView iv_location;

    DBHelper crearBD;
    private SQLiteDatabase db;

    private String id_ver;
    private int mYear, mMonth, mDay;

    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String fecha_actual;

    private String e_comentarios, e_fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario__visita);

        id_ver = getIntent().getStringExtra("id_ver");

        et_comentarios = (EditText) findViewById(R.id.v_comentarios);
        et_fecha = (EditText) findViewById(R.id.v_fecha);

        btn_elegir = (Button) findViewById(R.id.b_elegir);

        btn_elegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Formulario_Visita.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                et_fecha.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                //Log.e("SITU DATE: ", datee);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //******************LOCATION THINGS

        iv_location = (ImageView) findViewById(R.id.iv_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                iv_location.setImageResource(R.drawable.ic_location_on_black_24dp);
                //estado1.setImageResource(R.drawable.check);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            public void onProviderDisabled(String provider) {
                Log.i("LocAndroid",
                        "Proveedor GPS deshabilitado, habilitelo por favor");
                Intent in = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(in);
            }

            public void onProviderEnabled(String provider) {
                Log.i("LocAndroid", "Proveedor habilitado");
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("LocAndroid", "Estado proveedor: " + status);
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 0, locationListener);

        //LOCATION*************************************
        fecha_actual = fecha.format(fechaYhora.getTime());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return true;
    }

    private Dialog dialog;

    /*evento para el boton BACK*/
    @Override
    public void onBackPressed() {
        dialog = new Dialog(Formulario_Visita.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_two);
        dialog.show();

        Button b_enviar = (Button) dialog.findViewById(R.id.si_boton);
        b_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button b_cancelar = (Button) dialog.findViewById(R.id.no_boton);
        b_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_enviar) {
            dialog = new Dialog(Formulario_Visita.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_nuevo);
           dialog.show();
            if (lat != 0 && lon != 0) {

                if(!et_comentarios.getText().equals("") && !et_fecha.getText().equals("")){

                    TextView titulo = (TextView) dialog.findViewById(R.id.info_text);
                    titulo.setText("ENVIAR DATOS DE LA VISITA");

                    TextView info = (TextView) dialog.findViewById(R.id.info_text2);
                    info.setText("¿Está seguro de querer enviar la información de la visita? Si está seguro presione ENVIAR");

                    TextView info2 = (TextView) dialog.findViewById(R.id.info_text3);
                    info2.setVisibility(View.GONE);

                    Button b_enviar = (Button) dialog.findViewById(R.id.enviar_boton);
                    b_enviar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enviar();
                            dialog.dismiss();
                        }
                    });

                    Button b_cancelar = (Button) dialog.findViewById(R.id.cancelar_boton);
                    b_cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }else{
                    TextView titulo = (TextView) dialog.findViewById(R.id.info_text);
                    titulo.setText("TODOS LOS CAMPOS DEBEN ESTAR LLENOS");

                    TextView info = (TextView) dialog.findViewById(R.id.info_text2);
                    info.setText("Al parecer no lleno todos los campos, recuerde que todos los campos deben tener algún tipo de información. Inténtelo de nuevo.");

                    TextView info2 = (TextView) dialog.findViewById(R.id.info_text3);
                    info2.setVisibility(View.GONE);

                    Button b_enviar = (Button) dialog.findViewById(R.id.enviar_boton);
                    b_enviar.setVisibility(View.GONE);

                    Button b_cancelar = (Button) dialog.findViewById(R.id.cancelar_boton);
                    b_cancelar.setText("OK");
                    b_cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    return true;
                }


            } else {
                TextView titulo = (TextView) dialog.findViewById(R.id.info_text);
                titulo.setText("NO SE ENCONTRARON COORDENADAS");

                TextView info = (TextView) dialog.findViewById(R.id.info_text2);
                info.setText("No se obtuvieron coordenadas geográficas. Asegúrese de estar al aire libre y vuelva a intentarlo");

                TextView info2 = (TextView) dialog.findViewById(R.id.info_text3);
                info2.setVisibility(View.GONE);

                Button b_enviar = (Button) dialog.findViewById(R.id.enviar_boton);
                b_enviar.setVisibility(View.GONE);

                Button b_cancelar = (Button) dialog.findViewById(R.id.cancelar_boton);
                b_cancelar.setText("OK");
                b_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                return true;
            }

        } else {
            dialog = new Dialog(Formulario_Visita.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_two);
            dialog.show();

            Button b_enviar = (Button) dialog.findViewById(R.id.si_boton);
            b_enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Button b_cancelar = (Button) dialog.findViewById(R.id.no_boton);
            b_cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private ProgressDialog progressDialog;

    public void enviar(){

        progressDialog = new ProgressDialog(Formulario_Visita.this);
        progressDialog.setMessage("Enviando Datos de Visita....");
        progressDialog.show();

        e_comentarios = et_comentarios.getText().toString();
        e_fecha = et_fecha.getText().toString();

        crearBD = new DBHelper(Formulario_Visita.this);
        db = crearBD.getWritableDatabase();
        ContentValues values1 = new ContentValues();
        values1.put("id_sol", id_ver);
        values1.put("lat", lat);
        values1.put("lon", lon);
        values1.put("comentarios", e_comentarios);
        values1.put("fecha_realizada", fecha_actual);
        values1.put("fecha_regreso", e_fecha);
        values1.put("estado", "pendiente");
        db.insert("visita", null, values1);

        //fu.eliminar_tarea(getApplicationContext(),id_ver);
        System.out.println("LAT: " + lat + " LON: " + lon);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_ver", id_ver);
        map.put("comentarios", e_comentarios);
        map.put("fecha_regreso", e_fecha);
        map.put("lat", lat + "");
        map.put("lon", lon + "");
        map.put("fecha_realizada", fecha_actual);

        JSONObject jobject = new JSONObject(map);

        Log.e("SITU ENVIAR: ", jobject.toString());
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        VariablesURL.INSERT_VISITA,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                progressDialog.dismiss();
                                procesarRespuesta(response);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("SITU", "Error Volley: " + error.getMessage());
                                Toast.makeText(getApplicationContext(), "No se pudo comunicar con el servidor en este momento, inténtelo más tarde...", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                                finish();
                                startActivity(i);
                                db.close();
                                progressDialog.dismiss();
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    /**
     * Procesa la respuesta obtenida desde el sevidor
     *
     * @param response Objeto Json
     */
    private void procesarRespuesta(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Log.e("SITU ENVIAR: ", "BIEN");
                        Toast.makeText(getApplicationContext(), "Informacion de Visita subida correctamente", Toast.LENGTH_LONG).show();
                    ContentValues values2 = new ContentValues();
                    values2.put("estado", "subida");
                    db.update("visita", values2, "id_sol" + " = ?", new String[]{id_ver});
                    Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                    finish();
                    startActivity(i);
                    db.close();
                    break;

                case "2":
                    // Mostrar mensaje
                    Log.e("SITU ENVIAR: ", "MAL");
                    // Enviar código de falla
                    // getActivity().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    //getActivity().finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

