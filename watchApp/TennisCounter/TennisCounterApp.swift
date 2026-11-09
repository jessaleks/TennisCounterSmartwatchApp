import SwiftUI

@main
struct TennisCounterApp: App {
    @StateObject private var viewModel = MatchViewModel()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(viewModel)
        }
    }
}
