package com.example.jeancarla.bigsomer.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.example.jeancarla.bigsomer.helpers.VolleySingleton;

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

public class Formulario_Negativa extends AppCompatActivity {

    //para guardar los datos recibidos de la anterior Actividad
    private String acceso_cliente, tipo_ver, id_ver;

    //Strings para guardar los datos introducios en los campos de nombre,ci,cargo y comentarios
    private String nombre,ci,cargo,comentarios;
    
    //Datos para las fotos
    private String nombre_foto;
    private int nro_fotos;
    boolean control_foto = false;
    private List<String> lstFotos;

    //DataBase
    DBHelper crearBD;
    private SQLiteDatabase db;

    //Vistas
    LinearLayout parent, layoutFotos;
    private ImageView ivLocation;
    private ImageButton btnFoto;
    private EditText etNombre, etCi, etCargo, etComentarios;
    private TextView txtPdfLat;  
   
    //localización
    private double lat = 0;
    private double lon = 0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    //Para obtener las funciones
    private Funciones fu = new Funciones();

    //fechas
    //para guardar la fecha actual
    private String fecha_actual;
    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario__negativa);

        parent = (LinearLayout) findViewById(R.id.parent);
        acceso_cliente = getIntent().getStringExtra("cliente");
        tipo_ver = getIntent().getStringExtra("tipo");
        id_ver = getIntent().getStringExtra("id_ver");

        etNombre =(EditText) findViewById(R.id.v_nombre);
        etCargo =(EditText) findViewById(R.id.v_cargo);
        etCi =(EditText) findViewById(R.id.v_ci);
        etComentarios =(EditText) findViewById(R.id.v_com);

        txtPdfLat = (TextView) findViewById (R.id.txt_lat_lon);

        //******************LOCATION THINGS
        lstFotos = new ArrayList<>();

        ivLocation = (ImageView) findViewById(R.id.iv_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                //Cambia el texto en la imagen
                txtPdfLat.setText("LAT: "+lat+" / LON: "+lon);
                //Cambia la imagen indicando que ya se agarraron las coordenadas
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
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath()
                + "/Bigsomer_aver");
        if (!directory.isDirectory())
            directory.mkdirs();
        nombre_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + ".jpg";
        //FOTOS**********

        // **** Fecha Actual
        fecha_actual = fecha.format(fechaYhora.getTime());

        // Obtener número de fotos
        String nroFotos=fu.nro_fotosn(getApplicationContext(),acceso_cliente,tipo_ver);
        Log.e("SITU ",nroFotos);
        nro_fotos = Integer.parseInt(nroFotos);

        // Dar parámetros al Layout de las fotos y adicionarlo al Layout Principal
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
        layoutParams.setMargins(0, 10, 0, 0);
        layoutFotos = new LinearLayout(getApplicationContext());
        layoutFotos.setOrientation(LinearLayout.VERTICAL);
        parent.addView(layoutFotos);

        //Adicionar Botones Para las fotos
        for (int j = 1; j <= nro_fotos; j++) {
            final int nroFotoAdicionar = j;
            btnFoto = new ImageButton(getApplicationContext());
            btnFoto.setBackgroundResource(R.drawable.boton_camara);
            btnFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            btnFoto.setLayoutParams(layoutParams);
            btnFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sacarfoto(nroFotoAdicionar);
                }
            });
            layoutFotos.addView(btnFoto);
        }

        setToolbar();
    }

    //Toolbar
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
        //Botón en el Menú para enviar la verificación
        if (id == R.id.action_enviar) {
            dialog = new Dialog(Formulario_Negativa.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_nuevo);
            dialog.show();
            TextView txtDialogTitulo = (TextView) dialog.findViewById(R.id.info_text);
            TextView txtDialogInfo = (TextView) dialog.findViewById(R.id.info_text2);
            TextView txtDialogInfo2 = (TextView) dialog.findViewById(R.id.info_text3);

            Button btnDialogEnviar = (Button) dialog.findViewById(R.id.enviar_boton);
            Button btnDialogCancelar = (Button) dialog.findViewById(R.id.cancelar_boton);

            //Verificar si agarro Latitud y longitud
            if (lat != 0 && lon != 0) {
                //Verifica si hay las fotos requeridas
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
                            dialog.dismiss();
                        }
                    });

                    btnDialogCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    return true;
                } else {
                    //Crea diálogo indicando que no se cuenta con las fotos requeridas
                    txtDialogTitulo.setText("NO SE ENCONTRARON LAS FOTOS REQUERIDAS");
                    txtDialogInfo.setText("La presente verificación necesita de " + nro_fotos + " fotografías para poder ser enviada. Asegúrese de haber sacado todas las fotos o de tener espacio suficiente en la memoria del celular");
                    txtDialogInfo2.setVisibility(View.GONE);

                    btnDialogEnviar.setVisibility(View.GONE);

                    btnDialogCancelar.setText("OK");
                    btnDialogCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
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
                        dialog.dismiss();
                    }
                });

                return true;
            }

        }
        //Botón para volver atrás
        else {
            dialog = new Dialog(Formulario_Negativa.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_two);
            dialog.show();

            Button btnDialogSi = (Button) dialog.findViewById(R.id.si_boton);
            btnDialogSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Button btnDialogNo = (Button) dialog.findViewById(R.id.no_boton);
            btnDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private int cualFoto;
    static final int REQUEST_TAKE_PHOTO = 0;
    static final int SELECT_FILE = 1;

    //Método para sacar fotografía o elegir de la galería
    public void sacarfoto(int nro_foto) {

        cualFoto = nro_foto - 1;

        //Darle el nombre a la foto y la dirección donde se guardará
        nombre_foto = id_ver + "_" + nro_foto;
        nombre_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + "_" + nro_foto + ".jpg";
        final File fot = new File(nombre_foto);

        //Diálogo para seleccionar de la Cámara o de la Galería
        dialog=new Dialog(Formulario_Negativa.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fotos);
        dialog.show();

        Button posi=(Button) dialog.findViewById(R.id.positiva);
        Button nega = (Button) dialog.findViewById(R.id.negativa);

        //Sacar fotografía
        posi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Crea el File donde la foto debería ir
                    // Continúa solamente si el File fue creado exitosamente
                    if (fot != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fot));
                        dialog.dismiss();
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        control_foto = true;
                    }
                }
            }
        });

        //Elegir galería
        nega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                dialog.dismiss();
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        SELECT_FILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //displayDate();
        boolean error_foto = false;
        File file = new File(nombre_foto);
        Uri uri;

        //Recibe el dato para ver si se eligió foto o galería
        if (resultCode == Activity.RESULT_OK
                && resultCode != Formulario_Negativa.this.RESULT_CANCELED) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                //Procesar el sacado de foto
                new OperacionesFoto().execute();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, Formulario_Negativa.this);
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

                //Obtiene el botón en el Layout de fotos
                //guarda el nombre y la dirección de la foto en un vector
                View ve = layoutFotos.getChildAt(cualFoto);
                if (!lstFotos.contains(nombre_foto)) {
                    lstFotos.add(nombre_foto);
                }
                ImageButton ib = (ImageButton) ve;
                Log.e("SITU GALLERY: ", nombre_foto);
                //Cambia la imagen del botón por la foto
                ib.setImageBitmap(bm);
            }else {
                Toast.makeText(Formulario_Negativa.this,
                        "Error con la foto, toma una de nuevo",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Formulario_Negativa.this, "No se ha realizado la foto",
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
        File file = new File(nombre_foto);
        private ProgressDialog DialogFoto;
        private boolean error_foto = false;
        Uri uri;            

        protected void onPreExecute() {
            super.onPreExecute();
            DialogFoto = new ProgressDialog(Formulario_Negativa.this);
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
                    Log.e("FOTO CAMARA AQUI==", "ENTRA!!" + cualFoto);
                    //Obtiene el botón en el Layout de fotos
                    //guarda el nombre y la dirección de la foto en un vector
                    View ve = layoutFotos.getChildAt(cualFoto);
                    if (!lstFotos.contains(nombre_foto)) {
                        lstFotos.add(nombre_foto);
                    }
                    //Cambia la imagen del botón por la foto tomada
                    ImageButton ib = (ImageButton) ve;
                    ib.setImageBitmap(BitmapFactory
                            .decodeFile(nombre_foto));
                } else {
                    Toast.makeText(
                            Formulario_Negativa.this,
                            "Ocurrió un error al tomar la foto,tome una foto nueva",
                            Toast.LENGTH_SHORT).show();
                    file.delete();
                }
            } else {
                file.delete();
            }
        }
    }

    //Enviar los datos y las fotos
    private ProgressDialog progressDialog;
    public void enviarFormulario(){
        progressDialog = new ProgressDialog(Formulario_Negativa.this);
        progressDialog.setMessage("Descargando Tareas....");
        progressDialog.show();

        //Obtener los datos de los EditText
        nombre= etNombre.getText().toString();
        ci= etCi.getText().toString();
        cargo= etCargo.getText().toString();
        comentarios= etComentarios.getText().toString();

        //Abrir Base de datos
        crearBD = new DBHelper(Formulario_Negativa.this);
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
        values1.put("nombre", nombre);
        values1.put("ci", ci);
        values1.put("cargo", cargo);
        values1.put("comentarios", comentarios);
        values1.put("estado", "pendiente");
        db.insert("formulario_negativas", null, values1);

        //Generar PDF
        generarPdf();

        //Crea un HashMap para generar el JSON
        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_ver", id_ver);
        map.put("tipo_ver", tipo_ver);
        map.put("acceso_cliente", acceso_cliente);
        map.put("lat", lat + "");
        map.put("lon", lon + "");
        map.put("nombre_informante", nombre);
        map.put("ci_informante", ci);
        map.put("cargo_informante", cargo);
        map.put("comentarios", comentarios);
        map.put("fecha_realizada", fecha_actual);

        JSONObject jobject = new JSONObject(map);
        Log.e("SITU ENVIAR: ", jobject.toString());

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        VariablesURL.INSERT_NEGA,
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
                                //Se dispara si es que no existe conexión
                                //Los datos se encuentran guardados en la memoria del celular
                                //para Sincronizar después
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
                    break;

                case "2":
                    // Mostrar mensaje
                    Log.e("SITU ENVIAR: ", "MAL");
                    // Enviar código de falla
                    break;
            }
            //Una vez guarda los datos, sube las fotos
            new Guardarfotos().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog pDialog2;
    private String resultado2 = "problemas";
    VariablesURL variables = new VariablesURL();

    class Guardarfotos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(Formulario_Negativa.this);
            pDialog2.setMessage("Enviando fotos...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(false);
            pDialog2.show();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(Formulario_Negativa.this);
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
                //Recorre el vector
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
                    httpclient.getConnectionManager().shutdown();
                    resultado2 = "ok";
                    //Una vez se hayan subido las fotos, cambia el estado de la verificación de "pendiente" a "subida"
                    ContentValues values2 = new ContentValues();
                    values2.put("estado", "subida");
                    db.update("formulario_negativas", values2, "id_fr" + " = ?", new String[]{id_ver});
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Conexion", "NO");
                }
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void generarPdf(){

        PdfDocument document = new PdfDocument();
        //Oculta el Layout de fotos para sacar la captura
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

        // all created files will be saved at path /sdcard/BigSomer_pdf/
        File outputFile = new File(Environment.getExternalStorageDirectory()+"/BigSomer_pdf/",pdfName);

        try {
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
