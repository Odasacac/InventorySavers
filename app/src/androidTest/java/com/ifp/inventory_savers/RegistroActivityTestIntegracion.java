package com.ifp.inventory_savers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static java.util.regex.Pattern.matches;

import android.view.View;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
public class RegistroActivityTestIntegracion {

        @Rule
        public ActivityScenarioRule<RegistroActivity> activityRule =
                new ActivityScenarioRule<>(RegistroActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
        @Test
        public void registroUsuarioExitoso() {
            // Simula la entrada de datos del usuario
            onView(withId(R.id.editTextUser_registro)).perform(typeText("usuario"), closeSoftKeyboard());
            onView(withId(R.id.editTextMail_registro)).perform(typeText("correo@dementira3.com"), closeSoftKeyboard());
            onView(withId(R.id.editTextPassword_registro)).perform(typeText("123456"), closeSoftKeyboard());
            onView(withId(R.id.editTextConfirmaPass_registro)).perform(typeText("123456"), closeSoftKeyboard());


            // Simula el clic en el botón de registro
            onView(withId(R.id.button1_registro)).perform(click());

            // Agrega un tiempo de espera
            try {
                Thread.sleep(2000); // espera 2 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Verifica que se haya redirigido a la actividad de transición
            intended(hasComponent(TransicionActivity.class.getName()));
        }

    @Test
    public void pulsarBotonVolverIniciaLoginActivity() {
        // Simula el clic en el botón de volver
        onView(withId(R.id.buttonVolver_registro)).perform(click());

        // Verifica que se haya iniciado LoginActivity
        intended(hasComponent(LoginActivity.class.getName()));
    }
}

