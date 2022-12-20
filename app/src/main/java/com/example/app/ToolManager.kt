package com.example.app

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.example.app.Commands.Tools.ITool
import android.widget.Button
import com.example.app.Commands.ICommand

class ToolManager(context: Context, public var list: List<ICommand>):LinearLayout(context) {
    public var commandList = list
    var activeTool : ITool? = null

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
                if(command is ITool){
                activeTool?.Deactivate()
                activeTool = command
                command?.Activate()
                }
                else{
                    command.run()
                }
            }
            command.button = button
            addView(command.button)
            id++
        }
    }
}