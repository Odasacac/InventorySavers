package com.ifp.inventory_savers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class TransicionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transicion);

        // Recibir la acción de la actividad anterior
        Intent intent = getIntent();
        String action = intent.getStringExtra("ACTION");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Decide a qué actividad ir después de la transición
                Intent nextIntent;
                if ("IR_A_MENU".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, MenuActivity.class);
                } else if ("IR_A_REGISTRO".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, RegistroActivity.class);
                } else if ("IR_A_ALMACENES".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, AlmacenesActivity.class);
                } else if ("IR_A_EVENTOS".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, EventosActivity.class);
                } else if ("IR_A_LOGIN".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, LoginActivity.class);
                } else if ("IR_A_PRODUCTOS".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, ProductosActivity.class);
                } else if ("IR_A_REPORTES".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, ReportesActivity.class);
                } else if ("IR_A_USUARIOS".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, UsuariosActivity.class);
                } else if ("IR_A_LOGINUSUARIOS".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, LoginInternoActivity.class);
                } else if ("IR_A_PRODUCTOMASTER".equals(action)) {
                    nextIntent = new Intent(TransicionActivity.this, ProductosMasterActivity.class);
                } else {
                    // Si no se especifica ninguna acción, ir a MainActivity por defecto
                    nextIntent = new Intent(TransicionActivity.this, LoginActivity.class);
                }
                startActivity(nextIntent);
                finish();
            }
        }, 1500); // 1500 milisegundos = 1.5 segundos
    }
}