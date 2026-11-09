# PRODUCT

## Platform
android

## What it is
TennisCounter — a native Wear OS smartwatch app (Kotlin, Jetpack Compose, Wear
Compose Material 3 + Android Material 3, Navigation Compose). Single-module
Gradle app, `com.aleksanderjess.tenniscounter`.

## Who and when
One user, the owner, wearing a Galaxy Watch while playing tennis. Used
one-handed, outdoors, mid-match, at a glance. Points get scored many times per
match; accuracy and instant readability matter more than anything else.

## Core interactions (approved)
- Setup: 'Sety do wygrania' −/+ stepper for setsToWin 1/2/3 (default 1, no
  wrapping), current format description below, then Start match.
- Match: two large side-by-side score tiles (Player 1 / Player 2). Tap a tile to
  award that player a point (with haptic feedback). Points dominate; games and
  sets won shown compactly. Tennis-yellow dot marks the server.
- Scoring display: 0/15/30/40; "Deuce" at 40-40; "AD" for the advantaged
  player; numeric scores and a tie-break label during tie-breaks.
- Undo restores the entire previous state (points, games, sets, server,
  transitions). Available on the match screen and the winner screen.
- No scrolling anywhere: every screen shows all content and actions within
  the round viewport (user-approved Don't-Make-Me-Think simplification).
  Buttons are visually distinct from passive stats: score tiles carry a
  1dp outline — yellow when that player is serving, neutral gray otherwise —
  with a '+ punkt' cue; filled primary actions, outlined secondary.
- Winner screen: winning player's name, final sets won, Undo, New match.

- App UI language is Polish (default and only localized strings; user-approved).

## Constraints
- Round watch screens (192dp small round and physical round); all touch targets
  ≥48dp; margins safe on round displays.
- Offline, native/system fonts only (no fetched fonts).
- Pure Kotlin scoring engine in `lib/GameState.kt`; immutable-style transitions
  via `scorePoint` copy; `MatchSession` holds current + previous snapshots.
- No database, no match-history feature (existing mock history screen stays
  unreachable).
