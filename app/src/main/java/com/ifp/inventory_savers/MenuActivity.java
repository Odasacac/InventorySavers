package com.ifp.inventory_savers;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    protected ImageButton botonAlmacenes,botonProductos,botonUsuarios,botonReportes;
    protected Button botonLoginUserDep;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        String title = getResources().getString(R.string.inventrory_savers);
        setTitle(title);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
        }



        botonLoginUserDep=(Button) findViewById(R.id.buttonLoginUserDep_menu);
        mAuth = FirebaseAuth.getInstance();
        botonAlmacenes=findViewById(R.id.imageButtonAlamcenes_menu);
        botonProductos= findViewById(R.id.imageButtonProductos_menu);
        botonUsuarios=findViewById(R.id.imageButtonUsuarios_menu);
        botonReportes= findViewById(R.id.imageButtonReportes_menu);
        Glide.with(this)
                .asGif()
                .load(R.drawable.cajaalmacen)
                .into(botonAlmacenes);
        Glide.with(this)
                .asGif()
                .load(R.drawable.productos)
                .into(botonProductos);
        Glide.with(this)
                .asGif()
                .load(R.drawable.usuarios)
                .into(botonUsuarios);
        Glide.with(this)
                .asGif()
                .load(R.drawable.graficasok)
                .into(botonReportes);
        botonAlmacenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, AlmacenesActivity.class));
            }
        });botonProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, ProductosMasterActivity.class));
            }
        });botonUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, UsuariosActivity.class));
            }
        });botonReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, ReportesActivity.class));
            }
        });
        botonLoginUserDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SeleccionAlmacenesUsuarioDep.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_au_org:
                String url = "https://sites.google.com/view/proyecto-2dam-grupo17/inicio";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            case R.id.item_rrss_org:
                Toast.makeText(this, "Dirigiendo a redes sociales", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item_salir_org:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
}