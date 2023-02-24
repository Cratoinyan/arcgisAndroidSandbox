package com.example.app.Commands.Tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.example.app.MainActivity
import com.example.app.Managers.DBManager
import com.example.app.UI.SaveTrafoPopUp
import com.google.android.gms.location.LocationServices
import android.graphics.Point as androidPoint

class PointDrawer(private var context: Context, private var mapView: MapView, val dbManager: DBManager, val layout: ConstraintLayout,var activity:MainActivity) :ITool {
    private val pointGraphicsOverlay = GraphicsOverlay()
    override val id = "Add Point"
    private lateinit var point: Point
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val saveTrafoPopUp = SaveTrafoPopUp(context,dbManager,layout, pointGraphicsOverlay)
    init {
        mapView.graphicsOverlays.add(pointGraphicsOverlay)
    }

    override val onTouchListener = object : DefaultMapViewOnTouchListener(this.context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d("point", "point deneme")
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                point = mapView.screenToLocation(androidPoint(x,y))
                drawPointIfValid(point)
            }
            return true
        }
    }

    override fun Activate() {

        mapView.onTouchListener = onTouchListener
    }

    override fun Deactivate() {
        mapView.onTouchListener = DefaultMapViewOnTouchListener(context,mapView)
        pointGraphicsOverlay.graphics.clear()
        saveTrafoPopUp.dismiss()
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    private fun drawPoint(point: Point){
        //clear screen
        pointGraphicsOverlay.graphics.clear()
        saveTrafoPopUp.dismiss()

        val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        // create a graphic with the point geometry and symbol
        val pointGraphic = Graphic(point, simpleMarkerSymbol)

        // add the point graphic to the graphics overlay
        pointGraphicsOverlay.graphics.add(pointGraphic)

        //pop up menu to ask additional information about the point
        saveTrafoPopUp.showPopUp(point,activity)
    }

    @SuppressLint("MissingPermission")
    private fun drawPointIfValid(point: Point){
        var extent:Envelope
        locationClient.lastLocation.addOnSuccessListener { location ->
            //convert location from mercator(4326) to webmercator(3857) before creating the extent to avoid conflicts
            val lPoint = Point(location.longitude,location.latitude, SpatialReference.create(4326))
            val plPoint = GeometryEngine.project(lPoint, SpatialReference.create(3857)) as Point

            extent = Envelope(plPoint,400.0,400.0)

            if((point.x > extent.xMin && point.x < extent.xMax) &&
                (point.y > extent.yMin && point.y < extent.yMax))
                    drawPoint(point)
            else{
                Toast.makeText(context,"That point is too far away from your location",Toast.LENGTH_SHORT).show()
            }
        }
    }
}