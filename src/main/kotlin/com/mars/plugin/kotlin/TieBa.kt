package com.mars.plugin.kotlin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.treeStructure.Tree
import org.jsoup.Jsoup
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.regex.Pattern


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

        // TieBas
        val tieBas = arrayOf("Python", "Java")
        val nodeTieBas = mutableListOf<DefaultMutableTreeNode>()
        for (tieba in tieBas) {
            nodeTieBas.add(DefaultMutableTreeNode(tieba))
        }
        // Tree
        val root = DefaultMutableTreeNode("Root")

        // Relationship
        for (node in nodeTieBas) {
            root.add(node)
        }

        val tree = Tree(root);

        // Tree Event
        tree.addTreeSelectionListener {
            val node = tree.lastSelectedPathComponent as DefaultMutableTreeNode
            if (nodeTieBas.contains(node)) {
                // return@addTreeSelectionListener
                // Notification
                NotificationGroupManager.getInstance().getNotificationGroup("TieBaNotification")
                    .createNotification("Mars", node.toString(), NotificationType.INFORMATION).notify(project)

                val client: HttpClient = HttpClient.newHttpClient()

                val request: HttpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://tieba.baidu.com/f?kw=$node&ie=utf-8&pn=0&pagelets=frs-list/pagelet/thread"))
                    .GET()
                    .setHeader(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
                    )
                    .setHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .setHeader("Cache-Control", "max-age=0")
                    /*.setHeader("Connection", "keep-alive")*/
                    .setHeader("Sec-Fetch-Dest", "document")
                    .setHeader("Sec-Fetch-Mode", "navigate")
                    .setHeader("Sec-Fetch-Site", "none")
                    .setHeader("Sec-Fetch-User", "?1")
                    .setHeader("Upgrade-Insecure-Requests", "1")
                    .setHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                    )
                    .setHeader(
                        "sec-ch-ua",
                        "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\""
                    )
                    .setHeader("sec-ch-ua-mobile", "?0")
                    .setHeader("sec-ch-ua-platform", "\"Windows\"")
                    .build()

                val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

                val html: String = response.body()// your HTML string here
                val pattern =
                    Pattern.compile("Bigpipe.register\\(\"frs-list/pagelet/thread_list\", \\{\"content\":(\".*\"),\"parent\"")
                val matcher = pattern.matcher(html)

                if (matcher.find()) {
                    val jsonStr = matcher.group(1)
//                val jsonObject: JsonObject = Json.parseToJsonElement(jsonStr) as JsonObject
                    // Now you can use jsonObject
                    val unescapedJsonStr = jsonStr.replace("\\\"", "\"")
                    val doc = Jsoup.parse(unescapedJsonStr)
                    val elements = doc.select(".j_thread_list")
                    elements.forEach {
                        val element = it.select("a.j_th_tit")
                        val result = decodeUnicode(element.attr("title"))
                        node.add(
                            DefaultMutableTreeNode(
                                result
                            )
                        )
                    }

                    // 刷新视图
                    tree.revalidate()
                    tree.repaint()
                }
            } else {
                // open a new file editor (read-only mode)
                PsiFileFactory.getInstance(project)
                    .createFileFromText(node.toString(), PlainTextLanguage.INSTANCE, "").navigate(true)
            }
        }

        // Layout
        // header
        header.add(buttonRefresh)


        // panel
        panelMain.add(header, BorderLayout.NORTH)
        panelMain.add(JScrollPane(tree), BorderLayout.CENTER)
        toolWindow.component.add(panelMain)
    }

    fun decodeUnicode(input: String): String {
        val pattern = Regex(pattern = "\\\\u([0-9a-fA-F]{4})")
        return pattern.replace(input) {
            val unicodeValue = it.groupValues[1].toInt(16)
            unicodeValue.toChar().toString()
        }
    }
}
