package com.example.app.Commands.Tools

import android.content.Context
import android.view.MotionEvent
import android.widget.Button
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol

class LineDrawer(private val context: Context, private val mapView: MapView):ITool {
    private var pointList = PointCollection(SpatialReferences.getWebMercator())
    private val graphicsOverlay = GraphicsOverlay()
    override val id = "Add Line"

    init {
        mapView.graphicsOverlays.add(graphicsOverlay)
    }

    override val onTouchListener = object : DefaultMapViewOnTouchListener(context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val newPoint = mapView.screenToLocation(android.graphics.Point(x, y))
                drawLine(newPoint)
            }
            return true
        }
    }

    override fun Activate() {
        pointList.clear()
        mapView.onTouchListener = onTouchListener
    }

    override fun Deactivate() {
        mapView.onTouchListener = DefaultMapViewOnTouchListener(context, mapView)
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    private fun drawLine(point: Point){

        pointList.add(point)
        if (pointList.size >= 2){

            var polyLine = Polyline(pointList)

            val polylineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 3f)
            val polylineGraphic = Graphic(polyLine, polylineSymbol)

            // add the polyline graphic to the graphics overlay
            graphicsOverlay.graphics.add(polylineGraphic)
        }
    }
}