package com.siptech.siptrack.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.siptech.siptrack.di.androidModule
import com.siptech.siptrack.di.appModule
import com.siptech.siptrack.ui.SipTrackApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidContext(this@MainActivity)
            modules(androidModule(this@MainActivity), appModule())
        }

        setContent {
            SipTrackApp()
        }
    }
}
