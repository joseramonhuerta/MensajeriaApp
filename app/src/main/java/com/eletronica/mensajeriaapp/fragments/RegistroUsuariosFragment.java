package com.eletronica.mensajeriaapp.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eletronica.mensajeriaapp.GlobalVariables;
import com.eletronica.mensajeriaapp.Header;
import com.eletronica.mensajeriaapp.InicioSesion;
import com.eletronica.mensajeriaapp.ListViewAdapterResumenAdmin;
import com.eletronica.mensajeriaapp.ListViewAdapterUsuarios;
import com.eletronica.mensajeriaapp.Pedido;
import com.eletronica.mensajeriaapp.R;
import com.eletronica.mensajeriaapp.RecyclerViewAdapterRepartidores;
import com.eletronica.mensajeriaapp.RecyclerViewOnItemClickListener;
import com.eletronica.mensajeriaapp.Registro;
import com.eletronica.mensajeriaapp.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistroUsuariosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ListViewAdapterUsuarios.CambiarStatus{
    static View mView;

    ListView listView;
    ImageView btnClear;
    ImageView btnBusqueda;
    ImageView btnAgregarUsuario;
    EditText txtSearch;
    SwipeRefreshLayout swipeContainer;

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    // Server Http URL
    String HTTP_URL;
    String FinalJSonObject ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_usuarios, container, false);
        this.mView = view;

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainerUsuarios);
        txtSearch = (EditText) view.findViewById(R.id.txtSearchUsuarios);

        listView = (ListView) view.findViewById(R.id.listViewUsuarios);

        btnClear = (ImageView) view.findViewById(R.id.btnClearSearchUsuarios);
        btnAgregarUsuario = (ImageView) view.findViewById(R.id.btnAgregarUsuarios);
        btnBusqueda = (ImageView) view.findViewById(R.id.btnBusquedaUsuarios);

        btnBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsuarios(mView);
            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
                loadUsuarios(mView);
            }
        });

        btnAgregarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Registro.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("Rol", 1);
                startActivity(intent);

            }
        });
        swipeContainer.setOnRefreshListener(this);


        loadUsuarios(mView);
        return view;
    }


    @Override
    public void onRefresh() {

        loadUsuarios(mView);
    }

    public void loadUsuarios(View view){
        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;
        int rol = variablesGlobales.rol;

        String url = variablesGlobales.URLServicio + "obtenerusuarios.php?busqueda="+txtSearch.getText().toString();

        try {
            StringRequest stringRequest = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new RegistroUsuariosFragment.ParseJSonDataClass(vista).execute();

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
    public void cambiarStatus(int id, String status) {
        //progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "cambiar_status_usuario.php?id_usuario="+String.valueOf(id)+
                "&status="+status;
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //progressBar.setVisibility(View.GONE);

                    JSONArray jsonArray = response.optJSONArray("datos");
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        boolean success = Boolean.parseBoolean(jsonObject.optString("sucess"));

                        if (success) {
                            loadUsuarios(mView);
                        }

                        String msg = jsonObject.optString("msg");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        //e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            );

            request = Volley.newRequestQueue(getContext());
            request.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        List<User> usuariosList;
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
                        User usuario;


                        // Defining CustomSubjectNamesList AS Array List.
                        usuariosList = new ArrayList<User>();

                        for (int i = 0; i < jsonArray.length(); i++) {


                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.

                            //if(Integer.parseInt(jsonObject.getString("template")) == 2) {
                            usuario = new User();

                            usuario.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            usuario.setNombre(jsonObject.getString("nombre_usuario"));
                            usuario.setUsuario(jsonObject.getString("usuario"));
                            usuario.setCelular(jsonObject.getString("celular"));
                            usuario.setStatus(jsonObject.getString("status"));
                            usuario.setStatus_descripcion(jsonObject.getString("status_descripcion"));

                            usuariosList.add(usuario);

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
                final ListViewAdapterUsuarios adapter = new ListViewAdapterUsuarios(usuariosList, context, RegistroUsuariosFragment.this);

                // Setting up all data into ListView.
                listView.setAdapter(adapter);
                swipeContainer.setRefreshing(false);

                //progressBar.setVisibility(View.GONE);
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadUsuarios(mView);
    }

}
