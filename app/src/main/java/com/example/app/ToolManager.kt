package com.example.app

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.example.app.Commands.Tools.ITool
import android.widget.Button
import com.example.app.Commands.ICommand

class ToolManager(context: Context, public var list: List<ICommand>,layout: LinearLayout){
    public var commandList = list
    var activeTool : ITool? = null
    val layout = layout
    val context = context

    @SuppressLint("ResourceType")
    fun Initialize(){
        var id = 0
        activeTool = null
        for (command in commandList) {
            val button = Button(context)
            button.setId(id)
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            button.text = command.id
            button.setOnClickListener {
                onClickListener(command)
            }
            layout.addView(button)
            id++
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