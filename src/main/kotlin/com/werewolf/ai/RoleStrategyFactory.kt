package com.werewolf.ai

import com.werewolf.model.Role

object RoleStrategyFactory {
    fun create(role: Role): RoleStrategy = when (role) {
        Role.WEREWOLF -> WerewolfStrategy()
        Role.SEER     -> SeerStrategy()
        Role.KNIGHT   -> KnightStrategy()
        Role.MEDIUM   -> MediumStrategy()
        Role.VILLAGER -> NoActionStrategy()
        Role.LUNATIC  -> NoActionStrategy()
    }
}
