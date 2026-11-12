package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.SituacaoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa
import com.example.youshallnotsavetheprince.ui.componentes.Hud
import com.example.youshallnotsavetheprince.ui.componentes.PainelDefesas
import com.example.youshallnotsavetheprince.ui.componentes.Tabuleiro
import com.example.youshallnotsavetheprince.viewmodel.JogoUiState

@Composable
fun JogoScreen(
    estado: JogoUiState,
    onComprar: (TipoDefesa, Posicao) -> Unit,
    onMelhorar: (Int) -> Unit,
    onProximoTurno: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partida = estado.partida
    var tipoSelecionado by remember { mutableStateOf(estado.defesaSelecionada) }
    var idDefesaSelecionada by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (partida == null) {
            Text(if (estado.carregando) "Carregando partida..." else "Nenhuma partida em andamento.")
            estado.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
        } else {
            val emAndamento = partida.situacao == SituacaoPartida.EM_ANDAMENTO
            val defesaSelecionada = partida.defesas.firstOrNull { it.id == idDefesaSelecionada }

            Hud(
                fase = partida.config.numero,
                turno = partida.turno,
                limiteTurnos = partida.config.limiteTurnos,
                moedas = partida.moedas,
                vidaCastelo = partida.vidaCastelo,
                derrotados = partida.derrotados,
                bonusMoedas = estado.bonusMoedasAplicado
            )

            estado.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Tabuleiro(
                config = partida.config,
                defesas = partida.defesas,
                inimigos = partida.inimigos,
                defesaSelecionadaId = idDefesaSelecionada,
                onCelulaClick = { posicao ->
                    val defesaNaCasa = partida.defesas.firstOrNull { it.posicao == posicao }
                    val tipo = tipoSelecionado
                    if (defesaNaCasa != null) {
                        idDefesaSelecionada = defesaNaCasa.id
                    } else if (posicao !in partida.config.caminho && tipo != null && emAndamento) {
                        onComprar(tipo, posicao)
                    }
                }
            )

            PainelDefesas(
                moedas = partida.moedas,
                tipoSelecionado = tipoSelecionado,
                onSelecionarTipo = { tipoSelecionado = it },
                defesaSelecionada = defesaSelecionada,
                onMelhorar = onMelhorar
            )

            if (emAndamento) {
                Button(onClick = onProximoTurno, modifier = Modifier.fillMaxWidth()) {
                    Text("Próximo turno ▶")
                }
            } else {
                Text("Partida encerrada.", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar") }
        }
    }
}