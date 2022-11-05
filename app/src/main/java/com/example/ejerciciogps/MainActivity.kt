package com.example.ejerciciogps

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.ejerciciogps.Constantes.INTERVAL_TIME
import com.example.ejerciciogps.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlin.math.*

class MainActivity : AppCompatActivity() {
    companion object {
       val PERMISSION_ENABLED = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private lateinit var binding : ActivityMainBinding
    private var enabledGPS = false
    private val PERMISSION_ID = 42
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var contador = 0
    private var distancia = 0.0
    private  var distanciaTotal = 0.0
    private var velocidad = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabgps.setOnClickListener{
            view -> enableGPSService()
        }
        binding.fabCoords.setOnClickListener{
            initGpsService()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initGpsService() {
        if (hasGPSEnabled())
        {
            if(hasPermissionGranted())
            {
                fusedLocation = LocationServices.getFusedLocationProviderClient(this)
                fusedLocation.lastLocation.addOnSuccessListener{
                    location ->manageLocationData()
                }

            }else{
                requestPermissionLocation()
            }
        }else
            goToEnableGPS()
    }
    private fun requestPermissionLocation(){
        ActivityCompat.requestPermissions(this, PERMISSION_ENABLED, PERMISSION_ID)
    }

    @SuppressLint("MissingPermission")
    private fun manageLocationData() {
        var myLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_TIME
        ).setMaxUpdates(100).build()

        /*var myLocationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)*/
        fusedLocation.requestLocationUpdates(myLocationRequest, myLocationCallBack, Looper.myLooper())
    }

    var myLocationCallBack = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            var myLastLocation: Location? = locationResult.lastLocation
            if(myLastLocation != null){
                var lastLatitude = myLastLocation.latitude
                var lastLongitude = myLastLocation.longitude

                binding.txtLatitud.text = lastLatitude.toString()
                binding.txtLongitud.text = lastLongitude.toString()
                if (contador > 0)
                {
                    distancia = calculateDistance(lastLatitude,lastLongitude)
                    distanciaTotal += distancia
                    binding.txtDistancia.text = "$distancia Mts."
                    velocidad = calculateVelocity()
                    binding.txtVelocidad.text = "$velocidad Km/h"
                    binding.txtDistanciaTotal.text = "la distancia recorrida es $distanciaTotal mts"

                }
                latitud = myLastLocation.latitude
                longitud = myLastLocation.longitude
                contador++
                resolverAddress()
            }else
                Toast.makeText(applicationContext, "No se pudo capturar coordenadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resolverAddress() {
        val geocoder: Geocoder = Geocoder(this)
        try {

            var direcciones = geocoder.getFromLocation(latitud, longitud, 1)
            binding.txtDireccion.text = direcciones.get(0).getAddressLine(0)
        } catch (e:java.lang.Exception){
            binding.txtDireccion.text = "Direccion no disponible"
        }
    }

    private fun calculateDistance(lastLatitude: Double, lastLongitude: Double):Double
    {
        val radioTierra = 6371
        val diffLatitud = Math.toRadians(lastLatitude - latitud)
        val diffLongitud = Math.toRadians(lastLongitude - longitud)
        val sinLatitud = sin(diffLatitud / 2)
        val sinLongitud = sin(diffLongitud / 2)
        val resultado1 = (Math.pow(sinLatitud,2.0) + (Math.pow(sinLongitud, 2.0)
                * cos(Math.toRadians(lastLatitude))))
        val resultado2 = 2 * atan2(sqrt(resultado1), sqrt(1 - resultado1))
        val distancia = (radioTierra * resultado2 * 1000.0)
        return distancia
    }

    private fun calculateVelocity(): Double = (distancia / (INTERVAL_TIME /1000.0))*3.6

    private fun enableGPSService() {
       if(!hasGPSEnabled()){
           AlertDialog.Builder(this).setTitle(R.string.dialog_text_title)
               .setMessage(R.string.dialog_text_description)
               .setPositiveButton(R.string.dialog_button_accept,
                   DialogInterface.OnClickListener { dialog, wich ->
                       goToEnableGPS()
                   })
               .setNegativeButton(R.string.dialog_button_denny, { dialog, wich -> enabledGPS = false })
               .setCancelable(true).show()
       }
    }


    private fun goToEnableGPS() {
        enabledGPS = true
        val intent  = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun hasGPSEnabled() : Boolean
    {
        var locationManager: LocationManager
        = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isGPSPermissionsEnabled(): Boolean = ContextCompat.checkSelfPermission(baseContext,android.Manifest.permission.ACCESS_COARSE_LOCATION) ==  PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(baseContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun hasPermissionGranted(): Boolean {
        return PERMISSION_ENABLED.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}

//  