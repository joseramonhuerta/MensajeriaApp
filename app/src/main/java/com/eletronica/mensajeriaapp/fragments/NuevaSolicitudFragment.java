package com.eletronica.mensajeriaapp.fragments;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NuevaSolicitudFragment extends Fragment {
    TextInputLayout txtDireccionOrigen, txtDireccionDestino, txtComentarios;
    Button btnSolicitar, btnCancelar;
    Double origen_lat=null,origen_lng=null,destino_lat=null,destino_lng=null;
    View mView;
    ProgressBar progressBar;
    RequestQueue rq;
    JsonObjectRequest jrq;
    public NuevaSolicitudFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nueva_solicitud, container, false);
        mView = view;
        rq = Volley.newRequestQueue(getActivity().getApplicationContext());
        txtDireccionOrigen = (TextInputLayout) view.findViewById(R.id.txtDireccionOrigen);
        txtDireccionDestino = (TextInputLayout) view.findViewById(R.id.txtDireccionDestino);
        txtComentarios = (TextInputLayout) view.findViewById(R.id.txtComentarios);
        btnSolicitar = (Button) view.findViewById(R.id.btnAceptarNuevaSolicitud);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelarSolicitud);
        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBarNuevaSolicitud);

        Places.initialize(getActivity().getApplicationContext(),"AIzaSyBxcLXHYB8gQW5Xg112ivn6Gko3YyOveKU");

        txtDireccionOrigen.getEditText().setFocusable(false);
        txtDireccionOrigen.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                startActivityForResult(intent, 100);
            }
        });

        txtDireccionDestino.getEditText().setFocusable(false);
        txtDireccionDestino.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("MX").build(getActivity().getApplicationContext());
                startActivityForResult(intent, 200);
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
                if(!validarDireccionOrigen() | !validarDireccionDestino() | !validarComentarios()){
                    return;
                }

                registrarServicio();
            }
        });


        return view;
    }

    private void registrarServicio(){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();

        Location location = new Location("localizacion 1");
        location.setLatitude(this.origen_lat);  //latitud
        location.setLongitude(this.origen_lng); //longitud
        Location location2 = new Location("localizacion 2");
        location2.setLatitude(this.destino_lat);  //latitud
        location2.setLongitude(this.destino_lng); //longitud
        double distance = location.distanceTo(location2) / 1000;

        String origen = txtDireccionOrigen.getEditText().getText().toString();
        String destino = txtDireccionDestino.getEditText().getText().toString();
        String comentarios = txtComentarios.getEditText().getText().toString();
        String origenlat = String.valueOf(this.origen_lat);
        String origenlng = String.valueOf(this.origen_lng);
        String destinolat = String.valueOf(this.destino_lat);
        String destinolng = String.valueOf(this.destino_lng);
        String distancia = String.valueOf(distance);

        String url = variablesGlobales.URLServicio + "registra_nueva_solicitud.php?id_usuario="+variablesGlobales.id_usuario+"&origen="+origen+"&destino="+destino+"&comentarios="+comentarios+"&origenlat="+origenlat+"&origenlng="+origenlng+"&destinolat="+destinolat+"&destinolng="+destinolng+"&distancia="+distancia;
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

    private void limpiarPantalla() {
        txtDireccionOrigen.getEditText().setText("");
        txtDireccionDestino.getEditText().setText("");
        txtComentarios.getEditText().setText("");
        this.origen_lat = null;
        this.origen_lng = null;
        this.destino_lat = null;
        this.destino_lng = null;
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
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getActivity().getApplicationContext(),status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
