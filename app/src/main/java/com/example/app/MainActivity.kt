/*
 * Copyright 2020 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.app

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Point as androidPoint

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.geometry.Polyline
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.example.app.Commands.Tools.PointDrawer


import com.example.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit private var PointOnTouchListener: DefaultMapViewOnTouchListener
    lateinit private var PolyLineOnTouchListener: DefaultMapViewOnTouchListener
    lateinit private var PolygonOnTouchListener: DefaultMapViewOnTouchListener

    private val pointGraphicsOverlay = GraphicsOverlay()
    private val lineGraphicsOverlay = GraphicsOverlay()
    private val polygonGraphicsOverlay = GraphicsOverlay()

    private var pointList = PointCollection(SpatialReferences.getWebMercator())

    lateinit private var pointDrawer: PointDrawer

    lateinit private var toolManager: ToolManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)



        setContentView(activityMainBinding.root)
        val pointButton = findViewById<Button>(R.id.pointButton)
        val lineButton = findViewById<Button>(R.id.lineButton)
        val polygonButton = findViewById<Button>(R.id.polygonButton)



        pointDrawer = PointDrawer(this@MainActivity, mapView, pointButton)
        toolManager  = ToolManager(this@MainActivity, listOf(pointDrawer))

        pointButton.setOnClickListener{
            if (mapView.onTouchListener == PointOnTouchListener){
                mapView.onTouchListener = DefaultMapViewOnTouchListener(this@MainActivity,mapView)
                return@setOnClickListener
            }
            mapView.onTouchListener = PointOnTouchListener
        }

        lineButton.setOnClickListener{
            if (mapView.onTouchListener == PolyLineOnTouchListener){
                mapView.onTouchListener = DefaultMapViewOnTouchListener(this@MainActivity,mapView)
                return@setOnClickListener
            }
            pointList.clear()
            mapView.onTouchListener = PolyLineOnTouchListener
        }


        polygonButton.setOnClickListener{
            if (mapView.onTouchListener == PolygonOnTouchListener){
                mapView.onTouchListener = DefaultMapViewOnTouchListener(this@MainActivity,mapView)
                return@setOnClickListener
            }
            pointList.clear()
            mapView.onTouchListener= PolygonOnTouchListener
        }

        setApiKeyForApp()

        setupMap()

        mapView.graphicsOverlays.add(pointGraphicsOverlay)
        mapView.graphicsOverlays.add(lineGraphicsOverlay)
        mapView.graphicsOverlays.add(polygonGraphicsOverlay)

        PointOnTouchListener = object : DefaultMapViewOnTouchListener(this@MainActivity, mapView){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (e != null) {
                    val x = e.x.toInt()
                    val y = e.y.toInt()
                    val newPoint = mapView.screenToLocation(androidPoint(x,y))
                    addPoint(newPoint)
                }
                return true
            }
        }

        PolyLineOnTouchListener = object : DefaultMapViewOnTouchListener(this@MainActivity, mapView){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (e != null) {
                    val x = e.x.toInt()
                    val y = e.y.toInt()
                    val newPoint = mapView.screenToLocation(androidPoint(x,y))
                    addLine(newPoint)
                }
                return true
            }
        }

        PolygonOnTouchListener = object : DefaultMapViewOnTouchListener(this@MainActivity, mapView){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (e != null) {
                    val x = e.x.toInt()
                    val y = e.y.toInt()
                    val newPoint = mapView.screenToLocation(androidPoint(x,y))
                    addPolygon(newPoint)
                }
                return true
            }
        }

    }

    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
    }

    private fun addPoint(point: Point){
            val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)

            val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
            simpleMarkerSymbol.outline = blueOutlineSymbol

            // create a graphic with the point geometry and symbol
            val pointGraphic = Graphic(point, simpleMarkerSymbol)

            // add the point graphic to the graphics overlay
            pointGraphicsOverlay.graphics.add(pointGraphic)

    }

    private fun addLine(point: Point){
        pointList.add(point)
        if (pointList.size >= 2){

            var polyLine = Polyline(pointList)

            val polylineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 3f)
            val polylineGraphic = Graphic(polyLine, polylineSymbol)

            // add the polyline graphic to the graphics overlay
            lineGraphicsOverlay.graphics.add(polylineGraphic)
        }
    }

    private fun addPolygon(point: Point){
        pointList.add(point)

        polygonGraphicsOverlay.graphics.clear()

        if (pointList.size >= 3){

            var polygon = Polygon(pointList)

            val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
            val polygonSymbol = SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, -0x4cba03, blueOutlineSymbol)
            val polygonGraphic = Graphic(polygon, polygonSymbol)

            // add the polyline graphic to the graphics overlay
            polygonGraphicsOverlay.graphics.add(polygonGraphic)
        }
    }

    // set up your map here. You will call this method from onCreate()
    private fun setupMap() {

        // create a map with the BasemapStyle streets
        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC)

        // set the map to be displayed in the layout's MapView
        mapView.map = map
        // set the viewpoint, Viewpoint(latitude, longitude, scale)
        mapView.setViewpoint(Viewpoint(34.0270, -118.8050, 72000.0))

    }

    private fun setApiKeyForApp(){
        // set your API key
        // Note: it is not best practice to store API keys in source code. The API key is referenced
        // here for the convenience of this tutorial.

        ArcGISRuntimeEnvironment.setApiKey("AAPK81534aabc5ae4f7d9f09428664a6755a-Nx3w3r_hxOkhKWmdW8S9dyyDIKRRMWV18c0eiOn2q-XLkGDWdwfY_M694JkOmlk")

    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }
}

