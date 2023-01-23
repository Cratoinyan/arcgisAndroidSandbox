package com.example.app.Managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.widget.Button
import android.widget.LinearLayout
import com.example.app.Commands.ICommand
import com.example.app.Commands.Tools.ITool
import com.google.android.material.button.MaterialButton


class ToolManager(private val context: Context, private var list: List<ICommand>, private val layout: LinearLayout){
    private var commandList = list
    var activeTool : ITool? = null
    var activeBtn : MaterialButton? = null

    @SuppressLint("ResourceType")
    fun Initialize(){
        activeTool = null
        for ((id, command) in commandList.withIndex()) {
            //set button style
            val button = MaterialButton(context)
            button.id = id
            var layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.rightMargin = 5
            button.layoutParams = layoutParams
            button.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            button.text = command.id


            button.setOnClickListener {
                onClickListener(command,button)
            }
            layout.addView(button)
        }
    }

    private fun onClickListener(command:ICommand, button: MaterialButton){
        if(command is ITool){
            activeTool?.Deactivate()
            activeBtn?.strokeWidth = 0
            if(command == activeTool){
                activeTool = null
                activeBtn = null
            }
            else{
                activeTool = command
                activeBtn = button
                activeBtn?.strokeColor = ColorStateList.valueOf(Color.DKGRAY)
                activeBtn?.strokeWidth = 5
                command.Activate()
            }
        }
        else{
            command.run()
        }
    }
}