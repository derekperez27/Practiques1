package com.dominio.formularioapp

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException

class GestorUsuaris(context: Context) {


    private val nomFitxer = "usuaris.csv"
    private val fitxer: File

    init {

        val directoriIntern = context.filesDir

        fitxer = File(directoriIntern, nomFitxer)
    }


    fun desarUsuari(usuari: Usuari) {
        try {

            val liniaCSV = "\"${usuari.nombre}\",${usuari.edat},\"${usuari.email}\"\n"


            fitxer.appendText(liniaCSV)

            Log.d("GestorUsuaris", "Usuari ${usuari.nombre} desat correctament a ${fitxer.absolutePath}")

        } catch (e: IOException) {

            Log.e("GestorUsuaris", "Error en desar l'usuari: ${e.message}")
        }
    }

    /**
     * Llegeix tots els usuaris del fitxer i els retorna com una llista.
     * @return Una List<Usuari> amb tots els usuaris trobats.
     */
    fun obtenirUsuaris(): List<Usuari> {
        val llistaUsuaris = mutableListOf<Usuari>()

        if (!fitxer.exists()) {

            return llistaUsuaris
        }

        try {

            fitxer.forEachLine { linia ->

                val dades = linia.split(",")
                if (dades.size == 3) {

                    val nom = dades[0].trim().removeSurrounding("\"")
                    val edat = dades[1].trim().toIntOrNull()
                    val email = dades[2].trim().removeSurrounding("\"")

                    if (edat != null && nom.isNotEmpty() && email.isNotEmpty()) {
                        llistaUsuaris.add(Usuari(nom, edat, email))
                    }
                }
            }
            Log.d("GestorUsuaris", "S'han llegit ${llistaUsuaris.size} usuaris del fitxer.")

        } catch (e: IOException) {
            Log.e("GestorUsuaris", "Error en llegir els usuaris: ${e.message}")
        }

        return llistaUsuaris
    }
}
    