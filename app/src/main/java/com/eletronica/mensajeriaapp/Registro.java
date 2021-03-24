package com.eletronica.mensajeriaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    TextInputLayout txtNombreRegistro, txtCelularRegistro, txtUsuarioRegistro, txtPasswordRegistro, txtPlacasRegistro;
    ImageView btnCamara;
    ImageView ivFotoUsuario;
    Button btnRegistrar, btnBackLogin;

    ProgressBar progressBar;
    String HTTP_URL;
    String FinalJSonObject;
    StringRequest stringRequest;
    RequestQueue rq;
    JsonRequest jrq;

    String foto;
    String nombreImagen;
    File file;
    Uri output;
    Bitmap photobmp = null;

    int reenviarImagen = 0;

    boolean registroChofer = false;

    int rol = 0;

    View mView;

    String msgRegistro=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rq = Volley.newRequestQueue(Registro.this);
        mView = (View) findViewById(R.id.viewRegistro);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarRegistro);
        txtNombreRegistro = (TextInputLayout) findViewById(R.id.txtNombreRegistro);
        txtCelularRegistro = (TextInputLayout) findViewById(R.id.txtCelularRegistro);
        txtUsuarioRegistro = (TextInputLayout) findViewById(R.id.txtUsuarioRegistro);
        txtPasswordRegistro = (TextInputLayout) findViewById(R.id.txtPasswordRegistro);
        txtPlacasRegistro = (TextInputLayout) findViewById(R.id.txtPlacasRegistro);

        txtPlacasRegistro.setVisibility(View.GONE);

        btnRegistrar = (Button) findViewById(R.id.btnRegistrarRegistro);
        btnBackLogin = (Button) findViewById(R.id.btnBackLoginRegistro);

        btnCamara = (ImageView) findViewById(R.id.btnCamara);
        ivFotoUsuario = (ImageView) findViewById(R.id.ivFotoUsuario);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarNombre() | !validarCelular() | !validarUsuario() | !validarPassword() | !validarFoto() |!validarPlacas()){
                    return;
                }

                registrar(v);

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

        if(rol == 3){
            btnRegistrar.setText("REGISTRAR");
            btnBackLogin.setText("ATRAS");

            txtPlacasRegistro.setVisibility(View.VISIBLE);
        }

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCamara(1);
            }
        });


    }

    private void getCamara(int requestCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                nombreImagen = photoFile.getAbsolutePath();
                } catch(Exception ex) {

            }
            if (photoFile != null) {
                output = FileProvider.getUriForFile(Registro.this,"com.eletronica.mensajeriaapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombre = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombre,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitView = null;
        if ((requestCode == 1) && resultCode == RESULT_OK) {

            //ContentResolver cr = this.getContentResolver();
            Bitmap bit = null;
            try {
                /*
                bit = android.provider.MediaStore.Images.Media.getBitmap(cr, output);
                int rotate = 0;
                ExifInterface exif = new ExifInterface(
                        file.getAbsolutePath());
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = -270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = -180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = -90;
                        break;
                }

                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);

                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, true);

                //Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                //Bundle extras = data.getExtras();
                //bit = (Bitmap) extras.get("data");

                */

                File f = new File(nombreImagen);
                ExifInterface exif = new ExifInterface(f.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int angle = 0;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    angle = 90;
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    angle = 180;
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    angle = 270;
                }        Matrix mat = new Matrix();
                mat.postRotate(angle);
                Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, 297, 420, false);
                //bitView = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                bit = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            photobmp = bit;
            ivFotoUsuario.setImageBitmap(bit);
            reenviarImagen = 1;
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

    private Boolean validarFoto(){


        if(photobmp == null){
            Toast.makeText(Registro.this,"Debe tomarse una foto de perfil", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private Boolean validarPlacas(){
        String val = txtPlacasRegistro.getEditText().getText().toString();
        if (rol == 3) {
            if(val.isEmpty()){
                txtPlacasRegistro.setError("Introduzca las Placas");
                return false;
            }else{
                txtPlacasRegistro.setError(null);
                return true;
            }
        }else
            return true;
    }



    private void registrar(View view){
        final View vista = view;
        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String imagen = null;

        //String nombre = txtNombreRegistro .getEditText().getText().toString();
        //String celular = txtCelularRegistro.getEditText().getText().toString();
        //String usuario = txtUsuarioRegistro.getEditText().getText().toString();
        //String password = txtPasswordRegistro.getEditText().getText().toString();

        //if(reenviarImagen == 1 && photobmp != null){
         //   imagen =  convertirImgString(photobmp);
        //}

        //String url = variablesGlobales.URLServicio + "registrar_usuario.php?nombre_usuario="+nombre+"&celular="+celular+"&usuario="+usuario+"&pass="+password+"&rol="+String.valueOf(rol)+"&imagen="+imagen;
        // = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
       // rq.add(jrq);

        HTTP_URL =variablesGlobales.URLServicio + "registrar_usuario.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new Registro.ParseJSonDataClassGuardar(vista).execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Showing error message if something goes wrong.
                Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String imagen = null;

                String nombre = txtNombreRegistro .getEditText().getText().toString();
                String celular = txtCelularRegistro.getEditText().getText().toString();
                String usuario = txtUsuarioRegistro.getEditText().getText().toString();
                String password = txtPasswordRegistro.getEditText().getText().toString();
                String role=String.valueOf(rol);
                String placas = txtPlacasRegistro .getEditText().getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre_usuario", nombre);
                parametros.put("celular", celular);
                parametros.put("usuario", usuario);
                parametros.put("pass", password);

                parametros.put("rol", role);

                if(rol == 3)
                    parametros.put("placas", placas);

                if(reenviarImagen == 1 && photobmp != null){
                    imagen =  convertirImgString(photobmp);
                    parametros.put("imagen", imagen);
                }

                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        User usuario;

        public ParseJSonDataClassGuardar(View view) {
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
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        boolean success = jsonObject.getBoolean("success");
                        this.msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");



                        if(success) {
                            finish();
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
            super.onPostExecute(result);
            Toast.makeText(mView.getContext(),this.msg, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);

        }
    }

    private String convertirImgString(Bitmap bitmap){
        String encodedImage = null;
        if(bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return encodedImage;
    }
    /*
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
    */
}
