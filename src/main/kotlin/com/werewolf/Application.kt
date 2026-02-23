package com.werewolf

import com.werewolf.ai.*
import com.werewolf.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// ===== GameEngine =====

class GameEngine(
    private val humanPlayerName: String,
    private val characterRepository: CharacterRepository,
    private val claudeClient: ClaudeClient
) {
    private lateinit var players: MutableList<Player>
    private lateinit var aiPlayers: Map<String, AIPlayer>
    private val chatLog = mutableListOf<Pair<String, String>>()
    private var round = 1

    fun setup() {
        val aiCharacters = characterRepository.loadAll().take(8)

        // 9人構成：村人3・占い師1・霊能者1・騎士1・人狼2・狂人1
        val roles = mutableListOf(
            Role.WEREWOLF, Role.WEREWOLF, Role.LUNATIC,   // 人狼陣営
            Role.SEER, Role.MEDIUM, Role.KNIGHT,           // 特殊村人
            Role.VILLAGER, Role.VILLAGER, Role.VILLAGER    // 村人
        ).also { it.shuffle() }

        players = mutableListOf()

        players.add(Player(
            id = "human",
            name = humanPlayerName,
            role = roles[0],
            isHuman = true
        ))

        aiCharacters.forEachIndexed { i, ch ->
            players.add(Player(
                id = ch.id,
                name = ch.name,
                role = roles[i + 1],
                isHuman = false,
                character = ch
            ))
        }

        aiPlayers = players
            .filter { !it.isHuman }
            .associate { player ->
                player.id to AIPlayer(player.character!!, player.role, claudeClient)
            }

        println("\n============================")
        println("  人狼ゲーム スタート！")
        println("============================")
        println("参加者：${players.joinToString("、") { it.name }}")

        val humanPlayer = players.first { it.isHuman }
        println("\n【あなたの役職：${humanPlayer.role.displayName()}】")
        println(humanPlayer.role.roleDescription())
        println("============================\n")
    }

    suspend fun run() {


        while (true) {
            println("\n▼ ${round}日目 夜フェーズ")
            nightPhase()

            checkWinner()?.let { endGame(it); return }

            println("\n▼ ${round}日目 昼フェーズ（議論）")
            discussionPhase()

            println("\n▼ ${round}日目 投票フェーズ")
            votePhase()

            checkWinner()?.let { endGame(it); return }

            round++
        }
    }

    private suspend fun nightPhase() {
        val alivePairs = alivePlayers().map { it.id to it.name }
        val situation = buildSituation("夜フェーズ", "夜の行動を選んでください")

        val humanPlayer = players.firstOrNull { it.isHuman && it.isAlive }
        when (humanPlayer?.role) {
            Role.SEER -> {
                if (round == 1) {
                    // 初日は必ず白が出る相手を自動選択（人狼以外からランダム）
                    val safeTarget = players
                        .filter { !it.isHuman && it.isAlive && it.role != Role.WEREWOLF }
                        .random()
                    println("【占い師】${safeTarget.name} を占いました → 【村人】でした")
                    aiPlayers[safeTarget.id]?.let {
                        // 占い済みとして記録
                    }
                } else {
                    println("【占い師】誰を占いますか？")
                    val target = humanChooseTarget(alivePairs, humanPlayer.id)
                    val targetPlayer = players.first { it.id == target }
                    val result = if (targetPlayer.role == Role.WEREWOLF) "人狼" else "村人"
                    println("→ ${targetPlayer.name} は【$result】でした")
                }
            }
            Role.KNIGHT -> {
                println("【騎士】誰を護衛しますか？")
                humanChooseTarget(alivePairs, humanPlayer.id)
                println("→ 護衛しました")
            }
            null -> println("（あなたはすでに死亡しています。観戦中...）")
            else -> println("（あなたは夜の行動がありません）")
        }

        var attackTarget: String? = null

        for ((id, aiPlayer) in aiPlayers.filter { players.find { p -> p.id == it.key }?.isAlive == true }) {
            val target = aiPlayer.nightAction(situation, alivePairs) ?: continue
            if (aiPlayer.role == Role.WEREWOLF) attackTarget = target
        }
        // 初日は噛みなし
        if (round == 1) {
            println("\n夜が明けました。初日のため犠牲者はいません")
            return
        }

        if (attackTarget != null) {
            val victim = players.first { it.id == attackTarget }
            victim.isAlive = false
            println("\n夜が明けました…")
            println("★ ${victim.name} が犠牲になりました")
            addChat("システム", "${victim.name}が夜の間に犠牲になりました")
        } else {
            println("\n夜が明けました。犠牲者はいませんでした")
        }
    }

    private suspend fun discussionPhase() {
        val discussionRounds = 3  // 1人が3回発言できる

        repeat(discussionRounds) { roundIndex ->
            println("\n--- 議論 ${roundIndex + 1}/${discussionRounds} ---")
            val alivePairs = alivePlayers().map { it.id to it.name }

            // AIが順番に発言
            for (player in alivePlayers().filter { !it.isHuman }) {
                val aiPlayer = aiPlayers[player.id] ?: continue
                val instruction = when (roundIndex) {
                    0 -> "状況を整理して、最初の印象を述べてください"
                    1 -> "他のプレイヤーの発言を踏まえて、具体的に誰が怪しいか意見を述べてください"
                    else -> "議論をまとめて、誰に投票すべきか主張してください"
                }
                val situation = buildSituation("昼の議論（${roundIndex + 1}回目）", instruction)
                val output = aiPlayer.discuss(situation, alivePairs)
                println("[${player.name}]: ${output.speech}")
                addChat(player.name, output.speech)
                delay(300)
            }

            // 人間の発言（死亡していたら表示しない）
            val humanPlayer = players.first { it.isHuman }
            if (humanPlayer.isAlive) {
                println("\n【あなたの発言】（Enterでスキップ）")
                print("> ")
                val input = readLine() ?: ""
                if (input.isNotBlank()) addChat(humanPlayer.name, input)
            } else {
                println("\n（あなたはすでに死亡しています。観戦中...）")
            }
        }
    }

    private suspend fun votePhase() {
        val alivePairs = alivePlayers().map { it.id to it.name }
        val votes = mutableMapOf<String, Int>()

        for (player in alivePlayers().filter { !it.isHuman }) {
            val aiPlayer = aiPlayers[player.id] ?: continue
            val situation = buildSituation("投票フェーズ", "最も怪しいと思うプレイヤーに投票してください")
            val output = aiPlayer.vote(situation, alivePairs)
            val targetName = players.find { it.id == output.targetId }?.name ?: "不明"
            println("[${player.name}] → ${targetName}に投票（${output.reason}）")
            votes[output.targetId] = (votes[output.targetId] ?: 0) + 1
        }

        // 人間の投票 → 死亡チェックを追加
        val humanPlayer = players.first { it.isHuman }
        if (humanPlayer.isAlive) {
            println("\n【あなたの投票】誰を処刑しますか？")
            val humanVote = humanChooseTarget(alivePairs, humanPlayer.id)
            votes[humanVote] = (votes[humanVote] ?: 0) + 1
        } else {
            println("\n（あなたはすでに死亡しています。投票できません）")
        }

        val executedId = votes.maxByOrNull { it.value }!!.key
        val executed = players.first { it.id == executedId }
        executed.isAlive = false
        println("\n投票結果：${executed.name} が処刑されました（役職：${executed.role.displayName()}）")
        addChat("システム", "${executed.name}が処刑されました")

        val isWerewolf = executed.role == Role.WEREWOLF
        aiPlayers.values
            .filter { players.first { p -> p.id == it.character.id }.isAlive }
            .forEach { it.notifyExecution(round, executed.name, isWerewolf) }
    }

    private fun checkWinner(): String? {
        val aliveWerewolves = alivePlayers().count { it.role == Role.WEREWOLF }
        val aliveVillagers  = alivePlayers().count { it.role != Role.WEREWOLF }
        return when {
            aliveWerewolves == 0              -> "村人陣営"
            aliveWerewolves >= aliveVillagers -> "人狼陣営"
            else                              -> null
        }
    }

    private fun endGame(winner: String) {
        println("\n============================")
        println("  ゲーム終了！【$winner の勝利】")
        println("============================")
        println("最終役職：")
        players.forEach { println("  ${it.name}：${it.role.displayName()}") }
    }

    private fun alivePlayers() = players.filter { it.isAlive }

    private fun buildSituation(phase: String, instruction: String) = GameSituation(
        round = round,
        phase = phase,
        alivePlayers = alivePlayers().map { it.name },
        chatLog = chatLog.takeLast(10),
        instruction = instruction
    )

    private fun addChat(name: String, message: String) {
        chatLog.add(name to message)
    }

    private fun humanChooseTarget(
        alivePairs: List<Pair<String, String>>,
        selfId: String
    ): String {
        val targets = alivePairs.filter { it.first != selfId }
        targets.forEachIndexed { i, (_, name) -> println("  ${i + 1}. $name") }
        while (true) {
            print("> ")
            val input = readLine()?.trim()?.toIntOrNull()
            if (input != null && input in 1..targets.size) {
                return targets[input - 1].first
            }
            println("1〜${targets.size} の数字を入力してください")
        }
    }
}

