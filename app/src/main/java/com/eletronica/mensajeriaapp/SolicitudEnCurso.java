package com.eletronica.mensajeriaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
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
import android.location.LocationManager;
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
import com.eletronica.mensajeriaapp.fragments.SolicitudesFragment;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.eletronica.mensajeriaapp.utils.FetchURL;
import com.eletronica.mensajeriaapp.utils.TaskLoadedCallback;

public class SolicitudEnCurso extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,TaskLoadedCallback {
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Context context;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    RequestQueue requestFoto;
    Location location;
    GPS_controler gpsTracker;

    LocationManager locationManager;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    Double origenLat;
    Double origenLng;
    Double destinoLat;
    Double destinoLng;
    Double ubicacionLat = 23.2340033;
    Double ubicacionLng = -106.4271412;

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
    TextView txtCalificacion;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcion;
    TextView txtImporte;
    Button btnFinalizar;
    ImageButton btnLlamar;
    TextView txtCancelar;

    LinearLayout layParada1;
    LinearLayout layParada2;
    LinearLayout layParada3;

    TextView txtParada1;
    TextView txtParada2;
    TextView txtParada3;

    CircleImageView ivImagen;
    ImageView btnCerrar;

    Pedido pedido=null;

    private Polyline currentPolyline;

    int id_pedido = 0;
    int id_usuario = 0;

    boolean solicitud_activa = true;

    ProgressBar progressBar;

    boolean mapReady = false;

    final Handler handler = new Handler();
    final int delay = 3000; // 1000 milliseconds == 1 second

