package com.example.youshallnotsavetheprince.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.R
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario

@Composable
fun Hud(
    fase: Int,
    turno: Int,
    limiteTurnos: Int,
    moedas: Int,
    vidaCastelo: Int,
    derrotados: Int,
    bonusMoedas: Int,
    modifier: Modifier = Modifier
) {
    CartaoJogo(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("FASE $fase", color = Ouro, style = MaterialTheme.typography.titleMedium)
                Text("TURNO $turno / $limiteTurnos", color = TextoSecundario, style = MaterialTheme.typography.labelMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Indicador(R.drawable.icon_vida, "CASTELO", vidaCastelo.toString(), Modifier.weight(1f))
                Indicador(R.drawable.icon_moedas, "MOEDAS", moedas.toString(), Modifier.weight(1f))
                Indicador(R.drawable.icon_derrotados, "DERROTADOS", derrotados.toString(), Modifier.weight(1f))
            }
            Text(
                "Bônus inicial: +$bonusMoedas moedas",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun Indicador(icone: Int, rotulo: String, valor: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(icone), contentDescription = null, modifier = Modifier.size(30.dp))
        Text(rotulo, style = MaterialTheme.typography.labelMedium, color = TextoSecundario, textAlign = TextAlign.Center)
        Text(valor, style = MaterialTheme.typography.headlineSmall, color = if (rotulo == "MOEDAS") Ouro else MaterialTheme.colorScheme.onSurface)
    }
}
