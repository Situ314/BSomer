package com.example.jeancarla.bigsomer.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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

public class Formulario_Historial extends AppCompatActivity {

    private int controlador;
    private String tipo_final;
    private String sfecha;
    LinearLayout parent, layout_fotos;
    private EditText txt_respuesta, txt_respuesta_date;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private int n_fotos;
    DBHelper crearBD;
    private ImageView iv_location;
    private SQLiteDatabase db;
    static final int REQUEST_CAMERA = 0;
    static final int SELECT_FILE = 1;

    //    ListMultimap<String, String> mapSpinner = ArrayListMultimap.create();
    final List<Opcion> lst_opcionesf = new ArrayList<>();
    private SpinAdapter adapter;
    private Funciones fu = new Funciones();

    private TextView tv_cargo;
    private EditText et_cargo;
    // obtiene fecha actual
    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String fecha_actual;

    private List<SpinAdapter> lst_adapter = new ArrayList<>();
    private List<Opcion> lst_obj = new ArrayList<>();
    private List<Spinner> lst_spinner = new ArrayList<>();
    private String acceso_cliente, tipo_ver, id_ver, lati, longi,fecha_ver;
    boolean control_foto = false;
    private HashMap<String, String> spinnerMap = new HashMap<String, String>();
    private List<String> fotos;
    private double lat = 0;
    private double lon = 0;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    private LinearLayout date_layout;
    private String final_value, datee, selected_item;
    private Opcion opcion_sel = new Opcion();
    private String nombre_foto;
    private ImageButton button_foto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private SpinAdapter ap, api;

