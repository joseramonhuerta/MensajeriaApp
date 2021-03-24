package com.eletronica.mensajeriaapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterChoferes extends BaseAdapter {

    Context context;
    List<User> TempUserList;
    List<User> TempUserListFilter;

    public ListViewAdapterChoferes(List<User> listValue, Context context) {
        this.TempUserList = listValue;
        this.context = context;

        this.TempUserListFilter = new ArrayList<User>();
        this.TempUserListFilter.addAll(TempUserList);
    }

    @Override
    public int getCount() {
        return this.TempUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.TempUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewItemUser viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemUser();

            //LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //convertView = layoutInfiater.inflate(R.layout.listview_items_lavadoras, parent, false);

            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_item_choferes, null);

            viewItem.ivNombreUsuario = (ImageView) convertView.findViewById(R.id.ivNombreChoferesBusqueda);
            viewItem.ivNombreUsuario.setImageResource(R.drawable.icono_usuario);



            viewItem.txtNombreUsuario = (TextView)convertView.findViewById(R.id.txtNombreChoferesBusqueda);



            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemUser) convertView.getTag();
        }

        viewItem.txtNombreUsuario.setText(TempUserList.get(position).getNombre());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempUserList.clear();
        if (charText.length() == 0) {
            TempUserList.addAll(TempUserListFilter);
        }
        else
        {
            for (User wp : TempUserListFilter) {
                if (wp.getNombre().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempUserList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

class ViewItemUser
{
    TextView txtNombreUsuario;
    ImageView ivNombreUsuario;


}
