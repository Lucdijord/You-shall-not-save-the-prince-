package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.model.PresetPartida

@Composable
fun FasesScreen(
    fasesLiberadas: List<Int>,
    presets: List<PresetPartida>,
    onEscolher: (Int, Long, String) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    mensagem: String? = null
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var presetEscolhido by rememberSaveable { mutableStateOf<Long?>(null) }

    val presetAtualId = presetEscolhido?.takeIf { id -> presets.any { it.id == id } }
        ?: presets.firstOrNull()?.id
    val podeJogar = nome.isNotBlank() && presetAtualId != null

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Escolha a fase", style = MaterialTheme.typography.headlineSmall) }

        if (mensagem != null) {
            item { Text(mensagem, color = MaterialTheme.colorScheme.error) }
        }

        item {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do jogador") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Text("Preset da partida", style = MaterialTheme.typography.titleMedium) }

        if (presets.isEmpty()) {
            item { Text("Nenhum preset disponível.") }
        }
        items(presets, key = { it.id }) { preset ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { presetEscolhido = preset.id },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = preset.id == presetAtualId,
                    onClick = { presetEscolhido = preset.id }
                )
                Text("${preset.nome} — ${preset.moedasIniciais} moedas, ${preset.vidaInicialCastelo} vidas")
            }
        }

        item { Text("Fases", style = MaterialTheme.typography.titleMedium) }

        items((1..5).toList()) { fase ->
            val liberada = fase in fasesLiberadas
            val rotulo = when {
                !liberada -> "🔒 Fase $fase (bloqueada)"
                fase == 5 -> "Fase 5 — Princesa chefe 👸"
                else -> "Fase $fase"
            }
            Button(
                onClick = { presetAtualId?.let { onEscolher(fase, it, nome.trim()) } },
                enabled = liberada && podeJogar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(rotulo)
            }
        }

        if (nome.isBlank()) {
            item { Text("Informe seu nome para jogar.", style = MaterialTheme.typography.bodySmall) }
        }

        item {
            OutlinedButton(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
        }
    }
}