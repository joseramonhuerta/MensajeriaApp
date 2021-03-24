package com.eletronica.mensajeriaapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Integer.parseInt;

public class CuadroDialogoSolicitud extends DialogFragment implements OnMapReadyCallback, Response.Listener<JSONObject>, Response.ErrorListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Context mContext;
    FragmentManager fm;
    View mView;

    GoogleMap mMap;
    SupportMapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


    Double origen_latitud;
    Double origen_longitud;
    Double destino_latitud;
    Double destino_longitud;

    String parada1 = null;
    String parada2 = null;
    String parada3 = null;
    Double parada1_latitud;
    Double parada1_longitud;
    Double parada2_latitud;
    Double parada2_longitud;
    Double parada3_latitud;
    Double parada3_longitud;

    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    Dialog dialogo = null;
    int id_pedido = 0;
    int id_usuario = 0;
    Pedido pedido = null;

    CircleImageView ivImagen;

    RequestQueue rq;
    JsonRequest jrq;
    RequestQueue request;

    ProgressBar progressBar;

    GlobalVariables vg;

    public interface Actualizar {

        public void actualizaActividad();

    }
    Actualizar listener;
    Activity activity;

    public CuadroDialogoSolicitud(Context context, FragmentManager fm, View view) {
        this.mContext = context;
        this.fm = fm;
        this.mView = view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Actualizar) getTargetFragment();
            activity = getTargetFragment().getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cuadro_dialogo_solicitud, null);
        Bundle b = getArguments();
        rq = Volley.newRequestQueue(v.getContext());
        request = Volley.newRequestQueue(getContext());
        pedido = (Pedido) b.getSerializable("pedido");
        id_pedido = pedido.getId_pedido();
        id_usuario = pedido.getId_usuario();
        String nombre = pedido.getNombre();
        Double calificacion = pedido.getCalificacion();
        String origen = pedido.getOrigen();
        String destino = pedido.getDestino();
        parada1 = pedido.getParada1();
        parada2 = pedido.getParada2();
        parada3 = pedido.getParada3();
        String descripcion_origen = pedido.getDescripcion_origen();
        Double importe = pedido.getImporte();
        byte[] foto = pedido.getFoto();

        origen_latitud = pedido.getOrigen_latitud();
        origen_longitud = pedido.getOrigen_longitud();
        destino_latitud = pedido.getDestino_latitud();
        destino_longitud = pedido.getDestino_longitud();

        parada1_latitud = pedido.getParada_latitud_1();
        parada1_longitud = pedido.getParada_longitud_1();
        parada2_latitud = pedido.getParada_latitud_2();
        parada2_longitud = pedido.getParada_longitud_2();
        parada3_latitud = pedido.getParada_latitud_3();
        parada3_longitud = pedido.getParada_longitud_3();

        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtNombre = (TextView) v.findViewById(R.id.txtNombreUsuarioDialogoSolicitud);
        TextView txtCalificacion = (TextView) v.findViewById(R.id.txtCalificacionDialogoSolicitud);
        TextView txtOrigen = (TextView) v.findViewById(R.id.txtOrigenDialogoSolicitud);
        TextView txtDestino = (TextView) v.findViewById(R.id.txtDestinoDialogoSolicitud);
        TextView txtDescripcionOrigen = (TextView) v.findViewById(R.id.txtDescripcionOrigenDialogoSolicitud);
        TextView txtImporte = (TextView) v.findViewById(R.id.txtImporteDialogoSolicitud);
        ivImagen = (CircleImageView) v.findViewById(R.id.ivUsuarioDialogo);
        final Button btnAceptar = (Button) v.findViewById(R.id.btnAceptarDialogoSolicitud);
        final Button btnSalir = (Button) v.findViewById(R.id.btnSalirDialogoSolicitud);

        TextView txtParada1 = (TextView) v.findViewById(R.id.txtParada1DialogoSolicitud);
        TextView txtParada2 = (TextView) v.findViewById(R.id.txtParada2DialogoSolicitud);
        TextView txtParada3 = (TextView) v.findViewById(R.id.txtParada3DialogoSolicitud);

        LinearLayout layParada1 = (LinearLayout) v.findViewById(R.id.layParada1DialogoSolicitud);
        LinearLayout layParada2 = (LinearLayout) v.findViewById(R.id.layParada2DialogoSolicitud);
        LinearLayout layParada3 = (LinearLayout) v.findViewById(R.id.layParada3DialogoSolicitud);

        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarSolicitud);
        txtNombre.setText(nombre);
        DecimalFormat formato1 = new DecimalFormat("#,###.00");
        String valorFormateado1 = formato1.format(calificacion);
        txtCalificacion.setText(valorFormateado1);

        txtOrigen.setText(origen);
        txtDestino.setText(destino);
        txtDescripcionOrigen.setText(descripcion_origen);

        vg = new GlobalVariables();

        if(parada1 != "null"){
            layParada1.setVisibility(View.VISIBLE);
            txtParada1.setText(parada1);
        }

        if(parada2 != "null"){
            layParada2.setVisibility(View.VISIBLE);
            txtParada2.setText(parada2);
        }

        if(parada3 != "null"){
            layParada3.setVisibility(View.VISIBLE);
            txtParada3.setText(parada3);
        }

        String valorFormateado2 = formato1.format(importe);
        txtImporte.setText(valorFormateado2);
        /*
        if (foto != null) {
            byte[] encodeByte = (byte[]) (foto);
            if(encodeByte.length > 0){
                Bitmap photobmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                ivImagen.setImageBitmap(photobmp);

            }
        }*/

        cargarImagenUsuario();



        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptarSolicitud();

            }
        });

        //mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapaDialogoSolicitud);
        //mapFragment.getMapAsync(this);
        //initGoogleMap(savedInstanceState);

        return v;


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
            Toast.makeText(getContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void aceptarSolicitud() {
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "aceptar_solicitud.php?id_usuario="+String.valueOf(variablesGlobales.id_usuario)+
                "&id_pedido="+String.valueOf(id_pedido);
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapaDialogoSolicitud, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(mView.getContext(),error.getMessage().toString(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);

        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));

            if(success){
                Bundle b = new Bundle();

                listener.actualizaActividad();
                Intent intencion = new Intent(getActivity(), SolicitudEnCurso.class);
                intencion.putExtra("pedido", (Serializable)pedido);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }

            String msg = jsonObject.optString("msg");
            Toast.makeText(mView.getContext(),msg, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(mView.getContext(),"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }

        dialogo.dismiss();


    }


    private void initGoogleMap(Bundle savedInstanceState) {
        /*Bundle mapViewbundle = null;
        if(savedInstanceState != null){
            mapViewbundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewbundle);
        mMapView.getMapAsync(this);*/

        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapaDialogoSolicitud);
        //mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapFragment.onStop();
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng we = new LatLng(23.2340033,-106.4271412);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(we,12));



        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng marcador1 = new LatLng(origen_latitud, origen_longitud);
                LatLng marcador2 = new LatLng(destino_latitud, destino_longitud);

                if(parada1 != "null"){
                    LatLng stop1 = new LatLng(parada1_latitud, parada1_longitud);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop1)
                            .title(parada1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                if(parada2 != "null"){
                    LatLng stop2 = new LatLng(parada2_latitud, parada2_longitud);
                    mMap.addMarker(new MarkerOptions()
                            .position(stop2)
                            .title(parada2)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_stop)));
                }

                if(parada3 != "null"){
                    LatLng stop3 = new LatLng(parada3_latitud, parada3_longitud);
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

                //mMap.animateCamera(centrarmarcadores);
                mMap.moveCamera(centrarmarcadores);


            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
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
    }

    @Override
    public void onPause() {
        mapFragment.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapFragment.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        assert getFragmentManager() != null;
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapaDialogoSolicitud));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
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
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
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

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                        setUpMap();
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

}
