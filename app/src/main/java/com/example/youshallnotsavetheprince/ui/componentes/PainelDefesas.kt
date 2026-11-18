package com.example.youshallnotsavetheprince.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youshallnotsavetheprince.R
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.Defesa
import com.example.youshallnotsavetheprince.model.TipoDefesa
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.SuperficieAlta
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario

@Composable
fun PainelDefesas(
    moedas: Int,
    tipoSelecionado: TipoDefesa?,
    onSelecionarTipo: (TipoDefesa) -> Unit,
    defesaSelecionada: Defesa?,
    onMelhorar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CartaoJogo(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("ARSENAL", style = MaterialTheme.typography.labelMedium, color = Ouro)
            Text("Escolha seus defensores", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TipoDefesa.entries.forEach { tipo ->
                    val preco = CatalogoJogo.defesa(tipo).preco
                    val selecionado = tipo == tipoSelecionado
                    Surface(
                        modifier = Modifier.weight(1f).clickable { onSelecionarTipo(tipo) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selecionado) Ouro.copy(alpha = 0.18f) else SuperficieAlta,
                        border = BorderStroke(if (selecionado) 2.dp else 1.dp, if (selecionado) Ouro else Color(0xFF5F574D))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp, horizontal = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(tipo.icone()),
                                contentDescription = tipo.rotulo(),
                                modifier = Modifier.size(42.dp)
                            )
                            Text(tipo.rotulo(), fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Image(painterResource(R.drawable.icon_moedas), contentDescription = null, modifier = Modifier.size(15.dp))
                                Text("$preco", style = MaterialTheme.typography.labelMedium, color = Ouro)
                            }
                        }
                    }
                }
            }
            if (defesaSelecionada == null) {
                Text(
                    "Selecione uma defesa e toque em uma casa verde para comprá-la. Toque em uma defesa colocada para melhorá-la.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            } else {
                val atributos = CatalogoJogo.defesa(defesaSelecionada.tipo)
                val dano = atributos.danosPorNivel.getOrNull(defesaSelecionada.nivel - 1)
                val custo = atributos.custosMelhoria.getOrNull(defesaSelecionada.nivel - 1)
                Text("${defesaSelecionada.tipo.rotulo()} — nível ${defesaSelecionada.nivel}", style = MaterialTheme.typography.titleMedium)
                Text("Dano $dano  •  Alcance ${atributos.alcance}", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                if (custo != null) {
                    Button(onClick = { onMelhorar(defesaSelecionada.id) }, enabled = moedas >= custo, modifier = Modifier.fillMaxWidth()) {
                        Text("MELHORAR — $custo MOEDAS")
                    }
                } else {
                    Text("Nível máximo atingido.", style = MaterialTheme.typography.bodySmall, color = Ouro)
                }
            }
        }
    }
}
