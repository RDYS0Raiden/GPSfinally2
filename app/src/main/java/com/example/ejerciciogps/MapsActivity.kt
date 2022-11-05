package com.example.ejerciciogps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.ejerciciogps.Coordenadas.HospitalHobrero
import com.example.ejerciciogps.Coordenadas.IsabelaCatolica
import com.example.ejerciciogps.Coordenadas.LaPaz
import com.example.ejerciciogps.Coordenadas.maternoInfantil
import com.example.ejerciciogps.Coordenadas.megacenter
import com.example.ejerciciogps.Coordenadas.stadium
import com.example.ejerciciogps.Coordenadas.teatro
import com.example.ejerciciogps.Coordenadas.triangular
import com.example.ejerciciogps.Coordenadas.univalle
import com.example.ejerciciogps.Coordenadas.valleLuna
import com.example.ejerciciogps.Utills.binding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ejerciciogps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utills.binding = binding
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        enabledToggleButtons()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.apply {
            setMinZoomPreference(14f)
            setMaxZoomPreference(18f)

        }
        //mMap.setMinZoomPreference(17.0f)
        // Add a marker in Sydney and move the camera
        mMap.addMarker(MarkerOptions().position(univalle).title("Marker in Univalle").draggable(true))
        mMap.addMarker(MarkerOptions().position(stadium).title("Hernando Siles").draggable(true))
        mMap.addMarker(MarkerOptions().position(teatro).title("Teatro al aire libre").draggable(true))
        mMap.addMarker(MarkerOptions().position(megacenter).title("Megacente").draggable(true))
        mMap.addMarker(MarkerOptions().position(valleLuna).title("Valle de la luna").draggable(true))
        val laPazBounds = LatLngBounds(IsabelaCatolica, HospitalHobrero)
        val cameraUnivalle = CameraPosition.builder().bearing(45f).tilt(20f).zoom(16f).target(univalle).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraUnivalle))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LaPaz, 12F))
        lifecycleScope.launch{
            delay(3_500)
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(laPazBounds, 12))
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }



        mMap.setLatLngBoundsForCameraTarget(laPazBounds)
        mMap.isMyLocationEnabled = true


        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isRotateGesturesEnabled= false
            isMapToolbarEnabled= true
            isMyLocationButtonEnabled = true

        }

        //padding al mapa
        mMap.setPadding(0,0,0,Utills.dp(130))

        mMap.isTrafficEnabled = true
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(univalle))
      /*  lifecycleScope.launch{
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(valleLuna,16f))
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(megacenter,16f))
        }*/

        val cameraLaPaz = CameraPosition.builder()
            .bearing(45f).tilt(20f).target(univalle).zoom(16f).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraLaPaz))
        lifecycleScope.launch{
            delay(3000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stadium,16f))
            delay(3000)

        }

        mMap.setOnMapClickListener {
            mMap.addMarker(MarkerOptions().position(it).title("Nueva posicion").snippet("${it.latitude},${it.longitude}"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it))

        }
    }

    private fun enabledToggleButtons(){
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                mMap.mapType = when(checkedId){
                    R.id.btnNormal1 -> GoogleMap.MAP_TYPE_NORMAL
                    R.id.btnHibrido -> GoogleMap.MAP_TYPE_HYBRID
                    R.id.btnSatelital -> GoogleMap.MAP_TYPE_SATELLITE
                    R.id.btnTerreno -> GoogleMap.MAP_TYPE_TERRAIN
                    else  -> GoogleMap.MAP_TYPE_NONE
                }
            }
        }
    }
}