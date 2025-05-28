package com.upv.solidarityHub.ui.mapa

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
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
                "${baliza.nombre} (${baliza.tipo} - ${baliza.tipo_recurso})",
                baliza.descripcion,
                GeoPoint(baliza.latitud, baliza.longitud)
            )
            val balizaDrawable = activity?.getDrawable(R.drawable.ic_marker_default)?.mutate()
            val color = when (baliza.tipo_recurso?.lowercase()) {
                "comida" -> Color.GREEN
                "producto de limpieza" -> Color.BLUE
                "material médico" -> Color.RED
                "artículos para bebés" -> Color.MAGENTA
                "artículos de primera necesidad" -> Color.YELLOW
                else -> Color.GRAY
            }
            balizaDrawable?.setTint(color)
            balizaDrawable?.setBounds(0,0,32,32)
            balizaOverlay.setMarker(balizaDrawable)
            overlayBalizas.add(balizaOverlay)
        }
    }

    private fun showAddRecursoDialog() {
        val layoutAddRecurso  = createLayout()

        val dialog = AlertDialog.Builder(activity)
            .setTitle("Añadir recurso")
            .setMessage("Ingresa el título y la descripción del recurso")
            .setView(layoutAddRecurso.layout)
            .setPositiveButton("Aceptar") { _, _ ->
                val name = layoutAddRecurso.nameEditText.text.toString()
                val tipo = "Recurso"
                val description = layoutAddRecurso.descriptionEditText.text.toString()
                val tipoRecurso = layoutAddRecurso.comboBoxTipoRecurso.selectedItem.toString()
                if (name.isNotEmpty() && description.isNotEmpty()) {
                    addBalizaFromDialog(name, tipo, description, tipoRecurso)
                    Toast.makeText(activity, "Localización añadida: $name - $description - $tipoRecurso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Por favor, ingresa ambos campos.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun createLayout(): LayoutAddRecurso{
        val layoutAddRecurso = LayoutAddRecurso()
        layoutAddRecurso.layout = LinearLayout(activity)
        layoutAddRecurso.layout.orientation = LinearLayout.VERTICAL
        layoutAddRecurso.nameEditText = EditText(activity)
        layoutAddRecurso.nameEditText.hint = "Título del recurso"
        layoutAddRecurso.layout.addView(layoutAddRecurso.nameEditText)
        layoutAddRecurso.descriptionEditText = EditText(activity)
        layoutAddRecurso.descriptionEditText.hint = "Descripción del recurso"
        layoutAddRecurso.layout.addView(layoutAddRecurso.descriptionEditText)
        layoutAddRecurso.comboBoxTipoRecurso = Spinner(activity)
        val opcionTiposRecurso = listOf("Producto de limpieza","Comida","Material médico","Artículos para bebés","Artículos de primera necesidad")
        val adapter = ArrayAdapter(activity as Context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, opcionTiposRecurso)
        layoutAddRecurso.comboBoxTipoRecurso.adapter = adapter
        layoutAddRecurso.layout.addView(layoutAddRecurso.comboBoxTipoRecurso)
        return layoutAddRecurso
    }

    private fun addBalizaFromDialog(name: String, tipo: String, description: String, tipoRecurso: String) {
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
            description,
            tipoRecurso
        )
        addBaliza(baliza)
    }
}