package com.example.testtask

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        // Загружаем FXML
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("main.fxml"))
        val root = fxmlLoader.load<VBox>()

        // Создаем сцену
        val scene = Scene(root)

        // Подключаем стили
        scene.stylesheets.add(javaClass.getResource("/com/example/testtask/style.css")?.toExternalForm())


        // Устанавливаем сцену и показываем окно
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
