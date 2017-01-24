package com.example.jeancarla.bigsomer.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.jeancarla.bigsomer.helpers.DBHelper;
import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Usuario;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.example.jeancarla.bigsomer.helpers.VariablesURL;
import com.example.jeancarla.bigsomer.helpers.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = "ESTITO";

    private Calendar fechaYhora = Calendar.getInstance();
    SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String fecha_actual;
    String IMEI;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Gson gson = new Gson();
    private Funciones fu=new Funciones();
    private Dialog dialog, dialog2;
    private Button enviar, ayuda, cancelar;
    private String hay_usuario;
    private Usuario usuario_guardar = new Usuario();

    private boolean verificar = false;

    private DBHelper crearBD;
    private SQLiteDatabase db;
    private ContentValues values1;
    private String[]datos=new String[2];
    FragmentManager fragmentManager = getFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        hay_usuario=fu.hay_usuarios(getApplicationContext());

        if(!hay_usuario.equals("nada")){
            datos = hay_usuario.split("-");
            mEmailView.setText(datos[0]);
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               attemptLogin();
            }
        });
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_nuevo);

        dialog2 = new Dialog(LoginActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_imei);

        enviar = (Button) dialog.findViewById(R.id.enviar_boton);
        enviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "AQUI LE ENVIO LAS COSAS Y EL IMEI");
                TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                IMEI = mngr.getDeviceId();
                System.out.println("IMEI: " + IMEI);
                //OBTENER FECHA DEL DISPOSITIVO
                fecha_actual = fecha.format(fechaYhora.getTime());
                System.out.println("FECHA: " + fecha_actual);
                System.out.println("ID: " + usuario_guardar.getId_usuario());

                guardarusuario();
                dialog.dismiss();
            }
        });

        ayuda = (Button) dialog2.findViewById(R.id.enviar_boton);
        ayuda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "AQUI VAMOS A LLAMAR A ALGUIEN DE BIGSOMER");
                dialog2.dismiss();
            }
        });

        cancelar = (Button) dialog.findViewById(R.id.cancelar_boton);
        cancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Log.d(TAG, "AQUI VAMOS A LLAMAR A ALGUIEN DE BIGSOMER");
                dialog2.dismiss();
            }
        });

        cancelar = (Button) dialog2.findViewById(R.id.cancelar_boton);
        cancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Log.d(TAG, "AQUI VAMOS A LLAMAR A ALGUIEN DE BIGSOMER");
                dialog2.dismiss();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        IMEI = mngr.getDeviceId();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if(!hay_usuario.equals("nada")){
                if(email.equals(datos[0]) && password.equals(datos[1])){
                    Thread timerThread = new Thread(){
                        public void run(){
                            try{
                                sleep(1000);
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }finally{
                                Intent i = new Intent(LoginActivity.this, MenuPrincipal.class);
                                finish();
                                startActivity(i);
                            }
                        }
                    };
                    timerThread.start();
                }
                else{
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

            }else{
                cargarDatos(email, password, IMEI);
            }
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    /**
     * Obtiene los datos desde el servidor
     */
    public void cargarDatos(String user, String pass, String im) {
        boolean verif;
        final String usuario = user;
        // Añadir parámetro a la URL del web service
        String newURL = VariablesURL.GET_LOGIN2 + user + "&password=" + pass + "&imei=" + im;
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("===============" + s + "==============");
                        if (s.equals("Entrar")) {
                            pedirDatos(usuario);
                            System.out.println("===============" + s + "==============");
                            verificar = true;
                        } else if (s.equals("Incorrecto")) {
                            showProgress(false);
                            System.out.println("Usuario y Contraseña no coinciden, inténtelo de nuevo");
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                            verificar = false;
                        } else if (s.equals("Nuevo")) {
                            showProgress(false);
                            System.out.println("No esta detectando la cuenta con ningun IMEI");
                            dialog.show();
                            verificar = false;
                        } else if (s.equals("Otro Imei")) {
                            showProgress(false);
                            dialog2.show();
                            System.out.println("Cuenta con otro IMEI");
                            verificar = false;
                        } else {
                            System.out.println("No esta recibiendo nadaaaaa");
                            verificar = false;
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showProgress(false);
                        Log.d(TAG, "Error Volley aca: " + volleyError.getMessage());
                        Toast.makeText(getApplicationContext(), "Error al conectarse, por favor inténtelo de nuevo en un momento", Toast.LENGTH_LONG).show();
                    }
                }));

        verif = verificar;
        // return verif;

    }

    private void pedirDatos(String usua) {

        // Añadir parámetro a la URL del web service
        String newURL = VariablesURL.GET_USER + usua;

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        (String) null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                showProgress(false);
                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley en pedir Datos: " + error.getMessage());
                            }
                        }
                )
        );

    }

    /**
     * Procesa cada uno de los estados posibles de la
     * respuesta enviada desde el servidor
     *
     * @param response Objeto Json
     */
    private void procesarRespuesta(JSONObject response) {

        crearBD = new DBHelper(LoginActivity.this);
        db = crearBD.getWritableDatabase();
        ContentValues values1 = new ContentValues();
        System.out.println("SOMETHING ==============" + response.toString());

        Usuario usuario_guardar = gson.fromJson(response.toString(), Usuario.class);

        Log.i("DATOS===============> ", usuario_guardar.getId_usuario());

        values1.put("id_usuario", usuario_guardar.getId_usuario());
        values1.put("nombre_usuario", usuario_guardar.getNombre_usuario());
        values1.put("password", usuario_guardar.getPassword());
        values1.put("nombres", usuario_guardar.getNombres());
        values1.put("apellido_1", usuario_guardar.getApellido_1());
        values1.put("apellido_2", usuario_guardar.getApellido_2());
        values1.put("ciudad", usuario_guardar.getCiudad());
        values1.put("ci", usuario_guardar.getCi());
        values1.put("telefono", usuario_guardar.getTelefono());

        db.insert("usuario_completo", null, values1);

        db.close();

        Intent i = new Intent(LoginActivity.this, MenuPrincipal.class);
        finish();
        startActivity(i);

    }


    //COSAS DE LOS DIALOGOS


    public void guardarusuario() {

        // Obtener valores actuales de los controles

        String user = mEmailView.getText().toString();
        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("nombre_usuario", mEmailView.getText().toString());
        map.put("fecha_actual", fecha_actual);
        map.put("IMEI", IMEI);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        VariablesURL.INSERT_LOGIN,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                procesarRespuestaPost(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley En el Post: " + error.getMessage());
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

    private void procesarRespuestaPost(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("success");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "0":
                    // Mostrar mensaje
                    System.out.println("Error al guardar en la Base de Datos");
                    // Enviar código de éxito
                    // Terminar actividad
                    break;

                case "1":
                    // Mostrar mensaje

                    // Enviar código de falla
                    //getActivity().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    // getActivity().finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}




