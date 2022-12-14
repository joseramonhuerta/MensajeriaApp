package com.eletronica.mensajeriaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.utils.GPS_controler;
import com.eletronica.mensajeriaapp.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.eletronica.mensajeriaapp.utils.FetchURL;
import com.eletronica.mensajeriaapp.utils.TaskLoadedCallback;


public class SolicitudAceptada extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, TaskLoadedCallback {
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Context context;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    Location location;
    RequestQueue requestFoto;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    Double origenLat;
    Double origenLng;
    Double destinoLat;
    Double destinoLng;

    String parada1 = null;
    String parada2 = null;
    String parada3 = null;

    Double parada1Lat;
    Double parada1Lng;
    Double parada2Lat;
    Double parada2Lng;
    Double parada3Lat;
    Double parada3Lng;

    TextView txtNombre;
    TextView txtPlacas;
    TextView txtCalificacion;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcion;
    TextView txtImporte;
    Button btnFinalizar;
    ImageButton btnLlamar;

    CircleImageView ivImagen;

    LinearLayout layParada1;
    LinearLayout layParada2;
    LinearLayout layParada3;

    TextView txtParada1;
    TextView txtParada2;
    TextView txtParada3;
    
    Pedido pedido=null;

    int id_pedido = 0;
    int status = 0;
    int id_usuario = 0;

    private Polyline currentPolyline;

    boolean solicitud_activa = true;

    ProgressBar progressBar;

    final Handler handler = new Handler();
    final int delay = 3000; // 1000 milliseconds == 1 second