// ===== 役職の表示名・説明 =====

fun Role.displayName() = when (this) {
    Role.VILLAGER -> "村人"
    Role.WEREWOLF -> "人狼"
    Role.SEER     -> "占い師"
    Role.KNIGHT   -> "騎士"
    Role.MEDIUM   -> "霊能者"
    Role.LUNATIC  -> "狂人"
}

fun Role.roleDescription() = when (this) {
    Role.VILLAGER -> "人狼を見つけて処刑に導いてください"
    Role.WEREWOLF -> "バレないように村人を減らしてください"
    Role.SEER     -> "毎晩1人の役職を確認できます"
    Role.KNIGHT   -> "毎晩1人を人狼の襲撃から守れます"
    Role.MEDIUM   -> "処刑されたプレイヤーが人狼かどうかわかります"
    Role.LUNATIC  -> "人狼陣営ですが人狼が誰かは知りません。村人のふりをしてください"
}

// ===== エントリーポイント =====

fun main() = runBlocking {
    val apiKey = System.getenv("ANTHROPIC_API_KEY")
        ?: error("環境変数 ANTHROPIC_API_KEY を設定してください")

    val claudeClient = ClaudeClient(apiKey)

    try {
        print("あなたの名前を入力してください > ")
        val humanName = readLine()?.trim() ?: "プレイヤー"

        val engine = GameEngine(
            humanPlayerName = humanName,
            characterRepository = JsonCharacterRepository(),
            claudeClient = claudeClient
        )

        engine.setup()
        engine.run()

    } finally {
        claudeClient.close()
    }
}
