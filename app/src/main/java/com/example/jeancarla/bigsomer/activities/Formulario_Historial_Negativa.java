package com.example.jeancarla.bigsomer.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Formulario_Historial_Negativa extends AppCompatActivity {

    private String acceso_cliente, tipo_ver, id_ver, nombre_foto, lati, longi, fecha_ver, rnombre, rcargo, rci, rcomentario;

    DBHelper crearBD;
    private SQLiteDatabase db;
    static final int REQUEST_CAMERA = 0;
    static final int SELECT_FILE = 1;
    boolean control_foto = false;
    LinearLayout parent, layout_fotos;
    private int n_fotos;
    private List<String> fotos;
    private double lat = 0;
    private double lon = 0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;
    private ImageView iv_location;
    private ImageButton button_foto;
    private Funciones fu = new Funciones();

    private EditText et_nombre, et_ci, et_cargo, et_comentarios;
    private String nombre, ci, cargo, comentarios;
    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String fecha_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario__historial__negativa);

        acceso_cliente = getIntent().getStringExtra("cliente");
        tipo_ver = getIntent().getStringExtra("tipo");
        id_ver = getIntent().getStringExtra("id_ver");
        lati = getIntent().getStringExtra("lat");
        longi = getIntent().getStringExtra("lon");
        fecha_ver = getIntent().getStringExtra("fecha");
        rnombre = getIntent().getStringExtra("nombre");
        rci = getIntent().getStringExtra("ci");
        rcargo = getIntent().getStringExtra("cargo");
        rcomentario = getIntent().getStringExtra("comentario");

        et_nombre = (EditText) findViewById(R.id.v_nombre);
        et_cargo = (EditText) findViewById(R.id.v_cargo);
        et_ci = (EditText) findViewById(R.id.v_ci);
        et_comentarios = (EditText) findViewById(R.id.v_com);

        et_cargo.setText(rcargo);
        et_ci.setText(rci);
        et_comentarios.setText(rcomentario);
        et_nombre.setText(rnombre);

        parent = (LinearLayout) findViewById(R.id.parent);
        fotos = new ArrayList<>();
        //FOTOS**************

        String nrofotos = fu.nro_fotosn(getApplicationContext(), acceso_cliente, tipo_ver);
        Log.e("SITU ", nrofotos);
        n_fotos = Integer.parseInt(nrofotos);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
        layoutParams.setMargins(0, 10, 0, 0);
        layout_fotos = new LinearLayout(getApplicationContext());
        layout_fotos.setOrientation(LinearLayout.VERTICAL);
        parent.addView(layout_fotos);

        String fotos_tomadas = fu.get_fotos_negativas(getApplicationContext(), id_ver);
        fotos_tomadas = fotos_tomadas.replace("[", "").replace("]", "");
        String[] fotos_v = fotos_tomadas.split("\\, ");
        int index_fotos = 0;
        for (int j = 0; j < fotos_v.length; j++) {
            fotos.add(fotos_v[j]);
        }

        for (int j = 1; j <= n_fotos; j++) {
            final int nro_foto = j;

            button_foto = new ImageButton(getApplicationContext());
            // button_foto.setId(j);
            File f = new File(fotos_v[index_fotos]);
            Log.e("SITU FOTO: ", fotos_v[index_fotos]);

            button_foto = new ImageButton(getApplicationContext());
            Picasso.with(getApplicationContext()).load(f).into(button_foto);
            index_fotos++;
            // button_foto.setId(j);
            //button_foto.setBackgroundResource(R.drawable.boton_camara);
            button_foto.setScaleType(ImageView.ScaleType.FIT_XY);
            button_foto.setLayoutParams(layoutParams);
            button_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(nro_foto);
                }
            });
            layout_fotos.addView(button_foto);

        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_enviar) {

            dialog = new Dialog(Formulario_Historial_Negativa.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_nuevo);
            dialog.show();
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
            dialog = new Dialog(Formulario_Historial_Negativa.this);
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

    /*evento para el boton BACK*/
    @Override
    public void onBackPressed() {
        dialog = new Dialog(Formulario_Historial_Negativa.this);
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

    private int q_foto;
    private Dialog dialog;

    private void selectImage(int nro_foto) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        q_foto = nro_foto - 1;
        nombre_foto = id_ver + "_" + nro_foto;
        nombre_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + "_" + nro_foto + ".jpg";
        final File fot = new File(nombre_foto);

        dialog = new Dialog(Formulario_Historial_Negativa.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fotos);
        dialog.show();

        Button posi = (Button) dialog.findViewById(R.id.positiva);
        Button nega = (Button) dialog.findViewById(R.id.negativa);

        posi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fot));
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        nega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        SELECT_FILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean error_foto = false;
        File file = new File(nombre_foto);
        ProgressDialog DialogFoto;
        Uri uri;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Log.e("SITU GALLERY: ", nombre_foto);
                new OperacionesFoto().execute();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, Formulario_Historial_Negativa.this);

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

                View ve = layout_fotos.getChildAt(q_foto);
                ImageButton ib = (ImageButton) ve;
                Log.e("SITU GALLERY: ", nombre_foto);
                ib.setImageBitmap(bm);
            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class OperacionesFoto extends AsyncTask<String, String, String> {
        File file = new File(nombre_foto);
        private ProgressDialog DialogFoto;
        private boolean error_foto = false;
        Uri uri;

        protected void onPreExecute() {
            super.onPreExecute();
            DialogFoto = new ProgressDialog(Formulario_Historial_Negativa.this);
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
                            Formulario_Historial_Negativa.this,
                            "Ocurrió un error al tomar la foto,tome una foto nueva",
                            Toast.LENGTH_SHORT).show();
                    file.delete();
                }
            } else {
                file.delete();
            }
        }
    }

    private ProgressDialog progressDialog;

    public void enviarFormulario() {
        progressDialog = new ProgressDialog(Formulario_Historial_Negativa.this);
        progressDialog.setMessage("Descargando Tareas....");
        progressDialog.show();


        nombre = et_nombre.getText().toString();
        ci = et_ci.getText().toString();
        cargo = et_cargo.getText().toString();
        comentarios = et_comentarios.getText().toString();

        crearBD = new DBHelper(Formulario_Historial_Negativa.this);
        db = crearBD.getWritableDatabase();
        ContentValues values1 = new ContentValues();
        values1.put("id_fr", id_ver);
        values1.put("lat", lat);
        values1.put("lon", lon);
        values1.put("fecha_realizada", fecha_actual);
        values1.put("tipo_ver", tipo_ver);
        values1.put("acceso_cliente", acceso_cliente);
        values1.put("fotos", fotos.toString());
        values1.put("nombre", nombre);
        values1.put("ci", ci);
        values1.put("cargo", cargo);
        values1.put("comentarios", comentarios);
        values1.put("estado", "pendiente");
        db.insert("formulario_negativas", null, values1);


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

    private ProgressDialog pDialog2;
    private String resultado2 = "problemas";
    VariablesURL variables = new VariablesURL();

    class Guardarfotos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(Formulario_Historial_Negativa.this);
            pDialog2.setMessage("Enviando fotos...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(Formulario_Historial_Negativa.this);
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
                    httpclient.getConnectionManager().shutdown();
                    resultado2 = "ok";
                    ContentValues values2 = new ContentValues();
                    values2.put("estado", "subida");
                    db.update("formulario_negativas", values2, "id_fr" + " = ?", new String[]{id_ver});
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
}
