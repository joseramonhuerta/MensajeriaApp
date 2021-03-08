package com.eletronica.mensajeriaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.eletronica.mensajeriaapp.fragments.IngresosFragment;
import com.eletronica.mensajeriaapp.fragments.NuevaSolicitudFragment;
import com.eletronica.mensajeriaapp.fragments.PerfilFragment;
import com.eletronica.mensajeriaapp.fragments.ResumenFragment;
import com.eletronica.mensajeriaapp.fragments.SolicitudesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalCustomer extends AppCompatActivity {
    BottomNavigationView mMenu;
    int itemSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_customer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showSeletedFragment(new NuevaSolicitudFragment(), "NuevaSolicitudFragment", R.id.menu_nueva_solicitud);

        mMenu = (BottomNavigationView) findViewById(R.id.menu_principal_customer);

        mMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_nueva_solicitud & itemSelected != item.getItemId()){
                    showSeletedFragment(new NuevaSolicitudFragment(), "NuevaSolicitudFragment", item.getItemId());
                }

                if(item.getItemId() == R.id.menu_solicitudes_customer & itemSelected != item.getItemId()){
                    showSeletedFragment(new ResumenFragment(), "ResumenFragment", item.getItemId());
                }

                if(item.getItemId() == R.id.menu_perfil_customer & itemSelected != item.getItemId()){
                    showSeletedFragment(new PerfilFragment(), "PerfilFragment", item.getItemId());
                }
                return true;
            }
        });
    }

    private void showSeletedFragment(Fragment fragment, String tag, int item){
        getSupportFragmentManager().beginTransaction().replace(R.id.containerCustomer, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        itemSelected = item;
    }
}
