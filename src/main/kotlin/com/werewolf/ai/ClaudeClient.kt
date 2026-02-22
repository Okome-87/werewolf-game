package com.werewolf.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*

class ClaudeClient(private val apiKey: String) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000   // 30秒
            connectTimeoutMillis = 10_000   // 10秒
        }
    }

    suspend fun generate(
        systemPrompt: String,
        userPrompt: String,
        retryCount: Int = 2  // 失敗したら最大2回リトライ
    ): String {
        repeat(retryCount + 1) { attempt ->
            try {
                val response = client.post("https://api.anthropic.com/v1/messages") {
                    header("x-api-key", apiKey)
                    header("anthropic-version", "2023-06-01")
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("model", "claude-haiku-4-5-20251001")
                        put("max_tokens", 300)
                        putJsonArray("system") {
                            addJsonObject {
                                put("type", "text")
                                put("text", systemPrompt)
                            }
                        }
                        putJsonArray("messages") {
                            addJsonObject {
                                put("role", "user")
                                put("content", userPrompt)
                            }
                        }
                    })
                }
                val body = response.body<JsonObject>()
                val content = body["content"]
                    ?: throw IllegalStateException("contentがありません: $body")
                return content.jsonArray[0].jsonObject["text"]!!.jsonPrimitive.content

            } catch (e: Exception) {
                if (attempt == retryCount) throw e
                println("⚠️ API接続失敗 (${attempt + 1}回目)、リトライします...")
                delay(2000L * (attempt + 1))  // 2秒、4秒と待機時間を増やす
            }
        }
        throw IllegalStateException("リトライ上限に達しました")
    }

    fun close() = client.close()
}