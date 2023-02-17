package com.example.app.Data

import com.esri.arcgisruntime.geometry.Point
import java.io.Serializable
import java.util.*

class Trafo(var point: Point, var code:String, var name:String, var type:Short?, var date:Calendar, var field:String):Serializable{

}
