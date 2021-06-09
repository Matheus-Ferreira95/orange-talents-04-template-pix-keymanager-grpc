package br.com.zupacademy.matheus.chave

import br.com.zupacademy.matheus.KeymanagerRegistraGrpcServiceGrpc
import br.com.zupacademy.matheus.RegistraChavePixRequest
import br.com.zupacademy.matheus.TipoDeChave
import br.com.zupacademy.matheus.TipoDeConta
import br.com.zupacademy.matheus.integration.itau.ContasDeClientesNoItauClient
import br.com.zupacademy.matheus.integration.itau.DadosDaContaResponse
import br.com.zupacademy.matheus.integration.itau.InstituicaoResponse
import br.com.zupacademy.matheus.integration.itau.TitularResponse
import br.com.zupacademy.matheus.pix.ChavePix
import br.com.zupacademy.matheus.pix.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChavePixEndpointTest(val repository: ChavePixRepository,
                                            val grpcCliente: KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub){

    @Inject
    lateinit var itauClient: ContasDeClientesNoItauClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `consegue registrar uma nova chave pix`(){
        `when` (itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        val response = grpcCliente.registra(
            RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setChave("rponte@gmail.com")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())

        with(response){
            assertEquals(CLIENTE_ID.toString(), clienteId)
            assertNotNull(pixId)
        }
    }

    @Test
    internal fun `nao deve cadastrar chave quando ja existente`() {
        repository.save(ChavePix(
            clienteId = CLIENTE_ID,
            tipo = TipoDeChave.CPF,
            chave = "72670126049",
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = dadosDaContaResponse().toModel()
        ))

        val ex = assertThrows<StatusRuntimeException> {
            grpcCliente.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setChave("72670126049")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        with(ex) {
            assertEquals("Chave Pix 72670126049 existente", status.description)
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
        }

    }

    @Test
    fun `nao deve registrar chave pix quando nao encontrar dados da conta cliente`(){
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val ex = assertThrows<StatusRuntimeException> {
            grpcCliente.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClientId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.EMAIL)
                    .setChave("rponte@gmail.com")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(ex) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Cliente não encontrado no Itau", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix quando parametros forem invalidos`(){
        val thrown = assertThrows<StatusRuntimeException> {
            grpcCliente.registra(RegistraChavePixRequest.newBuilder().build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
        }
    }

    private fun dadosDaContaResponse(): DadosDaContaResponse {
        return DadosDaContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "qualquer coisa"),
            agencia = "1218",
            numero = "291900",
            titular = TitularResponse(CLIENTE_ID,"Rafael Ponte", "63657520325")
        )
    }


    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauClient(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @Factory
    class Clients  {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub? {
            return KeymanagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}