import Foundation
import TennisCore

struct MatchProjection: Equatable {
    var player1PointsLabel = "0"
    var player2PointsLabel = "0"
    var player1Games: Int = 0
    var player2Games: Int = 0
    var player1Sets: Int = 0
    var player2Sets: Int = 0
    var servingPlayer: Int = 1
    var setsToWin: Int = 1
    var isTiebreak = false
    var isDeuce = false
    var isMatchOver = false
    var canUndo = false
    var winningPlayer: Int = 0
}

@MainActor
final class MatchViewModel: ObservableObject {
    enum Screen: Equatable {
        case setup
        case match
    }

    static let minSetsToWin = 1
    static let maxSetsToWin = 3

    @Published private(set) var screen: Screen = .setup
    @Published private(set) var setupSetsToWin = MatchViewModel.minSetsToWin
    @Published private(set) var match = MatchProjection()

    private var controller: MatchController?
    private let store: SessionStore

    init(store: SessionStore = .shared) {
        self.store = store
        if let restored = store.load() {
            controller = restored
            publish()
            screen = .match
        }
    }

    func increaseSetsToWin() {
        setupSetsToWin = min(setupSetsToWin + 1, Self.maxSetsToWin)
    }

    func decreaseSetsToWin() {
        setupSetsToWin = max(setupSetsToWin - 1, Self.minSetsToWin)
    }

    func startMatch() {
        controller = MatchController(setsToWin: Int32(setupSetsToWin))
        persistAndPublish()
        screen = .match
    }

    @discardableResult
    func awardPoint(player: Int) -> Bool {
        guard let controller, !controller.isMatchOver, player == 1 || player == 2 else {
            return false
        }
        controller.awardPoint(player: Int32(player))
        persistAndPublish()
        return true
    }

    func undo() {
        guard let controller, controller.canUndo else { return }
        controller.undoPoint()
        persistAndPublish()
    }

    func startNewMatch() {
        store.clear()
        controller = nil
        setupSetsToWin = Self.minSetsToWin
        screen = .setup
        match = MatchProjection()
    }

    private func persistAndPublish() {
        if let controller {
            store.save(controller)
        }
        publish()
    }

    private func publish() {
        guard let controller else {
            match = MatchProjection()
            return
        }
        match = MatchProjection(
            player1PointsLabel: controller.player1PointsLabel,
            player2PointsLabel: controller.player2PointsLabel,
            player1Games: Int(controller.player1Games),
            player2Games: Int(controller.player2Games),
            player1Sets: Int(controller.player1Sets),
            player2Sets: Int(controller.player2Sets),
            servingPlayer: Int(controller.servingPlayer),
            setsToWin: Int(controller.setsToWin),
            isTiebreak: controller.isTiebreak,
            isDeuce: controller.isDeuce,
            isMatchOver: controller.isMatchOver,
            canUndo: controller.canUndo,
            winningPlayer: Int(controller.winningPlayer)
        )
    }
}
