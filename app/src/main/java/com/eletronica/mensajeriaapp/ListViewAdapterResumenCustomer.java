package com.eletronica.mensajeriaapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eletronica.mensajeriaapp.fragments.SolicitudesFragment;

import java.io.Serializable;
import java.util.List;

public class ListViewAdapterResumenCustomer extends BaseAdapter{
    List<Pedido> TempPedidosList;
    Context context;
    private static final int HEADER_ITEM = 1;
    private static final int PEDIDO_ITEM = 2;
    //LayoutInflater layoutInflater;

    public interface CancelarSolicitud {

        public void cancelarSolicitud(int id);
    }
    CancelarSolicitud listener;

    public ListViewAdapterResumenCustomer(List<Pedido> listValue, Context context, CancelarSolicitud listen)
    {
        this.context = context;
        this.TempPedidosList = listValue;
        //layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listen;
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

        ViewItemPedidoCustomer viewItemP = null;

        if (convertView == null) {
            viewItemP = new ViewItemPedidoCustomer();

            LayoutInflater layoutInflater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.listview_item_resumen, parent, false);

            viewItemP.layMensajero = (LinearLayout) convertView.findViewById(R.id.layMensajero);
            viewItemP.layBotones = (LinearLayout) convertView.findViewById(R.id.layBotonCancelarResumenCustomer);

            //viewItemP.layMensajero.setVisibility(View.GONE);
            //viewItemP.layBotones.setVisibility(View.GONE);

            viewItemP.ivReloj = (ImageView) convertView.findViewById(R.id.ivRelojResumenCustomer);
            viewItemP.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatusResumenCustomer);

            viewItemP.ivOrigen = (ImageView) convertView.findViewById(R.id.ivNombreResumenCustomer);
            viewItemP.ivDestino = (ImageView) convertView.findViewById(R.id.ivDestinoResumenCustomer);
            viewItemP.ivDescripcion = (ImageView) convertView.findViewById(R.id.ivDescripcionOrigenResumenCustomer);
            viewItemP.ivImporte = (ImageView) convertView.findViewById(R.id.ivImporteResumenCustomer);


