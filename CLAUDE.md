# werewolf-game

Kotlin + Ktor + Claude API で動くコンソール人狼ゲーム。1人の人間プレイヤー vs 8人のAIキャラクター（9人村）。

## コマンド

```bash
./gradlew run          # 実行
./gradlew build        # ビルド
./gradlew test         # テスト
```

実行には IntelliJ の Run Configurations → Environment variables に `ANTHROPIC_API_KEY=sk-ant-...` の設定が必要。

## アーキテクチャ

```
src/main/kotlin/com/werewolf/
├── Application.kt          # GameEngine・エントリーポイント
├── model/
│   ├── Character.kt        # AIキャラクター定義
│   ├── GameModels.kt       # Player・Role・GameSituation
│   └── GameOutput.kt       # LLM出力のデータクラス
└── ai/
    ├── ClaudeClient.kt     # API通信・リトライ処理
    ├── AIPlayer.kt         # discuss / vote / nightAction
    ├── PromptBuilder.kt    # パラメータ→プロンプト変換
    ├── WerewolfKnowledge.kt # 役職別戦略・難易度指示
    ├── OutputSchema.kt     # フェーズ別JSONスキーマ
    └── OutputParser.kt     # JSONパース・バリデーション
```

## 注意事項

- LLMへの入出力はすべて構造化JSON。スキーマは `OutputSchema.kt` で定義し、パースは `OutputParser.kt` で行う
- AIの発言・投票・夜行動はすべて `AIPlayer.kt` に集約する。ゲームロジック（`Application.kt`）にLLM呼び出しを直接書かない
- 占い結果の黒/白はLLMに判断させず、`allRoles: Map<String, Role>` からコード側で決定する
- 人狼の仲間情報（`werewolfAllies`）はシステムプロンプトに注入する。LLMが自己判断で仲間を決めないようにする

## 参照ドキュメント

- ゲームルール → @.claude/rules/game-rules.md
- 実装詳細 → @.claude/rules/implementation.md
- コード規約 → @.claude/rules/kotlin-conventions.md
