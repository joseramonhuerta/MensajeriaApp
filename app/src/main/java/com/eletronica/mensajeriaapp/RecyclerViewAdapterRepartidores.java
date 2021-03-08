package com.eletronica.mensajeriaapp;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

public class RecyclerViewAdapterRepartidores extends RecyclerView.Adapter<RecyclerViewAdapterRepartidores.MyViewHolder> {
    private List<User> data;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public RecyclerViewAdapterRepartidores(@NonNull List<User> data,
                                 @NonNull RecyclerViewOnItemClickListener
                                         recyclerViewOnItemClickListener) {
        this.data = data;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_repartidor, parent, false);



        return new MyViewHolder(row);
    }
    private final ArrayList selected = new ArrayList<>();
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User person = data.get(position);
        holder.getNameTextView().setText(person.getUsuario());
        //holder.getPersonImageView().setImageResource(R.drawable.selector_tecnicos);
        //set image with picasso.
        //permission required : android.permission.INTERNET
        //Picasso.get()
        //       .load(person.getUrlImage())
        //       .into(holder.getPersonImageView());

        if (!selected.contains(position)){
            holder.getPersonImageView().setImageResource(R.drawable.user);
        } else
            holder.getPersonImageView().setImageResource(R.drawable.user_2);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private TextView txtNombreRepartidor;
        private ImageView ivImagenRepartidor;


        public MyViewHolder(View itemView) {
            super(itemView);
            txtNombreRepartidor = (TextView) itemView.findViewById(R.id.txtNombreRepartidor);
            ivImagenRepartidor = (ImageView) itemView.findViewById(R.id.ivImagenRepartidor);
            itemView.setOnClickListener(this);


        }

        public TextView getNameTextView() {
            return txtNombreRepartidor;
        }

        public ImageView getPersonImageView() {
            return ivImagenRepartidor;
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            ivImagenRepartidor = (ImageView) itemView.findViewById(R.id.ivImagenRepartidor);
            ivImagenRepartidor.setImageResource(R.drawable.user_2);
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());

            if (selected.isEmpty()) {
                selected.add(position);
            } else {
                int oldSelected = (int) selected.get(0);
                selected.clear();
                selected.add(position);
                notifyItemChanged(oldSelected);
            }
            // we do not notify that an item has been selected
            // because that work is done here. we instead send
            // notifications for items to be deselected


        }

    }

}
