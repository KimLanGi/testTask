package com.example.testtask

import javafx.fxml.FXML
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import java.awt.Desktop
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
    private var currentPage: Int = 1
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val history = mutableListOf<String?>()

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

                    if (currentPage == 1) {
                        history.clear()
                    }
                    if (!history.contains(after)) {
                        history.add(after)
                    }

                    after = response.data.after
                    updatePagination()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Platform.runLater {
                    println("Failed to load posts: ${e.message}")
                }
            }
        }
    }

    private fun displayPosts(posts: List<PostContainer>) {
        listViewPosts.items.clear()
        for (post in posts) {
            val postBox = VBox(10.0).apply {
                styleClass.add("post-box")
            }

            val authorText = Text("Author: ${post.data.author}")
            val postedText = Text("Posted: ${formatTime(post.data.created_utc)}")
            val commentsText = Text("Comments: ${post.data.num_comments}")

            postBox.children.addAll(authorText, postedText, commentsText)

            if (!post.data.thumbnail.isNullOrBlank() && post.data.thumbnail.startsWith("http")) {
                val imageView = ImageView(Image(post.data.thumbnail)).apply {
                    fitWidth = 200.0
                    isPreserveRatio = true
                }

                // Context menu
                val contextMenu = ContextMenu()
                val saveImageMenuItem = MenuItem("Save Image")
                saveImageMenuItem.setOnAction { saveImage(post.data.thumbnail) }
                contextMenu.items.add(saveImageMenuItem)

                imageView.setOnContextMenuRequested { event ->
                    contextMenu.show(imageView, event.screenX, event.screenY)
                }

                // Open image
                imageView.setOnMouseClicked { event ->
                    if (event.button == MouseButton.PRIMARY) {
                        openImage(post.data.url)
                    }
                }

                postBox.children.add(imageView)
            }

            listViewPosts.items.add(postBox)
        }
    }

    private fun updatePagination() {
        pageLabel.text = "Page $currentPage"
        buttonPrevious.isDisable = currentPage == 1
        buttonNext.isDisable = after == null
    }

    @FXML
    private fun loadNextPage() {
        if (after != null) {
            buttonNext.isDisable = true
            currentPage++
            coroutineScope.launch {
                try {
                    val response = redditService.getTopPosts(after)
                    Platform.runLater {
                        displayPosts(response.data.children)

                        if (!history.contains(after)) {
                            history.add(after) // Add 'after' to the history
                        }

                        after = response.data.after
                        updatePagination()

                        buttonNext.isDisable = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Platform.runLater {
                        println("Failed to load posts: ${e.message}")
                        buttonNext.isDisable = false
                    }
                }
            }
            listViewPosts.scrollTo(0)
        }
    }

    @FXML
    private fun loadPreviousPage() {
        buttonPrevious.isDisable = true
        if (currentPage > 1) {
            currentPage--
            after = history.getOrNull(currentPage - 1) // Get previous 'after'
            loadPosts()
            listViewPosts.scrollTo(0)
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
