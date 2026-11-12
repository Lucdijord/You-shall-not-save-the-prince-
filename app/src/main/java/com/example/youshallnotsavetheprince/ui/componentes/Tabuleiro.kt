package com.example.youshallnotsavetheprince.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youshallnotsavetheprince.model.Defesa
import com.example.youshallnotsavetheprince.model.FaseConfig
import com.example.youshallnotsavetheprince.model.Inimigo
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.TipoDefesa
import com.example.youshallnotsavetheprince.model.TipoInimigo

fun TipoDefesa.emoji(): String = when (this) {
    TipoDefesa.GOBLIN -> "👺"
    TipoDefesa.ESQUELETO -> "💀"
    TipoDefesa.OGRO -> "👹"
}

fun TipoDefesa.rotulo(): String = when (this) {
    TipoDefesa.GOBLIN -> "Goblin"
    TipoDefesa.ESQUELETO -> "Esqueleto"
    TipoDefesa.OGRO -> "Ogro"
}

fun TipoInimigo.emoji(): String = when (this) {
    TipoInimigo.SOLDADO -> "🗡️"
    TipoInimigo.ESCUDEIRO -> "🛡️"
    TipoInimigo.CORREDOR -> "🏃"
    TipoInimigo.CAVALEIRO -> "🐴"
    TipoInimigo.PRINCESA -> "👸"
}

@Composable
fun Tabuleiro(
    config: FaseConfig,
    defesas: List<Defesa>,
    inimigos: List<Inimigo>,
    defesaSelecionadaId: Int?,
    onCelulaClick: (Posicao) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().border(2.dp, Color(0xFF3E2723))) {
        for (linha in 0 until config.linhas) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (coluna in 0 until config.colunas) {
                    val posicao = Posicao(linha, coluna)
                    val defesa = defesas.firstOrNull { it.posicao == posicao }
                    val inimigosNaCasa = inimigos.filter {
                        config.caminho.getOrNull(it.indiceCaminho) == posicao
                    }
                    CelulaTabuleiro(
                        noCaminho = posicao in config.caminho,
                        castelo = posicao == config.caminho.lastOrNull(),
                        defesa = defesa,
                        inimigos = inimigosNaCasa,
                        selecionada = defesa != null && defesa.id == defesaSelecionadaId,
                        onClick = { onCelulaClick(posicao) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CelulaTabuleiro(
    noCaminho: Boolean,
    castelo: Boolean,
    defesa: Defesa?,
    inimigos: List<Inimigo>,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fundo = if (noCaminho) Color(0xFFD7B98E) else Color(0xFF81C784)
    val corBorda = if (selecionada) MaterialTheme.colorScheme.primary else Color(0x33000000)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(fundo)
            .border(if (selecionada) 3.dp else 0.5.dp, corBorda)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (inimigos.isNotEmpty()) {
            val primeiro = inimigos.first()
            val extra = if (inimigos.size > 1) " +${inimigos.size - 1}" else ""
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(primeiro.tipo.emoji(), fontSize = 20.sp)
                Text("♥${primeiro.vida}$extra", fontSize = 10.sp, color = Color.Black)
            }
        } else if (defesa != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(defesa.tipo.emoji(), fontSize = 20.sp)
                Text("Nv ${defesa.nivel}", fontSize = 10.sp, color = Color.Black)
            }
        } else if (castelo) {
            Text("🏰", fontSize = 22.sp)
        }
    }
}