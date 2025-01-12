package com.example.testtask

import kotlinx.serialization.Serializable

@Serializable
data class RedditResponse(
    val data: RedditData
)

@Serializable
data class RedditData(
    val children: List<PostContainer>,
    val after: String?
)

@Serializable
data class PostContainer(
    val data: RedditPost
)

@Serializable
data class RedditPost(
    val author: String,
    val title: String,
    val created_utc: Long,
    val thumbnail: String?,
    val num_comments: Int,
    val url: String
)
