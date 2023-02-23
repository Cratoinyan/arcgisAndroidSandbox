package com.example.app.Managers

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.util.Log
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.internal.jni.CoreFeature
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView
import com.example.app.Data.SQLiteDB
import com.example.app.Data.Trafo

class DBManager(val geodatabasePath:String, val context: Context){
    lateinit var geoDataBase:Geodatabase
    lateinit var sqLiteDB: SQLiteDB
    private val trafoLayer = "Trafo"
    var activeDB = 0
    init {
        sqLiteDB = SQLiteDB(context)
    }

    fun switchDB(map: MapView){
        if(activeDB == 0){
            activeDB = 1
            map.map.operationalLayers.clear()
            loadFromSQLite(map)
        }
        else{
            activeDB = 0
            map.graphicsOverlays.clear()
            loadDB(map.map)
        }
    }

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

    fun addTrafo(trafo: Trafo):Long{
        if (activeDB == 0){
            addTrafoToGeoDB(trafo)
        }
        else{
            return addTrafoToSQLite(trafo)
        }
        return -1
    }

    fun addTrafoToGeoDB(trafo: Trafo){
        val attribute = mapOf(
            "adi" to trafo.name,
            "field" to trafo.field,
            "kodu" to trafo.code,
            "tipi" to trafo.type,
            "uretimtarihi" to trafo.date)

        val featureTable =  geoDataBase.getGeodatabaseFeatureTable(trafoLayer)
        val template = featureTable.featureTemplates
        var feature = featureTable.createFeature(attribute,trafo.point)

        Log.i("HERE",template.toString())//trafo attribute keys[adi,field,kodu,objectid(db will handle this),tipi,uretimtarihi]

        val future = featureTable.addFeatureAsync(feature)

        future.addDoneListener {
            Log.i("HERE",feature.attributes.toString())
        }
    }

    fun loadFromSQLite(map: MapView){
        sqLiteDB.loadTrafo(map)
    }

    fun addTrafoToSQLite(trafo:Trafo):Long{
        return sqLiteDB.addTrafo(trafo)
    }

    fun createSQLite(path: String){

    }
}