            viewItemP.txtHora = (TextView) convertView.findViewById(R.id.txtHoraResumenCustomer);
            viewItemP.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusResumenCustomer);

            viewItemP.txtOrigen = (TextView) convertView.findViewById(R.id.txtOrigenResumenCustomer);
            viewItemP.txtDestino = (TextView) convertView.findViewById(R.id.txtDestinoResumenCustomer);
            viewItemP.txtDescripcion = (TextView) convertView.findViewById(R.id.txtDescripcionOrigenResumenCustomer);
            viewItemP.txtImporte = (TextView) convertView.findViewById(R.id.txtImporteResumenCustomer);


            viewItemP.txtUsuarioMensajero = (TextView) convertView.findViewById(R.id.txtUsuarioMensajero);

            viewItemP.ivMensajero = (ImageView) convertView.findViewById(R.id.ivMensajero);
            viewItemP.ivMensajero.setImageResource(R.drawable.icono_repartidor);

            viewItemP.btnLlamarMensajero = (ImageButton) convertView.findViewById(R.id.btnLlamarMensajero);
            viewItemP.btnLlamarMensajero.setImageResource(R.drawable.icono_llamar);



            viewItemP.btnCancelar = (Button) convertView.findViewById(R.id.btnCancelarResumenCustomer);
            viewItemP.btnDetalles = (Button) convertView.findViewById(R.id.btnDetallesResumenCustomer);

            viewItemP.txtParada1 = (TextView)convertView.findViewById(R.id.txtParada1ResumenCustomer);
            viewItemP.txtParada2 = (TextView)convertView.findViewById(R.id.txtParada2ResumenCustomer);
            viewItemP.txtParada3 = (TextView)convertView.findViewById(R.id.txtParada3ResumenCustomer);

            viewItemP.layParada1 = (LinearLayout) convertView.findViewById(R.id.layParada1ResumenCustomer);
            viewItemP.layParada2 = (LinearLayout) convertView.findViewById(R.id.layParada2ResumenCustomer);
            viewItemP.layParada3 = (LinearLayout) convertView.findViewById(R.id.layParada3ResumenCustomer);

            viewItemP.ivParada1 = (ImageView) convertView.findViewById(R.id.ivParada1ResumenCustomer);
            viewItemP.ivParada2 = (ImageView) convertView.findViewById(R.id.ivParada2ResumenCustomer);
            viewItemP.ivParada3 = (ImageView) convertView.findViewById(R.id.ivParada3ResumenCustomer);


            viewItemP.ivReloj.setImageResource(R.drawable.calendario2);
            viewItemP.ivOrigen.setImageResource(R.drawable.icono_origen);
            viewItemP.ivDestino.setImageResource(R.drawable.icono_destino);
            viewItemP.ivDescripcion.setImageResource(R.drawable.globo);
            viewItemP.ivImporte.setImageResource(R.drawable.precio);

            convertView.setTag(viewItemP);
        } else {
            viewItemP = (ViewItemPedidoCustomer) convertView.getTag();


        }

        viewItemP.layMensajero.setVisibility(View.GONE);
        viewItemP.layBotones.setVisibility(View.GONE);
        viewItemP.btnDetalles.setVisibility(View.GONE);

        if(TempPedidosList.get(position).getId_usuario_mensajero() > 0)
            viewItemP.layMensajero.setVisibility(View.VISIBLE);

        if(TempPedidosList.get(position).getStatus() == 0 || TempPedidosList.get(position).getStatus() == 1)
            viewItemP.layBotones.setVisibility(View.VISIBLE);

        if(TempPedidosList.get(position).getStatus() > 0 )
            viewItemP.btnDetalles.setVisibility(View.VISIBLE);

        if(TempPedidosList.get(position).getStatus() == 0)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_0);
        if(TempPedidosList.get(position).getStatus() == 1)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_1);
        if(TempPedidosList.get(position).getStatus() == 2)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_2);
        if(((Pedido)TempPedidosList.get(position)).getStatus() == 3)
            viewItemP.ivStatus.setImageResource(R.drawable.icono_status_3);


        if(TempPedidosList.get(position).getStatus() == 0)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#848484"));
        if(TempPedidosList.get(position).getStatus() == 1)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#0C580F"));
        if(TempPedidosList.get(position).getStatus() == 2)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#025AA3"));
        if(((Pedido)TempPedidosList.get(position)).getStatus() == 3)
            viewItemP.txtStatus.setTextColor(Color.parseColor("#F30606"));

        viewItemP.txtHora.setText(TempPedidosList.get(position).getHora());
        viewItemP.txtStatus.setText(TempPedidosList.get(position).getStatus_descripcion());
        viewItemP.txtOrigen.setText(TempPedidosList.get(position).getOrigen());
        viewItemP.txtDestino.setText(TempPedidosList.get(position).getDestino());
        viewItemP.txtDescripcion.setText(TempPedidosList.get(position).getDescripcion_origen());
        viewItemP.txtImporte.setText(String.valueOf(TempPedidosList.get(position).getImporte()));

        viewItemP.txtUsuarioMensajero.setText(TempPedidosList.get(position).getNombre_mensajero());

        viewItemP.btnLlamarMensajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int id = ((ImageButton)view).getId();
                // Toast.makeText( context,"Click btnLLamar " +  String.valueOf(id)  ,Toast.LENGTH_LONG).show();

                String phoneNo = TempPedidosList.get(position).getCelular_mensajero();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(context, "No existe un numero de celular", Toast.LENGTH_SHORT).show();
                }


            }
        });


        viewItemP.btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(context);

                builder.setMessage("Desea CANCELAR esta solicitud?");
                builder.setTitle(TempPedidosList.get(position).getOrigen().toString()+" - "+TempPedidosList.get(position).getDestino().toString());
                builder.setCancelable(false);

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = TempPedidosList.get(position).getId_pedido();
                        listener.cancelarSolicitud(id);

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        viewItemP.btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pedido pedido = (Pedido) TempPedidosList.get(position);
                Intent intencion = new Intent(context, SolicitudAceptada.class);
                intencion.putExtra("pedido", (Serializable)pedido);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intencion);


            }
        });

        if(TempPedidosList.get(position).getParada1() != "null")
        {
            viewItemP.layParada1.setVisibility(View.VISIBLE);
            viewItemP.ivParada1.setImageResource(R.drawable.icono_stop);
            viewItemP.txtParada1.setText(TempPedidosList.get(position).getParada1());

        }


        if(TempPedidosList.get(position).getParada2() != "null")
        {
            viewItemP.layParada2.setVisibility(View.VISIBLE);
            viewItemP.ivParada2.setImageResource(R.drawable.icono_stop);
            viewItemP.txtParada2.setText(TempPedidosList.get(position).getParada2());

        }


        if(TempPedidosList.get(position).getParada3() != "null")
        {
            viewItemP.layParada3.setVisibility(View.VISIBLE);
            viewItemP.ivParada3.setImageResource(R.drawable.icono_stop);
            viewItemP.txtParada3.setText(TempPedidosList.get(position).getParada3());

        }

        return convertView;
    }
}


class ViewItemPedidoCustomer
{
    ImageView ivReloj;
    ImageView ivStatus;
    ImageView ivOrigen;
    ImageView ivDestino;
    ImageView ivDescripcion;
    ImageView ivImporte;
    ImageView ivMensajero;

    TextView txtHora;
    TextView txtStatus;
    TextView txtOrigen;
    TextView txtDestino;
    TextView txtDescripcion;
    TextView txtImporte;

    TextView txtUsuarioMensajero;

    ImageButton btnLlamarMensajero;
    Button btnCancelar;
    Button btnDetalles;

    LinearLayout layMensajero;
    LinearLayout layBotones;

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

/*
class ViewItemHeader
{
    TextView txtFechaHeader;
    TextView txtTotalHeader;



}
*/