package com.eletronica.mensajeriaapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListViewAdapterResumenAdmin extends BaseAdapter{
    List<Object> TempPedidosList;
    Context context;
    private static final int HEADER_ITEM = 1;
    private static final int PEDIDO_ITEM = 2;
    //LayoutInflater layoutInflater;


    public ListViewAdapterResumenAdmin(List<Object> listValue, Context context)
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

        final ViewGroup main = parent;
        ViewItemPedidoAdmin viewItemP = null;

        if (convertView == null) {
            viewItemP = new ViewItemPedidoAdmin();

            LayoutInflater layoutInflater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.listview_item_resumen_admin, parent, false);

            viewItemP.ivReloj = (ImageView) convertView.findViewById(R.id.ivRelojResumenAdmin);
            viewItemP.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatusResumenAdmin);
            viewItemP.ivUsuario = (ImageView) convertView.findViewById(R.id.ivNombreUsuarioResumenAdmin);
            viewItemP.ivOrigen = (ImageView) convertView.findViewById(R.id.ivOrigenResumenAdmin);
            viewItemP.ivDestino = (ImageView) convertView.findViewById(R.id.ivDestinoResumenAdmin);
            viewItemP.ivDescripcion = (ImageView) convertView.findViewById(R.id.ivDescripcionOrigenResumenAdmin);
            viewItemP.ivImporte = (ImageView) convertView.findViewById(R.id.ivImporteResumenAdmin);


            viewItemP.txtHora = (TextView) convertView.findViewById(R.id.txtHoraResumenAdmin);
            viewItemP.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusResumenAdmin);
            viewItemP.txtUsuario = (TextView) convertView.findViewById(R.id.txtNombreUsuarioResumenAdmin);
            viewItemP.txtOrigen = (TextView) convertView.findViewById(R.id.txtOrigenResumenAdmin);
            viewItemP.txtDestino = (TextView) convertView.findViewById(R.id.txtDestinoResumenAdmin);
            viewItemP.txtDescripcion = (TextView) convertView.findViewById(R.id.txtDescripcionOrigenResumenAdmin);
            viewItemP.txtImporte = (TextView) convertView.findViewById(R.id.txtImporteResumenAdmin);

            viewItemP.ivUsuario.setImageResource(R.drawable.user_1);
            viewItemP.ivReloj.setImageResource(R.drawable.calendario2);
            viewItemP.ivOrigen.setImageResource(R.drawable.icono_origen);
            viewItemP.ivDestino.setImageResource(R.drawable.icono_destino);
            viewItemP.ivDescripcion.setImageResource(R.drawable.globo);
            viewItemP.ivImporte.setImageResource(R.drawable.precio);

            convertView.setTag(viewItemP);
        } else {
            viewItemP = (ViewItemPedidoAdmin) convertView.getTag();
        }

        viewItemP.txtHora.setText(((Pedido) TempPedidosList.get(position)).getHora());
        viewItemP.txtStatus.setText(((Pedido) TempPedidosList.get(position)).getStatus_descripcion());
        viewItemP.txtUsuario.setText(((Pedido) TempPedidosList.get(position)).getNombre());
        viewItemP.txtOrigen.setText(((Pedido) TempPedidosList.get(position)).getOrigen());
        viewItemP.txtDestino.setText(((Pedido) TempPedidosList.get(position)).getDestino());
        viewItemP.txtDescripcion.setText(((Pedido) TempPedidosList.get(position)).getDescripcion_origen());
        viewItemP.txtImporte.setText(String.valueOf(((Pedido) TempPedidosList.get(position)).getImporte()));


        if(((Pedido) TempPedidosList.get(position)).getStatus() == 0)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_0);
        if(((Pedido) TempPedidosList.get(position)).getStatus() == 1)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_1);
        if(((Pedido) TempPedidosList.get(position)).getStatus() == 2)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_2);
        if(((Pedido)TempPedidosList.get(position)).getStatus() == 3)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_3);


        if(((Pedido) TempPedidosList.get(position)).getStatus() == 0)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#848484"));
        if(((Pedido) TempPedidosList.get(position)).getStatus() == 1)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#0C580F"));
        if(((Pedido) TempPedidosList.get(position)).getStatus() == 2)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#025AA3"));
        if(((Pedido)TempPedidosList.get(position)).getStatus() == 3)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#F30606"));


        return convertView;
    }
}


class ViewItemPedidoAdmin
{
    ImageView ivReloj;
    ImageView ivStatus;
    ImageView ivUsuario;    
    ImageView ivOrigen;
    ImageView ivDestino;
    ImageView ivDescripcion;
    ImageView ivImporte;
    ImageView ivMensajero;

    TextView txtHora;
    TextView txtStatus;
    TextView txtUsuario;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcion;
    TextView txtImporte;


}

/*
class ViewItemHeader
{
    TextView txtFechaHeader;
    TextView txtTotalHeader;



}
*/