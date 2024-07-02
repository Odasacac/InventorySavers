package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifp.inventory_savers.adaptador.AlmacenAdapter;
import com.ifp.inventory_savers.adaptador.UsuarioAdapter;
import com.ifp.inventory_savers.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;


public class ListaAlmacenes extends AppCompatActivity {

    protected TextView label1;
    protected RecyclerView recycler;
    protected Button boton1;
    private FirebaseFirestore mFirestore;
    private AlmacenAdapter adapter;
    private String correoUserOrg="";
    private String nombre="";

    private List<DocumentSnapshot> almacenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_almacenes);

        label1=(TextView) findViewById(R.id.label1_ListaAlmacenes);
        recycler=(RecyclerView) findViewById(R.id.Recycler_ListaAlmacenes);
        boton1=(Button) findViewById(R.id.boton1_ListaAlmacenes);

        // Instancio base de datos cloud firestone

        mFirestore=FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        correoUserOrg=currentUser.getEmail();
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FragmentManager fm = getSupportFragmentManager();

        /**
         *  --------------- MODIFICADO ---------------
         */
        mFirestore.collection("Almacenes")
                .whereEqualTo("correoUsuarioOrg", correoUserOrg)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            almacenes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // añadimos los datos de documento para el adapter
                                almacenes.add(document);
                            }
                            // Create adapter and set it on RecyclerView
                            if (fm == null) {
                                Log.e(TAG, "FragmentManager es nulo");
                            } else {
                                Log.e(TAG, "FragmentManager NO es nulo");
                                adapter = new AlmacenAdapter(almacenes, fm);
                            }
                            recycler.setAdapter(adapter);
                        }
                    }
                });
        /**
         * ................ ^^^^^^^^^^^^ _____________
         */


        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListaAlmacenes.this,AlmacenesActivity.class));
            }
        });

    }


}