package com.example.app.Commands

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.example.app.Managers.DBManager
import com.example.app.Managers.MapManager

class SwitchDB(var dbManager: DBManager, var mapManager: MapManager):ICommand {
    override fun run() {
        dbManager.switchDB(mapManager.mapView)
    }

    override val id: String = "Switch DB"
    override val onTouchListener: DefaultMapViewOnTouchListener
        get() = TODO("Not yet implemented")
}