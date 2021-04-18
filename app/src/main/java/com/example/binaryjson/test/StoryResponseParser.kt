package com.example.binaryjson.test

import org.json.JSONArray
import org.json.JSONObject

object StoryResponseParser {

    fun parse(json: JSONObject): StoryResponse {
        return StoryResponse(
                id = json.getString("id"),
                type = json.getString("type"),
                title = json.getString("title"),
                ttl = json.getLong("ttl"),
                rid = json.getString("rid"),
                item_id = json.getString("item_id"),
                item_type = json.getString("item_type"),
                items = parseChannels(json.getJSONArray("items"))
        )
    }

    private fun parseChannels(array: JSONArray): Array<StoryResponse.Channel> {
        return Array(array.length()) {
            val json = array.getJSONObject(it).getJSONObject("data")
            StoryResponse.Channel(
                    id = json.getString("id"),
                    source = parseSource(json.getJSONObject("source")),
                    isSubscribed = json.getBoolean("isSubscribed"),
                    stories = parseStory(json.getJSONArray("stories"))
            )
        }
    }

    private fun parseStory(array: JSONArray): Array<StoryResponse.Story> {
        return Array(array.length()) {
            val json = array.getJSONObject(it).getJSONObject("data")
            StoryResponse.Story(
                    id = json.getString("id"),
                    publicationTime = json.getLong("publicationTime"),
                    item_id = json.getString("item_id"),
                    item_type = json.getString("item_type"),
                    ttl = json.getLong("ttl"),
                    preview = json.getString("preview"),
                    previews = parsePreviews(json.getJSONObject("previews")),
                    video = json.getString("video"),
                    status = json.getString("status"),
            )
        }
    }

    private fun parsePreviews(json: JSONObject): StoryResponse.Previews {
        return StoryResponse.Previews(
                small = json.getString("small"),
                normal = json.getString("normal"),
                big = json.getString("big")
        )
    }

    private fun parseSource(json: JSONObject): StoryResponse.Source {
        return StoryResponse.Source(
                id = json.getString("id"),
                type = json.getString("type"),
                title = json.getString("title"),
                logo = json.getString("logo"),
                logos = parseLogos(json.getJSONObject("logos")),
                logoBackgroundColor = json.getString("logoBackgroundColor"),
                feedLink = json.getString("feedLink"),
                feedApiLink = json.getString("feedApiLink"),
                subscribers = json.getInt("subscribers"),
                isVerified = json.getBoolean("isVerified"),
                subscriptionForbidden = json.getBoolean("subscriptionForbidden"),
        )
    }

    private fun parseLogos(json: JSONObject): StoryResponse.Logos {
        return StoryResponse.Logos(
                small = json.getString("small"),
                normal = json.getString("normal")
        )
    }
}