package com.example.youshallnotsavetheprince.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.youshallnotsavetheprince.ui.componentes.CartaoJogo
import com.example.youshallnotsavetheprince.ui.theme.Ouro
import com.example.youshallnotsavetheprince.ui.theme.TextoSecundario
import com.example.youshallnotsavetheprince.viewmodel.AdminUiState

@Composable
fun AdminLoginScreen(
    estado: AdminUiState,
    onEntrar: (String) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ÁREA RESTRITA", color = Ouro, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        Text("Administração", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Gerencie os presets que definem o início de cada batalha.", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        Spacer(Modifier.height(24.dp))
        CartaoJogo(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter { c -> c.isDigit() }.take(8) },
                    label = { Text("PIN de acesso") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
                if (estado.carregando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                estado.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = { onEntrar(pin) }, enabled = pin.isNotBlank(), modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("ENTRAR") }
                OutlinedButton(onClick = onVoltar, modifier = Modifier.fillMaxWidth()) { Text("VOLTAR") }
            }
        }
    }
}
