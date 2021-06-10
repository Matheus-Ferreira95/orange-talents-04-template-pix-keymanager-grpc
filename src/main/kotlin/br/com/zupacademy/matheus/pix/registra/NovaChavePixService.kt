package br.com.zupacademy.matheus.pix.registra

import br.com.zupacademy.matheus.integration.bcb.BancoCentralClient
import br.com.zupacademy.matheus.integration.bcb.CreatePixKeyRequest
import br.com.zupacademy.matheus.integration.itau.ContasDeClientesNoItauClient
import br.com.zupacademy.matheus.pix.ChavePix
import br.com.zupacademy.matheus.pix.ChavePixExistenteException
import br.com.zupacademy.matheus.pix.ChavePixRepository
import io.micronaut.http.HttpStatus
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
class NovaChavePixService(@Inject val repository: ChavePixRepository, // 1
                          @Inject val itauClient: ContasDeClientesNoItauClient, // 1
                          @Inject val bcbClient: BancoCentralClient,) { // 1

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix { // 2

        // 1. verifica se chave já existe no sistema
        if (repository.existsByChave(novaChave.chave)) // 1
            throw ChavePixExistenteException("Chave Pix '${novaChave.chave}' existente") // 1

        // 2. busca dados da conta no ERP do ITAU
        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!, novaChave.tipoDeConta!!.name) // 1
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no Itau") // 1

        // 3. grava no banco de dados
        val chave = novaChave.toModel(conta)
        repository.save(chave)

        // 4. registra chave no BCB
        val bcbRequest = CreatePixKeyRequest.of(chave).also { // 1
            LOGGER.info("Registrando chave Pix no Banco Central do Brasil (BCB): $it")
        }

        val bcbResponse = bcbClient.create(bcbRequest) // 1
        if (bcbResponse.status != HttpStatus.CREATED) // 1
            throw IllegalStateException("Erro ao registrar chave Pix no Banco Central do Brasil (BCB)")

        // 5. atualiza chave do dominio com chave gerada pelo BCB
        chave.atualiza(bcbResponse.body()!!.key)

        return chave
    }
}