package com.example.testtask

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        // Load FXML
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("main.fxml"))
        val root = fxmlLoader.load<VBox>()

        val scene = Scene(root)

        scene.stylesheets.add(javaClass.getResource("/com/example/testtask/style.css")?.toExternalForm())

        stage.title = "Reddit Client"
        stage.scene = scene
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(HelloApplication::class.java)
        }
    }
}
