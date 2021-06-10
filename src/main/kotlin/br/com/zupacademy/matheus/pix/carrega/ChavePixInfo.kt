package br.com.zupacademy.matheus.pix.carrega

import br.com.zupacademy.matheus.TipoDeChave
import br.com.zupacademy.matheus.TipoDeConta
import br.com.zupacademy.matheus.pix.ChavePix
import br.com.zupacademy.matheus.pix.ContaAssociada
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipo: TipoDeChave,
    val chave: String,
    val tipoDeConta: TipoDeConta,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {

    companion object {
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                pixId = chave.id,
                clienteId = chave.clienteId,
                tipo = chave.tipo,
                chave = chave.chave,
                tipoDeConta = chave.tipoDeConta,
                conta = chave.conta,
                registradaEm = chave.criadaEm
            )
        }
    }
}