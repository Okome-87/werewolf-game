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

- 小さい改修：`issue-xxx` → `develop` → `main`
- 大きい改修：`issue-xxx` → `base-xxx` → `develop` → `main`

### ブランチ作成

作業開始時は指示されたブランチがあればそこから切ること。
特に指示がない場合はdevelopから切ること。

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
## Claude Codeが使用するghコマンド一覧

gh issue create    # Issue作成
gh issue list      # Issue一覧確認
gh issue view {番号}  # Issue詳細確認
gh pr create       # PR作成
gh pr list         # PR一覧確認
gh pr view {番号}  # PR詳細確認
