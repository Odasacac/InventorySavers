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
import com.ifp.inventory_savers.adaptador.UsuarioAdapter;
import com.ifp.inventory_savers.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuarios extends AppCompatActivity {

    protected TextView titulo;
    protected RecyclerView listaUsers;

    protected Button botonVolver;
    private FirebaseFirestore mFirestore;
    private UsuarioAdapter adapter;
    private String correoUserOrg="";
    private List<DocumentSnapshot> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        titulo=(TextView) findViewById(R.id.textViewTitulo_listaUsuarios);
        listaUsers=(RecyclerView) findViewById(R.id.recyclerViewLista_listaUsuarios);
        botonVolver=(Button) findViewById(R.id.buttonVolver_listaUsuarios);
        // Instancio base de datos cloud firestone
        mFirestore=FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        correoUserOrg=currentUser.getEmail();
        listaUsers.setLayoutManager(new LinearLayoutManager(this));

        FragmentManager fm = getSupportFragmentManager();

        mFirestore.collection("UserDependientes")
                .whereEqualTo("correoUsuario", correoUserOrg)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usuarios = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // añadimos los datos de documento para el adapter
                                usuarios.add(document);
                            }
                            // Create adapter and set it on RecyclerView
                            if (fm == null) {
                                Log.e(TAG, "FragmentManager es nulo");
                            } else {
                                adapter = new UsuarioAdapter(usuarios, fm);
                            }
                            listaUsers.setAdapter(adapter);
                        }
                    }
                });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListaUsuarios.this,UsuariosActivity.class));
            }
        });
    }
}