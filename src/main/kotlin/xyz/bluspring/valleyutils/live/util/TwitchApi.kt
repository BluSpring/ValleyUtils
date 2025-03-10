package xyz.bluspring.valleyutils.live.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import okhttp3.CacheControl
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.net.URI

object TwitchApi {
    private val logger = LoggerFactory.getLogger(TwitchApi::class.java)
    private val client = OkHttpClient()

    val clientId = "pjauiub7dh0kgopvo0f8npw6v5gf7k"
    val clientSecret = FabricLoader.getInstance().configDir.resolve("valleyutils_twitch_secret.txt").toFile().run {
        if (this.exists())
            this.readText()
        else ""
    }

    var accessToken: String? = null

    fun refreshAccessToken() {
        if (clientId.isBlank())
            throw IllegalStateException("Client ID not provided!")

        if (clientSecret.isBlank())
            throw IllegalStateException("Client Secret not provided!")

        val params = FormBody.Builder(Charsets.UTF_8).apply {
            addEncoded("client_id", clientId)
            addEncoded("client_secret", clientSecret)
            add("grant_type", "client_credentials")
        }
            .build()

        val req = Request.Builder().apply {
            url("https://id.twitch.tv/oauth2/token")
            post(params)
            header("Content-Type", "application/x-www-form-urlencoded")
            cacheControl(CacheControl.FORCE_NETWORK)
        }
            .build()

        client.newCall(req).execute().use { response ->
            if (response.code == 401) {
                accessToken = null

                return
            } else if (response.code != 200) {
                logger.error("Failed to refresh token! ${response.code} ${response.body?.string()}")

                return
            }

            val json = JsonParser.parseReader(response.body!!.charStream()).asJsonObject

            accessToken = json.get("access_token").asString
            logger.info("Successfully updated Twitch API access token!")
        }
    }

    fun post(uri: URI, json: JsonObject, secondTry: Boolean = false): JsonObject? {
        val req = Request.Builder().apply {
            url(uri.toURL())
            post(json.toString().toRequestBody("application/json".toMediaType()))
            cacheControl(CacheControl.FORCE_NETWORK)

            header("Client-Id", clientId)
            header("Authorization", "Bearer $accessToken")
        }
            .build()

        client.newCall(req).execute().use { resp ->
            if (resp.code == 401) {
                if (secondTry) {
                    logger.error("Failed to POST $uri - ${resp.code} ${resp.body?.string()}")
                    return null
                }

                refreshAccessToken()
                return post(uri, json, true)
            }

            return JsonParser.parseReader(resp.body!!.charStream()).asJsonObject
        }
    }

    fun get(uri: URI, secondTry: Boolean = false): JsonObject? {
        val req = Request.Builder().apply {
            url(uri.toURL())
            get()
            cacheControl(CacheControl.FORCE_NETWORK)

            header("Client-Id", clientId)
            header("Authorization", "Bearer $accessToken")
        }
            .build()

        client.newCall(req).execute().use { resp ->
            if (resp.code == 401) {
                if (secondTry) {
                    logger.error("Failed to GET $uri - ${resp.code} ${resp.body?.string()}")
                    return null
                }

                refreshAccessToken()
                return get(uri, true)
            }

            return JsonParser.parseReader(resp.body?.charStream()).asJsonObject
        }
    }

    fun getUserIds(usernames: List<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()

        for (usernameList in usernames.chunked(100)) {
            val json = get(URI.create("https://api.twitch.tv/helix/users?login=${usernameList.joinToString("&login=")}")) ?: continue

            if (!json.has("data")) {
                logger.error("Failed to load users: $json")
                continue
            }

            for (jsonElement in json.getAsJsonArray("data")) {
                val data = jsonElement.asJsonObject

                map[data.get("login").asString] = data.get("id").asString
            }
        }

        return map
    }
}