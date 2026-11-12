package com.example.youshallnotsavetheprince.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.Defesa
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.FaseConfig
import com.example.youshallnotsavetheprince.model.Inimigo
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import com.example.youshallnotsavetheprince.model.SituacaoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa
import com.example.youshallnotsavetheprince.model.TipoInimigo
import com.example.youshallnotsavetheprince.ui.screens.AdminLoginScreen
import com.example.youshallnotsavetheprince.ui.screens.AdminPresetsScreen
import com.example.youshallnotsavetheprince.ui.screens.FasesScreen
import com.example.youshallnotsavetheprince.ui.screens.HistoricoScreen
import com.example.youshallnotsavetheprince.ui.screens.InicioScreen
import com.example.youshallnotsavetheprince.ui.screens.JogoScreen
import com.example.youshallnotsavetheprince.ui.screens.ResultadoScreen
import com.example.youshallnotsavetheprince.ui.theme.YouShallNotSaveThePrinceTheme
import com.example.youshallnotsavetheprince.viewmodel.AdminUiState
import com.example.youshallnotsavetheprince.viewmodel.JogoUiState
import kotlinx.coroutines.launch

private fun tituloDaRota(rota: String?): String = when (rota) {
    Rotas.INICIO -> "Início"
    Rotas.FASES -> "Fases"
    Rotas.JOGO -> "Jogo"
    Rotas.RESULTADO -> "Resultado"
    Rotas.HISTORICO -> "Histórico"
    Rotas.ADMIN_LOGIN -> "Login do administrador"
    Rotas.ADMIN_PRESETS -> "Presets"
    else -> "You Shall Not Save the Prince"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    jogoState: JogoUiState,
    adminState: AdminUiState,
    onIniciarPartida: (Int, Long, String) -> Unit,
    onComprar: (TipoDefesa, Posicao) -> Unit,
    onMelhorar: (Int) -> Unit,
    onProximoTurno: () -> Unit,
    onRenomearResultado: (Long, String) -> Unit,
    onExcluirResultado: (Long) -> Unit,
    onEntrarAdmin: (String) -> Unit,
    onSairAdmin: () -> Unit,
    onCriarPreset: (String, Int, Int) -> Unit,
    onAtualizarPreset: (PresetPartida) -> Unit,
    onExcluirPreset: (Long) -> Unit,
    onFiltrarPresets: (String) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val escopo = rememberCoroutineScope()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    fun irPara(rota: String) {
        val destino = if (rota == Rotas.ADMIN_PRESETS && !adminState.adminAutenticado) {
            Rotas.ADMIN_LOGIN
        } else {
            rota
        }
        navController.navigate(destino) {
            popUpTo(Rotas.INICIO)
            launchSingleTop = true
        }
    }

    // Fim de partida: leva para a tela de resultado.
    LaunchedEffect(jogoState.resultadoAtual) {
        if (jogoState.resultadoAtual != null &&
            navController.currentDestination?.route == Rotas.JOGO
        ) {
            navController.navigate(Rotas.RESULTADO) {
                popUpTo(Rotas.JOGO) { inclusive = true }
            }
        }
    }

    // Login bem-sucedido: abre a tela de presets.
    LaunchedEffect(adminState.adminAutenticado) {
        if (adminState.adminAutenticado &&
            navController.currentDestination?.route == Rotas.ADMIN_LOGIN
        ) {
            navController.navigate(Rotas.ADMIN_PRESETS) {
                popUpTo(Rotas.ADMIN_LOGIN) { inclusive = true }
            }
        }
    }

    val itensDrawer = listOf(
        Rotas.INICIO to "🏰 Início",
        Rotas.FASES to "⚔️ Fases",
        Rotas.HISTORICO to "📜 Histórico",
        Rotas.ADMIN_PRESETS to "🔐 Administração"
    )

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "You Shall Not Save the Prince",
                    modifier = Modifier.padding(16.dp)
                )
                itensDrawer.forEach { (rota, rotulo) ->
                    val selecionado = rotaAtual == rota ||
                            (rota == Rotas.ADMIN_PRESETS && rotaAtual == Rotas.ADMIN_LOGIN)
                    NavigationDrawerItem(
                        label = { Text(rotulo) },
                        selected = selecionado,
                        onClick = {
                            escopo.launch { drawerState.close() }
                            irPara(rota)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(tituloDaRota(rotaAtual)) },
                    navigationIcon = {
                        IconButton(onClick = { escopo.launch { drawerState.open() } }) {
                            Text("☰", fontSize = 22.sp)
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Rotas.INICIO,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Rotas.INICIO) {
                    InicioScreen(
                        onJogar = { irPara(Rotas.FASES) },
                        onHistorico = { irPara(Rotas.HISTORICO) },
                        onAdmin = { irPara(Rotas.ADMIN_PRESETS) }
                    )
                }

                composable(Rotas.FASES) {
                    FasesScreen(
                        fasesLiberadas = jogoState.fasesLiberadas,
                        presets = jogoState.presets,
                        onEscolher = { fase, presetId, nome ->
                            onIniciarPartida(fase, presetId, nome)
                            navController.navigate(Rotas.JOGO) { launchSingleTop = true }
                        },
                        onVoltar = { navController.popBackStack() },
                        mensagem = jogoState.mensagem
                    )
                }

                composable(Rotas.JOGO) {
                    JogoScreen(
                        estado = jogoState,
                        onComprar = onComprar,
                        onMelhorar = onMelhorar,
                        onProximoTurno = onProximoTurno,
                        onVoltar = { navController.popBackStack() }
                    )
                }

                composable(Rotas.RESULTADO) {
                    ResultadoScreen(
                        resultado = jogoState.resultadoAtual,
                        onVoltar = { navController.popBackStack(Rotas.INICIO, inclusive = false) }
                    )
                }

                composable(Rotas.HISTORICO) {
                    HistoricoScreen(
                        resultados = jogoState.resultados,
                        onRenomear = onRenomearResultado,
                        onExcluir = onExcluirResultado,
                        onVoltar = { navController.popBackStack() },
                        mensagem = jogoState.mensagem
                    )
                }

                composable(Rotas.ADMIN_LOGIN) {
                    AdminLoginScreen(
                        estado = adminState,
                        onEntrar = onEntrarAdmin,
                        onVoltar = { navController.popBackStack() }
                    )
                }

                composable(Rotas.ADMIN_PRESETS) {
                    if (adminState.adminAutenticado) {
                        AdminPresetsScreen(
                            estado = adminState,
                            onCriar = onCriarPreset,
                            onAtualizar = onAtualizarPreset,
                            onExcluir = onExcluirPreset,
                            onFiltrar = onFiltrarPresets,
                            onSair = {
                                navController.navigate(Rotas.INICIO) {
                                    popUpTo(Rotas.INICIO)
                                    launchSingleTop = true
                                }
                                onSairAdmin()
                            }
                        )
                    } else {
                        // Rota protegida: sem autenticação, volta para o login.
                        LaunchedEffect(Unit) {
                            if (navController.currentDestination?.route == Rotas.ADMIN_PRESETS) {
                                navController.navigate(Rotas.ADMIN_LOGIN) {
                                    popUpTo(Rotas.ADMIN_PRESETS) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}