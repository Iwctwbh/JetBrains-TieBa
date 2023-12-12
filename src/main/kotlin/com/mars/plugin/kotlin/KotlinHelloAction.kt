package com.mars.plugin.kotlin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages

class KotlinHelloAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // TODO: insert action logic here
        val project = e.getData(PlatformDataKeys.PROJECT)
        Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon())
    }
}