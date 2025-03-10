package xyz.bluspring.valleyutils.live.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object YoutubeApi {
    val apiKey = "AIzaSyDXCm2e20zJ-WvKOfKHout6T89akF2q0L8"
    private val logger = LoggerFactory.getLogger(YoutubeApi::class.java)

    fun get(url: String): JsonObject? {
        val client = HttpClient.newHttpClient()

        val req = HttpRequest.newBuilder(URI.create("$url&key=$apiKey"))
            .apply {
                GET()

                timeout(Duration.ofMillis(20_000))
            }
            .build()

        val res = client.send(req, HttpResponse.BodyHandlers.ofString())

        if (res.statusCode() != 200) {
            logger.error("Failed to GET $url: ${res.statusCode()} ${res.body()}")
            return null
        }

        return JsonParser.parseString(res.body()).asJsonObject
    }
}