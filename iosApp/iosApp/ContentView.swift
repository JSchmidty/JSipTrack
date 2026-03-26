import SwiftUI
import SipTrackKit

/// ContentView — thin SwiftUI shell over the Compose Multiplatform UI.
/// The KMP SipTrackKit framework provides all business logic.
struct ContentView: View {
    var body: some View {
        // Compose Multiplatform renders the full UI via ComposeUIViewController
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // This will be provided by the composeApp KMP module
        // via MainViewControllerKt.MainViewController()
        UIViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
