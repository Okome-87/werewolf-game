package com.werewolf.ai

import com.werewolf.model.MediumResult

class MediumStrategy : RoleStrategy {
    override fun buildNightSchema(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        checkedIds: List<String>
    ) = OutputSchema.nightTarget(alivePlayers, selfId)

    override fun recordNightAction(round: Int, targetId: String, targetName: String) = NightRecord(
        importantAction = "${round}日目の夜：${targetName}の霊を見ました（結果は昼の議論でCOすること）",
        pendingReport = "${targetName}の霊を見ました。結果を今日の議論でCOしてください",
        markChecked = true
    )

    override fun recordExecution(round: Int, executedName: String, mediumResult: MediumResult): String {
        return "${round}日目：${executedName}が処刑された → 霊能結果【${mediumResult.displayName}】（昼の議論でCOすること）"
    }
}
