package com.example.app

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.widget.LinearLayout
import com.example.app.Commands.Tools.ITool
import android.util.Log
import android.widget.Button
import android.widget.Toast

class ToolManager(context: Context, public var list: List<ITool>):LinearLayout(context) {
    public var toolList = list
    var activeTool : ITool? = null

    @SuppressLint("ResourceType")
    fun Initialize(){
        var id = 0
        activeTool = null
        for (iTool in toolList) {
            val button = Button(context)
            button.setId(id)
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            button.setOnClickListener {
                activeTool?.Deactivate()
                activeTool = iTool
                iTool?.Activate()
            }
            iTool.button = button
            addView(iTool.button)
            id++
        }
    }
}