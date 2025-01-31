package br.com.zupacademy.matheus.integration.itau

import br.com.zupacademy.matheus.pix.ContaAssociada
import java.util.*

data class DadosDaContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse) {


    fun toModel(): ContaAssociada {
        return ContaAssociada(
            instituicao = this.instituicao.nome,
            nomeDoTitular = this.titular.nome,
            cpfDoTitular = this.titular.cpf,
            agencia = this.agencia,
            numeroDaConta = this.numero
        )
    }

}
data class TitularResponse(val id: UUID, val nome: String, val cpf: String)

data class InstituicaoResponse(val nome: String, val ispb: String)
