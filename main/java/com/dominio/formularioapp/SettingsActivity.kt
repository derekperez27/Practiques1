package com.dominio.formularioapp


import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dominio.formularioapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var isProgrammaticChange = false
    // ▼▼▼ ESBORREM LA BANDERA "languageChanged" ▼▼▼
    // private var languageChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.settings_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupDarkMode()
        setupLanguage()
    }

    private fun setupDarkMode() {
        binding.switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    // A DINS DE SettingsActivity.kt

    private fun setupLanguage() {
        updateLanguageSelection()

        binding.radioGroupLanguages.setOnCheckedChangeListener { _, checkedId ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener

            val langTag = when (checkedId) {
                R.id.radioCatalan -> "ca"
                R.id.radioSpanish -> "es"
                R.id.radioEnglish -> "en"
                R.id.radioItalian -> "it"
                else -> return@setOnCheckedChangeListener
            }

            val appLocale = LocaleListCompat.forLanguageTags(langTag)
            AppCompatDelegate.setApplicationLocales(appLocale)

            // ▼▼▼ CANVI MOLT IMPORTANT ▼▼▼
            // En lloc de recrear-se a si mateixa, ara simplement es tanca.
            // El "launcher" de la MainActivity s'encarregarà de la resta.
            // ESBORREM: this.recreate()
            finish() // AFEGIM AIXÒ
        }
    }


    private fun updateLanguageSelection() {
        // Aquesta funció es queda exactament igual
        isProgrammaticChange = true
        binding.radioCatalan.text = getString(R.string.language_catalan)
        binding.radioSpanish.text = getString(R.string.language_spanish)
        binding.radioEnglish.text = getString(R.string.language_english)
        binding.radioItalian.text = getString(R.string.language_italian)

        val currentLang = AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: "ca"
        when {
            currentLang.startsWith("ca") -> binding.radioCatalan.isChecked = true
            currentLang.startsWith("es") -> binding.radioSpanish.isChecked = true
            currentLang.startsWith("en") -> binding.radioEnglish.isChecked = true
            currentLang.startsWith("it") -> binding.radioItalian.isChecked = true
        }
        isProgrammaticChange = false
    }

    // ▼▼▼ ESBORREM LA FUNCIÓ "notifyAndFinish" ▼▼▼

    // ▼▼▼ RESTAUREM AQUESTA FUNCIÓ A LA SEVA FORMA MÉS SIMPLE ▼▼▼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {

            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
