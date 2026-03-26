package com.siptech.siptrack.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.siptech.siptrack.db.SipTrackDatabase
import com.siptech.siptrack.models.AppMode
import com.siptech.siptrack.models.BiologicalSex
import com.siptech.siptrack.models.Drink
import com.siptech.siptrack.models.DrinkCategory
import com.siptech.siptrack.models.DrinkSession
import com.siptech.siptrack.models.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SqlDelightDrinkRepository(private val database: SipTrackDatabase) : DrinkRepository {

    private val queries = database.sipTrackDatabaseQueries

    // ── Sessions ──────────────────────────────────────────────────────────────

    override suspend fun createSession(session: DrinkSession): DrinkSession {
        queries.insertSession(
            id = session.id,
            start_time = session.startTime.toEpochMilliseconds(),
            end_time = session.endTime?.toEpochMilliseconds(),
            mode = session.mode.name,
            notes = session.notes,
        )
        return session
    }

    override suspend fun updateSession(session: DrinkSession): DrinkSession {
        queries.updateSession(
            end_time = session.endTime?.toEpochMilliseconds(),
            notes = session.notes,
            id = session.id,
        )
        return session
    }

    override suspend fun endSession(sessionId: String) {
        queries.endSession(
            end_time = Clock.System.now().toEpochMilliseconds(),
            id = sessionId,
        )
    }

    override suspend fun getSession(sessionId: String): DrinkSession? {
        val entity = queries.getSession(sessionId).executeAsOneOrNull() ?: return null
        val drinks = getDrinksForSession(sessionId)
        return entity.toModel(drinks)
    }

    override suspend fun getActiveSession(): DrinkSession? {
        val entity = queries.getActiveSession().executeAsOneOrNull() ?: return null
        val drinks = getDrinksForSession(entity.id)
        return entity.toModel(drinks)
    }

    override fun observeActiveSession(): Flow<DrinkSession?> {
        return queries.getActiveSession().asFlow().mapToOneOrNull(Dispatchers.Default).map { entity ->
            entity?.let {
                val drinks = getDrinksForSession(it.id)
                it.toModel(drinks)
            }
        }
    }

    override fun observeAllSessions(): Flow<List<DrinkSession>> {
        return queries.getAllSessions().asFlow().mapToList(Dispatchers.Default).map { entities ->
            entities.map { entity ->
                val drinks = getDrinksForSession(entity.id)
                entity.toModel(drinks)
            }
        }
    }

    override suspend fun getAllSessions(): List<DrinkSession> {
        return queries.getAllSessions().executeAsList().map { entity ->
            val drinks = getDrinksForSession(entity.id)
            entity.toModel(drinks)
        }
    }

    override suspend fun deleteSession(sessionId: String) {
        queries.deleteSession(sessionId)
    }

    // ── Drinks ────────────────────────────────────────────────────────────────

    override suspend fun addDrink(drink: Drink): Drink {
        queries.insertDrink(
            id = drink.id,
            session_id = drink.sessionId,
            name = drink.name,
            category = drink.category.name,
            abv_percent = drink.abvPercent.toDouble(),
            volume_oz = drink.volumeOz.toDouble(),
            logged_at = drink.loggedAt.toEpochMilliseconds(),
            product_id = drink.productId,
            notes = drink.notes,
            is_micro_dose = if (drink.isMicroDose) 1L else 0L,
        )
        return drink
    }

    override suspend fun updateDrink(drink: Drink): Drink {
        queries.updateDrink(
            name = drink.name,
            abv_percent = drink.abvPercent.toDouble(),
            volume_oz = drink.volumeOz.toDouble(),
            notes = drink.notes,
            id = drink.id,
        )
        return drink
    }

    override suspend fun deleteDrink(drinkId: String) {
        queries.deleteDrink(drinkId)
    }

    override suspend fun getDrinksForSession(sessionId: String): List<Drink> {
        return queries.getDrinksForSession(sessionId).executeAsList().map { it.toModel() }
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    override suspend fun getProfile(): UserProfile {
        return queries.getProfile().executeAsOneOrNull()?.toModel() ?: UserProfile()
    }

    override suspend fun saveProfile(profile: UserProfile) {
        queries.upsertProfile(
            weight_kg = profile.weightKg.toDouble(),
            gender = profile.gender.name,
            age = profile.age.toLong(),
            metabolic_rate = profile.metabolicRate.toDouble(),
            personal_limit_bac = profile.personalLimitBac.toDouble(),
            drive_limit_bac = profile.driveLimitBac.toDouble(),
            emergency_contact_name = profile.emergencyContactName,
            emergency_contact_phone = profile.emergencyContactPhone,
            prefer_metric = if (profile.preferMetric) 1L else 0L,
            enable_notifications = if (profile.enableNotifications) 1L else 0L,
            enable_health_kit = if (profile.enableHealthKit) 1L else 0L,
            app_mode = profile.appMode.name,
            onboarding_complete = if (profile.onboardingComplete) 1L else 0L,
        )
    }

    override fun observeProfile(): Flow<UserProfile> {
        return queries.getProfile().asFlow().mapToOneOrNull(Dispatchers.Default).map { entity ->
            entity?.toModel() ?: UserProfile()
        }
    }

    // ── Data management ───────────────────────────────────────────────────────

    override suspend fun exportSessionsAsCsv(): String {
        val sessions = getAllSessions()
        val sb = StringBuilder()
        sb.appendLine("session_id,start_time,end_time,drink_name,abv_percent,volume_oz,standard_drinks,calories,logged_at")
        sessions.forEach { session ->
            if (session.drinks.isEmpty()) {
                sb.appendLine("${session.id},${session.startTime},${session.endTime ?: ""},,,,,, ")
            } else {
                session.drinks.forEach { drink ->
                    sb.appendLine(
                        "${session.id},${session.startTime},${session.endTime ?: ""}," +
                        "${drink.name},${drink.abvPercent},${drink.volumeOz}," +
                        "${drink.standardDrinks},${drink.calories},${drink.loggedAt}"
                    )
                }
            }
        }
        return sb.toString()
    }

    override suspend fun clearAllData() {
        queries.deleteAllDrinks()
        queries.deleteAllSessions()
        queries.deleteProfile()
    }
}

// ── Mapping extensions ────────────────────────────────────────────────────────

private fun com.siptech.siptrack.db.DrinkSessionEntity.toModel(drinks: List<Drink>): DrinkSession {
    return DrinkSession(
        id = id,
        startTime = Instant.fromEpochMilliseconds(start_time),
        endTime = end_time?.let { Instant.fromEpochMilliseconds(it) },
        drinks = drinks,
        mode = AppMode.valueOf(mode),
        notes = notes,
    )
}

private fun com.siptech.siptrack.db.DrinkEntity.toModel(): Drink {
    return Drink(
        id = id,
        sessionId = session_id,
        name = name,
        category = DrinkCategory.valueOf(category),
        abvPercent = abv_percent.toFloat(),
        volumeOz = volume_oz.toFloat(),
        loggedAt = Instant.fromEpochMilliseconds(logged_at),
        productId = product_id,
        notes = notes,
        isMicroDose = is_micro_dose == 1L,
    )
}

private fun com.siptech.siptrack.db.UserProfileEntity.toModel(): UserProfile {
    return UserProfile(
        id = id.toInt(),
        weightKg = weight_kg.toFloat(),
        gender = BiologicalSex.valueOf(gender),
        age = age.toInt(),
        metabolicRate = metabolic_rate.toFloat(),
        personalLimitBac = personal_limit_bac.toFloat(),
        driveLimitBac = drive_limit_bac.toFloat(),
        emergencyContactName = emergency_contact_name,
        emergencyContactPhone = emergency_contact_phone,
        preferMetric = prefer_metric == 1L,
        enableNotifications = enable_notifications == 1L,
        enableHealthKit = enable_health_kit == 1L,
        appMode = AppMode.valueOf(app_mode),
        onboardingComplete = onboarding_complete == 1L,
    )
}
