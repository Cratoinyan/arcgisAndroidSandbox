package com.example.app.UI

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.example.app.Data.Trafo
import com.example.app.MainActivity
import com.example.app.Managers.DBManager
import com.example.app.R
import java.util.*

class SaveTrafoPopUp(val context: Context,val dbManager: DBManager, val layout: ConstraintLayout, val pointGraphicsOverlay: GraphicsOverlay) {
    var popupWindow: PopupWindow? = null

    fun showPopUp(point: Point, activity: MainActivity){
        //get relevant layout
        val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.save_point_popup,null)

        //show popupwindow TODO: Take a look at bottom sheet dialog
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow = PopupWindow(view, width, height, true)
        popupWindow?.showAtLocation(layout, Gravity.BOTTOM,0,0)

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

            val id = dbManager.addTrafo(trafo)
            pointGraphicsOverlay.graphics.clear()
            popupWindow?.dismiss()
            activity.takeTrafoPhoto(id)
        }

        val cancelBtn = view.findViewById<Button>(R.id.cancel)
        cancelBtn.setOnClickListener {
            pointGraphicsOverlay.graphics.clear()
            popupWindow?.dismiss()
        }
    }

    fun dismiss(){
        popupWindow?.dismiss()
    }
}