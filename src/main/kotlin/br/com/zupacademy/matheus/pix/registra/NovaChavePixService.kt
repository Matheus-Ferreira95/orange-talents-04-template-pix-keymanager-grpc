package br.com.zupacademy.matheus.pix.registra

import br.com.zupacademy.matheus.integration.itau.ContasDeClientesNoItauClient
import br.com.zupacademy.matheus.pix.ChavePix
import br.com.zupacademy.matheus.pix.ChavePixExistenteException
import br.com.zupacademy.matheus.pix.ChavePixRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ContasDeClientesNoItauClient
)
{

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix {

        if (repository.existsByChave(novaChave.chave)) {
            throw ChavePixExistenteException("Chave Pix ${novaChave.chave} existente")
        }

        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!, novaChave.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalArgumentException("Cliente não encontrado no itau")

        val chave = novaChave.toModel(conta)
        repository.save(chave)

        return chave
    }
}