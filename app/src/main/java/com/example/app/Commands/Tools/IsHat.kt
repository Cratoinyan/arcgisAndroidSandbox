package com.example.app.Commands.Tools

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.MapView

class IsHat(private val context: Context, private val mapView: MapView, private val geodatabase: Geodatabase):ITool {
    private lateinit var featureTable:GeodatabaseFeatureTable
    private lateinit var featureLayer: FeatureLayer
    private val layerName = "Hat"

    override fun run() {
        TODO("Not yet implemented")
    }

    override val id = "Select Hat"

    override val onTouchListener= object : DefaultMapViewOnTouchListener(context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val newPoint = mapView.screenToLocation(android.graphics.Point(x, y))
                FindFeature(newPoint)
            }
            return true
        }
    }

    override fun Activate() {
        mapView.onTouchListener = onTouchListener

        mapView.map.operationalLayers.iterator().forEach { layer ->
            Log.i("LAYER",layer.name)
            if (layer.name == layerName) {
                featureLayer = layer as FeatureLayer
            }
        }
    }

    override fun Deactivate() {
        mapView.onTouchListener = DefaultMapViewOnTouchListener(context, mapView)
        featureLayer.clearSelection()
    }

    private fun FindFeature(point: Point){
        val extent = Envelope(point,30.0,30.0)

        featureTable = geodatabase.getGeodatabaseFeatureTable(layerName)
        val queryParams = QueryParameters()
        queryParams.spatialRelationship = QueryParameters.SpatialRelationship.INTERSECTS
        queryParams.geometry = extent
        val result = featureTable.queryFeaturesAsync(queryParams)

        result.addDoneListener {
            try{
                val resultIterator = result.get().iterator()
                if (resultIterator.hasNext()){
                    resultIterator.forEach { feature ->
                        featureLayer.clearSelection()
                        featureLayer.selectFeature(feature)
                    }
                    Log.i("RESULT",result.get().toString())
                    Toast.makeText(context,"There is a line here",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,"There is no line",Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception){
                Toast.makeText(context,"Feature search failed Error: ${e.message}",Toast.LENGTH_LONG).show()
            }
        }
    }
}