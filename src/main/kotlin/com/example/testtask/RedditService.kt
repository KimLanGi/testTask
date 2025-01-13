package com.example.testtask

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RedditService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getTopPosts(after: String? = null): RedditResponse {
        val url = "https://www.reddit.com/top.json" + if (after != null) "?after=$after" else ""
        return client.get(url).body()
    }

    suspend fun downloadImage(url: String): ByteArray {
        return client.get(url).body()
    }
}
