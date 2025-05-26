package com.upv.solidarityHub.ui.mapa

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
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
import org.osmdroid.views.overlay.*


class MapaDesaparecidos : DialogFragment() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var mapa: Mapa = Mapa()
    private lateinit var overlayBalizas: ArrayList<OverlayItem>
    private lateinit var overlayBalizasItemized: ItemizedOverlay<OverlayItem>
    private var supabaseAPI: SupabaseAPI = SupabaseAPI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mapa_desaparecidos, container, false)

        // Set up the view
        //enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the map
        loadMap(view)

        return view
    }
    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onResume() {
        super.onResume()
        mapa.getMap().onResume()
    }

    override fun onPause() {
        super.onPause()
        mapa.getMap().onPause()
    }

    private fun loadMap(view: View) {
        try {
            mapa.setMap(view.findViewById(R.id.mapView))
            overlayBalizas = ArrayList()
            mapa.setMapController(mapa.getMap().controller as MapController)
            updateOverlay()
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
        overlayBalizas.clear()
        var balizas: List<Baliza>? = null
        runBlocking {
            val deferred1 = async {
                balizas = supabaseAPI.getAllBalizas()
            }
            deferred1.await()
        }

        balizas?.forEach { baliza ->
            val balizaOverlay = OverlayItem("${baliza.nombre} (${baliza.tipo})", baliza.descripcion, GeoPoint(baliza.latitud, baliza.longitud))
            if (baliza.tipo == "Desaparecido") overlayBalizas.add(balizaOverlay)
        }

        mapa.getMap().overlays.clear()
        overlayBalizasItemized = ItemizedIconOverlay(overlayBalizas, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                item?.let {
                    AlertDialog.Builder(requireContext())
                        .setTitle(it.title)
                        .setMessage(it.snippet)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Eliminar") { _, _ ->

                            runBlocking {
                                val deferred1 = async {
                                    supabaseAPI.deleteBaliza(it.title.substringBefore(" (").trim())
                                }
                                deferred1.await()
                            }

                            Toast.makeText(
                                activity,
                                "Desaparecido eliminado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateOverlay()
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

}