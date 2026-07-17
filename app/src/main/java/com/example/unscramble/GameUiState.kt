package com.example.unscramble

data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val wrongCount: Int = 0,
    val skipCount: Int = 0, // Indikator jumlah skip yang sudah dipakai (maksimal 2)
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false
)