package com.siptech.siptrack.di

import com.siptech.siptrack.db.DatabaseDriverFactory
import com.siptech.siptrack.db.SipTrackDatabase
import org.koin.dsl.module

fun iosModule() = module {
    single { DatabaseDriverFactory() }
    single { SipTrackDatabase(get<DatabaseDriverFactory>().createDriver()) }
}