    GlobalVariables vg;
    AlertDialog alertGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_en_curso);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = getApplicationContext();

        // gpsTracker = new GPS_controler(this);
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragmentEnCurso");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapaDialogoSolicitudEnCurso, mapFragment, "mapFragmentEnCurso");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
        request = Volley.newRequestQueue(getApplicationContext());
        requestFoto = Volley.newRequestQueue(getApplicationContext());

        boolean estado = estadoGPS();

        if(estado == false){
            AlertNoGPS();
        }

        txtNombre = (TextView) findViewById(R.id.txtNombreUsuarioSolicitudEnCurso);
        txtCalificacion = (TextView) findViewById(R.id.txtCalificacionSolicitudEnCurso);
        txtOrigen = (TextView) findViewById(R.id.txtOrigenSolicitudEnCurso);
        txtDestino = (TextView) findViewById(R.id.txtDestinoSolicitudEnCurso);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcionOrigenSolicitudEnCurso);
        txtImporte = (TextView) findViewById(R.id.txtImporteSolicitudEnCurso);

        btnFinalizar = (Button) findViewById(R.id.btnFinalizarSolicitudEnCurso);
        btnLlamar = (ImageButton) findViewById(R.id.btnLlamarSolicitudEnCurso);
        txtCancelar = (TextView) findViewById(R.id.txtCancelarSolicitudEnCurso);

        ivImagen = (CircleImageView) findViewById(R.id.ivImagenUsuarioEnCurso);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBarSolicitudEnCurso);

        txtParada1 = (TextView) findViewById(R.id.txtParada1EnCurso);
        txtParada2 = (TextView) findViewById(R.id.txtParada2EnCurso);
        txtParada3 = (TextView) findViewById(R.id.txtParada3EnCurso);

        layParada1 = (LinearLayout) findViewById(R.id.layParada1EnCurso);
        layParada2 = (LinearLayout) findViewById(R.id.layParada2EnCurso);
        layParada3 = (LinearLayout) findViewById(R.id.layParada3EnCurso);
        btnCerrar = (ImageView) findViewById(R.id.btnCerrarSolicitudEnCurso);

        vg = new GlobalVariables();

        Intent intent = getIntent();
        pedido = (Pedido) intent.getExtras().getSerializable("pedido");

        id_pedido = pedido.getId_pedido();
        id_usuario = pedido.getId_usuario();
        txtNombre.setText(pedido.getNombre());
        txtCalificacion.setText(String.valueOf(pedido.getCalificacion()));
        txtOrigen.setText(pedido.getOrigen());
        txtDestino.setText(pedido.getDestino());
        txtDescripcion.setText(pedido.getDescripcion_origen());
        txtImporte.setText(String.valueOf(pedido.getImporte()));

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

        /*
        byte[] foto = pedido.getFoto();

        if (foto != null) {
            byte[] encodeByte = (byte[]) (foto);
            if(encodeByte.length > 0){
                Bitmap photobmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                ivImagen.setImageBitmap(photobmp);

            }
        }
        */
        cargarImagenUsuario();

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(SolicitudEnCurso.this);

                builder.setMessage("Desea FINALIZAR esta solicitud?");
                builder.setTitle(pedido.getOrigen().toString()+" - "+pedido.getDestino().toString());
                builder.setCancelable(false);

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalizarSolicitud();


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        txtCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(SolicitudEnCurso.this);

                builder.setMessage("Desea CANCELAR esta solicitud?");
                builder.setTitle(pedido.getOrigen().toString()+" - "+pedido.getDestino().toString());
                builder.setCancelable(false);

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelarSolicitud();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = pedido.getCelular();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(getApplicationContext(), "No existe un numero de celular", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler.postDelayed(new Runnable() {
            public void run() {
                if(solicitud_activa)
                    verificarStatus();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private boolean estadoGPS() {
        boolean activo;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            activo = true;
        else
            activo = false;

        return activo;

    }

    private void AlertNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS no esta activado, desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertGPS = builder.create();
        alertGPS.show();
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
        solicitud_activa = true;
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "verifica_status_solicitud.php?id_pedido="+String.valueOf(id_pedido) + "&ubicacionLat="+String.valueOf(ubicacionLat) + "&ubicacionLng="+String.valueOf(ubicacionLng);
        try{
            jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray = response.optJSONArray("datos");
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));
                        int status = Integer.parseInt(jsonObject.optString("status"));
                        if(success){
                            if(status == 3){
                                solicitud_activa = false;
                                String msg = jsonObject.optString("msg");
                                AlertDialog.Builder builder= new AlertDialog.Builder(SolicitudEnCurso.this);
                                builder.setMessage(msg);
                                builder.setTitle(pedido.getOrigen().toString()+" - "+pedido.getDestino().toString());
                                builder.setCancelable(false);
                                builder.setPositiveButton("Volver a las solicitudes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    } catch (JSONException e) {
                        //e.printStackTrace();
                        //Toast.makeText(getApplicationContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }




                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getApplicationContext(),"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            );

            request.add(jsonObjectRequest);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void finalizarSolicitud(){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "finalizar_solicitud.php?id_usuario="+String.valueOf(variablesGlobales.id_usuario)+
                "&id_pedido="+String.valueOf(id_pedido);

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONArray jsonArray = response.optJSONArray("datos");
                JSONObject jsonObject = null;

                try {
                    jsonObject = jsonArray.getJSONObject(0);
                    boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));

                    if(success){
                        finish();

                    }

                    String msg = jsonObject.optString("msg");
                    Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

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
    }


    private void cancelarSolicitud(){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "rechazar_solicitud.php?id_usuario="+String.valueOf(variablesGlobales.id_usuario)+
                "&id_pedido="+String.valueOf(id_pedido);

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONArray jsonArray = response.optJSONArray("datos");
                JSONObject jsonObject = null;

                try {
                    jsonObject = jsonArray.getJSONObject(0);
                    boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));

                    if(success){
                        finish();
                    }

                    String msg = jsonObject.optString("msg");
                    Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

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
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng we = new LatLng(23.2340033,-106.4271412);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(we,12));
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {






                //setLocationDriver(gpsTracker.getLocation());
                //setUpMap(); //Se quito esta funcion porque era para la validacion pero ya es estaba validando.

                LatLng marcador1 = new LatLng(origenLat, origenLng);
                LatLng marcador2 = new LatLng(destinoLat, destinoLng);

                if(!parada1.equals("null")){
                    LatLng stop1 = new LatLng(parada1Lat, parada1Lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop1)
                            .title(parada1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                if(!parada2.equals("null")){
                    LatLng stop2 = new LatLng(parada2Lat, parada2Lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop2)
                            .title(parada2)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                if(!parada3.equals("null")){
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
                int ancho =  mapFragment.getView().getMeasuredWidth();

                //int alto = getResources().getDisplayMetrics().heightPixels;
                int alto =  mapFragment.getView().getMeasuredHeight();
                int padding = (int) (alto * 0.20); // 25% de espacio (padding) superior e inferior

                CameraUpdate centrarmarcadores = CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding);

                mMap.moveCamera(centrarmarcadores);
            }
        });

        runOnUiThread(new Runnable() {
            public void run() {
                new FetchURL(SolicitudEnCurso.this).execute(getUrl(String.valueOf(origenLat), String.valueOf(origenLng),
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
                setUpMap();
                //mMap.setMyLocationEnabled(true);
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

        //setUpMap();
    }

    private void setLocationDriver(Location location){
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_repartidor));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mLastLocation = location;
        ubicacionLat = location.getLatitude();
        ubicacionLng = location.getLongitude();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
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
        setLocationDriver(location);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(SolicitudEnCurso.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        setUpMap();
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


}
