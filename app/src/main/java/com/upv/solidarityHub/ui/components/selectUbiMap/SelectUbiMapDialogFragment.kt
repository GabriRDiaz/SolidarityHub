package com.upv.solidarityHub.ui.components.selectUbiMap

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem


class SelectUbiMapDialogFragment : DialogFragment() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var mapController: MapController
    private lateinit var overlayBalizas: ArrayList<OverlayItem>
    private lateinit var overlayBalizasItemized: ItemizedOverlay<OverlayItem>
    private lateinit var buttonAddRecurso: ImageButton
    private var supabaseAPI: SupabaseAPI = SupabaseAPI()

    interface DialogListener {
        fun onDialogUbi(baliza: Baliza)
    }

    private var listener: DialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mapa_generico, container, false)
        loadMap(view)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure that the host fragment implements the callback interface
        try {
            listener = parentFragment as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DialogListener")
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()

    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun loadMap(view: View) {
        try {
            map = view.findViewById(R.id.mapView)
            overlayBalizas = ArrayList()
            mapController = map.controller as MapController
            buttonAddRecurso = view.findViewById(R.id.botonIrRegistrarse)
            buttonAddRecurso.setOnClickListener {
                //TODO: Malísimo, se supone que supabase pone el ID de forma automática no hace falta hacer esto. CAMBIAR!!!
                var balizas:List<Baliza>? = null
                runBlocking {
                    val deferred1 = async {
                        balizas = supabaseAPI.getAllBalizas()}
                    deferred1.await()
                }
                var id = balizas?.size?.plus(1) as Int
                var baliza = Baliza(id,map.getMapCenter().latitude,map.getMapCenter().longitude,"","","",null)
                listener!!.onDialogUbi(baliza)
                Toast.makeText(activity,"Ubicación añadida", Toast.LENGTH_LONG).show()

                dismiss()
            }
            updateOverlay()
            map.setTileSource(TileSourceFactory.MAPNIK)
            map.setMultiTouchControls(true)
            mapController.setZoom(20.0)
            mapController.setCenter(GeoPoint(39.4703606, -0.3836834))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error en loadMap: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun addBaliza(baliza: Baliza?) {
        runBlocking {
            val deferred1 = async {
                baliza?.let { supabaseAPI.addBaliza(it.id, baliza.latitud, baliza.longitud, baliza.nombre, baliza.tipo, baliza.descripcion, baliza.tipo_recurso) }
            }
            deferred1.await()
        }
        updateOverlay()
    }

    private fun updateOverlay() {
        var balizas: List<Baliza>? = null
        runBlocking {
            val deferred1 = async {
                balizas = supabaseAPI.getAllBalizas()
            }
            deferred1.await()
        }

        balizas?.forEach { baliza ->
            val balizaOverlay = OverlayItem("${baliza.nombre} (${baliza.tipo})", baliza.descripcion, GeoPoint(baliza.latitud, baliza.longitud))
            overlayBalizas.add(balizaOverlay)
        }

        map.overlays.clear()
        overlayBalizasItemized = ItemizedIconOverlay(overlayBalizas, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                item?.let {
                    AlertDialog.Builder(requireContext())
                        .setTitle(it.title)
                        .setMessage(it.snippet)
                        .setPositiveButton("OK", null)
                        .show()
                }
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        }, requireContext())
        map.overlays.add(overlayBalizasItemized)
        map.invalidate()
    }
    private fun showAddRecursoDialog() {
        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL
        val nameEditText = EditText(activity)
        nameEditText.hint = "Título del recurso"
        layout.addView(nameEditText)
        val descriptionEditText = EditText(activity)
        descriptionEditText.hint = "Descripción del recurso"
        layout.addView(descriptionEditText)
        val dialog = AlertDialog.Builder(activity)
            .setTitle("Añadir recurso")
            .setMessage("Ingresa el título y la descripción del recurso")
            .setView(layout)
            .setPositiveButton("Aceptar") { _, _ ->
                val name = nameEditText.text.toString()
                val tipo = "Recurso"
                val description = descriptionEditText.text.toString()

                if (name.isNotEmpty() && description.isNotEmpty()) {
                    var balizas:List<Baliza>? = null
                    runBlocking {
                        val deferred1 = async {
                            balizas = supabaseAPI.getAllBalizas()}
                        deferred1.await()
                    }
                    var id = balizas?.size?.plus(1) as Int
                    var baliza= Baliza(id,map.getMapCenter().latitude,map.getMapCenter().longitude,name,tipo,description,null)
                    addBaliza(baliza)
                    Toast.makeText(activity, "Localización añadida: $name - $description", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Por favor, ingresa ambos campos.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
}