package com.example.testtask

data class RedditPosts(
    val author: String,
    val title: String,
    val thumbnail: String?,
    val num_comments: Int,
    val created_utc: Long
)
