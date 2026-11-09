# DESIGN

Approved visual direction (user-approved; do not reopen).

## World
OLED scoreboard: pure black background, maximum-contrast numerals, a single
tennis-ball yellow accent. Native Material 3 structure and system type; the
scoreboard voice comes from scale and restraint, not decoration.

## Tokens
- Background: pure black `#000000` (OLED).
- Primary text / big scores: white `#FFFFFF`.
- Accent (serving dot, tie-break label, selected setup option): tennis yellow
  `#CCFF00` (used sparingly).
- Secondary text (games/sets, labels): light gray `#B3B3B3` on black.
- Tile surface: near-black `#1A1A1A`; no gradients, no emoji, no decorative
  charts, no shadows-as-decoration.

## Type
System font only, sp units. Oversized score numerals (display scale), compact
secondary stats. Never fixed px.

## Shape & layout
## Interaction model (user-approved simplification)
- NO scrolling on any screen: all content and actions visible at once.
- Buttons are unmistakable: filled yellow for primary actions (Rozpocznij
  mecz, Nowy mecz), yellow outline for secondary (Cofnij, stepper −/+),
  1dp yellow outline on the serving tile and gray on the other, plus a '+ punkt' cue inside.
  Passive text (status header, Gemy/Sety line) is never styled like a control.
- Setup is a −/+ stepper (1–3, bounded, no wrap) with a large read-only
  number and a single current-format description line.
- Two score tiles: player label at top, yellow serving dot, large score,
  '+ punkt' hint; 80dp tall; tap = haptic + award point.
- Result: winner line + sets + one row of Nowy mecz (filled) and Cofnij
  (outlined), all visible without scrolling.
- Round-safe geometry: every screen is a RoundSafeColumn of fixed-height
  rows; each row's width is the circle's chord at that row's farthest
  distance from center (minus 12dp), so nothing reaches the circular edge.
- Tile border: TennisYellow while serving, ScoreBorderGray #808080 otherwise;
  the '+ punkt' cue matches (yellow serving, neutral otherwise).
- All controls ≥48dp; FitText (BasicText + bounded TextAutoSize) keeps every
  label inside its row slot at font scales up to 1.3.
- Consistent dark theme across setup, match, and result screens.
