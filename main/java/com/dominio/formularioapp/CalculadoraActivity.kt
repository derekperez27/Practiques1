package com.dominio.formularioapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var editTextNumero1: EditText
    private lateinit var editTextNumero2: EditText
    private lateinit var textResultat: TextView


    private lateinit var botoSumar: Button
    private lateinit var botoRestar: Button
    private lateinit var botoMultiplicar: Button
    private lateinit var botoDividir: Button

    // Variable para guardar el idioma en el momento de la carga
    private var idiomaActualAlCarregar: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Guardamos el idioma actual al crear la actividad
        idiomaActualAlCarregar = AppCompatDelegate.getApplicationLocales().toLanguageTags()

        // Inicialización de las vistas
        editTextNumero1 = findViewById(R.id.editTextNumero1)
        editTextNumero2 = findViewById(R.id.editTextNumero2)
        textResultat = findViewById(R.id.textResultat)

        botoSumar = findViewById(R.id.botoSumar)
        botoRestar = findViewById(R.id.botoRestar)
        botoMultiplicar = findViewById(R.id.botoMultiplicar)
        botoDividir = findViewById(R.id.botoDividir)

        // Listeners para cada botón
        botoSumar.setOnClickListener {
            calcular(Operacion.SUMA)
        }
        botoRestar.setOnClickListener {
            calcular(Operacion.RESTA)
        }
        botoMultiplicar.setOnClickListener {
            calcular(Operacion.MULTIPLICACION)
        }
        botoDividir.setOnClickListener {
            calcular(Operacion.DIVISION)
        }
    }

    // Este método se ejecuta cuando la actividad vuelve a ser visible
    override fun onStart() {
        super.onStart()
        // Obtenemos el idioma actual de la aplicación
        val idiomaActualDelSistema = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        // Si el idioma actual es diferente al que había cuando se cargó la actividad...
        if (idiomaActualAlCarregar != idiomaActualDelSistema) {
            recreate() // ...la recreamos para que cargue los nuevos textos.
        }
    }

    // Definimos un enum para saber qué operación realizar
    private enum class Operacion {
        SUMA, RESTA, MULTIPLICACION, DIVISION
    }

    private fun calcular(operacion: Operacion) {
        val num1Str = editTextNumero1.text.toString()
        val num2Str = editTextNumero2.text.toString()

        if (num1Str.isEmpty() || num2Str.isEmpty()) {
            // CORREGIDO: Usamos el recurso de string que ya tienes
            Toast.makeText(this, getString(R.string.error_fill_both_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val num1 = num1Str.toDoubleOrNull()
        val num2 = num2Str.toDoubleOrNull()

        if (num1 == null || num2 == null) {
            // CORREGIDO: Usamos el recurso de string que ya tienes
            Toast.makeText(this, getString(R.string.error_invalid_numbers), Toast.LENGTH_SHORT).show()
            return
        }

        // Control para evitar la división por cero
        if (operacion == Operacion.DIVISION && num2 == 0.0) {
            // CORREGIDO: Usamos el recurso de string que ya tienes
            Toast.makeText(this, getString(R.string.error_divide_by_zero), Toast.LENGTH_SHORT).show()
            return
        }

        val resultat = when (operacion) {
            Operacion.SUMA -> num1 + num2
            Operacion.RESTA -> num1 - num2
            Operacion.MULTIPLICACION -> num1 * num2
            Operacion.DIVISION -> num1 / num2
        }

        // CORREGIDO: Usamos el recurso de string que ya tienes para que "Resultado:" se traduzca
        textResultat.text = "${getString(R.string.result_label)} $resultat"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
