package com.example.ejerciciogps

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ejerciciogps.Constantes.KEY_NAME
import com.example.ejerciciogps.databinding.ActivityPersistenciaBinding
import java.util.prefs.AbstractPreferences

class PersistenciaActivity : AppCompatActivity() {

    //crear un formulario con los datos: Nombre, Apellido, Nota 1p, Nota 2p, Nota F
    //Opcion para listar una materia

    private lateinit var binding: ActivityPersistenciaBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeSharedPreference()
        LoadData()
        binding.btnGuardar.setOnClickListener{
            saveData()
        }

    }

    private fun initializeSharedPreference() {
        sharedPreferences = getSharedPreferences("Persistencia", MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    private fun saveData()
    {
        val NombreCompleto = binding.etNombreCompleto.text.toString()
        editor.apply{
            putString(KEY_NAME, NombreCompleto)
        }.apply()
    }

    private fun LoadData()
    {
        binding.txtNombre.text = sharedPreferences.getString(KEY_NAME, "Vacio")
    }
}