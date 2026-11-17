package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.youshallnotsavetheprince.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.youshallnotsavetheprince.ui.componentes.CabecalhoJogo
import com.example.youshallnotsavetheprince.ui.componentes.CartaoJogo
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.Contorno
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { CabecalhoJogo("PREPARE SUA DEFESA", "Escolha a fase", "Selecione seu comandante e o desafio que deseja enfrentar.") }
        mensagem?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        item {
            CartaoJogo(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SEU COMANDANTE", style = MaterialTheme.typography.labelMedium, color = Ouro)
                    OutlinedTextField(
                        value = nome, onValueChange = { nome = it },
                        label = { Text("Nome do jogador") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("PREPARAÇÃO DA PARTIDA", style = MaterialTheme.typography.labelMedium, color = Ouro)
                    if (presets.isEmpty()) Text("Nenhum preset disponível.", color = TextoSecundario)
                    presets.forEach { preset ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { presetEscolhido = preset.id },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = preset.id == presetAtualId, onClick = { presetEscolhido = preset.id })
                            Column {
                                Text(preset.nome, style = MaterialTheme.typography.titleMedium)
                                Text("${preset.moedasIniciais} moedas  •  ${preset.vidaInicialCastelo} vidas", color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        item { Text("CAPÍTULOS", style = MaterialTheme.typography.labelMedium, color = Ouro) }
        items((1..5).toList()) { fase ->
            val liberada = fase in fasesLiberadas
            val subtitulo = if (fase == 5) "Enfrente a princesa" else "Defenda o castelo"
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, if (liberada) Ouro.copy(alpha = 0.65f) else Contorno.copy(alpha = 0.6f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("FASE $fase", style = MaterialTheme.typography.titleMedium, color = if (liberada) Ouro else TextoSecundario)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!liberada) {
                                Image(painterResource(R.drawable.icon_cadeado), contentDescription = null, modifier = Modifier.size(17.dp))
                            }
                            Text(if (liberada) "DISPONÍVEL" else "BLOQUEADA", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                        }
                    }
                    Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    Button(
                        onClick = { presetAtualId?.let { onEscolher(fase, it, nome.trim()) } },
                        enabled = liberada && podeJogar,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (liberada) "JOGAR FASE $fase" else "CONCLUA AS FASES ANTERIORES") }
                }
            }
        }
        if (nome.isBlank()) item { Text("Informe seu nome para jogar.", style = MaterialTheme.typography.bodySmall, color = TextoSecundario) }
        item { OutlinedButton(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("VOLTAR") } }
    }
}
