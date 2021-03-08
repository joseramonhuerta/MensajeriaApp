package com.eletronica.mensajeriaapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapterResumen extends BaseAdapter{
    List<Pedido> TempPedidosList;
    Context context;
    private static final int HEADER_ITEM = 1;
    private static final int PEDIDO_ITEM = 2;
    //LayoutInflater layoutInflater;

    public ListViewAdapterResumen(List<Pedido> listValue, Context context)
    {
        this.context = context;
        this.TempPedidosList = listValue;
        //layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
    /*
    @Override
    public int getItemViewType(int position){
        if(this.TempPedidosList.get(position) instanceof Pedido){
            return this.PEDIDO_ITEM;
        }else{
            return this.HEADER_ITEM;
        }
    }
    */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ViewItemPedido viewItemP = null;

        if (convertView == null) {
            viewItemP = new ViewItemPedido();

            LayoutInflater layoutInflater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.listview_item_pedido, parent, false);

            viewItemP.ivReloj = (ImageView) convertView.findViewById(R.id.ivRelojResumen);
            viewItemP.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatusResumen);
            viewItemP.ivUsuario = (ImageView) convertView.findViewById(R.id.ivUsuarioResumen);
            viewItemP.ivEstrella = (ImageView) convertView.findViewById(R.id.ivEstrellaResumen);
            viewItemP.ivOrigen = (ImageView) convertView.findViewById(R.id.ivOrigenResumen);
            viewItemP.ivDestino = (ImageView) convertView.findViewById(R.id.ivDestinoResumen);
            viewItemP.ivDescripcion = (ImageView) convertView.findViewById(R.id.ivDescripcionOrigenResumen);
            viewItemP.ivImporte = (ImageView) convertView.findViewById(R.id.ivImporteResumen);

            viewItemP.txtHora = (TextView) convertView.findViewById(R.id.txtHoraResumen);
            viewItemP.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusResumen);
            viewItemP.txtNombre = (TextView) convertView.findViewById(R.id.txtNombreUsuarioResumen);
            viewItemP.txtCalificacion = (TextView) convertView.findViewById(R.id.txtCalificacionResumen);
            viewItemP.txtOrigen = (TextView) convertView.findViewById(R.id.txtOrigenResumen);
            viewItemP.txtDestino = (TextView) convertView.findViewById(R.id.txtDestinoResumen);
            viewItemP.txtDescripcion = (TextView) convertView.findViewById(R.id.txtDescripcionOrigenResumen);
            viewItemP.txtImporte = (TextView) convertView.findViewById(R.id.txtImporteResumen);

            viewItemP.layHeader = (LinearLayout) convertView.findViewById(R.id.layResumenHeader);

            viewItemP.txtFechaHeader = (TextView) convertView.findViewById(R.id.textFechaResumenHeader);
            viewItemP.txtTotalHeader = (TextView) convertView.findViewById(R.id.txtTotalResumenHeader);

            viewItemP.ivReloj.setImageResource(R.drawable.icono_reloj);
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_0);
            viewItemP.ivUsuario.setImageResource(R.drawable.user_2);
            viewItemP.ivEstrella.setImageResource(R.drawable.estrella);
            viewItemP.ivOrigen.setImageResource(R.drawable.icono_origen);
            viewItemP.ivDestino.setImageResource(R.drawable.icono_destino);
            viewItemP.ivDescripcion.setImageResource(R.drawable.globo);
            viewItemP.ivImporte.setImageResource(R.drawable.precio);

            convertView.setTag(viewItemP);
        } else {
            viewItemP = (ViewItemPedido) convertView.getTag();
        }

        viewItemP.txtHora.setText(TempPedidosList.get(position).getHora());
        viewItemP.txtStatus.setText(TempPedidosList.get(position).getStatus_descripcion());
        viewItemP.txtNombre.setText(TempPedidosList.get(position).getNombre());
        viewItemP.txtCalificacion.setText(String.valueOf(TempPedidosList.get(position).getCalificacion()));
        viewItemP.txtOrigen.setText(TempPedidosList.get(position).getOrigen());
        viewItemP.txtDestino.setText(TempPedidosList.get(position).getDestino());
        viewItemP.txtDescripcion.setText(TempPedidosList.get(position).getDescripcion_origen());
        viewItemP.txtImporte.setText(String.valueOf(TempPedidosList.get(position).getImporte()));
        viewItemP.txtFechaHeader.setText( TempPedidosList.get(position).getFecha_header());
        viewItemP.txtTotalHeader.setText(String.valueOf( TempPedidosList.get(position).getTotal_header()));

        viewItemP.layHeader.setVisibility(convertView.GONE);

        if(TempPedidosList.get(position).getTemplate() == 1)
            viewItemP.layHeader.setVisibility(convertView.VISIBLE);




        if(TempPedidosList.get(position).getStatus() == 0)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_0);
        if(TempPedidosList.get(position).getStatus() == 1)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_1);
        if(TempPedidosList.get(position).getStatus() == 2)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_2);
        if(TempPedidosList.get(position).getStatus() == 3)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_3);


        if(TempPedidosList.get(position).getStatus() == 0)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#848484"));
        if(TempPedidosList.get(position).getStatus() == 1)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#0C580F"));
        if(TempPedidosList.get(position).getStatus() == 2)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#025AA3"));
        if(TempPedidosList.get(position).getStatus() == 3)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#F30606"));




        /*}*/

        return convertView;
    }
}


class ViewItemPedido
{
    ImageView ivReloj;
    ImageView ivStatus;
    ImageView ivUsuario;
    ImageView ivEstrella;
    ImageView ivOrigen;
    ImageView ivDestino;
    ImageView ivDescripcion;
    ImageView ivImporte;

    TextView txtHora;
    TextView txtStatus;
    TextView txtNombre;
    TextView txtCalificacion;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcion;
    TextView txtImporte;

    TextView txtFechaHeader;
    TextView txtTotalHeader;

    LinearLayout layHeader;


}