package com.example.app

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView

class MapManager(var mapView: MapView, val geodatabasePath:String) {

    lateinit var geoDataBase: Geodatabase

    // set up your map here. You will call this method from onCreate()
    fun setupMap() {
        setApiKeyForApp()

        // create a map with the BasemapStyle streets
        val map = ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY_BASE)

        // set the map to be displayed in the layout's MapView
        mapView.map = map
        // set the viewpoint, Viewpoint(latitude, longitude, scale)
        mapView.setViewpoint(Viewpoint(39.9334, 32.8597, 200000.0))

        loadDB()
    }

    private fun setApiKeyForApp(){
        // set your API key
        // Note: it is not best practice to store API keys in source code. The API key is referenced
        // here for the convenience of this tutorial.

        ArcGISRuntimeEnvironment.setApiKey("AAPK81534aabc5ae4f7d9f09428664a6755a-Nx3w3r_hxOkhKWmdW8S9dyyDIKRRMWV18c0eiOn2q-XLkGDWdwfY_M694JkOmlk")
    }

    private fun loadDB(){
        // instantiate geoDataBase with the path to the .geodatabase file
        geoDataBase = Geodatabase(geodatabasePath)

        // load the geoDataBase
        geoDataBase.loadAsync()

        geoDataBase.addDoneLoadingListener {

            if (geoDataBase.loadStatus == LoadStatus.LOADED) {
                for (featureTable in geoDataBase.geodatabaseFeatureTables){
                    val featureLayer = FeatureLayer(featureTable)
                    mapView.map.operationalLayers.add(featureLayer)
                }
            }
        }
    }
}