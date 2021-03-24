package com.eletronica.mensajeriaapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListViewAdapterSolicitudes extends BaseAdapter{
    List<Pedido> TempPedidosList;


    Context context;




    //@Override



    public ListViewAdapterSolicitudes(List<Pedido> listValue, Context context)
    {
        this.context = context;
        this.TempPedidosList = listValue;

    }

    public void updateReceiptsList(List<Pedido> newlist) {
        this.TempPedidosList = newlist;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return this.TempPedidosList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempPedidosList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ViewItem viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItem();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_item_solicitudes, parent, false);

            viewItem.txtNombreUsuario = (TextView)convertView.findViewById(R.id.txtNombreUsuario);
            viewItem.txtCalificacion = (TextView)convertView.findViewById(R.id.txtCalificacion);
            viewItem.txtOrigen = (TextView)convertView.findViewById(R.id.txtOrigen);
            viewItem.txtDestino = (TextView)convertView.findViewById(R.id.txtDestino);
            viewItem.txtDescripcionOrigen = (TextView)convertView.findViewById(R.id.txtDescripcionOrigen);
            viewItem.txtImporte = (TextView)convertView.findViewById(R.id.txtImporte);

            viewItem.txtParada1 = (TextView)convertView.findViewById(R.id.txtParada1);
            viewItem.txtParada2 = (TextView)convertView.findViewById(R.id.txtParada2);
            viewItem.txtParada3 = (TextView)convertView.findViewById(R.id.txtParada3);

            viewItem.layParada1 = (LinearLayout) convertView.findViewById(R.id.layParada1Solicitudes);
            viewItem.layParada2 = (LinearLayout) convertView.findViewById(R.id.layParada2Solicitudes);
            viewItem.layParada3 = (LinearLayout) convertView.findViewById(R.id.layParada3Solicitudes);

            viewItem.ivParada1 = (ImageView) convertView.findViewById(R.id.ivParada1);
            viewItem.ivParada2 = (ImageView) convertView.findViewById(R.id.ivParada2);
            viewItem.ivParada3 = (ImageView) convertView.findViewById(R.id.ivParada3);


            viewItem.ivUsuario = (CircleImageView) convertView.findViewById(R.id.ivUsuarioDialogoSolicitud);


            viewItem.ivOrigen = (ImageView) convertView.findViewById(R.id.ivOrigen);
            viewItem.ivOrigen.setImageResource(R.drawable.ubicacion1);

            viewItem.ivDestino = (ImageView) convertView.findViewById(R.id.ivDestino);
            viewItem.ivDestino.setImageResource(R.drawable.ubicacion2);

            viewItem.ivDescripcionOrigen = (ImageView) convertView.findViewById(R.id.ivDescripcionOrigen);
            viewItem.ivDescripcionOrigen.setImageResource(R.drawable.globo);

            viewItem.ivImporte = (ImageView) convertView.findViewById(R.id.ivImporte);
            viewItem.ivImporte.setImageResource(R.drawable.precio);

            viewItem.ivEstrella = (ImageView) convertView.findViewById(R.id.ivEstrella);
            viewItem.ivEstrella.setImageResource(R.drawable.estrella);





            //viewItem.btnLlamar.setTag(position);




            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItem) convertView.getTag();
        }

        viewItem.txtNombreUsuario.setText(TempPedidosList.get(position).getNombre());
        Double valor1 = TempPedidosList.get(position).getCalificacion();
        DecimalFormat formato1 = new DecimalFormat("#,###.00");
        String valorFormateado1 = formato1.format(valor1);
        viewItem.txtCalificacion.setText(valorFormateado1);
        viewItem.txtOrigen.setText(TempPedidosList.get(position).getOrigen());
        viewItem.txtDestino.setText(TempPedidosList.get(position).getDestino());
        viewItem.txtDescripcionOrigen.setText(TempPedidosList.get(position).getDescripcion_origen());
        Double valor2 = TempPedidosList.get(position).getImporte();
        DecimalFormat formato2 = new DecimalFormat("#,###.00");
        String valorFormateado2 = formato2.format(valor2);
        viewItem.txtImporte.setText(valorFormateado2);
        /*
        if (TempPedidosList.get(position).getFoto() != null) {
            byte[] encodeByte = (byte[]) (TempPedidosList.get(position).getFoto());
            if(encodeByte.length > 0){
                Bitmap photobmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                viewItem.ivUsuario.setImageBitmap(photobmp);

            }
        }*/




        return convertView;
    }
}


class ViewItem
{
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


}