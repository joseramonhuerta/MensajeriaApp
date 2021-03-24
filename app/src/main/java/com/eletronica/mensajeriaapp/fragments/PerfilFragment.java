package com.eletronica.mensajeriaapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.R;

import org.json.JSONException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    RequestQueue request;
    Bitmap photobmp = null;
    GlobalVariables vg;

    TextView txtNombreUsuario;
    TextView txtUsuario;
    TextView txtPerfil;
    LinearLayout btnSalir;
    CircleImageView ivFoto;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txtNombreUsuario = (TextView) view.findViewById(R.id.txtNombrePerfil);
        txtUsuario = (TextView) view.findViewById(R.id.txtUsuarioPerfil);
        txtPerfil = (TextView) view.findViewById(R.id.txtPerfil);
        btnSalir = (LinearLayout) view.findViewById(R.id.laySalir);
        //ImageView ivFoto = (ImageView) view.findViewById(R.id.imageViewPerfil);
        ivFoto = (CircleImageView) view.findViewById(R.id.imageViewPerfil);

        prefs = getContext().getSharedPreferences("MisPreferenciasMensajeria", Context.MODE_PRIVATE);
        editor = prefs.edit();
        request = Volley.newRequestQueue(getContext());

        vg = new GlobalVariables();

        txtNombreUsuario.setText(vg.nombre);
        txtUsuario.setText(vg.usuario);
        txtPerfil.setText(vg.descripcion_rol);
        /*
        if (vg.foto != null) {
            byte[] encodeByte = (byte[]) (vg.foto);
            if(encodeByte.length > 0){
                photobmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                ivFoto.setImageBitmap(photobmp);

            }
        }else
        {
            ivFoto.setImageResource(R.drawable.user_2);
        }*/

        cargarImagenUsuario();

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                getActivity().finish();
            }
        });


        return view;
    }

    public void cargarImagenUsuario(){

        String urlImg = vg.URLServicio + "fotos/" + String.valueOf(vg.id_usuario)+".jpg";
        urlImg = urlImg.replace(" ","%20");
        try{
            ImageRequest imageRequest = new ImageRequest(urlImg, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    ivFoto.setImageBitmap(response);
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

}
