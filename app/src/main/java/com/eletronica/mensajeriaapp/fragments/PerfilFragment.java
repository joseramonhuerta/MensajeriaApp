package com.eletronica.mensajeriaapp.fragments;


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

import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    Bitmap photobmp = null;
    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        TextView txtNombreUsuario = (TextView) view.findViewById(R.id.txtNombrePerfil);
        TextView txtUsuario = (TextView) view.findViewById(R.id.txtUsuarioPerfil);
        TextView txtPerfil = (TextView) view.findViewById(R.id.txtPerfil);
        LinearLayout btnSalir = (LinearLayout) view.findViewById(R.id.laySalir);
        ImageView ivFoto = (ImageView) view.findViewById(R.id.imageViewPerfil);

        GlobalVariables vg = new GlobalVariables();

        txtNombreUsuario.setText(vg.nombre);
        txtUsuario.setText(vg.usuario);
        txtPerfil.setText(vg.descripcion_rol);

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        return view;
    }

}
