package com.werewolf.ai

import com.werewolf.model.AICharacter
import com.werewolf.model.CharacterParameters
import com.werewolf.model.GameSituation

object PromptBuilder {

    fun buildSystemPrompt(character: AICharacter, role: String): String {
        return """
            あなたは人狼ゲームに参加している「${character.name}」です。
            以下の設定に厳密に従って発言してください。

            ## プロフィール
            - 名前：${character.name}（${character.age}歳）
            - 経歴：${character.background}
            - 性格：${character.personality}
            - 口調：${character.speechStyle}
            - 口癖（自然に使う）：${character.catchphrases.joinToString("、")}

            ## 役職（他プレイヤーに絶対に明かさないこと）
            - 役職：$role
            ${buildRoleInstruction(role)}

            ## 行動戦略
            ${character.strategy}

            ## 発言スタイル
            ${buildStyleInstruction(character.params)}

            ${WerewolfKnowledge.commonKnowledge}

            ${WerewolfKnowledge.roleKnowledge(role)}

            ${WerewolfKnowledge.difficultyInstruction(character.difficultyLevel)}

            ${WerewolfKnowledge.strategyStance(character.params)}

            ## 絶対ルール
            - 役職を直接明かさない
            - 「AIです」「キャラクターです」と言わない
            - 常に「${character.name}」として話す
            - 発言は必ず日本語
        """.trimIndent()
    }

    fun buildUserPrompt(
        character: AICharacter,
        situation: GameSituation,
        role: String = "",
        pendingReport: String? = null
    ): String {

        val reportInstruction = if (pendingReport != null) """
        ## 【重要】未報告の情報があります
        $pendingReport
        今日の議論でこの情報をCOして報告することを強く推奨します。
    """.trimIndent()
        else ""  // ← null のときは空文字

        return """
        ## 現在の状況
        - ${situation.round}日目 / ${situation.phase}
        - 生存プレイヤー：${situation.alivePlayers.joinToString("、")}

        ## これまでの会話
        ${formatChatLog(situation.chatLog)}

        $reportInstruction

        ## 指示
        ${situation.instruction}
    """.trimIndent()
    }

    private fun buildRoleInstruction(role: String): String = when (role) {
        "WEREWOLF" -> "人狼として正体を隠しつつ村人を欺いてください"
        "SEER"     -> "占い師として情報を戦略的に公開してください"
        "KNIGHT"   -> "騎士として正体を隠しながら重要な人物を護衛してください"
        else       -> "村人として人狼を見つけてください"
    }

    private fun buildStyleInstruction(p: CharacterParameters): String {
        val lines = mutableListOf<String>()
        lines += when {
            p.logic >= 4 -> "論理的・構造的に話す。感情表現は避ける"
            p.logic <= 2 -> "感情を前面に出す。直感で判断する"
            else         -> "論理と感情のバランスをとる"
        }
        lines += when {
            p.verbosity <= 2 -> "発言は短く。1〜2文で完結させる"
            p.verbosity >= 4 -> "根拠や考えを詳しく説明する"
            else             -> "必要な情報を過不足なく話す"
        }
        lines += when {
            p.assertiveness >= 4 -> "断定的に話す。曖昧な表現は使わない"
            p.assertiveness <= 2 -> "控えめに、可能性として提示する"
            else                 -> "適度な確信を持って話す"
        }
        lines += when {
            p.deception >= 4 -> "嘘をつく際は細部まで具体的に話して信憑性を上げる"
            p.deception <= 2 -> "嘘が下手。追及されると言葉が曖昧になる"
            else             -> "嘘はつけるが完璧ではない"
        }
        return lines.mapIndexed { i, l -> "${i + 1}. $l" }.joinToString("\n")
    }

    private fun formatChatLog(log: List<Pair<String, String>>): String {
        if (log.isEmpty()) return "（まだ発言はありません）"
        return log.joinToString("\n") { (name, msg) -> "[$name]: $msg" }
    }
}
