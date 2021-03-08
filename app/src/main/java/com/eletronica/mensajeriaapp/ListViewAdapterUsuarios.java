package com.eletronica.mensajeriaapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapterUsuarios extends BaseAdapter{
    List<User> TempUsuariosList;
    Context context;
    private static final int HEADER_ITEM = 1;
    private static final int PEDIDO_ITEM = 2;
    //LayoutInflater layoutInflater;

    public interface CambiarStatus {

        public void cambiarStatus(int id, String status);
    }
    CambiarStatus listener;

    public ListViewAdapterUsuarios(List<User> listValue, Context context, CambiarStatus listen)
    {
        this.context = context;
        this.TempUsuariosList = listValue;
        this.listener = listen;

    }

    @Override
    public int getCount()
    {
        return this.TempUsuariosList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempUsuariosList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
    /*
    @Override
    public int getItemViewType(int position){
        if(this.TempUsuariosList.get(position) instanceof Pedido){
            return this.PEDIDO_ITEM;
        }else{
            return this.HEADER_ITEM;
        }
    }
    */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ViewItemUsuario viewItemP = null;

            if (convertView == null || convertView.findViewById(R.id.txtFechaHeader) == null) {
                viewItemP = new ViewItemUsuario();

                LayoutInflater layoutInflater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = layoutInflater.inflate(R.layout.listview_item_usuario, parent, false);

                viewItemP.ivNombre = (ImageView) convertView.findViewById(R.id.ivNombreUsuarioUsuarios);
                viewItemP.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatusUsuarios);
                viewItemP.ivUsuario = (ImageView) convertView.findViewById(R.id.ivUsuarioUsuarios);
                viewItemP.ivCelular = (ImageView) convertView.findViewById(R.id.ivCelularUsuarios);

                viewItemP.txtCelular = (TextView) convertView.findViewById(R.id.txtCelularUsuarios);
                viewItemP.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusUsuarios);
                viewItemP.txtNombre = (TextView) convertView.findViewById(R.id.txtNombreUsuarioUsuarios);
                viewItemP.txtUsuario = (TextView) convertView.findViewById(R.id.txtUsuarioUsuarios);

                viewItemP.btnActivar = (Button) convertView.findViewById(R.id.btnActivarUsuarios);
                viewItemP.btnInactivar = (Button) convertView.findViewById(R.id.btnInactivarUsuarios);

                viewItemP.ivUsuario.setImageResource(R.drawable.log);
                viewItemP.ivNombre.setImageResource(R.drawable.icono_usuario);
                viewItemP.ivCelular.setImageResource(R.drawable.icono_llamar);
                viewItemP.ivStatus.setImageResource(R.drawable.icono_status);

                convertView.setTag(viewItemP);
            } else {
                viewItemP = (ViewItemUsuario) convertView.getTag();
            }

            viewItemP.txtUsuario.setText(TempUsuariosList.get(position).getUsuario());
            viewItemP.txtStatus.setText(TempUsuariosList.get(position).getStatus_descripcion());
            viewItemP.txtNombre.setText(TempUsuariosList.get(position).getNombre());
            viewItemP.txtCelular.setText(TempUsuariosList.get(position).getCelular());

            viewItemP.btnActivar.setVisibility(convertView.GONE);
            viewItemP.btnInactivar.setVisibility(convertView.GONE);

            if(TempUsuariosList.get(position).getStatus().equals("A")){
                viewItemP.btnInactivar.setVisibility(convertView.VISIBLE);
            }

            if(TempUsuariosList.get(position).getStatus().equals("I")){
                viewItemP.btnActivar.setVisibility(convertView.VISIBLE);
            }

            viewItemP.btnActivar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(context);

                    builder.setMessage("Desea Activar este Usuario?");
                    builder.setTitle(TempUsuariosList.get(position).getNombre());
                    builder.setCancelable(false);

                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int id = TempUsuariosList.get(position).getId_usuario();
                            listener.cambiarStatus(id,"A");

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

            viewItemP.btnInactivar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(context);

                    builder.setMessage("Desea Inactivar este Usuario?");
                    builder.setTitle(TempUsuariosList.get(position).getNombre());
                    builder.setCancelable(false);

                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int id = TempUsuariosList.get(position).getId_usuario();
                            listener.cambiarStatus(id,"I");

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



        /*}*/

        return convertView;
    }
}


class ViewItemUsuario
{

    ImageView ivUsuario;
    ImageView ivNombre;
    ImageView ivCelular;
    ImageView ivStatus;

    TextView txtUsuario;
    TextView txtNombre;
    TextView txtCelular;
    TextView txtStatus;


    Button btnActivar;
    Button btnInactivar;


}

/*
class ViewItemHeader
{
    TextView txtFechaHeader;
    TextView txtTotalHeader;



}
*/