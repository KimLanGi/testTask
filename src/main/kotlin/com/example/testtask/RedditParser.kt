package com.example.testtask

import com.google.gson.Gson
import com.google.gson.JsonObject

class RedditParser {
    fun parsePosts(json: String): List<RedditPosts> {
        val gson = Gson()
        val root = gson.fromJson(json, JsonObject::class.java)
        val posts = root["data"].asJsonObject["children"].asJsonArray.map {
            val data = it.asJsonObject["data"].asJsonObject
            RedditPosts(
                author = data["author"].asString,
                title = data["title"].asString,
                thumbnail = data["thumbnail"].asString.takeIf { it.startsWith("http") },
                num_comments = data["num_comments"].asInt,
                created_utc = data["created_utc"].asLong
            )
        }
        return posts
    }
}
