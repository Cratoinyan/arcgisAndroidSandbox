package com.example.app.Commands

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import android.widget.Button

interface ICommand {
    fun run()
    val id: String
    val onTouchListener: DefaultMapViewOnTouchListener
    var button: Button
}