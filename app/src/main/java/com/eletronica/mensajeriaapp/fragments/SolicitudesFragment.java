package com.eletronica.mensajeriaapp.fragments;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.CuadroDialogoSolicitud;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.ListViewAdapterSolicitudes;
import com.eletronica.mensajeriaapp.Pedido;
import com.eletronica.mensajeriaapp.Principal;
import com.eletronica.mensajeriaapp.R;
import com.eletronica.mensajeriaapp.RecyclerViewAdapterRepartidores;
import com.eletronica.mensajeriaapp.RecyclerViewAdapterSolicitudes;
import com.eletronica.mensajeriaapp.RecyclerViewOnItemClickListener;
import com.eletronica.mensajeriaapp.SolicitudEnCurso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolicitudesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CuadroDialogoSolicitud.Actualizar{

    ProgressBar progressBar = null;

    RequestQueue rq;
    JsonRequest jrq;

    //ListView listView;
    List<Pedido> pedidosList;
    List<Pedido> pedidosListTmp;
    SwipeRefreshLayout swipeContainer;
    RecyclerView listView;
    String FinalJSonObject;
    String FinalJSonObjectEnCurso;
    String FinalJSonObjectEstatus;
    //ListViewAdapterSolicitudes adapter;
    RecyclerViewAdapterSolicitudes adapter;
    static View mView;

    Context mContext;

    FragmentManager fm;
    FragmentTransaction ft;
    CuadroDialogoSolicitud dialogoFragment;
    CuadroDialogoSolicitud tPrev;
    boolean actualizaLista = false;
    Boolean readyToUpdateList = true;

    public static final int DIALOGO_FRAGMENT = 1;
    public static final String CHANNEL_ID = "NOTIFICACION";
    public static final int NOTIFICATION_ID = 0;
    /*public SolicitudesFragment() {
        // Required empty public constructor
    }
    */

    final Handler handler = new Handler();
    final int delay = 5000; // 1000 milliseconds == 1 second

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    @Override
    public void actualizaActividad() {


       // loadSolicitudes(mView);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solicitudes, container, false);
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.mContext = getActivity().getApplicationContext();
        listView = (RecyclerView) view.findViewById(R.id.listViewSolicitudes);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);
        swipeContainer.setOnRefreshListener(this);
        this.mView = view;

        //listView.addItemDecoration(new SimpleDividerItemDecoration(this));

        //loadSolicitudes(mView);


        fm = getFragmentManager();
        ft = fm.beginTransaction();

        //listView.setClickable(true);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Pedido pedido = (Pedido) listView.getItemAtPosition(position);

                dialogoFragment = new CuadroDialogoSolicitud(mContext, fm, mView);
                Bundle b = new Bundle();
                b.putSerializable("pedido", (Serializable)pedido);

                dialogoFragment.setArguments(b);
                tPrev =  (CuadroDialogoSolicitud) fm.findFragmentByTag("dialogoSolicitud");

                if(tPrev!=null)
                    ft.remove(tPrev);

                dialogoFragment.setTargetFragment(SolicitudesFragment.this, DIALOGO_FRAGMENT);
                dialogoFragment.show(getActivity().getSupportFragmentManager(), "dialogoSolicitud");
            }
        });*/
        //cuando cargue la vista se deben de cargar todas las solicitudes y guardar los id en un arreglo y
        loadSolicitudes(mView);


        handler.postDelayed(new Runnable() {
            public void run() {
                //funcion para checar los estatus de los pedidos que ya se le pasaron
                if(readyToUpdateList)
                    verificaStatusSolicitudes(mView);

                handler.postDelayed(this, delay);
            }
        }, delay);
        verificaSolicitudEnCurso(mView);
        return view;
    }

    private void verificaStatusSolicitudes(View view) {
        //progressBar.setVisibility(View.VISIBLE);
        readyToUpdateList = false;
        GlobalVariables vg = new GlobalVariables();
        final View vista = view;

        String Ids = obtenerIdSolicitudes();

        String id_usuario = String.valueOf(vg.id_usuario);
        String url = vg.URLServicio + "obtenersolicitudesstatus.php?id_usuario="+String.valueOf(id_usuario)+"&ids="+Ids;

        try {
            StringRequest stringRequest = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObjectEstatus = response ;

                            // Calling method to parse JSON object.
                            new ParseJSonDataClassEstatus(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });


            RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(vista.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private String obtenerIdSolicitudes(){
        String ids = "";
        Pedido p;
        for (int i = 0; i < pedidosList.size(); i++) {
            int id;
            p = pedidosList.get(i);
            id = p.getId_pedido();

            ids += String.valueOf(id) + ",";
        }

        if(ids.length() > 0)
            ids = ids.substring(0, ids.length() -1);

        return ids;
    }

    private void verificaSolicitudEnCurso(View view) {
        //progressBar.setVisibility(View.VISIBLE);
        GlobalVariables vg = new GlobalVariables();
        final View vista = view;

        String id_usuario = String.valueOf(vg.id_usuario);
        String url = vg.URLServicio + "obtenersolicitudencurso.php?id_usuario="+String.valueOf(id_usuario);

        try {
            StringRequest stringRequest = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObjectEnCurso = response ;

                            // Calling method to parse JSON object.
                            new ParseJSonDataClassEnCurso(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });


            RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSolicitudes(View view) {
        readyToUpdateList = false;
        //progressBar.setVisibility(View.VISIBLE);
        GlobalVariables vg = new GlobalVariables();
        final View vista = view;

        String id_cliente = String.valueOf(vg.id_usuario);
        String url = vg.URLServicio + "obtenersolicitudes.php";

        try {
            StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClass(vista).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


            RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        loadSolicitudes(mView);
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        //List<Pedido> pedidosList;
        // Creating List of Subject class.


        public ParseJSonDataClass(View view) {
            this.view = view;
            this.context = view.getContext();
        }

        //@Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        //@Override
        protected Void doInBackground(Void... arg0) {

            try {

                // Checking whether FinalJSonObject is not equals to null.
                if (FinalJSonObject != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObject);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        Pedido pedido;

                        // Defining CustomSubjectNamesList AS Array List.
                        pedidosList = new ArrayList<Pedido>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            pedido = new Pedido();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.

                            pedido.setId_pedido(Integer.parseInt(jsonObject.getString("id_pedido")));
                            pedido.setFecha(jsonObject.getString("fecha"));
                            pedido.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            pedido.setNombre(jsonObject.getString("nombre"));
                            pedido.setOrigen(jsonObject.getString("origen"));
                            pedido.setDestino(jsonObject.getString("destino"));
                            pedido.setDescripcion_origen(jsonObject.getString("descripcion_origen"));
                            pedido.setStatus(Integer.parseInt(jsonObject.getString("status")));
                            pedido.setStatus_descripcion(jsonObject.getString("descripcion_status"));
                            pedido.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                            pedido.setImporte(Double.parseDouble(jsonObject.getString("importe")));

                            pedido.setOrigen_latitud(Double.parseDouble(jsonObject.getString("origen_latitud")));
                            pedido.setOrigen_longitud(Double.parseDouble(jsonObject.getString("origen_longitud")));
                            pedido.setDestino_latitud(Double.parseDouble(jsonObject.getString("destino_latitud")));
                            pedido.setDestino_longitud(Double.parseDouble(jsonObject.getString("destino_longitud")));
                            pedido.setCelular(jsonObject.getString("celular"));

                            //pedido.setFoto(Base64.decode(jsonObject.getString("foto"), Base64.DEFAULT));

                            pedido.setParada1(jsonObject.getString("parada1"));
                            pedido.setParada_latitud_1(Double.parseDouble(jsonObject.getString("parada_latitud_1")));
                            pedido.setParada_longitud_1(Double.parseDouble(jsonObject.getString("parada_longitud_1")));

                            pedido.setParada2(jsonObject.getString("parada2"));
                            pedido.setParada_latitud_2(Double.parseDouble(jsonObject.getString("parada_latitud_2")));
                            pedido.setParada_longitud_2(Double.parseDouble(jsonObject.getString("parada_longitud_2")));

                            pedido.setParada3(jsonObject.getString("parada3"));
                            pedido.setParada_latitud_3(Double.parseDouble(jsonObject.getString("parada_latitud_3")));
                            pedido.setParada_longitud_3(Double.parseDouble(jsonObject.getString("parada_longitud_3")));
                            //pedido.setFoto_mensajero(Base64.decode(jsonObject.getString("foto_mensajero"), Base64.DEFAULT));

                            //origen_latitud,origen_longitud,destino_latitud,destino_longitud
                            // Adding subject list object into CustomSubjectNamesList.
                            pedidosList.add(pedido);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result)
        {
            try{
                //int firstPosition = listView.getFirstVisiblePosition();
                // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.
                //adapter = new RecyclerViewAdapterSolicitudes(pedidosList, context);

                // Setting up all data into ListView.
                ///listView.setAdapter(adapter);
                //listView.setSelection(firstPosition);

                adapter = new RecyclerViewAdapterSolicitudes(pedidosList, new RecyclerViewOnItemClickListener() {

                    @Override
                    public void onClick(View v, int position) {
                        //Toast.makeText(OrdenesServicios.this, tecnicosList.get(position).getNombre_tecnico(), Toast.LENGTH_SHORT).show();
                        //filtro_repartidor = repartidoresList.get(position).getId_usuario();

                        //ImageView ivImagentecnico = (ImageView)v.findViewById(R.id.ivImagenTecnico);
                        //ivImagentecnico.setImageResource(R.drawable.filtro_tecnico_selected);
                        //loadSolicitudes(mView);


                        /*
                        //se implemento en el RecyclerView

                        Pedido pedido = (Pedido) pedidosList.get(position);

                        dialogoFragment = new CuadroDialogoSolicitud(mContext, fm, mView);
                        Bundle b = new Bundle();
                        b.putSerializable("pedido", (Serializable)pedido);

                        dialogoFragment.setArguments(b);
                        tPrev =  (CuadroDialogoSolicitud) fm.findFragmentByTag("dialogoSolicitud");

                        if(tPrev!=null)
                            ft.remove(tPrev);

                        dialogoFragment.setTargetFragment(SolicitudesFragment.this, DIALOGO_FRAGMENT);
                        dialogoFragment.show(getActivity().getSupportFragmentManager(), "dialogoSolicitud");
                        */


                    }
                }, context, fm, mView, SolicitudesFragment.this);

                listView.setAdapter(adapter);
                //Horizontal orientation.
                listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
                listView.setItemAnimator(new DefaultItemAnimator());



                swipeContainer.setRefreshing(false);


                readyToUpdateList = true;









                //adapter.notifyDataSetChanged();
                //progressBar.setVisibility(View.GONE);

                //verificaSolicitudEnCurso();


            }catch (Exception e){

            }
        }
    }

    private class ParseJSonDataClassEnCurso extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;

        // Creating List of Subject class.


        public ParseJSonDataClassEnCurso(View view) {
            this.view = view;
            this.context = view.getContext();
        }

        //@Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        //@Override
        protected Void doInBackground(Void... arg0) {

            try {

                // Checking whether FinalJSonObject is not equals to null.
                if (FinalJSonObjectEnCurso != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectEnCurso);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        Pedido pedidoEnCurso;

                        // Defining CustomSubjectNamesList AS Array List.


                        pedidoEnCurso = new Pedido();

                        jsonObject = jsonArray.getJSONObject(0);

                        //Storing ID into subject list.

                        pedidoEnCurso.setId_pedido(Integer.parseInt(jsonObject.getString("id_pedido")));
                        pedidoEnCurso.setFecha(jsonObject.getString("fecha"));
                        pedidoEnCurso.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                        pedidoEnCurso.setNombre(jsonObject.getString("nombre"));
                        pedidoEnCurso.setOrigen(jsonObject.getString("origen"));
                        pedidoEnCurso.setDestino(jsonObject.getString("destino"));
                        pedidoEnCurso.setDescripcion_origen(jsonObject.getString("descripcion_origen"));
                        pedidoEnCurso.setStatus(Integer.parseInt(jsonObject.getString("status")));
                        pedidoEnCurso.setStatus_descripcion(jsonObject.getString("descripcion_status"));
                        pedidoEnCurso.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                        pedidoEnCurso.setImporte(Double.parseDouble(jsonObject.getString("importe")));

                        pedidoEnCurso.setOrigen_latitud(Double.parseDouble(jsonObject.getString("origen_latitud")));
                        pedidoEnCurso.setOrigen_longitud(Double.parseDouble(jsonObject.getString("origen_longitud")));
                        pedidoEnCurso.setDestino_latitud(Double.parseDouble(jsonObject.getString("destino_latitud")));
                        pedidoEnCurso.setDestino_longitud(Double.parseDouble(jsonObject.getString("destino_longitud")));
                        pedidoEnCurso.setCelular(jsonObject.getString("celular"));

                        pedidoEnCurso.setFoto(Base64.decode(jsonObject.getString("foto"), Base64.DEFAULT));

                        pedidoEnCurso.setParada1(jsonObject.getString("parada1"));
                        pedidoEnCurso.setParada_latitud_1(Double.parseDouble(jsonObject.getString("parada_latitud_1")));
                        pedidoEnCurso.setParada_longitud_1(Double.parseDouble(jsonObject.getString("parada_longitud_1")));

                        pedidoEnCurso.setParada2(jsonObject.getString("parada2"));
                        pedidoEnCurso.setParada_latitud_2(Double.parseDouble(jsonObject.getString("parada_latitud_2")));
                        pedidoEnCurso.setParada_longitud_2(Double.parseDouble(jsonObject.getString("parada_longitud_2")));

                        pedidoEnCurso.setParada3(jsonObject.getString("parada3"));
                        pedidoEnCurso.setParada_latitud_3(Double.parseDouble(jsonObject.getString("parada_latitud_3")));
                        pedidoEnCurso.setParada_longitud_3(Double.parseDouble(jsonObject.getString("parada_longitud_3")));


                        Intent intencion = new Intent(getActivity(), SolicitudEnCurso.class);
                        intencion.putExtra("pedido", (Serializable)pedidoEnCurso);
                        intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intencion);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result)

        {

        }
    }

    private class ParseJSonDataClassEstatus extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        //List<Pedido> pedidosList;
        // Creating List of Subject class.


        public ParseJSonDataClassEstatus(View view) {
            this.view = view;
            this.context = view.getContext();
        }

        //@Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        //@Override
        protected Void doInBackground(Void... arg0) {

            try {

                // Checking whether FinalJSonObject is not equals to null.
                if (FinalJSonObjectEstatus != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    JSONArray jsonArrayNuevas = null;
                    try {
                        Pedido pedido;

                         JSONObject jsonObject;

                         JSONObject jsonObject1 = new JSONObject(FinalJSonObjectEstatus);

                         JSONObject jsonObjectDatos =  jsonObject1.optJSONObject("datos");
                         //if(jsonObjectDatos.getJSONArray("status") != null) {

                         jsonArray = jsonObjectDatos.getJSONArray("status");
                         jsonArrayNuevas = jsonObjectDatos.getJSONArray("nuevas");

                         for (int i = 0; i < jsonArray.length(); i++) {
                             jsonObject = jsonArray.getJSONObject(i);
                             int id = Integer.parseInt(jsonObject.getString("id_pedido"));
                             int status = Integer.parseInt(jsonObject.getString("status"));
                             //Storing ID into subject list.
                             for (int x = 0; x < pedidosList.size(); x++) {
                                 Pedido p = pedidosList.get(x);
                                 if (p.getId_pedido() == id) {

                                     //actualizaLista = true;

                                     final int finalX = x;
                                     getActivity().runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             adapter.removeItem(finalX);
                                             //pedidosList.remove(finalX);
                                         }
                                     });


                                     break;
                                 }
                             }
                         }
                        // }

                        for (int i = 0; i < jsonArrayNuevas.length(); i++) {
                            pedido = new Pedido();


                            jsonObject = jsonArrayNuevas.getJSONObject(i);

                            //Storing ID into subject list.

                            pedido.setId_pedido(Integer.parseInt(jsonObject.getString("id_pedido")));
                            pedido.setFecha(jsonObject.getString("fecha"));
                            pedido.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            pedido.setNombre(jsonObject.getString("nombre"));
                            pedido.setOrigen(jsonObject.getString("origen"));
                            pedido.setDestino(jsonObject.getString("destino"));
                            pedido.setDescripcion_origen(jsonObject.getString("descripcion_origen"));
                            pedido.setStatus(Integer.parseInt(jsonObject.getString("status")));
                            pedido.setStatus_descripcion(jsonObject.getString("descripcion_status"));
                            pedido.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                            pedido.setImporte(Double.parseDouble(jsonObject.getString("importe")));

                            pedido.setOrigen_latitud(Double.parseDouble(jsonObject.getString("origen_latitud")));
                            pedido.setOrigen_longitud(Double.parseDouble(jsonObject.getString("origen_longitud")));
                            pedido.setDestino_latitud(Double.parseDouble(jsonObject.getString("destino_latitud")));
                            pedido.setDestino_longitud(Double.parseDouble(jsonObject.getString("destino_longitud")));
                            pedido.setCelular(jsonObject.getString("celular"));

                            //pedido.setFoto(Base64.decode(jsonObject.getString("foto"), Base64.DEFAULT));

                            pedido.setParada1(jsonObject.getString("parada1"));
                            pedido.setParada_latitud_1(Double.parseDouble(jsonObject.getString("parada_latitud_1")));
                            pedido.setParada_longitud_1(Double.parseDouble(jsonObject.getString("parada_longitud_1")));

                            pedido.setParada2(jsonObject.getString("parada2"));
                            pedido.setParada_latitud_2(Double.parseDouble(jsonObject.getString("parada_latitud_2")));
                            pedido.setParada_longitud_2(Double.parseDouble(jsonObject.getString("parada_longitud_2")));

                            pedido.setParada3(jsonObject.getString("parada3"));
                            pedido.setParada_latitud_3(Double.parseDouble(jsonObject.getString("parada_latitud_3")));
                            pedido.setParada_longitud_3(Double.parseDouble(jsonObject.getString("parada_longitud_3")));

                            //pedidosList.add(pedido);
                            //actualizaLista = true;

                            final Pedido finalp = pedido;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addItem(finalp, 0);
                                }
                            });


                        }
                        if(jsonArrayNuevas.length() > 0){
                            notificarNuevasSolicitudes();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result)
        {
           // try{
                //int firstPosition = listView.getFirstVisiblePosition();
                //final ListViewAdapterSolicitudes adapter = new ListViewAdapterSolicitudes(pedidosList, context);


                //listView.setAdapter(adapter);
                //listView.setSelection(firstPosition);
            swipeContainer.setRefreshing(false);

            //}catch (Exception e){

            //}
            //if(actualizaLista) {
            //    adapter.notifyDataSetChanged();
            //    //adapter.updateReceiptsList(pedidosList);
            //    //adapter.notifyDataSetChanged();
            //    actualizaLista = false;
            //}

            readyToUpdateList = true;
        }
    }

    public void notificarNuevasSolicitudes() {

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getActivity().getApplicationContext().getPackageName() + "/" + Notification.DEFAULT_SOUND);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence name = "Notificación";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            /*AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(soundUri, audioAttributes);*/
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Intent intent = new Intent(getActivity(), Principal.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.icono_mensajeria2);
        builder.setContentTitle("Mensajeria App");
        builder.setContentText("Nuevas solicitudes");
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.setLights(Color.MAGENTA, 1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //builder.setSound(soundUri);
        }


        NotificationManagerCompat notificationManagerCompat  = NotificationManagerCompat.from(getActivity().getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }



    @Override
    public void onResume() {
        super.onResume();
        loadSolicitudes(mView);
    }


}
