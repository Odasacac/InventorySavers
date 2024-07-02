package com.ifp.inventory_savers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    protected Button buttonAcceder;
    protected Button buttonRegistro;
    protected EditText editTextUser;
    protected EditText editTextPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String title = getResources().getString(R.string.inventrory_savers);
        setTitle(title);

        mAuth = FirebaseAuth.getInstance();
        editTextUser = (EditText) findViewById(R.id.editTextTextUser_auth);
        editTextPass = (EditText) findViewById(R.id.editTextTextPassword_auth);
        buttonAcceder = (Button) findViewById(R.id.buttonAcceso_auth);
        buttonRegistro = (Button) findViewById(R.id.buttonRegister_auth);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
        }

        buttonAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextUser.getText().toString();
                String password = editTextPass.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Ingrese su correo electrónico y contraseña.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Iniciar TransicionActivity con un extra para ir a MenuActivity
                            Intent intent = new Intent(LoginActivity.this, TransicionActivity.class);
                            intent.putExtra("ACTION", "IR_A_MENU");
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Autenticación fallida. Intente nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
                finish();
            }
        });
    }
}