    GlobalVariables vg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_aceptada);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = getApplicationContext();

        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragmentAceptada");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapaDialogoSolicitudAceptada, mapFragment, "mapFragmentAceptada");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
        request = Volley.newRequestQueue(getApplicationContext());
        requestFoto = Volley.newRequestQueue(getApplicationContext());

        txtNombre = (TextView) findViewById(R.id.txtNombreUsuarioSolicitudAceptada);
        txtCalificacion = (TextView) findViewById(R.id.txtCalificacionSolicitudAceptada);
        txtOrigen = (TextView) findViewById(R.id.txtOrigenSolicitudAceptada);
        txtDestino = (TextView) findViewById(R.id.txtDestinoSolicitudAceptada);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcionOrigenSolicitudAceptada);
        txtImporte = (TextView) findViewById(R.id.txtImporteSolicitudAceptada);

        btnFinalizar = (Button) findViewById(R.id.btnCerrarSolicitudAceptada);
        btnLlamar = (ImageButton) findViewById(R.id.btnLlamarSolicitudAceptada);
        txtPlacas = (TextView) findViewById(R.id.txtPlacasSolicitudAceptada);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarSolicitudAceptada);

        ivImagen = (CircleImageView) findViewById(R.id.ivImagenUsuarioAceptada);

        txtParada1 = (TextView) findViewById(R.id.txtParada1SolicitudAceptada);
        txtParada2 = (TextView) findViewById(R.id.txtParada2SolicitudAceptada);
        txtParada3 = (TextView) findViewById(R.id.txtParada3SolicitudAceptada);

        layParada1 = (LinearLayout) findViewById(R.id.layParada1SolicitudAceptada);
        layParada2 = (LinearLayout) findViewById(R.id.layParada2SolicitudAceptada);
        layParada3 = (LinearLayout) findViewById(R.id.layParada3SolicitudAceptada);

        vg = new GlobalVariables();

        Intent intent = getIntent();
        pedido = (Pedido) intent.getExtras().getSerializable("pedido");

        id_pedido = pedido.getId_pedido();
        id_usuario = pedido.getId_usuario();
        txtNombre.setText(pedido.getNombre_mensajero());
        txtCalificacion.setText(String.valueOf(pedido.getCalificacion_mensajero()));
        txtOrigen.setText(pedido.getOrigen());
        txtDestino.setText(pedido.getDestino());
        txtDescripcion.setText(pedido.getDescripcion_origen());
        txtImporte.setText(String.valueOf(pedido.getImporte()));
        txtPlacas.setText(pedido.getPlacas());
        
        origenLat = pedido.getOrigen_latitud();
        origenLng = pedido.getOrigen_longitud();
        destinoLat = pedido.getDestino_latitud();
        destinoLng = pedido.getDestino_longitud();

        parada1 = pedido.getParada1();
        parada1Lat = pedido.getParada_latitud_1();
        parada1Lng = pedido.getParada_longitud_1();

        parada2 = pedido.getParada2();
        parada2Lat = pedido.getParada_latitud_2();
        parada2Lng = pedido.getParada_longitud_2();

        parada3 = pedido.getParada3();
        parada3Lat = pedido.getParada_latitud_3();
        parada3Lng = pedido.getParada_longitud_3();

        status = pedido.getStatus();


        if(!parada1.equals("null")){
            layParada1.setVisibility(View.VISIBLE);
            txtParada1.setText(parada1);
        }

        if(!parada2.equals("null")){
            layParada2.setVisibility(View.VISIBLE);
            txtParada2.setText(parada2);
        }

        if(!parada3.equals("null")){
            layParada3.setVisibility(View.VISIBLE);
            txtParada3.setText(parada3);
        }

        cargarImagenUsuario();

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = pedido.getCelular_mensajero();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(getApplicationContext(), "No existe un numero de celular", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(status == 1) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    verificarStatus();
                    handler.postDelayed(this, delay);
                }
            }, delay);
        }


    }

    public void cargarImagenUsuario(){

        String urlImg = vg.URLServicio + "fotos/" + String.valueOf(id_usuario)+".jpg";
        urlImg = urlImg.replace(" ","%20");
        try{
            ImageRequest imageRequest = new ImageRequest(urlImg, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    ivImagen.setImageBitmap(response);
                }
            },0,0, ImageView.ScaleType.CENTER,null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            request.add(imageRequest);
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void verificarStatus(){
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "verifica_status_solicitud.php?id_pedido="+String.valueOf(id_pedido);
        try{
            jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray = response.optJSONArray("datos");
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));
                        int status_solicitud = Integer.parseInt(jsonObject.optString("status"));
                        double ubicacion_lat = Double.parseDouble(jsonObject.optString("ubicacion_latitud"));
                        double ubicacion_lng = Double.parseDouble(jsonObject.optString("ubicacion_longitud"));

                        LatLng driver = new LatLng(ubicacion_lat,ubicacion_lng);

                        status = status_solicitud;
                        setLocationDriver(driver);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            );

            request.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng we = new LatLng(23.2340033, -106.4271412);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(we, 12));
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                LatLng marcador1 = new LatLng(origenLat, origenLng);
                LatLng marcador2 = new LatLng(destinoLat, destinoLng);

                if (!parada1.equals("null")) {
                    LatLng stop1 = new LatLng(parada1Lat, parada1Lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop1)
                            .title(parada1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                if (!parada2.equals("null")) {
                    LatLng stop2 = new LatLng(parada2Lat, parada2Lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop2)
                            .title(parada2)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                if (!parada3.equals("null")) {
                    LatLng stop3 = new LatLng(parada3Lat, parada3Lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop3)
                            .title(parada3)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                mMap.addMarker(new MarkerOptions()
                        .position(marcador1)
                        .title(pedido.getOrigen())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_origen)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marcador1));

                mMap.addMarker(new MarkerOptions()
                        .position(marcador2)
                        .title(pedido.getDestino())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_destino)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marcador2));

                // Centrar Marcadores
                LatLngBounds.Builder constructor = new LatLngBounds.Builder();

                constructor.include(marcador1);
                constructor.include(marcador2);

                LatLngBounds limites = constructor.build();

                //int ancho = getResources().getDisplayMetrics().widthPixels;
                int ancho = mapFragment.getView().getMeasuredWidth();

                //int alto = getResources().getDisplayMetrics().heightPixels;
                int alto = mapFragment.getView().getMeasuredHeight();
                int padding = (int) (alto * 0.20); // 25% de espacio (padding) superior e inferior

                CameraUpdate centrarmarcadores = CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding);

                mMap.moveCamera(centrarmarcadores);

            }
        });

        runOnUiThread(new Runnable() {
            public void run() {
                new FetchURL(SolicitudAceptada.this).execute(getUrl(String.valueOf(origenLat), String.valueOf(origenLng),
                        String.valueOf(destinoLat), String.valueOf(destinoLng), "driving"),"driving");
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
                setUpMap();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            setUpMap();
            //mMap.setMyLocationEnabled(true);
        }
    }

    private String getUrl(String latInicial, String lngInicial, String latFinal, String lngFinal, String directionMode) {
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service


        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + latInicial + "," + lngInicial + "&destination=" + latFinal + "," + lngFinal + "&" + mode +  "&key=AIzaSyBxcLXHYB8gQW5Xg112ivn6Gko3YyOveKU";

        String wayPoints = "";

        if(!parada1.equals("null")){
            wayPoints = wayPoints + (wayPoints.equals("") ? "" : "%7C") + parada1Lat + "," + parada1Lng;
        }

        if(!parada2.equals("null")){
            wayPoints = wayPoints + (wayPoints.equals("") ? "" : "%7C") + parada2Lat + "," + parada2Lng;
        }

        if(!parada3.equals("null")){
            wayPoints = wayPoints + (wayPoints.equals("") ? "" : "%7C") + parada3Lat + "," + parada3Lng;
        }
        wayPoints = "&waypoints=" + wayPoints;
        url = url + wayPoints;
        return url;
    }

    private void setLocationDriver(LatLng location){
        if(status == 1){
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            //Place current location marker
            LatLng latLng = location;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_repartidor));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    /*nuevo codigo*/

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {



    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SolicitudAceptada.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                        setUpMap();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
