package com.example.youshallnotsavetheprince.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    val textoBonus = if (bonusMoedas > 0) {
        "Bônus online: +$bonusMoedas moedas"
    } else {
        "Sem conexão: bônus padrão +0"
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fase $fase", style = MaterialTheme.typography.titleMedium)
                Text("Turno $turno/$limiteTurnos", style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("❤️ Castelo: $vidaCastelo")
                Text("🪙 Moedas: $moedas")
            }
            Text("Inimigos derrotados: $derrotados")
            Text(textoBonus, style = MaterialTheme.typography.bodySmall)
        }
    }
}