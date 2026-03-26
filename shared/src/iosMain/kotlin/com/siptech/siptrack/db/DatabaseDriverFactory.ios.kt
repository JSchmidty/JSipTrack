package com.siptech.siptrack.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.siptech.siptrack.db.SipTrackDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(SipTrackDatabase.Schema, "siptrack.db")
    }
}
