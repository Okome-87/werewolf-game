package com.werewolf.ai

class WerewolfStrategy : RoleStrategy {
    override fun buildNightSchema(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        checkedIds: List<String>
    ) = OutputSchema.nightTarget(alivePlayers, selfId)

    override fun recordNightAction(round: Int, targetId: String, targetName: String) =
        NightRecord()
}
