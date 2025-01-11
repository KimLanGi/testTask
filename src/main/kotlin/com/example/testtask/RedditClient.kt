package com.example.testtask

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class RedditClient {
    suspend fun fetchTopPosts(): String = withContext(Dispatchers.IO) {
        val url = URL("http://www.reddit.com/top.json")
        val connection = url.openConnection() as HttpURLConnection
        connection.inputStream.bufferedReader().use { it.readText() }
    }
}
