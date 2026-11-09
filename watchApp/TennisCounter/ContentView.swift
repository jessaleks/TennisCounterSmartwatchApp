import SwiftUI
import WatchKit

struct ContentView: View {
    @EnvironmentObject private var viewModel: MatchViewModel

    var body: some View {
        Group {
            switch viewModel.screen {
            case .setup:
                SetupView()
            case .match:
                if viewModel.match.isMatchOver {
                    WinnerView()
                } else {
                    MatchView()
                }
            }
        }
        .background(Color.black.ignoresSafeArea())
    }
}

struct SetupView: View {
    @EnvironmentObject private var viewModel: MatchViewModel

    var body: some View {
        VStack(spacing: 6) {
            Text(AppStrings.setsToWinTitle)
                .font(.caption)
                .foregroundStyle(Color.secondaryGray)
                .lineLimit(1)
                .minimumScaleFactor(0.6)
            HStack(spacing: 10) {
                Button {
                    viewModel.decreaseSetsToWin()
                } label: {
                    Text("−")
                        .font(.title3.weight(.bold))
                        .frame(width: 44, height: 44)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.bordered)
                .tint(.tennisYellow)
                .disabled(viewModel.setupSetsToWin <= MatchViewModel.minSetsToWin)
                .accessibilityLabel(AppStrings.decreaseSets)
                .accessibilityIdentifier("decrease_sets")

                Text("\(viewModel.setupSetsToWin)")
                    .font(.system(size: 40, weight: .bold))
                    .foregroundStyle(.white)
                    .frame(minWidth: 44)
                    .accessibilityIdentifier("sets_to_win_value")

                Button {
                    viewModel.increaseSetsToWin()
                } label: {
                    Text("+")
                        .font(.title3.weight(.bold))
                        .frame(width: 44, height: 44)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.bordered)
                .tint(.tennisYellow)
                .disabled(viewModel.setupSetsToWin >= MatchViewModel.maxSetsToWin)
                .accessibilityLabel(AppStrings.increaseSets)
                .accessibilityIdentifier("increase_sets")
            }
            Text(AppStrings.formatDescription(viewModel.setupSetsToWin))
                .font(.caption2)
                .foregroundStyle(Color.secondaryGray)
                .lineLimit(1)
                .minimumScaleFactor(0.6)
            Button {
                viewModel.startMatch()
            } label: {
                Text(AppStrings.startMatch)
                    .font(.callout.weight(.semibold))
                    .foregroundStyle(.black)
                    .lineLimit(1)
                    .minimumScaleFactor(0.6)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.tennisYellow)
            .accessibilityIdentifier("start_match")
        }
        .padding(.horizontal, 10)
    }
}

struct MatchView: View {
    @EnvironmentObject private var viewModel: MatchViewModel

    private let statusRowHeight: CGFloat = 14
    private let statsRowHeight: CGFloat = 14
    private let undoHeight: CGFloat = 44
    private let gap: CGFloat = 4
    private let maxTileHeight: CGFloat = 92
    private let minTileHeight: CGFloat = 56

