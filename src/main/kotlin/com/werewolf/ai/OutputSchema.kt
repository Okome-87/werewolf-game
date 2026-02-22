package com.werewolf.ai

object OutputSchema {

    fun discussion(alivePlayers: List<Pair<String, String>>): String {
        val list = alivePlayers.joinToString("\n") { (id, name) -> "  - $id : $name" }
        return """
            ## 出力形式（厳守）
            JSON以外のテキストは一切出力しないこと。

            {
              "speech": "発言内容（100文字以内）",
              "suspectId": "疑っているプレイヤーのID、いなければ null"
            }

            有効なプレイヤーID：
            $list
            - 役職・正体を直接明かす発言は禁止
        """.trimIndent()
    }

    fun vote(alivePlayers: List<Pair<String, String>>, selfId: String): String {
        val list = alivePlayers
            .filter { it.first != selfId }
            .joinToString("\n") { (id, name) -> "  - $id : $name" }
        return """
            ## 出力形式（厳守）
            JSON以外のテキストは一切出力しないこと。

            {
              "targetId": "投票するプレイヤーのID",
              "reason": "理由（50文字以内）"
            }

            有効な投票先ID（自分自身は除く）：
            $list
        """.trimIndent()
    }

    fun nightTarget(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        excludeIds: List<String> = emptyList()
    ): String {
        val list = alivePlayers
            .filter { it.first != selfId && it.first !in excludeIds }
            .joinToString("\n") { (id, name) -> "  - $id : $name" }
        return """
            ## 出力形式（厳守）
            JSON以外のテキストは一切出力しないこと。

            {
              "targetId": "対象プレイヤーのID"
            }

            有効なID：
            $list
        """.trimIndent()
    }
}
