package com.eletronica.mensajeriaapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import static java.lang.Integer.parseInt;

public class CuadroDialogoSolicitud extends DialogFragment implements OnMapReadyCallback, Response.Listener<JSONObject>, Response.ErrorListener {
    Context mContext;
    FragmentManager fm;
    View mView;

    GoogleMap mMap;
    SupportMapFragment mapFragment;

    Double origen_latitud;
    Double origen_longitud;
    Double destino_latitud;
    Double destino_longitud;

    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    Dialog dialogo = null;
    int id_pedido = 0;
    Pedido pedido = null;

    RequestQueue rq;
    JsonRequest jrq;

    ProgressBar progressBar;

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
        pedido = (Pedido) b.getSerializable("pedido");
        id_pedido = pedido.getId_pedido();
        String nombre = pedido.getNombre();
        Double calificacion = pedido.getCalificacion();
        String origen = pedido.getOrigen();
        String destino = pedido.getDestino();
        String descripcion_origen = pedido.getDescripcion_origen();
        Double importe = pedido.getImporte();

        origen_latitud = pedido.getOrigen_latitud();
        origen_longitud = pedido.getOrigen_longitud();
        destino_latitud = pedido.getDestino_latitud();
        destino_longitud = pedido.getDestino_longitud();

        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo
                .setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtNombre = (TextView) v.findViewById(R.id.txtNombreUsuarioDialogoSolicitud);
        TextView txtCalificacion = (TextView) v.findViewById(R.id.txtCalificacionDialogoSolicitud);
        TextView txtOrigen = (TextView) v.findViewById(R.id.txtOrigenDialogoSolicitud);
        TextView txtDestino = (TextView) v.findViewById(R.id.txtDestinoDialogoSolicitud);
        TextView txtDescripcionOrigen = (TextView) v.findViewById(R.id.txtDescripcionOrigenDialogoSolicitud);
        TextView txtImporte = (TextView) v.findViewById(R.id.txtImporteDialogoSolicitud);
        final Button btnAceptar = (Button) v.findViewById(R.id.btnAceptarDialogoSolicitud);
        final Button btnSalir = (Button) v.findViewById(R.id.btnSalirDialogoSolicitud);
        //mMapView = (MapView) v.findViewById(R.id.mapaDialogoSolicitud);
        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarSolicitud);
        txtNombre.setText(nombre);
        DecimalFormat formato1 = new DecimalFormat("#,###.00");
        String valorFormateado1 = formato1.format(calificacion);
        txtCalificacion.setText(valorFormateado1);

        txtOrigen.setText(origen);
        txtDestino.setText(destino);
        txtDescripcionOrigen.setText(descripcion_origen);

        String valorFormateado2 = formato1.format(importe);
        txtImporte.setText(valorFormateado2);

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
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        //mMap.setMyLocationEnabled(true);
        LatLng we = new LatLng(23.2340033,-106.4271412);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(we,15));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            //    != PackageManager.PERMISSION_GRANTED
            //    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            //    != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
      //  }
        //23.2336172,origen_longitud=106.4153152,destino_latitud=23.2313368,destino_longitud=106.4072385
        //map.setMyLocationEnabled(true);
        setUpMap();

        LatLng marcador1 = new LatLng(origen_latitud, origen_longitud);
        LatLng marcador2 = new LatLng(destino_latitud, destino_longitud);

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

}
