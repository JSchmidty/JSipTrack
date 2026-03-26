package com.siptech.siptrack

import androidx.compose.ui.window.ComposeUIViewController
import com.siptech.siptrack.ui.SipTrackApp

/**
 * Entry point for the iOS ComposeUIViewController.
 * Called from Swift's ContentView.swift via:
 *   MainViewControllerKt.MainViewController()
 *
 * This bridges Swift/UIKit with the shared Compose Multiplatform UI.
 */
fun MainViewController() = ComposeUIViewController {
    SipTrackApp()
}
