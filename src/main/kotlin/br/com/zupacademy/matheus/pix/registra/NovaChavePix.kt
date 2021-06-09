package br.com.zupacademy.matheus.pix.registra

import br.com.zupacademy.matheus.TipoDeChave
import br.com.zupacademy.matheus.TipoDeConta
import br.com.zupacademy.matheus.compartilhado.validacao.ValidPixKey
import br.com.zupacademy.matheus.compartilhado.validacao.ValidUUID
import br.com.zupacademy.matheus.pix.ChavePix
import br.com.zupacademy.matheus.pix.ContaAssociada
import br.com.zupacademy.matheus.pix.TipoChave
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(

    @ValidUUID
    @field:NotBlank
    val clienteId: String?,

    @field:NotNull
    val tipo: TipoChave?,

    @field:Size(max = 77)
    val chave: String?,

    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {

    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipo = TipoDeChave.valueOf(this.tipo!!.name),
            chave = if (this.tipo == TipoChave.ALEATORIA) UUID.randomUUID().toString() else this.chave!!,
            tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
            conta = conta
        )
    }
}