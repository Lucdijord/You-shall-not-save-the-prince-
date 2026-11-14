package com.example.youshallnotsavetheprince.logic

import com.example.youshallnotsavetheprince.contratos.RegrasDefesa
import com.example.youshallnotsavetheprince.contratos.RegrasInimigo
import com.example.youshallnotsavetheprince.model.CatalogoJogo
import com.example.youshallnotsavetheprince.model.EstadoPartida
import com.example.youshallnotsavetheprince.model.Posicao
import com.example.youshallnotsavetheprince.model.PresetPartida
import com.example.youshallnotsavetheprince.model.ResultadoPartida
import com.example.youshallnotsavetheprince.model.SituacaoPartida
import com.example.youshallnotsavetheprince.model.TipoDefesa

class MotorTurnos(
    private val regrasDefesa: RegrasDefesa,
    private val regrasInimigo: RegrasInimigo
) {


    fun iniciarFase(numeroFase: Int, preset: PresetPartida, bonusMoedas: Int): EstadoPartida {
        val config = regrasInimigo.configurarFase(numeroFase)
        val bonusValido = bonusMoedas.coerceIn(BONUS_MINIMO, BONUS_MAXIMO)
        return EstadoPartida(
            config = config,
            turno = 0,
            moedas = preset.moedasIniciais + bonusValido,
            vidaCastelo = preset.vidaInicialCastelo,
            situacao = SituacaoPartida.EM_ANDAMENTO
        )
    }

    fun comprar(estado: EstadoPartida, tipo: TipoDefesa, posicao: Posicao): EstadoPartida {
        if (estado.situacao != SituacaoPartida.EM_ANDAMENTO) return estado
        return regrasDefesa.comprar(estado, tipo, posicao)
    }

    fun melhorar(estado: EstadoPartida, idDefesa: Int): EstadoPartida {
        if (estado.situacao != SituacaoPartida.EM_ANDAMENTO) return estado
        return regrasDefesa.melhorar(estado, idDefesa)
    }


    fun proximoTurno(estado: EstadoPartida): EstadoPartida {
        if (estado.situacao != SituacaoPartida.EM_ANDAMENTO) return estado
        var atual = estado.copy(turno = estado.turno + 1)
        atual = regrasInimigo.gerar(atual)
        atual = regrasInimigo.movimentar(atual)
        atual = regrasDefesa.atacar(atual)
        atual = resolverEscapados(atual)
        return atual.copy(situacao = avaliarSituacao(atual))
    }


    fun calcularPontos(estado: EstadoPartida): Int =
        estado.derrotados * PONTOS_POR_DERROTADO + maxOf(estado.vidaCastelo, 0) * PONTOS_POR_VIDA


    fun criarResultado(
        estado: EstadoPartida,
        nomeJogador: String,
        registradoEmMillis: Long
    ): ResultadoPartida? {
        if (estado.situacao == SituacaoPartida.EM_ANDAMENTO) return null
        return ResultadoPartida(
            nomeJogador = nomeJogador,
            fase = estado.config.numero,
            vitoria = estado.situacao == SituacaoPartida.VITORIA,
            turnos = estado.turno,
            pontos = calcularPontos(estado),
            vidaRestante = maxOf(estado.vidaCastelo, 0),
            registradoEmMillis = registradoEmMillis
        )
    }

    private fun resolverEscapados(estado: EstadoPartida): EstadoPartida {
        val tamanhoCaminho = estado.config.caminho.size
        val (escapados, restantes) = estado.inimigos.partition { it.indiceCaminho >= tamanhoCaminho }
        if (escapados.isEmpty()) return estado
        val dano = escapados.sumOf { CatalogoJogo.inimigo(it.tipo).danoAoCastelo }
        return estado.copy(inimigos = restantes, vidaCastelo = estado.vidaCastelo - dano)
    }

    private fun avaliarSituacao(estado: EstadoPartida): SituacaoPartida {
        val todasEntradasPassaram = estado.config.entradas.all { it.turno <= estado.turno }
        return when {
            estado.vidaCastelo <= 0 -> SituacaoPartida.DERROTA
            todasEntradasPassaram && estado.inimigos.isEmpty() -> SituacaoPartida.VITORIA
            estado.turno >= estado.config.limiteTurnos -> SituacaoPartida.DERROTA
            else -> SituacaoPartida.EM_ANDAMENTO
        }
    }

    companion object {
        const val BONUS_MINIMO = 0
        const val BONUS_MAXIMO = 30
        private const val PONTOS_POR_DERROTADO = 10
        private const val PONTOS_POR_VIDA = 5
    }
}
