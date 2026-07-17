package com.example.unscramble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                GameScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    // Popup Game Over
    if (gameUiState.isGameOver) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Game Over! 💀") },
            text = { Text(text = "Kamu salah tebak 3 kali!\nSkor Akhir: ${gameUiState.score}") },
            confirmButton = {
                TextButton(onClick = { gameViewModel.resetGame() }) {
                    Text(text = "Main Lagi")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Unscramble Party 🎉", fontSize = 32.sp, modifier = Modifier.padding(bottom = 8.dp))

        // Indikator Nyawa & Sisa Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val strikes = "❌ ".repeat(gameUiState.wrongCount)
            val remaining = "⚪ ".repeat(3 - gameUiState.wrongCount)
            Text(text = "Nyawa: $strikes$remaining", fontSize = 16.sp)

            val remainingSkips = 2 - gameUiState.skipCount
            Text(text = "Sisa Skip: $remainingSkips/2 ⏩", fontSize = 16.sp)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${gameUiState.currentWordCount} / $MAX_NO_OF_WORDS",
                    fontSize = 18.sp
                )
                Text(
                    text = gameUiState.currentScrambledWord,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = gameViewModel.userGuess,
                    onValueChange = { gameViewModel.updateUserGuess(it) },
                    singleLine = true,
                    label = { Text("Tebak Katanya") },
                    isError = gameUiState.isGuessedWordWrong,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                if (gameUiState.isGuessedWordWrong) {
                    Text(
                        text = "Salah! Skor berkurang -5 ⚠️",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Button(
            onClick = { gameViewModel.checkUserGuess() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Submit")
        }

        // Tombol Skip (Disabled kalau sudah 2x)
        OutlinedButton(
            onClick = { gameViewModel.skipWord() },
            enabled = gameUiState.skipCount < 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            if (gameUiState.skipCount < 2) {
                Text("Skip (${2 - gameUiState.skipCount}x lagi)")
            } else {
                Text("Skip Habis! ⛔")
            }
        }

        Text(
            text = "Score: ${gameUiState.score}",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}