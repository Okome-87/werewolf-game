package com.werewolf.model

enum class Role {
    VILLAGER,  // 村人
    WEREWOLF,  // 人狼
    SEER,      // 占い師
    KNIGHT,    // 騎士
    MEDIUM,    // 霊能者（処刑されたプレイヤーの役職がわかる）
    LUNATIC    // 狂人（人狼陣営だが人狼を知らない。村人のふりをする）
}

enum class GamePhase {
    NIGHT, DISCUSSION, VOTE, RESULT
}

data class Player(
    val id: String,
    val name: String,
    val role: Role,
    val isHuman: Boolean,
    var isAlive: Boolean = true,
    val character: AICharacter? = null  // AIのみ
)

data class ChatMessage(
    val playerName: String,
    val content: String,
    val phase: GamePhase,
    val round: Int
)

data class GameSituation(
    val round: Int,
    val phase: String,
    val alivePlayers: List<String>,
    val chatLog: List<Pair<String, String>>,
    val instruction: String
)
