package com.ifp.inventory_savers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class AlmacenesActivity extends AppCompatActivity {

    protected Button botonVolver;
    protected ImageButton listaImageButton,crearImageButton;
    protected TextView text1_almacenes; // Modificado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almacenes);


        botonVolver=findViewById(R.id.buttonVolver_almacenes);
        crearImageButton=findViewById(R.id.imageButtonAdd_almacenes);
        listaImageButton= findViewById(R.id.imageButtonLista_almacenes);
        Glide.with(this)
                .asGif()
                .load(R.drawable.add)
                .into(crearImageButton);
        Glide.with(this)
                .asGif()
                .load(R.drawable.tasks)
                .into(listaImageButton);
        crearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AlmacenesActivity.this,CrearAlmacen.class));
            }
        });
        listaImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AlmacenesActivity.this,ListaAlmacenes.class));
            }
        });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlmacenesActivity.this,MenuActivity.class));
            }
        });
        String title = getResources().getString(R.string.inventrory_savers);
        setTitle(title);
        // Cambiar el color del título
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }
}