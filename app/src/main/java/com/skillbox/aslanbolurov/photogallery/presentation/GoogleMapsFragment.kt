package com.skillbox.aslanbolurov.photogallery.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.skillbox.aslanbolurov.photogallery.R
import com.skillbox.aslanbolurov.photogallery.databinding.FragmentGoogleMapsBinding

private const val TAG = "GoogleMapsFragment"

class GoogleMapsFragment : Fragment() {

    private var _binding: FragmentGoogleMapsBinding? = null
    val binding get() = _binding!!

    private val viewModel: GoogleMapsViewModel by viewModels()

    private lateinit var fusedClient: FusedLocationProviderClient
    private var needAnimateCamera = false
    private var map: GoogleMap? = null
    private var locationListener: LocationSource.OnLocationChangedListener? = null




    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        if (map.values.isNotEmpty() && map.values.all { it }) {
            Log.d(TAG, "startLocation out!: ")
            startLocation()
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                locationListener?.onLocationChanged(location)

                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    18f
                )

                if (needAnimateCamera) {
                    map?.animateCamera(cameraUpdate)
                } else {
                    needAnimateCamera = true
                    map?.moveCamera(cameraUpdate)
                }


            }
            Log.d(TAG, "onLocationResult")
//            binding.message.text = result.lastLocation.toString()
        }
    }
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        with(googleMap.uiSettings) {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
        }
        googleMap.isMyLocationEnabled = true


        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(sydney)
                .title("Marker in Sydney")
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        googleMap.setLocationSource(object : LocationSource {
            override fun activate(p0: LocationSource.OnLocationChangedListener) {
                locationListener = p0
            }

            override fun deactivate() {
                locationListener = null
            }

        })


        map = googleMap


    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        map?.isMyLocationEnabled = true
        Log.d(TAG, "startLocation: ")
        val request = LocationRequest.create()
            .setInterval(1_000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGoogleMapsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onStop() {
        super.onStop()
        fusedClient.removeLocationUpdates(locationCallback)
        needAnimateCamera = false
    }

    private fun checkPermissions() {
        if (REQUIRED_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            startLocation()
        } else {
            launcher.launch(REQUIRED_PERMISSIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance() = GoogleMapsFragment()

        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}