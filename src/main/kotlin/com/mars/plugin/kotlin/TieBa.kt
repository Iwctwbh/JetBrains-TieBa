package com.mars.plugin.kotlin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode


class TieBa : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panelMain = JPanel(BorderLayout())
        val header = JPanel(BorderLayout())


        val buttonRefresh = JButton("Refresh")
        buttonRefresh.addActionListener {
        }

        /*val jToolBar = JToolBar("ToolBar", SwingConstants.HORIZONTAL)

        val refresh:Action = object:AbstractAction("Refresh",null){
            override fun actionPerformed(e: ActionEvent) {
                println("Refresh")
            }
        }

        JButton (refresh).also {
            it.toolTipText = "Refresh"
            jToolBar.add(it)
        }*/

        // Tree
        val root = DefaultMutableTreeNode("Root")
        val childPython = DefaultMutableTreeNode("Python")
        val childJava = DefaultMutableTreeNode("Java")

        val test = DefaultMutableTreeNode("Test")
        val test1 = DefaultMutableTreeNode("Test1")
        childPython.add(test)
        childPython.add(test1)

        // Relationship
        root.add(childPython);
        root.add(childJava);

        val tree = Tree(root);

        // Tree Event
        tree.addTreeSelectionListener {
            val node = tree.lastSelectedPathComponent as DefaultMutableTreeNode
            // Notification
            NotificationGroupManager.getInstance().getNotificationGroup("TieBaNotification")
                .createNotification("Mars", node.toString(), NotificationType.INFORMATION).notify(project)

            // open a new file editor
            PsiFileFactory.getInstance(project).createFileFromText(node.toString(), PlainTextLanguage.INSTANCE, "").navigate(true)
        }

        // Layout
        // header
        header.add(buttonRefresh)


        // panel
        panelMain.add(header, BorderLayout.NORTH)
        panelMain.add(tree, BorderLayout.CENTER)
        toolWindow.component.add(panelMain)
    }
}
