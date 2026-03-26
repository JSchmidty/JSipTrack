package com.siptech.siptrack.repository

import com.siptech.siptrack.models.Drink
import com.siptech.siptrack.models.DrinkSession
import com.siptech.siptrack.models.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for drink session data.
 * Platform-specific implementations (Android/iOS/watchOS) provide the actual storage.
 */
interface DrinkRepository {
    // Sessions
    suspend fun createSession(session: DrinkSession): DrinkSession
    suspend fun updateSession(session: DrinkSession): DrinkSession
    suspend fun endSession(sessionId: String)
    suspend fun getSession(sessionId: String): DrinkSession?
    suspend fun getActiveSession(): DrinkSession?
    fun observeActiveSession(): Flow<DrinkSession?>
    fun observeAllSessions(): Flow<List<DrinkSession>>
    suspend fun getAllSessions(): List<DrinkSession>
    suspend fun deleteSession(sessionId: String)

    // Drinks
    suspend fun addDrink(drink: Drink): Drink
    suspend fun updateDrink(drink: Drink): Drink
    suspend fun deleteDrink(drinkId: String)
    suspend fun getDrinksForSession(sessionId: String): List<Drink>

    // Profile
    suspend fun getProfile(): UserProfile
    suspend fun saveProfile(profile: UserProfile)
    fun observeProfile(): Flow<UserProfile>

    // Data management
    suspend fun exportSessionsAsCsv(): String
    suspend fun clearAllData()
}
