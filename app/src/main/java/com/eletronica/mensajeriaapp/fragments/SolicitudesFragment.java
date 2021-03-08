package com.eletronica.mensajeriaapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
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
import com.eletronica.mensajeriaapp.R;
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

    ListView listView;

    SwipeRefreshLayout swipeContainer;

    String FinalJSonObject;
    String FinalJSonObjectEnCurso;

    static View mView;

    Context mContext;

    FragmentManager fm;
    FragmentTransaction ft;
    CuadroDialogoSolicitud dialogoFragment;
    CuadroDialogoSolicitud tPrev;

    public static final int DIALOGO_FRAGMENT = 1;
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

        //progressBar.setVisibility(mView.VISIBLE);
        loadSolicitudes(mView);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solicitudes, container, false);
        this.mContext = getActivity().getApplicationContext();
        listView = (ListView) view.findViewById(R.id.listViewSolicitudes);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);
        swipeContainer.setOnRefreshListener(this);
        this.mView = view;

        loadSolicitudes(mView);


        fm = getFragmentManager();
        ft = fm.beginTransaction();

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        });

        handler.postDelayed(new Runnable() {
            public void run() {
                loadSolicitudes(mView);
                handler.postDelayed(this, delay);
            }
        }, delay);


        verificaSolicitudEnCurso(mView);
        return view;
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
        List<Pedido> pedidosList;
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
                int firstPosition = listView.getFirstVisiblePosition();
                // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.
                final ListViewAdapterSolicitudes adapter = new ListViewAdapterSolicitudes(pedidosList, context);

                // Setting up all data into ListView.
                listView.setAdapter(adapter);
                listView.setSelection(firstPosition);
                swipeContainer.setRefreshing(false);
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


    @Override
    public void onResume() {
        super.onResume();
        loadSolicitudes(mView);
    }


}
