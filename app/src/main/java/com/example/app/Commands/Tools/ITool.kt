package com.example.app.Commands.Tools

import com.example.app.Commands.ICommand

interface ITool: ICommand{
    fun Activate()
    fun Deactivate()
}