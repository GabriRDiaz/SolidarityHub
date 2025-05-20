package com.upv.solidarityHub.ui.mapa

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView

class Mapa() {
    private lateinit var map: MapView
    private lateinit var mapController: MapController
    private val ANGEL_GUIMERA_LOCATION = GeoPoint(39.4703606, -0.3836834)

    fun getMap(): MapView {
        return map
    }

    fun setMap(map: MapView) {
        this.map = map
        this.map.setTileSource(TileSourceFactory.MAPNIK)
        this.map.setMultiTouchControls(true)
    }

    fun getMapController(): MapController {
        return mapController
    }

    fun setMapController(mapController: MapController) {
        this.mapController = mapController
        this.mapController.setZoom(20.0)
        this.mapController.setCenter(this.ANGEL_GUIMERA_LOCATION)
    }

    fun getAngelGuimeraLocation(): GeoPoint {
        return ANGEL_GUIMERA_LOCATION
    }
}
