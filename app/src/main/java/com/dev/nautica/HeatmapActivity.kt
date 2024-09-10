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
            "Puri Beach" to BeachInfo("Odisha", LatLng(19.798, 85.824)),
            "Pati Sonepur Sea Beach" to BeachInfo("Odisha", LatLng(19.014, 84.963)),
            "Rushikonda Beach" to BeachInfo("Andhra Pradesh", LatLng(17.757, 83.382)),
            "Kovalam Beach" to BeachInfo("Kerala", LatLng(8.399, 76.978)),
            "Eden Beach" to BeachInfo("Tamil Nadu", LatLng(11.876, 79.813)),
            "Radhanagar Beach" to BeachInfo("Andaman and Nicobar Islands", LatLng(12.006, 92.974)),
            "Minicoy Thundi Beach" to BeachInfo("Lakshadweep", LatLng(8.266, 73.045)),
            "Kadmat Beach" to BeachInfo("Lakshadweep", LatLng(11.223, 72.782)),
            "Kappad Beach" to BeachInfo("Kerala", LatLng(11.393, 75.736)),
            "Kasarkod Beach" to BeachInfo("Karnataka", LatLng(14.480, 74.462)),
            "Padubidri Beach" to BeachInfo("Karnataka", LatLng(13.117, 74.794)),
            "Ghoghla Beach" to BeachInfo("Diu", LatLng(20.708, 71.022)),
            "Shivrajpur Beach" to BeachInfo("Gujarat", LatLng(22.339, 68.980)),

            // Gujarat beaches
            "Dumas Beach" to BeachInfo("Gujarat", LatLng(21.103, 72.638)),
            "Suvali Beach" to BeachInfo("Gujarat", LatLng(21.181, 72.620)),
            "Umbharat Beach" to BeachInfo("Gujarat", LatLng(20.814, 72.791)),
            "Dandi Beach" to BeachInfo("Gujarat", LatLng(20.892, 72.709)),
            "Dabhari Beach" to BeachInfo("Gujarat", LatLng(21.065, 72.649)),
            "Diu Beach" to BeachInfo("Diu", LatLng(20.708, 70.921)),
            "Tithal Beach" to BeachInfo("Gujarat", LatLng(20.608, 72.656)),
            "Mandavi Beach" to BeachInfo("Gujarat", LatLng(22.833, 69.345)),
            "Khambhat Beach" to BeachInfo("Gujarat", LatLng(22.308, 72.616)),

            // Maharashtra beaches
            "Aksa Beach" to BeachInfo("Maharashtra", LatLng(19.2220, 72.8386)),
            "Alibaug Beach" to BeachInfo("Maharashtra", LatLng(18.6414, 72.8328)),
            "Gorai Beach" to BeachInfo("Maharashtra", LatLng(19.2240, 72.8200)),
            "Juhu Beach" to BeachInfo("Maharashtra", LatLng(19.0982, 72.8328)),
            "Manori Beach" to BeachInfo("Maharashtra", LatLng(19.2201, 72.8355)),
            "Marvé Beach" to BeachInfo("Maharashtra", LatLng(19.1700, 72.8256)),
            "Versova Beach" to BeachInfo("Maharashtra", LatLng(19.1549, 72.8354)),
            "Agardanda Beach" to BeachInfo("Maharashtra", LatLng(18.5760, 72.8420)),
            "Diveagar Beach" to BeachInfo("Maharashtra", LatLng(18.1025, 72.8288)),
            "Ganpatipule Beach" to BeachInfo("Maharashtra", LatLng(17.5362, 73.3321)),
            "Guhagar Beach" to BeachInfo("Maharashtra", LatLng(17.4899, 73.1591)),
            "Kelwa Beach" to BeachInfo("Maharashtra", LatLng(19.8018, 72.8340)),
            "Tarkarli Beach" to BeachInfo("Maharashtra", LatLng(16.0131, 73.0555)),
            "Shivaji Park Beach" to BeachInfo("Maharashtra", LatLng(19.0352, 72.8352)),
            "Anjarle Beach" to BeachInfo("Maharashtra", LatLng(17.9450, 73.1350)),
            "Dapoli Beach" to BeachInfo("Maharashtra", LatLng(17.7480, 73.1170)),
            "Dahanu Beach" to BeachInfo("Maharashtra", LatLng(19.0000, 72.7833)),
            "Srivardhan Beach" to BeachInfo("Maharashtra", LatLng(17.8550, 73.1064)),
            "Kihim Beach" to BeachInfo("Maharashtra", LatLng(18.6141, 72.8317)),
            "Mandwa Beach" to BeachInfo("Maharashtra", LatLng(18.6351, 72.8350)),
            "Velneshwar Beach" to BeachInfo("Maharashtra", LatLng(18.4020, 73.2500)),
            "Vengurla Beach" to BeachInfo("Maharashtra", LatLng(15.8496, 73.5120)),
            "Bassein Beach" to BeachInfo("Maharashtra", LatLng(19.3053, 72.8367)),
            "Bhandarpule Beach" to BeachInfo("Maharashtra", LatLng(17.5335, 73.4155)),
            "Nagaon Beach" to BeachInfo("Maharashtra", LatLng(18.6064, 72.8314)),
            "Revdanda Beach" to BeachInfo("Maharashtra", LatLng(18.6092, 72.8610)),
            "Rewas Beach" to BeachInfo("Maharashtra", LatLng(18.6571, 72.7898)),
            "Kashid Beach" to BeachInfo("Maharashtra", LatLng(18.5012, 72.9630)),
            "Karde Beach" to BeachInfo("Maharashtra", LatLng(17.9758, 73.1007)),
            "Harihareshwar Beach" to BeachInfo("Maharashtra", LatLng(17.9158, 73.0850)),
            "Bagmandla Beach" to BeachInfo("Maharashtra", LatLng(18.8093, 72.9374)),
            "Kelshi Beach" to BeachInfo("Maharashtra", LatLng(17.5642, 73.2790)),
            "Harnai Beach" to BeachInfo("Maharashtra", LatLng(17.7252, 73.1245)),
            "Bordi Beach" to BeachInfo("Maharashtra", LatLng(19.0020, 72.7420)),
            "Ratnagiri Beach" to BeachInfo("Maharashtra", LatLng(16.9937, 73.3114)),
            "Awas Beach" to BeachInfo("Maharashtra", LatLng(17.6334, 73.4060)),
            "Sasawne Beach" to BeachInfo("Maharashtra", LatLng(17.7680, 73.1130)),
            "Malvan Beach" to BeachInfo("Maharashtra", LatLng(16.0373, 73.4593)),

            // Goa beaches
            "Agonda Beach" to BeachInfo("Goa", LatLng(15.0092, 74.0743)),
            "Arambol Beach" to BeachInfo("Goa", LatLng(15.5523, 73.7657)),
            "Benaulim Beach" to BeachInfo("Goa", LatLng(15.2693, 73.9506)),
            "Cavelossim Beach" to BeachInfo("Goa", LatLng(15.0053, 73.9933)),
            "Chapora Beach" to BeachInfo("Goa", LatLng(15.5521, 73.7460)),
            "Mandrem Beach" to BeachInfo("Goa", LatLng(15.5955, 73.7516)),
            "Palolem Beach" to BeachInfo("Goa", LatLng(15.0087, 74.0846)),
            "Varca Beach" to BeachInfo("Goa", LatLng(15.0107, 73.9437)),
            "Baga Beach" to BeachInfo("Goa", LatLng(15.5526, 73.7830)),
            "Candolim Beach" to BeachInfo("Goa", LatLng(15.5524, 73.7900)),
            "Calangute Beach" to BeachInfo("Goa", LatLng(15.5525, 73.7685)),
            "Colva Beach" to BeachInfo("Goa", LatLng(15.2731, 73.9992)),
            "Miramar Beach" to BeachInfo("Goa", LatLng(15.4933, 73.8250)),
            "Morjim Beach" to BeachInfo("Goa", LatLng(15.5832, 73.7583)),
            "Bambolim Beach" to BeachInfo("Goa", LatLng(15.4628, 73.8284)),
            "Cabo de Rama Beach" to BeachInfo("Goa", LatLng(15.0048, 73.9425)),
            "Anjuna Beach" to BeachInfo("Goa", LatLng(15.5505, 73.8123)),
            "Utorda Beach" to BeachInfo("Goa", LatLng(15.2751, 73.9923)),
            "Majorda Beach" to BeachInfo("Goa", LatLng(15.2693, 73.9751)),
            "Betalbatim Beach" to BeachInfo("Goa", LatLng(15.2560, 73.9890)),
            "Sernabatim Beach" to BeachInfo("Goa", LatLng(15.2560, 73.9980)),
            "Mobor Beach" to BeachInfo("Goa", LatLng(15.0134, 73.9898)),
            "Betul Beach" to BeachInfo("Goa", LatLng(15.0053, 73.9840)),
            "Querim Beach" to BeachInfo("Goa", LatLng(15.5836, 73.7465)),
            "Kalacha Beach" to BeachInfo("Goa", LatLng(15.6000, 73.7400)),
            "Ashvem Beach" to BeachInfo("Goa", LatLng(15.6067, 73.7512)),
            "Vagator Beach" to BeachInfo("Goa", LatLng(15.5521, 73.7642)),
            "Ozran Beach" to BeachInfo("Goa", LatLng(15.5545, 73.7635)),
            "Sinquerim Beach" to BeachInfo("Goa", LatLng(15.5499, 73.8156)),
            "Coco Beach" to BeachInfo("Goa", LatLng(15.5515, 73.7838)),
            "Kegdole Beach" to BeachInfo("Goa", LatLng(15.5653, 73.7767)),
            "Caranzalem Beach" to BeachInfo("Goa", LatLng(15.4970, 73.8096)),
            "Dona Paula Beach" to BeachInfo("Goa", LatLng(15.4911, 73.8238)),
            "Vaiguinim Beach" to BeachInfo("Goa", LatLng(15.4748, 73.8322)),
            "Siridao Beach" to BeachInfo("Goa", LatLng(15.5105, 73.8372)),
            "Bogmalo Beach" to BeachInfo("Goa", LatLng(15.4354, 73.8466)),
            "Baina Beach" to BeachInfo("Goa", LatLng(15.4394, 73.8556)),
            "Hansa Beach" to BeachInfo("Goa", LatLng(15.4349, 73.8453)),
            "Hollant Beach" to BeachInfo("Goa", LatLng(15.4282, 73.8554)),
            "Cansaulim Beach" to BeachInfo("Goa", LatLng(15.2728, 73.9937)),
            "Velsao Beach" to BeachInfo("Goa", LatLng(15.3339, 73.9608)),
            "Canaiguinim Beach" to BeachInfo("Goa", LatLng(15.2483, 73.9678)),
            "Kakolem Beach" to BeachInfo("Goa", LatLng(15.4914, 73.8857)),
            "Dharvalem Beach" to BeachInfo("Goa", LatLng(15.4945, 73.8925)),
            "Cola Beach" to BeachInfo("Goa", LatLng(15.0145, 74.0828)),
            "Patnem Beach" to BeachInfo("Goa", LatLng(15.0107, 74.0840)),
            "Rajbag Beach" to BeachInfo("Goa", LatLng(15.0206, 74.0720)),
            "Talpona Beach" to BeachInfo("Goa", LatLng(15.0160, 74.0770)),
            "Galgibag Beach" to BeachInfo("Goa", LatLng(15.0912, 74.0148)),
            "Polem Beach" to BeachInfo("Goa", LatLng(14.9750, 74.0650)),
            "Pebble Beach Goa" to BeachInfo("Goa", LatLng(15.0153, 74.0860)),
            // Karnataka beaches
            "Karwar Beach" to BeachInfo("Karnataka", LatLng(14.7976, 74.1258)),
            "Kudle Beach" to BeachInfo("Karnataka", LatLng(14.5577, 74.0624)),
            "Panambur Beach" to BeachInfo("Karnataka", LatLng(12.9274, 74.8308)),
            "NITK Beach" to BeachInfo("Karnataka", LatLng(12.9321, 74.8341)),
            "Sasihithlu Beach" to BeachInfo("Karnataka", LatLng(12.9757, 74.8320)),
            "Maravanthe Beach" to BeachInfo("Karnataka", LatLng(13.6757, 74.4474)),
            "Tannirubhavi Beach" to BeachInfo("Karnataka", LatLng(12.9304, 74.8309)),
            "Malpe Beach" to BeachInfo("Karnataka", LatLng(13.3058, 74.7877)),
            "Murudeshwara Beach" to BeachInfo("Karnataka", LatLng(14.1111, 74.4375)),
            "Apsarakonda Beach" to BeachInfo("Karnataka", LatLng(14.1186, 74.4538)),
            "Om Beach" to BeachInfo("Karnataka", LatLng(14.5567, 74.1290)),
            "Kaup Beach" to BeachInfo("Karnataka", LatLng(13.0972, 74.7730)),
            "Kodi Beach" to BeachInfo("Karnataka", LatLng(13.1144, 74.7424)),
            "Someshwar Beach" to BeachInfo("Karnataka", LatLng(14.5630, 74.0562)),
            "St Mary's Island Beach" to BeachInfo("Karnataka", LatLng(14.7336, 74.0755)),
            "Mukka Beach" to BeachInfo("Karnataka", LatLng(12.9266, 74.8318)),
            "Ullal Beach" to BeachInfo("Karnataka", LatLng(12.8740, 74.8330)),
            // West Bengal beaches
            "Henry Island Beach" to BeachInfo("West Bengal", LatLng(21.6846, 88.6582)),
            "Bakkhali Sea Beach" to BeachInfo("West Bengal", LatLng(21.5721, 88.2652)),
            "Frasergunj Sea Beach" to BeachInfo("West Bengal", LatLng(21.5971, 88.2902)),
            "Gangasagar Sea Beach" to BeachInfo("West Bengal", LatLng(21.6216, 88.9312)),
            "Junput Beach" to BeachInfo("West Bengal", LatLng(21.7032, 88.5952)),
            "Bankiput Sea Beach" to BeachInfo("West Bengal", LatLng(21.6583, 88.2564)),
            "Mandarmani Beach" to BeachInfo("West Bengal", LatLng(21.6371, 87.8294)),
            "Shankarpur Beach" to BeachInfo("West Bengal", LatLng(21.6280, 87.8550)),
            "Tajpur Beach" to BeachInfo("West Bengal", LatLng(21.6764, 87.9361)),
            "Digha Sea Beach" to BeachInfo("West Bengal", LatLng(21.6174, 87.5131)),
            "Udaypur Sea Beach" to BeachInfo("West Bengal", LatLng(21.6135, 87.5278)),
            // Kerala beaches
            "Kovalam Beach" to BeachInfo("Kerala", LatLng(8.4025, 76.9969)),
            "Kollam Beach" to BeachInfo("Kerala", LatLng(8.8833, 76.6000)),
            "Cherai Beach" to BeachInfo("Kerala", LatLng(10.1745, 76.2242)),
            "Chavakkad Beach" to BeachInfo("Kerala", LatLng(10.5333, 76.0833)),
            "Fort Kochi Beach" to BeachInfo("Kerala", LatLng(9.9656, 76.2414)),
            "Kanhangad Beach" to BeachInfo("Kerala", LatLng(12.2802, 75.0571)),
            "Marari Beach" to BeachInfo("Kerala", LatLng(9.6204, 76.3878)),
            "Meenkunnu Beach" to BeachInfo("Kerala", LatLng(11.9066, 75.3554)),
            "Muzhappilangad Beach" to BeachInfo("Kerala", LatLng(11.9930, 75.3881)),
            "Payyambalam Beach" to BeachInfo("Kerala", LatLng(11.8667, 75.3667)),
            "Saddam Beach" to BeachInfo("Kerala", LatLng(8.8833, 76.6000)),
            "Shangumughom Beach" to BeachInfo("Kerala", LatLng(8.4685, 76.9530)),
            "Snehatheeram Beach" to BeachInfo("Kerala", LatLng(10.1900, 76.0500)),
            "Kappil Beach Varkala" to BeachInfo("Kerala", LatLng(8.7328, 76.7068)),
            "Thirumullavaram Beach" to BeachInfo("Kerala", LatLng(8.7511, 76.5861)),
            "Hawa Beach" to BeachInfo("Kerala", LatLng(8.4020, 76.9981)),
            "Samudra Beach" to BeachInfo("Kerala", LatLng(8.4014, 76.9978)),
            "Lighthouse Beach" to BeachInfo("Kerala", LatLng(8.4008, 76.9986)),
            "Kannur Beach" to BeachInfo("Kerala", LatLng(11.8745, 75.3670)),
            "Kappad Beach" to BeachInfo("Kerala", LatLng(11.7019, 75.5694)),
            "Varkala Beach" to BeachInfo("Kerala", LatLng(8.7355, 76.7118)),
            "Padinjarekkara Beach" to BeachInfo("Kerala", LatLng(11.0486, 75.8095)),
            "Tanur Beach" to BeachInfo("Kerala", LatLng(10.7857, 75.8636)),
            "Azheekal Beach" to BeachInfo("Kerala", LatLng(10.4525, 75.8286)),
            "Alappuzha Beach" to BeachInfo("Kerala", LatLng(9.4900, 76.3390)),
            "Kozhikode Beach" to BeachInfo("Kerala", LatLng(11.2580, 75.7804)),
            "Bekal Beach" to BeachInfo("Kerala", LatLng(12.3626, 75.0456)),
            "Thiruvambadi Beach" to BeachInfo("Kerala", LatLng(10.4511, 75.8230)),
            "Kappil Beach" to BeachInfo("Kerala", LatLng(8.7328, 76.7068)),
            // Odisha beaches
            "Puri Beach" to BeachInfo("Odisha", LatLng(19.7980, 85.8245)),
            "Pati Sonepur Sea Beach" to BeachInfo("Odisha", LatLng(19.0140, 84.9630)),
            "Gopalpur Beach" to BeachInfo("Odisha", LatLng(19.2756, 84.8671)),
            "Chandrabhaga Beach" to BeachInfo("Odisha", LatLng(19.8924, 86.0896)),
            "Konark Beach" to BeachInfo("Odisha", LatLng(19.8905, 86.0587)),
            "Gahirmatha Beach" to BeachInfo("Odisha", LatLng(20.7325, 86.9792)),
            "Jambhira Beach" to BeachInfo("Odisha", LatLng(20.8306, 86.9290)),
            "Ramchandi Beach" to BeachInfo("Odisha", LatLng(19.9148, 86.0535)),
            "Kuhuri Beach" to BeachInfo("Odisha", LatLng(19.2853, 85.8436)),
            "Dhamra Beach" to BeachInfo("Odisha", LatLng(20.7355, 86.8231)),
            // Andra Pradesh beaches
            "Sonpur Beach" to BeachInfo("Andhra Pradesh", LatLng(17.1027, 82.2053)),
            "Donkuru Beach" to BeachInfo("Andhra Pradesh", LatLng(16.9120, 81.7925)),
            "Nelavanka Beach" to BeachInfo("Andhra Pradesh", LatLng(16.8212, 81.5792)),
            "Kaviti Beach" to BeachInfo("Andhra Pradesh", LatLng(16.8273, 81.6361)),
            "Onturu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.7890, 81.6585)),
            "Ramayyapatnam Beach" to BeachInfo("Andhra Pradesh", LatLng(16.7468, 81.8176)),
            "Baruva Beach" to BeachInfo("Andhra Pradesh", LatLng(16.7375, 81.7322)),
            "Battigalluru Beach" to BeachInfo("Andhra Pradesh", LatLng(16.6926, 81.5805)),
            "Sirmamidi Beach" to BeachInfo("Andhra Pradesh", LatLng(16.6740, 81.6360)),
            "Ratti Beach" to BeachInfo("Andhra Pradesh", LatLng(16.6518, 81.5852)),
            "Shivasagar Beach" to BeachInfo("Andhra Pradesh", LatLng(16.6260, 81.5584)),
            "Dokulapadu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.6210, 81.5630)),
            "Nuvvalarevu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.6070, 81.5590)),
            "KR Peta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5980, 81.5690)),
            "Bavanapadu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5650, 81.5460)),
            "Mula Peta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5450, 81.5300)),
            "BVS Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5180, 81.5050)),
            "Patha Meghavaram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4900, 81.4800)),
            "Guppidipeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4780, 81.4700)),
            "Kotharevu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4550, 81.4400)),
            "Rajaram Puram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4400, 81.4300)),
            "Kalingapatnam Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4250, 81.4100)),
            "Bandaruvanipeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4090, 81.4000)),
            "Mogadalapadu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3900, 81.3800)),
            "Vatsavalasa Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3750, 81.3700)),
            "S. Matchelesam Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3570, 81.3500)),
            "Balarampuram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3390, 81.3400)),
            "Kunduvanipeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3200, 81.3100)),
            "PD Palem Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3080, 81.2900)),
            "Budagatlapalem Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2950, 81.2800)),
            "Kotcherla Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2820, 81.2600)),
            "Jeerupalem Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2700, 81.2500)),
            "Kovvada Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2560, 81.2400)),
            "Pothayyapeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2450, 81.2300)),
            "Chintapalli NGF Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2300, 81.2200)),
            "Chintapalli Beach" to BeachInfo("Andhra Pradesh", LatLng(16.2100, 81.2000)),
            "Tammayyapalem Beach" to BeachInfo("Andhra Pradesh", LatLng(16.1950, 81.1800)),
            "Konada Beach" to BeachInfo("Andhra Pradesh", LatLng(16.1780, 81.1600)),
            "Divis Beach" to BeachInfo("Andhra Pradesh", LatLng(16.1650, 81.1450)),
            "Bheemili Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7064, 83.2333)),
            "Mangamaripeta Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6860, 83.2600)),
            "Thotlakonda Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6690, 83.2760)),
            "Rushikonda Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7498, 83.3053)),
            "Sagarnagar Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7348, 83.2940)),
            "Jodugullapalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7230, 83.2800)),
            "RK Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7218, 83.3065)),
            "Durga Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7100, 83.2950)),
            "Yarada Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7050, 83.2950)),
            "Gagavaram Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6950, 83.2820)),
            "Adi's Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6800, 83.2700)),
            "Appikonda Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6650, 83.2600)),
            "Tikkavanipalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6500, 83.2450)),
            "Mutyalammapalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6400, 83.2350)),
            "Thanthadi Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6280, 83.2200)),
            "Seethapalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6100, 83.2050)),
            "Rambilli Beach" to BeachInfo("Andhra Pradesh", LatLng(17.5950, 83.1900)),
            "Kothapatnam Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5680, 81.5630)),
            "Revupolavaram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5570, 81.5500)),
            "Gudivada Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5460, 81.5400)),
            "Gurrajupeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5300, 81.5200)),
            "Pedhatheenarla Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5200, 81.5100)),
            "Rajjyapeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.5080, 81.5000)),
            "Boyapadu Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4900, 81.4800)),
            "DLPuram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4750, 81.4600)),
            "Pentakota Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4570, 81.4450)),
            "Rajavaram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4450, 81.4300)),
            "Addaripeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4300, 81.4200)),
            "Danvaipeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4150, 81.4100)),
            "Gaddipeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.4000, 81.4000)),
            "K. Perumallapuram Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3870, 81.3900)),
            "Konapapapeta Beach" to BeachInfo("Andhra Pradesh", LatLng(16.3700, 81.3750)),
            "Uppada Beach" to BeachInfo("Andhra Pradesh", LatLng(17.0790, 82.0162)),
            "Nemam Beach" to BeachInfo("Andhra Pradesh", LatLng(17.0900, 82.0210)),
            "NTR Beach" to BeachInfo("Andhra Pradesh", LatLng(17.6800, 83.3200)),
            "Seahorse Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7000, 83.3300)),
            "Dragonmouth Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7100, 83.3400)),
            "Pallam Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7200, 83.3500)),
            "Sunrise Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7300, 83.3600)),
            "Surasani Yanam Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7400, 83.3700)),
            "Vasalatippa Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7500, 83.3800)),
            "Odalarevu Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7600, 83.3900)),
            "Turpupalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7700, 83.4000)),
            "Kesanapalli Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7800, 83.4100)),
            "Shankaraguptham Beach" to BeachInfo("Andhra Pradesh", LatLng(17.7900, 83.4200)),
            "Chintalamori Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8000, 83.4300)),
            "Natural Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8100, 83.4400)),
            "KDP Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8200, 83.4500)),
            "Antervedi Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8300, 83.4600)),
            "Pedamainavanilanka Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8400, 83.4700)),
            "Perupalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8500, 83.4800)),
            "Kanakadurga Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8600, 83.4900)),
            "Gollapalem Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8700, 83.5000)),
            "Podu Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8800, 83.5100)),
            "Pedapatnam Beach" to BeachInfo("Andhra Pradesh", LatLng(17.8900, 83.5200)),
            // Tamil Nadu beaches
            "Marina Beach" to BeachInfo("Tamil Nadu", LatLng(13.0470, 80.2790)),
            "Elliot's Beach" to BeachInfo("Tamil Nadu", LatLng(12.9940, 80.2770)),
            "Mahabalipuram Beach" to BeachInfo("Tamil Nadu", LatLng(12.6190, 80.1830)),
            "Kovalam Beach" to BeachInfo("Tamil Nadu", LatLng(8.3946, 76.9858)),
            "Rameswaram Beach" to BeachInfo("Tamil Nadu", LatLng(9.2833, 79.1333)),
            "Kanyakumari Beach" to BeachInfo("Tamil Nadu", LatLng(8.4333, 77.5833)),
            "Velankanni Beach" to BeachInfo("Tamil Nadu", LatLng(10.8900, 79.5200)),
            "Poompuhar Beach" to BeachInfo("Tamil Nadu", LatLng(11.1516, 79.5100)),
            "Dhanushkodi Beach" to BeachInfo("Tamil Nadu", LatLng(9.2667, 79.1500)),
            "Kundukkal Beach" to BeachInfo("Tamil Nadu", LatLng(11.3040, 79.7150)),
            "Tiruchendur Beach" to BeachInfo("Tamil Nadu", LatLng(8.4800, 77.8000)),
            "Sathankulam Beach" to BeachInfo("Tamil Nadu", LatLng(8.6370, 77.8810)),
            "Thiruvanmiyur Beach" to BeachInfo("Tamil Nadu", LatLng(12.9900, 80.2450)),
            "Nagercoil Beach" to BeachInfo("Tamil Nadu", LatLng(8.1860, 77.4380)),
            "Valkarai Beach" to BeachInfo("Tamil Nadu", LatLng(10.6890, 79.4730)),
            "Sankaran Koil Beach" to BeachInfo("Tamil Nadu", LatLng(10.5380, 78.6300)),
            "Azhagiapandipuram Beach" to BeachInfo("Tamil Nadu", LatLng(8.3910, 77.9710)),
            "Muthukadu Beach" to BeachInfo("Tamil Nadu", LatLng(12.8510, 80.2770)),
            "Pallikaranai Beach" to BeachInfo("Tamil Nadu", LatLng(12.9080, 80.2080)),
            "Alamparai Beach" to BeachInfo("Tamil Nadu", LatLng(12.6410, 80.2080)),
            // Pondicherry beaches
            "Promenade Beach" to BeachInfo("Pondicherry", LatLng(11.9347, 79.8176)),
            "Karaikal Beach" to BeachInfo("Pondicherry", LatLng(10.9305, 79.8382)),
            "Yanam Beach" to BeachInfo("Pondicherry", LatLng(16.7333, 82.2167)),
            "Auroville Beach" to BeachInfo("Pondicherry", LatLng(12.0060, 79.8134)),
            "Paradise Beach" to BeachInfo("Pondicherry", LatLng(11.9393, 79.8113)),
            "Serenity Beach" to BeachInfo("Pondicherry", LatLng(11.9343, 79.8154)),
            // Andaman beaches
            "Radhanagar Beach" to BeachInfo("Andaman and Nicobar Islands", LatLng(11.4690, 92.6450)),
            "Bangaram Beach" to BeachInfo("Lakshadweep Islands", LatLng(10.5594, 72.6381)),
            "Kala Patthar Beach" to BeachInfo("Andaman and Nicobar Islands", LatLng(11.4016, 92.7262)),
            "Elephant Beach" to BeachInfo("Andaman and Nicobar Islands", LatLng(11.5510, 92.7463)),
            "Wandoor Beach" to BeachInfo("Andaman and Nicobar Islands", LatLng(11.6500, 92.6500))

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
