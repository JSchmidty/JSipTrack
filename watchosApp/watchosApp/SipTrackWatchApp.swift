import SwiftUI
import SipTrackKit

/// Apple Watch (watchOS) thin SwiftUI shell over the KMP SipTrackKit framework.
/// Business logic (BAC calculator, models) lives in the shared KMP module.
@main
struct SipTrackWatchApp: App {
    var body: some Scene {
        WindowGroup {
            WatchContentView()
        }
    }
}
