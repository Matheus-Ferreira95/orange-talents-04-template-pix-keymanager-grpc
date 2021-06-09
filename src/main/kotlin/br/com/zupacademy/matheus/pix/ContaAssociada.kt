package br.com.zupacademy.matheus.pix

import javax.persistence.Embeddable

@Embeddable
class ContaAssociada(
    val instituicao: String?,
    val nomeDoTitular: String?,
    val cpfDoTitular: String?,
    val agencia: String?,
    val numeroDaConta: String?
)