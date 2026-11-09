package com.example.youshallnotsavetheprince.model

object CatalogoJogo {
    fun defesa(tipo: TipoDefesa): AtributosDefesa = when (tipo) {
        TipoDefesa.GOBLIN -> AtributosDefesa(30, 1, listOf(2, 3, 4), listOf(20, 25))
        TipoDefesa.ESQUELETO -> AtributosDefesa(50, 2, listOf(3, 4, 5), listOf(30, 35))
        TipoDefesa.OGRO -> AtributosDefesa(80, 1, listOf(5, 7, 9), listOf(45, 55))
    }

    fun inimigo(tipo: TipoInimigo): AtributosInimigo = when (tipo) {
        TipoInimigo.SOLDADO -> AtributosInimigo(4, 10, 1)
        TipoInimigo.ESCUDEIRO -> AtributosInimigo(6, 15, 1)
        TipoInimigo.CORREDOR -> AtributosInimigo(3, 12, 1)
        TipoInimigo.CAVALEIRO -> AtributosInimigo(10, 25, 2)
        TipoInimigo.PRINCESA -> AtributosInimigo(25, 100, 3)
    }
}
