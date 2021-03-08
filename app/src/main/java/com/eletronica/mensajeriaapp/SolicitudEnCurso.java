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
import android.content.pm.PackageManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolicitudEnCurso extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Context context;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    Location location;
    GPS_controler gpsTracker;


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    Double origenLat;
    Double origenLng;
    Double destinoLat;
    Double destinoLng;

    TextView txtNombre;
    TextView txtCalificacion;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcion;
    TextView txtImporte;
    Button btnFinalizar;
    ImageButton btnLlamar;
    TextView txtCancelar;

    Pedido pedido=null;

    int id_pedido = 0;

    boolean solicitud_activa = true;

    ProgressBar progressBar;

    final Handler handler = new Handler();
    final int delay = 3000; // 1000 milliseconds == 1 second


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_en_curso);
        context = getApplicationContext();

        gpsTracker = new GPS_controler(this);
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapaDialogoSolicitudEnCurso, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
        request = Volley.newRequestQueue(getApplicationContext());

        txtNombre = (TextView) findViewById(R.id.txtNombreUsuarioSolicitudEnCurso);
        txtCalificacion = (TextView) findViewById(R.id.txtCalificacionSolicitudEnCurso);
        txtOrigen = (TextView) findViewById(R.id.txtOrigenSolicitudEnCurso);
        txtDestino = (TextView) findViewById(R.id.txtDestinoSolicitudEnCurso);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcionOrigenSolicitudEnCurso);
        txtImporte = (TextView) findViewById(R.id.txtImporteSolicitudEnCurso);

        btnFinalizar = (Button) findViewById(R.id.btnFinalizarSolicitudEnCurso);
        btnLlamar = (ImageButton) findViewById(R.id.btnLlamarSolicitudEnCurso);
        txtCancelar = (TextView) findViewById(R.id.txtCancelarSolicitudEnCurso);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBarSolicitudEnCurso);

        Intent intent = getIntent();
        pedido = (Pedido) intent.getExtras().getSerializable("pedido");

        id_pedido = pedido.getId_pedido();
        txtNombre.setText(pedido.getNombre());
        txtCalificacion.setText(String.valueOf(pedido.getCalificacion()));
        txtOrigen.setText(pedido.getOrigen());
        txtDestino.setText(pedido.getDestino());
        txtDescripcion.setText(pedido.getDescripcion_origen());
        txtImporte.setText(String.valueOf(pedido.getImporte()));

        origenLat = pedido.getOrigen_latitud();
        origenLng = pedido.getOrigen_longitud();
        destinoLat = pedido.getDestino_latitud();
        destinoLng = pedido.getDestino_longitud() ;

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

        handler.postDelayed(new Runnable() {
            public void run() {
                if(solicitud_activa)
                    verificarStatus();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void verificarStatus(){
        GlobalVariables variablesGlobales = new GlobalVariables();

        String url = variablesGlobales.URLServicio + "verifica_status_solicitud.php?id_pedido="+String.valueOf(id_pedido) + "&ubicacionLat="+String.valueOf(mLastLocation.getLatitude()) + "&ubicacionLng="+String.valueOf(mLastLocation.getLongitude());
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
                    Toast.makeText(getApplicationContext(),"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            );

            request.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
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
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //mMap.setMyLocationEnabled(true);
        LatLng we = new LatLng(23.2340033,-106.4271412);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(we,15));

        setLocationDriver(gpsTracker.getLocation());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);
        }

        //23.2336172,origen_longitud=106.4153152,destino_latitud=23.2313368,destino_longitud=106.4072385
        //map.setMyLocationEnabled(true);
       setUpMap();


        LatLng marcador1 = new LatLng(origenLat, origenLng);
        LatLng marcador2 = new LatLng(destinoLat, destinoLng);

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

        runOnUiThread(new Runnable() {
            public void run() {
                new MyAsyncTask().execute(0);
            }
        });

    }

    private void setLocationDriver(Location location){
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_repartidor));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }
    public void ObtenerRuta(String latInicial, String lngInicial, String latFinal, String lngFinal){

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + latInicial + "," + lngInicial + "&destination=" + latFinal + "," + lngFinal + "&key=AIzaSyBxcLXHYB8gQW5Xg112ivn6Gko3YyOveKU&mode=drive";



        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;


                try {

                    jRoutes = response.getJSONArray("routes");


                    for(int i=0;i<jRoutes.length();i++){

                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");


                            for(int k=0;k<jSteps.length();k++){

                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                for(int l=0;l<list.size();l++){

                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);

                                }
                            }

                            Utils.routes.add(path);

                            /*Intent intent = new Intent(MapaInicio.this, Trasarlinea.class);
                            startActivity(intent);*/

                            trazarLinea();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);

    }

    public void trazarLinea(){
        ArrayList<LatLng>points=null;
        PolylineOptions lineOptions=null;

        for(int i = 0; i< Utils.routes.size(); i++){
            Log.d("aqui", String.valueOf(Utils.routes.size()));
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();


            List<HashMap<String, String>> path = Utils.routes.get(i);


            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }


            lineOptions.addAll(points);

            lineOptions.width(9);

            lineOptions.color(Color.BLUE);
        }

        mMap.addPolyline(lineOptions);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public  class MyAsyncTask extends AsyncTask<Integer, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();




        }

        @Override
        protected String doInBackground(Integer... integers) {

            try {
                while (location == null){
                    location = gpsTracker.getLocation();
                    publishProgress(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            location = gpsTracker.getLocation();
            publishProgress(2);

            return "Fin";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if(values[0] == 0){
                Log.d("Asyntask", "null");
            }else{
                ObtenerRuta(String.valueOf(origenLat), String.valueOf(origenLng),
                        String.valueOf(destinoLat), String.valueOf(destinoLng));

            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("asyntask", "FIN");

        }
    }

    private Boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == ((PackageManager.PERMISSION_GRANTED));
    }
    private void startInstalledAppDetailsActivity() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
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
        setLocationDriver(location);


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
                                ActivityCompat.requestPermissions(SolicitudEnCurso.this,
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
                        mMap.setMyLocationEnabled(true);
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

    @Override public void onBackPressed() {

    }
}