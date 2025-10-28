package com.dominio.formularioapp

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import com.google.android.material.textfield.TextInputEditText

class StickerEditText : TextInputEditText {

    // --- CAMBIO 1: El listener ahora debe devolver un Boolean ---
    // Antes: var onStickerReceived: ((Uri) -> Unit)? = null
    var onStickerReceived: ((Uri) -> Boolean)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(editorInfo) ?: return null

        EditorInfoCompat.setContentMimeTypes(
            editorInfo,
            arrayOf("image/png", "image/gif", "image/jpeg", "image/webp")
        )

        val callback =
            InputConnectionCompat.OnCommitContentListener { inputContentInfo, flags, _ ->
                val permissionsGranted =
                    (flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0

                if (permissionsGranted) {
                    try {
                        // --- CAMBIO 2: Llamamos al listener y esperamos un 'true' o 'false' ---
                        // Si el listener devuelve 'true', significa que la app ha gestionado el sticker.
                        // Si devuelve 'false' (o es nulo), la operación no se considera completada.
                        return@OnCommitContentListener onStickerReceived?.invoke(inputContentInfo.contentUri) ?: false
                    } catch (e: Exception) {
                        return@OnCommitContentListener false
                    }
                }
                // Si no hay permisos, la operación falla.
                false
            }

        return InputConnectionCompat.createWrapper(ic, editorInfo, callback)
    }
}
