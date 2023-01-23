package com.example.app.Managers

import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.internal.jni.CoreFeature
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap

class DBManager(val geodatabasePath:String) {
    public lateinit var geoDataBase:Geodatabase
    private val trafoLayer = "Trafo"

    fun loadDB(map: ArcGISMap){
        // instantiate geoDataBase with the path to the .geodatabase file
        geoDataBase = Geodatabase(geodatabasePath)

        // load the geoDataBase
        geoDataBase.loadAsync()

        geoDataBase.addDoneLoadingListener {

            if (geoDataBase.loadStatus == LoadStatus.LOADED) {
                for (featureTable in geoDataBase.geodatabaseFeatureTables){
                    val featureLayer = FeatureLayer(featureTable)
                    map.operationalLayers.add(featureLayer)
                }

            }
        }
    }

    fun addTrafo(point: Point){
        val featureTable =  geoDataBase.getGeodatabaseFeatureTable(trafoLayer)
        var feature = featureTable.createFeature()
        feature.geometry = point

        featureTable.addFeatureAsync(feature)
    }
}