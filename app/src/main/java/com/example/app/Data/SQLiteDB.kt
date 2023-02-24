package com.example.app.Data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.*
import java.io.ByteArrayOutputStream
import java.util.*

class SQLiteDB(val context: Context):SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION) {
    companion object{
        private val DB_VERSION = 1
        private val DB_NAME = "/sdcard/DATA/test.sqlite"
        private val TRAFO_TABLE_NAME = "trafo"
        private val COL_KEY = "ID"
        private val COL_IMG = "IMG"
        private val COL_SHAPE = "SHAPE"
        private val COL_NAME = "NAME"
        private val COL_CODE = "CODE"
        private val COL_FIELD = "FIELD"
        private val COL_TYPE = "TYPE"
        private val COL_DATE = "DATE"

        var trafoImgGraphicsOverlay = GraphicsOverlay()
        var trafoGraphicsOverlay = GraphicsOverlay()
    }

    init {
        trafoImgGraphicsOverlay.minScale = 2000.0
        trafoGraphicsOverlay.maxScale = 2000.0

        val symbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
        symbol.outline = blueOutlineSymbol

        trafoGraphicsOverlay.renderer = SimpleRenderer(symbol)
        trafoImgGraphicsOverlay.renderer = SimpleRenderer(symbol)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE "+ TRAFO_TABLE_NAME + "(" +
                "$COL_KEY INTEGER PRIMARY KEY," +
                "$COL_SHAPE text," +
                "$COL_NAME text," +
                "$COL_CODE text," +
                "$COL_FIELD text," +
                "$COL_TYPE," +
                "$COL_DATE integerdate," +
                "$COL_IMG blob);")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TRAFO_TABLE_NAME)
        onCreate(db)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTrafo(trafo: Trafo): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        Log.i("POINT",trafo.point.toJson())

        contentValues.put(COL_SHAPE,trafo.point.toJson())
        contentValues.put(COL_NAME,trafo.name)
        contentValues.put(COL_CODE,trafo.code)
        contentValues.put(COL_FIELD,trafo.field)
        contentValues.put(COL_TYPE,trafo.type)
        contentValues.put(COL_DATE,trafo.date.time.time)

        val result = db.insert(TRAFO_TABLE_NAME,null,contentValues)

        db.close()
        addTrafoGraphic(trafo,null)
        return result

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun loadTrafo(map:MapView){
        val db = this.readableDatabase
        val query = "select * from $TRAFO_TABLE_NAME"
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query,null)
        }
        catch(e:SQLiteException){
            db.execSQL(query)
            return
        }

        var point:Point
        var shape: String
        var code:String
        var name:String
        var type:Short
        var date:Long
        var field:String
        var trafo:Trafo
        var img:ByteArray?

        if(cursor.moveToFirst()){
            do {
                code = cursor.getString(cursor.getColumnIndex(COL_CODE))
                name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                type = cursor.getShort(cursor.getColumnIndex(COL_TYPE))
                date = cursor.getLong(cursor.getColumnIndex(COL_DATE))
                field = cursor.getString(cursor.getColumnIndex(COL_FIELD))
                shape = cursor.getString(cursor.getColumnIndex(COL_SHAPE))
                img = cursor.getBlob(cursor.getColumnIndex(COL_IMG))

                point = Point.fromJson(shape) as Point
                var calendar = Calendar.getInstance()
                calendar.time = Date(date)

                if(point != null){
                    trafo = Trafo(point,code,name,type,calendar,field)

                    addTrafoGraphic(trafo,img)
                }

            }while (cursor.moveToNext())
        }

        map.graphicsOverlays.add(trafoImgGraphicsOverlay)
        map.graphicsOverlays.add(trafoGraphicsOverlay)
        db.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTrafoGraphic(trafo: Trafo, img:ByteArray?){

        // create a graphic with the point geometry and symbol
        val trafoGraphic = Graphic(trafo.point)

        // add the point graphic to the graphics overlay
        trafoGraphicsOverlay.graphics.add(trafoGraphic)

        //if the trafo has an image add it to another graphicsOverlay so it will be shown when zoomed in
        if(img != null){
            var symbol:Symbol
            val bitmap = BitmapFactory.decodeByteArray(img,0,img.size)
            val imageSymbol = PictureMarkerSymbol.createAsync(BitmapDrawable(bitmap))
            imageSymbol.addDoneListener {
                symbol = imageSymbol.get()

                // create a graphic with the point geometry and symbol
                val trafoImgGraphic = Graphic(trafo.point, symbol)

                // add the point graphic to the graphics overlay
                trafoImgGraphicsOverlay.graphics.add(trafoImgGraphic)

            }
        }
        else{
            trafoImgGraphicsOverlay.graphics.add(Graphic(trafo.point))
        }
    }


    fun updateTrafoImg(id:Long, img:Bitmap): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        val whereClause = "$COL_KEY = $id"

        val stream = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.JPEG,90,stream)
        val imgByte = stream.toByteArray()

        contentValues.put(COL_IMG, imgByte)

        val result = db.update(TRAFO_TABLE_NAME,contentValues,whereClause,null)
        db.close()

        return result
    }
}