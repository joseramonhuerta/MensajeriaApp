package com.eletronica.mensajeriaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.eletronica.mensajeriaapp.fragments.IngresosFragment;
import com.eletronica.mensajeriaapp.fragments.PerfilFragment;
import com.eletronica.mensajeriaapp.fragments.RegistroUsuariosFragment;
import com.eletronica.mensajeriaapp.fragments.ResumenAdminFragment;
import com.eletronica.mensajeriaapp.fragments.SolicitudesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalAdmin extends AppCompatActivity {
    BottomNavigationView mMenu;
    int itemSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_admin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showSeletedFragment(new ResumenAdminFragment(), "SolicitudesAdminFragment", R.id.menu_solicitudes_admin);

        mMenu = (BottomNavigationView) findViewById(R.id.menu_principal_admin);

        mMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_solicitudes_admin & itemSelected != item.getItemId()){
                    showSeletedFragment(new ResumenAdminFragment(), "SolicitudesAdminFragment", R.id.menu_solicitudes_admin);
                }

                if(item.getItemId() == R.id.menu_usuarios & itemSelected != item.getItemId()){
                    showSeletedFragment(new RegistroUsuariosFragment(), "RegistroUsuariosFragment", R.id.menu_usuarios);
                }

                if(item.getItemId() == R.id.menu_perfil_admin & itemSelected != item.getItemId()){
                    showSeletedFragment(new PerfilFragment(), "PerfilFragment", item.getItemId());
                }
                return true;
            }
        });
    }

    private void showSeletedFragment(Fragment fragment, String tag, int item){
        getSupportFragmentManager().beginTransaction().replace(R.id.containerAdmin, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        itemSelected = item;
    }
}