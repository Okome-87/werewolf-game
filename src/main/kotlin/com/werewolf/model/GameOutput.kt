package com.werewolf.model

import kotlinx.serialization.Serializable

@Serializable
data class DiscussionOutput(
    val speech: String,
    val suspectId: String? = null
)

@Serializable
data class VoteOutput(
    val targetId: String,
    val reason: String
)
