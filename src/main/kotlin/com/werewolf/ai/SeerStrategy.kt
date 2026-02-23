package com.werewolf.ai

class SeerStrategy : RoleStrategy {
    override fun buildNightSchema(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        checkedIds: List<String>
    ) = OutputSchema.nightTarget(alivePlayers, selfId, checkedIds)

    override fun recordNightAction(round: Int, targetId: String, targetName: String) = NightRecord(
        importantAction = "${round}日目の夜：${targetName}を占った（結果は昼の議論でCOすること）",
        pendingReport = "${targetName}を占いました。結果を今日の議論でCOしてください",
        markChecked = true
    )
}
