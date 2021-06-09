package br.com.zupacademy.matheus.pix.registra

import br.com.zupacademy.matheus.KeymanagerRegistraGrpcServiceGrpc
import br.com.zupacademy.matheus.RegistraChavePixRequest
import br.com.zupacademy.matheus.RegistraChavePixResponse
import br.com.zupacademy.matheus.compartilhado.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RegistraChaveEndpoint(@Inject private val service: NovaChavePixService)
    : KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceImplBase(){

    override fun registra(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {

        val novaChave = request.toModel()
        val chaveCriada = service.registra(novaChave)

        responseObserver.onNext(RegistraChavePixResponse.newBuilder()
            .setClienteId(chaveCriada.clienteId.toString())
            .setPixId(chaveCriada.id.toString())
            .build())

        responseObserver.onCompleted()
    }
}