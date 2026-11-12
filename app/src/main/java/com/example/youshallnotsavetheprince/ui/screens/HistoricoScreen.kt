package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoricoScreen(
    resultados: List<ResultadoPartida>,
    onRenomear: (Long, String) -> Unit,
    onExcluir: (Long) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    mensagem: String? = null
) {
    var emEdicao by remember { mutableStateOf<ResultadoPartida?>(null) }
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR")) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Histórico de partidas", style = MaterialTheme.typography.headlineSmall)
        mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        if (resultados.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nenhuma partida registrada ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(resultados, key = { it.id }) { resultado ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val situacao = if (resultado.vitoria) "🏆 Vitória" else "💀 Derrota"
                            Text(
                                "$situacao — Fase ${resultado.fase}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Jogador: ${resultado.nomeJogador}")
                            Text(
                                "Pontos: ${resultado.pontos} • Turnos: ${resultado.turnos} " +
                                        "• Vida restante: ${resultado.vidaRestante}"
                            )
                            Text(
                                formato.format(Date(resultado.registradoEmMillis)),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Row {
                                TextButton(onClick = { emEdicao = resultado }) { Text("Renomear") }
                                TextButton(onClick = { onExcluir(resultado.id) }) { Text("Excluir") }
                            }
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
    }

    emEdicao?.let { resultado ->
        DialogoRenomear(
            nomeAtual = resultado.nomeJogador,
            onConfirmar = { novoNome ->
                onRenomear(resultado.id, novoNome)
                emEdicao = null
            },
            onCancelar = { emEdicao = null }
        )
    }
}

@Composable
private fun DialogoRenomear(
    nomeAtual: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var novoNome by remember { mutableStateOf(nomeAtual) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Renomear jogador") },
        text = {
            OutlinedTextField(
                value = novoNome,
                onValueChange = { novoNome = it },
                label = { Text("Nome do jogador") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(novoNome.trim()) },
                enabled = novoNome.isNotBlank()
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}