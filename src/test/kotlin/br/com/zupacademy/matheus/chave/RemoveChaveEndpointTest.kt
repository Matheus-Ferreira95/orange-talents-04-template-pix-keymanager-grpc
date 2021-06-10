package br.com.zupacademy.matheus.chave

import br.com.zupacademy.matheus.*
import br.com.zupacademy.matheus.pix.ChavePix
import br.com.zupacademy.matheus.pix.ChavePixRepository
import br.com.zupacademy.matheus.pix.ContaAssociada
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub,
) {

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(newChave(
            tipo = TipoDeChave.EMAIL,
            chave = "rponte@gmail.com",
            clienteId = UUID.randomUUID()
        ))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover chave pix existente`() {
        val response = grpcClient.remove(RemoveChavePixRequest.newBuilder()
            .setPixId(CHAVE_EXISTENTE.id.toString())
            .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
            .build())

        with(response) {
            assertEquals(CHAVE_EXISTENTE.id.toString(), pixId)
            assertEquals(CHAVE_EXISTENTE.clienteId.toString(), clienteId)
        }
    }

    @Test
    internal fun `nao deve remover chave pix quando nao existente`() {
        val pixIdNaoExistente = UUID.randomUUID().toString()

        val ex = assertThrows<StatusRuntimeException> {
            grpcClient.remove(RemoveChavePixRequest.newBuilder()
                .setPixId(pixIdNaoExistente)
                .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                .build())
        }

        with(ex) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao usuário", status.description)
        }

    }

    @Test
    internal fun `nao deve remover chave pix quando chave existir mas pertencer a outro cliente`() {
        val clienteIdNaoExistente = UUID.randomUUID().toString()

        val ex = assertThrows<StatusRuntimeException> {
            grpcClient.remove(RemoveChavePixRequest.newBuilder()
                .setPixId(CHAVE_EXISTENTE.id.toString())
                .setClienteId(clienteIdNaoExistente)
                .build())
        }

        with(ex) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao usuário", status.description)
        }

    }

    @Factory
    class Clients  {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub? {
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun newChave(
        tipo: TipoDeChave,
        chave: String = UUID.randomUUID().toString(),
        clienteId: UUID = UUID.randomUUID()): ChavePix {
        return ChavePix(
            clienteId = clienteId,
            tipo = tipo,
            chave = chave,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "UNIBANCO ITAU",
                nomeDoTitular = "Rafael Ponte",
                cpfDoTitular = "12345678900",
                agencia = "1218",
                numeroDaConta = "123456"
            )
        )
    }

}