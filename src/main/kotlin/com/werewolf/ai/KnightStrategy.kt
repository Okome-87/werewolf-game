package com.werewolf.ai

class KnightStrategy : RoleStrategy {
    override fun buildNightSchema(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        checkedIds: List<String>
    ) = OutputSchema.nightTarget(alivePlayers, selfId)

    override fun recordNightAction(round: Int, targetId: String, targetName: String) = NightRecord(
        importantAction = "${round}日目の夜：${targetName}を護衛した"
    )
}
