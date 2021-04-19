package com.mobilestudio.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mobilestudio.myapplication.contracts.QrCodeScannerContract
import com.mobilestudio.myapplication.databinding.ActivityMainBinding
import com.mobilestudio.myapplication.viewmodels.MapViewModel

class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener {

    private val AUTOCOMPLETE_REQUEST_CODE = 1020

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MapViewModel>()

    private lateinit var googleMap: GoogleMap

    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            processLocationUpdate(result)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //request location updates
//                    Toast.makeText(this, "Location granted", Toast.LENGTH_SHORT).show()
            } else {
                // show rationale
                showRationaleMessage()
            }
        }

    private val requestQrCode = registerForActivityResult(QrCodeScannerContract()) { contentCode ->
        if (contentCode.isNotBlank()) {
            Toast.makeText(this, contentCode, Toast.LENGTH_SHORT).show()
            //validate contentcode
            viewModel.searchIDContent(contentCode)
        } else {
            Toast.makeText(this, "Codigo invalido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestForLocationPermission()
        setUpAutoCompleteFragment()
//        setUpIntentSearch()
        binding.mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        binding.mapView.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setUpObservers()
        setUpActions()

    }

    private fun setUpActions() {
        binding.btnReadQr.setOnClickListener {
            requestQrCode.launch()
        }
    }

    private fun requestUpdateLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val requestLocation = LocationRequest.create().apply {
                interval = 4000
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                requestLocation,
                callback,
                Looper.getMainLooper()
            )
        } else {
            requestForLocationPermission()
        }
    }

    private fun processLocationUpdate(result: LocationResult?) {
        result ?: return
        for (location in result.locations) {
            val lat = location.latitude
            val lng = location.longitude
            updateCameraWithLatLng(lat, lng)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
    }

    private fun showRationaleMessage() {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Location permission")
            setMessage("We need this permission to update your current location")

            setPositiveButton("Ok") { dialogReference, _ ->
                requestForLocationPermission()
                dialogReference.dismiss()
            }

            setNegativeButton("Cancel") { dialogReference, _ ->
                Toast.makeText(this@MainActivity, "Location not granted", Toast.LENGTH_SHORT).show()
                dialogReference.dismiss()
            }
        }.create()
        dialog.show()
    }

    private fun requestForLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setUpObservers() {
        viewModel.addressName.observe(this) { addressName ->
            Log.e("PLACESNAME", "$addressName")
            autocompleteFragment.setText(addressName)
        }

        viewModel.carnet.observe(this) { carnet ->
//            val intent = Intent(this, DetailCarnetActivity::class.java)
//            intent.putExtra("IDCARNET", carnet.id)
//            startActivity(intent)
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        requestUpdateLocation()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private fun setUpIntentSearch() {
        binding.btnIntent.setOnClickListener {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (data != null) {
                when (resultCode) {
                    RESULT_OK -> processPlaceData(Autocomplete.getPlaceFromIntent(data))
                    AutocompleteActivity.RESULT_ERROR -> processStatus(
                        Autocomplete.getStatusFromIntent(
                            data
                        )
                    )
                    RESULT_CANCELED -> Toast.makeText(
                        this,
                        "Busqueda cancelada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setUpAutoCompleteFragment() {
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocompleteSearch) as AutocompleteSupportFragment

        val fieldList = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
        )

        val rectangularBounds = RectangularBounds.newInstance(
            LatLng(19.160586, -99.440252),
            LatLng(19.611394, -98.897802)
        )

        autocompleteFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN)
        autocompleteFragment.setLocationBias(rectangularBounds)
        autocompleteFragment.setCountries("MX")
        autocompleteFragment.setPlaceFields(fieldList)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                processPlaceData(place)
            }

            override fun onError(status: Status) {
                processStatus(status)
            }
        })


    }

    private fun processPlaceData(place: Place) {
        Log.v("PLACES", "RESULT --->>")
        Log.i("PLACE", "${place.id}")
        Log.i("PLACE", "${place.name}")
        Log.i("PLACE", "${place.address}")
        Log.i("PLACE", "${place.latLng}")
        Log.i("PLACE", "${place.photoMetadatas}")

        val placeLatLng = place.latLng
        if (placeLatLng != null) {
            updateCameraWithLatLng(placeLatLng.latitude, placeLatLng.longitude)
        } else {
            Toast.makeText(
                this,
                "Ocurrio un error al momento de obtener la posición del lugar, intenta nuevamente",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun processStatus(status: Status) {
        Log.w("OnError", "${status.isCanceled}")
        Log.w("OnError", "${status.isInterrupted}")
        Log.w("OnError", "${status.isSuccess}")
        Log.w("OnError", "${status.statusMessage}")
    }

    private fun updateCameraWithLatLng(lat: Double, lng: Double) {
        val cameraPosition = CameraPosition.builder()
            .target(LatLng(lat, lng))
//                .tilt(60f)
            .zoom(15f)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googleMap.animateCamera(cameraUpdate)
    }

    private fun addMarker(name: String, lat: Double, lng: Double) {
        val markerOptions = MarkerOptions()
            .title(name)
            .position(LatLng(lat, lng))

        val marker = googleMap.addMarker(markerOptions)
        marker.showInfoWindow()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.setOnCameraIdleListener(this)

        val cdmx = LatLng(19.413925, -99.167818)
//        addMarker("CDMX", cdmx.latitude, cdmx.longitude)
        updateCameraWithLatLng(cdmx.latitude, cdmx.longitude)
    }

    override fun onCameraIdle() {
        val target = googleMap.cameraPosition.target
        Log.d("TARGETCAMERA", "$target")

        viewModel.searchByLatLng(target.latitude, target.longitude)
    }
}