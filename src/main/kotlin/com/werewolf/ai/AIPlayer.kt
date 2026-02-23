package com.werewolf.ai

import com.werewolf.model.*

class AIPlayer(
    val character: AICharacter,
    val role: Role,
    private val client: ClaudeClient
) {
    private val strategy = RoleStrategyFactory.create(role)
    private val checkedIds = mutableListOf<String>()
    private var pendingReport: String? = null
    private val myImportantActions = mutableListOf<String>()

    private fun buildPrompt(situation: GameSituation, schema: String): Pair<String, String> {
        val system = PromptBuilder.buildSystemPrompt(character, role.name)
        val user = PromptBuilder.buildUserPrompt(character, situation) + "\n\n$schema"
        return system to user
    }

    suspend fun discuss(
        situation: GameSituation,
        alivePlayers: List<Pair<String, String>>
    ): DiscussionOutput {
        val system = PromptBuilder.buildSystemPrompt(character, role.name)

        // 自分の重要行動の記憶をuserプロンプトに追加
        val memoryNote = if (myImportantActions.isNotEmpty()) """
            ## あなた自身のこれまでの重要な行動
            ${myImportantActions.joinToString("\n")}
            上記の行動と発言に一貫性を持たせてください。
        """.trimIndent() else ""

        val schema = OutputSchema.discussion(alivePlayers)
        val user = PromptBuilder.buildUserPrompt(character, situation) +
                "\n\n$memoryNote" +
                "\n\n$schema"

        val raw = client.generate(system, user)
        val output = OutputParser.parseDiscussion(raw, alivePlayers)

        // COした場合は記録する
        if (output.speech.contains("CO") ||
            output.speech.contains("霊能") ||
            output.speech.contains("占い師") ||
            output.speech.contains("騎士")) {
            myImportantActions.add("${situation.round}日目：「${output.speech}」と発言した")
        }

        return output
    }

    suspend fun vote(
        situation: GameSituation,
        alivePlayers: List<Pair<String, String>>
    ): VoteOutput {
        val (system, user) = buildPrompt(
            situation,
            OutputSchema.vote(alivePlayers, character.id)
        )
        val raw = client.generate(system, user)
        return OutputParser.parseVote(
            raw,
            alivePlayers.map { it.first },
            character.id
        )
    }

    fun notifyExecution(round: Int, executedName: String, isWerewolf: Boolean) {
        strategy.recordExecution(round, executedName, isWerewolf)
            ?.let { myImportantActions.add(it) }
    }

    suspend fun nightAction(
        situation: GameSituation,
        alivePlayers: List<Pair<String, String>>,
        isFirstNight: Boolean = false
    ): String? {
        if (isFirstNight) return null

        val schema = strategy.buildNightSchema(alivePlayers, character.id, checkedIds)
            ?: return null

        val validTargets = alivePlayers.map { it.first }.filter { it != character.id }
        val (system, user) = buildPrompt(situation, schema)
        val raw = client.generate(system, user)
        val targetId = OutputParser.parseNightTarget(raw, validTargets)
        val targetName = alivePlayers.find { it.first == targetId }?.second ?: targetId

        val record = strategy.recordNightAction(situation.round, targetId, targetName)
        record.importantAction?.let { myImportantActions.add(it) }
        if (record.markChecked) checkedIds.add(targetId)
        record.pendingReport?.let { pendingReport = it }

        return targetId
    }
}
