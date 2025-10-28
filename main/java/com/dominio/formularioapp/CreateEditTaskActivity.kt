package com.dominio.formularioapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CreateEditTaskActivity : AppCompatActivity() {

    // --- Vistas de la UI ---
    private lateinit var editTextTitol: TextInputEditText
    private lateinit var editTextDescripcio: TextInputEditText
    private lateinit var radioGroupTipo: RadioGroup // <-- NUEVO: Referencia al RadioGroup
    private lateinit var buttonGuardar: Button

    // --- Estado de la Activity ---
    private var modeEdicio = false
    private var tascaId: Int = -1
    // Variable para guardar el estado 'completada' al editar, por defecto false
    private var tascaCompletada: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_edit_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // --- Inicialización de vistas ---
        editTextTitol = findViewById(R.id.editTextTitol)
        editTextDescripcio = findViewById(R.id.editTextDescripcio)
        radioGroupTipo = findViewById(R.id.radioGroupTipo) // <-- NUEVO: Inicializamos el RadioGroup
        buttonGuardar = findViewById(R.id.buttonGuardarTasca)

        // --- Comprobación de modo (Crear o Editar) ---
        if (intent.hasExtra("EXTRA_TASK_ID")) {
            modeEdicio = true
            tascaId = intent.getIntExtra("EXTRA_TASK_ID", -1)
            tascaCompletada = intent.getBooleanExtra("EXTRA_TASK_COMPLETED", false) // Recuperamos el estado 'completada'

            editTextTitol.setText(intent.getStringExtra("EXTRA_TASK_TITLE"))
            editTextDescripcio.setText(intent.getStringExtra("EXTRA_TASK_DESC"))

            // --- NUEVO: Seleccionamos el RadioButton correcto ---
            val tipusTasca = intent.getStringExtra("EXTRA_TASK_TYPE")
            if (tipusTasca == "NOTA") {
                radioGroupTipo.check(R.id.radioButtonNota)
            } else {
                radioGroupTipo.check(R.id.radioButtonTasca)
            }

            supportActionBar?.title = "Editar Element"
        } else {
            modeEdicio = false
            supportActionBar?.title = "Crear Element Nou"
        }

        buttonGuardar.setOnClickListener {
            guardarTasca()
        }
    }

    private fun guardarTasca() {
        val titol = editTextTitol.text.toString().trim()
        val descripcio = editTextDescripcio.text.toString().trim()

        if (titol.isEmpty()) {
            Toast.makeText(this, "El títol no pot estar buit", Toast.LENGTH_SHORT).show()
            return
        }

        // --- NUEVO: Determinamos el tipo seleccionado ---
        val tipusSeleccionatId = radioGroupTipo.checkedRadioButtonId
        val tipus = if (tipusSeleccionatId == R.id.radioButtonNota) "NOTA" else "TASCA"

        if (modeEdicio) {
            // --- LÓGICA DE ACTUALIZACIÓN ---
            // Creamos el objeto Tasca con el nuevo campo 'tipo' y mantenemos su estado 'completada'
            val tascaActualitzada = Tasca(id = tascaId, titol = titol, descripcio = descripcio, completada = tascaCompletada, tipo = tipus)
            actualitzarTascaExistent(tascaActualitzada)
        } else {
            // --- LÓGICA DE CREACIÓN ---
            // Creamos el objeto Tasca con el nuevo campo 'tipo'
            val novaTasca = Tasca(id = 0, titol = titol, descripcio = descripcio, completada = false, tipo = tipus)
            crearNovaTasca(novaTasca)
        }
    }

    private fun crearNovaTasca(tasca: Tasca) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.tasquesApi.crearTasca(tasca)
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateEditTaskActivity, "Element guardat amb èxit!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateEditTaskActivity", "Error del servidor al crear: ${response.code()} - $errorBody")
                    Toast.makeText(this@CreateEditTaskActivity, "Error al crear l'element: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("CreateEditTaskActivity", "Excepció al crear l'element", e)
                Toast.makeText(this@CreateEditTaskActivity, "Error de connexió: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun actualitzarTascaExistent(tasca: Tasca) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.tasquesApi.actualitzarTasca(tasca)
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateEditTaskActivity, "Element actualitzat correctament!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateEditTaskActivity", "Error del servidor al actualizar: ${response.code()} - $errorBody")
                    Toast.makeText(this@CreateEditTaskActivity, "Error al actualizar l'element: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("CreateEditTaskActivity", "Excepció al actualizar l'element", e)
                Toast.makeText(this@CreateEditTaskActivity, "Error de connexió: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
