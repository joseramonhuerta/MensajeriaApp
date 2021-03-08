package com.eletronica.mensajeriaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class ListViewAdapterSolicitudes extends BaseAdapter{
    List<Pedido> TempPedidosList;


    Context context;




    //@Override



    public ListViewAdapterSolicitudes(List<Pedido> listValue, Context context)
    {
        this.context = context;
        this.TempPedidosList = listValue;

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


            viewItem.ivUsuario = (ImageView) convertView.findViewById(R.id.ivUsuarioDialogoSolicitud);
            viewItem.ivUsuario.setImageResource(R.drawable.user_2);

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


    ImageView ivUsuario;
    ImageView ivOrigen;
    ImageView ivDestino;
    ImageView ivDescripcionOrigen;
    ImageView ivImporte;
    ImageView ivEstrella;



}