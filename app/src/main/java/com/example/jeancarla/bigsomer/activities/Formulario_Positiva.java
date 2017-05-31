package com.example.jeancarla.bigsomer.activities;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Formulario_Positiva extends AppCompatActivity {

    //Datos que recibiremos de actividad anterior
    private String acceso_cliente, tipo_ver, id_ver;

    //VIEWS
    private LinearLayout parent, layoutFotos, layoutDate;
    private EditText etRespuesta, etRespuestaDate;
    private ImageView ivLocation;
    private ImageButton btnFoto, btnFirma;
    private TextView txtPdfLat;
    private TextView txtCargo;
    private EditText etCargo;


    //Variables para las fotos
    private int nro_fotos;
    private List<String> lstFotos;
    private String direccion_foto;

    //Variables para la firma
    private String firma;
    private String direccion_foto_firma;
    public static final int SIGNATURE_ACTIVITY = 1;


    //Base de Datos
    DBHelper crearBD;
    private SQLiteDatabase db;

    private Funciones fu = new Funciones();
    
    // obtiene fecha actual
    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String fecha_actual;

    //Para los Spinner
    private List<SpinAdapter> lstAdapter = new ArrayList<>();
    private List<Spinner> lstSpinner = new ArrayList<>();
    private SpinAdapter spinAdapterParaEnviar, spinAdapterParaGenerar, spinAdapterPrincipal;
    private String valor_final_spinner;

    //Para localización
    private double lat = 0;
    private double lon = 0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    private Dialog dialogNotificarVacio;
    private int verificar_campos_vacio;

    //Parámetros para los LinearLayout
    LinearLayout.LayoutParams paramb = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0.3f);
    LinearLayout.LayoutParams paramet = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0.7f);
    LinearLayout.LayoutParams paramsp = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_positiva);

        acceso_cliente = getIntent().getStringExtra("cliente");
        tipo_ver = getIntent().getStringExtra("tipo");
        id_ver = getIntent().getStringExtra("id_ver");

        txtPdfLat = (TextView) findViewById (R.id.txt_lat_lon);
        parent = (LinearLayout) findViewById(R.id.parent);

        lstFotos = new ArrayList<>();

        txtCargo = (TextView) findViewById(R.id.cargo_static);
        etCargo = (EditText) findViewById(R.id.v_cargo);

        //Comprobar si es Domiciliar o Vivienda Social
        //Si es así eliminamos el campo "Cargo"
        if (tipo_ver.equals("1") || acceso_cliente.equals("21")) {
            parent.removeView(txtCargo);
            parent.removeView(etCargo);
        }

        paramsp.setMargins(0,0,0,20);
        //******************LOCATION THINGS

        ivLocation = (ImageView) findViewById(R.id.iv_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                //Cambia el texto con las cordenadas agarradas
                txtPdfLat.setText("LAT: "+lat+" / LON: "+lon);
                //Cambia la imagen para indicar que existen cordenadas
                ivLocation.setImageResource(R.drawable.ic_location_on_black_24dp);
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
        //Crea la dirección en la que serán guardadas y su nombre
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath()
                + "/Bigsomer_aver");
        if (!directory.isDirectory())
            directory.mkdirs();
        direccion_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + ".jpg";
        //FOTOS**************

        etRespuestaDate = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
        //Obtiene la fecha actual
        fecha_actual = fecha.format(fechaYhora.getTime());
        System.out.println(fecha_actual);
        etRespuestaDate.setText("No indica");

        //Abrir Base de Datos
        DBHelper admin = new DBHelper(getApplicationContext());
        SQLiteDatabase bd = admin.getWritableDatabase();

        //OBTENER EL FORMULARIO(PREGUNTAS, TIPOS DE PREGUNTAS,OPCIONES) DEL CLIENTE Y TIPO DE VERIF. CORRESPONDIENTE
        //Agarra un cursor y lo guarda en una lista de "Formnularios"
        int i = 0;
        List<Formulario> lstPreguntas = new ArrayList<Formulario>();
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

                lstPreguntas.add(formulario[i]);
                i++;
            } while (fila.moveToNext());

            bd.close();
        } else {
            Log.e("ERROR FETCHING ", "oooooops");
            bd.close();
        }

        layoutDate = new LinearLayout(getApplicationContext());
        layoutDate.setOrientation(LinearLayout.HORIZONTAL);

    if(!lstPreguntas.isEmpty())
        //Si es que no está vacía se genera el formulario
        generarFormulario(lstPreguntas);
        //Toolbar
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

    private Dialog dialogEnviarVerificacion;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //Botón en el Menú para enviar la verificación
        if (id == R.id.action_enviar) {

            dialogEnviarVerificacion = new Dialog(Formulario_Positiva.this);
            dialogEnviarVerificacion.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogEnviarVerificacion.setContentView(R.layout.dialog_nuevo);
            dialogEnviarVerificacion.show();
            TextView txtDialogTitulo = (TextView) dialogEnviarVerificacion.findViewById(R.id.info_text);
            TextView txtDialogInfo = (TextView) dialogEnviarVerificacion.findViewById(R.id.info_text2);
            TextView txtDialogInfo2 = (TextView) dialogEnviarVerificacion.findViewById(R.id.info_text3);

            Button btnDialogEnviar = (Button) dialogEnviarVerificacion.findViewById(R.id.enviar_boton);
            Button btnDialogCancelar = (Button) dialogEnviarVerificacion.findViewById(R.id.cancelar_boton);

            //Verificar si agarro Latitud y longitud
            if (lat != 0 && lon != 0) {
                //Verifica si hay las lstFotos requeridas
                if (nro_fotos == lstFotos.size()) {
                    //Crear diálogo preguntando si el verificador está seguro de enviar la tarea
                    txtDialogTitulo.setText("ENVIAR VERIFICACIÓN");

                    txtDialogInfo.setText("¿Está seguro de querer enviar el formulario? Si está seguro presione ENVIAR");

                    txtDialogInfo2.setVisibility(View.GONE);

                    btnDialogEnviar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Procedemos a enviar Formulario
                            enviarFormulario();
                            dialogEnviarVerificacion.dismiss();
                        }
                    });

                    btnDialogCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogEnviarVerificacion.dismiss();
                        }
                    });

                    return true;
                } else {
                    //Crea diálogo indicando que no se cuenta con las lstFotos requeridas
                    txtDialogTitulo.setText("NO SE ENCONTRARON LAS FOTOS REQUERIDAS");

                    txtDialogInfo.setText("La presente verificación necesita de " + nro_fotos + " fotografías para poder ser enviada. Asegúrese de haber sacado todas las Fotos o de tener espacio suficiente en la memoria del celular");

                    txtDialogInfo2.setVisibility(View.GONE);

                    btnDialogEnviar.setVisibility(View.GONE);

                    btnDialogCancelar.setText("OK");
                    btnDialogCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogEnviarVerificacion.dismiss();
                        }
                    });

                    return true;
                }
            } else {
                //Crea diálogo indicando que no se cuenta con las coordenadas
                txtDialogTitulo.setText("NO SE ENCONTRARON COORDENADAS");

                txtDialogInfo.setText("No se obtuvieron coordenadas geográficas. Asegúrese de estar al aire libre y vuelva a intentarlo");

                txtDialogInfo2.setVisibility(View.GONE);

                btnDialogEnviar.setVisibility(View.GONE);

                btnDialogCancelar.setText("OK");
                btnDialogCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogEnviarVerificacion.dismiss();
                    }
                });

                return true;
            }

        }
        //Botón para volver atrás
        else {
            dialogEnviarVerificacion = new Dialog(Formulario_Positiva.this);
            dialogEnviarVerificacion.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogEnviarVerificacion.setContentView(R.layout.dialog_two);
            dialogEnviarVerificacion.show();

            Button btnDialogSi = (Button) dialogEnviarVerificacion.findViewById(R.id.si_boton);
            btnDialogSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Button btnDialogNo = (Button) dialogEnviarVerificacion.findViewById(R.id.no_boton);
            btnDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogEnviarVerificacion.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    /*evento para el boton BACK*/
    @Override
    public void onBackPressed() {
        dialogEnviarVerificacion = new Dialog(Formulario_Positiva.this);
        dialogEnviarVerificacion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnviarVerificacion.setContentView(R.layout.dialog_two);
        dialogEnviarVerificacion.show();

        Button btnDialogSi = (Button) dialogEnviarVerificacion.findViewById(R.id.si_boton);
        btnDialogSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnDialogNo = (Button) dialogEnviarVerificacion.findViewById(R.id.no_boton);
        btnDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEnviarVerificacion.dismiss();
            }
        });
    }

    /**********************
     * BACK
     ********************************/

    //Datos para seleccionar fecha si es que se necesita
    private int mYear, mMonth, mDay;

    //**GENERA EL FORMULARIO CON EL "FORMULARIO" QUE AGARRAMOS DE LA BD
    public void generarFormulario(final List<Formulario> lstPreguntas) {

        //Agregamos un NULL en la lista de Spinners y Adapters
        //Porque luego necesitaremos estos datos para recolectar los datos
        //Al principio agregamos un Null porque el primero es el TextView de las cordenadas
        //Cuando el View NO SEA UN SPINEER agregamos NULL A ESTAS LISTAS
        lstSpinner.add(null);
        lstAdapter.add(null);
        //Recorrer la lista de Formularios
        for (int i = 0; i < lstPreguntas.size(); i++) {
            //Verificamos si es Visible la pregunta
            //Cuando no sea visible el estado sera "0"
            //Eso pasa cuando esa rpegunta es habilitada con otra pregunta.
            if (lstPreguntas.get(i).getVisible().equals("1")) {
                //Obtener Pregunta
                String pregunta = lstPreguntas.get(i).getPregunta();
                //Crear un texView con la pregunta y darle su formato
                TextView txtPregunta = new TextView(getApplicationContext());
                txtPregunta.setText(pregunta);
                txtPregunta.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                txtPregunta.setTextSize(20);
                txtPregunta.setTypeface(null, Typeface.BOLD);
                //Agregamos el TextView al Layout Parent
                parent.addView(txtPregunta);
                //Como dije arriba agregamos un NULL a la lista de spinners y adapters porque este VIEW no es SPINENR
                lstSpinner.add(null);
                lstAdapter.add(null);
                //Obtenemos el tipo de la respuesta
                //(Pregunta abierta, Numeral, para introducir Fecha o Spinner)
                String tipo = lstPreguntas.get(i).getTipo();
                //Crea el EditText en caso de que sea abierta
                etRespuesta = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
                //Crea el EditText en caso de que olo obtenga fechas
                etRespuestaDate.setInputType(InputType.TYPE_CLASS_DATETIME);
                //Depende de que TIPO es
                switch (tipo) {
                    //"1" es Spinner
                    case ("1"):
                        //Crea Spinner
                        final Spinner spRespuestas = (Spinner) getLayoutInflater().inflate(R.layout.spinner_verif, null);
                        //Crea un vector con las Opciones
                        //Las opciones son devueltas de esta manera
                        //Por ej: "si-no-tal vez"
                        //Las separa por el guión y guarda todos esos datos
                        String[] opciones_para_spinner = lstPreguntas.get(i).getOpciones().split("-");
                        //Crea un vector con el ID de las Opciones
                        //Similar a lo de arriba
                        String[] idopciones_para_spinner = lstPreguntas.get(i).getIdopciones().split("-");
                        //Crea un vector con las Dependeintes de esa Respuesta
                        //en caso de que no tenga dependientes este valor es 0
                        //si es que lo tiene, nos da el numero de la pregunta que es abierta con esta opción
                        String[] dependientes_para_spinner = lstPreguntas.get(i).getDependientes().split("-");

                        //Generamos una lista de "Opciones"
                        final List<Opcion> lstOpciones = new ArrayList<>();
                        for (int j = 0; j < opciones_para_spinner.length; j++) {
                            //Crea un String con el ID|Tipo|Opción
                            String value = lstPreguntas.get(i).getId().toString() + "|" + lstPreguntas.get(i).getTipo() + "|" + idopciones_para_spinner[j];
                            //Crea un Objeto "Opción" con los mismos datos de arriba
                            //Esto lo hacemos porque lo necestiamos para crear el Adapter
                            Opcion op = new Opcion(value, opciones_para_spinner[j], dependientes_para_spinner[j]);
                            Log.e("SITU ERROR: ", lstPreguntas.get(i).getId().toString() + "|" + lstPreguntas.get(i).getTipo() + "|" + idopciones_para_spinner[j]);
                            //Adiciona este Objeto a la Lista
                            lstOpciones.add(op);
                        }

                        //Crea un Vector del tamaño de la Lista de "Opciones"
                        Opcion[] vectorOpciones = new Opcion[lstOpciones.size()];
                        //Cambia de Lista a Vector
                        vectorOpciones = lstOpciones.toArray(vectorOpciones);
                        //Crea el Adapter para el Spinner con el Objeto "Opciones"
                        //Esto hará que se muestre la opción, pero por atrás tenemos el ID de esa opción y su tipo
                        spinAdapterPrincipal = new SpinAdapter(Formulario_Positiva.this,
                                android.R.layout.simple_spinner_item,
                                vectorOpciones);
                        spRespuestas.setAdapter(spinAdapterPrincipal);
                        //Adicionamos el Spinneradapter y el Spinner a la lista de Adapters y Spinners
                        lstAdapter.add(spinAdapterPrincipal);
                        lstSpinner.add(spRespuestas);
                        Log.e("SITU SELECTED: ", "pos: " + spRespuestas.getSelectedItemPosition());
                        //agrega los parámetros hechos para el Spinner
                        spRespuestas.setLayoutParams(paramsp);
                        //Adicionar el Spinner al Layout Parent
                        parent.addView(spRespuestas);
                        break;

                //"3" es pregunta abierta
                    case ("3"):
                        //Creamos un TAG exclusivo para este TexView con su ID y TIPO
                        //Esto nos sirve para enviar los datos posteriormente
                        etRespuesta.setTag(lstPreguntas.get(i).getId() + "|" + lstPreguntas.get(i).getTipo());
                        //Adcionar el textView al Layout Parent
                        parent.addView(etRespuesta);
                        //Como dije arriba agregamos un NULL a la lista de spinners y adapters porque este VIEW no es SPINENR
                        lstSpinner.add(null);
                        lstAdapter.add(null);
                        break;
                //"4" es una pregunta abierta pero con opción solo de números
                    case ("4"):
                        //Creamos un TAG exclusivo para este TexView con su ID y TIPO
                        //Esto nos sirve para enviar los datos posteriormente
                        etRespuesta.setTag(lstPreguntas.get(i).getId() + "|" + lstPreguntas.get(i).getTipo());
                        etRespuesta.setInputType(InputType.TYPE_CLASS_NUMBER);
                        //Adcionar el textView al Layout Parent
                        parent.addView(etRespuesta);
                        //Como dije arriba agregamos un NULL a la lista de spinners y adapters porque este VIEW no es SPINENR
                        lstSpinner.add(null);
                        lstAdapter.add(null);
                        break;
                //"5" es opción de fecha
                    case ("5"):
                        //Adicionamos el Layout para las fechas al Layout Parent
                        parent.addView(layoutDate);
                        //Creamos un TAG exclusivo para este TexView con su ID y TIPO
                        //Esto nos sirve para enviar los datos posteriormente
                        etRespuestaDate.setTag(lstPreguntas.get(i).getId() + "|" + lstPreguntas.get(i).getTipo());
                        //Hacemos que no se pueda escribir en este TextView
                        etRespuestaDate.setFocusable(false);
                        //Agregamos los parámetros
                        etRespuestaDate.setLayoutParams(paramb);
                        //Adcionar el textView al Layout de la Fecha
                        layoutDate.addView(etRespuestaDate);
                        //Creamos un botón para abrir un diálogo que tiene un calendario
                        //para elegir la fecha
                        Button btnSeleccionarFecha = new Button(getApplicationContext());
                        btnSeleccionarFecha.setText("ELEGIR");
                        btnSeleccionarFecha.setBackgroundResource(R.drawable.rectangulo_login);
                        btnSeleccionarFecha.setTextColor(Color.WHITE);
                        btnSeleccionarFecha.setOnClickListener(new View.OnClickListener() {
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
                                                etRespuestaDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                            }
                                        }, mYear, mMonth, mDay);
                                datePickerDialog.show();
                            }
                        });
                        btnSeleccionarFecha.setLayoutParams(paramet);
                        //Adcionar el Botón al Layout de la Fecha
                        layoutDate.addView(btnSeleccionarFecha);
                        //Como dije arriba agregamos un NULL a la lista de spinners y adapters porque este VIEW no es SPINENR
                        lstSpinner.add(null);
                        lstAdapter.add(null);
                        break;
                }
            }
        }

        //Luego de recorrer la lista de Preguntas y Repsuestas del Formulario
        //Agregamos la sección de Comentarios
        //Ya que esta sección siempre estará presente, sin importar el tipo ni el cliente
        //Primero la Pregunta
        TextView txtPreguntaComentarios = new TextView(getApplicationContext());
        txtPreguntaComentarios.setText("Comentarios");
        txtPreguntaComentarios.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        txtPreguntaComentarios.setTextSize(20);
        txtPreguntaComentarios.setTypeface(null, Typeface.BOLD);
        //Adcionar el TextView al Layout Parent
        parent.addView(txtPreguntaComentarios);

        //Después el EditText para llenar los datos
        EditText etRespuestaComentarios = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
        //Adcionar el EditText al Layout Parent
        parent.addView(etRespuestaComentarios);

        //Obtenemos el número de fotografías necesarias según cliente y tipo de verf.
        nro_fotos = Integer.parseInt(lstPreguntas.get(0).getN_foto_positiva());

        //Creamos un linearLayout para las fotografías
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
        layoutParams.setMargins(0, 10, 0, 0);
        layoutFotos = new LinearLayout(getApplicationContext());
        layoutFotos.setOrientation(LinearLayout.VERTICAL);
        //Adicionar el LayoutFotos al Layout Parent
        parent.addView(layoutFotos);

        //Adicionar Botones Para las lstFotos
        for (int j = 1; j <= nro_fotos; j++) {
            final int cual_foto = j;
            //Creamos el Botón para la foto
            btnFoto = new ImageButton(getApplicationContext());
            btnFoto.setBackgroundResource(R.drawable.boton_camara);
            btnFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            btnFoto.setLayoutParams(layoutParams);
            btnFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sacarfoto(cual_foto);
                }
            });
            //Adicionar el Botón para la Foto al Layout Fotos
            layoutFotos.addView(btnFoto);
        }

        //Comprobamos si la verif. necesitará de alguna Firma
        firma = lstPreguntas.get(0).getFirma();
        //Y creamos los parámetros para su VIEW
        LinearLayout.LayoutParams layoutParamsFirma = new LinearLayout.LayoutParams(350, 150);
        layoutParamsFirma.setMargins(0, 10, 0, 0);
        layoutParamsFirma.gravity= Gravity.CENTER;

        //Si la verif. necesita firma su estado es "1"
        if (firma.equals("1")){
            //Creamos botón para la firma
            btnFirma = new ImageButton(getApplicationContext());
            btnFirma.setBackgroundResource(R.drawable.boton_firma);
            btnFirma.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btnFirma.setLayoutParams(layoutParamsFirma);
            btnFirma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pasamos a la Actividad que nos ayudará a capturar la firma
                    Intent intent = new Intent(Formulario_Positiva.this, CaptureSignature.class);
                    intent.putExtra("id_ver", id_ver.toString());
                    startActivityForResult(intent,SIGNATURE_ACTIVITY);
                }
            });
            //Adicionar el Botón Firma al Layout Parent
            parent.addView(btnFirma);
        }
        Log.e("SITU ERROR CHILDS: ", parent.getChildCount()+"");

        //Recorremos el Layout Parent
        for (int posicion_view = 0; posicion_view < parent.getChildCount(); posicion_view++) {
            View v = parent.getChildAt(posicion_view);
            Log.e("SITU ERROR CHILDS: ", parent.getChildCount()+"");
            Log.e("SITU ERROR VIEW nro: ", ""+posicion_view);
            Log.e("SITU ERROR VIEW: ", v.toString());
            //Pues aquí viene lo complicado con los Spinners...
            //Verificamos si el View es un Spinner
             if (v instanceof Spinner) {
                int check = 0;
                Spinner spRespuesta = (Spinner) v;
                 //Si el tipo de Ver es 1 o 21 no necesita de la pregunta de "Cargo de Entrevistado"
                 //por lo que tenemos que ver el SpinnerAdapter correspondiente en ese momento.
                if(tipo_ver.equals("1")||acceso_cliente.equals("21")){
                    Log.e("SITU API: ", lstAdapter.get(posicion_view - 4).toString());
                    spinAdapterParaGenerar = lstAdapter.get(posicion_view - 4);}
                else{
                    Log.e("SITU API: ", lstAdapter.get(posicion_view - 6).toString());
                    spinAdapterParaGenerar = lstAdapter.get(posicion_view - 6);}
                //Analizamos si el Adapter contiene alguna respeusta que tiene dependientes
                 //si lo tiene cambiamos el flag a "1"
                for (int i = 0; i < spinAdapterParaGenerar.getCount(); i++) {
                    Log.e("SITU API: ", spinAdapterParaGenerar.getCount() +"");
                    if (!spinAdapterParaGenerar.getItem(i).getDependientes().equals("0")) {
                        check = 1;
                    }
                    Log.e("SITU CHECK: ",check+"");
                }

                if (check == 1) {
                    //Analizamos la posición para adicionar la pregunta que se abre
                    final int posicion_actual = posicion_view;
                    //Creamos un textView para pregunta, un EditText o un Spinner para ver que tipo es la respuesta
                    final TextView txtPreguntaEspecial = new TextView(getApplicationContext());
                    final EditText etRespuestaEspecial = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
                    final Spinner spRespuestaEspecial = (Spinner) getLayoutInflater().inflate(R.layout.spinner_verif, null);
                    //Adicionar la Pregunta en la posición que corresponde en el Layout Parent
                    parent.addView(txtPreguntaEspecial, posicion_actual + 1);
                    //Necsitamos adicionar este nuevo valor a la lista de Spinners y Adapters
                    //Por eso vemos cual es el tipo de Verif.
                    //Como dije arriba agregamos un NULL a la lista de spinners y adapters porque este VIEW no es SPINENR
                    if(tipo_ver.equals("1")||acceso_cliente.equals("21"))
                    {
                        lstSpinner.add(posicion_view-3,null);
                        lstAdapter.add(posicion_view-3,null);
                    }
                    else{
                        lstSpinner.add(posicion_view-5,null);
                        lstAdapter.add(posicion_view-5,null);
                    }

                    //Adicionar la Respuesta en la posición que corresponde en el Layout Parent
                    parent.addView(etRespuestaEspecial, posicion_actual + 2);
                    //Necsitamos adicionar este nuevo valor a la lista de Spinners y Adapters
                    //Por eso vemos cual es el tipo de Verif.
                    //Como dije arriba agregamos un NULL a la lista de spinners y adapters porque este VIEW no es SPINNER
                    if(tipo_ver.equals("1")||acceso_cliente.equals("21"))
                    {
                        lstSpinner.add(posicion_view-2,null);
                        lstAdapter.add(posicion_view-2,null);
                    }
                    else{
                        lstSpinner.add(posicion_view-4,null);
                        lstAdapter.add(posicion_view-4,null);
                    }

                    //Ponemos un Texto y un Tag por Default
                    etRespuestaEspecial.setText("No procede");
                    etRespuestaEspecial.setTag(0 + "|" + 0);
                    //Los ocultamos hasta confirmar que la opción seleccionada los habilite
                    etRespuestaEspecial.setVisibility(View.GONE);
                    txtPreguntaEspecial.setVisibility(View.GONE);
                    //Analizamos el spinner y le damos un evento al hacerle click
                    spRespuesta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        SpinAdapter spinAdapterFinal = spinAdapterParaGenerar;
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long id) {
                            // Here you get the current item (a User object) that is selected by its position
                            Log.e("SITU PROBANDO: ", spinAdapterFinal.getItem(position).getValor()+" "+ spinAdapterFinal.getItem(position).getDependientes());
                            Log.e("SITU ERROR OBJ: ", lstSpinner.toString());
                            Log.e("SITU ERROR ADP: ", lstAdapter.toString());

                            //analizamos si la opción seleccionada tiene dependientes
                            if(!spinAdapterFinal.getItem(position).getDependientes().equals("0")) {

                                //Obtener Dependientes
                                Formulario formularioDependiente = fu.getFormulario(spinAdapterFinal.getItem(position).getDependientes(), lstPreguntas);
                                //Darle el texto y el formato al View de Pregunta creada arriba
                                txtPreguntaEspecial.setText(formularioDependiente.getPregunta());
                                txtPreguntaEspecial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                txtPreguntaEspecial.setTextSize(20);
                                txtPreguntaEspecial.setTypeface(null, Typeface.BOLD);
                                String tipo_dependiente = formularioDependiente.getTipo();

                                //Analizamos el tipo
                                //"1" es Spinner
                                if(tipo_dependiente.equals("1")) {
                                    //Quitamos el View de Respuestas normal
                                    //lo remplazamos con una respuesta tipo Spinner
                                    //misma idea para adicionar Spinner que arriba
                                    parent.removeView(parent.getChildAt(posicion_actual+2));
                                    parent.addView(spRespuestaEspecial, posicion_actual+2);
                                    String[] opciones_dependiente = formularioDependiente.getOpciones().split("-");
                                    String[] idopciones_dependiente = formularioDependiente.getIdopciones().split("-");
                                    String[] dependientes_dependiente = formularioDependiente.getDependientes().split("-");

                                    final List<Opcion> lstOpciones = new ArrayList<>();
                                    for (int j = 0; j < opciones_dependiente.length; j++) {
                                        String value = formularioDependiente.getId().toString() + "|" + formularioDependiente.getTipo() + "|" + idopciones_dependiente[j];
                                        Opcion op = new Opcion(value, opciones_dependiente[j], dependientes_dependiente[j]);
                                        Log.e("SITU ERROR: ", formularioDependiente.getId().toString() + "|" + formularioDependiente.getTipo() + "|" + idopciones_dependiente[j]);
                                        lstOpciones.add(op);
                                    }

                                    Opcion[] vOpciones = new Opcion[lstOpciones.size()];
                                    vOpciones = lstOpciones.toArray(vOpciones);
                                    spinAdapterPrincipal = new SpinAdapter(Formulario_Positiva.this,
                                            android.R.layout.simple_spinner_item,
                                            vOpciones);
                                    spRespuestaEspecial.setAdapter(spinAdapterPrincipal);
                                    //Necesitamos adicionar este nuevo valor a la lista de Spinners y Adapters
                                    //Por eso vemos cual es el tipo de Verif.
                                    //Esto es SPINNER así que tenemos que agregar el Spinner y su Spinner
                                    // Adapter a las listas
                                    if(tipo_ver.equals("1")||acceso_cliente.equals("21"))
                                    {
                                            lstSpinner.remove(posicion_actual - 2);
                                            lstAdapter.remove(posicion_actual - 2);
                                            lstSpinner.add(posicion_actual - 2, spRespuestaEspecial);
                                            lstAdapter.add(posicion_actual - 2, spinAdapterPrincipal);
                                    }
                                    else{
                                            lstSpinner.remove(posicion_actual - 4);
                                            lstAdapter.remove(posicion_actual - 4);
                                            lstSpinner.add(posicion_actual - 4, spRespuestaEspecial);
                                            lstAdapter.add(posicion_actual - 4, spinAdapterPrincipal);
                                    }
                                    spRespuestaEspecial.setVisibility(View.VISIBLE);
                                    Log.e("SITU SELECTED: ", "pos: " + spRespuestaEspecial.getSelectedItemPosition());
                                }
                                //Si no es Spinner es de respuesta abierta, así que adicionamos el TAG y un nuevo Texto
                                else{
                                    etRespuestaEspecial.setTag(formularioDependiente.getId() + "|" + formularioDependiente.getTipo());
                                    etRespuestaEspecial.setText("No indica");
                                    //lo mostramos
                                    etRespuestaEspecial.setVisibility(View.VISIBLE);
                                }
                                //Mostramos la pregunta
                                txtPreguntaEspecial.setVisibility(View.VISIBLE);
                            }
                            else{
                                //Esto está para que desaparezca en tiempo real si es que seleccionamos otra opción
                                Log.e("SITU, ","WEEEA ENTRO");
                                //Ya no me acuerdo por qué hacíamos esto, pero si o si teniamos q forzarlo
                                Opcion[] vacio = new Opcion[]{null,null,null,null,null,null,null,null};
                                SpinAdapter spinAdapterVacio = new SpinAdapter(Formulario_Positiva.this,
                                        android.R.layout.simple_spinner_item,
                                        vacio);
                                 if(tipo_ver.equals("1")||acceso_cliente.equals("21")){
                                    lstAdapter.remove(posicion_actual - 2);
                                     lstAdapter.add(posicion_actual-2, spinAdapterVacio);
                                }
                                else{
                                   lstAdapter.remove(posicion_actual - 4);
                                     lstAdapter.add(posicion_actual-4, spinAdapterVacio);
                               }
                                etRespuestaEspecial.setText("No procede");
                                etRespuestaEspecial.setVisibility(View.GONE);
                                spRespuestaEspecial.setVisibility(View.GONE);
                                txtPreguntaEspecial.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapter) {
                        }
                    });
                }
            }
        }
    }

    private ProgressDialog progressDialog;

    //*****ENVIAR PARA Q SE DESERIALIZE EN EL PHP
    public void enviarFormulario() {
        progressDialog = new ProgressDialog(Formulario_Positiva.this);
        progressDialog.setMessage("Enviando Verificación....");
        progressDialog.show();

        //El vacío nos indicará si hay algún campo que no haya sido llenado
        //Si el vacío es > a 0, entonce sno dejará enviar los datos
        verificar_campos_vacio = 0;
        int childs = parent.getChildCount();
        Log.e("CHILDS", "" + childs + "  " + nro_fotos);
        List<String> lstPreguntas = new ArrayList<>();
        List<String> lstRespuestas = new ArrayList<>();

        //Recorremos los Childs del Parent
        for (int i = 1; i < childs; i++) {
            View v = parent.getChildAt(i);
            //Dependiendo el Tipo de Vista tenemos que recoger los datos de manera diferente
            if (v instanceof EditText) {
                EditText etRespuesta = (EditText) v;
                String respuesta_final;
                //Ver si el EditText está vacío
                if (etRespuesta.getText().toString().trim().length() == 0) {
                    verificar_campos_vacio++;
                }
                //Con el TAG obtenemos el String que enviaremos a la BD
                if (etRespuesta.getTag() != null)
                    respuesta_final = etRespuesta.getTag() + "|" + etRespuesta.getText().toString();
                else
                    respuesta_final = etRespuesta.getText().toString();

                //Adicionamos a una Lista de respuestas
                lstRespuestas.add(respuesta_final);
            } else if (v instanceof TextView) {
                TextView txtPregunta = (TextView) v;
                //Esta es la pregunta, tenemos que ahcerle unas modificaciones para enviarla
                String pregunta_final = txtPregunta.getText().toString().replace(" ", "_").toLowerCase();
                lstPreguntas.add(pregunta_final);
            } else if (v instanceof LinearLayout) {
                //Si es LinearLayout o son las lstFotos, o es para la respuesta de tipo FECHA
                LinearLayout layoutFecha = (LinearLayout) v;
                //Vemos los child de este Linear Layout
                //Sacamos los datos del EditText
                if (layoutFecha.getChildAt(0) instanceof EditText) {
                    Log.e("SITU: ", "ENTRO AQUI2" + i);
                    String respuesta_final;
                    //Con el TAG y los DATOS obtenemos el String que enviaremos a la BD
                    respuesta_final = etRespuestaDate.getTag() + "|" + etRespuestaDate.getText().toString();
                    //Adicionamos este String a la Lista de Respuestas
                    lstRespuestas.add(respuesta_final);
                }
            } else if (v instanceof Spinner) {
                 //Necesitamos el Adaptador del Spinner, para analizar el objecto "Opción"
                if(tipo_ver.equals("1")||acceso_cliente.equals("21"))
                    spinAdapterParaEnviar = lstAdapter.get(i - 4);
                else
                    spinAdapterParaEnviar = lstAdapter.get(i - 6);

                Spinner spRespuesta = (Spinner) v;
                spRespuesta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        // Here you get the current item (a User object) that is selected by its position
                        Log.e("SITU PROBANDO: ", "ENTRO!!!!!!!!!!");
                        valor_final_spinner = spinAdapterParaEnviar.getItem(position).getID();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });

                if(spinAdapterParaEnviar.getItem(spRespuesta.getSelectedItemPosition()) != null){
                    valor_final_spinner = spinAdapterParaEnviar.getItem(spRespuesta.getSelectedItemPosition()).getID();
                }else{
                    valor_final_spinner = "";
                }
                //Adicionamos este String a la Lista de Respuestas
                lstRespuestas.add(valor_final_spinner);
            }
            Log.e("SITU ERROR VIEW: ", v.toString());
            Log.e("SITU ERROR VACIO: ", verificar_campos_vacio +"");
        }
        //Si vacío es mayr a 0, significa que hay algún campo vque no está llenado
        //SI este es el caso, lanza un diálogo y evita que los datos se envían
        if (verificar_campos_vacio != 0) {
            progressDialog.dismiss();
            dialogNotificarVacio = new Dialog(Formulario_Positiva.this);
            dialogNotificarVacio.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogNotificarVacio.setContentView(R.layout.dialog_vacio);
            dialogNotificarVacio.show();

            Button btnDialogCancelar = (Button) dialogNotificarVacio.findViewById(R.id.cancelar_boton);

            btnDialogCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogNotificarVacio.dismiss();
                }
            });


        }
        //Si todos los campos están llenos
        else {
        JsonObject innerObject = new JsonObject();
        Log.e("TAMANO!! ", "" + lstPreguntas.size());
        Log.e("TAMANO!! ", "" + lstRespuestas.size());

            //Creamos un Objeto JSON con las preguntas y las respuestas para guardarlo EN LA MEMORIA DEL CELULAR
        for (int k = 0; k < lstPreguntas.size(); k++) {
            Log.e("INDICE!! ", "" + k + "   " + lstRespuestas.get(k).toString());
            innerObject.addProperty(lstPreguntas.get(k).toString(), lstRespuestas.get(k).toString());
        }

        JsonObject jsono = new JsonObject();
        jsono.add("respuestas", innerObject);

        String jsonenviar = new Gson().toJson(lstRespuestas);

            //Si tiene Firma, adicionamos la dirección en la cual está guardada
        if(firma.equals("1"))
            direccion_foto_firma = Environment.getExternalStorageDirectory()
                    + "/Bigsomer_firmas_bgvs/" + id_ver +"_firma" + ".jpg";
        else
            direccion_foto_firma = "";

        //Abrimos Base de Datos
        crearBD = new DBHelper(Formulario_Positiva.this);
        db = crearBD.getWritableDatabase();

        //Crear un ContentValues para insertar los datos en la BD
        ContentValues values1 = new ContentValues();
        values1.put("id_fr", id_ver);
        values1.put("lat", lat);
        values1.put("lon", lon);
        values1.put("fecha_realizada", fecha_actual);
        values1.put("tipo_ver", tipo_ver);
        values1.put("acceso_cliente", acceso_cliente);
        values1.put("fotos", lstFotos.toString());
        values1.put("firma", direccion_foto_firma);
        values1.put("respuestas", jsonenviar.toString());
        values1.put("estado", "pendiente");
        db.insert("formulario_respuestas", null, values1);

        System.out.println("id_ver: " + id_ver + " fotos: " + lstFotos.toString() + "cosota: " + jsonenviar.toString() + " LA COSA Q IRA EN LA OTRA COSA " + jsono);
        System.out.println("LAT: " + lat + " LON: " + lon);

        //generamos el PDF antes de enviar
        generarPdf();

        //Crea un HashMap para generar el JSON PARA ENVIAR
        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_ver", id_ver);
        map.put("tipo_ver", tipo_ver);
        map.put("acceso_cliente", acceso_cliente);
        map.put("lat", lat + "");
        map.put("lon", lon + "");
        map.put("respuestas", jsonenviar.toString());
        map.put("fecha_realizada", fecha_actual);

        JSONObject jobject = new JSONObject(map);

            //Vemos si hay problemas con el servidor
            //Comprueba si se están subiendo las lstFotos
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
                    break;

                case "2":
                    // Mostrar mensaje
                    Log.e("SITU ENVIAR: ", "MAL");
                    break;
            }
            new Guardarfotos().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int q_foto;
    static final int REQUEST_TAKE_PHOTO = 0;
    static final int SELECT_FILE = 1;

    //Método para sacar fotografía o elegir de la galería
    public void sacarfoto(int nro_foto) {

        q_foto = nro_foto - 1;

        //Darle el nombre a la foto y la dirección donde se guardará
        direccion_foto = id_ver + "_" + nro_foto;
        direccion_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + "_" + nro_foto + ".jpg";
        final File fot = new File(direccion_foto);

        //Diálogo para seleccionar de la Cámara o de la Galería
        dialogEnviarVerificacion =new Dialog(Formulario_Positiva.this);
        dialogEnviarVerificacion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnviarVerificacion.setContentView(R.layout.dialog_fotos);
        dialogEnviarVerificacion.show();

        Button btnDialogFoto =(Button) dialogEnviarVerificacion.findViewById(R.id.positiva);
        Button btnDialogGaleria = (Button) dialogEnviarVerificacion.findViewById(R.id.negativa);

        //Sacar fotografía
        btnDialogFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Crea el File donde la foto debería ir
                    // Continúa solamente si el File fue creado exitosamente
                    if (fot != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fot));
                        dialogEnviarVerificacion.dismiss();
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });

        //Elegir galería
        btnDialogGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                dialogEnviarVerificacion.dismiss();
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        SELECT_FILE);
            }
        });
    }

    private ProgressDialog progressDialogFotos;
    private String resultado2 = "problemas";

    class Guardarfotos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogFotos = new ProgressDialog(Formulario_Positiva.this);
            progressDialogFotos.setMessage("Enviando Fotos...");
            progressDialogFotos.setIndeterminate(false);
            progressDialogFotos.setCancelable(false);
            progressDialogFotos.show();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(Formulario_Positiva.this);
            db = crearBD.getWritableDatabase();

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
                    for (int i = 0; i < lstFotos.size(); i++) {

                        File file = new File(lstFotos.get(i));
                        entityBuilder.addBinaryBody("fotoUp", file);

                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        java.lang.String respuestaWeb = EntityUtils.toString(response
                                .getEntity());

                        System.out.println("La web me muestra+ ////////////  " + respuestaWeb);
                    }

                    if(firma.equals("1")){
                        File file = new File(direccion_foto_firma);
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
            progressDialogFotos.dismiss();
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
        File file = new File(direccion_foto);
        Uri uri;
        boolean error_foto = false;
        //Recibe el dato para ver si se eligió foto o galería
        if (resultCode == Activity.RESULT_OK && resultCode != Formulario_Positiva.this.RESULT_CANCELED) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                //Procesar el sacado de foto
                new OperacionesFoto().execute();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, Formulario_Positiva.this);

                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                try {
                    System.gc();
                    bm = Bitmap.createScaledBitmap(
                            bm, 400, 300, false);
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();
                        uri = Uri.fromFile(new File(direccion_foto));
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

                //Obtiene el botón en el Layout de lstFotos
                //guarda el nombre y la dirección de la foto en un vector
                View ve = layoutFotos.getChildAt(q_foto);
                if (!lstFotos.contains(direccion_foto)) {
                    lstFotos.add(direccion_foto);
                }
                ImageButton ib = (ImageButton) ve;
                //Cambia la imagen del botón por la foto
                ib.setImageBitmap(bm);
            }else {
                Toast.makeText(Formulario_Positiva.this,
                        "Error con la foto, toma una de nuevo",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(Formulario_Positiva.this, "No se ha realizado la foto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Obtiene la dirección de donde se saca la foto de la galería
    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //Clase para sacar la foto
    class OperacionesFoto extends AsyncTask<String, String, String> {
        File file = new File(direccion_foto);
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
                        BitmapFactory.decodeFile(direccion_foto), 400, 300, false);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bit_.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                    uri = Uri.fromFile(new File(direccion_foto));
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

                    View viewFoto = layoutFotos.getChildAt(q_foto);
                    if (!lstFotos.contains(direccion_foto)) {
                        lstFotos.add(direccion_foto);
                    }

                    ImageButton btnImagenFoto = (ImageButton) viewFoto;
                    btnImagenFoto.setImageBitmap(BitmapFactory
                            .decodeFile(direccion_foto));
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void generarPdf(){

        PdfDocument document = new PdfDocument();
        //Oculta el Layout de lstFotos para sacar la captura
        layoutFotos.setVisibility(View.GONE);
        View content = parent;
        // crate a page info with attributes as below
        // page number, height and width
        // i have used height and width to that of pdf content view
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(),
                content.getHeight() - 20, pageNumber).create();

        // create a new page from the PageInfo
        PdfDocument.Page page = document.startPage(pageInfo);

        // repaint the user's text into the page
        content.draw(page.getCanvas());

        // do final processing of the page
        document.finishPage(page);

        // saving pdf document to sdcard
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        String pdfName = "pdf_"
                + id_ver + ".pdf";

        // all created files will be saved at path /sdcard/PDFDemo_AndroidSRC/
        File outputFile = new File(Environment.getExternalStorageDirectory()+"/BigSomer_pdf/",pdfName);

        try {
            //outputFile.mkdirs();
            outputFile.createNewFile();
            OutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            document.close();
            out.close();
            Toast.makeText(getApplicationContext(),"Pdf creado "+pdfName,Toast.LENGTH_LONG).show();
            layoutFotos.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"ERROR"+e.toString(),Toast.LENGTH_LONG).show();
           Log.e("PDF: ", e.toString());
        }
    }
}
