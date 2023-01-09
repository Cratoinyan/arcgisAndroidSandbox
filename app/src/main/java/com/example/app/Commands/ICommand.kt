package com.example.app.Commands

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener

interface ICommand {
    fun run()
    val id: String
    val onTouchListener: DefaultMapViewOnTouchListener
}