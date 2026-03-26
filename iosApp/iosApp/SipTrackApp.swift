import SwiftUI
import SipTrackKit

@main
struct SipTrackApp: App {
    init() {
        KoinIosHelperKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
