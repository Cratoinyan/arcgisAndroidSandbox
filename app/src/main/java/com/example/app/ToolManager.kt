package com.example.app

import android.content.Context
import android.view.MotionEvent
import com.example.app.Commands.Tools.ITool

class ToolManager(context: Context,list: List<ITool>) {
    public var _context = context
    public var toolList = list

    public fun Initialize(){

    }

    public fun onTouchClick(e: MotionEvent){

    }
}