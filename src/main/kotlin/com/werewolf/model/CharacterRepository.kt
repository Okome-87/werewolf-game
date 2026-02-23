package com.werewolf.model

interface CharacterRepository {
    fun loadAll(): List<AICharacter>
}
