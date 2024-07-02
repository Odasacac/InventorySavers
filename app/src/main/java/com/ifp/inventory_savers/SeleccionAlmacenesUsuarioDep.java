package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SeleccionAlmacenesUsuarioDep extends AppCompatActivity {

    protected TextView titulo,seleccionSpinner;
    protected Spinner spinnerAlmacenes;
    protected Button botonAceptar,botonVolver;
    private FirebaseFirestore mFirestore;
    private String correoUserOrg="";
    private Intent extras;
    private String nombreAlmacenSeleccionado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_almacenes_usuario_dep);

        titulo=findViewById(R.id.textViewTitulo_seleccionAlmacen);
        seleccionSpinner=findViewById(R.id.textViewSpinner_seleccionAlmacen);
        spinnerAlmacenes=findViewById(R.id.spinnerAlmacenes_seleccionAlmacen);
        botonAceptar=findViewById(R.id.buttonAceptar_seleccionAlmacen);
        botonVolver=findViewById(R.id.buttonVolver_seleccionAlmacen);

        // Instancio base de datos cloud firestone
        mFirestore=FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        correoUserOrg=currentUser.getEmail();

        // Obtener nombres de almacenes de Firestore y configurar en el Spinner
        obtenerNombresAlmacenesFirestore();

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreAlmacenSeleccionado=spinnerAlmacenes.getSelectedItem().toString();
                Log.e(TAG, "Almacen seleccionado: "+nombreAlmacenSeleccionado);
                extras= new Intent(SeleccionAlmacenesUsuarioDep.this, LoginInternoActivity.class);
                extras.putExtra("nombreAlmacen", nombreAlmacenSeleccionado); // Pasoamos el nombre del usuario para mostrarlo en PIN_titulo
                startActivity(extras);

            }
        });

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SeleccionAlmacenesUsuarioDep.this, MenuActivity.class));
            }
        });
    }
    /**
     *  --------------- MODIFICADO ---------------
     */
    private void obtenerNombresAlmacenesFirestore() {
        // Consulta a Firestore para obtener nombres de almacenes
        mFirestore.collection("Almacenes").whereEqualTo("correoUsuarioOrg", correoUserOrg)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> nombresAlmacenes = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Obtener nombre del almacén y agregarlo a la lista
                            String nombreAlmacen = documentSnapshot.getString("nombreAlmacen");
                            nombresAlmacenes.add(nombreAlmacen);
                        }
                        // Configurar adaptador para el Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SeleccionAlmacenesUsuarioDep.this, android.R.layout.simple_spinner_item, nombresAlmacenes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAlmacenes.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error
                    }
                });
    }
}