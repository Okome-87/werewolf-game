package com.werewolf.ai

import com.werewolf.model.*

class AIPlayer(
    val character: AICharacter,
    val role: Role,
    private val client: ClaudeClient
) {
    private val checkedIds = mutableListOf<String>()
    private var pendingReport: String? = null
    private val myImportantActions = mutableListOf<String>()  // 追加：自分の重要行動の記録

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

    suspend fun nightAction(
        situation: GameSituation,
        alivePlayers: List<Pair<String, String>>,
        isFirstNight: Boolean = false  // 追加
    ): String? {
        if (isFirstNight) return null
        val validTargets = alivePlayers
            .map { it.first }
            .filter { it != character.id }

        val schema = when (role) {
            Role.WEREWOLF -> OutputSchema.nightTarget(alivePlayers, character.id)
            Role.SEER     -> OutputSchema.nightTarget(alivePlayers, character.id, checkedIds)
            Role.KNIGHT   -> OutputSchema.nightTarget(alivePlayers, character.id)
            Role.MEDIUM   -> OutputSchema.nightTarget(alivePlayers,character.id)
            else -> return null
        }

        val (system, user) = buildPrompt(situation, schema)
        val raw = client.generate(system, user)
        val targetId = OutputParser.parseNightTarget(raw, validTargets)
        if (role == Role.SEER) checkedIds.add(targetId)

        if (role == Role.SEER) {
            checkedIds.add(targetId)
            val targetName = alivePlayers.find { it.first == targetId }?.second ?: targetId
            // 占い結果を翌日の議論のために記録
            myImportantActions.add("${situation.round}日目の夜：${targetName}を占った（結果は昼の議論でCOすること）")
            pendingReport = "${targetName}を占いました。結果を今日の議論でCOしてください"
        }

        if (role == Role.MEDIUM) {
            checkedIds.add(targetId)
            val targetName = alivePlayers.find { it.first == targetId }?.second ?: targetId
            // 霊能結果を翌日の議論のために記録
            myImportantActions.add("${situation.round}日目の夜：${targetName}の霊を見ました（結果は昼の議論でCOすること）")
            pendingReport = "${targetName}の霊を見ました。結果を今日の議論でCOしてください"
        }

        if (role == Role.KNIGHT) {
            val targetName = alivePlayers.find { it.first == targetId }?.second ?: targetId
            myImportantActions.add("${situation.round}日目の夜：${targetName}を護衛した")
        }

        return targetId
    }
}
