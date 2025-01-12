package com.example.testtask

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import javafx.application.Platform
import java.awt.Desktop
import javafx.scene.control.Label
import java.net.URI

class RedditController {
    @FXML
    private lateinit var listViewPosts: ListView<VBox>
    @FXML
    private lateinit var buttonPrevious: Button
    @FXML
    private lateinit var buttonNext: Button
    @FXML
    private lateinit var pageLabel: Label

    private val redditService = RedditService()
    private var after: String? = null
    private var before: String? = null  // Для хранения предыдущего значения 'after'
    private var currentPage: Int = 1
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @FXML
    fun initialize() {
        loadPosts()
    }

    private fun loadPosts() {
        coroutineScope.launch {
            try {
                val response = redditService.getTopPosts(after)
                Platform.runLater {
                    displayPosts(response.data.children)
                    after = response.data.after
                    before = response.data.before
                    updatePagination()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displayPosts(posts: List<PostContainer>) {
        listViewPosts.items.clear()
        for (post in posts) {
            val postBox = VBox(10.0)
            postBox.children.add(Text("Author: ${post.data.author}"))
            postBox.children.add(Text("Posted: ${formatTime(post.data.created_utc)}"))
            postBox.children.add(Text("Comments: ${post.data.num_comments}"))

            if (!post.data.thumbnail.isNullOrBlank() && post.data.thumbnail.startsWith("http")) {
                val imageView = ImageView(Image(post.data.thumbnail))
                imageView.fitWidth = 100.0
                imageView.isPreserveRatio = true

                imageView.setOnMouseClicked {
                    openImage(post.data.url) // Открытие URL изображения в браузере
                }
                postBox.children.add(imageView)

                val saveButton = Button("Save Image")
                saveButton.setOnAction { saveImage(post.data.thumbnail) }
                postBox.children.add(saveButton)
            }

            listViewPosts.items.add(postBox)
        }
    }

    private fun updatePagination() {

        pageLabel.text = "Page $currentPage"

        buttonPrevious.isDisable = before == null
        buttonNext.isDisable = after == null
    }

    @FXML
    private fun loadNextPage() {
        if (after != null) {
            currentPage++
            loadPosts()
            listViewPosts.scrollTo(0)
        }
    }

    @FXML
    private fun loadPreviousPage() {
        if (before != null) {
            currentPage--
            after = before  // Меняем 'after' на 'before' для загрузки предыдущей страницы
            loadPosts()
        }
    }

    private fun formatTime(unixTime: Double): String {
        val currentTime = System.currentTimeMillis() / 1000
        val hoursAgo = (currentTime - unixTime).toInt() / 3600
        return "$hoursAgo hours ago"
    }

    private fun openImage(url: String) {
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveImage(thumbnailUrl: String) {
        val fileChooser = FileChooser()
        fileChooser.title = "Save Image"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"))
        val file = fileChooser.showSaveDialog(listViewPosts.scene.window)
        if (file != null) {
            coroutineScope.launch {
                try {
                    val imageBytes = downloadImage(thumbnailUrl)
                    file.writeBytes(imageBytes)
                    Platform.runLater {
                        println("Image saved to ${file.absolutePath}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun downloadImage(url: String): ByteArray {
        return redditService.downloadImage(url)
    }
}