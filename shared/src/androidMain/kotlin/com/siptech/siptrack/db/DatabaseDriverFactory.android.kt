package com.siptech.siptrack.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.siptech.siptrack.db.SipTrackDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(SipTrackDatabase.Schema, context, "siptrack.db")
    }
}
