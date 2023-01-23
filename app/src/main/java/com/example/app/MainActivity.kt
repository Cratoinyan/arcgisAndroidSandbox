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
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.mapping.view.MapView
import com.example.app.Commands.Tools.SelectFeature
import com.example.app.Commands.Tools.LineDrawer
import com.example.app.Commands.Tools.PointDrawer
import com.example.app.Commands.Tools.PolygonDrawer
import com.example.app.Managers.MapManager
import com.example.app.Managers.ToolManager
import com.example.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val geoDatabasePath = "/sdcard/DATA/test.geodatabase"

    private lateinit var toolManager: ToolManager
    private lateinit var mapManager: MapManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        mapManager = MapManager(mapView,geoDatabasePath)
        mapManager.setupMap()

        val scrollView = HorizontalScrollView(this)
        val linearLayout = LinearLayout(this)
        scrollView.addView(linearLayout)

        val pointDrawer = PointDrawer(this@MainActivity, mapView)
        val lineDrawer = LineDrawer(this@MainActivity, mapView)
        val polygonDrawer = PolygonDrawer(this@MainActivity, mapView)
        val selectHat = SelectFeature(this@MainActivity,mapView,"Hat","Select Hat")
        val selectTrafo = SelectFeature(this@MainActivity,mapView,"Trafo","Select Trafo")
        val selectIstasyon = SelectFeature(this@MainActivity,mapView,"IstasyonAlani","Select Istasyon Alanı")


        activityMainBinding.layout.addView(scrollView)
        toolManager  = ToolManager(this@MainActivity, listOf(pointDrawer,lineDrawer,polygonDrawer,selectHat,selectTrafo,selectIstasyon),linearLayout)

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

