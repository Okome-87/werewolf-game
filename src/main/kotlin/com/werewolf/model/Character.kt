package com.werewolf.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterParameters(
    val logic: Int,         // 論理性    1-5
    val verbosity: Int,     // 発言量    1-5
    val assertiveness: Int, // 主張の強さ 1-5
    val empathy: Int,       // 共感性    1-5
    val deduction: Int,     // 推論深度  1-5
    val suspicion: Int,     // 疑心度    1-5
    val adaptability: Int,  // 適応性    1-5
    val charisma: Int,      // 魅力度    1-5
    val deception: Int,     // 欺瞞性    1-5
    val consistency: Int,   // 一貫性    1-5
    val initiative: Int,    // 積極性    1-5
    val trust: Int,         // 信頼構築  1-5
    val volatility: Int     // 感情波長  1-5
)

@Serializable
data class AICharacter(
    val id: String,
    val name: String,
    val age: Int,
    val background: String,
    val speechStyle: String,
    val personality: String,
    val strategy: String,
    val catchphrases: List<String>,
    val params: CharacterParameters,
    val difficultyLevel: Int = 2  // 1=初心者 / 2=普通 / 3=熟練
)
