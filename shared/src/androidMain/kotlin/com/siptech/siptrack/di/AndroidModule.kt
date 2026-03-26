package com.siptech.siptrack.di

import android.content.Context
import com.siptech.siptrack.db.DatabaseDriverFactory
import com.siptech.siptrack.db.SipTrackDatabase
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    single { DatabaseDriverFactory(context) }
    single { SipTrackDatabase(get<DatabaseDriverFactory>().createDriver()) }
}
