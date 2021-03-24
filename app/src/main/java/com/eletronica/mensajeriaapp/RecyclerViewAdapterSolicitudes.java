package com.eletronica.mensajeriaapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;

import com.eletronica.mensajeriaapp.fragments.SolicitudesFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapterSolicitudes extends RecyclerView.Adapter<RecyclerViewAdapterSolicitudes.MyViewHolder> {
    private List<Pedido> TempPedidosList;
    Context context;

    FragmentManager fm;
    FragmentTransaction ft;
    CuadroDialogoSolicitud dialogoFragment;
    CuadroDialogoSolicitud tPrev;
    View mView;
    Fragment frag;
    public static final int DIALOGO_FRAGMENT = 1;


    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public RecyclerViewAdapterSolicitudes(@NonNull List<Pedido> data,
                                           @NonNull RecyclerViewOnItemClickListener
                                                   recyclerViewOnItemClickListener,  Context context, FragmentManager fm, View view, Fragment frag) {
        this.TempPedidosList = data;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
        this.context = context;
        this.fm = fm;
        this.mView = view;
        this.ft = fm.beginTransaction();
        this.frag = frag;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_solicitudes, parent, false);

        //LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //View row  = layoutInfiater.inflate(R.layout.listview_item_solicitudes, parent, false);



        return new MyViewHolder(row);
    }
    //private final ArrayList selected = new ArrayList<>();
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pedido pedido = TempPedidosList.get(position);
        //holder.getTxtNombreUsuario().setText(pedido.getUsuario());

        holder.getTxtNombreUsuario().setText(pedido.getNombre());
        Double valor1 = pedido.getCalificacion();
        DecimalFormat formato1 = new DecimalFormat("#,###.00");
        String valorFormateado1 = formato1.format(valor1);
        holder.getTxtCalificacion().setText(valorFormateado1);
        holder.getTxtOrigen().setText(pedido.getOrigen());
        holder.getTxtDestino().setText(pedido.getDestino());
        holder.getTxtDescripcionOrigen().setText(pedido.getDescripcion_origen());
        Double valor2 = pedido.getImporte();
        DecimalFormat formato2 = new DecimalFormat("#,###.00");
        String valorFormateado2 = formato2.format(valor2);
        holder.getTxtImporte().setText(valorFormateado2);

        int id = pedido.getId_usuario();
        GlobalVariables vg = new GlobalVariables();
        String imageUri = vg.URLServicio + "fotos/" + String.valueOf(id)+ ".jpg";
        Picasso.get().load(imageUri).into( holder.getIvUsuario());

        if(pedido.getParada1() != "null")
        {
            holder.getLayParada1().setVisibility(View.VISIBLE);
            holder.getIvParada1().setImageResource(R.drawable.icono_stop);
            holder.getTxtParada1().setText(pedido.getParada1());

        }


        if(pedido.getParada2() != "null")
        {
            holder.getLayParada2().setVisibility(View.VISIBLE);
            holder.getIvParada2().setImageResource(R.drawable.icono_stop);
            holder.getTxtParada2().setText(pedido.getParada2());

        }


        if(pedido.getParada3() != "null")
        {
            holder.getLayParada3().setVisibility(View.VISIBLE);
            holder.getIvParada3().setImageResource(R.drawable.icono_stop);
            holder.getTxtParada3().setText(pedido.getParada3());

        }

        holder.getIvOrigen().setImageResource(R.drawable.ubicacion1);
        holder.getIvDestino().setImageResource(R.drawable.ubicacion2);
        holder.getIvDescripcionOrigen().setImageResource(R.drawable.globo);
        holder.getIvImporte().setImageResource(R.drawable.precio);
        holder.getIvEstrella().setImageResource(R.drawable.estrella);
        //holder.getPersonImageView().setImageResource(R.drawable.selector_tecnicos);
        //set image with picasso.
        //permission required : android.permission.INTERNET
        //Picasso.get()
        //       .load(person.getUrlImage())
        //       .into(holder.getPersonImageView());

       // if (!selected.contains(position)){
       //     holder.getPersonImageView().setImageResource(R.drawable.user);
       // } else
        //    holder.getPersonImageView().setImageResource(R.drawable.user_2);

    }

    @Override
    public int getItemCount() {
        return TempPedidosList.size();
    }



    public void addItem(Pedido item, int position) {
        int tam = TempPedidosList.size();
        TempPedidosList.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, TempPedidosList.size());

    }

    public void removeItem(int position) {
        int tam = TempPedidosList.size();
        TempPedidosList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, TempPedidosList.size());

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
            TextView txtNombreUsuario;
            TextView txtCalificacion;
            TextView txtOrigen;
            TextView txtDestino;
            TextView txtDescripcionOrigen;
            TextView txtImporte;


            CircleImageView ivUsuario;
            ImageView ivOrigen;
            ImageView ivDestino;
            ImageView ivDescripcionOrigen;
            ImageView ivImporte;
            ImageView ivEstrella;

            TextView txtParada1;
            TextView txtParada2;
            TextView txtParada3;
            ImageView ivParada1;
            ImageView ivParada2;
            ImageView ivParada3;
            LinearLayout layParada1;
            LinearLayout layParada2;
            LinearLayout layParada3;


        public MyViewHolder(View itemView) {
            super(itemView);
            txtNombreUsuario = (TextView) itemView.findViewById(R.id.txtNombreUsuario);
            txtCalificacion = (TextView) itemView.findViewById(R.id.txtCalificacion);
            txtOrigen = (TextView) itemView.findViewById(R.id.txtOrigen);
            txtDestino = (TextView) itemView.findViewById(R.id.txtDestino);
            txtDescripcionOrigen = (TextView) itemView.findViewById(R.id.txtDescripcionOrigen);
            txtImporte = (TextView) itemView.findViewById(R.id.txtImporte);

            txtParada1 = (TextView) itemView.findViewById(R.id.txtParada1);
            txtParada2 = (TextView) itemView.findViewById(R.id.txtParada2);
            txtParada3 = (TextView) itemView.findViewById(R.id.txtParada3);

            layParada1 = (LinearLayout)  itemView.findViewById(R.id.layParada1Solicitudes);
            layParada2 = (LinearLayout)  itemView.findViewById(R.id.layParada2Solicitudes);
            layParada3 = (LinearLayout)  itemView.findViewById(R.id.layParada3Solicitudes);

            ivParada1 = (ImageView)  itemView.findViewById(R.id.ivParada1);
            ivParada2 = (ImageView)  itemView.findViewById(R.id.ivParada2);
            ivParada3 = (ImageView)  itemView.findViewById(R.id.ivParada3);


            ivUsuario = (CircleImageView)  itemView.findViewById(R.id.ivUsuarioDialogoSolicitud);
            ivOrigen = (ImageView)  itemView.findViewById(R.id.ivOrigen);
            ivDestino = (ImageView)  itemView.findViewById(R.id.ivDestino);
            ivDescripcionOrigen = (ImageView)  itemView.findViewById(R.id.ivDescripcionOrigen);
            ivImporte = (ImageView)  itemView.findViewById(R.id.ivImporte);
            ivEstrella = (ImageView)  itemView.findViewById(R.id.ivEstrella);

            itemView.setOnClickListener(this);


        }

        public TextView getTxtNombreUsuario() {
            return txtNombreUsuario;
        }

        public TextView getTxtCalificacion() {
            return txtCalificacion;
        }

        public TextView getTxtOrigen() {
            return txtOrigen;
        }

        public TextView getTxtDestino() {
            return txtDestino;
        }

        public TextView getTxtDescripcionOrigen() {
            return txtDescripcionOrigen;
        }

        public TextView getTxtImporte() {
            return txtImporte;
        }

        public TextView getTxtParada1() {
            return txtParada1;
        }

        public TextView getTxtParada2() {
            return txtParada2;
        }

        public TextView getTxtParada3() {
            return txtParada3;
        }

        public LinearLayout getLayParada1() {
            return layParada1;
        }

        public LinearLayout getLayParada2() {
            return layParada2;
        }

        public LinearLayout getLayParada3() {
            return layParada3;
        }

        public ImageView getIvParada1() {
            return ivParada1;
        }

        public ImageView getIvParada2() {
            return ivParada2;
        }

        public ImageView getIvParada3() {
            return ivParada3;
        }

        public CircleImageView getIvUsuario() {
            return ivUsuario;
        }

        public ImageView getIvOrigen() {
            return ivOrigen;
        }

        public ImageView getIvDestino() {
            return ivDestino;
        }

        public ImageView getIvDescripcionOrigen() {
            return ivDescripcionOrigen;
        }

        public ImageView getIvImporte() {
            return ivImporte;
        }

        public ImageView getIvEstrella() {
            return ivEstrella;
        }



        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Pedido pedido = (Pedido) TempPedidosList.get(position);

            dialogoFragment = new CuadroDialogoSolicitud(context, fm, mView);
            Bundle b = new Bundle();
            b.putSerializable("pedido", (Serializable)pedido);

            dialogoFragment.setArguments(b);
            tPrev =  (CuadroDialogoSolicitud) fm.findFragmentByTag("dialogoSolicitud");

            if(tPrev!=null)
                ft.remove(tPrev);

            dialogoFragment.setTargetFragment(frag, DIALOGO_FRAGMENT);
            dialogoFragment.show(fm, "dialogoSolicitud");


           // Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();

           // recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());

            /*if (selected.isEmpty()) {
                selected.add(position);
            } else {
                int oldSelected = (int) selected.get(0);
                selected.clear();
                selected.add(position);
                notifyItemChanged(oldSelected);
            }*/
            // we do not notify that an item has been selected
            // because that work is done here. we instead send
            // notifications for items to be deselected




        }

    }

}
