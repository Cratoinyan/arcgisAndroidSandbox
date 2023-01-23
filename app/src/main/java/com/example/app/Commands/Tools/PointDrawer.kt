package com.example.app.Commands.Tools

import android.content.Context
import android.util.Log
import android.graphics.Point as androidPoint
import android.view.MotionEvent
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.example.app.Managers.DBManager

class PointDrawer(private var context: Context, private var mapView: MapView, val dbManager: DBManager) :ITool {
    private val pointGraphicsOverlay = GraphicsOverlay()
    override val id = "Add Point"
    private lateinit var point: Point

    init {
        this.mapView.graphicsOverlays.add(pointGraphicsOverlay)
    }

    override val onTouchListener = object : DefaultMapViewOnTouchListener(this.context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d("point", "point deneme")
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                point = mapView.screenToLocation(androidPoint(x,y))
                drawPoint(point)
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
        dbManager.addTrafo(point)
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    private fun drawPoint(point: Point){
        pointGraphicsOverlay.graphics.clear()

       val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        // create a graphic with the point geometry and symbol
        val pointGraphic = Graphic(point, simpleMarkerSymbol)

        // add the point graphic to the graphics overlay
        pointGraphicsOverlay.graphics.add(pointGraphic)
    }
}