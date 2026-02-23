# GitHub運用ルール

## ブランチ戦略

```
main
└── develop
     ├── issue-{番号}-{概要}   # 小さい改修・単一タスク
     └── base/{機能名}                 # 大きい改修
          ├── issue-{番号}-{概要}
          └── issue-{番号}-{概要}
```

### マージの流れ

- 小さい改修：`issue` → `develop` → `main`
- 大きい改修：`issue` → `base` → `develop` → `main`

### ブランチ作成

作業開始時は必ず `develop` から切ること。

```bash
git checkout develop
git pull origin develop
git checkout -b issue-{番号}-{概要}
```

---

## Issueルール

### 作成タイミング
作業前に必ずIssueを作成する。Issueなしでの実装・PRは禁止。

### タイトル
```
issue-{番号}-{概要}

```

### 本文テンプレート
```
## 概要
（何の問題か・何をしたいか）

## 対応方針
- （箇条書きで）

## 完了条件
- （何ができたら完了か）
```

---

## PRルール

### タイトル
Conventional Commits形式で書く。

```
fix: 占い師が誤った結果を出すバグを修正 #1
feat: 霊能者の夜フェーズを実装 #3
refactor: PromptBuilderの役職分岐をwhenに統一 #5
docs: game-rules.mdに定石を追加 #7
```

typeの種類：
- `fix` : バグ修正
- `feat` : 機能追加
- `refactor` : リファクタリング
- `docs` : ドキュメントのみの変更

### 本文テンプレート
```
## 概要
（何をしたか1〜2行で）

## 対応内容
- （変更点を箇条書きで）

## 関連Issue
fixes #XX
```

### PR作成コマンド
```bash
gh pr create \
  --base develop \
  --title "fix: タイトル #Issue番号" \
  --body "## 概要
（概要）

## 対応内容
- （変更点）

## 関連Issue
fixes #XX"
```

---

## 禁止事項

- `gh pr merge` の実行（マージは必ず人間がGitHub上で行う）
- `gh repo delete` の実行
- Issueなしでのブランチ作成・PR作成
- `main` / `develop` への直接コミット
