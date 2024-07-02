package com.ifp.inventory_savers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CrearUsuarios extends AppCompatActivity {
    protected TextView titulo, putUsuario, putPermisos;
    protected EditText nombreUsuario;
    protected Button botonCrear, botonVolver;
    protected Spinner listaPermisos;
    private ArrayAdapter<String> adapter;
    private String[] nivelPermisos = {"Seleccione tipo de trabajador...", "Empleado", "Encargado","Gerente"};
    private String nivelSeleccionado = "";
    private String nombreUsuarioDependiente = "";
    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuarios);

        titulo = (TextView) findViewById(R.id.textViewTitulo_crearUsuaario);
        putUsuario = (TextView) findViewById(R.id.textViewPutNombre_crearUsuaario);
        putPermisos = (TextView) findViewById(R.id.textViewPermisos_crearUsuaario);
        nombreUsuario = (EditText) findViewById(R.id.editTextNombreUser_crearUsuaario);
        botonCrear = (Button) findViewById(R.id.buttonCrear_crearUsuaario);
        botonVolver = (Button) findViewById(R.id.buttonVolver_crearUsuaario);
        listaPermisos = (Spinner) findViewById(R.id.spinnerPermisos_crearUsuaario);
        // Instancio base de datos cloud firestone
        mFirestore=FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nivelPermisos);
        listaPermisos.setAdapter(adapter);
        //recupero ID de usuario para actualizarr
        String id=getIntent().getStringExtra("id_usuario");
            botonCrear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nombreUsuarioDependiente = nombreUsuario.getText().toString();
                    if (nombreUsuarioDependiente.isEmpty()) {
                        Toast.makeText(CrearUsuarios.this, "Campo nombre obligatorio.", Toast.LENGTH_SHORT).show();
                    } else if (nivelSeleccionado.equals("Seleccione tipo de trabajador...")) {
                        Toast.makeText(CrearUsuarios.this, "Seleccione una opción de la lista.", Toast.LENGTH_SHORT).show();
                    } else {
                        comprobarUsuarioExistente(nombreUsuarioDependiente);
                    }
                }
            });

        listaPermisos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nivelSeleccionado = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CrearUsuarios.this, UsuariosActivity.class));
            }
        });
    }
    /**
     * Método que crea un nuevo usuario en la colección "UserDependientes" de Firestore.
     *
     * @param nombreUsuario El nombre del usuario que se desea crear.
     * @param nivelSeleccionado El nivel de permisos seleccionado para el usuario.
     */
    private void postUser(String nombreUsuario, String nivelSeleccionado) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", nombreUsuario);
            map.put("nivelPermisos", nivelSeleccionado);
            map.put("correoUsuario", userEmail);
            mFirestore.collection("UserDependientes").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(CrearUsuarios.this, "Usuario creado correctamente.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CrearUsuarios.this, UsuariosActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CrearUsuarios.this, "Error al crear el usuario.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // El usuario no está autenticado
            Toast.makeText(CrearUsuarios.this, "No se pudo obtener el usuario autenticado.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Método que verifica si ya existe un usuario con el mismo nombre en la colección "UserDependientes" de Firestore.
     * Si no existe, procede a crear el nuevo usuario.
     *
     * @param nombreUsuario El nombre del usuario que se desea verificar.
     */
    private void comprobarUsuarioExistente(final String nombreUsuario) {
        String correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirestore.collection("UserDependientes").whereEqualTo("correoUsuarioOrg",correoUsuario)
                .whereEqualTo("nombre", nombreUsuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            // Ya existe un usuario con el mismo nombre
                            Toast.makeText(CrearUsuarios.this, "El nombre de usuario ya existe.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Continuar con la creación del usuario
                            postUser(nombreUsuario, nivelSeleccionado);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CrearUsuarios.this, "Error al verificar usuarios existentes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}