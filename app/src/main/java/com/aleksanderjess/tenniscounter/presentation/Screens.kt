package com.aleksanderjess.tenniscounter.presentation

sealed class Screen(val route: String) {
    data object MatchHistory : Screen("match_history")
    data object SetWizard : Screen("set_wizard")
    data object MatchScreen : Screen("match_screen/{setsToWin}") {
        fun createRoute(setsToWin: Int) = "match_screen/$setsToWin"
    }
}