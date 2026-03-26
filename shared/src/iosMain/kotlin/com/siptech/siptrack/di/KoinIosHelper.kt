package com.siptech.siptrack.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(iosModule(), appModule())
    }
}
