package com.werewolf.ai

class NoActionStrategy : RoleStrategy {
    override fun buildNightSchema(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        checkedIds: List<String>
    ) = null

    override fun recordNightAction(round: Int, targetId: String, targetName: String) =
        NightRecord()
}
