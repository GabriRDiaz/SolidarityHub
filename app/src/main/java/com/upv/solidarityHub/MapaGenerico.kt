package com.upv.solidarityHub

import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.*

class MapaGenerico : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private lateinit var mapController: MapController
    private lateinit var overlayBalizas: ArrayList<OverlayItem>
    private lateinit var overlayBalizasItemized: ItemizedOverlay<OverlayItem>
    private lateinit var buttonAddBaliza: ImageButton
    private lateinit var supabaseAPI: SupabaseAPI
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
        //Inicializar mapa
        loadMap()
        var beaconTest: OverlayItem = OverlayItem("Punto","Punto",GeoPoint(39.4703606,-0.3836834))
        addBaliza(beaconTest)
        


    }
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
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
    fun loadMap(){
        map = findViewById<MapView>(R.id.mapView)
        overlayBalizas = ArrayList<OverlayItem>()
        mapController = map.controller as MapController
        buttonAddBaliza = findViewById(R.id.button5)
        buttonAddBaliza.setOnClickListener {
            showAddBalizaDialog()
        }
        updateOverlay()
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        mapController.setZoom(20.0)
        mapController.setCenter(GeoPoint(39.4703606,-0.3836834))

    }
    fun addBaliza(baliza:OverlayItem){
        overlayBalizas.apply {
            add(
                baliza
            )
        }
        updateOverlay()
    }
    fun updateOverlay(){
            val balizas = supabaseAPI.getAllBalizas()
        map.overlays.clear()
        overlayBalizasItemized = ItemizedIconOverlay(overlayBalizas, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                item?.let {
                    AlertDialog.Builder(this@MapaGenerico)
                        .setTitle(it.title)
                        .setMessage(it.snippet) // Puedes usar snippet como descripción
                        .setPositiveButton("OK", null)
                        .show()
                }
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        }, applicationContext)
        map.overlays.add(overlayBalizasItemized)
        map.invalidate()
    }
    private fun showAddBalizaDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val nameEditText = EditText(this)
        nameEditText.hint = "Nombre de la baliza"
        layout.addView(nameEditText)
        val descriptionEditText = EditText(this)
        descriptionEditText.hint = "Descripción de la baliza"
        layout.addView(descriptionEditText)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Añadir baliza")
            .setMessage("Ingresa el nombre y la descripción de la baliza")
            .setView(layout)
            .setPositiveButton("Aceptar") { _, _ ->
                val name = nameEditText.text.toString()
                val description = descriptionEditText.text.toString()

                if (name.isNotEmpty() && description.isNotEmpty()) {
                    // Aquí puedes hacer lo que desees con el nombre y la descripción.
                    Toast.makeText(this, "Localización añadida: $name - $description", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor, ingresa ambos campos.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
}