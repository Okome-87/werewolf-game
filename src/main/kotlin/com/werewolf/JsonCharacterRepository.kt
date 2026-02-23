package com.werewolf

import com.werewolf.model.AICharacter
import com.werewolf.model.CharacterRepository
import kotlinx.serialization.json.Json

class JsonCharacterRepository(
    private val resourcePath: String = "/characters.json"
) : CharacterRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun loadAll(): List<AICharacter> {
        val content = this::class.java.getResourceAsStream(resourcePath)
            ?.bufferedReader()
            ?.readText()
            ?: error("キャラクター設定ファイルが見つかりません: $resourcePath")
        return json.decodeFromString(content)
    }
}
