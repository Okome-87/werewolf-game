package com.werewolf.ai

import com.werewolf.model.DiscussionOutput
import com.werewolf.model.VoteOutput
import kotlinx.serialization.json.*

object OutputParser {

    private val json = Json { ignoreUnknownKeys = true }

    private fun extractJson(raw: String): String {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start == -1 || end == -1) throw IllegalArgumentException("JSONなし: $raw")
        return raw.substring(start, end + 1)
    }

    fun parseDiscussion(raw: String, alivePlayers: List<Pair<String, String>>): DiscussionOutput {
        return try {
            val obj = json.decodeFromString<DiscussionOutput>(extractJson(raw))
            val validIds = alivePlayers.map { it.first }.toSet()
            DiscussionOutput(
                speech = obj.speech.take(100),
                suspectId = obj.suspectId?.takeIf { it in validIds }
            )
        } catch (e: Exception) {
            DiscussionOutput(speech = "…。", suspectId = null)
        }
    }

    fun parseVote(raw: String, validTargetIds: List<String>, selfId: String): VoteOutput {
        val validIds = (validTargetIds - selfId).toSet()
        return try {
            val obj = json.decodeFromString<VoteOutput>(extractJson(raw))
            VoteOutput(
                targetId = if (obj.targetId in validIds) obj.targetId else validIds.random(),
                reason = obj.reason.take(50)
            )
        } catch (e: Exception) {
            VoteOutput(targetId = validIds.random(), reason = "直感だ")
        }
    }

    fun parseNightTarget(raw: String, validTargetIds: List<String>): String {
        return try {
            val obj = json.parseToJsonElement(extractJson(raw)).jsonObject
            val id = obj["targetId"]?.jsonPrimitive?.content
            if (id != null && id in validTargetIds) id else validTargetIds.random()
        } catch (e: Exception) {
            validTargetIds.random()
        }
    }
}
