package com.eletronica.mensajeriaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import static java.lang.Integer.parseInt;

public class InicioSesion extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    Button btnRegristro, btnIniciarSesion;
    ImageView logo;
    TextView txtLogoName, txtSesion;
    TextInputLayout txtUsuario, txtPassword;

    RequestQueue rq;
    JsonRequest jrq;

    ProgressBar progressBar;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_inicio_sesion);
        rq = Volley.newRequestQueue(InicioSesion.this);

        btnIniciarSesion =(Button) findViewById(R.id.btnAceptarDialogoSolicitud);
        btnRegristro = (Button) findViewById(R.id.btnRegistrarse);
        logo = (ImageView) findViewById(R.id.imgLogo);
        txtLogoName = (TextView) findViewById(R.id.txtLogoName);
        txtSesion = (TextView) findViewById(R.id.txtSesion);
        txtUsuario = (TextInputLayout) findViewById(R.id.txtUsuario);
        txtPassword = (TextInputLayout) findViewById(R.id.txtPassword);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        prefs = getApplicationContext().getSharedPreferences("MisPreferenciasMensajeria", Context.MODE_PRIVATE);



        //String user = prefs.getString("user", "");
        //String pass = prefs.getString("pass", "");
        //txtUsuario.getEditText().setText(user);
        //txtPassword.getEditText().setText(pass);

        if(prefs.contains("user")){
            User usuario = new User();

            String user = prefs.getString("user", "");
            String nombre = prefs.getString("nombre", "");
            String celular = prefs.getString("celular", "");
            int id_user = prefs.getInt("id_usuario", 0);
            int rol = prefs.getInt("rol", 0);
            String descripcion_rol = prefs.getString("descripcion_rol", "");
            String token = prefs.getString("token","");


            usuario.setUsuario(user);
            usuario.setNombre(nombre);
            usuario.setId_usuario(id_user);
            usuario.setRol(rol);
            usuario.setCelular(celular);
            usuario.setDescripcion_rol(descripcion_rol);
            usuario.setToken(token);

            GlobalVariables variablesGlobales = new GlobalVariables();
            variablesGlobales.id_usuario = id_user;
            variablesGlobales.nombre = nombre;
            variablesGlobales.usuario = user;
            variablesGlobales.celular = celular;
            variablesGlobales.rol = rol;
            variablesGlobales.descripcion_rol = descripcion_rol;
            variablesGlobales.token = token;

            Intent intencion;

            if(rol == 1){
                intencion = new Intent(InicioSesion.this, Principal.class);
            }else if(rol == 2){
                intencion = new Intent(InicioSesion.this, PrincipalCustomer.class);
            }else if(rol == 3){
                intencion = new Intent(InicioSesion.this, PrincipalAdmin.class);
            }else{
                intencion = new Intent(InicioSesion.this, PrincipalCustomer.class);
            }

            intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intencion);

        }




        btnRegristro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(InicioSesion.this, Registro.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("Rol", 2);
                    startActivity(intent);


            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarUsuario() || !validarPassword()){
                    return;
                }

                iniciarSesion();

            }
        });

        notifications();
    }

    private void notifications(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                           return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = token.toString();

                        Log.e("getToken", "Get token: " + msg);

                        //Toast.makeText(InicioSesion.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Boolean validarUsuario(){
        String val = txtUsuario.getEditText().getText().toString();

        if(val.isEmpty()){
            txtUsuario.setError("Introduzca el Usuario");
            return false;
        }else{
            txtUsuario.setError(null);
            return true;
        }

    }

    private Boolean validarPassword(){
        String val = txtPassword.getEditText().getText().toString();

        if(val.isEmpty()){
            txtPassword.setError("Introduzca la Contraseña");
            return false;
        }else{
            txtPassword.setError(null);
            return true;
        }

    }

    private void iniciarSesion(){

        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "sesion.php?usuario="+txtUsuario.getEditText().getText().toString()+
                "&clave="+txtPassword.getEditText().getText().toString() + "&token=" + variablesGlobales.token;
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(InicioSesion.this,"Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);

        User usuario = new User();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            usuario.setUsuario(jsonObject.optString("usuario"));
            usuario.setNombre(jsonObject.optString("nombre_usuario"));
            usuario.setId_usuario(parseInt(jsonObject.optString("id_usuario")));
            usuario.setRol(parseInt(jsonObject.optString("rol")));
            usuario.setDescripcion_rol(jsonObject.optString("descripcion_rol"));
            usuario.setFoto(Base64.decode(jsonObject.getString("foto"), Base64.DEFAULT));
            usuario.setToken(jsonObject.optString("token"));
            usuario.setCelular(jsonObject.optString("celular"));

            GlobalVariables variablesGlobales = new GlobalVariables();
            variablesGlobales.id_usuario = usuario.getId_usuario();
            variablesGlobales.nombre = usuario.getNombre();
            variablesGlobales.usuario = usuario.getUsuario();
            variablesGlobales.rol = usuario.getRol();
            variablesGlobales.descripcion_rol = usuario.getDescripcion_rol();
            variablesGlobales.foto = usuario.getFoto();
            variablesGlobales.token = usuario.getToken();
            variablesGlobales.celular = usuario.getCelular();
            String topic = "";

            if(usuario.getRol() == 1)
                topic = "topicUsuarios";
            else if(usuario.getRol() == 2)
                topic = "topicClientes";
            else if(usuario.getRol() == 3)
                topic = "topicAdmin";
            else
                topic = "topicClientes";

            FirebaseMessaging.getInstance().subscribeToTopic(topic);

        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(InicioSesion.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", txtUsuario.getEditText().getText().toString());
        editor.putString("pass", txtPassword.getEditText().getText().toString());
        editor.putInt("id_usuario",  usuario.getId_usuario());
        editor.putString("nombre", usuario.getNombre());
        editor.putInt("rol",  usuario.getRol());
        editor.putString("celular",  usuario.getCelular());
        editor.putString("descripcion_rol",usuario.getDescripcion_rol());
        editor.putString("token", usuario.getToken());

        editor.commit();


        Intent intencion;

        if(usuario.getRol() == 1){
            intencion = new Intent(InicioSesion.this, Principal.class);
        }else if(usuario.getRol() == 2){
            intencion = new Intent(InicioSesion.this, PrincipalCustomer.class);
        }else if(usuario.getRol() == 3){
            intencion = new Intent(InicioSesion.this, PrincipalAdmin.class);
        }else{
            intencion = new Intent(InicioSesion.this, PrincipalCustomer.class);
        }

        intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intencion);


    }

    @Override public void onBackPressed() {

    }
}
