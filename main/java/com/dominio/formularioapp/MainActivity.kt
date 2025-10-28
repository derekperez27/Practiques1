package com.dominio.formularioapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

// ▼▼▼ LA IMPORTACIÓN VA AQUÍ, LIMPIA Y SIN TEXTO ADICIONAL ▼▼▼
import com.dominio.formularioapp.TasquesActivity

class MainActivity : AppCompatActivity() {

    private lateinit var botoAnarARegistre: Button
    private lateinit var botoVeureLlista: Button
    private lateinit var botoAnarACalculadora: Button
    private lateinit var botoAnarANoticies: Button
    private lateinit var botoAnarATasques: Button

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botoAnarARegistre = findViewById(R.id.botoAnarARegistre)
        botoVeureLlista = findViewById(R.id.botoVeureLlista)
        botoAnarACalculadora = findViewById(R.id.botoAnarACalculadora)
        botoAnarANoticies = findViewById(R.id.botoAnarANoticies)
        botoAnarATasques = findViewById(R.id.botoAnarATasques)

        botoAnarACalculadora.setOnClickListener {
            val intent = Intent(this, CalculadoraActivity::class.java)
            startActivity(intent)
        }
        botoAnarARegistre.setOnClickListener {
            val intent = Intent(this, RegitroActivity::class.java)
            startActivity(intent)
        }
        botoVeureLlista.setOnClickListener {
            val intent = Intent(this, LlistaUsuarisActivity::class.java)
            startActivity(intent)
        }
        botoAnarANoticies.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        botoAnarATasques.setOnClickListener {
            // Ahora, con la importación correcta, esta línea ya no dará error.
            val intent = Intent(this, TasquesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                settingsLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
