package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PinLoginInternoActivity extends AppCompatActivity {
    protected TextView titulo;
    protected EditText pin, confirmacionPin;
    protected Button botonAceptar,botonVolver;
    private String pinIngresado="";
    private String pinConfirmacion="";
    private Intent extras;
    private String nombreUsuario="";
    private String nombreAlmacenSeleccionado="";
    private String nivelPermisos="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_login_interno);

        titulo= findViewById(R.id.textViewTitulo_pinUsuario);
        pin= findViewById(R.id.editTextNumberPassword_pinUsuario);
        confirmacionPin=findViewById(R.id.editTextNumberPasswordConfirmar_pinUsuario);
        botonAceptar= findViewById(R.id.buttonAceptar_pinUsuario);
        botonVolver= findViewById(R.id.buttonVolver_pinUsuario);

        nombreAlmacenSeleccionado=getIntent().getStringExtra("nombreAlmacen");
        nombreUsuario = getIntent().getStringExtra("userName");
        nivelPermisos= getIntent().getStringExtra("nivelPermisos");
        Log.d(TAG, "Soy el usuario: "+nombreUsuario);
        Log.d(TAG, "Soy el nivelPermisos: "+nivelPermisos);
        Log.e(TAG, "Almacen seleccionado al llegar a PIN: "+nombreAlmacenSeleccionado);

        titulo.setText("Bienvenido(a), " + nombreUsuario);

        extras= new Intent(PinLoginInternoActivity.this, ProductosActivity.class);
        extras.putExtra("nombreAlmacen", nombreAlmacenSeleccionado);
        extras.putExtra("userName", nombreUsuario); // Pasoamos el nombre del usuario para mostrarlo en PIN_titulo
        extras.putExtra("nivelPermisos", nivelPermisos); // Pasoamos el nombre del usuario para mostrarlo en PIN_titulo

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (botonAceptar.getText().equals("ACEPTAR")) { // Si el botón dice "Guardar"
                    pinIngresado = pin.getText().toString();
                    if (!pinIngresado.isEmpty()) {
                        // Mostrar el campo de confirmación
                        pin.setVisibility(View.INVISIBLE);
                        confirmacionPin.setVisibility(View.VISIBLE);
                        // Cambiar el texto del botón a "Confirmar"
                        botonAceptar.setText("CONFIRMAR");
                        botonAceptar.setBackgroundColor(ContextCompat.getColor(PinLoginInternoActivity.this, R.color.verde_boton_confirmar));
                    } else {
                        Toast.makeText(PinLoginInternoActivity.this, "Campo PIN obligatorio...", Toast.LENGTH_SHORT).show();
                    }
                } else if (botonAceptar.getText().equals("CONFIRMAR")) { // Si el botón dice "Confirmar"
                    pinConfirmacion = confirmacionPin.getText().toString();
                    if (!pinConfirmacion.isEmpty()) {
                    if (pinConfirmacion.equals(pinIngresado)) {
                        Toast.makeText(PinLoginInternoActivity.this, "¡PIN CONFIRMADO!", Toast.LENGTH_SHORT).show();
                        startActivity(extras);
                        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("PIN", pinIngresado);
                        editor.putBoolean("primeraVez", true);// Este es un boolean para comprovar si inicia por primera vez
                        // la app con este user. lo tengo que poner aqui por si cierra sesión en productos, modificar este valor para
                        // qeu llegre a la activity del PIN en true.
                        editor.apply();
                        Log.d(TAG, "PIN guardado: " + pinIngresado);
                    } else {
                        Toast.makeText(PinLoginInternoActivity.this, "El PIN ingresado no coincide...", Toast.LENGTH_SHORT).show();
                        botonAceptar.setText("ACEPTAR");
                        botonAceptar.setBackgroundColor(ContextCompat.getColor(PinLoginInternoActivity.this, R.color.redi));
                        pin.setVisibility(View.VISIBLE);
                        pin.setText("");
                        confirmacionPin.setVisibility(View.INVISIBLE);
                        confirmacionPin.setText("");
                    }
                    }else{
                        Toast.makeText(PinLoginInternoActivity.this, "Campo PIN obligatorio...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PinLoginInternoActivity.this,LoginInternoActivity.class));
            }
        });
    }
}