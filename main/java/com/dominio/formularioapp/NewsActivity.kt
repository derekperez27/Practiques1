package com.dominio.formularioapp

import android.content.Intent // <<<--- AÑADIDO
import android.os.Bundle
import android.util.Log
import android.view.Menu // <<<--- AÑADIDO
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts // <<<--- AÑADIDO
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dominio.formularioapp.databinding.ActivityNewsBinding
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter

    // ▼▼▼ 1. COPIAMOS EL LAUNCHER QUE FUNCIONA DE MAINACTIVITY ▼▼▼
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Aquest codi s'executa quan tornem de SettingsActivity i recrea l'activitat.
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.news_title)

        setupRecyclerView()

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotBlank()) {
                fetchNews(query)
            } else {
                // He internacionalizado este texto también, para mantener la coherencia
                Toast.makeText(this, getString(R.string.error_search_query_empty), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // La función onResume() ya no es necesaria, porque recreate() es una solución mejor.
    // override fun onResume() { ... }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList())
        binding.newsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@NewsActivity)
        }
    }

    // ▼▼▼ 2. AÑADIMOS EL MENÚ DE AJUSTES (EL ICONO DE LA RUEDA) ▼▼▼
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // ▼▼▼ 3. MODIFICAMOS LA LÓGICA DE LOS BOTONES DEL MENÚ ▼▼▼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Esto gestiona la flecha de "atrás" de la barra superior
            android.R.id.home -> {
                finish()
                true
            }
            // Esto gestiona el clic en el icono de "Ajustes"
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                // Usamos el launcher para que la actividad se recree al volver
                settingsLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchNews(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.newsRecyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.newsApi.searchEverything(
                    query = query,
                    sortBy = "publishedAt",
                    apiKey = BuildConfig.NEWS_API_KEY
                )

                if (response.isSuccessful && response.body() != null) {
                    val articles = response.body()!!.articles
                    newsAdapter.updateArticles(articles)

                    if (articles.isEmpty()) {
                        Toast.makeText(this@NewsActivity, "No se encontraron noticias para '$query'", Toast.LENGTH_SHORT).show()
                    }

                    binding.progressBar.visibility = View.GONE
                    binding.newsRecyclerView.visibility = View.VISIBLE

                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@NewsActivity, "Error en la respuesta: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@NewsActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                Log.e("NewsActivity", "Excepción al obtener noticias", e)
            }
        }
    }
}
