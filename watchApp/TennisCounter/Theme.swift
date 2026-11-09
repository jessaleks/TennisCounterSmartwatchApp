import SwiftUI

extension Color {
    static let tennisYellow = Color(red: 0xCC / 255, green: 0xFF / 255, blue: 0)
    static let secondaryGray = Color(red: 0xB3 / 255, green: 0xB3 / 255, blue: 0xB3 / 255)
    static let tileSurface = Color(red: 0x1A / 255, green: 0x1A / 255, blue: 0x1A / 255)
    static let borderGray = Color(red: 0x80 / 255, green: 0x80 / 255, blue: 0x80 / 255)
}

enum AppStrings {
    static let setsToWinTitle = "Sety do wygrania"
    static let startMatch = "Rozpocznij mecz"
    static let decreaseSets = "Mniej setów"
    static let increaseSets = "Więcej setów"
    static func formatDescription(_ setsToWin: Int) -> String {
        switch setsToWin {
        case 1: return "Jeden set"
        case 2: return "Do 2 wygranych setów"
        default: return "Do 3 wygranych setów"
        }
    }
    static func playerLabel(_ player: Int) -> String { "Gracz \(player)" }
    static func awardPoint(_ player: Int, serving: Bool) -> String {
        serving ? "Dodaj punkt graczowi \(player), serwuje" : "Dodaj punkt graczowi \(player)"
    }
    static func matchStats(_ g1: Int, _ g2: Int, _ s1: Int, _ s2: Int) -> String {
        "Gemy \(g1)–\(g2) · Sety \(s1)–\(s2)"
    }
    static let undo = "Cofnij"
    static let tiebreak = "Tie-break"
    static let deuce = "Równowaga"
    static func matchWinner(_ player: Int) -> String { "Wygrywa gracz \(player)" }
    static func setsResult(_ s1: Int, _ s2: Int) -> String { "Sety \(s1)–\(s2)" }
    static let newMatch = "Nowy mecz"
    static let addPointHint = "+ punkt"
}
