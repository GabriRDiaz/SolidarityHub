package com.upv.solidarityHub.ui.mapa

import android.app.AlertDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.*

class MapaGenericoFragment : Fragment() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var mapa: Mapa = Mapa()
    private lateinit var overlayBalizas: ArrayList<OverlayItem>
    private lateinit var overlayBalizasItemized: ItemizedOverlay<OverlayItem>
    private lateinit var buttonAddRecurso: ImageButton
    private var supabaseAPI: SupabaseAPI = SupabaseAPI()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mapa_generico, container, false)

        // Set up the view
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the map
        loadMap(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        mapa.getMap().onResume()
    }

    override fun onPause() {
        super.onPause()
        mapa.getMap().onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun loadMap(view: View) {
        mapa.setMap(view.findViewById(R.id.mapView))
        mapa.setMapController(mapa.getMap().controller as MapController)
        buttonAddRecurso = view.findViewById(R.id.botonIrRegistrarse)
        buttonAddRecurso.setOnClickListener {
            showAddRecursoDialog()
        }
        overlayBalizas = ArrayList()
        updateOverlay()
    }

    private fun addBaliza(baliza: Baliza?) {
        runBlocking {
            val deferred1 = async {
                baliza?.let { supabaseAPI.addBaliza(it.id, baliza.latitud, baliza.longitud, baliza.nombre, baliza.tipo, baliza.descripcion) }
            }
            deferred1.await()
        }
        updateOverlay()
    }

    private fun updateOverlay() {
        overlayBalizas.clear()
        var balizas: List<Baliza>? = null
        runBlocking {
            val deferred1 = async {
                balizas = supabaseAPI.getAllBalizas()
            }
            deferred1.await()
        }
        addAllBalizasToOverlay(balizas)
        mapa.getMap().overlays.clear()
        overlayBalizasItemized = ItemizedIconOverlay(overlayBalizas, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                item?.let {
                    AlertDialog.Builder(requireContext())
                        .setTitle(it.title)
                        .setMessage(it.snippet)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Eliminar") { _, _ ->

                            deleteBaliza(it)
                            Toast.makeText(
                                    activity,
                                    "Recurso eliminado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show()

                        }
                        .show()
                }
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        }, requireContext())
        mapa.getMap().overlays.add(overlayBalizasItemized)
        mapa.getMap().invalidate()
    }

    private fun deleteBaliza(it: OverlayItem) {
        runBlocking {
            val deferred1 = async {
                supabaseAPI.deleteBaliza(it.title.substringBefore(" (").trim())
            }
            deferred1.await()
        }
        updateOverlay()
    }

    private fun addAllBalizasToOverlay(balizas: List<Baliza>?) {
        balizas?.forEach { baliza ->
            val balizaOverlay = OverlayItem(
                "${baliza.nombre} (${baliza.tipo})",
                baliza.descripcion,
                GeoPoint(baliza.latitud, baliza.longitud)
            )
            overlayBalizas.add(balizaOverlay)
        }
    }

    private fun showAddRecursoDialog() {
        val (layout, nameEditText, descriptionEditText) = createLayout()
        val dialog = AlertDialog.Builder(activity)
            .setTitle("Añadir recurso")
            .setMessage("Ingresa el título y la descripción del recurso")
            .setView(layout)
            .setPositiveButton("Aceptar") { _, _ ->
                val name = nameEditText.text.toString()
                val tipo = "Recurso"
                val description = descriptionEditText.text.toString()
                if (name.isNotEmpty() && description.isNotEmpty()) {
                    addBalizaFromDialog(name, tipo, description)
                    Toast.makeText(activity, "Localización añadida: $name - $description", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Por favor, ingresa ambos campos.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun createLayout(): Triple<LinearLayout, EditText, EditText> {
        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL
        val nameEditText = EditText(activity)
        nameEditText.hint = "Título del recurso"
        layout.addView(nameEditText)
        val descriptionEditText = EditText(activity)
        descriptionEditText.hint = "Descripción del recurso"
        layout.addView(descriptionEditText)
        return Triple(layout, nameEditText, descriptionEditText)
    }

    private fun addBalizaFromDialog(name: String, tipo: String, description: String) {
        var balizas: List<Baliza>? = null
        runBlocking {
            val deferred1 = async {
                balizas = supabaseAPI.getAllBalizas()
            }
            deferred1.await()
        }
        var id = balizas?.size?.plus(1) as Int
        var baliza = Baliza(
            id,
            mapa.getMap().getMapCenter().latitude,
            mapa.getMap().getMapCenter().longitude,
            name,
            tipo,
            description
        )
        addBaliza(baliza)
    }
}