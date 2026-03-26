import SwiftUI
import SipTrackKit

/// iOS App entry point.
/// Initializes Koin DI before the first Compose view appears.
@main
struct SipTrackiOSApp: App {

    init() {
        // Initialize Koin dependency injection for iOS.
        // This calls the initKoin() function defined in:
        //   shared/src/iosMain/kotlin/com/siptech/siptrack/di/KoinIosHelper.kt
        KoinIosHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
