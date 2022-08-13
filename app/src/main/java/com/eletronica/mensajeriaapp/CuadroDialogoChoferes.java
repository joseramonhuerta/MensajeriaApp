
package com.eletronica.mensajeriaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CuadroDialogoChoferes extends DialogFragment {
    Context mContext;
    FragmentManager fm;
    Dialog dialogo = null;
    View mView;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    TextView txtSearchUser;
    ImageView btnClearSearchUser;
    ListView listViewUsers;
    Button btnAceptar;
    Button btnSalir;

    ProgressBar progressBar;

    String HTTP_URL = "";
    String FinalJSonObject ;

    int position_selected=-1;
    int id_pedido=0;
    int id_usuario=0;
    String nombre_usuario="";
    String celular="";
    Pedido pedido = null;

    public interface ActualizarUsuario {

        public void actualizaActividadUsuario(View view,int id_usuario,String nombre_usuario);
    }

    CuadroDialogoChoferes.ActualizarUsuario listener;
    Activity activity;

    public CuadroDialogoChoferes(Context context, FragmentManager fm, View view) {
        this.mContext = context;
        this.fm = fm;
        this.mView = view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ActualizarUsuario) getTargetFragment();
            activity = getTargetFragment().getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.cuadro_dialogo_choferes, null);
        request = Volley.newRequestQueue(getContext());
        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Bundle b = getArguments();

        pedido = (Pedido) b.getSerializable("pedido");
        id_pedido = pedido.getId_pedido();


        txtSearchUser = (TextView) v.findViewById(R.id.txtSearchDialogoChoferes);
        btnClearSearchUser = (ImageView) v.findViewById(R.id.btnClearSearchDialogoChoferes);

        btnAceptar = (Button) v.findViewById(R.id.btnAceptarDialogoChoferes);
        btnSalir = (Button) v.findViewById(R.id.btnSalirDialogoChoferes);

        listViewUsers = (ListView) v.findViewById(R.id.listViewDialogoChoferes);

        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarDialogoChoferes);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position_selected > -1) {
                    actualizarChofer();
                    //Toast.makeText(v.getContext(), desc + "Entregar ID: " + String.valueOf(idAsig) + " IDPRo: " + String.valueOf(idPro) + " Pedido: " + String.valueOf(id_pedido) + " Precio: " + String.valueOf(precio_renta), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(v.getContext(), "Debe seleccionar un chofer", Toast.LENGTH_SHORT).show();
                }


            }
        });



        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();;
            }
        });

        btnClearSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearchUser.setText("");

            }
        });

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                position_selected = position;
                User selItem = (User) listViewUsers.getAdapter().getItem(position);

                id_usuario =  selItem.getId_usuario();
                nombre_usuario = selItem.getNombre();
            }
        });

        loadChoferes(v);



        return v;

    }

    private void actualizarChofer(){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "actualizarChofer.php?id_usuario="+String.valueOf(id_usuario)+
                "&id_pedido="+String.valueOf(id_pedido);

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONArray jsonArray = response.optJSONArray("datos");
                JSONObject jsonObject = null;

                try {
                    jsonObject = jsonArray.getJSONObject(0);
                    boolean success =  Boolean.parseBoolean(jsonObject.optString("sucess"));

                    if(success){
                        dialogo.dismiss();
                        listener.actualizaActividadUsuario(mView,id_usuario,nombre_usuario);

                    }

                    String msg = jsonObject.optString("msg");
                    Toast.makeText(mContext,msg, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(mContext,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,"Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        request.add(jsonObjectRequest);
    }

    private void loadChoferes(final View view){
        progressBar.setVisibility(View.VISIBLE);

        GlobalVariables variablesGlobales = new GlobalVariables();

        HTTP_URL = variablesGlobales.URLServicio + "obtenerchoferes.php";
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new CuadroDialogoChoferes.ParseJSonDataClass(view.getContext()).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(view.getContext(),error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        List<User> usersList;

        public ParseJSonDataClass(Context context) {

            this.context = context;
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
                        usersList = new ArrayList<User>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            usuario = new User();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            usuario.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                            usuario.setNombre(jsonObject.getString("nombre_usuario"));


                            //Storing Subject name in subject list.


                            // Adding subject list object into CustomSubjectNamesList.
                            usersList.add(usuario);
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
            // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.
            //FragmentManager fm = getFragmentManager();
            final ListViewAdapterChoferes adapter = new ListViewAdapterChoferes(usersList, context);

            // Setting up all data into ListView.
            listViewUsers.setAdapter(adapter);

            txtSearchUser.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //listView.getAdapter().getFilter().filter(s.toString());


                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //listView.getAdapter().
                    String text = txtSearchUser.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                    // listView.getAdapter().getFilter().filter(s.toString());
                }
            });

            // Hiding progress bar after all JSON loading done.
            progressBar.setVisibility(View.GONE);

        }
    }



}
