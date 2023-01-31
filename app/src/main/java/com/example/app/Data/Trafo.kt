package com.example.app.Data

import com.esri.arcgisruntime.geometry.Point
import java.util.*

data class Trafo(var point: Point, var code:String, var name:String, var type:Short?, var date:Calendar, var field:String)
