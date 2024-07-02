package com.ifp.inventory_savers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Build;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class RegistroActivityTestUnitarios {

    @Mock
    private FirebaseAuth mAuth;
    @Mock
    private Context mContext;

    private RegistroActivity registroActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registroActivity = spy(new RegistroActivity());
        registroActivity.setFirebaseAuth(mAuth);
        registroActivity.setContext(mContext);
        doNothing().when(registroActivity).finish();

    }
    @Test
    public void registerUser_exito_test() {
        String email = "test@ejemplo.com";
        String password = "password123";
        String usuario = "Usuario";
        TaskCompletionSource<AuthResult> taskCompletionSource = new TaskCompletionSource<>();

        // Simular creación de usuario exitosa
        when(mAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(taskCompletionSource.getTask()); // Usa taskCompletionSource.getTask()
        // Ejecutar el método
        registroActivity.registerUserPublic(email, password, usuario);

        // Verificar que createUserWithEmailAndPassword se llama con los argumentos correctos
        verify(mAuth).createUserWithEmailAndPassword(email, password);
    }
    @Test
    public void registerUser_fallo_test() {
        String email = "test@ejemplo.com";
        String password = "password123";
        String usuario = "Usuario";

        // Simular fallo en la creación de usuario con una excepción
        Exception exception = new Exception("Error de autenticación");
        when(mAuth.createUserWithEmailAndPassword(email, password))
                .thenReturn(Tasks.forException(exception)); // Usa Tasks.forException

        // Ejecutar el método
        registroActivity.registerUserPublic(email, password, usuario);

        // Verificar que createUserWithEmailAndPassword se llama con los argumentos correctos
        verify(mAuth).createUserWithEmailAndPassword(email, password);

        // Verificar que la actividad no se finaliza
        verify(registroActivity, times(0)).finish();
    }
    @Test
    public void registerUser_fallo_passwordDebil_test() {
        String email = "test@ejemplo.com";
        String password = "";
        String usuario = "Usuario";
        // Crear una tarea fallida con la excepcion de conmtraseña corta( minimo deve tener 6 caracteres)
        Task<AuthResult> failedTask = Tasks.forException(new FirebaseAuthWeakPasswordException
                ("ERROR_WEAK_PASSWORD", "The given password is invalid. [ Password should be at least 6 characters ]", "The password is too weak"));

        // Simular fallo de creación de usuario debido a contraseña débil
        when(mAuth.createUserWithEmailAndPassword(email, password)).thenReturn(failedTask);

        // Ejecutar el método
        registroActivity.registerUserPublic(email, password, usuario);

        // Verificar que createUserWithEmailAndPassword se llama con los argumentos correctos
        verify(mAuth).createUserWithEmailAndPassword(email, password);
    }
    @Test
    public void registerUser_fallo_emailIncorrecto_test() {
        String email = "test";
        String password = "password";
        String usuario = "Usuario";

        // Crear una tarea fallida con la excepcion de formato de correo incorrecto
        Task<AuthResult> failedTask = Tasks.forException(new FirebaseAuthInvalidCredentialsException
                ("ERROR_INVALID_EMAIL", "The email address is badly formatted."));

        // Simular fallo de creación de usuario debido a formato de correo electrónico incorrecto
        when(mAuth.createUserWithEmailAndPassword(email, password)).thenReturn(failedTask);

        // Ejecutar el método
        registroActivity.registerUserPublic(email, password, usuario);

        // Verificar que createUserWithEmailAndPassword se llama con los argumentos correctos
        verify(mAuth).createUserWithEmailAndPassword(email, password);
    }
}