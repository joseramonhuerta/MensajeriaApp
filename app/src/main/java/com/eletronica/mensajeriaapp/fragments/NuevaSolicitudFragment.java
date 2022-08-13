package com.eletronica.mensajeriaapp.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.CuadroDialogoConfirmarSolicitud;
import com.eletronica.mensajeriaapp.CuadroDialogoObtenerUbicacion;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NuevaSolicitudFragment extends Fragment implements CuadroDialogoConfirmarSolicitud.Confirmar, CuadroDialogoObtenerUbicacion.ActualizarUbicacion{
    TextInputLayout txtDireccionOrigen, txtDireccionDestino, txtComentarios, txtParada1, txtParada2, txtParada3;
    Button btnSolicitar, btnCancelar;
    ImageView btnAgregarParada,btnEliminarParada1,btnEliminarParada2,btnEliminarParada3;
    LinearLayout layParada1, layParada2, layParada3;
    Double origen_lat=null,origen_lng=null,destino_lat=null,destino_lng=null, parada1_lat=null,parada1_lng=null, parada2_lat=null,parada2_lng=null, parada3_lat=null,parada3_lng=null;
    View mView;
    ProgressBar progressBar;
    RequestQueue rq;
    JsonObjectRequest jrq;

    String origen;
    String destino;
    String comentarios;
    String parada1;
    String parada2;
    String parada3;

    private Geocoder geocoder;
    private FragmentManager fm;
    public static final int DIALOGO_FRAGMENT = 100;
    public static final int DIALOGO_FRAGMENT_UBICACION = 200;
    public NuevaSolicitudFragment() {
        // Required empty public constructor
    }

    @Override
    public void solicitudConfirmada() {

        //progressBar.setVisibility(mView.VISIBLE);
        registrarServicio();


    }

    @Override
    public void actualizaActividadUbicacion(int control,String mOrigen, LatLng mOrigenLatLng) {
        if(control == 1){
            origen = mOrigen;
            txtDireccionOrigen.getEditText().setText(origen);
            origen_lat = mOrigenLatLng.latitude;
            origen_lng=mOrigenLatLng.longitude;
        }
        if(control == 2){
            parada1 = mOrigen;
            txtParada1.getEditText().setText(parada1);
            parada1_lat = mOrigenLatLng.latitude;
            parada1_lng=mOrigenLatLng.longitude;
        }
        if(control == 3){
            parada2 = mOrigen;
            txtParada2.getEditText().setText(parada2);
            parada2_lat = mOrigenLatLng.latitude;
            parada2_lng=mOrigenLatLng.longitude;
        }
        if(control == 4){
            parada3 = mOrigen;
            txtParada3.getEditText().setText(parada3);
            parada3_lat = mOrigenLatLng.latitude;
            parada3_lng=mOrigenLatLng.longitude;
        }
        if(control == 5){
            destino = mOrigen;
            txtDireccionDestino.getEditText().setText(destino);
            destino_lat = mOrigenLatLng.latitude;
            destino_lng=mOrigenLatLng.longitude;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nueva_solicitud, container, false);
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = view;
        fm = getFragmentManager();
        geocoder = new Geocoder(getActivity().getApplicationContext());
        rq = Volley.newRequestQueue(getActivity().getApplicationContext());
        txtDireccionOrigen = (TextInputLayout) view.findViewById(R.id.txtDireccionOrigen);
        txtDireccionDestino = (TextInputLayout) view.findViewById(R.id.txtDireccionDestino);
        txtComentarios = (TextInputLayout) view.findViewById(R.id.txtComentarios);
        btnSolicitar = (Button) view.findViewById(R.id.btnAceptarNuevaSolicitud);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelarSolicitud);
        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBarNuevaSolicitud);

        layParada1 = (LinearLayout) view.findViewById(R.id.layParada1);
        layParada2 = (LinearLayout) view.findViewById(R.id.layParada2);
        layParada3 = (LinearLayout) view.findViewById(R.id.layParada3);

        txtParada1 = (TextInputLayout) view.findViewById(R.id.txtDireccionParada1);
        txtParada2 = (TextInputLayout) view.findViewById(R.id.txtDireccionParada2);
        txtParada3 = (TextInputLayout) view.findViewById(R.id.txtDireccionParada3);

        btnAgregarParada = (ImageView) view.findViewById(R.id.btnAgregarParada);
        btnEliminarParada1 = (ImageView) view.findViewById(R.id.btnEliminarParada1);
        btnEliminarParada2 = (ImageView) view.findViewById(R.id.btnEliminarParada2);
        btnEliminarParada3 = (ImageView) view.findViewById(R.id.btnEliminarParada3);

        Places.initialize(getActivity().getApplicationContext(),"AIzaSyBxcLXHYB8gQW5Xg112ivn6Gko3YyOveKU");

        txtDireccionOrigen.getEditText().setFocusable(false);
        txtDireccionOrigen.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                //Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                //startActivityForResult(intent, 100);
                obtenerUbicacion(1);
            }
        });

        txtDireccionDestino.getEditText().setFocusable(false);
        txtDireccionDestino.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                startActivityForResult(intent, 200);*/
                obtenerUbicacion(5);
            }
        });

        txtParada1.getEditText().setFocusable(false);
        txtParada1.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                startActivityForResult(intent, 300);*/
                obtenerUbicacion(2);
            }
        });

        txtParada2.getEditText().setFocusable(false);
        txtParada2.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                startActivityForResult(intent, 400);*/
                obtenerUbicacion(3);
            }
        });

        txtParada3.getEditText().setFocusable(false);
        txtParada3.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                startActivityForResult(intent, 500);*/
                obtenerUbicacion(4);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarPantalla();
            }
        });

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarDireccionOrigen() | !validarDireccionDestino() | !validarComentarios() | !validarParada1() | !validarParada2() | !validarParada3()){
                    return;
                }

                /*
                FragmentTransaction ft = fm.beginTransaction();
                CuadroDialogoConfirmarSolicitud dialogoFragment = new CuadroDialogoConfirmarSolicitud(getContext(), fm, mView);

                double distance = getDistancia();
                Bundle b = new Bundle();
                b.putDouble("distancia", distance);

                dialogoFragment.setArguments(b);
                CuadroDialogoConfirmarSolicitud tPrev =  (CuadroDialogoConfirmarSolicitud) fm.findFragmentByTag("dialogoConfirmarSolicitud");

                if(tPrev!=null)
                    ft.remove(tPrev);

                dialogoFragment.setTargetFragment(NuevaSolicitudFragment.this, DIALOGO_FRAGMENT);
                dialogoFragment.show(fm, "dialogoConfirmarSolicitud");
                */
                //registrarServicio();
                confirmarSolicitud();
            }


        });

        btnAgregarParada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mostrarLayoutParada();
            }
        });

        btnEliminarParada1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarLayoutParada(1);
            }
        });

        btnEliminarParada2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarLayoutParada(2);
            }
        });

        btnEliminarParada3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarLayoutParada(3);
            }
        });

        checkPermisoUbicacion();
        return view;
    }

    public void checkPermisoUbicacion() {


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                getUbicacion();
                //mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            getUbicacion();
            //mMap.setMyLocationEnabled(true);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity().getApplicationContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        getUbicacion();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity().getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void obtenerUbicacion(int control){

        FragmentTransaction ft = fm.beginTransaction();
        CuadroDialogoObtenerUbicacion dialogoFragment = new CuadroDialogoObtenerUbicacion(getContext(), fm, mView, control);

        CuadroDialogoObtenerUbicacion tPrev =  (CuadroDialogoObtenerUbicacion) fm.findFragmentByTag("dialogoUbicacion");

        if(tPrev!=null)
            return;//ft.remove(tPrev);

        dialogoFragment.setTargetFragment(NuevaSolicitudFragment.this, DIALOGO_FRAGMENT_UBICACION);
        dialogoFragment.show(fm, "dialogoUbicacion");


        //Intent intent = new Intent(getActivity().getApplicationContext(), ObtenerUbicacion.class);
        //startActivity(intent);

    }


    private void confirmarSolicitud() {

            GlobalVariables variablesGlobales = new GlobalVariables();

            double distance = getDistancia();

            this.origen = txtDireccionOrigen.getEditText().getText().toString();
            this.destino = txtDireccionDestino.getEditText().getText().toString();
            this.comentarios = txtComentarios.getEditText().getText().toString();
            this.parada1 = txtParada1.getEditText().getText().toString();
            this.parada2 = txtParada2.getEditText().getText().toString();
            this.parada3 = txtParada3.getEditText().getText().toString();

            //variablesGlobales.URLServicio + "obtener_importe.php?distancia="+String.valueOf(distancia);
            String url = variablesGlobales.URLServicio + "obtener_importe.php?distancia="+String.valueOf(distance);
            jrq=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);

                    JSONArray jsonArray = response.optJSONArray("datos");
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));

                        if(success){
                            double tarifa = jsonObject.optDouble("tarifa");

                            FragmentTransaction ft = fm.beginTransaction();
                            CuadroDialogoConfirmarSolicitud dialogoFragment = new CuadroDialogoConfirmarSolicitud(getContext(), fm, mView);

                            Bundle b = new Bundle();
                            b.putString("origen", origen);
                            b.putString("destino", destino);
                            b.putString("comentarios", comentarios);
                            b.putString("parada1", parada1);
                            b.putString("parada2", parada2);
                            b.putString("parada3", parada3);
                            b.putDouble("tarifa", tarifa);


                            dialogoFragment.setArguments(b);
                            CuadroDialogoConfirmarSolicitud tPrev =  (CuadroDialogoConfirmarSolicitud) fm.findFragmentByTag("dialogoConfirmarSolicitud");

                            if(tPrev!=null)
                                return;//ft.remove(tPrev);

                            dialogoFragment.setTargetFragment(NuevaSolicitudFragment.this, DIALOGO_FRAGMENT);
                            dialogoFragment.show(fm, "dialogoConfirmarSolicitud");

                        }else{
                            String msg = jsonObject.optString("msg");
                            Toast.makeText(mView.getContext(),msg, Toast.LENGTH_SHORT).show();
                        }



                    } catch (JSONException e) {
                        //e.printStackTrace();
                        Toast.makeText(mView.getContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                    }




                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(),"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            );

            rq.add(jrq);



    }

    private  void getUbicacion(){

        try{

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }

            LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext()).requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext()).removeLocationUpdates(this);
                    if(locationResult != null && locationResult.getLocations().size() > 0){
                        int lastesLocationIndex = locationResult.getLocations().size() - 1;
                        origen_lat = locationResult.getLocations().get(lastesLocationIndex).getLatitude();
                        origen_lng = locationResult.getLocations().get(lastesLocationIndex).getLongitude();
                        //String loc = locationResult.getLocations().get(lastesLocationIndex).toString();
                        try {
                            List<Address> adress = geocoder.getFromLocation(origen_lat,origen_lng, 1);

                            Address ad = adress.get(0);
                            ArrayList<String> addressFragment = new ArrayList<>();
                            for(int i = 0; i <= ad.getMaxAddressLineIndex(); i++){
                                addressFragment.add(ad.getAddressLine(i));
                            }

                            String adres = TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")), addressFragment);


                            txtDireccionOrigen.getEditText().setText(adres);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //txtDireccionOrigen.getEditText().setText(
                    }
                }
            }, Looper.myLooper());

        }catch (Exception e){
            Toast.makeText(mView.getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    private void mostrarLayoutParada() {
        String valParada1 = txtParada1.getEditText().getText().toString();
        String valParada2 = txtParada2.getEditText().getText().toString();
        String valParada3 = txtParada3.getEditText().getText().toString();

        if(!layParada1.isShown()){
            layParada1.setVisibility(View.VISIBLE);
        }else if(layParada1.isShown() & !valParada1.isEmpty() & !layParada2.isShown()){
            layParada2.setVisibility(View.VISIBLE);
        }else if(layParada2.isShown() & !valParada2.isEmpty() & !layParada3.isShown()){
            layParada3.setVisibility(View.VISIBLE);
        }



    }

    private void ocultarLayoutParada(int boton) {

        if(boton == 3){
            layParada3.setVisibility(View.GONE);
            txtParada3.getEditText().setText("");
            this.parada3_lat = null;
            this.parada3_lng = null;
        }else if(boton == 2 & !layParada3.isShown()){
            layParada2.setVisibility(View.GONE);
            txtParada2.getEditText().setText("");
            this.parada2_lat = null;
            this.parada2_lng = null;
        }else if(boton == 1 & !layParada2.isShown()){
            layParada1.setVisibility(View.GONE);
            txtParada1.getEditText().setText("");
            this.parada1_lat = null;
            this.parada1_lng = null;
        }


    }

    private double getDistancia(){
        double distanceOrigenParada1 = 0;
        double distanceParada1Parada2 = 0;
        double distanceParada2Parada3 = 0;
        double distanceParada3Destino = 0;
        double distanceOrigenDestino = 0;
        double distanceParada1Destino = 0;
        double distanceParada2Destino = 0;

        Location locationOrigen = new Location("Origen");
        locationOrigen.setLatitude(this.origen_lat);  //latitud
        locationOrigen.setLongitude(this.origen_lng); //longitud

        Location locationDestino = new Location("Destino");
        locationDestino.setLatitude(this.destino_lat);  //latitud
        locationDestino.setLongitude(this.destino_lng); //longitud

        Location location = new Location("Location");
        Location location2 = new Location("Location2");
        Location location3 = new Location("Location3");

        if(this.parada1_lat == null){
            distanceOrigenDestino = locationOrigen.distanceTo(locationDestino);
        }else if(this.parada1_lat != null & this.parada2_lat == null){

            location.setLatitude(this.parada1_lat);  //latitud
            location.setLongitude(this.parada1_lng); //longitud

            distanceOrigenParada1 = locationOrigen.distanceTo(location);
            distanceParada1Destino = location.distanceTo(locationDestino);
        }else if(this.parada1_lat != null & this.parada2_lat != null & this.parada3_lat == null){
            location.setLatitude(this.parada1_lat);  //latitud
            location.setLongitude(this.parada1_lng); //longitud

            distanceOrigenParada1 = locationOrigen.distanceTo(location);

            location2.setLatitude(this.parada2_lat);  //latitud
            location2.setLongitude(this.parada2_lng); //longitud

            distanceParada1Parada2 = location.distanceTo(location2);

            distanceParada2Destino = location2.distanceTo(locationDestino);
        }else if(this.parada1_lat != null & this.parada2_lat != null & this.parada3_lat != null ){
            location.setLatitude(this.parada1_lat);  //latitud
            location.setLongitude(this.parada1_lng); //longitud

            distanceOrigenParada1 = locationOrigen.distanceTo(location);

            location2.setLatitude(this.parada2_lat);  //latitud
            location2.setLongitude(this.parada2_lng); //longitud

            distanceParada1Parada2 = location.distanceTo(location2);

            location3.setLatitude(this.parada3_lat);  //latitud
            location3.setLongitude(this.parada3_lng); //longitud

            distanceParada2Parada3 = location2.distanceTo(location3);

            distanceParada3Destino = location3.distanceTo(locationDestino);
        }




        double distance = (distanceOrigenParada1 + distanceParada1Parada2 + distanceParada2Parada3 + distanceParada3Destino + distanceOrigenDestino + distanceParada1Destino + distanceParada2Destino) / 1000;

        return distance;
    }

    private void registrarServicio(){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();

        double distance = getDistancia();

        String origen = txtDireccionOrigen.getEditText().getText().toString();
        String destino = txtDireccionDestino.getEditText().getText().toString();
        String comentarios = txtComentarios.getEditText().getText().toString();
        String origenlat = String.valueOf(this.origen_lat);
        String origenlng = String.valueOf(this.origen_lng);
        String destinolat = String.valueOf(this.destino_lat);
        String destinolng = String.valueOf(this.destino_lng);

        String parada1 = txtParada1.getEditText().getText().toString();
        String parada1lat = String.valueOf(this.parada1_lat);
        String parada1lng = String.valueOf(this.parada1_lng);

        String parada2 = txtParada2.getEditText().getText().toString();
        String parada2lat = String.valueOf(this.parada2_lat);
        String parada2lng = String.valueOf(this.parada2_lng);

        String parada3 = txtParada3.getEditText().getText().toString();
        String parada3lat = String.valueOf(this.parada3_lat);
        String parada3lng = String.valueOf(this.parada3_lng);

        String distancia = String.valueOf(distance);

        String url = variablesGlobales.URLServicio + "registra_nueva_solicitud.php?id_usuario="+variablesGlobales.id_usuario+"&origen="+origen+"&destino="+destino+"&comentarios="+comentarios+"&origenlat="+origenlat+"&origenlng="+origenlng+"&destinolat="+destinolat+"&destinolng="+destinolng+"&distancia="+distancia+"&parada1="+parada1+"&parada1lat="+parada1lat+"&parada1lng="+parada1lng+"&parada2="+parada2+"&parada2lat="+parada2lat+"&parada2lng="+parada2lng+"&parada3="+parada3+"&parada3lat="+parada3lat+"&parada3lng="+parada3lng;
        jrq=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONArray jsonArray = response.optJSONArray("datos");
                JSONObject jsonObject = null;

                    try {
                    jsonObject = jsonArray.getJSONObject(0);
                    boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));
                    String msg = jsonObject.optString("msg");
                    if(success){
                        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
                        limpiarPantalla();
                        notificacionNuevaSolicitud();
                    }else{
                        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(getContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                }




            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        rq.add(jrq);


    }

    private void notificacionNuevaSolicitud(){
            RequestQueue myrequest = Volley.newRequestQueue(getActivity().getApplicationContext());
            JSONObject json = new JSONObject();

            try{
                //sacar de la base de datos
                //String token = "";

                json.put("to", "/topics/" + "topicUsuarios");
                JSONObject notificacion = new JSONObject();
                notificacion.put("titulo","Nuevas Solicitudes");
                notificacion.put("detalle","Se agregaron nuevas solicitudes");

                json.put("data", notificacion);

                String URL = "https://fcm.googleapis.com/fcm/send";

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null){
                    @Override
                    public Map<String, String> getHeaders(){
                        Map<String, String> header = new HashMap<>();

                        header.put("content-type","application/json");
                        header.put("authorization", "key=AAAASeeDWdM:APA91bHWiH23piw50MzPjVr_urfwRUTvDImu4797wZYLv6m22EkGAP0Z17aZ-8AYL4zeFw3XNZBOOb_SDJ0pLwEGd2doYDwbhD4ZtrkVMrqsunrcRMm28m2g9oOKMk2BkiCcpqJjDUzs");
                        return header;
                    }
                };

                myrequest.add(request);
            }catch(JSONException e){
                e.printStackTrace();
            }


    }

    private void limpiarPantalla() {
        txtDireccionOrigen.getEditText().setText("");
        txtDireccionDestino.getEditText().setText("");
        txtComentarios.getEditText().setText("");
        this.origen_lat = null;
        this.origen_lng = null;
        this.destino_lat = null;
        this.destino_lng = null;

        txtParada1.getEditText().setText("");
        txtParada2.getEditText().setText("");
        txtParada3.getEditText().setText("");

        this.parada1_lat = null;
        this.parada1_lng = null;
        this.parada2_lat = null;
        this.parada2_lng = null;
        this.parada3_lat = null;
        this.parada3_lng = null;

        layParada1.setVisibility(View.GONE);
        layParada2.setVisibility(View.GONE);
        layParada3.setVisibility(View.GONE);

        getUbicacion();

    }

    private boolean validarDireccionOrigen(){
        boolean valido = false;

        String val = txtDireccionOrigen.getEditText().getText().toString();

        if(val.isEmpty()){
            txtDireccionOrigen.setError("Introduzca la direccion del origen");
            valido = false;
        }else{
            txtDireccionOrigen.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarDireccionDestino(){
        boolean valido = false;

        String val = txtDireccionDestino.getEditText().getText().toString();

        if(val.isEmpty()){
            txtDireccionDestino.setError("Introduzca la direccion del destino");
            valido = false;
        }else{
            txtDireccionDestino.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarComentarios(){
        boolean valido = false;

        String val = txtComentarios.getEditText().getText().toString();

        if(val.isEmpty()){
            txtComentarios.setError("Introduzca los comentarios");
            valido = false;
        }else{
            txtComentarios.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarParada1(){
        boolean valido = false;

        String val = txtParada1.getEditText().getText().toString();
        if(layParada1.isShown()){
            if(val.isEmpty()){
                txtParada1.setError("Introduzca la direccion de la Parada 1");
                valido = false;
            }else{
                txtParada1.setError(null);
                valido = true;
            }
        }else{
            valido = true;
        }

        return valido;
    }

    private boolean validarParada2(){
        boolean valido = false;

        String val = txtParada2.getEditText().getText().toString();
        if(layParada2.isShown()){
            if(val.isEmpty()){
                txtParada2.setError("Introduzca la direccion de la Parada 2");
                valido = false;
            }else{
                txtParada2.setError(null);
                valido = true;
            }
        }else{
            valido = true;
        }

        return valido;
    }

    private boolean validarParada3(){
        boolean valido = false;

        String val = txtParada3.getEditText().getText().toString();
        if(layParada3.isShown()){
            if(val.isEmpty()){
                txtParada3.setError("Introduzca la direccion de la Parada 3");
                valido = false;
            }else{
                txtParada3.setError(null);
                valido = true;
            }
        }else{
            valido = true;
        }

        return valido;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtDireccionOrigen.getEditText().setText(place.getAddress());
            this.origen_lat = place.getLatLng().latitude;
            this.origen_lng = place.getLatLng().longitude;
        }if(requestCode ==200 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtDireccionDestino.getEditText().setText(place.getAddress());
            this.destino_lat = place.getLatLng().latitude;
            this.destino_lng = place.getLatLng().longitude;
        }if(requestCode ==300 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtParada1.getEditText().setText(place.getAddress());
            this.parada1_lat = place.getLatLng().latitude;
            this.parada1_lng = place.getLatLng().longitude;
        }if(requestCode ==400 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtParada2.getEditText().setText(place.getAddress());
            this.parada2_lat = place.getLatLng().latitude;
            this.parada2_lng = place.getLatLng().longitude;
        }if(requestCode ==500 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtParada3.getEditText().setText(place.getAddress());
            this.parada3_lat = place.getLatLng().latitude;
            this.parada3_lng = place.getLatLng().longitude;
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getActivity().getApplicationContext(),status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}
