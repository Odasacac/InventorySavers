package com.ifp.inventory_savers;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class EventosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        String title = getResources().getString(R.string.inventrory_savers);
        setTitle(title);

        // Cambiar el color del título
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
        }

        ImageView imageView1_eventos = findViewById(R.id.imageView1_eventos);
        imageView1_eventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar un Toast con el mensaje "Dirigiendo a menú"
                String mensaje = getString(R.string.toast_dirigir_menu);
                Toast.makeText(EventosActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                // Iniciar TransicionActivity para ir a MenuActivity
                Intent intent = new Intent(EventosActivity.this, TransicionActivity.class);
                intent.putExtra("ACTION", "IR_A_MENU");
                startActivity(intent); // Iniciar la actividad MenuActivity
            }
        });

        ImageView imageView2_eventos = findViewById(R.id.imageView2_eventos);
        imageView2_eventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = getString(R.string.toast_dirigir_opciones);
                Toast.makeText(EventosActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                // Aquí puedes agregar el código para dirigir a la actividad de opciones si es necesario
            }
        });
        ImageView imageView3_almacenes = findViewById(R.id.imageView3_eventos);
        imageView3_almacenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventosActivity.this, getString(R.string.mensaje_redes_sociales), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }
}