package com.werewolf.ai

data class NightRecord(
    val importantAction: String? = null,
    val pendingReport: String? = null,
    val markChecked: Boolean = false
)

interface RoleStrategy {
    fun buildNightSchema(
        alivePlayers: List<Pair<String, String>>,
        selfId: String,
        checkedIds: List<String>
    ): String?

    fun recordNightAction(
        round: Int,
        targetId: String,
        targetName: String
    ): NightRecord

    fun recordExecution(round: Int, executedName: String, isWerewolf: Boolean): String? = null
}
