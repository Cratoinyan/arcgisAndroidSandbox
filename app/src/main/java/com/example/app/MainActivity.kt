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
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.mapping.view.MapView
import com.example.app.Commands.SwitchDB
import com.example.app.Commands.Tools.SelectFeature
import com.example.app.Commands.Tools.LineDrawer
import com.example.app.Commands.Tools.PointDrawer
import com.example.app.Commands.Tools.PolygonDrawer
import com.example.app.Managers.DBManager
import com.example.app.Managers.MapManager
import com.example.app.Managers.ToolManager
import com.example.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val geoDatabasePath = "/sdcard/DATA/test.geodatabase"
    private val cameraRequest = 1888
    private var lastTrafoId: Long? = null

    private lateinit var toolManager: ToolManager
    private lateinit var mapManager: MapManager
    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        val pd = PackageManager.PERMISSION_DENIED

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == pd || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == pd ){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequest)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 3421)

        }

        dbManager = DBManager(geoDatabasePath,this)
        mapManager = MapManager(mapView, dbManager)
        mapManager.setupMap()

        val scrollView = HorizontalScrollView(this)
        val linearLayout = LinearLayout(this)
        scrollView.addView(linearLayout)

        val pointDrawer = PointDrawer(this@MainActivity, mapView, dbManager, activityMainBinding.layout, this)
        val lineDrawer = LineDrawer(this@MainActivity, mapView)
        val polygonDrawer = PolygonDrawer(this@MainActivity, mapView)
        val selectHat = SelectFeature(this@MainActivity,mapView,"Hat","Select Hat")
        val selectTrafo = SelectFeature(this@MainActivity,mapView,"Trafo","Select Trafo")
        val selectIstasyon = SelectFeature(this@MainActivity,mapView,"IstasyonAlani","Select Istasyon Alanı")
        val switchDB = SwitchDB(dbManager,mapManager)


        activityMainBinding.layout.addView(scrollView)
        toolManager  = ToolManager(this@MainActivity, listOf(pointDrawer,lineDrawer,polygonDrawer,selectHat,selectTrafo,selectIstasyon,switchDB),linearLayout)

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

    fun takeTrafoPhoto(id:Long){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra("com.example.app.id",id)
        lastTrafoId = id
        Log.i("hello",id.toString())
        startActivityForResult(cameraIntent,cameraRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("hello","req code ="+requestCode + " cam code=" + cameraRequest)
        if (requestCode == cameraRequest && resultCode == Activity.RESULT_OK) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
//            val id = data?.extras?.get("id") as Long
            val msg = data.getLongExtra("com.example.app.id",-1).toString()
            if (msg != null) {
                Log.i("hello",msg + " " + photo.toString())
            }
            if(lastTrafoId != null){
                val id:Long = lastTrafoId!!.toLong()
                dbManager.sqLiteDB.updateTrafoImg(id, photo)
            }
        }
    }
}

