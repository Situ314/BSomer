package com.example.jeancarla.bigsomer.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeancarla.bigsomer.helpers.CaptureSignature;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.classes.Formulario;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.classes.Opcion;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.example.jeancarla.bigsomer.helpers.VolleySingleton;
import com.example.jeancarla.bigsomer.adapters.SpinAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Formulario_Positiva extends AppCompatActivity {

    private int controlador;
    private String tipo_final;
    private String sfecha;
    LinearLayout parent, layout_fotos;
    private EditText txt_respuesta, txt_respuesta_date;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private int n_fotos;
    private String firma;
    private int vacio;
    DBHelper crearBD;
    private ImageView iv_location;
    private SQLiteDatabase db;
//    ListMultimap<String, String> mapSpinner = ArrayListMultimap.create();

    private SpinAdapter adapter;
    private Funciones fu = new Funciones();
    // obtiene fecha actual
    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String fecha_actual;

    private List<SpinAdapter> lst_adapter = new ArrayList<>();
    private List<Opcion> lst_obj = new ArrayList<>();
    private List<Spinner> lst_spinner = new ArrayList<>();
    private String acceso_cliente, tipo_ver, id_ver;
    boolean control_foto = false;
    private HashMap<String, String> spinnerMap = new HashMap<String, String>();
    private List<String> fotos;
    private double lat = 0;
    private double lon = 0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    private String pago_alquiler;
    private Dialog dialog2;
    private LinearLayout date_layout;
    private String final_value, datee, selected_item;
    private Opcion opcion_sel = new Opcion();
    private String nombre_foto,fotofirma;
    private ImageButton button_foto, button_firma;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private SpinAdapter ap, api;

    private TextView tv_cargo;
    private EditText et_cargo;
    LinearLayout.LayoutParams paramb = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0.3f);
    LinearLayout.LayoutParams paramet = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0.7f);

    public static final int SIGNATURE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_verif);

        acceso_cliente = getIntent().getStringExtra("cliente");
        tipo_ver = getIntent().getStringExtra("tipo");
        id_ver = getIntent().getStringExtra("id_ver");

        parent = (LinearLayout) findViewById(R.id.parent);
        fotos = new ArrayList<>();

        tv_cargo = (TextView) findViewById(R.id.cargo_static);
        et_cargo = (EditText) findViewById(R.id.v_cargo);

        if (tipo_ver.equals("1")) {
            parent.removeView(tv_cargo);
            parent.removeView(et_cargo);
            //tv_cargo.setVisibility(View.GONE);
           // et_cargo.setVisibility(View.GONE);
        }
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

        //***********FOOOOOTOS
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath()
                + "/Bigsomer_aver");
        if (!directory.isDirectory())
            directory.mkdirs();

        nombre_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + ".jpg";
        //FOTOS**************
        txt_respuesta_date = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
        fecha_actual = fecha.format(fechaYhora.getTime());
        System.out.println(fecha_actual);
        datee = fecha_actual.toString();
        txt_respuesta_date.setText("No indica");

       // Toast.makeText(getApplicationContext(), acceso_cliente + " " + tipo_ver, Toast.LENGTH_LONG).show();

        DBHelper admin = new DBHelper(getApplicationContext());
        SQLiteDatabase bd = admin.getWritableDatabase();

        int i = 0;
        List<Formulario> preguntas = new ArrayList<Formulario>();
        Formulario[] formulario = new Formulario[100];
        String[] user = new String[]{acceso_cliente, tipo_ver};
        Cursor fila = bd.rawQuery("SELECT * FROM formulario WHERE acceso=? AND tipo_verificacion=?", user);
        if (fila.moveToFirst()) {
            do {
                formulario[i] = new Formulario();
                formulario[i].setId(fila.getString(0));
                formulario[i].setAcceso(fila.getString(1));
                formulario[i].setTipoVerificacion(fila.getString(2));
                formulario[i].setPregunta(fila.getString(3));
                formulario[i].setN_foto_positiva(fila.getString(4));
                formulario[i].setN_foto_negativa(fila.getString(5));
                formulario[i].setFirma(fila.getString(6));
                formulario[i].setTipo(fila.getString(7));
                formulario[i].setOpciones(fila.getString(8));
                formulario[i].setIdopciones(fila.getString(9));
                formulario[i].setDependientes(fila.getString(10));
                formulario[i].setVisible(fila.getString(11));

                preguntas.add(formulario[i]);
                i++;
            } while (fila.moveToNext());

            bd.close();
        } else {
            Log.e("ERROR FETCHING ", "oooooops");
            bd.close();
        }

        date_layout = new LinearLayout(getApplicationContext());
        date_layout.setOrientation(LinearLayout.HORIZONTAL);
        myCalendar = Calendar.getInstance();
        Date date1 = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sfecha = sdf.format(date1);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //txt_respuesta_date.setText(new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear+1).append("/").append(year));

                updateLabel();
            }

        };
        // Toast.makeText(getApplicationContext(),preguntas.get(0).getPregunta(),Toast.LENGTH_LONG).show();
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if(!preguntas.isEmpty())
        generarFormulario(preguntas);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_enviar) {

            dialog = new Dialog(Formulario_Positiva.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_nuevo);
            dialog.show();
            if (lat != 0 && lon != 0) {
                if (n_fotos == fotos.size()) {
                    TextView titulo = (TextView) dialog.findViewById(R.id.info_text);
                    titulo.setText("ENVIAR VERIFICACIÓN");

                    TextView info = (TextView) dialog.findViewById(R.id.info_text2);
                    info.setText("¿Está seguro de querer enviar el formulario? Si está seguro presione ENVIAR");

                    TextView info2 = (TextView) dialog.findViewById(R.id.info_text3);
                    info2.setVisibility(View.GONE);

                    Button b_enviar = (Button) dialog.findViewById(R.id.enviar_boton);
                    b_enviar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enviarFormulario();
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

                    return true;
                } else {
                    TextView titulo = (TextView) dialog.findViewById(R.id.info_text);
                    titulo.setText("NO SE ENCONTRARON LAS FOTOS REQUERIDAS");

                    TextView info = (TextView) dialog.findViewById(R.id.info_text2);
                    info.setText("La presente verificación necesita de " + n_fotos + " fotografías para poder ser enviada. Asegúrese de haber sacado todas las fotos o de tener espacio suficiente en la memoria del celular");

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
            dialog = new Dialog(Formulario_Positiva.this);
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

    private void updateLabel() {

        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        sfecha = sdf.format(myCalendar.getTime());
        Log.e("HORAAAA", sdf.format(myCalendar.getTime()));
        displayDate();
    }

    /*evento para el boton BACK*/
    @Override
    public void onBackPressed() {
        dialog = new Dialog(Formulario_Positiva.this);
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

    /**********************
     * BACK
     ********************************/

    private int mYear, mMonth, mDay, mHour, mMinute;

    //**genera el formulario de acuerdo a la BD
    public void generarFormulario(final List<Formulario> datos_verificacion) {

        for (int i = 0; i < datos_verificacion.size(); i++) {

            if (datos_verificacion.get(i).getVisible().equals("1")) {

                String pregunta = datos_verificacion.get(i).getPregunta();
                TextView tv_pregunta = new TextView(getApplicationContext());
                tv_pregunta.setText(pregunta);
                tv_pregunta.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                tv_pregunta.setTextSize(20);
                tv_pregunta.setTypeface(null, Typeface.BOLD);
                parent.addView(tv_pregunta);
                lst_spinner.add(null);
                lst_adapter.add(null);
                String tipo = datos_verificacion.get(i).getTipo();
                txt_respuesta = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);

                txt_respuesta_date.setInputType(InputType.TYPE_CLASS_DATETIME);
                switch (tipo) {
                    case ("1"):
                        final Spinner respuestas = (Spinner) getLayoutInflater().inflate(R.layout.spinner_verif, null);
                        String[] opciones = datos_verificacion.get(i).getOpciones().split("-");
                        String[] idopciones = datos_verificacion.get(i).getIdopciones().split("-");
                        String[] dependientes = datos_verificacion.get(i).getDependientes().split("-");

                        final List<Opcion> lst_opciones = new ArrayList<>();
                        for (int j = 0; j < opciones.length; j++) {
                            String value = datos_verificacion.get(i).getId().toString() + "|" + datos_verificacion.get(i).getTipo() + "|" + idopciones[j];
                            Opcion op = new Opcion(value, opciones[j], dependientes[j]);
                            Log.e("SITU ERROR: ", datos_verificacion.get(i).getId().toString() + "|" + datos_verificacion.get(i).getTipo() + "|" + idopciones[j]);
                            lst_opciones.add(op);
                        }

                        Opcion[] opciones_f = new Opcion[lst_opciones.size()];
                        opciones_f = lst_opciones.toArray(opciones_f);
                        adapter = new SpinAdapter(Formulario_Positiva.this,
                                android.R.layout.simple_spinner_item,
                                opciones_f);
                        respuestas.setAdapter(adapter);
                        selected_item = respuestas.getSelectedItem().toString();
                        lst_adapter.add(adapter);
                        lst_spinner.add(respuestas);
                        Log.e("SITU SELECTED: ", "pos: " + respuestas.getSelectedItemPosition());
                        parent.addView(respuestas);
                        break;

                    case ("3"):
                        txt_respuesta.setTag(datos_verificacion.get(i).getId() + "|" + datos_verificacion.get(i).getTipo());
                        parent.addView(txt_respuesta);
                        lst_spinner.add(null);
                        lst_adapter.add(null);
                        break;

                    case ("4"):
                        txt_respuesta.setTag(datos_verificacion.get(i).getId() + "|" + datos_verificacion.get(i).getTipo());
                        txt_respuesta.setInputType(InputType.TYPE_CLASS_NUMBER);
                        parent.addView(txt_respuesta);
                        lst_spinner.add(null);
                        lst_adapter.add(null);
                        break;

                    case ("5"):
                        parent.addView(date_layout);
                        txt_respuesta_date.setTag(datos_verificacion.get(i).getId() + "|" + datos_verificacion.get(i).getTipo());
                        txt_respuesta_date.setFocusable(false);
                        //txt_respuesta_date.setText("No indica");
                        txt_respuesta_date.setLayoutParams(paramb);
                        date_layout.addView(txt_respuesta_date);
                        Button date_button = new Button(getApplicationContext());
                        date_button.setText("ELEGIR");
                        date_button.setBackgroundResource(R.drawable.rectangulo_login);
                        date_button.setTextColor(Color.WHITE);
                        date_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar c = Calendar.getInstance();
                                mYear = c.get(Calendar.YEAR);
                                mMonth = c.get(Calendar.MONTH);
                                mDay = c.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(Formulario_Positiva.this,
                                        new DatePickerDialog.OnDateSetListener() {

                                            @Override
                                            public void onDateSet(DatePicker view, int year,
                                                                  int monthOfYear, int dayOfMonth) {
                                                txt_respuesta_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                                Log.e("SITU DATE: ", datee);
                                            }
                                        }, mYear, mMonth, mDay);
                                datePickerDialog.show();
                            }
                        });

                        // parent.addView(txt_respuesta_date);
                        date_button.setLayoutParams(paramet);
                        date_layout.addView(date_button);
                        lst_spinner.add(null);
                        lst_adapter.add(null);
                        break;
                }
            }
        }

        TextView tv_pregunta_com = new TextView(getApplicationContext());
        tv_pregunta_com.setText("Comentarios");
        tv_pregunta_com.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        tv_pregunta_com.setTextSize(20);
        tv_pregunta_com.setTypeface(null, Typeface.BOLD);
        parent.addView(tv_pregunta_com);

        EditText txt_respuesta_com = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
        parent.addView(txt_respuesta_com);

        n_fotos = Integer.parseInt(datos_verificacion.get(0).getN_foto_positiva());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
        layoutParams.setMargins(0, 10, 0, 0);
        layout_fotos = new LinearLayout(getApplicationContext());
        layout_fotos.setOrientation(LinearLayout.VERTICAL);
        parent.addView(layout_fotos);
        for (int j = 1; j <= n_fotos; j++) {
            final int nro_foto = j;

            button_foto = new ImageButton(getApplicationContext());
            // button_foto.setId(j);
            button_foto.setBackgroundResource(R.drawable.boton_camara);
            button_foto.setScaleType(ImageView.ScaleType.FIT_XY);
            button_foto.setLayoutParams(layoutParams);
            button_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sacarfoto(nro_foto);
                }
            });
            layout_fotos.addView(button_foto);
        }

        firma = datos_verificacion.get(0).getFirma();

        LinearLayout.LayoutParams layoutParamsFirma = new LinearLayout.LayoutParams(350, 150);
        layoutParamsFirma.setMargins(0, 10, 0, 0);
        layoutParamsFirma.gravity= Gravity.CENTER;

        if (firma.equals("1")){

            button_firma = new ImageButton(getApplicationContext());
            button_firma.setBackgroundResource(R.drawable.boton_firma);
            button_firma.setScaleType(ImageView.ScaleType.CENTER_CROP);
            button_firma.setLayoutParams(layoutParamsFirma);
            button_firma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Formulario_Positiva.this, CaptureSignature.class);
                    intent.putExtra("id_ver", id_ver.toString());
                    startActivityForResult(intent,SIGNATURE_ACTIVITY);
                }
            });
            parent.addView(button_firma);
        }
       // Log.e("SITU ERROR OBJ: ", lst_spinner.toString());
       // Log.e("SITU ERROR ADP: ", lst_adapter.toString());
        Log.e("SITU ERROR CHILDS: ", parent.getChildCount()+"");
        controlador = 0;
        for (int q = 0; q < parent.getChildCount(); q++) {
            View v = parent.getChildAt(q);
            //Log.e("SITU ERROR OBJ: ", lst_spinner.toString());
            //Log.e("SITU ERROR ADP: ", lst_adapter.toString());
            Log.e("SITU ERROR CHILDS: ", parent.getChildCount()+"");
            Log.e("SITU ERROR VIEW nro: ", ""+q);
            Log.e("SITU ERROR VIEW: ", v.toString());
            System.out.println("MEL:"+ Arrays.toString(lst_adapter.toArray()));
             if (v instanceof Spinner) {
                int check = 0;
                Spinner res = (Spinner) v;
                Log.e("SITU ERROR CONTAR: ", (q - 4)+"");
                if(tipo_ver.equals("1"))
                    api = lst_adapter.get(q - 4);
                else
                    api = lst_adapter.get(q - 6);
// Log.e("SITU", ap.getItem(1).getValor().toString());
                //res.setAdapter(ap);

                for (int i = 0; i < api.getCount(); i++) {
                    if (!api.getItem(i).getDependientes().equals("0")) {
                        check = 1;
                    }
                }

                if (check == 1) {
                    final int pos = q;
                    final TextView pregunta_especial = new TextView(getApplicationContext());
                    final EditText respuesta_especial = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
                    final Spinner respuesta_especial_sp = (Spinner) getLayoutInflater().inflate(R.layout.spinner_verif, null);


                    final List<Opcion> lst_opciones = new ArrayList<>();
                    parent.addView(pregunta_especial, pos + 1);
                    lst_spinner.add(q-3,null);
                    lst_adapter.add(q-3,null);
                    parent.addView(respuesta_especial, pos + 2);
                    lst_spinner.add(q-2,null);
                    lst_adapter.add(q-2,null);
                    respuesta_especial.setText("No procede");
                    respuesta_especial.setTag(0 + "|" + 0);
                    respuesta_especial.setVisibility(View.GONE);
                    pregunta_especial.setVisibility(View.GONE);
                    res.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        SpinAdapter fin = api;

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long id) {
                            // Here you get the current item (a User object) that is selected by its position
                            Log.e("SITU PROBANDO: ", fin.getItem(position).getValor()+" "+fin.getItem(position).getDependientes());
                            Log.e("SITU ERROR OBJ: ", lst_spinner.toString());
                            Log.e("SITU ERROR ADP: ", lst_adapter.toString());

                            if(!fin.getItem(position).getDependientes().equals("0")) {
                                Formulario get = fu.getFormulario(fin.getItem(position).getDependientes(), datos_verificacion);
                                pregunta_especial.setText(get.getPregunta());
                                pregunta_especial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                pregunta_especial.setTextSize(20);
                                pregunta_especial.setTypeface(null, Typeface.BOLD);
                                String tipo_v = get.getTipo();

                                if(tipo_v.equals("1")) {
                                    parent.removeView(respuesta_especial);
                                    parent.addView(respuesta_especial_sp, pos+2);
                                    String[] opciones = get.getOpciones().split("-");
                                    String[] idopciones = get.getIdopciones().split("-");
                                    String[] dependientes = get.getDependientes().split("-");

                                    final List<Opcion> lst_opciones = new ArrayList<>();
                                    for (int j = 0; j < opciones.length; j++) {
                                        String value = get.getId().toString() + "|" + get.getTipo() + "|" + idopciones[j];
                                        Opcion op = new Opcion(value, opciones[j], dependientes[j]);
                                        Log.e("SITU ERROR: ", get.getId().toString() + "|" + get.getTipo() + "|" + idopciones[j]);
                                        lst_opciones.add(op);
                                    }

                                    Opcion[] opciones_f = new Opcion[lst_opciones.size()];
                                    opciones_f = lst_opciones.toArray(opciones_f);
                                    adapter = new SpinAdapter(Formulario_Positiva.this,
                                            android.R.layout.simple_spinner_item,
                                            opciones_f);
                                    respuesta_especial_sp.setAdapter(adapter);
                                    selected_item = respuesta_especial_sp.getSelectedItem().toString();
                                    lst_spinner.remove(pos-2);
                                    lst_adapter.remove(pos-2);
                                    lst_spinner.add(pos-2,respuesta_especial_sp);
                                    lst_adapter.add(pos-2,adapter);
                                    Log.e("SITU SELECTED: ", "pos: " + respuesta_especial_sp.getSelectedItemPosition());
                                }
                                else{

                                    respuesta_especial.setTag(get.getId() + "|" + get.getTipo());
                                 //   respuesta_especial.setText("No procede");
                                    respuesta_especial.setText("No indica");
                                    respuesta_especial.setVisibility(View.VISIBLE);
                                }
                                pregunta_especial.setVisibility(View.VISIBLE);
                            }
                            else{
                                Log.e("SITU, ","WEEEA ENTRO");
                                respuesta_especial.setText("No procede");
                                respuesta_especial.setVisibility(View.GONE);
                                pregunta_especial.setVisibility(View.GONE);
                            }
                            //final_value = ap.getItem(position).getID();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapter) {
                        }
                    });
                }
            }
        }
    }


    private class MyOnDateChangedListener implements DatePicker.OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            txt_respuesta_date.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
        }
    }

    private void displayDate() {
        txt_respuesta_date.setText("" + fecha);
    }

    private ProgressDialog progressDialog;

    //*****ENVIAR PARA Q SE DESERIALIZE EN EL PHP
    public void enviarFormulario() {
        progressDialog = new ProgressDialog(Formulario_Positiva.this);
        progressDialog.setMessage("Enviando Verificación....");
        progressDialog.show();


        vacio = 0;
        int childs = parent.getChildCount();
        Log.e("CHILDS", "" + childs + "  " + n_fotos);
        List<String> lst_preguntas = new ArrayList<>();
        List<String> lst_respuestas = new ArrayList<>();

        int n_keys = 0;
        for (int i = 0; i < childs; i++) {
            View v = parent.getChildAt(i);
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                String final_et;
                if (et.getText().toString().trim().length() == 0) {
                    vacio++;
                }
                if (et.getTag() != null)
                    final_et = et.getTag() + "|" + et.getText().toString();
                else
                    final_et = et.getText().toString();

                lst_respuestas.add(final_et);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                String tv_final = tv.getText().toString().replace(" ", "_").toLowerCase();
                lst_preguntas.add(tv_final);
            } else if (v instanceof LinearLayout) {
                LinearLayout ly = (LinearLayout) v;
                Log.e("SITU: ", "ENTRO AQUI " + i + " " + ly.getChildCount());

                if (ly.getChildAt(0) instanceof EditText) {
                    Log.e("SITU: ", "ENTRO AQUI2" + i);
                    String final_et;
                    final_et = txt_respuesta_date.getTag() + "|" + txt_respuesta_date.getText().toString();
                    lst_respuestas.add(final_et);
                }
            } else if (v instanceof Spinner) {
                //Spinner sp=lst_spinner.get(i-6);
                if(tipo_ver.equals("1"))
                    ap = lst_adapter.get(i - 4);
                else
                    ap = lst_adapter.get(i - 6);

                Spinner sp = (Spinner) v;
                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        // Here you get the current item (a User object) that is selected by its position
                        Log.e("SITU PROBANDO: ", "ENTRO!!!!!!!!!!");
                        final_value = ap.getItem(position).getID();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });
              //  Log.e("SITU ERROR: ", i + " pos: " + sp.getSelectedItemPosition());
              //  Log.e("SITU ERROR: ", i + " item: " + ap.getItem(sp.getSelectedItemPosition()).getID());
                final_value = ap.getItem(sp.getSelectedItemPosition()).getID();
                lst_respuestas.add(final_value);
            }
            Log.e("SITU ERROR VIEW: ", v.toString());
            Log.e("SITU ERROR VACIO: ", vacio+"");


            //          Log.e("SITU: ", "LISTA "+i+ lst_respuestas.get(i).toString());
        }
        if (vacio != 0) {

            progressDialog.dismiss();

            dialog2 = new Dialog(Formulario_Positiva.this);
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog2.setContentView(R.layout.dialog_vacio);
            dialog2.show();

            Button con = (Button) dialog2.findViewById(R.id.cancelar_boton);

            con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                }
            });


        } else {
        JsonObject innerObject = new JsonObject();
        Log.e("TAMANO!! ", "" + lst_preguntas.size());
        Log.e("TAMANO!! ", "" + lst_respuestas.size());

        for (int k = 0; k < lst_preguntas.size(); k++) {
            Log.e("INDICE!! ", "" + k + "   " + lst_respuestas.get(k).toString());
            innerObject.addProperty(lst_preguntas.get(k).toString(), lst_respuestas.get(k).toString());
        }

        JsonObject jsono = new JsonObject();
        jsono.add("respuestas", innerObject);

        String jsonenviar = new Gson().toJson(lst_respuestas);

        if(firma.equals("1"))
            fotofirma = Environment.getExternalStorageDirectory()
                    + "/Bigsomer_firmas_bgvs/" + id_ver +"_firma" + ".jpg";
        else
            fotofirma = "";

        crearBD = new DBHelper(Formulario_Positiva.this);
        db = crearBD.getWritableDatabase();
        ContentValues values1 = new ContentValues();
        values1.put("id_fr", id_ver);
        values1.put("lat", lat);
        values1.put("lon", lon);
        values1.put("fecha_realizada", fecha_actual);
        values1.put("tipo_ver", tipo_ver);
        values1.put("acceso_cliente", acceso_cliente);
        values1.put("fotos", fotos.toString());
        values1.put("firma",fotofirma);
        values1.put("respuestas", jsonenviar.toString());
        values1.put("estado", "pendiente");
        db.insert("formulario_respuestas", null, values1);

        //fu.eliminar_tarea(getApplicationContext(),id_ver);
        System.out.println("id_ver: " + id_ver + " fotos: " + fotos.toString() + "cosota: " + jsonenviar.toString() + " LA COSA Q IRA EN LA OTRA COSA " + jsono);
        System.out.println("LAT: " + lat + " LON: " + lon);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_ver", id_ver);
        map.put("tipo_ver", tipo_ver);
        map.put("acceso_cliente", acceso_cliente);
        map.put("lat", lat + "");
        map.put("lon", lon + "");
        map.put("respuestas", jsonenviar.toString());
        map.put("fecha_realizada", fecha_actual);

        JSONObject jobject = new JSONObject(map);

            if(fu.check_connection(getApplicationContext())) {

                Log.e("SITU ENVIAR: ", jobject.toString());
                // Actualizar datos en el servidor
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                VariablesURL.INSERT,
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
            }else{

                Toast.makeText(getApplicationContext(),"Hay un problema, con el servidor por favor inténtelo más tarde.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"Si este error persiste, por favor comuníquelo a su supervisor.",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
                finish();
                startActivity(i);
                db.close();
            }
        }
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
                    // Enviar código de éxito
                    //getActivity().setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    //getActivity().finish();
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
            new Guardarfotos().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int q_foto;
    static final int REQUEST_TAKE_PHOTO = 1;

    public void sacarfoto(int nro_foto) {

        q_foto = nro_foto - 1;

        nombre_foto = id_ver + "_" + nro_foto;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        nombre_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + "_" + nro_foto + ".jpg";
        File fot = new File(nombre_foto);
        //Hay camara?????
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            // Continue only if the File was successfully created
            if (fot != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fot));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                control_foto = true;
            }
        }
    }

    private ProgressDialog pDialog2;
    private String resultado2 = "problemas";
    VariablesURL variables = new VariablesURL();

    class Guardarfotos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(Formulario_Positiva.this);
            pDialog2.setMessage("Enviando fotos...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(Formulario_Positiva.this);
            db = crearBD.getWritableDatabase();
            ContentValues values1 = new ContentValues();
            values1 = new ContentValues();


            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //ENVIAR DATOS A LA NUBE
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(VariablesURL.INSERT_FOTOS);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                //SUBIR LAS FOTOS
                try {
                    for (int i = 0; i < fotos.size(); i++) {

                        File file = new File(fotos.get(i));
                        entityBuilder.addBinaryBody("fotoUp", file);

                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        java.lang.String respuestaWeb = EntityUtils.toString(response
                                .getEntity());

                        System.out.println("La web me muestra+ ////////////  " + respuestaWeb);
                    }

                    if(firma.equals("1")){
                        File file = new File(fotofirma);
                        entityBuilder.addBinaryBody("fotoUp", file);

                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        java.lang.String respuestaWeb = EntityUtils.toString(response
                                .getEntity());

                        System.out.println("La web me muestra+ FIRMA////////////  " + respuestaWeb);
                    }

                    httpclient.getConnectionManager().shutdown();
                    resultado2 = "ok";
                    ContentValues values2 = new ContentValues();
                    values2.put("estado", "subida");
                    db.update("formulario_respuestas", values2, "id_fr" + " = ?", new String[]{id_ver});



                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Conexion", "NO");
                }
                //resultado2 = "ok";
            } catch (Exception e) {

                resultado2 = "problemas";
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog2.dismiss();
            //		pushDB();
            if (resultado2.equals("ok"))
                Toast.makeText(getApplicationContext(), "Verificación subida correctamente", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "No se subió la verificación, por favor intente SINCRONIZAR más tarde", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(), MenuPrincipal.class);
            finish();
            startActivity(i);
            db.close();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //displayDate();
        if (resultCode == Activity.RESULT_OK
                && resultCode != Formulario_Positiva.this.RESULT_CANCELED) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                new OperacionesFoto().execute();
            } else {
                Toast.makeText(Formulario_Positiva.this,
                        "Error con la foto, toma una de nuevo",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Formulario_Positiva.this, "No se ha realizado la foto",
                    Toast.LENGTH_SHORT).show();
        }
    }


    class OperacionesFoto extends AsyncTask<String, String, String> {
        File file = new File(nombre_foto);
        private ProgressDialog DialogFoto;
        private boolean error_foto = false;
        Uri uri;

        protected void onPreExecute() {
            super.onPreExecute();
            DialogFoto = new ProgressDialog(Formulario_Positiva.this);
            DialogFoto
                    .setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            DialogFoto.setMessage("Comprimiendo foto. Espere por favor...");
            DialogFoto.show();
        }

        protected String doInBackground(String... args) {
            // Muestra la foto tomada.
            try {
                System.gc();
                Bitmap bit_ = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeFile(nombre_foto), 400, 300, false);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bit_.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                    uri = Uri.fromFile(new File(nombre_foto));
                    Log.i("Comprimiendo imagen", "si");
                } catch (Exception e) {
                    Log.i("Comprimiendo imagen", "no");
                    error_foto = true;
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                error_foto = true;
                Log.i("Foto recibida?", "no");
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                error_foto = true;
                Log.i("Out of memory?", "Sip");
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            boolean error_volviendo = false;
            if (DialogFoto.isShowing()) {
                try {
                    DialogFoto.dismiss();

                } catch (Exception e) {
                    error_volviendo = true;
                    Log.e("Error en la ventana", "Error aca");
                }
            }
            if (error_volviendo == false) {
                if (error_foto == false) {
                    Log.e("FOTO CAMARA AQUI==", "ENTRA!!" + q_foto);

                    View ve = layout_fotos.getChildAt(q_foto);
                    if (!fotos.contains(nombre_foto)) {
                        fotos.add(nombre_foto);
                    }

                    ImageButton ib = (ImageButton) ve;
                    ib.setImageBitmap(BitmapFactory
                            .decodeFile(nombre_foto));
                    // Picasso.with(getApplicationContext()).load(uri).resize(400,300).centerCrop().into(ib);
                } else {
                    Toast.makeText(
                            Formulario_Positiva.this,
                            "Ocurrió un error al tomar la foto,tome una foto nueva",
                            Toast.LENGTH_SHORT).show();
                    file.delete();
                }
            } else {
                file.delete();
            }
        }
    }

    public boolean isEmpty() {
        boolean vacio = false;

        for (int i = 0; i <= parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof EditText) {

            }
        }
        return vacio;
    }
}
