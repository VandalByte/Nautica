package com.dev.nautica

import android.app.AlertDialog
import android.location.Geocoder
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import java.util.Locale

class HeatmapActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var heatmapProvider: HeatmapTileProvider
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var beachData: HashMap<String, BeachInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heatmap) // Ensure this matches the XML filename

        // Initialize AutoCompleteTextView for search
        autoCompleteTextView = findViewById(R.id.auto_complete_search)

        // Initialize beach data
        initializeBeachData()

        // Set up the autocomplete adapter
        val beachNames = beachData.keys.toList()
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, beachNames)
        autoCompleteTextView.setAdapter(adapter)


        // Handle selection from dropdown
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedBeachName = parent.getItemAtPosition(position) as String
            searchLocation(selectedBeachName)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add OnMapClickListener to the map
        mMap.setOnMapClickListener(this)

        // Sample suitability data
        val suitabilityData = listOf(
            SuitabilityData(LatLng(13.0475, 80.2824), 87),  // Marina Beach, Chennai
            SuitabilityData(LatLng(19.0968, 72.8265), 45),  // Juhu Beach, Mumbai
            SuitabilityData(LatLng(8.7379, 76.6986), 62),   // Varkala Beach, Kerala
            SuitabilityData(LatLng(15.5489, 73.7555), 95),  // Calangute Beach, Goa
            SuitabilityData(LatLng(8.3916, 76.9785), 38),   // Kovalam Beach, Kerala
            SuitabilityData(LatLng(15.0101, 74.0237), 74),  // Palolem Beach, Goa
            SuitabilityData(LatLng(14.5503, 74.3186), 66),  // Gokarna Beach, Karnataka
            SuitabilityData(LatLng(19.7983, 85.8245), 55),  // Puri Beach, Odisha
            SuitabilityData(LatLng(12.9238, 74.8225), 81),  // Malpe Beach, Karnataka
            SuitabilityData(LatLng(22.6031, 88.4197), 40),  // Digha Beach, West Bengal
            SuitabilityData(LatLng(11.9321, 79.8342), 78),  // Paradise Beach, Puducherry
            SuitabilityData(LatLng(21.6284, 69.6000), 92),  // Mandvi Beach, Gujarat
            SuitabilityData(LatLng(15.2762, 73.9060), 48),  // Vagator Beach, Goa
            SuitabilityData(LatLng(22.8142, 70.0131), 65),  // Shivrajpur Beach, Gujarat
            SuitabilityData(LatLng(8.4836, 76.9485), 73),   // Sanguthurai Beach, Kerala
            SuitabilityData(LatLng(20.9374, 86.2616), 59),  // Chandrabhaga Beach, Odisha
            SuitabilityData(LatLng(15.1745, 73.9422), 33),  // Anjuna Beach, Goa
            SuitabilityData(LatLng(16.8376, 82.2712), 70),  // Kakinada Beach, Andhra Pradesh
            SuitabilityData(LatLng(10.7829, 79.8407), 62),  // Poompuhar Beach, Tamil Nadu
            SuitabilityData(LatLng(19.0748, 72.8798), 94),  // Aksa Beach, Mumbai
            SuitabilityData(LatLng(12.6765, 74.7771), 88),  // Ullal Beach, Karnataka
            SuitabilityData(LatLng(11.9465, 79.7858), 72),  // Serenity Beach, Puducherry
            SuitabilityData(LatLng(10.0159, 76.2250), 60),  // Cherai Beach, Kerala
            SuitabilityData(LatLng(13.6269, 74.6908), 81),  // Maravanthe Beach, Karnataka
            SuitabilityData(LatLng(21.7110, 87.8272), 45),  // Talasari Beach, Odisha
            SuitabilityData(LatLng(15.1286, 73.9391), 99),  // Baga Beach, Goa
            SuitabilityData(LatLng(12.4218, 74.7743), 53),  // Someshwara Beach, Karnataka
            SuitabilityData(LatLng(9.9362, 76.2599), 64),   // Fort Kochi Beach, Kerala
            SuitabilityData(LatLng(20.3060, 86.8655), 76),  // Pati Sonepur Beach, Odisha
            SuitabilityData(LatLng(15.2104, 73.9850), 70),  // Morjim Beach, Goa
            SuitabilityData(LatLng(17.6904, 83.2352), 58),  // Rishikonda Beach, Andhra Pradesh
            SuitabilityData(LatLng(21.0534, 69.4523), 89),  // Gopnath Beach, Gujarat
            SuitabilityData(LatLng(15.3969, 73.8184), 67),  // Candolim Beach, Goa
            SuitabilityData(LatLng(9.3012, 79.3129), 74),   // Dhanushkodi Beach, Tamil Nadu
            SuitabilityData(LatLng(16.9944, 82.2475), 82),  // Uppada Beach, Andhra Pradesh
            SuitabilityData(LatLng(12.9081, 74.8073), 38),  // Panambur Beach, Karnataka
            SuitabilityData(LatLng(21.0221, 72.5496), 94),  // Dumas Beach, Gujarat
            SuitabilityData(LatLng(11.6146, 92.7317), 77),  // Radhanagar Beach, Andaman
            SuitabilityData(LatLng(16.5131, 81.7310), 46),  // Vodarevu Beach, Andhra Pradesh
            SuitabilityData(LatLng(10.7166, 79.8367), 63)   // Silver Beach, Tamil Nadu
        )

        // Convert SuitabilityData to WeightedLatLng
        val weightedData = suitabilityData.map {
            WeightedLatLng(it.latLng, it.suitabilityScore.toDouble())
        }

        // Define a gradient using a single color (green) with varying intensity
        val gradient = Gradient(
            intArrayOf(
                0x2835b8.toInt(),  // Green with no transparency
                0xFF2835b8.toInt() // Fully opaque green
            ),
            floatArrayOf(0.2f, 1.0f) // Adjust the range as needed
        )

        // Create HeatmapTileProvider with the single-color gradient
        heatmapProvider = HeatmapTileProvider.Builder()
            .weightedData(weightedData)
            .radius(50) // Adjust radius as needed
            .opacity(0.8) // Adjust opacity as needed
            .gradient(gradient) // Apply custom gradient
            .build()

        // Add heatmap overlay to the map
        mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))

        // Move the camera to a central location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(20.5937, 78.9629), 5f)) // Zoom level 5
    }

    private fun initializeBeachData() {
        beachData = hashMapOf(
            "Marina Beach" to BeachInfo("Chennai", LatLng(13.0475, 80.2824)),
            "Juhu Beach" to BeachInfo("Mumbai", LatLng(19.0968, 72.8265)),
            "Varkala Beach" to BeachInfo("Kerala", LatLng(8.7379, 76.6986)),
            // Add more beach data...
        )
    }

    private fun searchLocation(beachName: String) {
        val beachInfo = beachData[beachName]
        beachInfo?.let {
            val latLng = it.latLng

            // Move the camera to the selected beach location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

            // Add a marker at the selected beach location
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(beachName)
            )

            // Make API request to get weather and suitability data (unchanged)
            val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

            ApiClient.weatherApiService.getWeatherData(latLng.latitude, latLng.longitude, currentHour)
                .enqueue(object : retrofit2.Callback<ApiResponse> {
                    override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                val location = apiResponse.response.display.location
                                // TODO: change the max_temp to temp
                                val maxTemp = apiResponse.response.display.temp
                                val suitability = apiResponse.response.suitability_percentage

                                AlertDialog.Builder(this@HeatmapActivity)
                                    .setTitle("Beach Info")
                                    .setMessage("""
                                        Beach: $beachName
                                        Location: ${it.placeName}
                                        Max Temp: $maxTemp°C
                                        Suitability: $suitability%
                                    """.trimIndent())
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                        AlertDialog.Builder(this@HeatmapActivity)
                            .setTitle("Error")
                            .setMessage("Failed to fetch weather data: ${t.message}")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                })
        }
    }


    override fun onMapClick(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        addresses?.firstOrNull()?.let { address ->
            val placeName = address.featureName ?: "Unknown Place"
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            // Get the current hour for the 'time' query parameter
            val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

            // Make API request
            ApiClient.weatherApiService.getWeatherData(latitude, longitude, currentHour)
                .enqueue(object : retrofit2.Callback<ApiResponse> {
                    override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                val location = apiResponse.response.display.location
                                val maxTemp = apiResponse.response.display.temp
                                val suitability = apiResponse.response.suitability_percentage

                                // Show place name, coordinates, and additional weather data
                                AlertDialog.Builder(this@HeatmapActivity)
                                    .setTitle("Location Details")
                                    .setMessage("""
                                    Place: $placeName
                                    Latitude: ${String.format("%.4f", latitude)}
                                    Longitude: ${String.format("%.4f", longitude)}
                                    Location: $location
                                    Max Temp: $maxTemp°C
                                    Suitability: $suitability%
                                """.trimIndent())
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                        // Handle error
                        AlertDialog.Builder(this@HeatmapActivity)
                            .setTitle("Error")
                            .setMessage("Failed to fetch weather data: ${t.message}")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                })
        }
    }
    data class BeachInfo(val placeName: String, val latLng: LatLng)
}
