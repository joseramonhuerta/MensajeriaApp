package com.eletronica.mensajeriaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

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

import static java.lang.Integer.parseInt;

public class EditPerfil extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    TextInputLayout txtNombreEditPerfil, txtCelularEditPerfil, txtUsuarioEditPerfil, txtPasswordEditPerfil, txtPlacasEditPerfil;
    ImageView btnCamara;
    ImageView ivFotoUsuario;
    Button btnRegistrar, btnBackLogin;

    ProgressBar progressBar;
    String HTTP_URL;
    String FinalJSonObject;
    StringRequest stringRequest;
    RequestQueue rq, requestfoto;
    JsonRequest jrq;

    String foto;
    String nombreImagen;
    File file;
    Uri output;
    Bitmap photobmp = null;

    int reenviarImagen = 0;

    boolean registroChofer = false;

    int rol = 0;
    int id_usuario = 0;

    View mView;

    String msgRegistro=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rq = Volley.newRequestQueue(EditPerfil.this);
        requestfoto = Volley.newRequestQueue(EditPerfil.this);
        mView = (View) findViewById(R.id.viewEditPerfil);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarEditPerfil);
        txtNombreEditPerfil = (TextInputLayout) findViewById(R.id.txtNombreEditPerfil);
        txtCelularEditPerfil = (TextInputLayout) findViewById(R.id.txtCelularEditPerfil);
        txtUsuarioEditPerfil = (TextInputLayout) findViewById(R.id.txtUsuarioEditPerfil);
        txtPasswordEditPerfil = (TextInputLayout) findViewById(R.id.txtPasswordEditPerfil);
        txtPlacasEditPerfil = (TextInputLayout) findViewById(R.id.txtPlacasEditPerfil);

        txtPlacasEditPerfil.setVisibility(View.GONE);

        btnRegistrar = (Button) findViewById(R.id.btnGuardarEditPerfil);
        btnBackLogin = (Button) findViewById(R.id.btnCancelarEditPerfil);

        btnCamara = (ImageView) findViewById(R.id.btnCamaraEditPerfil);
        ivFotoUsuario = (ImageView) findViewById(R.id.ivFotoUsuarioEditPerfil);

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
        id_usuario = extras.getInt("IDUsuario");
        if(rol == 3){
           txtPlacasEditPerfil.setVisibility(View.VISIBLE);
        }

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCamara(1);
            }
        });

        getUsuario();

    }

    private void getUsuario(){

        progressBar.setVisibility(View.VISIBLE);
        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "obtener_usuario.php?id_usuario="+ String.valueOf(this.id_usuario);
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
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
                output = FileProvider.getUriForFile(EditPerfil.this,"com.eletronica.mensajeriaapp.fileprovider", photoFile);
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
        String val = txtNombreEditPerfil.getEditText().getText().toString();

        if(val.isEmpty()){
            txtNombreEditPerfil.setError("Introduzca el Nombre");
            return false;
        }else{
            txtNombreEditPerfil.setError(null);
            return true;
        }
    }

    private Boolean validarCelular(){
        String val = txtCelularEditPerfil.getEditText().getText().toString();
        String patternCelular = "[0-9]{10}$";

        if(val.isEmpty()){
            txtCelularEditPerfil.setError("Introduzca el Celular");
            return false;
        }else if(!val.matches(patternCelular)){
            txtCelularEditPerfil.setError("Solo Números de 10 dígitos");
            return false;
        }else{
            txtCelularEditPerfil.setError(null);
            return true;
        }
    }

    private Boolean validarUsuario(){
        String val = txtUsuarioEditPerfil.getEditText().getText().toString();
        String pattern = "[a-zA-Z0-9]$";

        if(val.isEmpty()){
            txtUsuarioEditPerfil.setError("Introduzca el Usuario");
            return false;
        }else if(val.length() > 20){
            txtUsuarioEditPerfil.setError("Usuario muy largo");
            return false;
        }/*else if(!val.matches(pattern)){
            txtUsuario.setError("Usuario incorrecto");
            return false;
        }*/else{
            txtUsuarioEditPerfil.setError(null);
            return true;
        }
    }

    private Boolean validarPassword(){
        String val = txtPasswordEditPerfil.getEditText().getText().toString();
        String patternPassword = "[a-zA-Z0-9]{20}$";

        if(val.isEmpty()){
            txtPasswordEditPerfil.setError("Introduzca la Contraseña");
            return false;
        }else if(val.length() > 20){
            txtPasswordEditPerfil.setError("Contraseña muy larga");
            return false;
        }/*else if(!val.matches(patternPassword)){
            txtPassword.setError("Contraseña incorrecta");
            return false;
        }*/else{
            txtPasswordEditPerfil.setError(null);
            return true;
        }
    }

    private Boolean validarFoto(){


        if(photobmp == null){
            Toast.makeText(EditPerfil.this,"Debe tomarse una foto de perfil", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private Boolean validarPlacas(){
        String val = txtPlacasEditPerfil.getEditText().getText().toString();
        if (rol == 3) {
            if(val.isEmpty()){
                txtPlacasEditPerfil.setError("Introduzca las Placas");
                return false;
            }else{
                txtPlacasEditPerfil.setError(null);
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

        HTTP_URL =variablesGlobales.URLServicio + "editar_usuario.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new EditPerfil.ParseJSonDataClassGuardar(vista).execute();
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
                String id_user = String.valueOf(id_usuario);
                String nombre = txtNombreEditPerfil .getEditText().getText().toString();
                String celular = txtCelularEditPerfil.getEditText().getText().toString();
                String usuario = txtUsuarioEditPerfil.getEditText().getText().toString();
                String password = txtPasswordEditPerfil.getEditText().getText().toString();
                String role=String.valueOf(rol);
                String placas = txtPlacasEditPerfil .getEditText().getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", id_user);
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(EditPerfil.this,"No se encontro el usuario.", Toast.LENGTH_SHORT).show();
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
            usuario.setCelular(jsonObject.optString("celular"));
            usuario.setRol(parseInt(jsonObject.optString("rol")));
            usuario.setDescripcion_rol(jsonObject.optString("descripcion_rol"));
            usuario.setFoto(Base64.decode(jsonObject.getString("foto"), Base64.DEFAULT));
            usuario.setToken(jsonObject.optString("pass"));
            usuario.setToken(jsonObject.optString("token"));
            usuario.setPlacas(jsonObject.optString("placas"));
            usuario.setPassword(jsonObject.optString("pass"));

            GlobalVariables variablesGlobales = new GlobalVariables();

            txtNombreEditPerfil.getEditText().setText(usuario.getNombre());
            txtUsuarioEditPerfil.getEditText().setText(usuario.getUsuario());
            txtPlacasEditPerfil.getEditText().setText(usuario.getPlacas());
            txtCelularEditPerfil.getEditText().setText(usuario.getCelular());
            txtPasswordEditPerfil.getEditText().setText(usuario.getPassword());



            String urlImg = variablesGlobales.URLServicio + "fotos/" + String.valueOf(variablesGlobales.id_usuario)+".jpg";
            urlImg = urlImg.replace(" ","%20");
            try{
                ImageRequest imageRequest = new ImageRequest(urlImg, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        photobmp = response;
                        ivFotoUsuario.setImageBitmap(photobmp);


                    }
                },0,0, ImageView.ScaleType.CENTER,null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
                requestfoto.add(imageRequest);
            } catch (Exception e) {
                //e.printStackTrace();
                //Toast.makeText(EditPerfil.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

            }





        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(EditPerfil.this,"Error: " + e.toString(), Toast.LENGTH_SHORT).show();

        }
        /*
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", txtUsuario.getEditText().getText().toString());
        editor.putString("pass", txtPassword.getEditText().getText().toString());
        editor.putInt("id_usuario",  usuario.getId_usuario());
        editor.putString("nombre", usuario.getNombre());
        editor.putInt("rol",  usuario.getRol());
        editor.putString("descripcion_rol",usuario.getDescripcion_rol());
        editor.putString("token", usuario.getToken());

        editor.commit();
        */
        //Toast.makeText(EditPerfil.this,"Usuario Actualizado", Toast.LENGTH_SHORT).show();

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
                            GlobalVariables variablesGlobales = new GlobalVariables();
                            variablesGlobales.nombre = txtNombreEditPerfil.getEditText().getText().toString();
                            variablesGlobales.usuario = txtUsuarioEditPerfil.getEditText().getText().toString();
                            variablesGlobales.celular = txtCelularEditPerfil.getEditText().getText().toString();
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

}