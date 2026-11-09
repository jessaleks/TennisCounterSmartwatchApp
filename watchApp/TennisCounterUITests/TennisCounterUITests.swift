import XCTest

final class TennisCounterUITests: XCTestCase {

    override func setUp() {
        continueAfterFailure = false
    }

    private func launchAtSetup(_ app: XCUIApplication) {
        app.launch()
        if app.buttons["start_match"].waitForExistence(timeout: 10) {
            return
        }
        if app.buttons["new_match_button"].exists {
            app.buttons["new_match_button"].tap()
            XCTAssertTrue(app.buttons["start_match"].waitForExistence(timeout: 5))
            return
        }
        let tile = app.buttons["player1_score_tile"]
        XCTAssertTrue(tile.waitForExistence(timeout: 5))
        for _ in 0..<90 where !app.buttons["new_match_button"].exists {
            tile.tap()
        }
        XCTAssertTrue(app.buttons["new_match_button"].waitForExistence(timeout: 5))
        app.buttons["new_match_button"].tap()
        XCTAssertTrue(app.buttons["start_match"].waitForExistence(timeout: 5))
    }

    private func finishMatchAndReset(_ app: XCUIApplication) {
        let tile = app.buttons["player1_score_tile"]
        for _ in 0..<24 where !app.buttons["new_match_button"].exists {
            tile.tap()
        }
        XCTAssertTrue(app.buttons["new_match_button"].waitForExistence(timeout: 5))
        app.buttons["new_match_button"].tap()
        XCTAssertTrue(app.buttons["start_match"].waitForExistence(timeout: 5))
    }

    @MainActor
    func testSetupPointUndoWinnerFlow() throws {
        let app = XCUIApplication()
        launchAtSetup(app)

        app.buttons["start_match"].tap()
        let tile = app.buttons["player1_score_tile"]
        XCTAssertTrue(tile.waitForExistence(timeout: 5))
        XCTAssertEqual(tile.value as? String, "0")

        tile.tap()
        XCTAssertEqual(tile.value as? String, "15")

        app.buttons["undo_button"].tap()
        XCTAssertEqual(tile.value as? String, "0")
        XCTAssertFalse(app.buttons["undo_button"].isEnabled)

        finishMatchAndReset(app)
    }

    @MainActor
    func testWinnerFlowAndRelaunchPersistence() throws {
        let app = XCUIApplication()
        launchAtSetup(app)

        app.buttons["start_match"].tap()
        let tile = app.buttons["player1_score_tile"]
        XCTAssertTrue(tile.waitForExistence(timeout: 5))

        for _ in 0..<24 { tile.tap() }
        XCTAssertTrue(app.buttons["new_match_button"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.staticTexts["match_winner"].exists)

        app.terminate()
        app.launch()
        XCTAssertTrue(app.buttons["new_match_button"].waitForExistence(timeout: 10))

        app.buttons["new_match_button"].tap()
        XCTAssertTrue(app.buttons["start_match"].waitForExistence(timeout: 5))
    }
}
