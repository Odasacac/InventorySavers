package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifp.inventory_savers.adaptador.UsuarioAdapter;

import java.util.ArrayList;

public class LoginInternoActivity extends AppCompatActivity {

    protected EditText nombreUsuario;
    protected Button botonAcceder,botonVolver;
    private FirebaseFirestore mFirestore;
    private String correoUserOrg="";
    private String comprobarUsuario="";
    private String nivelPermisos="";
    private Intent extras;
    private String nombreAlmacen="";
    boolean usuarioEncontrado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_interno);

        nombreUsuario=(EditText) findViewById(R.id.editTextUsuario_loginInterno);
        botonAcceder=(Button) findViewById(R.id.buttonAcceder_loginInterno);
        botonVolver=(Button) findViewById(R.id.buttonVolver_loginInterno);
        //Identifico al usuarioOrganizacion
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        correoUserOrg=currentUser.getEmail();
        // Instancio base de datos cloud firestone
        mFirestore=FirebaseFirestore.getInstance();
        // Paso el nombre del almacen seleccionado
        Intent intent = getIntent();
        nombreAlmacen= intent.getStringExtra("nombreAlmacen");
        Log.e(TAG, "Almacen seleccionado al llegar a loginInterno: "+nombreAlmacen);
        botonAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestore.collection("UserDependientes")
                        .whereEqualTo("correoUsuario", correoUserOrg)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null) {
                                        if (!querySnapshot.isEmpty()) {
                                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                comprobarUsuario = document.getString("nombre"); // Obtiene el nombre de usuario de la base de datos
                                                nivelPermisos = document.getString("nivelPermisos");
                                                if (comprobarUsuario.equals(nombreUsuario.getText().toString())) {
                                                    Toast.makeText(getApplicationContext(), "Usuario CORRECTO", Toast.LENGTH_SHORT).show();
                                                    extras= new Intent(LoginInternoActivity.this, PinLoginInternoActivity.class);
                                                    extras.putExtra("nombreAlmacen", nombreAlmacen);
                                                    Log.e(TAG, "Almacen seleccionado al SALIR de loginInterno: "+nombreAlmacen);
                                                    extras.putExtra("userName", comprobarUsuario); // Pasoamos el nombre del usuario para mostrarlo en PIN_titulo
                                                    extras.putExtra("nivelPermisos", nivelPermisos);// Pasamos el ID del DOCUMENTO para usarlo en las siguientes clases.
                                                    startActivity(extras);
                                                    usuarioEncontrado = true; // Se encontró un usuario
                                                    break; // Salir del bucle for
                                                }
                                            }
                                            if (!usuarioEncontrado) { // No se encontró un usuario
                                                Toast.makeText(getApplicationContext(), "Usuario no registrado", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Usuario no registrado", Toast.LENGTH_SHORT).show();
                                        }
                                        }
                                    else {
                                            //Toast.makeText(getApplicationContext(), "Campo usuario obligatorio...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                else {
                                        // Se produjo un error al consultar la base de datos
                                        // Maneja el error de forma adecuada
                                }

                            }
                        });
            }
        });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginInternoActivity.this,MenuActivity.class));
            }
        });
    }
}