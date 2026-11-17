package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.R
import com.example.youshallnotsavetheprince.ui.componentes.CartaoJogo
import com.example.youshallnotsavetheprince.ui.theme.FundoEscuro
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario

@Composable
fun InicioScreen(
    onJogar: () -> Unit,
    onHistorico: () -> Unit,
    onAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF172838), FundoEscuro, FundoEscuro)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("A ÚLTIMA DEFESA", color = Ouro, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(R.drawable.logo_ys),
            contentDescription = "Brasão do castelo - You Shall Not Save the Prince",
            modifier = Modifier.size(144.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "YOU SHALL NOT\nSAVE THE PRINCE",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "O príncipe foi sequestrado. Agora seu dever é defender o castelo das tropas da princesa.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = TextoSecundario
        )
        Spacer(Modifier.height(28.dp))
        CartaoJogo(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("SUA JORNADA", color = Ouro, style = MaterialTheme.typography.labelMedium)
                Text("5 fases  •  3 defesas  •  1 castelo", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onJogar, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                    Image(painterResource(R.drawable.icon_castelo), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.size(10.dp))
                    Text("INICIAR JORNADA", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onHistorico, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    Image(painterResource(R.drawable.icon_historico), contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.size(10.dp))
                    Text("HISTÓRICO DE BATALHAS")
                }
                OutlinedButton(onClick = onAdmin, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    Image(painterResource(R.drawable.icon_cadeado), contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.size(10.dp))
                    Text("ADMINISTRAÇÃO")
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("DEFENDA O IMPOSSÍVEL", style = MaterialTheme.typography.labelMedium, color = Ouro)
    }
}
