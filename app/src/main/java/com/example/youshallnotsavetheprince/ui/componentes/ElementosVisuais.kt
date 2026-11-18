package com.example.youshallnotsavetheprince.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.Superficie
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario
import com.example.youshallnotsavetheprince.ui.theme.Contorno

@Composable
fun CabecalhoJogo(sobretitulo: String, titulo: String, descricao: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(sobretitulo.uppercase(), style = MaterialTheme.typography.labelMedium, color = Ouro)
        Text(titulo, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        descricao?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario) }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Contorno)
    }
}

@Composable
fun CartaoJogo(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, Contorno.copy(alpha = 0.7f)),
        colors = CardDefaults.cardColors(containerColor = Superficie)
    ) { content() }
}
