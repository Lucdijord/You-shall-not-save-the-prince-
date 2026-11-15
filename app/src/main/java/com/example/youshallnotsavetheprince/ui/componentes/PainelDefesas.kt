package com.example.youshallnotsavetheprince.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.Defesa
import com.example.youshallnotsavetheprince.model.TipoDefesa

@Composable
fun PainelDefesas(
    moedas: Int,
    tipoSelecionado: TipoDefesa?,
    onSelecionarTipo: (TipoDefesa) -> Unit,
    defesaSelecionada: Defesa?,
    onMelhorar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Defesas", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoDefesa.entries.forEach { tipo ->
                    val preco = CatalogoJogo.defesa(tipo).preco
                    val rotulo = "${tipo.emoji()} ${tipo.rotulo()}\n$preco 🪙"
                    if (tipo == tipoSelecionado) {
                        Button(
                            onClick = { onSelecionarTipo(tipo) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(rotulo, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onSelecionarTipo(tipo) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(rotulo, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            if (defesaSelecionada == null) {
                Text(
                    "Escolha uma defesa e toque numa casa verde para comprar. " +
                            "Toque numa defesa do tabuleiro para melhorá-la.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                val atributos = CatalogoJogo.defesa(defesaSelecionada.tipo)
                val dano = atributos.danosPorNivel.getOrNull(defesaSelecionada.nivel - 1)
                val custo = atributos.custosMelhoria.getOrNull(defesaSelecionada.nivel - 1)

                Text(
                    "${defesaSelecionada.tipo.rotulo()} nível ${defesaSelecionada.nivel} " +
                            "• dano $dano • alcance ${atributos.alcance}"
                )
                if (custo != null) {
                    Button(
                        onClick = { onMelhorar(defesaSelecionada.id) },
                        enabled = moedas >= custo,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Melhorar por $custo moedas")
                    }
                } else {
                    Text("Nível máximo atingido.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}