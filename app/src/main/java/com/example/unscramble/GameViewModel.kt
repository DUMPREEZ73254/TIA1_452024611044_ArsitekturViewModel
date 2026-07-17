package com.example.unscramble

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle(),
            currentWordCount = 1,
            score = 0,
            wrongCount = 0,
            skipCount = 0, // Reset sisa skip
            isGameOver = false
        )
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            val newScore = if (_uiState.value.score >= 5) _uiState.value.score - 5 else 0
            val newWrongCount = _uiState.value.wrongCount + 1

            if (newWrongCount >= 3) {
                _uiState.value = _uiState.value.copy(
                    score = newScore,
                    wrongCount = newWrongCount,
                    isGuessedWordWrong = true,
                    isGameOver = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    score = newScore,
                    wrongCount = newWrongCount,
                    isGuessedWordWrong = true
                )
            }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        if (_uiState.value.skipCount < 2) {
            val newSkipCount = _uiState.value.skipCount + 1
            _uiState.value = _uiState.value.copy(skipCount = newSkipCount)
            updateGameState(_uiState.value.score)
            updateUserGuess("")
        }
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size >= MAX_NO_OF_WORDS) {
            usedWords.clear()
            _uiState.value = _uiState.value.copy(
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = 1,
                isGuessedWordWrong = false
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = _uiState.value.currentWordCount.inc()
            )
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}