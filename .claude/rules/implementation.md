# 設計方針

このファイルには「なぜそう設計するか」を記録する。
実装の詳細（メソッドの中身・修正コード例）は書かない。

---

## 入出力をロジックから分離する

将来の Web 化・アプリ化に備え、入出力をインターフェースで抽象化する。
ゲームロジック（`GameEngine`）は入出力の実装を知らなくてよい。

```
GameEngine
    ↓ 依存
InputPort（interface）    OutputPort（interface）
    ↑ 実装                    ↑ 実装
ConsoleInput             ConsoleOutput
WebSocketInput           WebSocketOutput
```

**方針：**

- `GameEngine` 内で `println()` や `readLine()` を直接呼ばず、入出力インターフェースを経由する

---

## キャラクター設定をコードから分離する

AI キャラクターの定義（名前・年齢・パラメータ値など）をコードにベタ書きしない。
`characters.json` などのプロパティファイルに切り出し、起動時に読み込む。

**理由：**

- キャラクター追加・調整のたびにコードの再コンパイルが必要になるのを避ける
- 将来的にユーザーがキャラクターをカスタマイズできる余地を残す

```
resources/
└── characters.json    # キャラクター定義
└── roles.json         # 役職構成パターン（将来の複数構成対応）
```

---

## ロジックの構造をインターフェースと継承で明示する

役職ごとの振る舞いの違いは、型で表現する。
`when (role)` による分岐をビジネスロジックの中心に置かない。

**方針：**

- 夜の行動・議論戦略など役職ごとに異なる振る舞いは `RoleStrategy` インターフェースで定義する
- 具体的な役職クラス（`WerewolfStrategy`・`SeerStrategy` など）が実装する
- `AIPlayer` は `RoleStrategy` に依存し、具体クラスを知らない

**`when (role)` を書いてよい場所：**

- `RoleStrategy` のファクトリ生成箇所（1 箇所に集約）

---

## デザインパターンの適用方針

### Strategy パターン

役職ごとの行動の違いを表現する（上述）。
`if/when` による役職分岐をロジック全体に散らばせない。

### Factory パターン

`RoleStrategy` の生成は Factory に集約する。
役職が追加されたとき、修正箇所が 1 箇所になるようにする。

### Repository パターン

キャラクター定義の読み込みは `CharacterRepository` インターフェースで抽象化する。
`GameEngine` はファイル形式（JSON・YAML など）を知らなくてよい。

---

## LLM 使用方針

### コード側で決定できる内容は LLM に出力させない

- 占い/霊能結果の黒/白 → `allRoles` からコード側で決定する（LLM に判断させない）
- 襲撃/護衛結果の成功/失敗 → `allRoles` からコード側で決定する（LLM に判断させない）

### LLM の出力を信頼しない

LLM が返す値をそのままゲームロジックに使わない。
不正な値・矛盾した値はコード側で検証・補正する。

- 投票先・行動対象（占い、霊能、襲撃、護衛など） → `OutputParser` でバリデーションし、不正値はフォールバックする
- LLM への入出力はすべて構造化 JSON。スキーマは `OutputSchema` で定義する

## LLM 呼び出しの集約

`ClaudeClient` の呼び出しは `AIPlayer` のみが行う。
`GameEngine` やその他のクラスから直接呼び出さない。

**理由：**

- LLM の差し替え（モデル変更・モック化）の影響範囲を `AIPlayer` と `ClaudeClient` に限定する
- テスト時に `AIPlayer` をモックにすれば `GameEngine` のロジックを LLM なしで検証できる
