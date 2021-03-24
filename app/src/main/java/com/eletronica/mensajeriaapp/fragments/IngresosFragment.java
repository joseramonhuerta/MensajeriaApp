package com.eletronica.mensajeriaapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.CuadroDialogoSolicitud;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.ListViewAdapterResumen;
import com.eletronica.mensajeriaapp.Pedido;
import com.eletronica.mensajeriaapp.Header;
import com.eletronica.mensajeriaapp.R;
import com.eletronica.mensajeriaapp.SolicitudEnCurso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.eletronica.mensajeriaapp.fragments.SolicitudesFragment.DIALOGO_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class IngresosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ProgressBar progressBar = null;

    RequestQueue rq;
    JsonRequest jrq;

    ListView listView;

    SwipeRefreshLayout swipeContainer;

    String FinalJSonObject;

    static View mView;

    Context mContext;

    FragmentManager fm;
    FragmentTransaction ft;

    int limite = 0;
    int incremento = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingresos, container, false);
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        this.mContext = getActivity().getApplicationContext();
        listView = (ListView) view.findViewById(R.id.listViewResumen);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainerResumen);
        swipeContainer.setOnRefreshListener(this);
        this.mView = view;

        loadSolicitudes(mView);

        fm = getFragmentManager();
        ft = fm.beginTransaction();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Pedido pedido = (Pedido) listView.getItemAtPosition(position);
                if(pedido.getStatus() == 1) {
                    Intent intencion = new Intent(getActivity(), SolicitudEnCurso.class);
                    intencion.putExtra("pedido", (Serializable) pedido);
                    intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intencion);
                }
            }
        });



        return view;
    }

    private void loadSolicitudes(View view) {
        //progressBar.setVisibility(View.VISIBLE);
        GlobalVariables vg = new GlobalVariables();
        final View vista = view;
        limite+=incremento;
        String id_usuario= String.valueOf(vg.id_usuario);
        String url = vg.URLServicio + "obtenersolicitudesresumen.php?id_usuario="+id_usuario+"&limite="+String.valueOf(limite);

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
                        Header header;

                        // Defining CustomSubjectNamesList AS Array List.
                        pedidosList = new ArrayList<Pedido>();

                        for (int i = 0; i < jsonArray.length(); i++) {


                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.

                            //if(Integer.parseInt(jsonObject.getString("template")) == 2) {
                                pedido = new Pedido();

                                pedido.setId_pedido(Integer.parseInt(jsonObject.getString("id_pedido")));
                                pedido.setFecha(jsonObject.getString("fecha"));
                                pedido.setHora(jsonObject.getString("hora"));
                                pedido.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                                pedido.setNombre(jsonObject.getString("nombre"));
                                pedido.setOrigen(jsonObject.getString("origen"));
                                pedido.setDestino(jsonObject.getString("destino"));
                                pedido.setDescripcion_origen(jsonObject.getString("descripcion_origen"));
                                pedido.setStatus(Integer.parseInt(jsonObject.getString("status_pedido")));
                                pedido.setStatus_descripcion(jsonObject.getString("descripcion_status"));
                                pedido.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                                pedido.setImporte(Double.parseDouble(jsonObject.getString("importe")));

                                pedido.setOrigen_latitud(Double.parseDouble(jsonObject.getString("origen_latitud")));
                                pedido.setOrigen_longitud(Double.parseDouble(jsonObject.getString("origen_longitud")));
                                pedido.setDestino_latitud(Double.parseDouble(jsonObject.getString("destino_latitud")));
                                pedido.setDestino_longitud(Double.parseDouble(jsonObject.getString("destino_longitud")));

                                pedido.setTemplate(Integer.parseInt(jsonObject.getString("formato")));
                                pedido.setFecha_header(jsonObject.getString("fecha_header"));
                                pedido.setTotal_header(Double.parseDouble(jsonObject.getString("total_header")));

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

                                //origen_latitud,origen_longitud,destino_latitud,destino_longitud
                                // Adding subject list object into CustomSubjectNamesList.
                                pedidosList.add(pedido);
                            /*}else{
                                header = new Header();
                                header.setFecha(jsonObject.getString("fecha_header"));
                                header.setTotal(Double.parseDouble(jsonObject.getString("total_header")));
                                pedidosList.add(header);
                            }*/
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
            try {
                // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.
                final ListViewAdapterResumen adapter = new ListViewAdapterResumen(pedidosList, context);

                // Setting up all data into ListView.
                listView.setAdapter(adapter);
                swipeContainer.setRefreshing(false);

                //progressBar.setVisibility(View.GONE);
            }catch (Exception e){

            }
        }
    }
}
