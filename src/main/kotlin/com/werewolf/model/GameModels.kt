package com.werewolf.model

enum class MediumResult(val displayName: String) {
    WHITE("白"),
    BLACK("黒"),
}

enum class Role(val mediumResult: MediumResult) {
    VILLAGER(MediumResult.WHITE),
    WEREWOLF(MediumResult.BLACK),
    SEER    (MediumResult.WHITE),
    KNIGHT  (MediumResult.WHITE),
    MEDIUM  (MediumResult.WHITE),
    LUNATIC (MediumResult.WHITE),
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
