package com.example.app.Commands.Tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.MapView

class SelectFeature(private val context: Context, private val mapView: MapView, private val layerName:String, override val id:String):ITool {
    private lateinit var featureLayer: FeatureLayer

    override fun run() {
        TODO("Not yet implemented")
    }

    override val onTouchListener= object : DefaultMapViewOnTouchListener(context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val newPoint = mapView.screenToLocation(android.graphics.Point(x, y))
                selectFeature(newPoint)
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

    private fun selectFeature(point: Point){
        val extent = Envelope(point,30.0,30.0)

        val selectedFeatures = featureLayer.selectedFeaturesAsync
        val queryParams = QueryParameters()
        queryParams.spatialRelationship = QueryParameters.SpatialRelationship.INTERSECTS
        queryParams.geometry = extent
        queryParams.maxFeatures = 1
        val result = featureLayer.selectFeaturesAsync(queryParams,FeatureLayer.SelectionMode.NEW)

        result.addDoneListener {
            try{
                val resultIterator = result.get().iterator()
                if (resultIterator.hasNext()){
                    Toast.makeText(context,"There is a ${layerName} here",Toast.LENGTH_SHORT).show()
                    resultIterator.forEach { feature ->
                        Toast.makeText(context,feature.attributes["name"].toString(),Toast.LENGTH_SHORT)
                    }
                }
                else{
                       Toast.makeText(context,"There is no ${layerName}",Toast.LENGTH_LONG).show()
                    featureLayer.selectFeatures(selectedFeatures.get())
                }
            }catch (e: Exception){
                Toast.makeText(context,"Feature search failed Error: ${e.message}",Toast.LENGTH_LONG).show()
            }
        }
    }
}