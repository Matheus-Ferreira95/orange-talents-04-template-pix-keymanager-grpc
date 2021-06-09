package br.com.zupacademy.matheus.pix.registra

import br.com.zupacademy.matheus.RegistraChavePixRequest
import br.com.zupacademy.matheus.TipoDeChave.UNKNOWN_TIPO_CHAVE
import br.com.zupacademy.matheus.TipoDeConta
import br.com.zupacademy.matheus.TipoDeConta.UNKNOWN_TIPO_CONTA
import br.com.zupacademy.matheus.pix.TipoChave


fun RegistraChavePixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        clienteId = clientId,
        tipo = when (tipoDeChave) {
            UNKNOWN_TIPO_CHAVE -> null
            else -> TipoChave.valueOf(tipoDeChave.name)
        },
        chave = chave,
        tipoDeConta = when (tipoDeConta) {
            UNKNOWN_TIPO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        }
    )
}