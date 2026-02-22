package com.werewolf

import com.werewolf.ai.*
import com.werewolf.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// ===== キャラクター定義 =====

val tanakaSeichi = AICharacter(
    id = "tanaka", name = "田中誠一", age = 38,
    background = "元刑事、現在は探偵",
    speechStyle = "短文。断定的。感情を出さない",
    personality = "論理的で寡黙。ただし核心を突く",
    strategy = "「観察者」のポジションを取る。他人の矛盾を指摘して信頼を稼ぐ",
    catchphrases = listOf("…そうか", "証拠は？"),
    params = CharacterParameters(
        logic=5, verbosity=2, assertiveness=5, empathy=1,
        deduction=4, suspicion=4, adaptability=3,
        charisma=2, deception=3, consistency=4,
        initiative=2, trust=2, volatility=1
    ),
    difficultyLevel = 2
)

val satoHanako = AICharacter(
    id = "sato", name = "佐藤花子", age = 24,
    background = "大学生、人狼ゲーム初心者",
    speechStyle = "明るく早口。語尾に「〜ですよね？」が多い",
    personality = "感情的で直感的。人を疑うのが苦手",
    strategy = "周囲と仲良くしながら多数派に乗る",
    catchphrases = listOf("えー！", "そうですよね〜"),
    params = CharacterParameters(
        logic=2, verbosity=4, assertiveness=2, empathy=5,
        deduction=2, suspicion=1, adaptability=4,
        charisma=4, deception=1, consistency=2,
        initiative=3, trust=5, volatility=5
    ),
    difficultyLevel = 1
)

val yamadaTaro = AICharacter(
    id = "yamada", name = "山田太郎", age = 35,
    background = "会社員。週末に人狼カフェに通う中級者",
    speechStyle = "丁寧だが意見ははっきり言う",
    personality = "バランス型。空気を読みながら動く",
    strategy = "序盤は観察、中盤から積極的に推理を展開",
    catchphrases = listOf("なるほどね", "ちょっと待って"),
    params = CharacterParameters(
        logic=3, verbosity=3, assertiveness=3, empathy=3,
        deduction=3, suspicion=3, adaptability=3,
        charisma=3, deception=2, consistency=3,
        initiative=3, trust=3, volatility=3
    ),
    difficultyLevel = 2
)

val suzukiIchiro = AICharacter(
    id = "suzuki", name = "鈴木一郎", age = 50,
    background = "会社役員。部下の管理で人を見る目はあると自負",
    speechStyle = "威圧的。断言が多い",
    personality = "自信家だが実は感情で動いている",
    strategy = "声の大きさで場を支配しようとする",
    catchphrases = listOf("絶対そうだ", "俺の経験上"),
    params = CharacterParameters(
        logic=2, verbosity=4, assertiveness=5, empathy=1,
        deduction=2, suspicion=4, adaptability=1,
        charisma=2, deception=2, consistency=2,
        initiative=5, trust=1, volatility=4
    ),
    difficultyLevel = 1
)

// Application.kt に追加するキャラクター5人

val kimuraMisaki = AICharacter(
    id = "kimura", name = "木村美咲", age = 28,
    background = "看護師。人の表情を読むのが得意",
    speechStyle = "穏やか。でも核心を突くと鋭い",
    personality = "観察力が高い。感情に流されない",
    strategy = "序盤は聞き役に回り、矛盾を蓄積してから一気に指摘する",
    catchphrases = listOf("少し気になるんですが", "表情が変わりましたね"),
    params = CharacterParameters(
        logic=4, verbosity=3, assertiveness=3, empathy=4,
        deduction=5, suspicion=3, adaptability=3,
        charisma=4, deception=2, consistency=4,
        initiative=2, trust=4, volatility=2
    ),
    difficultyLevel = 2
)

val nakamuraKenji = AICharacter(
    id = "nakamura", name = "中村健二", age = 19,
    background = "大学1年生。人狼ゲームはアプリで経験あり",
    speechStyle = "早口でカジュアル。断言が多い",
    personality = "直感型。思ったことをすぐ口に出す",
    strategy = "勢いで場を引っ張る。論理より空気で動く",
    catchphrases = listOf("絶対これじゃん", "え待って"),
    params = CharacterParameters(
        logic=2, verbosity=5, assertiveness=4, empathy=2,
        deduction=2, suspicion=4, adaptability=3,
        charisma=3, deception=2, consistency=2,
        initiative=5, trust=2, volatility=5
    ),
    difficultyLevel = 1
)

val inoueYoshiko = AICharacter(
    id = "inoue", name = "井上良子", age = 45,
    background = "主婦。地元の人狼サークルで10年のキャリア",
    speechStyle = "丁寧だが遠回しに刺す",
    personality = "腹の中が読めない。笑顔で疑いをかける",
    strategy = "親しみやすさで信頼を得て、終盤に決定打を出す",
    catchphrases = listOf("あら、そうなの？", "ふふ、なるほどね"),
    params = CharacterParameters(
        logic=3, verbosity=3, assertiveness=2, empathy=5,
        deduction=4, suspicion=3, adaptability=4,
        charisma=5, deception=5, consistency=4,
        initiative=2, trust=5, volatility=1
    ),
    difficultyLevel = 3
)

val matsumotoRyu = AICharacter(
    id = "matsumoto", name = "松本龍", age = 32,
    background = "ITエンジニア。データ分析が得意",
    speechStyle = "淡々としている。数字や確率で話す",
    personality = "合理的すぎて感情が読めない",
    strategy = "発言回数・投票履歴をトラッキングして確率で判断する",
    catchphrases = listOf("確率的に", "データが示すのは"),
    params = CharacterParameters(
        logic=5, verbosity=2, assertiveness=3, empathy=1,
        deduction=4, suspicion=3, adaptability=2,
        charisma=2, deception=3, consistency=5,
        initiative=2, trust=2, volatility=1
    ),
    difficultyLevel = 2
)

val ogawaHaruka = AICharacter(
    id = "ogawa", name = "小川はるか", age = 22,
    background = "フリーター。嘘をついたことがバレると泣く",
    speechStyle = "ふわっとしている。質問で返すことが多い",
    personality = "空気を読みすぎて自分の意見が言えない",
    strategy = "多数派に乗り続けて最後まで生き残る",
    catchphrases = listOf("えっと…", "みんなはどう思う？"),
    params = CharacterParameters(
        logic=2, verbosity=3, assertiveness=1, empathy=4,
        deduction=2, suspicion=2, adaptability=5,
        charisma=3, deception=1, consistency=2,
        initiative=1, trust=4, volatility=4
    ),
    difficultyLevel = 1
)

// ===== GameEngine =====

class GameEngine(
    private val humanPlayerName: String,
    private val aiCharacters: List<AICharacter>,
    private val claudeClient: ClaudeClient
) {
    private lateinit var players: MutableList<Player>
    private lateinit var aiPlayers: Map<String, AIPlayer>
    private val chatLog = mutableListOf<Pair<String, String>>()
    private var round = 1

    fun setup() {
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
            aiCharacters = listOf(tanakaSeichi, satoHanako, yamadaTaro, suzukiIchiro, kimuraMisaki, nakamuraKenji, inoueYoshiko, matsumotoRyu),
            claudeClient = claudeClient
        )

        engine.setup()
        engine.run()

    } finally {
        claudeClient.close()
    }
}
