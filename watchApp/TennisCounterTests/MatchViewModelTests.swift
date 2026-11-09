import XCTest
@testable import TennisCounter

@MainActor
final class MatchViewModelTests: XCTestCase {

    private var suiteName: String!
    private var defaults: UserDefaults!
    private var store: SessionStore!

    override func setUp() {
        super.setUp()
        suiteName = "MatchViewModelTests.\(UUID().uuidString)"
        defaults = UserDefaults(suiteName: suiteName)
        store = SessionStore(defaults: defaults)
    }

    override func tearDown() {
        defaults.removePersistentDomain(forName: suiteName)
        super.tearDown()
    }

    func testSetupBoundsAndNoWrap() {
        let vm = MatchViewModel(store: store)
        XCTAssertEqual(vm.screen, .setup)
        XCTAssertEqual(vm.setupSetsToWin, 1)

        vm.decreaseSetsToWin()
        XCTAssertEqual(vm.setupSetsToWin, 1)

        vm.increaseSetsToWin()
        vm.increaseSetsToWin()
        vm.increaseSetsToWin()
        XCTAssertEqual(vm.setupSetsToWin, 3)

        vm.decreaseSetsToWin()
        XCTAssertEqual(vm.setupSetsToWin, 2)
    }

    func testScoreUndoAndPersistence() {
        let vm = MatchViewModel(store: store)
        vm.startMatch()
        XCTAssertEqual(vm.screen, .match)
        XCTAssertFalse(vm.match.canUndo)

        XCTAssertTrue(vm.awardPoint(player: 1))
        XCTAssertEqual(vm.match.player1PointsLabel, "15")
        XCTAssertTrue(vm.match.canUndo)

        vm.undo()
        XCTAssertEqual(vm.match.player1PointsLabel, "0")
        XCTAssertFalse(vm.match.canUndo)
    }

    func testSnapshotSavedAndReloaded() {
        let vm = MatchViewModel(store: store)
        vm.startMatch()
        vm.awardPoint(player: 1)
        vm.awardPoint(player: 2)

        let reloaded = MatchViewModel(store: store)
        XCTAssertEqual(reloaded.screen, .match)
        XCTAssertEqual(reloaded.match, vm.match)

        reloaded.undo()
        XCTAssertEqual(reloaded.match.player2PointsLabel, "0")
    }

    func testFullSessionRestoration() {
        let vm = MatchViewModel(store: store)
        vm.increaseSetsToWin()
        vm.increaseSetsToWin()
        vm.startMatch()
        for _ in 0..<5 { vm.awardPoint(player: 1) }
        vm.awardPoint(player: 2)

        let reloaded = MatchViewModel(store: store)
        XCTAssertEqual(reloaded.match, vm.match)
        XCTAssertEqual(reloaded.match.setsToWin, 3)
        XCTAssertTrue(reloaded.match.canUndo)
    }

    func testMatchFinishPersistenceUndoAndReset() {
        let vm = MatchViewModel(store: store)
        vm.startMatch()
        for _ in 0..<24 { XCTAssertTrue(vm.awardPoint(player: 1)) }
        XCTAssertTrue(vm.match.isMatchOver)
        XCTAssertEqual(vm.match.winningPlayer, 1)
        XCTAssertEqual(vm.match.player1Sets, 1)
        XCTAssertEqual(vm.match.player2Sets, 0)

        let finished = vm.match
        XCTAssertFalse(vm.awardPoint(player: 1))
        XCTAssertFalse(vm.awardPoint(player: 2))
        XCTAssertEqual(vm.match, finished)

        let reloaded = MatchViewModel(store: store)
        XCTAssertEqual(reloaded.screen, .match)
        XCTAssertEqual(reloaded.match, finished)

        reloaded.undo()
        XCTAssertFalse(reloaded.match.isMatchOver)
        XCTAssertEqual(reloaded.match.winningPlayer, 0)

        for _ in 0..<24 { reloaded.awardPoint(player: 1) }
        XCTAssertTrue(reloaded.match.isMatchOver)

        reloaded.startNewMatch()
        XCTAssertEqual(reloaded.screen, .setup)
        XCTAssertEqual(reloaded.setupSetsToWin, 1)
        XCTAssertNil(store.load())

        let fresh = MatchViewModel(store: store)
        XCTAssertEqual(fresh.screen, .setup)
    }

    func testCorruptSnapshotClearsOnlyOwnKey() {
        defaults.set("v1:garbage,,,1", forKey: SessionStore.storageKey)
        defaults.set("keep", forKey: "other.key")

        let vm = MatchViewModel(store: store)
        XCTAssertEqual(vm.screen, .setup)
        XCTAssertNil(defaults.string(forKey: SessionStore.storageKey))
        XCTAssertEqual(defaults.string(forKey: "other.key"), "keep")
    }

    func testNonStringPreferenceClearsOnlyOwnKey() {
        defaults.set(42, forKey: SessionStore.storageKey)
        defaults.set("keep", forKey: "other.key")

        let vm = MatchViewModel(store: store)
        XCTAssertEqual(vm.screen, .setup)
        XCTAssertNil(defaults.object(forKey: SessionStore.storageKey))
        XCTAssertEqual(defaults.string(forKey: "other.key"), "keep")
    }
}
