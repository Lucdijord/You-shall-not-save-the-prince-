package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.youshallnotsavetheprince.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import com.example.youshallnotsavetheprince.ui.componentes.CartaoJogo
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario

@Composable
fun ResultadoScreen(
    resultado: ResultadoPartida?,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    mensagem: String? = null
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (resultado?.vitoria == true) "BATALHA CONCLUÍDA" else "FIM DA BATALHA", style = MaterialTheme.typography.labelMedium, color = Ouro)
        Spacer(Modifier.height(10.dp))
        Image(
            painter = painterResource(if (resultado?.vitoria == true) R.drawable.icon_trofeu else R.drawable.icon_escudeiro),
            contentDescription = if (resultado?.vitoria == true) "Vitória" else "Derrota",
            modifier = Modifier.size(78.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(if (resultado?.vitoria == true) "VITÓRIA" else "DERROTA", style = MaterialTheme.typography.headlineLarge, color = Ouro)
        Spacer(Modifier.height(8.dp))
        Text(if (resultado?.vitoria == true) "O castelo resistiu ao ataque." else "O destino do castelo foi selado.", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        CartaoJogo(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (resultado == null) {
                    Text("Nenhum resultado disponível.")
                } else {
                    Text(resultado.nomeJogador, style = MaterialTheme.typography.titleLarge)
                    Text("FASE ${resultado.fase}", style = MaterialTheme.typography.labelMedium, color = Ouro)
                    Text("${resultado.pontos}", style = MaterialTheme.typography.headlineLarge, color = Ouro)
                    Text("PONTOS", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                    Text("Turnos utilizados: ${resultado.turnos}")
                    Text("Vida restante do castelo: ${resultado.vidaRestante}")
                }
            }
        }
        mensagem?.let { texto ->
            Spacer(Modifier.height(12.dp))
            Text(texto, color = if ((texto.startsWith("Bônus online:") || texto.startsWith("Bônus online indisponível:"))) TextoSecundario else MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onVoltar, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("VOLTAR AO INÍCIO") }
    }
}
