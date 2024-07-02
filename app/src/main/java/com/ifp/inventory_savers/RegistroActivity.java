package com.ifp.inventory_savers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase representa la actividad de registro de usuarios en la aplicación.
 * Permite a los usuarios crear una cuenta proporcionando su correo electrónico, contraseña y nombre de usuario.
 */
public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    protected EditText editextMail, editextPass,editextConfirmaPass, editextUser;
    protected Button button1_registro,botonVolver;
    private Context mContext;
    /**
     * Método que se llama cuando la actividad se está iniciando.
     * Se encarga de inicializar los componentes de la interfaz de usuario y configurar los listeners de los botones.
     * @param savedInstanceState Instancia previamente guardada del estado de esta actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        String title = getResources().getString(R.string.inventrory_savers);
        setTitle(title);

        mAuth = FirebaseAuth.getInstance();
        mFirestore=FirebaseFirestore.getInstance();

        editextMail = findViewById(R.id.editTextMail_registro);
        editextPass = findViewById(R.id.editTextPassword_registro);
        editextConfirmaPass= findViewById(R.id.editTextConfirmaPass_registro);
        editextUser = findViewById(R.id.editTextUser_registro);
        button1_registro = findViewById(R.id.button1_registro);
        botonVolver = findViewById(R.id.buttonVolver_registro);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
        }
        button1_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editextMail.getText().toString();
                final String password = editextPass.getText().toString();
                final String confirmPassword= editextConfirmaPass.getText().toString();
                final String user = editextUser.getText().toString();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || user.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(RegistroActivity.this, "Formato de correo electrónico inválido.", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                }  else {
                    guardarUser(email, user);
                    registerUser(email, password, user);
                }
            }
        });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this,LoginActivity.class));
            }
        });
    }
    /**
     * Método privado para verificar si el formato del correo electrónico es válido.
     * @param target Correo electrónico a verificar.
     * @return true si el correo electrónico es válido, false de lo contrario.
     */
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    /**
     * Método privado para registrar un nuevo usuario en Firebase Authentication.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param user Nombre de usuario.
     */
    private void registerUser(String email, String password, String user) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Usuario registrado con éxito
                        Toast.makeText(RegistroActivity.this, "Usuario registrado con éxito.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity.this, TransicionActivity.class);
                        intent.putExtra("ACTION", "IR_A_LOGIN");
                        startActivity(intent);
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            // El correo electrónico ya está en uso
                            Toast.makeText(RegistroActivity.this, "El correo electrónico ya está en uso.", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            // No hay conexión a Internet
                            Toast.makeText(RegistroActivity.this,
                                    "No hay conexión a Internet. Por favor, verifica tu conexión e intenta nuevamente.", Toast.LENGTH_SHORT).show();
                        }else {
                            // Manejar otros errores
                            Toast.makeText(RegistroActivity.this, "Error al crear el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /**
     * Método privado para guardarlos datos de un nuevo usuario en Firebase Authentication.
     * @param userEmail Correo electrónico del usuario.
     * @param uName Nombre del usuario.
     */
    private void guardarUser(String userEmail, String uName) {
        // Guardar información adicional en Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", uName);
        userData.put("email", userEmail);
        mFirestore.collection("UserOrganizacion").add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Datos guardados exitosamente en Firestone
                        Toast.makeText(RegistroActivity.this, "Registro exitoso en la base de datos",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error al guardar datos
                        Toast.makeText(RegistroActivity.this, "Error al guardar datos en Firestore",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
   /*
    Estos metodos se han implementado para poder realizar test de jUnit
    */
    /**
     * Método público utilizado en pruebas de unidad para registrar un usuario.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param usuario Nombre de usuario.
     */
    public void registerUserPublic(String email, String password, String usuario) {
        registerUser(email, password, usuario);
    }
    /**
     * Método público utilizado en pruebas de unidad para establecer la instancia de FirebaseAuth.
     * @param mAuth Instancia de FirebaseAuth.
     */
    public void setFirebaseAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }
    /**
     * Método público utilizado en pruebas de unidad para establecer el contexto.
     * @param context Contexto de la aplicación.
     */
    public void setContext(Context context) {
        mContext = context;
    }

}