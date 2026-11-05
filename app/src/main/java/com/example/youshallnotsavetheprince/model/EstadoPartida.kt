package com.example.youshallnotsavetheprince.model

data class EstadoPartida(
    val config: FaseConfig,
    val turno: Int = 0,
    val moedas: Int = config.moedasIniciais,
    val vidaCastelo: Int = config.vidaInicialCastelo,
    val defesas: List<Defesa> = emptyList(),
    val inimigos: List<Inimigo> = emptyList(),
    val derrotados: Int = 0,
    val situacao: SituacaoPartida = SituacaoPartida.EM_ANDAMENTO
)
