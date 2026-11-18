package com.example.youshallnotsavetheprince.data.repository

import com.example.youshallnotsavetheprince.data.local.PresetPartidaEntity
import com.example.youshallnotsavetheprince.data.local.ResultadoPartidaEntity
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida

internal fun PresetPartidaEntity.paraModelo() = PresetPartida(
    id = id,
    nome = nome,
    moedasIniciais = moedasIniciais,
    vidaInicialCastelo = vidaInicialCastelo
)

internal fun PresetPartida.paraEntidade() = PresetPartidaEntity(
    id = id,
    nome = nome.trim(),
    moedasIniciais = moedasIniciais,
    vidaInicialCastelo = vidaInicialCastelo
)

internal fun ResultadoPartidaEntity.paraModelo() = ResultadoPartida(
    id = id,
    nomeJogador = nomeJogador,
    fase = fase,
    vitoria = vitoria,
    turnos = turnos,
    pontos = pontos,
    vidaRestante = vidaRestante,
    registradoEmMillis = registradoEmMillis
)

internal fun ResultadoPartida.paraEntidade() = ResultadoPartidaEntity(
    id = id,
    nomeJogador = nomeJogador.trim(),
    fase = fase,
    vitoria = vitoria,
    turnos = turnos,
    pontos = pontos,
    vidaRestante = vidaRestante,
    registradoEmMillis = registradoEmMillis
)
