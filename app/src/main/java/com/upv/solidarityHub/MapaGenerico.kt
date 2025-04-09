package com.upv.solidarityHub

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.*
import org.osmdroid.api.IGeoPoint
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapaGenerico : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private lateinit var mapController: MapController
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_mapa_generico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        map = findViewById<MapView>(R.id.mapView)
        map.setMultiTouchControls(true)
        mapController = map.controller as MapController
        mapController.setZoom(20.0)
        var mylocation : MyLocationNewOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext),map)
        mylocation.enableMyLocation()

        //var puntero : GeoPoint = GeoPoint(mylocation.myLocation)

        mapController.setCenter(GeoPoint(39.4703606,-0.3836834))
        map.setTileSource(TileSourceFactory.MAPNIK)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var overlay: OverlayItem = OverlayItem("Punto","Punto",GeoPoint(39.4703606,-0.3836834))

        val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.baseline_add_location_alt_24)
        val items = ArrayList<OverlayItem>().apply {
            add(
                overlay
            )
        }
        var overlayyy: ItemizedOverlay<OverlayItem> = ItemizedIconOverlay<OverlayItem>(items, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                Toast.makeText(this@MapaGenerico, "Pulsaste: ${item?.title}", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        },
            applicationContext)
        map.overlays.add(overlayyy)
    }
    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }
}