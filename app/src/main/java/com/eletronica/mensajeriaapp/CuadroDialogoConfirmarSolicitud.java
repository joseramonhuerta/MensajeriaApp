package com.eletronica.mensajeriaapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class CuadroDialogoConfirmarSolicitud extends DialogFragment  {
    FragmentManager fm;
    View mView;
    Context mContext;

    Dialog dialogo = null;
    int id_pedido = 0;
    int id_usuario = 0;
    double distancia = 0.0;
    Pedido pedido = null;

    TextView txtImporte;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcionOrigen;
    TextView txtParada1;
    TextView txtParada2;
    TextView txtParada3;
    LinearLayout layParada1;
    LinearLayout layParada2;
    LinearLayout layParada3;


    RequestQueue rq;
    JsonRequest jrq;
    RequestQueue request;

    ProgressBar progressBar;

    public interface Confirmar {

        public void solicitudConfirmada();

    }
    Confirmar listener;

    public CuadroDialogoConfirmarSolicitud(Context context, FragmentManager fm,View view) {
        this.mContext = context;
        this.fm = fm;
        this.mView = view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Confirmar) getTargetFragment();
            //activity = getTargetFragment().getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cuadro_dialogo_confirmar_servicio, null);
        Bundle b = getArguments();
        rq = Volley.newRequestQueue(v.getContext());

        txtImporte = (TextView) v.findViewById(R.id.txtImporteConfirmarSolicitud);

        txtOrigen = (TextView) v.findViewById(R.id.txtOrigenDialogoConfirmarSolicitud);
        txtDestino = (TextView) v.findViewById(R.id.txtDestinoDialogoConfirmarSolicitud);
        txtDescripcionOrigen = (TextView) v.findViewById(R.id.txtDescripcionOrigenDialogoConfirmarSolicitud);
        txtParada1 = (TextView) v.findViewById(R.id.txtParada1DialogoConfirmarSolicitud);
        txtParada2 = (TextView) v.findViewById(R.id.txtParada2DialogoConfirmarSolicitud);
        txtParada3 = (TextView) v.findViewById(R.id.txtParada3DialogoConfirmarSolicitud);
        layParada1 = (LinearLayout) v.findViewById(R.id.layParada1DialogoConfirmarSolicitud);
        layParada2 = (LinearLayout) v.findViewById(R.id.layParada2DialogoConfirmarSolicitud);
        layParada3 = (LinearLayout) v.findViewById(R.id.layParada3DialogoConfirmarSolicitud);


        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarConfirmarSolicitud);
        final Button btnAceptar = (Button) v.findViewById(R.id.btnAceptarDialogoConfirmarSolicitud);
        final Button btnSalir = (Button) v.findViewById(R.id.btnSalirDialogoConfirmarSolicitud);

        //distancia = b.getDouble("distancia");
        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        String origen = b.getString("origen");
        String destino = b.getString("destino");
        String comentarios = b.getString("comentarios");
        String parada1 = b.getString("parada1");
        String parada2 = b.getString("parada2");
        String parada3 = b.getString("parada3");
        Double tarifa = b.getDouble("tarifa");

        DecimalFormat formato1 = new DecimalFormat("#,###.00");
        String valorFormateado1 = formato1.format(tarifa);
        txtImporte.setText("$ " + valorFormateado1);

        txtOrigen.setText(origen);
        txtDestino.setText(destino);
        txtDescripcionOrigen.setText(comentarios);

        if(!parada1.isEmpty()){
            layParada1.setVisibility(View.VISIBLE);
            txtParada1.setText(parada1);
        }

        if(!parada2.isEmpty() ){
            layParada2.setVisibility(View.VISIBLE);
            txtParada2.setText(parada2);
        }

        if(!parada3.isEmpty()){
            layParada3.setVisibility(View.VISIBLE);
            txtParada3.setText(parada3);
        }


        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aceptarSolicitud();
                dialogo.dismiss();
                listener.solicitudConfirmada();

            }
        });

        //mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapaDialogoSolicitud);
        //mapFragment.getMapAsync(this);
        //initGoogleMap(savedInstanceState);

        return v;


    }


}
