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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.data.FeatureTable
import com.esri.arcgisruntime.data.Geodatabase
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import com.example.app.Commands.Tools.IsHat
import com.example.app.Commands.Tools.LineDrawer
import com.example.app.Commands.Tools.PointDrawer
import com.example.app.Commands.Tools.PolygonDrawer
import com.example.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var pointDrawer: PointDrawer
    private lateinit var lineDrawer: LineDrawer
    private lateinit var polygonDrawer: PolygonDrawer
    private lateinit var isHat: IsHat
    private val geodatabasePath = "/sdcard/DATA/test.geodatabase"
    private lateinit var geodatabase: Geodatabase

    private lateinit var toolManager: ToolManager
    private lateinit var mapManager: MapManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        mapManager = MapManager(mapView,geodatabasePath)
        mapManager.setupMap()

        val linearLayout = LinearLayout(this)

        pointDrawer = PointDrawer(this@MainActivity, mapView)
        lineDrawer = LineDrawer(this@MainActivity, mapView)
        polygonDrawer = PolygonDrawer(this@MainActivity, mapView)
        isHat = IsHat(this@MainActivity,mapView, mapManager.geodatabase)

        activityMainBinding.layout.addView(linearLayout)
        toolManager  = ToolManager(this@MainActivity, listOf(pointDrawer,lineDrawer,polygonDrawer,isHat),linearLayout)

        toolManager.Initialize()
        
    }

    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
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

