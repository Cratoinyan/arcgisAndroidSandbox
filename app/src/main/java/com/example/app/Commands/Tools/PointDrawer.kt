package com.example.app.Commands.Tools

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.example.app.Data.Trafo
import com.example.app.Managers.DBManager
import com.example.app.R
import java.lang.Math.floor
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Point as androidPoint

class PointDrawer(private var context: Context, private var mapView: MapView, val dbManager: DBManager, val layout: ConstraintLayout) :ITool {
    private val pointGraphicsOverlay = GraphicsOverlay()
    override val id = "Add Point"
    private lateinit var point: Point
    private var popupWindow: PopupWindow? = null

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
        popupWindow?.dismiss()
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    private fun drawPoint(point: Point){
        //clear screen
        pointGraphicsOverlay.graphics.clear()
        popupWindow?.dismiss()

        val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        // create a graphic with the point geometry and symbol
        val pointGraphic = Graphic(point, simpleMarkerSymbol)

        // add the point graphic to the graphics overlay
        pointGraphicsOverlay.graphics.add(pointGraphic)

        //pop up menu to ask additional information about the point
        popUpForm()
    }

    private fun popUpForm(){
        //get relevant layout
        val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.save_point_popup,null)

        //show popupwindow
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow = PopupWindow(view, width, height, true)
        popupWindow?.showAtLocation(layout,Gravity.BOTTOM,0,0)

        val code = view.findViewById<EditText>(R.id.code_input)
        val name = view.findViewById<EditText>(R.id.name_input)
        val type = view.findViewById<EditText>(R.id.type_input)
        val field = view.findViewById<EditText>(R.id.field_input)
        val datePicker = view.findViewById<DatePicker>(R.id.date_input)

        //set button events to save to db or close the popup
        val saveBtn = view.findViewById<Button>(R.id.popup_close_button)
        saveBtn.setOnClickListener {
            //get the date
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val year = datePicker.year

            val cal = Calendar.getInstance()
            cal.set(year,month,day)

            //prepare trafo
            val trafo = Trafo(point,
                code.text.toString(),
                name.text.toString(),
                type.text.toString().toShortOrNull(),
                cal,
                field.text.toString())

            dbManager.addTrafo(trafo)
            pointGraphicsOverlay.graphics.clear()
            popupWindow?.dismiss()
        }

        val cancelBtn = view.findViewById<Button>(R.id.cancel)
        cancelBtn.setOnClickListener {
            pointGraphicsOverlay.graphics.clear()
            popupWindow?.dismiss()
        }
    }
}