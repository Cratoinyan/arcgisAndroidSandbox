package com.example.app

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.example.app.Commands.Tools.ITool
import android.widget.Button
import com.example.app.Commands.ICommand

class ToolManager(private val context: Context, private var list: List<ICommand>, private val layout: LinearLayout){
    private var commandList = list
    var activeTool : ITool? = null

    @SuppressLint("ResourceType")
    fun Initialize(){
        activeTool = null
        for ((id, command) in commandList.withIndex()) {
            val button = Button(context)
            button.id = id
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            button.text = command.id
            button.setOnClickListener {
                onClickListener(command)
            }
            layout.addView(button)
        }
    }

    private fun onClickListener(command:ICommand){
        if(command is ITool){
            activeTool?.Deactivate()
            if(command == activeTool)
            {
                activeTool = null
            }
            else{
                activeTool = command
                command.Activate()
            }
        }
        else{
            command.run()
        }
    }
}