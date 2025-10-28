package com.example.holamundo2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dominio.formularioapp.MainActivity;
import com.dominio.formularioapp.R;
import com.dominio.formularioapp.RegitroActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Pruebas de instrumentación para la UI, navegación y funcionalidades.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    // --- PRUEBAS QUE EMPIEZAN DESDE MainActivity ---

    // Esta regla lanza MainActivity antes de CADA prueba en esta clase.
    // Para probar RegitroActivity de forma aislada, es mejor crear otra clase de prueba.
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Prueba #5: Verifica que el texto "Hola Mundo!" y la imagen son visibles en la pantalla principal.
     */
    @Test
    public void prueba_HolaMundoYContenidoPrincipalSonVisibles() {

        onView(withId(R.id.textHolaMundo))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.imagePulgar))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    /**
     * Prueba #2: Verifica que la navegación a la pantalla de registro funciona.
     */
    @Test
    public void prueba_NavegacionARegistroActivity() {
        // Hacemos scroll hasta el botón y hacemos clic.
        onView(withId(R.id.botoAnarARegistre)).perform(scrollTo(), click());

        // Verificamos que la nueva pantalla se ha cargado comprobando su título.
        onView(withId(R.id.titulo_formulario)).check(matches(withText("Formulari de Registre")));
    }

    /**
     * Prueba #3: Verifica que la funcionalidad de la calculadora es correcta.
     */
    @Test
    public void prueba_FuncionalidadCalculadora() {
        // Rellenamos los campos de la calculadora.
        onView(withId(R.id.editTextNumero1)).perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.editTextNumero2)).perform(typeText("5"), closeSoftKeyboard());

        // Hacemos clic en el botón de calcular.
        onView(withId(R.id.botoCalcular)).perform(click());

        // Verificamos que el resultado mostrado es el correcto.
        onView(withId(R.id.textResultat)).check(matches(withText("Resultat: 15.0")));
    }
}
