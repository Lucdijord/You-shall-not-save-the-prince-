package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.viewmodel.AdminUiState

@Composable
fun AdminPresetsScreen(
    estado: AdminUiState,
    onCriar: (String, Int, Int) -> Unit,
    onAtualizar: (PresetPartida) -> Unit,
    onExcluir: (Long) -> Unit,
    onFiltrar: (String) -> Unit,
    onSair: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var moedas by rememberSaveable { mutableStateOf("100") }
    var vida by rememberSaveable { mutableStateOf("10") }
    var erroFormulario by remember { mutableStateOf<String?>(null) }
    var filtro by rememberSaveable { mutableStateOf(estado.filtro) }
    var emEdicao by remember { mutableStateOf<PresetPartida?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Presets de partida", style = MaterialTheme.typography.headlineSmall)
        }

        if (estado.carregando) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }
        estado.mensagem?.let { mensagem ->
            item { Text(mensagem, color = MaterialTheme.colorScheme.error) }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Novo preset", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = moedas,
                            onValueChange = { moedas = it.filter { c -> c.isDigit() }.take(4) },
                            label = { Text("Moedas") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = vida,
                            onValueChange = { vida = it.filter { c -> c.isDigit() }.take(3) },
                            label = { Text("Vida") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    erroFormulario?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = {
                            val moedasInt = moedas.toIntOrNull()
                            val vidaInt = vida.toIntOrNull()
                            if (moedasInt == null || vidaInt == null) {
                                erroFormulario = "Informe valores numéricos para moedas e vida."
                            } else {
                                erroFormulario = null
                                onCriar(nome.trim(), moedasInt, vidaInt)
                                nome = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Criar preset") }
                }
            }
        }

        item {
            OutlinedTextField(
                value = filtro,
                onValueChange = {
                    filtro = it
                    onFiltrar(it)
                },
                label = { Text("Filtrar por nome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (estado.presetsVisiveis.isEmpty()) {
            item { Text("Nenhum preset encontrado.") }
        }

        items(estado.presetsVisiveis, key = { it.id }) { preset ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(preset.nome, style = MaterialTheme.typography.titleMedium)
                        Text("${preset.moedasIniciais} moedas • ${preset.vidaInicialCastelo} vidas")
                    }
                    TextButton(onClick = { emEdicao = preset }) { Text("Editar") }
                    TextButton(onClick = { onExcluir(preset.id) }) { Text("Excluir") }
                }
            }
        }

        item {
            OutlinedButton(onClick = onSair, modifier = Modifier.fillMaxWidth()) {
                Text("Sair da administração")
            }
        }
    }

    emEdicao?.let { preset ->
        DialogoEditarPreset(
            preset = preset,
            onConfirmar = { atualizado ->
                onAtualizar(atualizado)
                emEdicao = null
            },
            onCancelar = { emEdicao = null }
        )
    }
}

@Composable
private fun DialogoEditarPreset(
    preset: PresetPartida,
    onConfirmar: (PresetPartida) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf(preset.nome) }
    var moedas by remember { mutableStateOf(preset.moedasIniciais.toString()) }
    var vida by remember { mutableStateOf(preset.vidaInicialCastelo.toString()) }

    val moedasInt = moedas.toIntOrNull()
    val vidaInt = vida.toIntOrNull()

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar preset") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = moedas,
                    onValueChange = { moedas = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Moedas") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = vida,
                    onValueChange = { vida = it.filter { c -> c.isDigit() }.take(3) },
                    label = { Text("Vida") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = moedasInt != null && vidaInt != null,
                onClick = {
                    if (moedasInt != null && vidaInt != null) {
                        onConfirmar(
                            preset.copy(
                                nome = nome.trim(),
                                moedasIniciais = moedasInt,
                                vidaInicialCastelo = vidaInt
                            )
                        )
                    }
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}