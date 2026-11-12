package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.model.ResultadoPartida

@Composable
fun ResultadoScreen(
    resultado: ResultadoPartida?,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (resultado == null) {
            Text("Nenhum resultado disponível.", style = MaterialTheme.typography.titleMedium)
        } else {
            Text(
                if (resultado.vitoria) "🏆 Vitória!" else "💀 Derrota",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Jogador: ${resultado.nomeJogador}")
                    Text("Fase: ${resultado.fase}")
                    Text("Turnos: ${resultado.turnos}")
                    Text("Vida restante do castelo: ${resultado.vidaRestante}")
                    Text("Pontos: ${resultado.pontos}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao início") }
    }
}