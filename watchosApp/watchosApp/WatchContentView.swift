import SwiftUI
import SipTrackKit

/// Main watch face — shows BAC, status, and quick log button.
/// Real BAC data is loaded from the shared KMP DashboardViewModel.
struct WatchContentView: View {
    // TODO: Wire up to KMP ViewModel via StateFlow bridge
    @State private var bac: Double = 0.0
    @State private var isSafeToDrive: Bool = true

    private var bacColor: Color {
        if bac <= 0.04 { return .green }
        if bac < 0.08  { return .yellow }
        return .red
    }

    var body: some View {
        VStack(spacing: 8) {
            Text("BAC")
                .font(.caption2)
                .foregroundColor(.gray)

            Text(String(format: "%.3f", bac))
                .font(.system(size: 36, weight: .black, design: .rounded))
                .foregroundColor(bacColor)

            Text(isSafeToDrive ? "✅ OK to Drive" : "🚗 Don't Drive")
                .font(.caption)

            Button(action: logDrink) {
                Label("Log Drink", systemImage: "plus.circle.fill")
            }
            .buttonStyle(.borderedProminent)
            .tint(.blue)
        }
        .padding()
    }

    private func logDrink() {
        // TODO: WatchConnectivity → send message to iPhone to log drink
        // WCSession.default.sendMessage(["action": "logDrink"], replyHandler: nil)
    }
}

struct WatchContentView_Previews: PreviewProvider {
    static var previews: some View {
        WatchContentView()
    }
}
