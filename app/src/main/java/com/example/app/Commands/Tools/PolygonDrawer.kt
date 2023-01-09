package com.example.app.Commands.Tools

import android.content.Context
import android.view.MotionEvent
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol

class PolygonDrawer(private val context: Context, private val mapView: MapView): ITool {
    private val graphicsOverlay = GraphicsOverlay()
    private var pointList = PointCollection(SpatialReferences.getWebMercator())
    override val id = "Add Polygon"

    init {
        mapView.graphicsOverlays.add(graphicsOverlay)
    }

    override val onTouchListener = object : DefaultMapViewOnTouchListener(context,mapView){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val newPoint = mapView.screenToLocation(android.graphics.Point(x, y))
                drawPolygon(newPoint)
            }
            return true
        }
    }

    override fun Activate() {
        pointList.clear()
        mapView.onTouchListener = onTouchListener
    }

    override fun Deactivate() {
        mapView.onTouchListener = DefaultMapViewOnTouchListener(context,mapView)
    }
    override fun run() {
        TODO("Not yet implemented")
    }

    private fun drawPolygon(point: Point){
        pointList.add(point)

        graphicsOverlay.graphics.clear()

        if (pointList.size >= 3){

            var polygon = Polygon(pointList)
            if(GeometryEngine.isSimple(polygon))
            {
            }
            else{
                pointList.remove(point)
                polygon = Polygon(pointList)
            }
            val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
            val polygonSymbol = SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, -0x4cba03, blueOutlineSymbol)
            val polygonGraphic = Graphic(polygon, polygonSymbol)

            // add the polyline graphic to the graphics overlay
            graphicsOverlay.graphics.add(polygonGraphic)

        }
    }
}