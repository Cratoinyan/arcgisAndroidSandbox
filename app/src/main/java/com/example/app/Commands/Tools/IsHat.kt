package com.example.app.Commands.Tools

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.esri.arcgisruntime.data.FeatureTable
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.MapView
import android.graphics.Point as androidPoint

class IsHat(private val context: Context, private val mapView: MapView, private val geodatabase: Geodatabase):ITool {
    private lateinit var featureTable:GeodatabaseFeatureTable

    override fun run() {
        TODO("Not yet implemented")
    }

    override val id = "Find Feature"

    override val onTouchListener= object : DefaultMapViewOnTouchListener(context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val newPoint = mapView.screenToLocation(android.graphics.Point(x, y))
                FFeature(newPoint)
            }
            return true
        }
    }

    override fun Activate() {
        mapView.onTouchListener = onTouchListener
    }

    override fun Deactivate() {
        mapView.onTouchListener = DefaultMapViewOnTouchListener(context, mapView)
    }

    private fun FFeature(point: Point){
        val extent = Envelope(point,10.0,10.0)

        featureTable = geodatabase.getGeodatabaseFeatureTable("Hat")
        val queryParams = QueryParameters()
        queryParams.spatialRelationship = QueryParameters.SpatialRelationship.INTERSECTS
        queryParams.geometry = extent
        val result = featureTable.queryFeatureCountAsync(queryParams)

        result.addDoneListener {
            try{
                val resultSet = result.get()
                if (resultSet > 0){
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