package com.eletronica.mensajeriaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    TextInputLayout txtNombreRegistro, txtCelularRegistro, txtUsuarioRegistro, txtPasswordRegistro;

    Button btnRegistrar, btnBackLogin;

    ProgressBar progressBar;

    RequestQueue rq;
    JsonRequest jrq;

    int rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rq = Volley.newRequestQueue(Registro.this);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarRegistro);
        txtNombreRegistro = (TextInputLayout) findViewById(R.id.txtNombreRegistro);
        txtCelularRegistro = (TextInputLayout) findViewById(R.id.txtCelularRegistro);
        txtUsuarioRegistro = (TextInputLayout) findViewById(R.id.txtUsuarioRegistro);
        txtPasswordRegistro = (TextInputLayout) findViewById(R.id.txtPasswordRegistro);

        btnRegistrar = (Button) findViewById(R.id.btnRegistrarRegistro);
        btnBackLogin = (Button) findViewById(R.id.btnBackLoginRegistro);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarNombre() | !validarCelular() | !validarUsuario() | !validarPassword()){
                    return;
                }

                registrar();

            }
        });

        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();

        rol = extras.getInt("Rol");

        if(rol == 1){
            btnRegistrar.setText("REGISTRAR");
            btnBackLogin.setText("ATRAS");
        }


    }

    private Boolean validarNombre(){
        String val = txtNombreRegistro.getEditText().getText().toString();

        if(val.isEmpty()){
            txtNombreRegistro.setError("Introduzca el Nombre");
            return false;
        }else{
            txtNombreRegistro.setError(null);
            return true;
        }
    }

    private Boolean validarCelular(){
        String val = txtCelularRegistro.getEditText().getText().toString();
        String patternCelular = "[0-9]{10}$";

        if(val.isEmpty()){
            txtCelularRegistro.setError("Introduzca el Celular");
            return false;
        }else if(!val.matches(patternCelular)){
            txtCelularRegistro.setError("Solo Números de 10 dígitos");
            return false;
        }else{
            txtCelularRegistro.setError(null);
            return true;
        }
    }

    private Boolean validarUsuario(){
        String val = txtUsuarioRegistro.getEditText().getText().toString();
        String pattern = "[a-zA-Z0-9]$";

        if(val.isEmpty()){
            txtUsuarioRegistro.setError("Introduzca el Usuario");
            return false;
        }else if(val.length() > 20){
            txtUsuarioRegistro.setError("Usuario muy largo");
            return false;
        }/*else if(!val.matches(pattern)){
            txtUsuario.setError("Usuario incorrecto");
            return false;
        }*/else{
            txtUsuarioRegistro.setError(null);
            return true;
        }
    }

    private Boolean validarPassword(){
        String val = txtPasswordRegistro.getEditText().getText().toString();
        String patternPassword = "[a-zA-Z0-9]{20}$";

        if(val.isEmpty()){
            txtPasswordRegistro.setError("Introduzca la Contraseña");
            return false;
        }else if(val.length() > 20){
            txtPasswordRegistro.setError("Contraseña muy larga");
            return false;
        }/*else if(!val.matches(patternPassword)){
            txtPassword.setError("Contraseña incorrecta");
            return false;
        }*/else{
            txtPasswordRegistro.setError(null);
            return true;
        }
    }

    private void registrar(){
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();

        String nombre = txtNombreRegistro .getEditText().getText().toString();
        String celular = txtCelularRegistro.getEditText().getText().toString();
        String usuario = txtUsuarioRegistro.getEditText().getText().toString();
        String password = txtPasswordRegistro.getEditText().getText().toString();

        String url = variablesGlobales.URLServicio + "registrar_usuario.php?nombre_usuario="+nombre+"&celular="+celular+"&usuario="+usuario+"&pass="+password+"&rol="+String.valueOf(rol);
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }


    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);

        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;
        boolean success = false;
        try {
            jsonObject = jsonArray.getJSONObject(0);
            String msg =jsonObject.optString("msg");
            success = Boolean.parseBoolean(jsonObject.optString("success"));

            Toast.makeText(Registro.this,msg, Toast.LENGTH_SHORT).show();

            if(success) {
                finish();
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(Registro.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(Registro.this,"No se pudo registrar.", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

}