    LinearLayout.LayoutParams paramb = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0.3f);
    LinearLayout.LayoutParams paramet = new LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0.7f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario__historial);

        acceso_cliente = getIntent().getStringExtra("cliente");
        tipo_ver = getIntent().getStringExtra("tipo");
        id_ver = getIntent().getStringExtra("id_ver");
        lati = getIntent().getStringExtra("lat");
        longi = getIntent().getStringExtra("lon");
        fecha_ver = getIntent().getStringExtra("fecha");

        tv_cargo = (TextView) findViewById(R.id.cargo_static);
        et_cargo = (EditText) findViewById(R.id.v_cargo);


        parent = (LinearLayout) findViewById(R.id.parent);
        fotos = new ArrayList<>();
        //******************LOCATION THINGS

        iv_location = (ImageView) findViewById(R.id.iv_location);

        if (tipo_ver.equals("1")) {
            parent.removeView(tv_cargo);
            parent.removeView(et_cargo);
            //tv_cargo.setVisibility(View.GONE);
            // et_cargo.setVisibility(View.GONE);
        }

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

        txt_respuesta_date.setText("No indica");


       // Toast.makeText(getApplicationContext(), acceso_cliente + "HISTORIAL " + tipo_ver, Toast.LENGTH_LONG).show();

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
                formulario[i].setTipo(fila.getString(6));
                formulario[i].setOpciones(fila.getString(7));
                formulario[i].setIdopciones(fila.getString(8));
                formulario[i].setDependientes(fila.getString(9));
                formulario[i].setVisible(fila.getString(10));

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

        try {
            generarFormulario(preguntas);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private Dialog dialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_enviar) {

            dialog = new Dialog(Formulario_Historial.this);
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
            dialog = new Dialog(Formulario_Historial.this);
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
        dialog = new Dialog(Formulario_Historial.this);
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

    public void generarFormulario(List<Formulario> datos_verificacion) throws JSONException {
        String fotos_tomadas = fu.get_fotos(getApplicationContext(), id_ver);
        fotos_tomadas = fotos_tomadas.replace("[","").replace("]","");
        String []fotos_v = fotos_tomadas.split("\\, ");
        int index_fotos=0;
        for(int j=0; j<fotos_v.length;j++){
            fotos.add(fotos_v[j]);
        }
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

                //txt_respuesta_date.setInputType(InputType.TYPE_CLASS_DATETIME);
                switch (tipo) {
                    case ("1"):
                        final List<Opcion> lst_opciones = new ArrayList<>();
                        final Spinner respuestas = (Spinner) getLayoutInflater().inflate(R.layout.spinner_verif, null);
                        String[] opciones = datos_verificacion.get(i).getOpciones().split("-");
                        String[] idopciones = datos_verificacion.get(i).getIdopciones().split("-");
                        String[] dependientes = datos_verificacion.get(i).getDependientes().split("-");
                        for (int j = 0; j < opciones.length; j++) {
                            String value = datos_verificacion.get(i).getId().toString() + "|" + datos_verificacion.get(i).getTipo() + "|" + idopciones[j];
                            Opcion op = new Opcion(value, opciones[j], dependientes[j]);
                            Log.e("SITU ERROR: ", datos_verificacion.get(i).getId().toString() + "|" + datos_verificacion.get(i).getTipo() + "|" + idopciones[j]);

                            //mapSpinner.put(opciones[j],value);
                            lst_opciones.add(op);
                            lst_opcionesf.add(op);
                            //spinnerMap.put(value,opciones[j]);
                            //       spinnerArray[i] = Province_NAME.get(i);
                        }

                        Opcion[] opciones_f = new Opcion[lst_opciones.size()];
                        opciones_f = lst_opciones.toArray(opciones_f);
                        adapter = new SpinAdapter(Formulario_Historial.this,
                                android.R.layout.simple_spinner_item,
                                opciones_f);
                        respuestas.setAdapter(adapter);

                        //Opcion op=respuestas.getSelectedItem();
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


                                DatePickerDialog datePickerDialog = new DatePickerDialog(Formulario_Historial.this,
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
        //button_foto.setLayoutParams();
        for (int j = 1; j <= n_fotos; j++) {
            final int nro_foto = j;

            button_foto = new ImageButton(getApplicationContext());
            // button_foto.setId(j);
            File f = new File(fotos_v[index_fotos]);
            Log.e("SITU FOTO: ",fotos_v[index_fotos]);
            Picasso.with(getApplicationContext()).load(f).into(button_foto);
            index_fotos++;
            button_foto.setScaleType(ImageView.ScaleType.FIT_XY);
            button_foto.setLayoutParams(layoutParams);
            button_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(nro_foto);
                    //  sacarfoto(nro_foto);
                }
            });
            layout_fotos.addView(button_foto);
        }
        Log.e("SITU ERROR OBJ: ", lst_spinner.toString());
        Log.e("SITU ERROR ADP: ", lst_adapter.toString());

        controlador = 0;
        for (int q = 0; q < parent.getChildCount(); q++) {
            View v = parent.getChildAt(q);
            //Log.e("SITU ERROR OBJ: ", lst_spinner.toString());
            //Log.e("SITU ERROR ADP: ", lst_adapter.toString());
            Log.e("SITU ERROR CHILDS: ", parent.getChildCount()+"");
            Log.e("SITU ERROR VIEW nro: ", ""+q);
            Log.e("SITU ERROR VIEW: ", v.toString());
            if (v instanceof Spinner) {
                int check = 0;
                Spinner res = (Spinner) v;
                Log.e("SITU ERROR CONTAR: ", (q - 4)+"");
                if(tipo_ver.equals("1"))
                    api = lst_adapter.get(q - 4);
                else
                    api = lst_adapter.get(q - 6);
                for (int i = 0; i < api.getCount(); i++) {
                    if (!api.getItem(i).getDependientes().equals("0")) {
                        check = 1;
                    }
                }

                if (check == 1) {
                    final List<Formulario> datos_v=datos_verificacion;
                    final int pos = q;
                    final TextView pregunta_especial = new TextView(getApplicationContext());
                    final EditText respuesta_especial = (EditText) getLayoutInflater().inflate(R.layout.edittext_verif, null);
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
                                Formulario get = fu.getFormulario(fin.getItem(position).getDependientes(), datos_v);
                                pregunta_especial.setText(get.getPregunta());
                                pregunta_especial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                pregunta_especial.setTextSize(20);
                                pregunta_especial.setTypeface(null, Typeface.BOLD);
                                respuesta_especial.setTag(get.getId() + "|" + get.getTipo());
                                //   respuesta_especial.setText("No procede");

                                if (!fin.getItem(position).getDependientes().equals("0")) {
                                    respuesta_especial.setText("No indica");
                                    respuesta_especial.setVisibility(View.VISIBLE);
                                    pregunta_especial.setVisibility(View.VISIBLE);
                                }

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
        llenar_formulario();
    }

    public void llenar_formulario() throws JSONException {
        int childs = parent.getChildCount();
        Log.e("SITU CHILDS", "" + childs + "  " + n_fotos);
        ArrayList<String> respuestas = fu.get_respuestas(getApplicationContext(), id_ver);
        Log.e("SITU RESP. ",respuestas.toString());
        String fotos_tomadas = fu.get_fotos(getApplicationContext(), id_ver);
        fotos_tomadas = fotos_tomadas.replace("[","").replace("]","");
        String []fotos_v = fotos_tomadas.split("\\,");
        int index_fotos=0;
        int n_respuesta = 0;

        for (int i = 0; i < childs; i++) {
            View v = parent.getChildAt(i);
            Log.e("SITU NRO R ", ""+n_respuesta);
            Log.e("SITU I ", ""+i);
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                String final_et;
                if (et.getTag() != null) {
                    String[] v_respuesta = respuestas.get(n_respuesta).toString().split("\\|");
                    if(v_respuesta.length > 2 && !v_respuesta[0].equals("0") && !fu.get_visible(getApplicationContext(),v_respuesta[0]).equals("0")) {
                        Log.e("SITU VECTOR: ",v_respuesta[0]+"-"+v_respuesta[1]+"-"+ v_respuesta[2]);
                        et.setText(v_respuesta[2].toString());
                    }
                    if (!v_respuesta[0].equals("0") && !fu.get_visible(getApplicationContext(),v_respuesta[0]).equals("0"))
                    {
                        n_respuesta++;
                    }
                } else {
                    et.setText(respuestas.get(n_respuesta).toString());
                    n_respuesta++;
                }
             } else if (v instanceof LinearLayout) {
                LinearLayout ly = (LinearLayout) v;
                if(ly.getChildAt(0) instanceof LinearLayout) {
                    String[] v_respuesta = respuestas.get(n_respuesta).toString().split("\\|");
                    txt_respuesta_date.setText(v_respuesta[2]);
                    n_respuesta++;
                }
            }
            else if (v instanceof Spinner) {
                if(tipo_ver.equals("1"))
                    ap = lst_adapter.get(i - 4);
                else
                    ap = lst_adapter.get(i - 6);
                String[] v_respuesta = respuestas.get(n_respuesta).toString().split("\\|");
                Spinner sp = (Spinner) v;
               Opcion n = fu.get_nombre(lst_opcionesf,respuestas.get(n_respuesta).toString());
                n_respuesta++;
                sp.setSelection(ap.getPosition(n));
            }
            else if(v instanceof ImageButton){
                ImageButton ib = (ImageButton) v;
                File f = new File(fotos_v[index_fotos]);
                Picasso.with(getApplicationContext()).load(f).into(ib);
                index_fotos++;
            }
        }
    }

    private int q_foto;

    private void selectImage(int nro_foto) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        q_foto = nro_foto - 1;
        nombre_foto = id_ver + "_" + nro_foto;
        nombre_foto = Environment.getExternalStorageDirectory() + "/Bigsomer_aver/" + id_ver + "_" + nro_foto + ".jpg";
        final File fot = new File(nombre_foto);

        dialog=new Dialog(Formulario_Historial.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fotos);
        dialog.show();

        Button posi=(Button) dialog.findViewById(R.id.positiva);
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
                Log.e("SITU GALLERY: ",nombre_foto);
                new OperacionesFoto().execute();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, Formulario_Historial.this);

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
                Log.e("SITU GALLERY: ",nombre_foto);
                ib.setImageBitmap(bm);
            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
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
            DialogFoto = new ProgressDialog(Formulario_Historial.this);
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
                            Formulario_Historial.this,
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

    //*****ENVIAR PARA Q SE DESERIALIZE EN EL PHP
    public void enviarFormulario() {
        progressDialog = new ProgressDialog(Formulario_Historial.this);
        progressDialog.setMessage("Enviando Verificación....");
        progressDialog.show();
        int childs = parent.getChildCount();
        Log.e("CHILDS", "" + childs + "  " + n_fotos);
        List<String> lst_preguntas = new ArrayList<>();
        List<String> lst_respuestas = new ArrayList<>();

        int n_keys = 0;
        for (int i = 0; i <= childs; i++) {
            View v = parent.getChildAt(i);
            //  if(i==0 || i%2==0){
            // Log.e("SITU ERROR: ",i+" "+v.toString());
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                String final_et;
                if (et.getTag() != null)
                    final_et = et.getTag() + "|" + et.getText().toString();
                else
                    final_et = et.getText().toString();

                lst_respuestas.add(final_et);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                String tv_final = tv.getText().toString().replace(" ", "_").toLowerCase();
                lst_preguntas.add(tv_final);
            } else if (v instanceof LinearLayout){

                LinearLayout ly = (LinearLayout) v;
                if(ly.getChildAt(0) instanceof LinearLayout) {
                    String final_et;
                    final_et = txt_respuesta_date.getTag() + "|" + txt_respuesta_date.getText().toString();
                    lst_respuestas.add(final_et);
                };

            }
            // }
            //  else {
            else if (v instanceof Spinner) {
                //Spinner sp=lst_spinner.get(i-6);
                ap = lst_adapter.get(i - 6);
                //sp.setOnItemClickListener();
                Spinner sp = (Spinner) v;
                //sp.getSelectedItem();
                // sp.getSelectedItem().equals(lst_obj.get(i).getValor());
                //Log.e("SITU ERROR VALOR: ",i+"  "+ sp.get);
                //final int index=i;
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
                //Opcion opcion_final=lst_obj.get(i);
                //final_value=opcion_final.getID();
                /*    Object a=sp.getSelectedItem();
                    Opcion osp=sp.getItem(0);
                    Opcion o_spinner=sp.getSelectedItem();
                    String value_nombre=sp.getSelectedItem().toString();
                    //String final_value=spinnerMap.get(value_nombre);
                    Log.e("SITU ERROR: ",i+" "+final_value);*/
                Log.e("SITU ERROR: ", i + " pos: " + sp.getSelectedItemPosition());
                Log.e("SITU ERROR: ", i + " item: " + ap.getItem(sp.getSelectedItemPosition()).getID());
                final_value = ap.getItem(sp.getSelectedItemPosition()).getID();
                lst_respuestas.add(final_value);

            }
            //}
        }

        JsonObject innerObject = new JsonObject();
        Log.e("TAMANO!! ", "" + lst_preguntas.size());
        Log.e("TAMANO!! ", "" + lst_respuestas.size());
        for (int k = 0; k < lst_preguntas.size(); k++) {
            innerObject.addProperty(lst_preguntas.get(k).toString(), lst_respuestas.get(k).toString());
            Log.e("INDICE!! ", "" + k);
        }

        JsonObject jsono = new JsonObject();
        jsono.add("respuestas", innerObject);

        String jsonenviar = new Gson().toJson(lst_respuestas);

        crearBD = new DBHelper(Formulario_Historial.this);
        db = crearBD.getWritableDatabase();
        ContentValues values1 = new ContentValues();
        values1.put("id_fr", id_ver);
        values1.put("lat", lati);
        values1.put("lon", longi);
        values1.put("fecha_realizada", fecha_ver);
        values1.put("tipo_ver", tipo_ver);
        values1.put("acceso_cliente", acceso_cliente);
        values1.put("fotos", fotos.toString());
        values1.put("respuestas", jsonenviar.toString());
        values1.put("estado", "pendiente");
        db.insert("formulario_respuestas", null, values1);

        //fu.eliminar_tarea(getApplicationContext(),id_ver);
        System.out.println("id_ver: " + id_ver + " fotos: " + fotos.toString() + "cosota: " + jsonenviar.toString() + " LA COSA Q IRA EN LA OTRA COSA " + jsono);
        System.out.println("LAT: " + lati + " LON: " + longi);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_ver", id_ver);
        map.put("tipo_ver", tipo_ver);
        map.put("acceso_cliente", acceso_cliente);
        map.put("lat", lati + "");
        map.put("lon", longi + "");
        map.put("respuestas", jsonenviar.toString());
        map.put("fecha_realizada", fecha_ver);

        JSONObject jobject = new JSONObject(map);

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
            pDialog2 = new ProgressDialog(Formulario_Historial.this);
            pDialog2.setMessage("Enviando fotos...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            //GUARDAR DATOS EN SQLITE
            crearBD = new DBHelper(Formulario_Historial.this);
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
}
