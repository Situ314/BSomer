package com.example.jeancarla.bigsomer.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jeancarla.bigsomer.R;
import com.example.jeancarla.bigsomer.classes.Cliente;
import com.example.jeancarla.bigsomer.classes.Formulario;
import com.example.jeancarla.bigsomer.classes.Tarea;
import com.example.jeancarla.bigsomer.helpers.Funciones;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener  {

    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    private double latitud, longitud, dlat, dlon;
    private String acceso_cliente;

    private double lat,lon;
    private static final int LOCATION_REQUEST_CODE = 1;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private Marker cliente_marker;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        acceso_cliente = getIntent().getStringExtra("cliente");

        //******************OBTIENE LOCALIZACIÓN ACTUAL DEL USUARIO
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();

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

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

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


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        //Hace ZOOM al Mapa en la locaclización del usuario
        if (location != null)
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        cargarMarkers(googleMap);
    }

    private String usuario = "10001";
    private String TAG = "SITU";
    Funciones fu=new Funciones();
    private BitmapDescriptor bitmap_marker;
    private String f_asignacion, fecha_faltante;
    //Carga los markers de todas las tareas que tiene el usuario
    public void cargarMarkers(GoogleMap Map){
        final GoogleMap googleMap = Map;
        List<Tarea> tareas = fu.getTareas(getApplicationContext());

        //recorre las tareas y coloca un marker en ese punto
        //Cooloca una imagen diferente por TIPo de verificación
        for(int i=0; i<tareas.size();i++) {
            latitud = Double.parseDouble(tareas.get(i).getUbLatitud());
            longitud = Double.parseDouble(tareas.get(i).getUbLongitud());
            switch (tareas.get(i).getIdtipo()){
                case "1":
                    bitmap_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_dom);
                    break;
                case "4":
                   bitmap_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_dep);
                    break;
                case "5":
                    bitmap_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_ind);
                    break;
                default:
                    bitmap_marker = BitmapDescriptorFactory.fromResource(R.drawable.marker_vs);
                    break;
            }

            LatLng direccion = new LatLng(latitud, longitud);
            cliente_marker = googleMap.addMarker(new MarkerOptions()
                    .position(direccion)
                    .title(tareas.get(i).getIdVer())
                    .icon(bitmap_marker)
                    .snippet("Nombre: "+tareas.get(i).getNombre() + System.getProperty ("line.separator") + "CI: " + tareas.get(i).getCi()));
            googleMap.setOnInfoWindowClickListener(this);
        }
    }

    //Evento para hacer click en la Ventana de información del Marker
    @Override
    public void onInfoWindowClick(Marker marker) {
        String id_marker = marker.getTitle();
        List<Tarea> tareas = fu.getTareas(getApplicationContext());

        for(int i = 0; i < tareas.size(); i++){
            //Obttiene y llena todos los datos para que se muestre la pantalla de Mapa_Detalles
            if(tareas.get(i).getIdVer().equals(id_marker)){
                final Double lat, lon;
                lat = fu.Convertir_Datos(tareas.get(i).getUbLatitud());
                lon = fu.Convertir_Datos(tareas.get(i).getUbLongitud());
                final String nombre, medidor, tarea, direccion2, ci, comentarios, nombre_empresa;
                final String cliente = tareas.get(i).getCliente();
                final String tipo = tareas.get(i).getIdtipo();
                final String id_ver = tareas.get(i).getIdVer();

                nombre = tareas.get(i).getNombre();
                medidor = tareas.get(i).getMedidor();
                tarea = tareas.get(i).getIdVer();
                direccion2 = tareas.get(i).getDireccion();
                ci = tareas.get(i).getCi();
                comentarios = tareas.get(i).getReferencias();
                nombre_empresa = tareas.get(i).getNombreEmpresa();

                long diferencia = 2880 - Long.parseLong(tareas.get(i).getF_asignacion());
                long diff = diferencia * 60000;

                if(diff > 0) {
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long daysInMilli = hoursInMilli * 24;

                    long elapsedDays = diff / daysInMilli;
                    diff = diff % daysInMilli;

                    long elapsedHours = diff / hoursInMilli;
                    diff = diff % hoursInMilli;

                    long elapsedMinutes = diff / minutesInMilli;
                    diff = diff % minutesInMilli;

                    long elapsedSeconds = diff / secondsInMilli;

                    fecha_faltante = elapsedDays + " días " + elapsedHours + " horas " + elapsedMinutes + " minutos";
                }
                else{
                    fecha_faltante = "Esta verificación ya se encuentra con RETRASO";
                }

                final int i_cliente = i;
                intent = new Intent(getApplicationContext(), Mapa_Detalles.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cliente", cliente);
                bundle.putSerializable("tipo", tipo);
                bundle.putSerializable("id_ver", id_ver);
                bundle.putSerializable("lat", lat);
                bundle.putSerializable("lon", lon);
                bundle.putSerializable("nombre", nombre);
                bundle.putSerializable("medidor", medidor);
                bundle.putSerializable("tarea", tarea);
                bundle.putSerializable("direccion", direccion2);
                bundle.putSerializable("nombre_empresa",nombre_empresa);
                bundle.putSerializable("ci", ci);
                bundle.putSerializable("comentarios", comentarios);
                bundle.putSerializable("falta", fecha_faltante);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}
