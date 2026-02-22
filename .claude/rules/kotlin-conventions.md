# Kotlinコード規約（AI向け）

detektが数値・機械的にチェックするルールはここに書かない。
臨機応変な判断が必要なルールのみ記載する。

---

## 命名

クラス・関数・変数名は「それが何をするものか」が名前から伝わるように付ける。
`Manager` `Wrapper` `Util` のような意味の薄い単語は避ける。

```kotlin
// ✅
fun buildSystemPrompt(character: AICharacter, role: String): String
val alivePlayers = players.filter { it.isAlive }

// ❌
fun process(): String
val data = players.filter { it.isAlive }
```

頭字語は2文字なら全大文字、3文字以上は先頭のみ大文字。

```kotlin
// ✅ AIPlayer / HttpClient
// ❌ AiPlayer / HTTPClient
```

---

## 関数 vs プロパティ

以下をすべて満たすなら関数ではなくプロパティにする。

- 例外を投げない
- 計算コストが低い
- 状態が変わらなければ同じ値を返す

```kotlin
// ✅ プロパティが適切
val alivePlayers: List<Player> get() = players.filter { it.isAlive }

// ✅ 関数が適切（API呼び出しを伴う）
suspend fun generate(prompt: String): String
```

---

## `if` vs `when`

2択は `if`、3択以上は `when`。

役職ごとの分岐は必ず `when` で全ケースを網羅し、`else` に逃げない。
新しい役職が追加されたときにコンパイルエラーで気づけるようにするため。

```kotlin
// ✅
when (role) {
    Role.WEREWOLF -> buildWerewolfPrompt()
    Role.SEER     -> buildSeerPrompt()
    Role.KNIGHT   -> buildKnightPrompt()
    Role.MEDIUM   -> buildMediumPrompt()
    Role.MADMAN   -> buildMadmanPrompt()
    Role.VILLAGER -> buildVillagerPrompt()
}

// ❌ else で逃げると役職追加時に気づけない
when (role) {
    Role.WEREWOLF -> buildWerewolfPrompt()
    else          -> buildVillagerPrompt()
}
```

---

## イミュータブル優先

公開APIでは `MutableList` を返さない。内部で変更が必要なら内側でのみ使い、外には `List` として公開する。

```kotlin
// ✅
private val _players = mutableListOf<Player>()
val players: List<Player> get() = _players

// ❌
val players = mutableListOf<Player>()
```

---

## スコープ関数の使い分け

| 関数 | 使う場面 |
|---|---|
| `let` | null チェックして処理する |
| `apply` | オブジェクトの初期化・設定をまとめる |
| `also` | ログ出力など副作用を追加する |
| `run` | 複数のプロパティを使って値を返す |
| `with` | 同じオブジェクトへの複数操作をまとめる |

迷ったら `let` か `apply` で十分。多用しすぎると可読性が下がる。

---

## マルチライン文字列

プロンプト文字列は `trimIndent()` を使ったマルチライン文字列で書く。
結合演算子（`+`）や `\n` の埋め込みは使わない。

```kotlin
// ✅
val prompt = """
    あなたは${character.name}です。
    役職は${role}です。
""".trimIndent()

// ❌
val prompt = "あなたは" + character.name + "です。\n役職は" + role + "です。"
```

---

## 拡張関数

特定のクラスに強く関連する処理は拡張関数にする。
ただし汎用処理でなければトップレベルに置かず、使う側のクラス内にとどめる。

```kotlin
// ✅ Role に関連する表示処理は拡張関数として定義
fun Role.displayName(): String = when (this) { ... }
fun Role.roleDescription(): String = when (this) { ... }
```
