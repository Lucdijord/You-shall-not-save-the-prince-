package com.example.youshallnotsavetheprince

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youshallnotsavetheprince.ui.navigation.AppNavigation
import com.example.youshallnotsavetheprince.ui.theme.YouShallNotSaveThePrinceTheme
import com.example.youshallnotsavetheprince.viewmodel.AdminViewModel
import com.example.youshallnotsavetheprince.viewmodel.JogoViewModel

class MainActivity : ComponentActivity() {
    private val container by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val jogo: JogoViewModel = viewModel(factory = container.jogoFactory)
            val admin: AdminViewModel = viewModel(factory = container.adminFactory)
            val jogoState = jogo.uiState.collectAsStateWithLifecycle()
            val adminState = admin.uiState.collectAsStateWithLifecycle()

            YouShallNotSaveThePrinceTheme {
                AppNavigation(
                    jogoState = jogoState.value,
                    adminState = adminState.value,
                    onIniciarPartida = jogo::iniciarPartida,
                    onComprar = jogo::comprar,
                    onMelhorar = jogo::melhorar,
                    onProximoTurno = jogo::proximoTurno,
                    onLimparMensagem = jogo::limparMensagem,
                    onSairPartida = jogo::sairDaPartida,
                    onRenomearResultado = jogo::renomearResultado,
                    onExcluirResultado = jogo::excluirResultado,
                    onEntrarAdmin = admin::autenticar,
                    onSairAdmin = admin::sair,
                    onCriarPreset = admin::criarPreset,
                    onAtualizarPreset = admin::atualizarPreset,
                    onExcluirPreset = admin::excluirPreset,
                    onFiltrarPresets = admin::filtrar
                )
            }
        }
    }
}