    var body: some View {
        GeometryReader { geo in
            let fixedHeight = statusRowHeight + statsRowHeight + undoHeight + gap * 3
            let tileHeight = min(maxTileHeight, max(minTileHeight, geo.size.height - fixedHeight))
            VStack(spacing: gap) {
                Group {
                    if viewModel.match.isTiebreak {
                        Text(AppStrings.tiebreak)
                    } else if viewModel.match.isDeuce {
                        Text(AppStrings.deuce)
                    } else {
                        Text(" ")
                    }
                }
                .font(.caption2.weight(.semibold))
                .foregroundStyle(Color.tennisYellow)
                .frame(height: statusRowHeight)

                HStack(spacing: 6) {
                    ScoreTile(
                        player: 1,
                        label: viewModel.match.player1PointsLabel,
                        serving: viewModel.match.servingPlayer == 1
                    ) {
                        awardPoint(player: 1)
                    }
                    ScoreTile(
                        player: 2,
                        label: viewModel.match.player2PointsLabel,
                        serving: viewModel.match.servingPlayer == 2
                    ) {
                        awardPoint(player: 2)
                    }
                }
                .frame(height: tileHeight)

                Text(AppStrings.matchStats(
                    viewModel.match.player1Games,
                    viewModel.match.player2Games,
                    viewModel.match.player1Sets,
                    viewModel.match.player2Sets
                ))
                .font(.caption2)
                .foregroundStyle(Color.secondaryGray)
                .frame(height: statsRowHeight)
                .accessibilityIdentifier("match_stats")

                Button {
                    viewModel.undo()
                } label: {
                    Text(AppStrings.undo)
                        .font(.callout.weight(.semibold))
                        .foregroundStyle(Color.tennisYellow)
                        .frame(maxWidth: .infinity, minHeight: undoHeight)
                }
                .buttonStyle(.bordered)
                .tint(.tennisYellow)
                .disabled(!viewModel.match.canUndo)
                .accessibilityIdentifier("undo_button")
            }
            .frame(width: geo.size.width, height: geo.size.height)
        }
        .padding(.horizontal, 6)
    }

    private func awardPoint(player: Int) {
        if viewModel.awardPoint(player: player) {
            WKInterfaceDevice.current().play(.success)
        }
    }
}

struct ScoreTile: View {
    let player: Int
    let label: String
    let serving: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 0) {
                HStack(spacing: 4) {
                    Text(AppStrings.playerLabel(player))
                        .font(.caption2)
                        .foregroundStyle(Color.secondaryGray)
                    if serving {
                        Circle()
                            .fill(Color.tennisYellow)
                            .frame(width: 6, height: 6)
                            .accessibilityIdentifier("player\(player)_serving_dot")
                    }
                }
                Text(label)
                    .font(.system(size: 30, weight: .bold))
                    .foregroundStyle(.white)
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                    .accessibilityIdentifier("player\(player)_points")
                Text(AppStrings.addPointHint)
                    .font(.caption2)
                    .foregroundStyle(serving ? Color.tennisYellow : Color.secondaryGray)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.tileSurface, in: RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .stroke(serving ? Color.tennisYellow : Color.borderGray, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .accessibilityLabel(AppStrings.awardPoint(player, serving: serving))
        .accessibilityValue(label)
        .accessibilityIdentifier("player\(player)_score_tile")
    }
}

struct WinnerView: View {
    @EnvironmentObject private var viewModel: MatchViewModel

    var body: some View {
        VStack(spacing: 8) {
            Text(AppStrings.matchWinner(viewModel.match.winningPlayer))
                .font(.headline)
                .foregroundStyle(.white)
                .multilineTextAlignment(.center)
                .accessibilityIdentifier("match_winner")
            Text(AppStrings.setsResult(viewModel.match.player1Sets, viewModel.match.player2Sets))
                .font(.caption)
                .foregroundStyle(Color.secondaryGray)
            HStack(spacing: 8) {
                Button {
                    viewModel.startNewMatch()
                } label: {
                    Text(AppStrings.newMatch)
                        .font(.callout.weight(.semibold))
                        .foregroundStyle(.black)
                }
                .buttonStyle(.borderedProminent)
                .tint(.tennisYellow)
                .accessibilityIdentifier("new_match_button")

                Button {
                    viewModel.undo()
                } label: {
                    Text(AppStrings.undo)
                        .font(.callout)
                }
                .buttonStyle(.bordered)
                .tint(.tennisYellow)
                .disabled(!viewModel.match.canUndo)
                .accessibilityIdentifier("undo_button")
            }
        }
        .padding(.horizontal, 10)
    }
}

#Preview("Setup") {
    SetupView()
        .environmentObject(MatchViewModel(store: SessionStore(defaults: .init(suiteName: "preview")!)))
        .background(Color.black)
}
