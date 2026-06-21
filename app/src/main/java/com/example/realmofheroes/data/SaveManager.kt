package com.example.realmofheroes.data

import android.content.Context
import com.example.realmofheroes.model.CharacterSaveState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Owns all persistent player progress: currency, per-character level/XP/unlock
 * state, and how many stages have been cleared. Uses SharedPreferences + Gson
 * for simplicity — fine at this data scale, and avoids pulling in a full Room
 * database for what's currently a handful of small records.
 */
class SaveManager(context: Context) {

    private val prefs = context.getSharedPreferences("realm_of_heroes_save", Context.MODE_PRIVATE)
    private val gson = Gson()

    var currency: Int
        get() = prefs.getInt(KEY_CURRENCY, 0)
        set(value) { prefs.edit().putInt(KEY_CURRENCY, value).apply() }

    /** Highest stage number the player has cleared. Stage (highestStageCleared + 1) is the next playable one. */
    var highestStageCleared: Int
        get() = prefs.getInt(KEY_HIGHEST_STAGE, 0)
        set(value) { prefs.edit().putInt(KEY_HIGHEST_STAGE, value).apply() }

    fun getCharacterSaveStates(): MutableMap<String, CharacterSaveState> {
        val json = prefs.getString(KEY_CHARACTER_STATES, null)
        val result: MutableMap<String, CharacterSaveState> = if (json != null) {
            val type = object : TypeToken<MutableMap<String, CharacterSaveState>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableMapOf()
        }

        // Ensure every roster character has a save entry, defaulting starter
        // characters (unlockCost == 0) to unlocked on first run.
        for (template in CharacterRoster.all) {
            if (!result.containsKey(template.id)) {
                result[template.id] = CharacterSaveState(
                    templateId = template.id,
                    unlocked = template.unlockCost == 0
                )
            }
        }
        return result
    }

    fun saveCharacterStates(states: Map<String, CharacterSaveState>) {
        prefs.edit().putString(KEY_CHARACTER_STATES, gson.toJson(states)).apply()
    }

    fun getCharacterState(templateId: String): CharacterSaveState {
        return getCharacterSaveStates()[templateId]
            ?: CharacterSaveState(templateId, unlocked = CharacterRoster.byId(templateId).unlockCost == 0)
    }

    fun unlockCharacter(templateId: String): Boolean {
        val template = CharacterRoster.byId(templateId)
        if (currency < template.unlockCost) return false

        val states = getCharacterSaveStates()
        val state = states[templateId] ?: CharacterSaveState(templateId)
        if (state.unlocked) return true // already unlocked, no-op success

        currency -= template.unlockCost
        state.unlocked = true
        states[templateId] = state
        saveCharacterStates(states)
        return true
    }

    /** Applies battle rewards: currency + XP (with possible level-ups) for the given character. */
    fun applyBattleRewards(templateId: String, currencyEarned: Int, xpEarned: Int): Int {
        currency += currencyEarned

        val states = getCharacterSaveStates()
        val state = states[templateId] ?: CharacterSaveState(templateId, unlocked = true)
        val levelsGained = state.addXp(xpEarned)
        states[templateId] = state
        saveCharacterStates(states)
        return levelsGained
    }

    fun markStageCleared(stageNumber: Int) {
        if (stageNumber > highestStageCleared) {
            highestStageCleared = stageNumber
        }
    }

    fun isStageUnlocked(stageNumber: Int): Boolean {
        return stageNumber <= highestStageCleared + 1
    }

    companion object {
        private const val KEY_CURRENCY = "currency"
        private const val KEY_HIGHEST_STAGE = "highest_stage_cleared"
        private const val KEY_CHARACTER_STATES = "character_states"
    }
}
