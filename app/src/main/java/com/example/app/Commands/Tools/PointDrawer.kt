package com.example.app.Commands.Tools

import android.app.Activity
import android.content.Context
import android.util.Log
import android.graphics.Point as androidPoint
import android.view.MotionEvent
import android.widget.Button
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol

class PointDrawer(context: Context, mapView: MapView) :ITool {
    private var _context:Context = context
    private var _mapView:MapView = mapView
    override val onTouchListener = object : DefaultMapViewOnTouchListener(_context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d("point", "point deneme")
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val newPoint = mapView.screenToLocation(androidPoint(x,y))
                drawPoint(newPoint)
            }
            return true
        }
    }
    override lateinit var button: Button
    val pointGraphicsOverlay = GraphicsOverlay()

    init {
        _mapView.graphicsOverlays.add(pointGraphicsOverlay)
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    override val id: String
        get() = "pointTest"


    override fun Activate() {

        _mapView.onTouchListener = onTouchListener
    }

    override fun Deactivate() {
        _mapView.onTouchListener = DefaultMapViewOnTouchListener(_context,_mapView)
    }

    private fun drawPoint(point: Point){
       val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        // create a graphic with the point geometry and symbol
        val pointGraphic = Graphic(point, simpleMarkerSymbol)

        // add the point graphic to the graphics overlay
        pointGraphicsOverlay.graphics.add(pointGraphic)
    }
}