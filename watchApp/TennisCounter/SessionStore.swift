import Foundation
import TennisCore

final class SessionStore {
    static let shared = SessionStore()
    static let storageKey = "com.aleksanderjess.tenniscounter.watch.session"
    static let envelopePrefix = "v1:"

    private let defaults: UserDefaults

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    func load() -> MatchController? {
        guard let raw = defaults.object(forKey: Self.storageKey) else {
            return nil
        }
        guard let stored = raw as? String,
              stored.hasPrefix(Self.envelopePrefix),
              let controller = MatchController.companion.restoreSnapshot(
                  snapshot: String(stored.dropFirst(Self.envelopePrefix.count))
              ) else {
            defaults.removeObject(forKey: Self.storageKey)
            return nil
        }
        return controller
    }

    func save(_ controller: MatchController) {
        defaults.set(
            Self.envelopePrefix + controller.exportSnapshot(),
            forKey: Self.storageKey
        )
    }

    func clear() {
        defaults.removeObject(forKey: Self.storageKey)
    }
}
