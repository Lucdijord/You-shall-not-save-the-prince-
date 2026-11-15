package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InicioScreen(
    onJogar: () -> Unit,
    onHistorico: () -> Unit,
    onAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏰", fontSize = 64.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "You Shall Not Save the Prince",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Os monstros sequestraram o príncipe e agora precisam defender o castelo " +
                    "contra a princesa e suas tropas.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onJogar, modifier = Modifier.fillMaxWidth()) { Text("Jogar") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onHistorico, modifier = Modifier.fillMaxWidth()) { Text("Histórico") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onAdmin, modifier = Modifier.fillMaxWidth()) { Text("Administração") }
    }
}