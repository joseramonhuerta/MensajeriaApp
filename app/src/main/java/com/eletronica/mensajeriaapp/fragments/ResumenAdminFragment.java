package com.eletronica.mensajeriaapp.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.Header;
import com.eletronica.mensajeriaapp.ListViewAdapterResumenAdmin;
import com.eletronica.mensajeriaapp.ListViewAdapterResumenCustomer;
import com.eletronica.mensajeriaapp.Pedido;
import com.eletronica.mensajeriaapp.R;
import com.eletronica.mensajeriaapp.RecyclerViewAdapterRepartidores;
import com.eletronica.mensajeriaapp.RecyclerViewOnItemClickListener;
import com.eletronica.mensajeriaapp.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumenAdminFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    static View mView;

    ListView listView;
    RecyclerView listViewRepartidores;
    ImageView btnClear;
    ImageView btnBusqueda;
    ImageView btnFiltro;
    EditText txtSearch;
    TextView txtFiltros;
    SwipeRefreshLayout swipeContainer;

    int limite = 5;
    int filtro = 0;
    int filtro_repartidor = 0;

    // Server Http URL
    String HTTP_URL;
    String FinalJSonObject ;
    String FinalJSonObjectTecnicos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resumen_admin, container, false);
        this.mView = view;
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);
        txtSearch = (EditText) view.findViewById(R.id.txtSearch);

        //txtSearch.addTextChangedListener(searchTextWatcher);



        listView = (ListView) view.findViewById(R.id.listViewSolicitudesAdmin);
        listViewRepartidores = (RecyclerView) view.findViewById(R.id.listViewRepartidores);

        // btnActualizarEntregar = (Button) view.findViewById(R.id.btnActualizarEntregar);

        //mView = (View) findViewById(R.id.viewOrdenServicio);

        btnClear = (ImageView) view.findViewById(R.id.btnClearSearch);
        btnFiltro = (ImageView) view.findViewById(R.id.btnFiltroSearch);
        btnBusqueda = (ImageView) view.findViewById(R.id.btnBusqueda);
        txtFiltros = (TextView) view.findViewById(R.id.txtFiltros);

        loadRepartidores(mView);

        btnBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSolicitudes(mView);
            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
                loadSolicitudes(mView);
            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFiltro(v);

            }
        });
        swipeContainer.setOnRefreshListener(this);


        loadSolicitudes(mView);
        return view;
    }

    @Override
    public void onRefresh() {
        this.limite += 5;
        loadSolicitudes(mView);
    }

    public void loadRepartidores(View view){
        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();


        HTTP_URL = variablesGlobales.URLServicio + "obtenerusuariosrepartidores.php";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObjectTecnicos = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClassRepartidores(vista).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }


    private class ParseJSonDataClassRepartidores extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<User> repartidoresList;

        public ParseJSonDataClassRepartidores(View view) {
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
                if (FinalJSonObjectTecnicos != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectTecnicos);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        User usuario;

                        // Defining CustomSubjectNamesList AS Array List.
                        repartidoresList = new ArrayList<User>();

                        usuario = new User();

                        usuario.setId_usuario(0);
                        usuario.setNombre("Todos");
                        usuario.setUsuario("Todos");
                        repartidoresList.add(usuario);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            usuario = new User();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            usuario.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            usuario.setNombre(jsonObject.getString("nombre_usuario"));
                            usuario.setUsuario(jsonObject.getString("usuario"));
                            //usuario.setFoto(Base64.decode(jsonObject.getString("foto"), Base64.DEFAULT));


                            // Adding subject list object into CustomSubjectNamesList.
                            repartidoresList.add(usuario);
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

            //listViewTecnicos.setAdapter(adapter);

            listViewRepartidores.setAdapter(new RecyclerViewAdapterRepartidores(repartidoresList, new RecyclerViewOnItemClickListener() {

                @Override
                public void onClick(View v, int position) {
                    //Toast.makeText(OrdenesServicios.this, tecnicosList.get(position).getNombre_tecnico(), Toast.LENGTH_SHORT).show();
                    filtro_repartidor = repartidoresList.get(position).getId_usuario();

                    //ImageView ivImagentecnico = (ImageView)v.findViewById(R.id.ivImagenTecnico);
                    //ivImagentecnico.setImageResource(R.drawable.filtro_tecnico_selected);
                    loadSolicitudes(mView);



                }
            }));

            //Horizontal orientation.
            listViewRepartidores.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));




        }
    }

    public void loadSolicitudes(View view){
        actualizarFiltros();
        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;
        int rol = variablesGlobales.rol;

        String url = variablesGlobales.URLServicio + "obtenersolicitudesresumenadmin.php?filtro="+filtro+"&filtro_repartidor="+filtro_repartidor+"&limite="+String.valueOf(this.limite)+"&busqueda="+txtSearch.getText().toString();

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

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        List<Object> pedidosList;
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
                        pedidosList = new ArrayList<Object>();

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
                            pedido.setStatus(Integer.parseInt(jsonObject.getString("status")));
                            pedido.setStatus_descripcion(jsonObject.getString("descripcion_status"));
                            pedido.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion")));
                            pedido.setImporte(Double.parseDouble(jsonObject.getString("importe")));

                            pedido.setOrigen_latitud(Double.parseDouble(jsonObject.getString("origen_latitud")));
                            pedido.setOrigen_longitud(Double.parseDouble(jsonObject.getString("origen_longitud")));
                            pedido.setDestino_latitud(Double.parseDouble(jsonObject.getString("destino_latitud")));
                            pedido.setDestino_longitud(Double.parseDouble(jsonObject.getString("destino_longitud")));

                            pedido.setId_usuario_mensajero(Integer.parseInt(jsonObject.getString("id_mensajero")));
                            pedido.setNombre_mensajero(jsonObject.getString("nombre_mensajero"));
                            pedido.setCelular_mensajero(jsonObject.getString("celular_mensajero"));
                            pedido.setPlacas(jsonObject.getString("placas"));
                            pedido.setUbicacion_latitud(Double.parseDouble(jsonObject.getString("ubicacion_latitud")));
                            pedido.setUbicacion_longitud(Double.parseDouble(jsonObject.getString("ubicacion_longitud")));
                            pedido.setCalificacion(Double.parseDouble(jsonObject.getString("calificacion_mensajero")));
                            pedido.setFoto_mensajero(Base64.decode(jsonObject.getString("foto_mensajero"), Base64.DEFAULT));

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
            try{
              // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.
                final ListViewAdapterResumenAdmin adapter = new ListViewAdapterResumenAdmin(pedidosList, context);

                // Setting up all data into ListView.
                listView.setAdapter(adapter);
                swipeContainer.setRefreshing(false);

                //progressBar.setVisibility(View.GONE);
            }catch (Exception e){

            }
        }
    }

    private void seleccionarFiltro(View view){
        String[] opciones = {"Todos", "Pendiente", "En Curso", "Finalizado", "Cancelado"};

        //0=Recibido,1=Revision,2=Cotizacion,3=Reparacion,4=Reparado,5=Entregado,6=Devolucion
        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
        builder.setTitle("Filtro");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtro = which;
                if(filtro > 0)
                    btnFiltro.setImageResource(R.drawable.filtro_status_selected);
                else
                    btnFiltro.setImageResource(R.drawable.filtro_status);
                actualizarFiltros();
                loadSolicitudes(mView);
            }
        });

        Dialog dialogo =  builder.create();
        dialogo.show();
    }

    public void actualizarFiltros(){
        String status="";
        switch (filtro) {
            case 0:
                status = "Todos";
                break;
            case 1:
                status = "Pendiente";
                break;
            case 2:
                status = "En Curso";
                break;
            case 3:
                status = "Finalizado";
                break;
            case 4:
                status = "Cancelado";
                break;

        }

        String filtros = "Estatus: " + status;
        txtFiltros.setText(filtros);
    }
}
