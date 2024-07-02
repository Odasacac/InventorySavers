package com.ifp.inventory_savers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CrearAlmacen extends AppCompatActivity {

    protected TextView titulo;
    protected EditText nombreAlmacen;
    protected Button botonCrear;
    protected Button botonVolver;
    private String nomAlmacen;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private boolean almacenExistente = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_almacen);

        titulo = findViewById(R.id.textViewTitulo_CrearAlmacen);
        nombreAlmacen = findViewById(R.id.editTextNombreAlmacen_crearAlmacen);
        botonCrear = findViewById(R.id.buttonCrear_crearAlmacen);
        botonVolver = findViewById(R.id.buttonVolver_crearAlmacen);
        // Instancio base de datos cloud Firestore
        mFirestore = FirebaseFirestore.getInstance();

        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nomAlmacen = nombreAlmacen.getText().toString();
                String correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                almacenExistente = false;
                if (nomAlmacen.isEmpty()) {
                    Toast.makeText(CrearAlmacen.this, "Campo nombre obligatorio.", Toast.LENGTH_SHORT).show();
                } else {
                    // Comprobar si nomAlmacen ya existe en la base de datos
                    FirebaseFirestore.getInstance().collection("Almacenes") .whereEqualTo("correoUsuarioOrg",correoUsuario)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String nombreAlmacenBD = document.getString("nombreAlmacen");
                                            if (nomAlmacen.equals(nombreAlmacenBD)) {
                                                almacenExistente = true;
                                                break;
                                            }
                                        }

                                        if (!almacenExistente) { // Si no existe el almacén
                                            postAlmacen(nomAlmacen);
                                        } else {
                                            Toast.makeText(CrearAlmacen.this, "Almacén ya existente", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CrearAlmacen.this, MenuActivity.class));
            }

        });
    }

    private void postAlmacen(String nomAlmacen) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            Map<String, Object> map = new HashMap<>();
            /**
             *  --------------- MODIFICADO ---------------
             */
            map.put("nombreAlmacen", nomAlmacen);
            map.put("correoUsuarioOrg", userEmail);
            /**
             * ................ ^^^^^^^^^^^^ _____________
             */
            mFirestore.collection("Almacenes").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CrearAlmacen.this, "Almacén creado correctamente.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CrearAlmacen.this, AlmacenesActivity.class));
                    } else {
                        Toast.makeText(CrearAlmacen.this, "Error al crear el almacén.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // El usuario no está autenticado
            Toast.makeText(CrearAlmacen.this, "No se pudo obtener el usuario autenticado.", Toast.LENGTH_SHORT).show();
        }
    }
}